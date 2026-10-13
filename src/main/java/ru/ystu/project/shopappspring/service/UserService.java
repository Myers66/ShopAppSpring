package ru.ystu.project.shopappspring.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ystu.project.shopappspring.model.Role;
import ru.ystu.project.shopappspring.model.User;
import ru.ystu.project.shopappspring.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id=" + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public User createUser(String username, String plainPassword, Role role, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Пользователь с логином '" + username + "' уже существует");
        }
        User user = new User(username, passwordEncoder.encode(plainPassword), role, fullName);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, String username, String fullName, Role role, String newPassword) {
        User user = findById(id);
        user.setUsername(username);
        user.setFullName(fullName);
        user.setRole(role);
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}