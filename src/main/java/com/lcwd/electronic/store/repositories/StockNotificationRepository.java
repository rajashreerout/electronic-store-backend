package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.StockNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockNotificationRepository extends JpaRepository<StockNotification, Long> {
    List<StockNotification> findByProductProductIdAndNotifiedFalse(String productId);
    Optional<StockNotification> findByProductProductIdAndUserUserId(String productId, String userId);
    List<StockNotification> findByUserUserIdAndNotifiedFalse(String userId);
}