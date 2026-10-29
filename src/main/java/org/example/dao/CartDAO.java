package org.example.dao;

import jakarta.persistence.EntityManager;
import org.example.entity.Cart;
import org.example.entity.CartItem;
import org.example.entity.Customer;
import org.example.util.JPAUtil;

import java.util.List;

public class CartDAO {

    public Cart findByCustomerId(Long customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Cart> carts = em.createQuery(
                    "SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.product WHERE c.customer.id = :cid",
                    Cart.class)
                    .setParameter("cid", customerId)
                    .getResultList();
            if (!carts.isEmpty()) {
                return carts.get(0);
            }
            Customer customer = em.find(Customer.class, customerId);
            if (customer != null) {
                em.getTransaction().begin();
                Cart cart = new Cart(customer);
                em.persist(cart);
                customer.setCart(cart);
                em.getTransaction().commit();
                return cart;
            }
            return null;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public CartItem findCartItem(Long cartId, Long productId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<CartItem> items = em.createQuery(
                    "SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId",
                    CartItem.class)
                    .setParameter("cartId", cartId)
                    .setParameter("productId", productId)
                    .getResultList();
            return items.isEmpty() ? null : items.get(0);
        } finally {
            em.close();
        }
    }

    public void saveItem(CartItem item) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(item);
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

    public void updateItem(CartItem item) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(item);
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

    public void removeItem(Long cartItemId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CartItem item = em.find(CartItem.class, cartItemId);
            if (item != null) {
                em.remove(item);
            }
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

    public void clearCart(Long cartId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
                    .setParameter("cartId", cartId)
                    .executeUpdate();
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
}
