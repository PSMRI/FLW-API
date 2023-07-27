package com.iemr.flw.controller;

import static org.mockito.Mockito.*;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.service.CoupleService;
import com.iemr.flw.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoupleControllerTest {

    @InjectMocks
    private CoupleController coupleController;

    @Mock
    private CoupleService coupleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSaveEligibleCouple() {
        // Arrange
        List<EligibleCoupleDTO> eligibleCoupleDTOs = Arrays.asList(new EligibleCoupleDTO());

        when(coupleService.registerEligibleCouple(any())).thenReturn("Save successful");

        // Act
        ResponseEntity<?> responseEntity = coupleController.saveEligibleCouple(eligibleCoupleDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals("Save successful", apiResponse.getData());

        verify(coupleService, times(1)).registerEligibleCouple(eligibleCoupleDTOs);
    }

    @Test
    void testSaveEligibleCouple_Exception() {
        // Arrange
        List<EligibleCoupleDTO> eligibleCoupleDTOs = Arrays.asList(new EligibleCoupleDTO());

        when(coupleService.registerEligibleCouple(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = coupleController.saveEligibleCouple(eligibleCoupleDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(coupleService, times(1)).registerEligibleCouple(eligibleCoupleDTOs);
    }

    @Test
    void testGetEligibleCouple() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(198);

        List<EligibleCoupleDTO> responseDTOs = Arrays.asList(new EligibleCoupleDTO());
        when(coupleService.getEligibleCoupleRegRecords(any())).thenReturn(responseDTOs);

        // Act
        ResponseEntity<?> responseEntity = coupleController.getEligibleCouple(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals(responseDTOs, apiResponse.getData());

        verify(coupleService, times(1)).getEligibleCoupleRegRecords(requestDTO);
    }

    @Test
    void testGetEligibleCouple_Exception() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1098);

        when(coupleService.getEligibleCoupleRegRecords(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = coupleController.getEligibleCouple(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(coupleService, times(1)).getEligibleCoupleRegRecords(requestDTO);
    }

    @Test
    void testSaveEligibleCoupleTracking() {
        // Arrange
        List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOS = Arrays.asList(new EligibleCoupleTrackingDTO());

        when(coupleService.registerEligibleCoupleTracking(any())).thenReturn("Save successful");

        // Act
        ResponseEntity<?> responseEntity = coupleController.saveEligibleCoupleTracking(eligibleCoupleTrackingDTOS, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals("Save successful", apiResponse.getData());

        verify(coupleService, times(1)).registerEligibleCoupleTracking(eligibleCoupleTrackingDTOS);
    }

    @Test
    void testSaveEligibleCoupleTracking_Exception() {
        // Arrange
        List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOS = Arrays.asList(new EligibleCoupleTrackingDTO());

        when(coupleService.registerEligibleCoupleTracking(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = coupleController.saveEligibleCoupleTracking(eligibleCoupleTrackingDTOS, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(coupleService, times(1)).registerEligibleCoupleTracking(eligibleCoupleTrackingDTOS);
    }

    @Test
    void testGetEligibleCoupleTracking() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1298);

        List<EligibleCoupleTrackingDTO> responseDTOs = Arrays.asList(new EligibleCoupleTrackingDTO());
        when(coupleService.getEligibleCoupleTracking(any())).thenReturn(responseDTOs);

        // Act
        ResponseEntity<?> responseEntity = coupleController.getEligibleCoupleTracking(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals(responseDTOs, apiResponse.getData());

        verify(coupleService, times(1)).getEligibleCoupleTracking(requestDTO);
    }

    @Test
    void testGetEligibleCoupleTracking_Exception() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1028);

        when(coupleService.getEligibleCoupleTracking(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = coupleController.getEligibleCoupleTracking(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(coupleService, times(1)).getEligibleCoupleTracking(requestDTO);
    }
}
