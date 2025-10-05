package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.StockNotificationDto;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.StockNotification;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.StockNotificationRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.StockNotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockNotificationServiceImpl implements StockNotificationService {

    @Autowired
    private StockNotificationRepository notificationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public StockNotificationDto subscribeToStockNotification(String userId, String productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if notification already exists
        if (notificationRepository.findByProductProductIdAndUserUserId(productId, userId).isPresent()) {
            throw new RuntimeException("Already subscribed to notifications for this product");
        }

        StockNotification notification = new StockNotification();
        notification.setProduct(product);
        notification.setUser(user);
        notification.setNotified(false);

        StockNotification savedNotification = notificationRepository.save(notification);
        return mapper.map(savedNotification, StockNotificationDto.class);
    }

    @Override
    public void unsubscribeFromStockNotification(String userId, String productId) {
        StockNotification notification = notificationRepository.findByProductProductIdAndUserUserId(productId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification subscription not found"));
        
        notificationRepository.delete(notification);
    }

    @Override
    public List<StockNotificationDto> getUserNotifications(String userId) {
        List<StockNotification> notifications = notificationRepository.findByUserUserIdAndNotifiedFalse(userId);
        return notifications.stream()
                .map(notification -> mapper.map(notification, StockNotificationDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void processStockNotifications(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.isStock() && product.getQuantity() > 0) {
            List<StockNotification> notifications = notificationRepository.findByProductProductIdAndNotifiedFalse(productId);
            
            for (StockNotification notification : notifications) {
                notification.setNotified(true);
                notification.setNotifiedDate(new Date());
                notificationRepository.save(notification);
            }
        }
    }
}