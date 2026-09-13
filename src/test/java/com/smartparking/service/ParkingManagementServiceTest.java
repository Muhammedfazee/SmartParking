package com.smartparking.service;

import com.smartparking.dto.CheckInRequest;
import com.smartparking.dto.CheckInResponse;
import com.smartparking.entity.ParkingSpot;
import com.smartparking.entity.ParkingTransaction;
import com.smartparking.entity.Vehicle;
import com.smartparking.entity.VehicleType;
import com.smartparking.exception.NoParkingSpotAvailableException;
import com.smartparking.exception.ResourceNotFoundException;
import com.smartparking.exception.VehicleAlreadyParkedException;
import com.smartparking.repository.ParkingSpotRepository;
import com.smartparking.repository.ParkingTransactionRepository;
import com.smartparking.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ParkingManagementService
 */
@ExtendWith(MockitoExtension.class)
public class ParkingManagementServiceTest {

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingTransactionRepository parkingTransactionRepository;

    @Mock
    private ParkingSpotService parkingSpotService;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private ParkingManagementService parkingManagementService;

    private Vehicle testVehicle;
    private ParkingSpot testSpot;
    private ParkingTransaction testTransaction;

    @BeforeEach
    public void setUp() {
        // Create test data
        testVehicle = new Vehicle(1L, "ABC123", VehicleType.CAR, "John Doe", "555-1234");
        testSpot = new ParkingSpot(1L, "F1S01", 1, true, VehicleType.CAR);
        testTransaction = new ParkingTransaction(1L, testVehicle, testSpot,
                LocalDateTime.now(), null, null, true);
    }

    @Test
    public void testCheckInSuccess() {
        // Arrange
        CheckInRequest request = new CheckInRequest("ABC123", VehicleType.CAR, "John Doe", "555-1234");

        when(vehicleService.registerVehicle(anyString(), any(VehicleType.class), anyString(), anyString()))
                .thenReturn(testVehicle);
        when(parkingSpotService.allocateSpot(any(VehicleType.class)))
                .thenReturn(testSpot);
        when(parkingTransactionRepository.findActiveByLicensePlate(anyString()))
                .thenReturn(Optional.empty());
        when(parkingTransactionRepository.save(any(ParkingTransaction.class)))
                .thenReturn(testTransaction);

        // Act
        CheckInResponse response = parkingManagementService.checkIn(request);

        // Assert
        assertNotNull(response);
        assertEquals("ABC123", response.getLicensePlate());
        assertEquals("F1S01", response.getSpotNumber());
        assertEquals(1, response.getFloorNumber());
        verify(vehicleService, times(1)).registerVehicle(anyString(), any(VehicleType.class), anyString(), anyString());
        verify(parkingSpotService, times(1)).allocateSpot(any(VehicleType.class));
    }

    @Test
    public void testCheckInFailureVehicleAlreadyParked() {
        // Arrange
        CheckInRequest request = new CheckInRequest("ABC123", VehicleType.CAR, "John Doe", "555-1234");

        when(parkingTransactionRepository.findActiveByLicensePlate(anyString()))
                .thenReturn(Optional.of(testTransaction));

        // Act & Assert
        assertThrows(VehicleAlreadyParkedException.class, () -> {
            parkingManagementService.checkIn(request);
        });
    }

    @Test
    public void testCheckInFailureNoSpotAvailable() {
        // Arrange
        CheckInRequest request = new CheckInRequest("ABC123", VehicleType.CAR, "John Doe", "555-1234");

        when(parkingTransactionRepository.findActiveByLicensePlate(anyString()))
                .thenReturn(Optional.empty());
        when(vehicleService.registerVehicle(anyString(), any(VehicleType.class), anyString(), anyString()))
                .thenReturn(testVehicle);
        when(parkingSpotService.allocateSpot(any(VehicleType.class)))
                .thenThrow(new NoParkingSpotAvailableException("No parking spots available"));

        // Act & Assert
        assertThrows(NoParkingSpotAvailableException.class, () -> {
            parkingManagementService.checkIn(request);
        });
    }

    @Test
    public void testGetParkingAvailability() {
        // Arrange
        List<ParkingSpot> spotList = new ArrayList<>();
        spotList.add(testSpot);

        when(parkingSpotService.getAllSpots()).thenReturn(spotList);
        when(parkingSpotService.getAvailableSpots(any(VehicleType.class)))
                .thenReturn(spotList);

        // Act
        var availability = parkingManagementService.getParkingAvailability();

        // Assert
        assertNotNull(availability);
        assertEquals(4, availability.size()); // 4 vehicle types
        verify(parkingSpotService, times(4)).getAvailableSpots(any(VehicleType.class));
    }

    @Test
    public void testGetActiveTransactions() {
        // Arrange
        List<ParkingTransaction> transactions = new ArrayList<>();
        transactions.add(testTransaction);

        when(parkingTransactionRepository.findByIsActiveTrue())
                .thenReturn(transactions);

        // Act
        var result = parkingManagementService.getActiveTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVehicle.getLicensePlate(), result.get(0).getVehicle().getLicensePlate());
    }
}
