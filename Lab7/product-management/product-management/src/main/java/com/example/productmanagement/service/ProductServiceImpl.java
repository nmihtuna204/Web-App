package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// TODO: Add @Service annotation
@Service
// TODO: Add @Transactional annotation
@Transactional
public class ProductServiceImpl implements ProductService {
    
    // TODO: Inject ProductRepository using constructor injection
    private final ProductRepository productRepository;
    
    // TODO: Create constructor with @Autowired
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> getAllProducts() {
        // TODO: Return all products from repository
        return productRepository.findAll();
    }
    
    @Override
    public Optional<Product> getProductById(Long id) {
        // TODO: Return product by id from repository
        return productRepository.findById(id);
    }
    
    @Override
    public Product saveProduct(Product product) {
        // TODO: Save product to repository
        return productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Long id) {
        // TODO: Delete product from repository
        productRepository.deleteById(id);
    }
    
    @Override
    public List<Product> searchProducts(String keyword) {
        // TODO: Search products using repository method
        return productRepository.findByNameContaining(keyword);
    }
    
    @Override
    public List<Product> getProductsByCategory(String category) {
        // TODO: Get products by category from repository
        return productRepository.findByCategory(category);
    }
}
