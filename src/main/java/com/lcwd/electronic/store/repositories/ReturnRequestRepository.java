package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.ReturnRequest;
import com.lcwd.electronic.store.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    Page<ReturnRequest> findByOrder(Order order, Pageable pageable);
    Page<ReturnRequest> findByStatus(String status, Pageable pageable);
}