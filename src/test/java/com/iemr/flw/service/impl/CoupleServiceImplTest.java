package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.EligibleCoupleRegister;
import com.iemr.flw.domain.iemr.EligibleCoupleTracking;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.repo.iemr.EligibleCoupleRegisterRepo;
import com.iemr.flw.repo.iemr.EligibleCoupleTrackingRepo;
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

class CoupleServiceImplTest {

    @Mock
    private EligibleCoupleRegisterRepo eligibleCoupleRegisterRepo;

    @Mock
    private EligibleCoupleTrackingRepo eligibleCoupleTrackingRepo;

    @InjectMocks
    private CoupleServiceImpl coupleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRegisterEligibleCouple_Success() {
        // Arrange
        List<EligibleCoupleDTO> eligibleCoupleDTOs = new ArrayList<>();
        eligibleCoupleDTOs.add(new EligibleCoupleDTO());

        EligibleCoupleRegister existingECR = new EligibleCoupleRegister();
        when(eligibleCoupleRegisterRepo.getECRWithBen(anyLong())).thenReturn(existingECR);

        // Act
        String result = coupleService.registerEligibleCouple(eligibleCoupleDTOs);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("already exists"));

        verify(eligibleCoupleRegisterRepo, times(1)).getECRWithBen(anyLong());
        verify(eligibleCoupleRegisterRepo, never()).save(anyList());
    }

    @Test
    void testRegisterEligibleCouple_Success_NewECR() {
        // Arrange
        List<EligibleCoupleDTO> eligibleCoupleDTOs = new ArrayList<>();
        eligibleCoupleDTOs.add(new EligibleCoupleDTO());

        when(eligibleCoupleRegisterRepo.getECRWithBen(anyLong())).thenReturn(null);

        // Act
        String result = coupleService.registerEligibleCouple(eligibleCoupleDTOs);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Eligible Couple Register Details Saved!"));

        verify(eligibleCoupleRegisterRepo, times(1)).getECRWithBen(anyLong());
        verify(eligibleCoupleRegisterRepo, times(1)).save(anyList());
    }

    @Test
    void testRegisterEligibleCoupleTracking_Success() {
        // Arrange
        List<EligibleCoupleTrackingDTO> eligibleCoupleTrackingDTOs = new ArrayList<>();
        eligibleCoupleTrackingDTOs.add(new EligibleCoupleTrackingDTO());

        // Act
        String result = coupleService.registerEligibleCoupleTracking(eligibleCoupleTrackingDTOs);

        // Assert
        assertNotNull(result);
        assertEquals("saved successfully", result);

        verify(eligibleCoupleTrackingRepo, times(1)).save(anyList());
    }

    @Test
    void testGetEligibleCoupleRegRecords_Success() {
        // Arrange
        GetBenRequestHandler dto = new GetBenRequestHandler();
        dto.setAshaId(123);
        dto.setFromDate(new Timestamp((new Date()).getTime()));
        dto.setToDate(new Timestamp((new Date()).getTime()));

        List<EligibleCoupleRegister> eligibleCoupleRegisterList = new ArrayList<>();
        eligibleCoupleRegisterList.add(new EligibleCoupleRegister());

        when(eligibleCoupleRegisterRepo.getECRegRecords(dto.getAshaId(), dto.getFromDate(), dto.getToDate())).thenReturn(eligibleCoupleRegisterList);

        // Act
        List<EligibleCoupleDTO> result = coupleService.getEligibleCoupleRegRecords(dto);

        // Assert
        assertNotNull(result);
        assertEquals(eligibleCoupleRegisterList.size(), result.size());

        verify(eligibleCoupleRegisterRepo, times(1)).getECRegRecords(dto.getAshaId(), dto.getFromDate(), dto.getToDate());
    }

    @Test
    void testGetEligibleCoupleTracking_Success() {
        // Arrange
        GetBenRequestHandler dto = new GetBenRequestHandler();
        dto.setAshaId(123);
        dto.setFromDate(new Timestamp((new Date()).getTime()));
        dto.setToDate(new Timestamp((new Date()).getTime()));

        List<EligibleCoupleTracking> eligibleCoupleTrackingList = new ArrayList<>();
        eligibleCoupleTrackingList.add(new EligibleCoupleTracking());

        when(eligibleCoupleTrackingRepo.getECTrackRecords(dto.getAshaId(), dto.getFromDate(), dto.getToDate())).thenReturn(eligibleCoupleTrackingList);

        // Act
        List<EligibleCoupleTrackingDTO> result = coupleService.getEligibleCoupleTracking(dto);

        // Assert
        assertNotNull(result);
        assertEquals(eligibleCoupleTrackingList.size(), result.size());

        verify(eligibleCoupleTrackingRepo, times(1)).getECTrackRecords(dto.getAshaId(), dto.getFromDate(), dto.getToDate());
    }
}


