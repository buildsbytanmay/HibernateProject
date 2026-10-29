package org.example.service;

import org.example.dao.ProductDAO;
import org.example.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public Product addProduct(String name, String category, BigDecimal price, int stockQuantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        Product product = new Product(name.trim(), category != null ? category.trim() : "", price, stockQuantity);
        productDAO.save(product);
        return product;
    }

    public Product updateProduct(Long id, String name, String category, BigDecimal price, int stockQuantity) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        product.setName(name.trim());
        product.setCategory(category != null ? category.trim() : "");
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        productDAO.update(product);
        return product;
    }

    public void deleteProduct(Long id) {
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }
        if (productDAO.isProductInOrders(id)) {
            throw new IllegalStateException("Cannot delete product: it is referenced in existing orders.");
        }
        if (productDAO.isProductInCarts(id)) {
            throw new IllegalStateException("Cannot delete product: it is currently in customer shopping carts.");
        }
        productDAO.delete(id);
    }

    public Product getProductById(Long id) {
        return productDAO.findById(id);
    }

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productDAO.findAll();
        }
        return productDAO.searchByNameOrCategory(keyword.trim());
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productDAO.findLowStock(threshold);
    }

    public void updateStock(Long id, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        Product product = productDAO.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }
        product.setStockQuantity(newStock);
        productDAO.update(product);
    }

    public long getTotalProductsCount() {
        return productDAO.countTotal();
    }
}
