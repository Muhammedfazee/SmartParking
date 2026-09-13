package com.smartparking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API
 * Handles all custom and standard exceptions
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle NoParkingSpotAvailableException
     */
    @ExceptionHandler(NoParkingSpotAvailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleNoParkingSpotAvailableException(
            NoParkingSpotAvailableException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT,
                "PARKING_FULL");
    }

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND");
    }

    /**
     * Handle VehicleAlreadyParkedException
     */
    @ExceptionHandler(VehicleAlreadyParkedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleVehicleAlreadyParkedException(
            VehicleAlreadyParkedException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT,
                "VEHICLE_ALREADY_PARKED");
    }

    /**
     * Handle generic Exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
    }

    /**
     * Build error response
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status, String errorCode) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("status", status.value());
        errorResponse.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
