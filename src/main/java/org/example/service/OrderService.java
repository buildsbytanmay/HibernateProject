package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.dao.OrderDAO;
import org.example.entity.Cart;
import org.example.entity.CartItem;
import org.example.entity.Customer;
import org.example.entity.Order;
import org.example.entity.OrderItem;
import org.example.entity.Product;
import org.example.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
    }

    public Order checkout(Long customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Customer customer = em.find(Customer.class, customerId);
            if (customer == null) {
                throw new IllegalArgumentException("Customer not found with ID: " + customerId);
            }

            List<Cart> carts = em.createQuery(
                    "SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.product WHERE c.customer.id = :cid",
                    Cart.class)
                    .setParameter("cid", customerId)
                    .getResultList();

            if (carts.isEmpty() || carts.get(0).getItems().isEmpty()) {
                throw new IllegalStateException("Cart is empty. Cannot checkout.");
            }

            Cart cart = carts.get(0);
            List<CartItem> cartItems = cart.getItems();

            for (CartItem ci : cartItems) {
                Product p = em.find(Product.class, ci.getProduct().getId());
                if (p == null) {
                    throw new IllegalStateException("Product not found: " + ci.getProduct().getName());
                }
                if (p.getStockQuantity() < ci.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for product '" + p.getName()
                            + "'. Available: " + p.getStockQuantity() + ", requested: " + ci.getQuantity());
                }
            }

            BigDecimal total = BigDecimal.ZERO;
            Order order = new Order();
            order.setCustomer(customer);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PLACED");

            for (CartItem ci : cartItems) {
                Product p = em.find(Product.class, ci.getProduct().getId());
                p.setStockQuantity(p.getStockQuantity() - ci.getQuantity());
                em.merge(p);

                BigDecimal unitPrice = p.getPrice();
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity()));
                total = total.add(subtotal);

                OrderItem orderItem = new OrderItem(order, p, ci.getQuantity(), unitPrice, subtotal);
                order.addItem(orderItem);
            }

            order.setTotalAmount(total);
            em.persist(order);

            em.createQuery("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
                    .setParameter("cartId", cart.getId())
                    .executeUpdate();

            em.getTransaction().commit();
            return order;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void cancelOrder(Long orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order == null) {
                throw new IllegalArgumentException("Order not found with ID: " + orderId);
            }

            if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
                throw new IllegalStateException("Order #" + orderId + " has already been cancelled.");
            }

            if (!"PLACED".equalsIgnoreCase(order.getStatus())) {
                throw new IllegalStateException("Order #" + orderId + " is not eligible for cancellation. Current status: " + order.getStatus());
            }

            List<OrderItem> items = em.createQuery(
                    "SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id = :oid",
                    OrderItem.class)
                    .setParameter("oid", orderId)
                    .getResultList();

            for (OrderItem item : items) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                em.merge(product);
            }

            order.setStatus("CANCELLED");
            em.merge(order);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Order getOrderById(Long orderId) {
        return orderDAO.findById(orderId);
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderDAO.findByCustomerId(customerId);
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    public long getTotalOrdersCount() {
        return orderDAO.countTotal();
    }

    public BigDecimal getTotalSales() {
        return orderDAO.sumTotalSales();
    }
}
