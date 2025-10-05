package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.OrderAnalyticsDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import java.time.LocalDateTime;

public interface OrderAnalyticsService {
    OrderAnalyticsDto getDailyAnalytics(LocalDateTime date);
    PageableResponse<OrderAnalyticsDto> getAnalyticsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int pageNumber, int pageSize, String sortBy, String sortDir);
    void generateDailyAnalytics();
    OrderAnalyticsDto getRevenueByCategory(String categoryId, LocalDateTime startDate, LocalDateTime endDate);
    OrderAnalyticsDto getRevenueByProduct(String productId, LocalDateTime startDate, LocalDateTime endDate);
}