package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.NotificationDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface PushNotificationService {
    SseEmitter subscribe(String userId);
    void sendNotification(String userId, NotificationDto notification);
}