
package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.ProductReviewDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.ProductReview;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.helper.Helper;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.ProductReviewRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.ProductReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    @Autowired
    private ProductReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public ProductReviewDto createReview(String productId, String userId, ProductReviewDto reviewDto) {
        try {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            // Validate the review data
            if (reviewDto.getRating() < 1 || reviewDto.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }

            ProductReview review = new ProductReview();
            review.setProduct(product);
            review.setUser(user);
            review.setRating(reviewDto.getRating());
            review.setComment(reviewDto.getReview());  // Map review to comment
            review.setReviewDate(new Date());

        ProductReview savedReview = reviewRepository.save(review);

        // Update product's average rating
        double avgRating = reviewRepository.averageRatingByProductProductId(productId);
        long totalReviews = reviewRepository.countByProductProductId(productId);
        product.setAverageRating(avgRating);
        product.setTotalReviews((int) totalReviews);
        productRepository.save(product);

        return mapper.map(savedReview, ProductReviewDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create review: " + e.getMessage());
        }
    }

    @Override
    public ProductReviewDto updateReview(Long reviewId, ProductReviewDto reviewDto) {
        ProductReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getReview());  // Map review to comment

        ProductReview updatedReview = reviewRepository.save(review);

        // Update product's average rating
        String productId = review.getProduct().getProductId();
        double avgRating = reviewRepository.averageRatingByProductProductId(productId);
        long totalReviews = reviewRepository.countByProductProductId(productId);
        Product product = review.getProduct();
        product.setAverageRating(avgRating);
        product.setTotalReviews((int) totalReviews);
        productRepository.save(product);

        return mapper.map(updatedReview, ProductReviewDto.class);
    }

    @Override
    public void deleteReview(Long reviewId) {
        ProductReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        String productId = review.getProduct().getProductId();
        reviewRepository.delete(review);

        // Update product's average rating
        double avgRating = reviewRepository.averageRatingByProductProductId(productId);
        long totalReviews = reviewRepository.countByProductProductId(productId);
        Product product = review.getProduct();
        product.setAverageRating(avgRating);
        product.setTotalReviews((int) totalReviews);
        productRepository.save(product);
    }

    @Override
    public PageableResponse<ProductReviewDto> getProductReviews(String productId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<ProductReview> page = reviewRepository.findByProductProductId(productId, pageable);
        return Helper.getPageableResponse(page, ProductReviewDto.class);
    }

    @Override
    public double getProductAverageRating(String productId) {
        Double avgRating = reviewRepository.averageRatingByProductProductId(productId);
        return avgRating != null ? avgRating : 0.0;
    }

    @Override
    public long getProductReviewCount(String productId) {
        return reviewRepository.countByProductProductId(productId);
    }
}