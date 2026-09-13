package com.smartparking.dto;

import com.smartparking.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for check-in request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {
    private String licensePlate;
    private VehicleType vehicleType;
    private String ownerName;
    private String contactNumber;
}
