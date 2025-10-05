package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.ProductDto;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.ProductView;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.ProductViewRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.ProductRecommendationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductRecommendationServiceImpl implements ProductRecommendationService {

    @Autowired
    private ProductViewRepository productViewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public List<ProductDto> getRecommendedProducts(String userId, int limit) {
        List<String> productIds = productViewRepository.findMostViewedProductIds(userId);
        return productIds.stream()
            .limit(limit)
            .map(id -> productRepository.findById(id)
                .map(product -> mapper.map(product, ProductDto.class))
                .orElse(null))
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getSimilarProducts(String productId, int limit) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        // Get products in the same category
        return product.getCategory().getProducts().stream()
            .filter(p -> !p.getProductId().equals(productId))
            .limit(limit)
            .map(p -> mapper.map(p, ProductDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public void recordProductView(String userId, String productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductView productView = new ProductView();
        productView.setUser(user);
        productView.setProduct(product);
        productView.setViewDate(new Date());

        productViewRepository.save(productView);
    }

    @Override
    public List<ProductDto> getMostViewedProducts(int limit) {
        // This is a simplified implementation. In a real system, you'd want to use analytics
        // or aggregation to get truly most viewed products across all users
        return productRepository.findAll().stream()
            .sorted((p1, p2) -> Long.compare(
                productViewRepository.countByProductProductId(p2.getProductId()),
                productViewRepository.countByProductProductId(p1.getProductId())
            ))
            .limit(limit)
            .map(product -> mapper.map(product, ProductDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getRecentlyViewedProducts(String userId, int limit) {
        return productViewRepository.findByUserUserIdOrderByViewDateDesc(userId).stream()
            .map(ProductView::getProduct)
            .distinct()
            .limit(limit)
            .map(product -> mapper.map(product, ProductDto.class))
            .collect(Collectors.toList());
    }
}