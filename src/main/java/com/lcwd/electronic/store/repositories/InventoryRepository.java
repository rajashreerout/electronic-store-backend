package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lcwd.electronic.store.entities.Product;
import java.util.List;
import java.util.Optional;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct(Product product);
    Page<Inventory> findByCurrentStockLessThanEqual(int threshold, Pageable pageable);
    List<Inventory> findByCurrentStockLessThanEqual(int threshold);
    Optional<Inventory> findByProductProductId(String productId);
    Page<Inventory> findByStockAlertTrue(Pageable pageable);
    boolean existsByProductProductId(String productId);
}