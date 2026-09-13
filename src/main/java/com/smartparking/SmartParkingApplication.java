package com.smartparking;

import com.smartparking.service.ParkingSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot application class for Smart Parking System
 */
@SpringBootApplication
@RequiredArgsConstructor
public class SmartParkingApplication {

    private final ParkingSpotService parkingSpotService;

    public static void main(String[] args) {
        SpringApplication.run(SmartParkingApplication.class, args);
    }

    /**
     * Initialize parking lot on application startup
     */
    @Bean
    public CommandLineRunner initializeParking() {
        return args -> {
            try {
                parkingSpotService.initializeParkingLot();
                System.out.println("✓ Parking lot initialized successfully");
            } catch (Exception e) {
                System.err.println("Parking lot may already be initialized: " + e.getMessage());
            }
        };
    }
}
