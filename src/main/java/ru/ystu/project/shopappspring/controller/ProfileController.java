package ru.ystu.project.shopappspring.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.ystu.project.shopappspring.dto.PasswordChangeForm;
import ru.ystu.project.shopappspring.model.User;
import ru.ystu.project.shopappspring.service.UserService;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("passwordForm", new PasswordChangeForm());
        return "profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @ModelAttribute("passwordForm") PasswordChangeForm form,
                                 Model model) {
        User user = userService.findByUsername(userDetails.getUsername());

        if (form.getNewPassword() == null || !form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Пароли не совпадают");
            return "profile";
        }

        try {
            userService.changePassword(user.getId(), form.getOldPassword(), form.getNewPassword());
            return "redirect:/profile?success=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("error", e.getMessage());
            return "profile";
        }
    }
}