package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DeliveryOutcome;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.DeliveryOutcomeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DeliveryOutcomeServiceImplTest {
    @Mock
    private DeliveryOutcomeRepo deliveryOutcomeRepo;
    @Mock
    private BeneficiaryRepo beneficiaryRepo;

    @InjectMocks
    private DeliveryOutcomeServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerDeliveryOutcome_newAndExisting() {
        DeliveryOutcomeDTO dto1 = new DeliveryOutcomeDTO();
        dto1.setBenId(1L);
        DeliveryOutcomeDTO dto2 = new DeliveryOutcomeDTO();
        dto2.setBenId(2L);
        List<DeliveryOutcomeDTO> dtos = Arrays.asList(dto1, dto2);
        DeliveryOutcome existing = new DeliveryOutcome();
        existing.setId(99L);
        when(deliveryOutcomeRepo.findDeliveryOutcomeByBenIdAndIsActive(1L, true)).thenReturn(null);
        when(deliveryOutcomeRepo.findDeliveryOutcomeByBenIdAndIsActive(2L, true)).thenReturn(existing);
        String result = service.registerDeliveryOutcome(dtos);
        assertTrue(result.contains("no of delivery outcome details saved: 2"));
        verify(deliveryOutcomeRepo).saveAll(anyList());
    }

    @Test
    void registerDeliveryOutcome_exception() {
        DeliveryOutcomeDTO dto = new DeliveryOutcomeDTO();
        dto.setBenId(1L);
        List<DeliveryOutcomeDTO> dtos = Collections.singletonList(dto);
        when(deliveryOutcomeRepo.findDeliveryOutcomeByBenIdAndIsActive(1L, true)).thenReturn(null);
        when(deliveryOutcomeRepo.saveAll(anyList())).thenThrow(new RuntimeException("Database error"));
        String result = service.registerDeliveryOutcome(dtos);
        assertTrue(result.startsWith("error while saving delivery outcome details:"));
    }

    @Test
    void getDeliveryOutcome_success() {
        GetBenRequestHandler req = new GetBenRequestHandler();
        req.setAshaId(123);
        Timestamp from = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp to = Timestamp.valueOf("2023-12-31 00:00:00");
        req.setFromDate(from);
        req.setToDate(to);
        when(beneficiaryRepo.getUserName(123)).thenReturn("user");
        DeliveryOutcome outcome = new DeliveryOutcome();
        when(deliveryOutcomeRepo.getDeliveryOutcomeByAshaId("user", from, to)).thenReturn(Collections.singletonList(outcome));
        List<DeliveryOutcomeDTO> result = service.getDeliveryOutcome(req);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getDeliveryOutcome_exception() {
        GetBenRequestHandler req = new GetBenRequestHandler();
        req.setAshaId(123);
        Timestamp from = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp to = Timestamp.valueOf("2023-12-31 00:00:00");
        req.setFromDate(from);
        req.setToDate(to);
        when(beneficiaryRepo.getUserName(123)).thenReturn("user");
        when(deliveryOutcomeRepo.getDeliveryOutcomeByAshaId(anyString(), any(Timestamp.class), any(Timestamp.class))).thenThrow(new RuntimeException("fail"));
        List<DeliveryOutcomeDTO> result = service.getDeliveryOutcome(req);
        assertNull(result);
    }
}
