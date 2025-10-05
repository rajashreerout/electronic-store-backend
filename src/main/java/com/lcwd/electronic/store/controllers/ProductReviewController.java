package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ProductReviewDto;
import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.services.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ProductReviewController {

    @Autowired
    private ProductReviewService reviewService;

    @PostMapping
    public ResponseEntity<ProductReviewDto> createReview(@RequestBody ProductReviewDto reviewDto) {
        try {
            ProductReviewDto createdReview = reviewService.createReview(
                reviewDto.getProductId(),
                reviewDto.getUserId(),
                reviewDto
            );
            return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create review: " + e.getMessage());
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ProductReviewDto> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ProductReviewDto reviewDto) {
        return new ResponseEntity<>(reviewService.updateReview(reviewId, reviewDto), HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponseMessage> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        ApiResponseMessage response = ApiResponseMessage.builder()
            .message("Review deleted successfully!!")
            .status(HttpStatus.OK)
            .success(true)
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<ProductReviewDto>> getReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return new ResponseEntity<>(reviewService.getProductReviews(productId, pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);
    }

    @GetMapping("/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable String productId) {
        return new ResponseEntity<>(reviewService.getProductAverageRating(productId), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getReviewCount(@PathVariable String productId) {
        return new ResponseEntity<>(reviewService.getProductReviewCount(productId), HttpStatus.OK);
    }
}