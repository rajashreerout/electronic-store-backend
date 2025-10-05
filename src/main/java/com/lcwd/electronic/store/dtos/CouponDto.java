package com.lcwd.electronic.store.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {
    private Long id;
    
    @NotBlank(message = "Coupon code is required")
    private String code;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Discount percentage is required")
    private double discountPercent;
    
    @NotNull(message = "Maximum discount amount is required")
    private double maxDiscount;
    
    @NotNull(message = "Minimum purchase amount is required")
    private double minPurchaseAmount;
    
    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;
    
    @NotNull(message = "Valid until date is required")
    private LocalDateTime validUntil;
    
    private boolean active;
    private int usageLimit;
    private int timesUsed;
}