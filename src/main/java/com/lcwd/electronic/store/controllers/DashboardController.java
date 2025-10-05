package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.DashboardStatsDto;
import com.lcwd.electronic.store.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponseMessage> getDashboardStats() {
        DashboardStatsDto stats = dashboardService.getDashboardStats();
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Dashboard statistics fetched successfully")
                .success(true)
                .status(HttpStatus.OK)
                .data(stats)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}