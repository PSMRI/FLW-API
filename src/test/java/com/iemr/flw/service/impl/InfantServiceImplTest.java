package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.InfantRegister;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.InfantRegisterDTO;
import com.iemr.flw.repo.iemr.InfantRegisterRepo;
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

class InfantServiceImplTest {

    @Mock
    private InfantRegisterRepo infantRegisterRepo;

    @InjectMocks
    private InfantServiceImpl infantService;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    void testRegisterInfant_Success() {
//        // Arrange
//        List<InfantRegisterDTO> infantRegisterDTOs = new ArrayList<>();
//        infantRegisterDTOs.add(new InfantRegisterDTO());
//
//        // Act
//        String result = infantService.registerInfant(infantRegisterDTOs);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("saved successfully", result);
//
//        verify(infantRegisterRepo, times(1)).save(anyList());
//    }
//
//    @Test
//    void testGetInfantDetails_Success() {
//        // Arrange
//        GetBenRequestHandler dto = new GetBenRequestHandler();
//        dto.setAshaId(123);
//        dto.setFromDate(new Timestamp((new Date()).getTime()));
//        dto.setToDate(new Timestamp((new Date()).getTime()));
//
//        List<InfantRegister> infantRegisterList = new ArrayList<>();
//        infantRegisterList.add(new InfantRegister());
//
//        when(infantRegisterRepo.getInfantDetailsForUser(dto.getAshaId(), dto.getFromDate(), dto.getToDate())).thenReturn(infantRegisterList);
//
//        // Act
//        List<InfantRegisterDTO> result = infantService.getInfantDetails(dto);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(infantRegisterList.size(), result.size());
//
//        verify(infantRegisterRepo, times(1)).getInfantDetailsForUser(dto.getAshaId(), dto.getFromDate(), dto.getToDate());
//    }
}
