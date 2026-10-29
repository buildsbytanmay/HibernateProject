package org.example.service;

import org.example.dao.CartDAO;
import org.example.dao.CustomerDAO;
import org.example.dao.ProductDAO;
import org.example.entity.Cart;
import org.example.entity.CartItem;
import org.example.entity.Customer;
import org.example.entity.Product;

public class CartService {

    private final CartDAO cartDAO;
    private final CustomerDAO customerDAO;
    private final ProductDAO productDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
    }

    public Cart getCart(Long customerId) {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        return cartDAO.findByCustomerId(customerId);
    }

    public void addToCart(Long customerId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }

        Cart cart = cartDAO.findByCustomerId(customerId);
        CartItem existingItem = cartDAO.findCartItem(cart.getId(), productId);

        int totalRequested = quantity;
        if (existingItem != null) {
            totalRequested += existingItem.getQuantity();
        }

        if (totalRequested > product.getStockQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + totalRequested + ") exceeds available stock (" + product.getStockQuantity() + ").");
        }

        if (existingItem != null) {
            existingItem.setQuantity(totalRequested);
            cartDAO.updateItem(existingItem);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            cartDAO.saveItem(newItem);
        }
    }

    public void updateItemQuantity(Long customerId, Long productId, int newQuantity) {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }

        Cart cart = cartDAO.findByCustomerId(customerId);
        CartItem existingItem = cartDAO.findCartItem(cart.getId(), productId);
        if (existingItem == null) {
            throw new IllegalArgumentException("Product not found in customer cart.");
        }

        if (newQuantity <= 0) {
            cartDAO.removeItem(existingItem.getId());
        } else {
            if (newQuantity > product.getStockQuantity()) {
                throw new IllegalArgumentException("Requested quantity (" + newQuantity + ") exceeds available stock (" + product.getStockQuantity() + ").");
            }
            existingItem.setQuantity(newQuantity);
            cartDAO.updateItem(existingItem);
        }
    }

    public void removeFromCart(Long customerId, Long productId) {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        Cart cart = cartDAO.findByCustomerId(customerId);
        CartItem existingItem = cartDAO.findCartItem(cart.getId(), productId);
        if (existingItem == null) {
            throw new IllegalArgumentException("Product not found in customer cart.");
        }
        cartDAO.removeItem(existingItem.getId());
    }

    public void clearCart(Long customerId) {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        Cart cart = cartDAO.findByCustomerId(customerId);
        cartDAO.clearCart(cart.getId());
    }
}
