package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.entities.Product;

public interface InventoryInitializationService {

    void initializeInventoryForExistingProducts();

    void initializeInventoryForNewProduct(Product product);

}