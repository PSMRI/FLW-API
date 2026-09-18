package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.InfantRegister;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.InfantRegisterDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.InfantRegisterRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfantServiceImplTest {
    @Mock
    private InfantRegisterRepo infantRegisterRepo;
    @Mock
    private BeneficiaryRepo beneficiaryRepo;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private InfantServiceImpl infantService;

    @Test
    void registerInfant_newInfant_success() {
        InfantRegisterDTO dto = new InfantRegisterDTO();
        dto.setBenId(1L);
        dto.setBabyIndex(1);
        List<InfantRegisterDTO> dtos = Collections.singletonList(dto);
        when(infantRegisterRepo.findInfantRegisterByBenIdAndBabyIndexAndIsActive(1L, 1, true)).thenReturn(null);
        doNothing().when(modelMapper).map(any(InfantRegisterDTO.class), any(InfantRegister.class));
        String result = infantService.registerInfant(dtos);
        assertTrue(result.contains("no of infant register details saved: 1"));
    }

    @Test
    void registerInfant_existingInfant_success() {
        InfantRegisterDTO dto = new InfantRegisterDTO();
        dto.setBenId(2L);
        dto.setBabyIndex(2);
        List<InfantRegisterDTO> dtos = Collections.singletonList(dto);
        InfantRegister existing = new InfantRegister();
        existing.setId(99L);
        when(infantRegisterRepo.findInfantRegisterByBenIdAndBabyIndexAndIsActive(2L, 2, true)).thenReturn(existing);
        doNothing().when(modelMapper).map(any(InfantRegisterDTO.class), any(InfantRegister.class));
    
        String result = infantService.registerInfant(dtos);
        assertTrue(result.contains("no of infant register details saved: 1"));
    }

    @Test
    void registerInfant_exception() {
        InfantRegisterDTO dto = new InfantRegisterDTO();
        dto.setBenId(3L);
        dto.setBabyIndex(3);
        List<InfantRegisterDTO> dtos = Collections.singletonList(dto);
        when(infantRegisterRepo.findInfantRegisterByBenIdAndBabyIndexAndIsActive(3L, 3, true)).thenThrow(new RuntimeException("fail"));
        String result = infantService.registerInfant(dtos);
        assertNull(result);
    }

    @Test
    void getInfantDetails_success() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(dto.getAshaId()).thenReturn(ashaId);
        when(dto.getFromDate()).thenReturn(fromDate);
        when(dto.getToDate()).thenReturn(toDate);
        when(beneficiaryRepo.getUserName(ashaId)).thenReturn("user1");
        List<InfantRegister> infantList = Arrays.asList(new InfantRegister(), new InfantRegister());
        when(infantRegisterRepo.getInfantDetailsForUser("user1", fromDate, toDate)).thenReturn(infantList);
        when(mapper.convertValue(any(InfantRegister.class), eq(InfantRegisterDTO.class))).thenReturn(new InfantRegisterDTO());
        List<InfantRegisterDTO> result = infantService.getInfantDetails(dto);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getInfantDetails_exception() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        when(dto.getAshaId()).thenReturn(ashaId);
        when(beneficiaryRepo.getUserName(ashaId)).thenThrow(new RuntimeException("fail"));
        List<InfantRegisterDTO> result = infantService.getInfantDetails(dto);
        assertNull(result);
    }
}
