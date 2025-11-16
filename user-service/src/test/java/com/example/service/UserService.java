package com.example.service;

import com.example.dao.UserDao;
import com.example.model.User;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Long createUser(String name, String email, Integer age) {
        validateUserData(name, email, age);
        User user = new User(name, email, age);
        return userDao.save(user);
    }

    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public void updateUser(Long id, String name, String email, Integer age) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        User user = userDao.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (age != null) {
            user.setAge(age);
        }

        userDao.update(user);
    }

    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        userDao.delete(id);
    }

    private void validateUserData(String name, String email, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (age != null && (age < 0 || age > 150)) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }
}
