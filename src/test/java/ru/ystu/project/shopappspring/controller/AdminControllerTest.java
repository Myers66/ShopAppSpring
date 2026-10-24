package ru.ystu.project.shopappspring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.ystu.project.shopappspring.model.Role;
import ru.ystu.project.shopappspring.model.User;
import ru.ystu.project.shopappspring.service.CustomUserDetailsService;
import ru.ystu.project.shopappspring.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // Тест 1: Список пользователей доступен админу
    @Test
    @WithMockUser(roles = "ADMIN")
    void testListUsers_AsAdmin_ReturnsUsersPage() throws Exception {
        User admin = new User("admin", "hash", Role.ADMIN, "Администратор");
        admin.setId(1L);
        when(userService.getAllUsers()).thenReturn(List.of(admin));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("admin")));
    }

    // Тест 2: Обычный пользователь не может зайти в админку
    @Test
    @WithMockUser(roles = "USER")
    void testListUsers_AsUser_Forbidden() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    // Тест 3: Неавторизованный пользователь перенаправляется на /login
    @Test
    void testListUsers_Anonymous_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // Тест 4: Форма создания открывается
    @Test
    @WithMockUser(roles = "ADMIN")
    void testNewUserForm_ReturnsForm() throws Exception {
        mockMvc.perform(get("/admin/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeExists("userForm"))
                .andExpect(model().attribute("isEdit", false));
    }

    // Тест 5: POST-создание пользователя — успешный редирект
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_ValidData_Redirects() throws Exception {
        when(userService.createUser(eq("newuser"), eq("password123"), eq(Role.USER), eq("New User")))
                .thenReturn(new User("newuser", "hash", Role.USER, "New User"));

        mockMvc.perform(post("/admin/users")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("role", "USER")
                        .param("fullName", "New User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1))
                .createUser(eq("newuser"), eq("password123"), eq(Role.USER), eq("New User"));
    }

    // Тест 6: POST-создание с коротким логином — возврат формы с ошибкой
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_ShortUsername_ReturnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .with(csrf())
                        .param("username", "ab")
                        .param("password", "password123")
                        .param("role", "USER")
                        .param("fullName", "Test"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attributeHasFieldErrors("userForm", "username"));

        verify(userService, never()).createUser(anyString(), anyString(), any(), anyString());
    }

    // Тест 7: POST-создание с дубликатом — возврат формы с ошибкой
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_DuplicateUsername_ReturnsFormWithError() throws Exception {
        when(userService.createUser(eq("admin"), anyString(), any(Role.class), anyString()))
                .thenThrow(new IllegalArgumentException("Пользователь с логином 'admin' уже существует"));

        mockMvc.perform(post("/admin/users")
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", "password123")
                        .param("role", "USER")
                        .param("fullName", "Test"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attribute("error", "Пользователь с логином 'admin' уже существует"));
    }

    // Тест 8: Форма редактирования подгружает существующего пользователя
    @Test
    @WithMockUser(roles = "ADMIN")
    void testEditUserForm_LoadsUser() throws Exception {
        User user = new User("testuser", "hash", Role.USER, "Test User");
        user.setId(5L);
        when(userService.findById(5L)).thenReturn(user);

        mockMvc.perform(get("/admin/users/5/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-form"))
                .andExpect(model().attribute("isEdit", true))
                .andExpect(model().attributeExists("userForm"));
    }

    // Тест 9: Удаление пользователя — редирект на список
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_Redirects() throws Exception {
        mockMvc.perform(post("/admin/users/3/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).deleteUser(3L);
    }
}