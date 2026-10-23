package ru.ystu.project.shopappspring.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        model.addAttribute("errorTitle", "Ошибка");
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("errorCode", "400");
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException e, Model model) {
        model.addAttribute("errorTitle", "Страница не найдена");
        model.addAttribute("errorMessage", "Запрошенная страница не существует.");
        model.addAttribute("errorCode", "404");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleAll(Exception e, Model model, HttpServletRequest request) {
        model.addAttribute("errorTitle", "Внутренняя ошибка");
        model.addAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "Что-то пошло не так.");
        model.addAttribute("errorCode", "500");
        return "error";
    }
}