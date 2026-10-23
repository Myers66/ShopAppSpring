package ru.ystu.project.shopappspring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ystu.project.shopappspring.model.Role;
import ru.ystu.project.shopappspring.model.User;
import ru.ystu.project.shopappspring.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "hashed_pass", Role.USER, "Test User");
        testUser.setId(1L);
    }

    // Тест 1: Успешное создание пользователя
    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed123");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.createUser("newuser", "password123", Role.USER, "New User");

        assertNotNull(created);
        assertEquals("newuser", created.getUsername());
        assertEquals("hashed123", created.getPasswordHash());
        assertEquals(Role.USER, created.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Тест 2: Ошибка при дублирующемся логине
    @Test
    void testCreateUser_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("testuser", "password123", Role.USER, "Test")
        );

        assertTrue(ex.getMessage().contains("уже существует"));
        verify(userRepository, never()).save(any(User.class));
    }

    // Тест 3: Ошибка при коротком пароле
    @Test
    void testCreateUser_ShortPassword_ThrowsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("newuser", "ab", Role.USER, "Test")
        );

        assertTrue(ex.getMessage().contains("Пароль должен быть минимум 3 символа"));
        verify(userRepository, never()).save(any(User.class));
    }

    // Тест 4: Обновление пользователя без смены пароля
    @Test
    void testUpdateUser_WithoutPasswordChange() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updated = userService.updateUser(1L, "newlogin", "New Name", Role.ADMIN, "");

        assertEquals("newlogin", updated.getUsername());
        assertEquals("New Name", updated.getFullName());
        assertEquals(Role.ADMIN, updated.getRole());
        // Пароль не должен меняться
        assertEquals("hashed_pass", updated.getPasswordHash());
        verify(passwordEncoder, never()).encode(anyString());
    }

    // Тест 5: Обновление с новым паролем
    @Test
    void testUpdateUser_WithNewPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpass123")).thenReturn("hashed_new");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updated = userService.updateUser(1L, "testuser", "Test User", Role.USER, "newpass123");

        assertEquals("hashed_new", updated.getPasswordHash());
        verify(passwordEncoder, times(1)).encode("newpass123");
    }

    // Тест 6: Ошибка при обновлении с коротким новым паролем
    @Test
    void testUpdateUser_ShortNewPassword_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(1L, "testuser", "Test", Role.USER, "ab")
        );

        assertTrue(ex.getMessage().contains("Пароль должен быть минимум 3 символа"));
    }
}