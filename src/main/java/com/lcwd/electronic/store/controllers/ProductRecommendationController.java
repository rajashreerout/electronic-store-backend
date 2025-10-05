package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ProductDto;
import com.lcwd.electronic.store.services.ProductRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/recommendations")
public class ProductRecommendationController {

    @Autowired
    private ProductRecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductDto>> getRecommendedProducts(
            @PathVariable String userId,
            @RequestParam(defaultValue = "5") int limit) {
        return new ResponseEntity<>(recommendationService.getRecommendedProducts(userId, limit), HttpStatus.OK);
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDto>> getSimilarProducts(
            @PathVariable String productId,
            @RequestParam(defaultValue = "5") int limit) {
        return new ResponseEntity<>(recommendationService.getSimilarProducts(productId, limit), HttpStatus.OK);
    }

    @PostMapping("/user/{userId}/product/{productId}/view")
    public ResponseEntity<Void> recordProductView(
            @PathVariable String userId,
            @PathVariable String productId) {
        recommendationService.recordProductView(userId, productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<List<ProductDto>> getMostViewedProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return new ResponseEntity<>(recommendationService.getMostViewedProducts(limit), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/recently-viewed")
    public ResponseEntity<List<ProductDto>> getRecentlyViewedProducts(
            @PathVariable String userId,
            @RequestParam(defaultValue = "5") int limit) {
        return new ResponseEntity<>(recommendationService.getRecentlyViewedProducts(userId, limit), HttpStatus.OK);
    }
}