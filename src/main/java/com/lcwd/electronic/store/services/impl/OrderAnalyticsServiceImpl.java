package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.OrderAnalyticsDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.entities.Order;
import com.lcwd.electronic.store.entities.OrderAnalytics;
import com.lcwd.electronic.store.repositories.OrderAnalyticsRepository;
import com.lcwd.electronic.store.repositories.OrderRepository;
import com.lcwd.electronic.store.services.OrderAnalyticsService;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Service
public class OrderAnalyticsServiceImpl implements OrderAnalyticsService {

    private final OrderAnalyticsRepository orderAnalyticsRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public OrderAnalyticsServiceImpl(
        OrderAnalyticsRepository orderAnalyticsRepository,
        OrderRepository orderRepository,
        ModelMapper modelMapper
    ) {
        this.orderAnalyticsRepository = orderAnalyticsRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderAnalyticsDto getDailyAnalytics(LocalDateTime date) {
        Date startOfDay = Date.from(date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(date.toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant());
        
        List<OrderAnalytics> analytics = orderAnalyticsRepository.findByDateBetween(startOfDay, endOfDay);
        if (analytics.isEmpty()) {
            generateDailyAnalytics();
            analytics = orderAnalyticsRepository.findByDateBetween(startOfDay, endOfDay);
        }
        
        return analytics.isEmpty() ? new OrderAnalyticsDto() : modelMapper.map(analytics.get(0), OrderAnalyticsDto.class);
    }

    @Override
    public PageableResponse<OrderAnalyticsDto> getAnalyticsByDateRange(
            LocalDateTime startDateTime, 
            LocalDateTime endDateTime, 
            int pageNumber, 
            int pageSize, 
            String sortBy, 
            String sortDir) {
        
        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Page<OrderAnalytics> page = orderAnalyticsRepository.findByDateBetween(
                startDate, 
                endDate, 
                PageRequest.of(pageNumber, pageSize, sort)
        );

        PageableResponse<OrderAnalyticsDto> response = new PageableResponse<>();
        response.setContent(page.getContent().stream()
                .map(analytics -> modelMapper.map(analytics, OrderAnalyticsDto.class))
                .toList());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        return response;
    }

    @Override
    public void generateDailyAnalytics() {
        LocalDateTime now = LocalDateTime.now();
        Date startOfDay = Date.from(now.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(now.toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant());

        // Get all orders for today
        List<Order> todayOrders = orderRepository.findByOrderedDateBetween(startOfDay, endOfDay);

        // Calculate metrics
        double dailyRevenue = todayOrders.stream()
                .mapToDouble(Order::getOrderAmount)
                .sum();

        int totalOrders = todayOrders.size();
        
        int completedOrders = (int) todayOrders.stream()
                .filter(order -> "DELIVERED".equals(order.getOrderStatus()))
                .count();

        int cancelledOrders = (int) todayOrders.stream()
                .filter(order -> "CANCELLED".equals(order.getOrderStatus()))
                .count();

        double averageOrderValue = totalOrders > 0 ? dailyRevenue / totalOrders : 0;

        // Count unique products and customers
        int totalProducts = (int) todayOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(item -> item.getProduct().getProductId())
                .distinct()
                .count();

        int totalCustomers = (int) todayOrders.stream()
                .map(order -> order.getUser().getUserId())
                .distinct()
                .count();

        // First, try to find existing analytics for today
        List<OrderAnalytics> existingAnalytics = orderAnalyticsRepository.findByDateBetween(startOfDay, endOfDay);
        OrderAnalytics analytics;
        
        if (!existingAnalytics.isEmpty()) {
            // Update existing analytics
            analytics = existingAnalytics.get(0);
            analytics.setDailyRevenue(dailyRevenue);
            analytics.setTotalOrders(totalOrders);
            analytics.setCompletedOrders(completedOrders);
            analytics.setCancelledOrders(cancelledOrders);
            analytics.setAverageOrderValue(averageOrderValue);
            analytics.setTotalProducts(totalProducts);
            analytics.setTotalCustomers(totalCustomers);
        } else {
            // Create new analytics if none exist
            analytics = OrderAnalytics.builder()
                    .date(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                    .dailyRevenue(dailyRevenue)
                    .totalOrders(totalOrders)
                    .completedOrders(completedOrders)
                    .cancelledOrders(cancelledOrders)
                    .averageOrderValue(averageOrderValue)
                    .totalProducts(totalProducts)
                    .totalCustomers(totalCustomers)
                    .build();
        }

        orderAnalyticsRepository.save(analytics);
    }

    @Override
    public OrderAnalyticsDto getRevenueByCategory(String categoryId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<Order> orders = orderRepository.findByOrderedDateBetween(startDate, endDate);
        
        double revenue = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .filter(item -> categoryId.equals(item.getProduct().getCategory().getCategoryId()))
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getDiscountedPrice())
                .sum();

        return OrderAnalyticsDto.builder()
                .date(LocalDateTime.now())
                .dailyRevenue(revenue)
                .build();
    }

    @Override
    public OrderAnalyticsDto getRevenueByProduct(String productId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<Order> orders = orderRepository.findByOrderedDateBetween(startDate, endDate);
        
        double revenue = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .filter(item -> productId.equals(item.getProduct().getProductId()))
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getDiscountedPrice())
                .sum();

        return OrderAnalyticsDto.builder()
                .date(LocalDateTime.now())
                .dailyRevenue(revenue)
                .build();
    }
}