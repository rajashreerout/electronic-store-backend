package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.Inventory;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.InventoryRepository;
import com.lcwd.electronic.store.services.InventoryInitializationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class InventoryInitializationServiceImpl implements InventoryInitializationService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public InventoryInitializationServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void initializeInventoryForExistingProducts() {
        List<Product> products = productRepository.findAll();
        
        for (Product product : products) {
            // Check if inventory already exists for this product
            if (!inventoryRepository.existsByProductProductId(product.getProductId())) {
                Inventory inventory = Inventory.builder()
                    .product(product)
                    .currentStock(product.getQuantity())
                    .lowStockThreshold(5)
                    .reorderPoint(10)
                    .lastUpdated(LocalDateTime.now())
                    .lastRestocked(LocalDateTime.now())
                    .stockAlert(product.getQuantity() <= 5)
                    .sku(generateSku(product))
                    .build();
                
                inventoryRepository.save(inventory);
            }
        }
    }

    private String generateSku(Product product) {
        // Generate a SKU based on product category and ID
        String categoryPrefix = product.getCategory() != null ? 
            product.getCategory().getTitle().substring(0, Math.min(3, product.getCategory().getTitle().length())).toUpperCase() : 
            "GEN";
        return categoryPrefix + "-" + product.getProductId().substring(0, 8);
    }

    @Override
    @Transactional
    public void initializeInventoryForNewProduct(Product product) {
        if (!inventoryRepository.existsByProductProductId(product.getProductId())) {
            Inventory inventory = Inventory.builder()
                .product(product)
                .currentStock(product.getQuantity())
                .lowStockThreshold(5)
                .reorderPoint(10)
                .lastUpdated(LocalDateTime.now())
                .lastRestocked(LocalDateTime.now())
                .stockAlert(product.getQuantity() <= 5)
                .sku(generateSku(product))
                .build();
            
            inventoryRepository.save(inventory);
        }
    }
}