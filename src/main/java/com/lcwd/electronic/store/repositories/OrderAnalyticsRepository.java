package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.OrderAnalytics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderAnalyticsRepository extends JpaRepository<OrderAnalytics, Long> {
    List<OrderAnalytics> findByDateBetween(Date startDate, Date endDate);
    Page<OrderAnalytics> findByDateBetween(Date startDate, Date endDate, Pageable pageable);
}