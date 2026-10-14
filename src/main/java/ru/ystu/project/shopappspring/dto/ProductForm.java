package ru.ystu.project.shopappspring.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProductForm {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Positive(message = "Цена должна быть больше нуля")
    private double price;

    private String description;

    @Min(value = 0, message = "Остаток не может быть отрицательным")
    private int stockQuantity;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
}