package ru.ystu.project.shopappspring.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.ystu.project.shopappspring.model.Role;
import ru.ystu.project.shopappspring.service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (userService.findByUsername("admin") == null) {
            userService.createUser("admin", "admin", Role.ADMIN, "Администратор");
            System.out.println(">>> Создан администратор по умолчанию: admin / admin");
        }
    }
}