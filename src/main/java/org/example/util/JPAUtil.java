package org.example.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static EntityManagerFactory emf;

    public static synchronized EntityManager getEntityManager() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("shoppingCartPU");
        }
        return emf.createEntityManager();
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("shoppingCartPU");
        }
        return emf;
    }

    public static synchronized void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }
}
