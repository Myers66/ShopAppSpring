package ru.ystu.project.shopappspring.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ystu.project.shopappspring.dto.UserForm;
import ru.ystu.project.shopappspring.model.User;
import ru.ystu.project.shopappspring.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    // Форма создания
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("isEdit", false);
        return "admin/user-form";
    }

    // Обработка создания
    @PostMapping
    public String createUser(@Valid @ModelAttribute("userForm") UserForm form,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/user-form";
        }
        try {
            userService.createUser(form.getUsername(), form.getPassword(),
                    form.getRole(), form.getFullName());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", false);
            return "admin/user-form";
        }
        return "redirect:/admin/users";
    }

    // НОВОЕ: форма редактирования
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setRole(user.getRole());
        form.setFullName(user.getFullName());
        model.addAttribute("userForm", form);
        model.addAttribute("isEdit", true);
        return "admin/user-form";
    }

    // НОВОЕ: обработка обновления
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userForm") UserForm form,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/user-form";
        }
        try {
            userService.updateUser(id, form.getUsername(), form.getFullName(),
                    form.getRole(), form.getPassword());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", true);
            return "admin/user-form";
        }
        return "redirect:/admin/users";
    }
}