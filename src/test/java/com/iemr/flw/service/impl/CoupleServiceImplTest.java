package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.EligibleCoupleRegister;
import com.iemr.flw.domain.iemr.EligibleCoupleTracking;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.repo.iemr.EligibleCoupleRegisterRepo;
import com.iemr.flw.repo.iemr.EligibleCoupleTrackingRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CoupleServiceImplTest {

    @Mock
    private EligibleCoupleRegisterRepo eligibleCoupleRegisterRepo;

    @Mock
    private EligibleCoupleTrackingRepo eligibleCoupleTrackingRepo;

    @InjectMocks
    private CoupleServiceImpl coupleService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerEligibleCouple_ValidInput_ReturnsSuccessMessage() {
        List<EligibleCoupleDTO> eligibleCoupleDTOs = new ArrayList<>();

        // Mock the repository save method
        when(eligibleCoupleRegisterRepo.save(anyListOf(EligibleCoupleRegister.class))).thenReturn(new ArrayList<>());

        // Act
        String result = coupleService.registerEligibleCouple(eligibleCoupleDTOs);

        // Assert
        assertEquals("saved successfully", result);
    }

    @Test
    void registerEligibleCoupleTracking_ValidInput_ReturnsSuccessMessage() {
        List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOs = new ArrayList<>();

        // Mock the repository save method
        when(eligibleCoupleTrackingRepo.save(anyListOf(EligibleCoupleTracking.class))).thenReturn(new ArrayList<>());

        // Act
        String result = coupleService.registerEligibleCoupleTracking(eligibleCoupleTrackingDTOs);

        // Assert
        assertEquals("saved successfully", result);
    }

    @Test
    void getEligibleCouple_ValidBenId_ReturnsEligibleCoupleDTO() {
        Long benId = 1L;
        EligibleCoupleRegister eligibleCoupleRegister = new EligibleCoupleRegister();
        // Set up the repository getECRWithBen method to return eligibleCoupleRegister
        when(eligibleCoupleRegisterRepo.getECRWithBen(benId)).thenReturn(eligibleCoupleRegister);

        // Act
        EligibleCoupleDTO result = coupleService.getEligibleCouple(benId);

        // Assert
        assertEquals(eligibleCoupleRegister, objectMapper.convertValue(result, EligibleCoupleRegister.class));
    }

    @Test
    void getEligibleCouple_InvalidBenId_ReturnsNull() {
        Long benId = 999L;

        // Set up the repository getECRWithBen method to return null
        when(eligibleCoupleRegisterRepo.getECRWithBen(benId)).thenReturn(null);

        // Act
        EligibleCoupleDTO result = coupleService.getEligibleCouple(benId);

        // Assert
        assertEquals(null, result);
    }

    @Test
    void getEligibleCoupleTracking_ValidEcrId_ReturnsEligibleCoupleTrackingDTOList() {
        Long ecrId = 1L;
        List<EligibleCoupleTracking> eligibleCoupleTrackingList = new ArrayList<>();
        // Set up the repository getDetailsForEC method to return eligibleCoupleTrackingList
        when(eligibleCoupleTrackingRepo.getDetailsForEC(ecrId)).thenReturn(eligibleCoupleTrackingList);

        // Act
        List<EligibleCoupleTrackingDTO> result = coupleService.getEligibleCoupleTracking(ecrId);

        // Assert
        assertEquals(eligibleCoupleTrackingList.size(), result.size());
    }

    @Test
    void getEligibleCoupleTracking_InvalidEcrId_ReturnsEmptyList() {
        Long ecrId = 999L;

        // Set up the repository getDetailsForEC method to return an empty list
        when(eligibleCoupleTrackingRepo.getDetailsForEC(ecrId)).thenReturn(new ArrayList<>());

        // Act
        List<EligibleCoupleTrackingDTO> result = coupleService.getEligibleCoupleTracking(ecrId);

        // Assert
        assertEquals(0, result.size());
    }
}


