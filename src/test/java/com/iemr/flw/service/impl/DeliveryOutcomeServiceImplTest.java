package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.DeliveryOutcome;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;
import com.iemr.flw.repo.iemr.DeliveryOutcomeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DeliveryOutcomeServiceImplTest {

    @Mock
    private DeliveryOutcomeRepo deliveryOutcomeRepo;

    @InjectMocks
    private DeliveryOutcomeServiceImpl deliveryOutcomeService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerDeliveryOutcome_ValidInput_ReturnsSuccessMessage() {
        List<DeliveryOutcomeDTO> deliveryOutcomeDTOs = new ArrayList<>();
        // Add test data to deliveryOutcomeDTOs

        // Mock the repository save method
        when(deliveryOutcomeRepo.save(anyListOf(DeliveryOutcome.class))).thenReturn(new ArrayList<>());

        // Act
        String result = deliveryOutcomeService.registerDeliveryOutcome(deliveryOutcomeDTOs);

        // Assert
        assertEquals("saved successfully", result);
    }

    @Test
    void getDeliveryOutcome_ValidBenId_ReturnsDeliveryOutcomeDTO() {
        Long benId = 1L;
        DeliveryOutcome deliveryOutcome = new DeliveryOutcome();
        // Set up the repository getDeliveryOutcomeByBenId method to return deliveryOutcome
        when(deliveryOutcomeRepo.getDeliveryOutcomeByBenId(benId)).thenReturn(deliveryOutcome);

        // Act
        DeliveryOutcomeDTO result = deliveryOutcomeService.getDeliveryOutcome(benId);

        // Assert
        assertEquals(deliveryOutcome, objectMapper.convertValue(result, DeliveryOutcome.class));
    }

    @Test
    void getDeliveryOutcome_InvalidBenId_ReturnsNull() {
        Long benId = 999L;

        // Set up the repository getDeliveryOutcomeByBenId method to return null
        when(deliveryOutcomeRepo.getDeliveryOutcomeByBenId(benId)).thenReturn(null);

        // Act
        DeliveryOutcomeDTO result = deliveryOutcomeService.getDeliveryOutcome(benId);

        // Assert
        assertEquals(null, result);
    }
}

