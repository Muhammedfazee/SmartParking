package com.smartparking.controller;

import com.smartparking.dto.CheckInRequest;
import com.smartparking.entity.VehicleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ParkingController
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ParkingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/parking/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testGetAvailability() throws Exception {
        mockMvc.perform(get("/api/parking/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].vehicleType").exists())
                .andExpect(jsonPath("$[0].totalSpots").isNumber())
                .andExpect(jsonPath("$[0].availableSpots").isNumber());
    }

    @Test
    public void testCheckInSuccess() throws Exception {
        CheckInRequest request = new CheckInRequest(
                "TEST123",
                VehicleType.CAR,
                "Test User",
                "555-0000"
        );

        mockMvc.perform(post("/api/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate").value("TEST123"))
                .andExpect(jsonPath("$.spotNumber").exists())
                .andExpect(jsonPath("$.floorNumber").isNumber())
                .andExpect(jsonPath("$.entryTime").exists());
    }

    @Test
    public void testCheckInDuplicateVehicle() throws Exception {
        CheckInRequest request = new CheckInRequest(
                "DUP123",
                VehicleType.CAR,
                "Test User",
                "555-0000"
        );

        // First check-in should succeed
        mockMvc.perform(post("/api/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second check-in with same license plate should fail
        mockMvc.perform(post("/api/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testGetActiveTransactions() throws Exception {
        mockMvc.perform(get("/api/parking/active-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
