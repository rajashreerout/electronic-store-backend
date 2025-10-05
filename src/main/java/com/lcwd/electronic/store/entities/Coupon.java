package com.lcwd.electronic.store.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;
    private String description;
    private double discountPercent;
    private double maxDiscount;
    private double minPurchaseAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;
    private int usageLimit;
    private int timesUsed;
}