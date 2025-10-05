package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.NotificationDto;
import com.lcwd.electronic.store.dtos.PageableResponse;

public interface NotificationService {
    NotificationDto createNotification(String userId, String title, String message, String type);
    PageableResponse<NotificationDto> getUserNotifications(String userId, int pageNumber, int pageSize);
    void markAsRead(Long notificationId);
    void markAllAsRead(String userId);
    long getUnreadCount(String userId);
}