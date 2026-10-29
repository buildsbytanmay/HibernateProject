package org.example.dao;

import jakarta.persistence.EntityManager;
import org.example.entity.Order;
import org.example.util.JPAUtil;

import java.math.BigDecimal;
import java.util.List;

public class OrderDAO {

    public void save(Order order) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(order);
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

    public void update(Order order) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
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

    public Order findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Order> orders = em.createQuery(
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product LEFT JOIN FETCH o.customer WHERE o.id = :id",
                    Order.class)
                    .setParameter("id", id)
                    .getResultList();
            return orders.isEmpty() ? null : orders.get(0);
        } finally {
            em.close();
        }
    }

    public List<Order> findByCustomerId(Long customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.customer.id = :cid ORDER BY o.orderDate DESC",
                    Order.class)
                    .setParameter("cid", customerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Order> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.customer ORDER BY o.orderDate DESC",
                    Order.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countTotal() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(o) FROM Order o", Long.class).getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }

    public BigDecimal sumTotalSales() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            BigDecimal sum = em.createQuery(
                    "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'PLACED'",
                    BigDecimal.class)
                    .getSingleResult();
            return sum != null ? sum : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
}
