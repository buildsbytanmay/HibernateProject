package org.example.dao;

import jakarta.persistence.EntityManager;
import org.example.entity.Product;
import org.example.util.JPAUtil;

import java.util.List;

public class ProductDAO {

    public void save(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(product);
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

    public void update(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(product);
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

    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Product product = em.find(Product.class, id);
            if (product != null) {
                em.remove(product);
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

    public Product findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public List<Product> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p ORDER BY p.id", Product.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Product> searchByNameOrCategory(String keyword) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:kw) OR LOWER(p.category) LIKE LOWER(:kw) ORDER BY p.id", Product.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Product> findLowStock(int threshold) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE p.stockQuantity <= :th ORDER BY p.stockQuantity ASC", Product.class)
                    .setParameter("th", threshold)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countTotal() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }

    public boolean isProductInOrders(Long productId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :pid", Long.class)
                    .setParameter("pid", productId)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    public boolean isProductInCarts(Long productId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(ci) FROM CartItem ci WHERE ci.product.id = :pid", Long.class)
                    .setParameter("pid", productId)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }
}
