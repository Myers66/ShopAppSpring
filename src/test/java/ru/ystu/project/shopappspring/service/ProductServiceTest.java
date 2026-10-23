package ru.ystu.project.shopappspring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ystu.project.shopappspring.model.Product;
import ru.ystu.project.shopappspring.repository.ProductRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Ноутбук", 50000.0, "Мощный ноутбук", 10);
        testProduct.setId(1L);
    }

    // Тест 1: Успешное создание товара
    @Test
    void testCreateProduct_Success() {
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product created = productService.createProduct("Мышка", 1500.0, "Беспроводная", 50);

        assertNotNull(created);
        assertEquals("Мышка", created.getName());
        assertEquals(1500.0, created.getPrice());
        assertEquals(50, created.getStockQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // Тест 2: Ошибка при отрицательной цене
    @Test
    void testCreateProduct_NegativePrice_ThrowsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct("Мышка", -100, "Описание", 10)
        );

        assertTrue(ex.getMessage().contains("Цена должна быть положительной"));
        verify(productRepository, never()).save(any(Product.class));
    }

    // Тест 3: Ошибка при нулевой цене
    @Test
    void testCreateProduct_ZeroPrice_ThrowsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct("Мышка", 0, "Описание", 10)
        );

        assertTrue(ex.getMessage().contains("Цена должна быть положительной"));
        verify(productRepository, never()).save(any(Product.class));
    }

    // Тест 4: Ошибка при отрицательном остатке
    @Test
    void testCreateProduct_NegativeStock_ThrowsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct("Мышка", 1500, "Описание", -5)
        );

        assertTrue(ex.getMessage().contains("Остаток не может быть отрицательным"));
        verify(productRepository, never()).save(any(Product.class));
    }

    // Тест 5: Успешное обновление
    @Test
    void testUpdateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product updated = productService.updateProduct(1L, "Ноутбук Pro", 75000.0, "Обновлённый", 5);

        assertEquals("Ноутбук Pro", updated.getName());
        assertEquals(75000.0, updated.getPrice());
        assertEquals(5, updated.getStockQuantity());
    }

    // Тест 6: Ошибка при обновлении с отрицательной ценой
    @Test
    void testUpdateProduct_NegativePrice_ThrowsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(1L, "Ноутбук", -500, "Описание", 10)
        );

        assertTrue(ex.getMessage().contains("Цена должна быть положительной"));
        verify(productRepository, never()).save(any(Product.class));
    }

    // Тест 7: Ошибка, если товар не найден
    @Test
    void testFindById_NotFound_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.findById(999L)
        );

        assertTrue(ex.getMessage().contains("не найден"));
    }
}