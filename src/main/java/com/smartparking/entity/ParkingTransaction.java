package com.smartparking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing a parking transaction (vehicle entry and exit)
 */
@Entity
@Table(name = "parking_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parking_spot_id", nullable = false)
    private ParkingSpot parkingSpot;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    @Column
    private LocalDateTime exitTime;

    @Column
    private Double fees;

    @Column(nullable = false)
    private boolean isActive = true;

    public ParkingTransaction(Vehicle vehicle, ParkingSpot parkingSpot, LocalDateTime entryTime) {
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.entryTime = entryTime;
        this.isActive = true;
    }
}
