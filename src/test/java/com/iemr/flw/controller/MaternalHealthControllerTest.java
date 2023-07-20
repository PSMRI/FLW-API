package com.iemr.flw.controller;

import com.iemr.flw.dto.*;
import com.iemr.flw.service.DeliveryOutcomeService;
import com.iemr.flw.service.InfantService;
import com.iemr.flw.service.PregnantWomanService;
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

class MaternalHealthControllerTest {

    @Mock
    private PregnantWomanService pregnantWomanService;

    @Mock
    private DeliveryOutcomeService deliveryOutcomeService;

    @Mock
    private InfantService infantService;

    @InjectMocks
    private MaternalHealthController maternalHealthController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void savePregnantWomanRegistrations_ValidInput_ReturnsAcceptedResponse() {
        List<PregnantWomanDTO> pregnantWomanDTOs = new ArrayList<>();
        // Add test data to pregnantWomanDTOs

        when(pregnantWomanService.registerPregnantWoman(pregnantWomanDTOs)).thenReturn("Success");

        // Act
        ResponseEntity<?> response = maternalHealthController.savePregnantWomanRegistrations(pregnantWomanDTOs, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void savePregnantWomanRegistrations_InvalidInput_ReturnsInternalServerErrorResponse() {
        List<PregnantWomanDTO> pregnantWomanDTOs = new ArrayList<>();
        // No test data added to pregnantWomanDTOs

        when(pregnantWomanService.registerPregnantWoman(pregnantWomanDTOs)).thenThrow(new RuntimeException("Invalid input"));

        // Act
        ResponseEntity<?> response = maternalHealthController.savePregnantWomanRegistrations(pregnantWomanDTOs, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getPregnantWoman_ValidBenId_ReturnsAcceptedResponse() {
        Long benId = 123L;
        PregnantWomanDTO expectedResponse = new PregnantWomanDTO();
        // Set up pregnantWomanService.getPregnantWoman to return expectedResponse
        when(pregnantWomanService.getPregnantWoman(benId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = maternalHealthController.getPregnantWoman(benId, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void getPregnantWoman_InvalidBenId_ReturnsInternalServerErrorResponse() {
        Long benId = 999L;

        when(pregnantWomanService.getPregnantWoman(benId)).thenThrow(new RuntimeException("Invalid beneficiary ID"));

        // Act
        ResponseEntity<?> response = maternalHealthController.getPregnantWoman(benId, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    @Test
    void saveANCVisit_ValidInput_ReturnsAcceptedResponse() {
        List<ANCVisitDTO> ancVisitDTOs = new ArrayList<>();
        // Add test data to ancVisitDTOs

        when(pregnantWomanService.saveANCVisit(ancVisitDTOs)).thenReturn("Success");

        // Act
        ResponseEntity<?> response = maternalHealthController.saveANCVisit(ancVisitDTOs, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void saveANCVisit_InvalidInput_ReturnsInternalServerErrorResponse() {
        List<ANCVisitDTO> ancVisitDTOs = new ArrayList<>();
        // No test data added to ancVisitDTOs

        when(pregnantWomanService.saveANCVisit(ancVisitDTOs)).thenThrow(new RuntimeException("Invalid input"));

        // Act
        ResponseEntity<?> response = maternalHealthController.saveANCVisit(ancVisitDTOs, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getANCVisitDetails_ValidPwrId_ReturnsAcceptedResponse() {
        Long pwrId = 123L;
        List<ANCVisitDTO> expectedResponse = new ArrayList<>();
        // Set up pregnantWomanService.getANCVisits to return expectedResponse
        when(pregnantWomanService.getANCVisits(pwrId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = maternalHealthController.getANCVisitDetails(pwrId, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void getANCVisitDetails_InvalidPwrId_ReturnsInternalServerErrorResponse() {
        Long pwrId = 999L;

        when(pregnantWomanService.getANCVisits(pwrId)).thenThrow(new RuntimeException("Invalid PWR ID"));

        // Act
        ResponseEntity<?> response = maternalHealthController.getANCVisitDetails(pwrId, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void saveDeliveryOutcome_ValidInput_ReturnsAcceptedResponse() {
        List<DeliveryOutcomeDTO> deliveryOutcomeDTOS = new ArrayList<>();
        // Add test data to deliveryOutcomeDTOS

        when(deliveryOutcomeService.registerDeliveryOutcome(deliveryOutcomeDTOS)).thenReturn("Success");

        // Act
        ResponseEntity<?> response = maternalHealthController.saveDeliveryOutcome(deliveryOutcomeDTOS, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void saveDeliveryOutcome_InvalidInput_ReturnsInternalServerErrorResponse() {
        List<DeliveryOutcomeDTO> deliveryOutcomeDTOS = new ArrayList<>();
        // No test data added to deliveryOutcomeDTOS

        when(deliveryOutcomeService.registerDeliveryOutcome(deliveryOutcomeDTOS)).thenThrow(new RuntimeException("Invalid input"));

        // Act
        ResponseEntity<?> response = maternalHealthController.saveDeliveryOutcome(deliveryOutcomeDTOS, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getDeliveryOutcome_ValidBenId_ReturnsAcceptedResponse() {
        Long benId = 123L;
        DeliveryOutcomeDTO expectedResponse = new DeliveryOutcomeDTO();
        // Set up deliveryOutcomeService.getDeliveryOutcome to return expectedResponse
        when(deliveryOutcomeService.getDeliveryOutcome(benId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = maternalHealthController.getDeliveryOutcome(benId, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void getDeliveryOutcome_InvalidBenId_ReturnsInternalServerErrorResponse() {
        Long benId = 999L;

        when(deliveryOutcomeService.getDeliveryOutcome(benId)).thenThrow(new RuntimeException("Invalid beneficiary ID"));

        // Act
        ResponseEntity<?> response = maternalHealthController.getDeliveryOutcome(benId, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void saveInfantRegistration_ValidInput_ReturnsAcceptedResponse() {
        List<InfantRegisterDTO> infantRegisterDTOs = new ArrayList<>();
        // Add test data to infantRegisterDTOs

        when(infantService.registerInfant(infantRegisterDTOs)).thenReturn("Success");

        // Act
        ResponseEntity<?> response = maternalHealthController.saveEligibleCouple(infantRegisterDTOs, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void saveInfantRegistration_InvalidInput_ReturnsInternalServerErrorResponse() {
        List<InfantRegisterDTO> infantRegisterDTOs = new ArrayList<>();
        // No test data added to infantRegisterDTOs

        when(infantService.registerInfant(infantRegisterDTOs)).thenThrow(new RuntimeException("Invalid input"));

        // Act
        ResponseEntity<?> response = maternalHealthController.saveEligibleCouple(infantRegisterDTOs, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getInfantDetails_ValidBenId_ReturnsAcceptedResponse() {
        Long benId = 123L;
        InfantRegisterDTO expectedResponse = new InfantRegisterDTO();
        // Set up infantService.getInfantDetails to return expectedResponse
        when(infantService.getInfantDetails(benId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = maternalHealthController.getEligibleCouple(benId, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void getInfantDetails_InvalidBenId_ReturnsInternalServerErrorResponse() {
        Long benId = 999L;

        when(infantService.getInfantDetails(benId)).thenThrow(new RuntimeException("Invalid beneficiary ID"));

        // Act
        ResponseEntity<?> response = maternalHealthController.getEligibleCouple(benId, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
