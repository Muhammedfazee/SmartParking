package com.smartparking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a parking spot in the parking lot
 */
@Entity
@Table(name = "parking_spots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String spotNumber;

    @Column(nullable = false)
    private Integer floorNumber;

    @Column(nullable = false)
    private boolean available = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType supportedVehicleType;

    public ParkingSpot(String spotNumber, Integer floorNumber, VehicleType supportedVehicleType) {
        this.spotNumber = spotNumber;
        this.floorNumber = floorNumber;
        this.supportedVehicleType = supportedVehicleType;
        this.available = true;
    }
}
