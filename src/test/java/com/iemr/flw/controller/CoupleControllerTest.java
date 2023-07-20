package com.iemr.flw.controller;

import com.iemr.flw.dto.EligibleCoupleDTO;
import com.iemr.flw.dto.EligibleCoupleTrackingDTO;
import com.iemr.flw.service.CoupleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CoupleControllerTest {

    @Mock
    private CoupleService coupleService;

    @InjectMocks
    private CoupleController coupleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveEligibleCouple_ValidInput_ReturnsAcceptedResponse() {
        List<EligibleCoupleDTO> eligibleCoupleDTOs = new ArrayList<>();

        when(coupleService.registerEligibleCouple(eligibleCoupleDTOs)).thenReturn("Success");

        // Act
        ResponseEntity<?> response = coupleController.saveEligibleCouple(eligibleCoupleDTOs, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void saveEligibleCouple_InvalidInput_ReturnsInternalServerErrorResponse() {
        List<EligibleCoupleDTO> eligibleCoupleDTOs = new ArrayList<>();

        when(coupleService.registerEligibleCouple(eligibleCoupleDTOs)).thenThrow(new RuntimeException("Invalid input"));

        // Act
        ResponseEntity<?> response = coupleController.saveEligibleCouple(eligibleCoupleDTOs, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getEligibleCouple_ValidBenId_ReturnsAcceptedResponse() {
        Long benId = 123L;
        EligibleCoupleDTO expectedResponse = new EligibleCoupleDTO();
        // Set up coupleService.getEligibleCouple to return expectedResponse
        when(coupleService.getEligibleCouple(benId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = coupleController.getEligibleCouple(benId, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void getEligibleCouple_InvalidBenId_ReturnsInternalServerErrorResponse() {
        Long benId = 999L;

        when(coupleService.getEligibleCouple(benId)).thenThrow(new RuntimeException("Invalid beneficiary ID"));

        // Act
        ResponseEntity<?> response = coupleController.getEligibleCouple(benId, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    void saveEligibleCoupleTracking_ValidInput_ReturnsAcceptedResponse() {
        List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOS = new ArrayList<>();
        // Add test data to eligibleCoupleTrackingDTOS

        when(coupleService.registerEligibleCoupleTracking(eligibleCoupleTrackingDTOS)).thenReturn("Success");

        // Act
        ResponseEntity<?> response = coupleController.saveEligibleCoupleTracking(eligibleCoupleTrackingDTOS, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void saveEligibleCoupleTracking_InvalidInput_ReturnsInternalServerErrorResponse() {
        List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOS = new ArrayList<>();
        // No test data added to eligibleCoupleTrackingDTOS

        when(coupleService.registerEligibleCoupleTracking(eligibleCoupleTrackingDTOS)).thenThrow(new RuntimeException("Invalid input"));

        // Act
        ResponseEntity<?> response = coupleController.saveEligibleCoupleTracking(eligibleCoupleTrackingDTOS, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getEligibleCoupleTracking_ValidEcrId_ReturnsAcceptedResponse() {
        Long ecrId = 123L;
        List<EligibleCoupleTrackingDTO> expectedResponse = new ArrayList<>();
        // Set up coupleService.getEligibleCoupleTracking to return expectedResponse
        when(coupleService.getEligibleCoupleTracking(ecrId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = coupleController.getEligibleCoupleTracking(ecrId, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void getEligibleCoupleTracking_InvalidEcrId_ReturnsInternalServerErrorResponse() {
        Long ecrId = 999L;

        when(coupleService.getEligibleCoupleTracking(ecrId)).thenThrow(new RuntimeException("Invalid ECR ID"));

        // Act
        ResponseEntity<?> response = coupleController.getEligibleCoupleTracking(ecrId, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
