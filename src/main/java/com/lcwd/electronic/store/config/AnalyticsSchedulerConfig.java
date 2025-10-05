package com.lcwd.electronic.store.config;

import com.lcwd.electronic.store.services.OrderAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class AnalyticsSchedulerConfig {

    @Autowired
    private OrderAnalyticsService orderAnalyticsService;

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void generateHourlyAnalytics() {
        orderAnalyticsService.generateDailyAnalytics();
    }

    // Run at midnight every day
    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyAnalytics() {
        orderAnalyticsService.generateDailyAnalytics();
    }
}