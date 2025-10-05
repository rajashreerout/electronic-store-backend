package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.StockNotificationDto;
import java.util.List;

public interface StockNotificationService {
    StockNotificationDto subscribeToStockNotification(String userId, String productId);
    void unsubscribeFromStockNotification(String userId, String productId);
    List<StockNotificationDto> getUserNotifications(String userId);
    void processStockNotifications(String productId);
}