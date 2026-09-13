package com.smartparking.repository;

import com.smartparking.entity.ParkingSpot;
import com.smartparking.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ParkingSpot entities
 */
@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    Optional<ParkingSpot> findBySpotNumber(String spotNumber);

    /**
     * Find available parking spots for a specific vehicle type
     */
    List<ParkingSpot> findByAvailableTrueAndSupportedVehicleType(VehicleType vehicleType);

    /**
     * Find available spots on a specific floor
     */
    List<ParkingSpot> findByAvailableTrueAndFloorNumber(Integer floorNumber);

    /**
     * Find available spots for a vehicle type on a specific floor
     */
    List<ParkingSpot> findByAvailableTrueAndSupportedVehicleTypeAndFloorNumber(
            VehicleType vehicleType, Integer floorNumber);

    /**
     * Count available spots for a vehicle type
     */
    long countByAvailableTrueAndSupportedVehicleType(VehicleType vehicleType);

    /**
     * Find all occupied spots
     */
    List<ParkingSpot> findByAvailableFalse();
}
