package com.smartparking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for check-in response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponse {
    private Long transactionId;
    private String licensePlate;
    private String spotNumber;
    private Integer floorNumber;
    private LocalDateTime entryTime;
    private String message;
}
