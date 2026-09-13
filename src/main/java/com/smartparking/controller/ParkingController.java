package com.smartparking.controller;

import com.smartparking.dto.CheckInRequest;
import com.smartparking.dto.CheckInResponse;
import com.smartparking.dto.CheckOutResponse;
import com.smartparking.dto.ParkingAvailabilityDto;
import com.smartparking.entity.ParkingTransaction;
import com.smartparking.service.ParkingManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for parking management operations
 * Exposes endpoints for check-in, check-out, and parking status
 */
@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingManagementService parkingManagementService;

    /**
     * Check-in a vehicle
     * POST /api/parking/check-in
     */
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody CheckInRequest request) {
        try {
            CheckInResponse response = parkingManagementService.checkIn(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Check-out a vehicle by license plate
     * POST /api/parking/check-out/{licensePlate}
     */
    @PostMapping("/check-out/{licensePlate}")
    public ResponseEntity<?> checkOut(@PathVariable String licensePlate) {
        try {
            CheckOutResponse response = parkingManagementService.checkOut(licensePlate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Check-out a vehicle by transaction ID
     * POST /api/parking/check-out/transaction/{transactionId}
     */
    @PostMapping("/check-out/transaction/{transactionId}")
    public ResponseEntity<?> checkOutByTransactionId(@PathVariable Long transactionId) {
        try {
            CheckOutResponse response = parkingManagementService.checkOutByTransactionId(transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get parking availability
     * GET /api/parking/availability
     */
    @GetMapping("/availability")
    public ResponseEntity<List<ParkingAvailabilityDto>> getAvailability() {
        List<ParkingAvailabilityDto> availability = parkingManagementService.getParkingAvailability();
        return ResponseEntity.ok(availability);
    }

    /**
     * Get active parking transactions
     * GET /api/parking/active-transactions
     */
    @GetMapping("/active-transactions")
    public ResponseEntity<List<ParkingTransaction>> getActiveTransactions() {
        List<ParkingTransaction> transactions = parkingManagementService.getActiveTransactions();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get parking history for a vehicle
     * GET /api/parking/history/{licensePlate}
     */
    @GetMapping("/history/{licensePlate}")
    public ResponseEntity<?> getVehicleHistory(@PathVariable String licensePlate) {
        try {
            List<ParkingTransaction> history = parkingManagementService.getVehicleHistory(licensePlate);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Smart Parking System is running");
        return ResponseEntity.ok(response);
    }

    /**
     * Build error response
     */
    private ResponseEntity<Map<String, String>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
