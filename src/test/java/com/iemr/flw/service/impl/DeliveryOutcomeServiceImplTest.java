package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.DeliveryOutcome;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;
import com.iemr.flw.repo.iemr.DeliveryOutcomeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryOutcomeServiceImplTest {

    @Mock
    private DeliveryOutcomeRepo deliveryOutcomeRepo;

    @InjectMocks
    private DeliveryOutcomeServiceImpl deliveryOutcomeService;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRegisterDeliveryOutcome_Success() {
        // Arrange
        List<DeliveryOutcomeDTO> deliveryOutcomeDTOs = new ArrayList<>();
        deliveryOutcomeDTOs.add(new DeliveryOutcomeDTO());

        // Act
        String result = deliveryOutcomeService.registerDeliveryOutcome(deliveryOutcomeDTOs);

        // Assert
        assertNotNull(result);
        assertEquals("saved successfully", result);

        verify(deliveryOutcomeRepo, times(1)).save(anyList());
    }

    @Test
    void testGetDeliveryOutcome_Success() {
        // Arrange
        GetBenRequestHandler dto = new GetBenRequestHandler();
        dto.setAshaId(123);
        dto.setFromDate(new Timestamp((new Date()).getTime()));
        dto.setToDate(new Timestamp((new Date()).getTime()));

        List<DeliveryOutcome> deliveryOutcomeList = new ArrayList<>();
        deliveryOutcomeList.add(new DeliveryOutcome());

        when(deliveryOutcomeRepo.getDeliveryOutcomeByBenId(dto.getAshaId(), dto.getFromDate(), dto.getToDate())).thenReturn(deliveryOutcomeList);

        // Act
        List<DeliveryOutcomeDTO> result = deliveryOutcomeService.getDeliveryOutcome(dto);

        // Assert
        assertNotNull(result);
        assertEquals(deliveryOutcomeList.size(), result.size());

        verify(deliveryOutcomeRepo, times(1)).getDeliveryOutcomeByBenId(dto.getAshaId(), dto.getFromDate(), dto.getToDate());
    }
}
