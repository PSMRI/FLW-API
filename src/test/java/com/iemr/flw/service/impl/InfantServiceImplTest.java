package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.InfantRegister;
import com.iemr.flw.dto.iemr.InfantRegisterDTO;
import com.iemr.flw.repo.iemr.InfantRegisterRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InfantServiceImplTest {

    @Mock
    private InfantRegisterRepo infantRegisterRepo;

    @InjectMocks
    private InfantServiceImpl infantService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerInfant_ValidInput_ReturnsSuccessMessage() {
        List<InfantRegisterDTO> infantRegisterDTOs = new ArrayList<>();
        // Add test data to infantRegisterDTOs

        // Mock the repository save method
        when(infantRegisterRepo.save(anyListOf(InfantRegister.class))).thenReturn(new ArrayList<>());

        // Act
        String result = infantService.registerInfant(infantRegisterDTOs);

        // Assert
        assertEquals("saved successfully", result);
    }

    @Test
    void getInfantDetails_ValidBenId_ReturnsInfantRegisterDTO() {
        Long benId = 1L;
        InfantRegister infantRegister = new InfantRegister();
        // Set up the repository getInfantDetailsWithBen method to return infantRegister
        when(infantRegisterRepo.getInfantDetailsWithBen(benId)).thenReturn(infantRegister);

        // Act
        InfantRegisterDTO result = infantService.getInfantDetails(benId);

        // Assert
        assertEquals(infantRegister, objectMapper.convertValue(result, InfantRegister.class));
    }

    @Test
    void getInfantDetails_InvalidBenId_ReturnsNull() {
        Long benId = 999L;

        // Set up the repository getInfantDetailsWithBen method to return null
        when(infantRegisterRepo.getInfantDetailsWithBen(benId)).thenReturn(null);

        // Act
        InfantRegisterDTO result = infantService.getInfantDetails(benId);

        // Assert
        assertEquals(null, result);
    }
}

