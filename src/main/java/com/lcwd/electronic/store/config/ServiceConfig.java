package com.lcwd.electronic.store.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.lcwd.electronic.store.services",
    "com.lcwd.electronic.store.services.impl"
})
public class ServiceConfig {
    // Configuration class to ensure service beans are properly scanned and registered
}