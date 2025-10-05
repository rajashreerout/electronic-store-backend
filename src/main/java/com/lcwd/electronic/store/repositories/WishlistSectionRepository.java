package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.WishlistSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistSectionRepository extends JpaRepository<WishlistSection, Long> {
    List<WishlistSection> findByUserUserId(String userId);
}