package com.smartparking.exception;

/**
 * Exception thrown when a vehicle is already parked
 */
public class VehicleAlreadyParkedException extends RuntimeException {
    public VehicleAlreadyParkedException(String message) {
        super(message);
    }

    public VehicleAlreadyParkedException(String message, Throwable cause) {
        super(message, cause);
    }
}
