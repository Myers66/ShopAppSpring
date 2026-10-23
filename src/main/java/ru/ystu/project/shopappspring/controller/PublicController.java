package ru.ystu.project.shopappspring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ystu.project.shopappspring.service.ProductService;

@Controller
public class PublicController {

    private final ProductService productService;

    public PublicController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String productsPage(@RequestParam(required = false) String name,
                               @RequestParam(required = false) Double min,
                               @RequestParam(required = false) Double max,
                               Model model) {
        model.addAttribute("products", productService.searchProducts(name, min, max));
        model.addAttribute("searchName", name);
        model.addAttribute("searchMin", min);
        model.addAttribute("searchMax", max);
        return "products";
    }
}