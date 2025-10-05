package com.lcwd.electronic.store.dtos;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDto {
    private Long id;
    
    @NotNull(message = "Product ID is required")
    private String productId;
    
    @Min(value = 0, message = "Current stock cannot be negative")
    private int currentStock;
    
    @Min(value = 1, message = "Low stock threshold must be at least 1")
    private int lowStockThreshold;
    
    @Min(value = 1, message = "Reorder point must be at least 1")
    private int reorderPoint;
    
    private LocalDateTime lastRestocked;
    private LocalDateTime lastUpdated;
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    private String location;
    private boolean stockAlert;
}