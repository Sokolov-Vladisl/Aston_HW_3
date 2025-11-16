package com.example;

import com.example.dao.UserDao;
import com.example.model.User;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final UserDao userDao = new UserDao();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            System.out.println("=== User Service Application ===");
            runApplication();
        } catch (Exception e) {
            logger.severe("Application error: " + e.getMessage());
            System.out.println("Critical error occurred. Check logs for details.");
        } finally {
            scanner.close();
        }
    }

    private static void runApplication() {
        while (true) {
            printMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> getUser();
                    case 3 -> getAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 6 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); // clear invalid input
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== User Service Menu ===");
        System.out.println("1. Create User");
        System.out.println("2. Get User by ID");
        System.out.println("3. Get All Users");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    private static void createUser() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter age: ");
            int age = scanner.nextInt();
            scanner.nextLine(); // consume newline

            User user = new User(name, email, age);
            Long id = userDao.save(user);
            System.out.println("User created successfully with ID: " + id);
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    private static void getUser() {
        try {
            System.out.print("Enter user ID: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            userDao.findById(id).ifPresentOrElse(
                    user -> System.out.println("User found: " + user),
                    () -> System.out.println("User not found with ID: " + id)
            );
        } catch (Exception e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
    }

    private static void getAllUsers() {
        try {
            List<User> users = userDao.findAll();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                System.out.println("Users:");
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
    }

    private static void updateUser() {
        try {
            System.out.print("Enter user ID to update: ");
            Long id = scanner.nextLong();
            scanner.nextLine(); // consume newline

            userDao.findById(id).ifPresentOrElse(user -> {
                System.out.print("Enter new name (current: " + user.getName() + "): ");
                String name = scanner.nextLine();
                if (!name.trim().isEmpty()) user.setName(name);

                System.out.print("Enter new email (current: " + user.getEmail() + "): ");
                String email = scanner.nextLine();
                if (!email.trim().isEmpty()) user.setEmail(email);

                System.out.print("Enter new age (current: " + user.getAge() + "): ");
                String ageInput = scanner.nextLine();
                if (!ageInput.trim().isEmpty()) {
                    user.setAge(Integer.parseInt(ageInput));
                }

                userDao.update(user);
                System.out.println("User updated successfully");
            }, () -> System.out.println("User not found with ID: " + id));
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        try {
            System.out.print("Enter user ID to delete: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            userDao.delete(id);
            System.out.println("User deleted successfully");
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }
}