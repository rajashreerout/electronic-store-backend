package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.DashboardStatsDto;
import com.lcwd.electronic.store.repositories.CategoryRepository;
import com.lcwd.electronic.store.repositories.OrderRepository;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardStatsDto getDashboardStats() {
        return DashboardStatsDto.builder()
                .products(productRepository.count())
                .categories(categoryRepository.count())
                .orders(orderRepository.count())
                .users(userRepository.count())
                .build();
    }
}