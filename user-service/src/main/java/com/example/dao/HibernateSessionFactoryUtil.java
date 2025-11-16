package com.example.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateSessionFactoryUtil {
    private static final Logger logger = Logger.getLogger(HibernateSessionFactoryUtil.class.getName());
    private static SessionFactory sessionFactory;

    static {
        initializeSessionFactory();
    }

    private static void initializeSessionFactory() {
        try {
            logger.info("Initializing Hibernate SessionFactory...");

            Configuration configuration = new Configuration();
            configuration.configure();

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            logger.info("Hibernate SessionFactory initialized successfully");

        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "Failed to initialize Hibernate SessionFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            initializeSessionFactory();
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed");
        }
    }
}