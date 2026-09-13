package com.smartparking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a vehicle in the parking system
 */
@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "License plate cannot be blank")
    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String contactNumber;

    public Vehicle(String licensePlate, VehicleType vehicleType, String ownerName, String contactNumber) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.ownerName = ownerName;
        this.contactNumber = contactNumber;
    }
}
