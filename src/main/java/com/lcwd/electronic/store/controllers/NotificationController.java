package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.NotificationDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.services.NotificationService;
import com.lcwd.electronic.store.services.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @PreAuthorize("#userId == authentication.principal.userId")
    @GetMapping("/users/{userId}")
    public ResponseEntity<PageableResponse<NotificationDto>> getUserNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return new ResponseEntity<>(
            notificationService.getUserNotifications(userId, pageNumber, pageSize),
            HttpStatus.OK
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponseMessage> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return new ResponseEntity<>(
            ApiResponseMessage.builder()
                .message("Notification marked as read")
                .success(true)
                .status(HttpStatus.OK)
                .build(),
            HttpStatus.OK
        );
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @PatchMapping("/users/{userId}/read-all")
    public ResponseEntity<ApiResponseMessage> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return new ResponseEntity<>(
            ApiResponseMessage.builder()
                .message("All notifications marked as read")
                .success(true)
                .status(HttpStatus.OK)
                .build(),
            HttpStatus.OK
        );
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @GetMapping("/users/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        return new ResponseEntity<>(
            notificationService.getUnreadCount(userId),
            HttpStatus.OK
        );
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String userId) {
        return pushNotificationService.subscribe(userId);
    }
}