package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.Order;
import com.lcwd.electronic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUser(User user);
    List<Order> findByOrderedDateBetween(Date startDate, Date endDate);
}
