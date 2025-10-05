package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductViewRepository extends JpaRepository<ProductView, Long> {
    List<ProductView> findByUserUserIdOrderByViewDateDesc(String userId);
    
    @Query("SELECT v.product.productId FROM ProductView v WHERE v.user.userId = ?1 GROUP BY v.product.productId ORDER BY COUNT(v) DESC")
    List<String> findMostViewedProductIds(String userId);
    
    long countByProductProductId(String productId);
}