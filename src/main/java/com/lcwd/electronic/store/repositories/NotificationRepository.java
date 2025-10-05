package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserUserIdOrderByCreatedDateDesc(String userId, Pageable pageable);
    long countByUserUserIdAndReadFalse(String userId);
}