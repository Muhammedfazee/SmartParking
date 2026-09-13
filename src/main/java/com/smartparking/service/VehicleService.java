package com.smartparking.service;

import com.smartparking.entity.Vehicle;
import com.smartparking.entity.VehicleType;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing vehicles
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    /**
     * Register a new vehicle
     */
    public Vehicle registerVehicle(String licensePlate, VehicleType vehicleType, String ownerName, String contactNumber) {
        if (vehicleRepository.existsByLicensePlate(licensePlate)) {
            // Vehicle already exists, return existing one
            return vehicleRepository.findByLicensePlate(licensePlate)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        }

        Vehicle vehicle = new Vehicle(licensePlate, vehicleType, ownerName, contactNumber);
        return vehicleRepository.save(vehicle);
    }

    /**
     * Get vehicle by license plate
     */
    public Vehicle getVehicleByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found with license plate: " + licensePlate));
    }

    /**
     * Get vehicle by ID
     */
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found with id: " + id));
    }

    /**
     * Get all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Check if vehicle exists
     */
    public boolean vehicleExists(String licensePlate) {
        return vehicleRepository.existsByLicensePlate(licensePlate);
    }
}
