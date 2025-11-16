package com.example.service;

import com.example.dao.UserDao;
import com.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao);
    }

    @Test
    void createUser_WithValidData_ShouldReturnUserId() {
        String name = "John Doe";
        String email = "john@example.com";
        Integer age = 30;
        Long expectedId = 1L;

        when(userDao.save(any(User.class))).thenReturn(expectedId);

        Long result = userService.createUser(name, email, age);

        assertThat(result).isEqualTo(expectedId);
        verify(userDao).save(any(User.class));
    }

    @Test
    void createUser_WithInvalidName_ShouldThrowException() {

        assertThatThrownBy(() -> userService.createUser("", "test@example.com", 25))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be empty");

        verifyNoInteractions(userDao);
    }

    @Test
    void createUser_WithInvalidEmail_ShouldThrowException() {
        assertThatThrownBy(() -> userService.createUser("John Doe", "", 25))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be empty");

        verifyNoInteractions(userDao);
    }

    @Test
    void createUser_WithInvalidAge_ShouldThrowException() {

        assertThatThrownBy(() -> userService.createUser("John Doe", "john@example.com", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Age must be between 0 and 150");

        verifyNoInteractions(userDao);
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        Long userId = 1L;
        User expectedUser = new User("John Doe", "john@example.com", 30);
        expectedUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.getUserById(userId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedUser);
        verify(userDao).findById(userId);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        assertThatThrownBy(() -> userService.getUserById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid user ID");

        verifyNoInteractions(userDao);
    }

    @Test
    void getUserById_WithNonExistentId_ShouldReturnEmpty() {
        Long userId = 999L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertThat(result).isEmpty();
        verify(userDao).findById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> expectedUsers = Arrays.asList(
                new User("John Doe", "john@example.com", 30),
                new User("Jane Smith", "jane@example.com", 25)
        );

        when(userDao.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedUsers);
        verify(userDao).findAll();
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        Long userId = 1L;
        User existingUser = new User("Old Name", "old@example.com", 25);
        existingUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.updateUser(userId, "New Name", "new@example.com", 30);

        assertThat(existingUser.getName()).isEqualTo("New Name");
        assertThat(existingUser.getEmail()).isEqualTo("new@example.com");
        assertThat(existingUser.getAge()).isEqualTo(30);
        verify(userDao).update(existingUser);
    }

    @Test
    void updateUser_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        Long userId = 1L;
        User existingUser = new User("Old Name", "old@example.com", 25);
        existingUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.updateUser(userId, "New Name", null, null);

        assertThat(existingUser.getName()).isEqualTo("New Name");
        assertThat(existingUser.getEmail()).isEqualTo("old@example.com"); // unchanged
        assertThat(existingUser.getAge()).isEqualTo(25); // unchanged
        verify(userDao).update(existingUser);
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowException() {
        Long userId = 999L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, "New Name", "new@example.com", 30))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with ID: 999");

        verify(userDao, never()).update(any());
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userDao).delete(userId);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        assertThatThrownBy(() -> userService.deleteUser(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid user ID");

        verifyNoInteractions(userDao);
    }
}