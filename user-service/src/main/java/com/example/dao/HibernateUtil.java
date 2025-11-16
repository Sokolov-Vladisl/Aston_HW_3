package com.example.dao;

import com.example.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {
    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());
    private static SessionFactory sessionFactory;

    static {
        initializeSessionFactory();
    }

    private static void initializeSessionFactory() {
        try {
            logger.info("Initializing Hibernate SessionFactory...");

            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .build();

            Metadata metadata = new MetadataSources(standardRegistry)
                    .addAnnotatedClass(User.class) // Явно добавляем Entity класс
                    .getMetadataBuilder()
                    .build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();
            logger.info("Hibernate SessionFactory initialized successfully");

        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "Failed to initialize Hibernate SessionFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed");
        }
    }
}