package ru.ystu.project.shopappspring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ystu.project.shopappspring.model.Product;
import ru.ystu.project.shopappspring.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар с id=" + id + " не найден"));
    }

    @Transactional
    public Product createProduct(String name, double price, String description, int stockQuantity) {
        if (price <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Остаток не может быть отрицательным");
        }
        Product product = new Product(name, price, description, stockQuantity);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, String name, double price, String description, int stockQuantity) {
        if (price <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Остаток не может быть отрицательным");
        }
        Product product = findById(id);
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setStockQuantity(stockQuantity);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Product> searchByPriceRange(double min, double max) {
        return productRepository.findByPriceBetween(min, max);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String name, Double minPrice, Double maxPrice) {
        return productRepository.findAll().stream()
                .filter(p -> name == null || name.isBlank()
                        || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .toList();
    }
}