package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.ProductDto;
import java.util.List;

public interface ProductRecommendationService {
    List<ProductDto> getRecommendedProducts(String userId, int limit);
    List<ProductDto> getSimilarProducts(String productId, int limit);
    void recordProductView(String userId, String productId);
    List<ProductDto> getMostViewedProducts(int limit);
    List<ProductDto> getRecentlyViewedProducts(String userId, int limit);
}