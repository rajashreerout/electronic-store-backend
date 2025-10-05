package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.NotificationDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.entities.Notification;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.NotificationRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.NotificationService;
import com.lcwd.electronic.store.services.PushNotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;



    @Autowired
    private PushNotificationService pushNotificationService;

    @Override
    public NotificationDto createNotification(String userId, String title, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        NotificationDto notificationDto = mapper.map(savedNotification, NotificationDto.class);

        // Send push notification
        pushNotificationService.sendNotification(userId, notificationDto);



        return notificationDto;
    }

    @Override
    public PageableResponse<NotificationDto> getUserNotifications(String userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Notification> notificationsPage = notificationRepository.findByUserUserIdOrderByCreatedDateDesc(userId, pageable);
        
        return new PageableResponse<>(
            notificationsPage.getContent().stream()
                .map(notification -> mapper.map(notification, NotificationDto.class))
                .toList(),
            pageNumber,
            pageSize,
            notificationsPage.getTotalElements(),
            notificationsPage.getTotalPages(),
            notificationsPage.isLast()
        );
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        notificationRepository.findByUserUserIdOrderByCreatedDateDesc(userId, Pageable.unpaged())
                .forEach(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                });
    }

    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserUserIdAndReadFalse(userId);
    }
}