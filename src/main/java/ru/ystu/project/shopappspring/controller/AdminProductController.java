package ru.ystu.project.shopappspring.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ystu.project.shopappspring.dto.ProductForm;
import ru.ystu.project.shopappspring.model.Product;
import ru.ystu.project.shopappspring.service.ProductService;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("isEdit", false);
        return "admin/product-form";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute("productForm") ProductForm form,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/product-form";
        }
        try {
            productService.createProduct(form.getName(), form.getPrice(),
                    form.getDescription(), form.getStockQuantity());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", false);
            return "admin/product-form";
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        ProductForm form = new ProductForm();
        form.setId(product.getId());
        form.setName(product.getName());
        form.setPrice(product.getPrice());
        form.setDescription(product.getDescription());
        form.setStockQuantity(product.getStockQuantity());
        model.addAttribute("productForm", form);
        model.addAttribute("isEdit", true);
        return "admin/product-form";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productForm") ProductForm form,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/product-form";
        }
        try {
            productService.updateProduct(id, form.getName(), form.getPrice(),
                    form.getDescription(), form.getStockQuantity());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", true);
            return "admin/product-form";
        }
        return "redirect:/admin/products";
    }

    // НОВОЕ: удаление
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }
}