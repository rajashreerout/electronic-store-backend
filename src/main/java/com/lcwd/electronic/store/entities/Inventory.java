package com.lcwd.electronic.store.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Product product;
    
    private int currentStock;
    private int lowStockThreshold;
    private int reorderPoint;
    private LocalDateTime lastRestocked;
    private LocalDateTime lastUpdated;
    private String sku;
    private String location;
    private boolean stockAlert;
}