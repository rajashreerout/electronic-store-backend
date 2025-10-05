package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.services.InventoryInitializationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminUtilController {

    private final InventoryInitializationService inventoryInitializationService;

    public AdminUtilController(InventoryInitializationService inventoryInitializationService) {
        this.inventoryInitializationService = inventoryInitializationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/initialize-inventory")
    public ResponseEntity<ApiResponseMessage> initializeInventory() {
        inventoryInitializationService.initializeInventoryForExistingProducts();
        return new ResponseEntity<>(
            ApiResponseMessage.builder()
                .message("Inventory initialized for all existing products")
                .success(true)
                .status(HttpStatus.OK)
                .build(),
            HttpStatus.OK
        );
    }
}