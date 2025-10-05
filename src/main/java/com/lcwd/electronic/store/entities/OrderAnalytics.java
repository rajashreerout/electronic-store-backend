package com.lcwd.electronic.store.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "order_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;
    private double dailyRevenue;
    private int totalOrders;
    private int completedOrders;
    private int cancelledOrders;
    private double averageOrderValue;
    private int totalProducts;
    private int totalCustomers;
}