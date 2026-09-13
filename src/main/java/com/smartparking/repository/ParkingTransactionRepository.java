package com.smartparking.repository;

import com.smartparking.entity.ParkingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ParkingTransaction entities
 */
@Repository
public interface ParkingTransactionRepository extends JpaRepository<ParkingTransaction, Long> {

    /**
     * Find active transaction for a vehicle
     */
    Optional<ParkingTransaction> findByVehicleIdAndIsActiveTrue(Long vehicleId);

    /**
     * Find active transaction by vehicle license plate
     */
    @Query("SELECT pt FROM ParkingTransaction pt WHERE pt.vehicle.licensePlate = :licensePlate AND pt.isActive = true")
    Optional<ParkingTransaction> findActiveByLicensePlate(@Param("licensePlate") String licensePlate);

    /**
     * Find all active transactions
     */
    List<ParkingTransaction> findByIsActiveTrue();

    /**
     * Find all transactions for a vehicle
     */
    List<ParkingTransaction> findByVehicleId(Long vehicleId);

    /**
     * Find all completed transactions (with exit time)
     */
    @Query("SELECT pt FROM ParkingTransaction pt WHERE pt.exitTime IS NOT NULL ORDER BY pt.exitTime DESC")
    List<ParkingTransaction> findCompletedTransactions();
}
