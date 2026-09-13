package com.smartparking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enum representing different types of vehicles
 */
public enum VehicleType {
    MOTORCYCLE(1, 5.0),      // 1 spot, $5/hour
    CAR(1, 10.0),            // 1 spot, $10/hour
    SUV(1, 12.0),            // 1 spot, $12/hour
    BUS(2, 20.0);            // 2 spots, $20/hour

    private final int spotsRequired;
    private final double hourlyRate;

    VehicleType(int spotsRequired, double hourlyRate) {
        this.spotsRequired = spotsRequired;
        this.hourlyRate = hourlyRate;
    }

    public int getSpotsRequired() {
        return spotsRequired;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}
