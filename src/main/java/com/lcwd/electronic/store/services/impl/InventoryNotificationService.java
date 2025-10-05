package com.lcwd.electronic.store.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.lcwd.electronic.store.services.NotificationService;

import java.util.Map;

@Service
public class InventoryNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    public void notifyInventoryUpdate(String productId, int newStock, boolean isLowStock) {
        messagingTemplate.convertAndSend("/topic/inventory-updates", 
            Map.of(
                "type", "STOCK_UPDATE",
                "productId", productId,
                "currentStock", newStock,
                "isLowStock", isLowStock
            ));
    }

    public void processNotifications(String productId) {
        notificationService.createNotification(
            "wetrsdfwetwfasfwdf", // admin role id from application.properties
            "Stock Update",
            String.format("Product %s is now back in stock", productId),
            "INVENTORY"
        );
    }

    public void notifyProductAdded(String productId) {
        messagingTemplate.convertAndSend("/topic/inventory-updates", 
            Map.of(
                "type", "PRODUCT_ADDED",
                "productId", productId
            ));
    }
}