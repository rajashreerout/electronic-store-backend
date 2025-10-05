package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
}