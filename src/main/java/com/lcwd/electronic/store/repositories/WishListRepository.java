package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByUserUserId(String userId);
    boolean existsByUserUserIdAndProductProductId(String userId, String productId);
    @Modifying
    @Query("DELETE FROM WishList w WHERE w.user.userId = ?1 AND w.product.productId = ?2")
    void deleteByUserUserIdAndProductProductId(String userId, String productId);
}