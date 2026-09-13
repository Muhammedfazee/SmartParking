package com.smartparking.exception;

/**
 * Exception thrown when no parking spot is available
 */
public class NoParkingSpotAvailableException extends RuntimeException {
    public NoParkingSpotAvailableException(String message) {
        super(message);
    }

    public NoParkingSpotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
