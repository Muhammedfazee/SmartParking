package com.smartparking.util;

import com.smartparking.entity.VehicleType;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Utility class for parking fee calculation
 */
public class FeeCalculationUtil {

    private static final double MINIMUM_CHARGE = 2.0;  // Minimum charge for any parking

    /**
     * Calculate parking fee based on entry and exit time and vehicle type
     */
    public static Double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, VehicleType vehicleType) {
        if (entryTime == null || exitTime == null) {
            return 0.0;
        }

        // Calculate duration in hours
        Duration duration = Duration.between(entryTime, exitTime);
        double hours = Math.ceil(duration.toMinutes() / 60.0);  // Round up to next hour

        // Calculate fee
        double fee = hours * vehicleType.getHourlyRate();

        // Apply minimum charge
        return Math.max(fee, MINIMUM_CHARGE);
    }

    /**
     * Calculate current parking duration in hours
     */
    public static long getCurrentParkingDurationInMinutes(LocalDateTime entryTime) {
        if (entryTime == null) {
            return 0;
        }
        return Duration.between(entryTime, LocalDateTime.now()).toMinutes();
    }

    /**
     * Calculate parking duration between two times in hours
     */
    public static double getParkingDurationInHours(LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null) {
            return 0;
        }
        Duration duration = Duration.between(entryTime, exitTime);
        return Math.ceil(duration.toMinutes() / 60.0);
    }
}
