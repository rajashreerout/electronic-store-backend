package com.lcwd.electronic.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockNotificationDto {
    private Long id;
    private ProductDto product;
    private UserDto user;
    private boolean notified;
    private Date createdDate;
    private Date notifiedDate;
    private Integer lowStockThreshold;
}