package com.rohlik.store.service;

import com.rohlik.store.exception.NotFoundException;
import com.rohlik.store.model.Product;
import com.rohlik.store.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    /**
     * Constructor
     * @param productRepository Product repository
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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
                    if (updatedProduct.getName() != null) {
                        product.setName(updatedProduct.getName());
                    }
                    if (updatedProduct.getPrice() != null) {
                        product.setPrice(updatedProduct.getPrice());
                    }
                    if (updatedProduct.getStockQuantity() != null) {
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
        if (productRepository.existsById(id)) {
            log.info("Deleting product by ID {}", id);
            productRepository.deleteById(id);
        } else {
            log.warn("Attempting to delete a non-existent product with ID {}", id);
            throw new NotFoundException("Product not found");
        }
    }
}
