package com.smartparking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for check-out response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponse {
    private Long transactionId;
    private String licensePlate;
    private String spotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double parkingDurationHours;
    private Double fees;
    private String message;
}
