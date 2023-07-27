package com.iemr.flw.controller;

import static org.mockito.Mockito.*;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.ANCVisitDTO;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;
import com.iemr.flw.dto.iemr.InfantRegisterDTO;
import com.iemr.flw.dto.iemr.PregnantWomanDTO;
import com.iemr.flw.service.DeliveryOutcomeService;
import com.iemr.flw.service.InfantService;
import com.iemr.flw.service.PregnantWomanService;
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

class MaternalHealthControllerTest {

    @InjectMocks
    private MaternalHealthController maternalHealthController;

    @Mock
    private PregnantWomanService pregnantWomanService;

    @Mock
    private DeliveryOutcomeService deliveryOutcomeService;

    @Mock
    private InfantService infantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSavePregnantWomanRegistrations() {
        // Arrange
        List<PregnantWomanDTO> pregnantWomanDTOs = Arrays.asList(new PregnantWomanDTO());

        when(pregnantWomanService.registerPregnantWoman(any())).thenReturn("Save successful");

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.savePregnantWomanRegistrations(pregnantWomanDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals("Save successful", apiResponse.getData());

        verify(pregnantWomanService, times(1)).registerPregnantWoman(pregnantWomanDTOs);
    }

    @Test
    void testSavePregnantWomanRegistrations_Exception() {
        // Arrange
        List<PregnantWomanDTO> pregnantWomanDTOs = Arrays.asList(new PregnantWomanDTO());

        when(pregnantWomanService.registerPregnantWoman(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.savePregnantWomanRegistrations(pregnantWomanDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(pregnantWomanService, times(1)).registerPregnantWoman(pregnantWomanDTOs);
    }

    @Test
    void testGetPregnantWomanList() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1090);

        List<PregnantWomanDTO> responseDTOs = Arrays.asList(new PregnantWomanDTO());
        when(pregnantWomanService.getPregnantWoman(any())).thenReturn(responseDTOs);

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.getPregnantWomanList(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals(responseDTOs, apiResponse.getData());

        verify(pregnantWomanService, times(1)).getPregnantWoman(requestDTO);
    }

    @Test
    void testGetPregnantWomanList_Exception() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1090);

        when(pregnantWomanService.getPregnantWoman(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.getPregnantWomanList(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(pregnantWomanService, times(1)).getPregnantWoman(requestDTO);
    }

    @Test
    void testSaveANCVisit() {
        // Arrange
        List<ANCVisitDTO> ancVisitDTOs = Arrays.asList(new ANCVisitDTO());

        when(pregnantWomanService.saveANCVisit(any())).thenReturn("Save successful");

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.saveANCVisit(ancVisitDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals("Save successful", apiResponse.getData());

        verify(pregnantWomanService, times(1)).saveANCVisit(ancVisitDTOs);
    }

    @Test
    void testSaveANCVisit_Exception() {
        // Arrange
        List<ANCVisitDTO> ancVisitDTOs = Arrays.asList(new ANCVisitDTO());

        when(pregnantWomanService.saveANCVisit(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.saveANCVisit(ancVisitDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(pregnantWomanService, times(1)).saveANCVisit(ancVisitDTOs);
    }

    @Test
    void testGetANCVisitDetails() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1098);

        List<ANCVisitDTO> responseDTOs = Arrays.asList(new ANCVisitDTO());
        when(pregnantWomanService.getANCVisits(any())).thenReturn(responseDTOs);

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.getANCVisitDetails(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals(responseDTOs, apiResponse.getData());

        verify(pregnantWomanService, times(1)).getANCVisits(requestDTO);
    }

    @Test
    void testGetANCVisitDetails_Exception() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1098);

        when(pregnantWomanService.getANCVisits(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.getANCVisitDetails(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(pregnantWomanService, times(1)).getANCVisits(requestDTO);
    }

    @Test
    void testSaveDeliveryOutcome() {
        // Arrange
        List<DeliveryOutcomeDTO> deliveryOutcomeDTOs = Arrays.asList(new DeliveryOutcomeDTO());

        when(deliveryOutcomeService.registerDeliveryOutcome(any())).thenReturn("Save successful");

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.saveDeliveryOutcome(deliveryOutcomeDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals("Save successful", apiResponse.getData());

        verify(deliveryOutcomeService, times(1)).registerDeliveryOutcome(deliveryOutcomeDTOs);
    }

    @Test
    void testSaveDeliveryOutcome_Exception() {
        // Arrange
        List<DeliveryOutcomeDTO> deliveryOutcomeDTOs = Arrays.asList(new DeliveryOutcomeDTO());

        when(deliveryOutcomeService.registerDeliveryOutcome(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.saveDeliveryOutcome(deliveryOutcomeDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(deliveryOutcomeService, times(1)).registerDeliveryOutcome(deliveryOutcomeDTOs);
    }

    @Test
    void testGetDeliveryOutcome() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1098);

        List<DeliveryOutcomeDTO> responseDTOs = Arrays.asList(new DeliveryOutcomeDTO());
        when(deliveryOutcomeService.getDeliveryOutcome(any())).thenReturn(responseDTOs);

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.getDeliveryOutcome(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals(responseDTOs, apiResponse.getData());

        verify(deliveryOutcomeService, times(1)).getDeliveryOutcome(requestDTO);
    }

    @Test
    void testGetDeliveryOutcome_Exception() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1098);

        when(deliveryOutcomeService.getDeliveryOutcome(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.getDeliveryOutcome(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(deliveryOutcomeService, times(1)).getDeliveryOutcome(requestDTO);
    }

    @Test
    void testSaveInfantList() {
        // Arrange
        List<InfantRegisterDTO> infantRegisterDTOs = Arrays.asList(new InfantRegisterDTO());

        when(infantService.registerInfant(any())).thenReturn("Save successful");

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.saveInfantList(infantRegisterDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals("Save successful", apiResponse.getData());

        verify(infantService, times(1)).registerInfant(infantRegisterDTOs);
    }

    @Test
    void testSaveInfantList_Exception() {
        // Arrange
        List<InfantRegisterDTO> infantRegisterDTOs = Arrays.asList(new InfantRegisterDTO());

        when(infantService.registerInfant(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.saveInfantList(infantRegisterDTOs, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(infantService, times(1)).registerInfant(infantRegisterDTOs);
    }

    @Test
    void testGetInfantList() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1098);

        List<InfantRegisterDTO> responseDTOs = Arrays.asList(new InfantRegisterDTO());
        when(infantService.getInfantDetails(any())).thenReturn(responseDTOs);

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.getInfantList(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals(responseDTOs, apiResponse.getData());

        verify(infantService, times(1)).getInfantDetails(requestDTO);
    }

    @Test
    void testGetInfantList_Exception() {
        // Arrange
        GetBenRequestHandler requestDTO = new GetBenRequestHandler();
        requestDTO.setAshaId(1098);

        when(infantService.getInfantDetails(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = maternalHealthController.getInfantList(requestDTO, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(infantService, times(1)).getInfantDetails(requestDTO);
    }

}
