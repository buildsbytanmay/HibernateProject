package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.entity.Cart;
import org.example.entity.Customer;
import org.example.util.JPAUtil;

import java.util.List;

public class CustomerDAO {

    public void save(Customer customer) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(customer);
            Cart cart = new Cart(customer);
            customer.setCart(cart);
            em.persist(cart);
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

    public void update(Customer customer) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(customer);
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

    public Customer findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }

    public List<Customer> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Customer c ORDER BY c.id", Customer.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Customer> searchByNameOrEmail(String keyword) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(:kw) OR LOWER(c.email) LIKE LOWER(:kw) ORDER BY c.id", Customer.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Customer findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public long countTotal() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(c) FROM Customer c", Long.class).getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
}
