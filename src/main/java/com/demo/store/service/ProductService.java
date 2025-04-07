package com.demo.store.service;

import com.demo.store.exception.NotFoundException;
import com.demo.store.model.Product;
import com.demo.store.repository.OrderProductRepository;
import com.demo.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    /**
     * Get a list of all products
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        log.info("Getting a list of all products");
        return productRepository.findAll();
    }

    /**
     * Get a product by its ID
     * @param id Product ID
     * @return Product object
     */
    public Product getProductById(Long id) {
        log.info("Getting a product by ID {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product by ID {} not found", id);
                    return new NotFoundException("Product not found");
                });
    }

    /**
     * Create a new product
     * @param product Product object
     * @return Created product
     */
    @Transactional
    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty() || product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid product data");
        }
        log.info("Creation of a new product: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * Update an existing product
     * @param id Product ID
     * @param updatedProduct Updated product object
     * @return Updated product
     */
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        log.info("Updating of product by ID {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    log.info("Product found, updating...");
                    if (updatedProduct.getName() != null && !updatedProduct.getName().isEmpty()) {
                        product.setName(updatedProduct.getName());
                    }
                    if (updatedProduct.getPrice() != null && updatedProduct.getPrice().compareTo(BigDecimal.ZERO) >= 0) {
                        product.setPrice(updatedProduct.getPrice());
                    }
                    if (updatedProduct.getStockQuantity() != null && updatedProduct.getStockQuantity() >= 0) {
                        product.setStockQuantity(updatedProduct.getStockQuantity());
                    }
                    return productRepository.save(product);
                })
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found, update failed", id);
                    return new NotFoundException("Product not found");
                });
    }

    /**
     * Delete a product by its ID
     * @param id Product ID
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (orderProductRepository.existsByProductId(id)) {
            log.warn("Attempting to delete a product with ID {} that is associated with existing orders", id);
            throw new IllegalStateException("Cannot delete product that is associated with existing orders");
        }
        if (productRepository.existsById(id)) {
            log.info("Deleting product by ID {}", id);
            productRepository.deleteById(id);
        } else {
            log.warn("Attempting to delete a non-existent product with ID {}", id);
            throw new NotFoundException("Product not found");
        }
    }
}
