package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProductProductId(String productId);
    Page<ProductReview> findByProductProductId(String productId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.productId = :productId")
    Double averageRatingByProductProductId(@Param("productId") String productId);
    
    long countByProductProductId(String productId);
}