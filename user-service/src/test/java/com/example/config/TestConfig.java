package com.example.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TestConfig {

    public static SessionFactory createTestSessionFactory(String url, String username, String password) {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.test.cfg.xml");

            configuration.setProperty("hibernate.connection.url", url);
            configuration.setProperty("hibernate.connection.username", username);
            configuration.setProperty("hibernate.connection.password", password);
            configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}
