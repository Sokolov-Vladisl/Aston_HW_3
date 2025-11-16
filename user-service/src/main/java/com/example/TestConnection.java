package com.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/userdb",
                    "postgres",
                    "123"
            );
            System.out.println("✅ Подключение успешно!");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Ошибка подключения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}