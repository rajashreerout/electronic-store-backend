package com.lcwd.electronic.store.entities;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String size;
    private String color;
    private int stock;
    private double price;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}