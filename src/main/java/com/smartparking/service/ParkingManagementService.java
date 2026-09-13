package com.smartparking.service;

import com.smartparking.dto.CheckInRequest;
import com.smartparking.dto.CheckInResponse;
import com.smartparking.dto.CheckOutResponse;
import com.smartparking.dto.ParkingAvailabilityDto;
import com.smartparking.entity.ParkingSpot;
import com.smartparking.entity.ParkingTransaction;
import com.smartparking.entity.Vehicle;
import com.smartparking.entity.VehicleType;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.exception.VehicleAlreadyParkedException;
import com.smartparking.repository.ParkingTransactionRepository;
import com.smartparking.util.FeeCalculationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main service for parking management operations
 * Handles vehicle check-in, check-out, and parking fee calculations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ParkingManagementService {

    private final ParkingSpotService parkingSpotService;
    private final VehicleService vehicleService;
    private final ParkingTransactionRepository parkingTransactionRepository;

    /**
     * Check-in a vehicle to the parking lot
     * Handles parking spot allocation and transaction recording
     */
    public CheckInResponse checkIn(CheckInRequest request) {
        // Check if vehicle is already parked
        if (parkingTransactionRepository.findActiveByLicensePlate(request.getLicensePlate()).isPresent()) {
            throw new VehicleAlreadyParkedException(
                    "Vehicle with license plate " + request.getLicensePlate() + " is already parked");
        }

        // Register or get vehicle
        Vehicle vehicle = vehicleService.registerVehicle(
                request.getLicensePlate(),
                request.getVehicleType(),
                request.getOwnerName(),
                request.getContactNumber()
        );

        // Allocate parking spot
        ParkingSpot spot = parkingSpotService.allocateSpot(request.getVehicleType());

        // Record parking transaction
        LocalDateTime entryTime = LocalDateTime.now();
        ParkingTransaction transaction = new ParkingTransaction(vehicle, spot, entryTime);
        transaction = parkingTransactionRepository.save(transaction);

        // Build response
        CheckInResponse response = new CheckInResponse();
        response.setTransactionId(transaction.getId());
        response.setLicensePlate(vehicle.getLicensePlate());
        response.setSpotNumber(spot.getSpotNumber());
        response.setFloorNumber(spot.getFloorNumber());
        response.setEntryTime(entryTime);
        response.setMessage("Vehicle checked in successfully. Parking spot: " + spot.getSpotNumber());

        return response;
    }

    /**
     * Check-out a vehicle from the parking lot
     * Calculates parking fee and updates availability
     */
    public CheckOutResponse checkOut(String licensePlate) {
        // Find active transaction
        ParkingTransaction transaction = parkingTransactionRepository.findActiveByLicensePlate(licensePlate)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active parking transaction found for vehicle: " + licensePlate));

        // Perform checkout
        return performCheckOut(transaction);
    }

    /**
     * Check-out by transaction ID
     */
    public CheckOutResponse checkOutByTransactionId(Long transactionId) {
        ParkingTransaction transaction = parkingTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Parking transaction not found with id: " + transactionId));

        if (!transaction.isActive()) {
            throw new ResourceNotFoundException(
                    "Transaction is not active. Already checked out.");
        }

        return performCheckOut(transaction);
    }

    /**
     * Internal method to perform checkout operations
     */
    private CheckOutResponse performCheckOut(ParkingTransaction transaction) {
        LocalDateTime exitTime = LocalDateTime.now();

        // Calculate fees
        Double fees = FeeCalculationUtil.calculateFee(
                transaction.getEntryTime(),
                exitTime,
                transaction.getVehicle().getVehicleType()
        );

        // Update transaction
        transaction.setExitTime(exitTime);
        transaction.setFees(fees);
        transaction.setActive(false);
        transaction = parkingTransactionRepository.save(transaction);

        // Release parking spot
        parkingSpotService.releaseSpot(transaction.getParkingSpot().getId());

        // Build response
        double durationHours = FeeCalculationUtil.getParkingDurationInHours(
                transaction.getEntryTime(), exitTime);

        CheckOutResponse response = new CheckOutResponse();
        response.setTransactionId(transaction.getId());
        response.setLicensePlate(transaction.getVehicle().getLicensePlate());
        response.setSpotNumber(transaction.getParkingSpot().getSpotNumber());
        response.setEntryTime(transaction.getEntryTime());
        response.setExitTime(exitTime);
        response.setParkingDurationHours(durationHours);
        response.setFees(fees);
        response.setMessage("Vehicle checked out successfully. Total fee: $" + String.format("%.2f", fees));

        return response;
    }

    /**
     * Get parking availability for all vehicle types
     */
    public List<ParkingAvailabilityDto> getParkingAvailability() {
        List<ParkingAvailabilityDto> availabilityList = new ArrayList<>();

        for (VehicleType type : VehicleType.values()) {
            List<ParkingSpot> availableSpots = parkingSpotService.getAvailableSpots(type);
            List<ParkingSpot> allSpots = parkingSpotService.getAllSpots();

            long total = allSpots.stream()
                    .filter(s -> s.getSupportedVehicleType() == type)
                    .count();
            long available = availableSpots.size();
            long occupied = total - available;
            double occupancyPercentage = total > 0 ? (occupied * 100.0) / total : 0;

            availabilityList.add(new ParkingAvailabilityDto(
                    type,
                    total,
                    available,
                    occupied,
                    occupancyPercentage
            ));
        }

        return availabilityList;
    }

    /**
     * Get transaction history for a vehicle
     */
    public List<ParkingTransaction> getVehicleHistory(String licensePlate) {
        Vehicle vehicle = vehicleService.getVehicleByLicensePlate(licensePlate);
        return parkingTransactionRepository.findByVehicleId(vehicle.getId());
    }

    /**
     * Get all active transactions
     */
    public List<ParkingTransaction> getActiveTransactions() {
        return parkingTransactionRepository.findByIsActiveTrue();
    }
}
