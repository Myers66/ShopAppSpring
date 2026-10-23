package ru.ystu.project.shopappspring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.ystu.project.shopappspring.service.ProductService;

@Controller
public class PublicController {

    private final ProductService productService;

    public PublicController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }
}