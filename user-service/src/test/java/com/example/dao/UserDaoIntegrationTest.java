package com.example.dao;

import com.example.model.User;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private UserDao userDao;

    @BeforeAll
    void setup() {
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());
    }

    @BeforeEach
    void init() {
        userDao = new UserDao();
        clearDatabase();
    }

    @AfterAll
    void tearDown() {
        HibernateUtil.shutdown();
    }

    private void clearDatabase() {
        List<User> users = userDao.findAll();
        for (User user : users) {
            userDao.delete(user.getId());
        }
    }

    @Test
    void save_WithValidUser_ShouldPersistUser() {
        User user = new User("John Doe", "john@example.com", 30);

        Long userId = userDao.save(user);

        assertThat(userId).isNotNull();

        Optional<User> savedUser = userDao.findById(userId);
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getName()).isEqualTo("John Doe");
        assertThat(savedUser.get().getEmail()).isEqualTo("john@example.com");
        assertThat(savedUser.get().getAge()).isEqualTo(30);
        assertThat(savedUser.get().getCreatedAt()).isNotNull();
    }

    @Test
    void save_WithDuplicateEmail_ShouldThrowException() {
        User user1 = new User("John Doe", "duplicate@example.com", 30);
        userDao.save(user1);

        User user2 = new User("Jane Smith", "duplicate@example.com", 25);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDao.save(user2);
        });

        assertThat(exception.getMessage()).contains("Email already exists");
    }

    @Test
    void findById_WithExistingUser_ShouldReturnUser() {
        User user = new User("John Doe", "john@example.com", 30);
        Long userId = userDao.save(user);

        Optional<User> foundUser = userDao.findById(userId);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(userId);
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void findById_WithNonExistentUser_ShouldReturnEmpty() {
        Optional<User> foundUser = userDao.findById(999L);

        assertThat(foundUser).isEmpty();
    }

    @Test
    void findAll_WithMultipleUsers_ShouldReturnAllUsers() {

        userDao.save(new User("John Doe", "john@example.com", 30));
        userDao.save(new User("Jane Smith", "jane@example.com", 25));
        userDao.save(new User("Bob Johnson", "bob@example.com", 35));

        List<User> users = userDao.findAll();

        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith", "Bob Johnson");
    }

    @Test
    void findAll_WithNoUsers_ShouldReturnEmptyList() {
        List<User> users = userDao.findAll();

        assertThat(users).isEmpty();
    }

    @Test
    void update_WithExistingUser_ShouldUpdateUser() {

        User user = new User("Old Name", "old@example.com", 25);
        Long userId = userDao.save(user);

        User userToUpdate = userDao.findById(userId).get();
        userToUpdate.setName("New Name");
        userToUpdate.setEmail("new@example.com");
        userToUpdate.setAge(30);

        userDao.update(userToUpdate);

        Optional<User> updatedUser = userDao.findById(userId);
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("New Name");
        assertThat(updatedUser.get().getEmail()).isEqualTo("new@example.com");
        assertThat(updatedUser.get().getAge()).isEqualTo(30);
    }

    @Test
    void delete_WithExistingUser_ShouldRemoveUser() {

        User user = new User("John Doe", "john@example.com", 30);
        Long userId = userDao.save(user);

        assertThat(userDao.findById(userId)).isPresent();

        userDao.delete(userId);

        assertThat(userDao.findById(userId)).isEmpty();
    }

    @Test
    void delete_WithNonExistentUser_ShouldNotThrowException() {

        assertDoesNotThrow(() -> userDao.delete(999L));
    }
}