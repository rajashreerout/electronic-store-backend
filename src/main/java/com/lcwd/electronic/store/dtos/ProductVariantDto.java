package com.lcwd.electronic.store.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantDto {
    private Long id;
    private String size;
    private String color;
    private int stock;
    private double price;
}