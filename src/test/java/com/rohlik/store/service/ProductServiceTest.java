package com.rohlik.store.service;

import com.rohlik.store.exception.NotFoundException;
import com.rohlik.store.model.Product;
import com.rohlik.store.repository.OrderProductRepository;
import com.rohlik.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(19.99));
        product.setStockQuantity(10);
    }

    /**
     * Test for getting all products
     */
    @Test
    void getAllProducts_ShouldReturnProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        var products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findAll();
    }

    /**
     * Test for getting a product by its ID
     */
    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals(product.getId(), foundProduct.getId());
        verify(productRepository, times(1)).findById(1L);
    }

    /**
     * Test for getting a product by its ID when the product does not exist
     */
    @Test
    void getProductById_ShouldThrowException_WhenProductDoesNotExist() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    /**
     * Test for creating a product
     */
    @Test
    void createProduct_ShouldSucceed() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        var createdProduct = productService.createProduct(product);

        assertNotNull(createdProduct);
        assertEquals(product.getName(), createdProduct.getName());
        verify(productRepository, times(1)).save(product);
    }

    /**
     * Test for creating a product with invalid data
     */
    @Test
    void updateProduct_ShouldSucceed_WhenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        var updatedProduct = productService.updateProduct(1L, product);

        assertNotNull(updatedProduct);
        assertEquals(product.getName(), updatedProduct.getName());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
    }

    /**
     * Test for updating a product
     */
    @Test
    void updateProduct_ShouldThrowException_WhenProductDoesNotExist() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, product));
        verify(productRepository, times(1)).findById(1L);
    }

    /**
     * Test for deleting a product
     */
    @Test
    void deleteProduct_ShouldSucceed_WhenProductExists() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(orderProductRepository.existsByProductId(1L)).thenReturn(false);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(orderProductRepository, times(1)).existsByProductId(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    /**
     * Test for deleting a product, which is in an order
     */
    @Test
    void deleteProduct_ShouldThrowException_WhenProductIsInOrder() {
        when(orderProductRepository.existsByProductId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> productService.deleteProduct(1L));
        verify(orderProductRepository, times(1)).existsByProductId(1L);
        verify(productRepository, never()).deleteById(1L);
    }

    /**
     * Test for deleting a product when the product does not exist
     */
    @Test
    void deleteProduct_ShouldThrowException_WhenProductDoesNotExist() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, never()).deleteById(1L);
    }
}