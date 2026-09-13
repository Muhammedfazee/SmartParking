package com.smartparking.dto;

import com.smartparking.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for parking availability information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingAvailabilityDto {
    private VehicleType vehicleType;
    private long totalSpots;
    private long availableSpots;
    private long occupiedSpots;
    private double occupancyPercentage;
}
