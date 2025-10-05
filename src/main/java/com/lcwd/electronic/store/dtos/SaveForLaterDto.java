package com.lcwd.electronic.store.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveForLaterDto {
    private String id;
    private String userId;
    private String productId;
    private int quantity;
    private double price;
    private String addedFrom;  // CART, WISHLIST
}