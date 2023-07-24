package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.ANCVisit;
import com.iemr.flw.domain.iemr.PregnantWomanRegister;
import com.iemr.flw.dto.iemr.ANCVisitDTO;
import com.iemr.flw.dto.iemr.PregnantWomanDTO;
import com.iemr.flw.repo.iemr.ANCVisitRepo;
import com.iemr.flw.repo.iemr.PregnantWomanRegisterRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PregnantWomanServiceImplTest {

    @Mock
    private PregnantWomanRegisterRepo pregnantWomanRegisterRepo;

    @Mock
    private ANCVisitRepo ancVisitRepo;

    @InjectMocks
    private PregnantWomanServiceImpl pregnantWomanService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerPregnantWoman_ValidInput_ReturnsSuccessMessage() {
        List<PregnantWomanDTO> pregnantWomanDTOs = new ArrayList<>();
        // Add test data to pregnantWomanDTOs

        // Mock the repository save method
        when(pregnantWomanRegisterRepo.save(anyListOf(PregnantWomanRegister.class))).thenReturn(new ArrayList<>());

        // Act
        String result = pregnantWomanService.registerPregnantWoman(pregnantWomanDTOs);

        // Assert
        assertEquals("saved successfully", result);
    }

    @Test
    void getPregnantWoman_ValidBenId_ReturnsPregnantWomanDTO() {
        Long benId = 1L;
        PregnantWomanRegister pregnantWomanRegister = new PregnantWomanRegister();
        // Set up the repository getPWRWithBen method to return pregnantWomanRegister
        when(pregnantWomanRegisterRepo.getPWRWithBen(benId)).thenReturn(pregnantWomanRegister);

        // Act
        PregnantWomanDTO result = pregnantWomanService.getPregnantWoman(benId);

        // Assert
        assertEquals(pregnantWomanRegister, objectMapper.convertValue(result, PregnantWomanRegister.class));
    }

    @Test
    void getANCVisits_ValidPwrId_ReturnsANCVisitDTOList() {
        Long pwrId = 1L;
        List<ANCVisit> ancVisits = new ArrayList<>();
        // Set up the repository getANCForPW method to return ancVisits
        when(ancVisitRepo.getANCForPW(pwrId)).thenReturn(ancVisits);

        // Act
        List<ANCVisitDTO> result = pregnantWomanService.getANCVisits(pwrId);

        // Assert
        assertEquals(ancVisits.stream()
                .map(anc -> objectMapper.convertValue(anc, ANCVisitDTO.class))
                .collect(Collectors.toList()), result);
    }

    @Test
    void saveANCVisit_ValidInput_ReturnsSuccessMessage() {
        List<ANCVisitDTO> ancVisitDTOs = new ArrayList<>();
        // Add test data to ancVisitDTOs

        // Mock the repository save method
        when(ancVisitRepo.save(anyListOf(ANCVisit.class))).thenReturn(new ArrayList<>());

        // Act
        String result = pregnantWomanService.saveANCVisit(ancVisitDTOs);

        // Assert
        assertEquals("saved successfully", result);
    }
}

