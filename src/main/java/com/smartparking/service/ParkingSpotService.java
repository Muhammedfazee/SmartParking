package com.smartparking.service;

import com.smartparking.entity.ParkingSpot;
import com.smartparking.entity.VehicleType;
import com.smartparking.exception.NoParkingSpotAvailableException;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing parking spots
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;

    /**
     * Allocate a parking spot for a vehicle
     * Uses a greedy algorithm to find the first available spot
     */
    public ParkingSpot allocateSpot(VehicleType vehicleType) {
        List<ParkingSpot> availableSpots = parkingSpotRepository
                .findByAvailableTrueAndSupportedVehicleType(vehicleType);

        if (availableSpots.isEmpty()) {
            throw new NoParkingSpotAvailableException(
                    "No parking spots available for vehicle type: " + vehicleType);
        }

        // Greedy approach: allocate the first available spot
        // Can be enhanced with more sophisticated algorithms like:
        // 1. Nearest to exit
        // 2. Lowest floor
        // 3. Based on parking duration prediction
        ParkingSpot spot = availableSpots.get(0);
        spot.setAvailable(false);
        return parkingSpotRepository.save(spot);
    }

    /**
     * Release a parking spot when vehicle exits
     */
    public void releaseSpot(Long spotId) {
        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking spot not found with id: " + spotId));

        spot.setAvailable(true);
        parkingSpotRepository.save(spot);
    }

    /**
     * Get parking spot by spot number
     */
    public ParkingSpot getSpotByNumber(String spotNumber) {
        return parkingSpotRepository.findBySpotNumber(spotNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Parking spot not found with number: " + spotNumber));
    }

    /**
     * Get parking spot by ID
     */
    public ParkingSpot getSpotById(Long id) {
        return parkingSpotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Parking spot not found with id: " + id));
    }

    /**
     * Get available spots for a specific vehicle type
     */
    public List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        return parkingSpotRepository.findByAvailableTrueAndSupportedVehicleType(vehicleType);
    }

    /**
     * Get all parking spots
     */
    public List<ParkingSpot> getAllSpots() {
        return parkingSpotRepository.findAll();
    }

    /**
     * Get occupancy statistics for a vehicle type
     */
    public long getAvailableSpotsCount(VehicleType vehicleType) {
        return parkingSpotRepository.countByAvailableTrueAndSupportedVehicleType(vehicleType);
    }

    /**
     * Initialize parking lot with default spots
     */
    public void initializeParkingLot() {
        // Create parking lot structure
        int[][] spotConfiguration = {
            // Floor 1: 10 CAR spots, 5 MOTORCYCLE spots, 2 BUS spots
            new int[]{10, 5, 0, 2},
            // Floor 2: 10 CAR spots, 5 MOTORCYCLE spots, 2 BUS spots
            new int[]{10, 5, 0, 2},
            // Floor 3: 8 CAR spots, 3 MOTORCYCLE spots, 1 BUS spot
            new int[]{8, 3, 0, 1}
        };

        VehicleType[] vehicleTypes = {
            VehicleType.CAR, VehicleType.MOTORCYCLE, VehicleType.SUV, VehicleType.BUS
        };

        for (int floor = 0; floor < spotConfiguration.length; floor++) {
            int spotCounter = 1;
            for (int typeIdx = 0; typeIdx < vehicleTypes.length; typeIdx++) {
                int count = spotConfiguration[floor][typeIdx];
                for (int i = 0; i < count; i++) {
                    String spotNumber = String.format("F%dS%02d", floor + 1, spotCounter);
                    ParkingSpot spot = new ParkingSpot(
                            spotNumber,
                            floor + 1,
                            vehicleTypes[typeIdx]
                    );
                    parkingSpotRepository.save(spot);
                    spotCounter++;
                }
            }
        }
    }
}
