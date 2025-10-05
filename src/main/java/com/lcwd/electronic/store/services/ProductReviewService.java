package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.ProductReviewDto;
import com.lcwd.electronic.store.dtos.PageableResponse;

public interface ProductReviewService {
    ProductReviewDto createReview(String productId, String userId, ProductReviewDto reviewDto);
    ProductReviewDto updateReview(Long reviewId, ProductReviewDto reviewDto);
    void deleteReview(Long reviewId);
    PageableResponse<ProductReviewDto> getProductReviews(String productId, int pageNumber, int pageSize, String sortBy, String sortDir);
    double getProductAverageRating(String productId);
    long getProductReviewCount(String productId);
}