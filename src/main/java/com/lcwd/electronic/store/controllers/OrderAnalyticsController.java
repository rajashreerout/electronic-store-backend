package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.OrderAnalyticsDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.services.OrderAnalyticsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/analytics")
public class OrderAnalyticsController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final OrderAnalyticsService analyticsService;

    public OrderAnalyticsController(OrderAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/daily")
    public ResponseEntity<OrderAnalyticsDto> getDailyAnalytics(
            @RequestParam(required = false) String date) {
        try {
            LocalDateTime dateTime;
            if (date != null && !date.trim().isEmpty()) {
                dateTime = LocalDateTime.parse(date.trim(), DATE_FORMATTER);
            } else {
                dateTime = LocalDateTime.now();
            }
            return new ResponseEntity<>(analyticsService.getDailyAnalytics(dateTime), HttpStatus.OK);
        } catch (DateTimeParseException e) {
            throw new ResourceNotFoundException("Invalid date format. Use format: yyyy-MM-dd HH:mm:ss");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/range")
    public ResponseEntity<PageableResponse<OrderAnalyticsDto>> getAnalyticsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate.trim(), DATE_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(endDate.trim(), DATE_FORMATTER);
            return new ResponseEntity<>(
                    analyticsService.getAnalyticsByDateRange(start, end, pageNumber, pageSize, sortBy, sortDir),
                    HttpStatus.OK);
        } catch (DateTimeParseException e) {
            throw new ResourceNotFoundException("Invalid date format. Use format: yyyy-MM-dd HH:mm:ss");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<OrderAnalyticsDto> getRevenueByCategory(
            @PathVariable String categoryId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return new ResponseEntity<>(
                analyticsService.getRevenueByCategory(categoryId, startDate, endDate),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/product/{productId}")
    public ResponseEntity<OrderAnalyticsDto> getRevenueByProduct(
            @PathVariable String productId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return new ResponseEntity<>(
                analyticsService.getRevenueByProduct(productId, startDate, endDate),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate")
    public ResponseEntity<ApiResponseMessage> generateDailyAnalytics() {
        analyticsService.generateDailyAnalytics();
        return new ResponseEntity<>(
                ApiResponseMessage.builder()
                    .message("Daily analytics generated successfully")
                    .success(true)
                    .status(HttpStatus.OK)
                    .build(),
                HttpStatus.OK);
    }
}