package ru.ystu.project.shopappspring.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ===== Публичные страницы =====

    @Test
    void testPublicPages_AccessibleWithoutAuth() throws Exception {
        // Главная страница
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        // Список товаров
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());

        // Форма входа
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        // Форма регистрации
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    // ===== Защищённые страницы =====

    @Test
    void testAdminPage_AnonymousUser_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAdminPage_UserRole_Forbidden() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminPage_AdminRole_Allowed() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminProductsPage_AdminRole_Allowed() throws Exception {
        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk());
    }

    // ===== H2-консоль =====

    @Test
    void testH2Console_Accessible() throws Exception {
        // H2-консоль в тестах отключена (spring.h2.console.enabled=false),
        // поэтому ожидаем 404
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().isNotFound());
    }

    // ===== CSS и статика =====

    @Test
    void testStaticResources_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk());
    }
}