package com.lcwd.electronic.store.dtos;

import lombok.*;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAnalyticsDto {
    private Long id;
    private LocalDateTime date;
    private double dailyRevenue;
    private int totalOrders;
    private int completedOrders;
    private int cancelledOrders;
    private double averageOrderValue;
    private int totalProducts;
    private int totalCustomers;
    private int creditCardPayments;
    private int debitCardPayments;
    private int upiPayments;
    private int netBankingPayments;
}