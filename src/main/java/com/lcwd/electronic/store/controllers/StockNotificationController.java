package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.StockNotificationDto;
import com.lcwd.electronic.store.services.StockNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock-notifications")
public class StockNotificationController {

    @Autowired
    private StockNotificationService notificationService;

    @PreAuthorize("#userId == authentication.principal.userId")
    @PostMapping("/users/{userId}/products/{productId}")
    public ResponseEntity<StockNotificationDto> subscribeToNotification(
            @PathVariable String userId,
            @PathVariable String productId) {
        return new ResponseEntity<>(notificationService.subscribeToStockNotification(userId, productId), HttpStatus.CREATED);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @DeleteMapping("/users/{userId}/products/{productId}")
    public ResponseEntity<ApiResponseMessage> unsubscribeFromNotification(
            @PathVariable String userId,
            @PathVariable String productId) {
        notificationService.unsubscribeFromStockNotification(userId, productId);
        return new ResponseEntity<>(
            ApiResponseMessage.builder()
                .message("Unsubscribed from notifications successfully")
                .success(true)
                .status(HttpStatus.OK)
                .build(),
            HttpStatus.OK
        );
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<StockNotificationDto>> getUserNotifications(@PathVariable String userId) {
        return new ResponseEntity<>(notificationService.getUserNotifications(userId), HttpStatus.OK);
    }
}