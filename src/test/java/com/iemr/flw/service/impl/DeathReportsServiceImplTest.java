package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.CDR;
import com.iemr.flw.domain.iemr.MDSR;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.CdrDTO;
import com.iemr.flw.dto.iemr.MdsrDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.CdrRepo;
import com.iemr.flw.repo.iemr.MdsrRepo;
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
class DeathReportsServiceImplTest {
    @Mock
    private CdrRepo cdrRepo;
    @Mock
    private MdsrRepo mdsrRepo;
    @Mock
    private BeneficiaryRepo beneficiaryRepo;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private DeathReportsServiceImpl deathReportsService;

    @Test
    void registerCDR_new_success() {
        CdrDTO dto = new CdrDTO();
        dto.setBenId(1L);
        List<CdrDTO> dtos = Collections.singletonList(dto);
        when(cdrRepo.findCDRByBenId(1L)).thenReturn(null);
        doNothing().when(modelMapper).map(any(CdrDTO.class), any(CDR.class));
        String result = deathReportsService.registerCDR(dtos);
        assertTrue(result.contains("no of cdr details saved: 1"));
    }

    @Test
    void registerCDR_existing_success() {
        CdrDTO dto = new CdrDTO();
        dto.setBenId(2L);
        List<CdrDTO> dtos = Collections.singletonList(dto);
        CDR existing = new CDR();
        existing.setId(99L);
        when(cdrRepo.findCDRByBenId(2L)).thenReturn(existing);
        doNothing().when(modelMapper).map(any(CdrDTO.class), any(CDR.class));
        String result = deathReportsService.registerCDR(dtos);
        assertTrue(result.contains("no of cdr details saved: 1"));
    }

    @Test
    void registerCDR_exception() {
        CdrDTO dto = new CdrDTO();
        dto.setBenId(3L);
        List<CdrDTO> dtos = Collections.singletonList(dto);
        when(cdrRepo.findCDRByBenId(3L)).thenThrow(new RuntimeException("fail"));
        String result = deathReportsService.registerCDR(dtos);
        assertTrue(result.startsWith("error while saving cdr details:"));
    }

    @Test
    void registerMDSR_new_success() {
        MdsrDTO dto = new MdsrDTO();
        dto.setBenId(1L);
        List<MdsrDTO> dtos = Collections.singletonList(dto);
        when(mdsrRepo.findMDSRByBenId(1L)).thenReturn(null);
        doNothing().when(modelMapper).map(any(MdsrDTO.class), any(MDSR.class));
        String result = deathReportsService.registerMDSR(dtos);
        assertTrue(result.contains("no of mdsr details saved: 1"));
    }

    @Test
    void registerMDSR_existing_success() {
        MdsrDTO dto = new MdsrDTO();
        dto.setBenId(2L);
        List<MdsrDTO> dtos = Collections.singletonList(dto);
        MDSR existing = new MDSR();
        existing.setId(99L);
        when(mdsrRepo.findMDSRByBenId(2L)).thenReturn(existing);
        doNothing().when(modelMapper).map(any(MdsrDTO.class), any(MDSR.class));
        String result = deathReportsService.registerMDSR(dtos);
        assertTrue(result.contains("no of mdsr details saved: 1"));
    }

    @Test
    void registerMDSR_exception() {
        MdsrDTO dto = new MdsrDTO();
        dto.setBenId(3L);
        List<MdsrDTO> dtos = Collections.singletonList(dto);
        when(mdsrRepo.findMDSRByBenId(3L)).thenThrow(new RuntimeException("fail"));
        String result = deathReportsService.registerMDSR(dtos);
        assertTrue(result.startsWith("error while saving mdsr details:"));
    }

    @Test
    void getCdrRecords_success() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(dto.getAshaId()).thenReturn(ashaId);
        when(dto.getFromDate()).thenReturn(fromDate);
        when(dto.getToDate()).thenReturn(toDate);
        when(beneficiaryRepo.getUserName(ashaId)).thenReturn("user1");
        List<CDR> cdrList = Arrays.asList(new CDR(), new CDR());
        when(cdrRepo.getAllCdrByBenId("user1", fromDate, toDate)).thenReturn(cdrList);
        when(mapper.convertValue(any(CDR.class), eq(CdrDTO.class))).thenReturn(new CdrDTO());
        List<CdrDTO> result = deathReportsService.getCdrRecords(dto);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getCdrRecords_exception() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        when(dto.getAshaId()).thenReturn(ashaId);
        when(beneficiaryRepo.getUserName(ashaId)).thenThrow(new RuntimeException("fail"));
        List<CdrDTO> result = deathReportsService.getCdrRecords(dto);
        assertNull(result);
    }

    @Test
    void getMdsrRecords_success() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(dto.getAshaId()).thenReturn(ashaId);
        when(dto.getFromDate()).thenReturn(fromDate);
        when(dto.getToDate()).thenReturn(toDate);
        when(beneficiaryRepo.getUserName(ashaId)).thenReturn("user1");
        List<MDSR> mdsrList = Arrays.asList(new MDSR(), new MDSR());
        when(mdsrRepo.getAllMdsrByAshaId("user1", fromDate, toDate)).thenReturn(mdsrList);
        when(mapper.convertValue(any(MDSR.class), eq(MdsrDTO.class))).thenReturn(new MdsrDTO());
        List<MdsrDTO> result = deathReportsService.getMdsrRecords(dto);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getMdsrRecords_exception() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        when(dto.getAshaId()).thenReturn(ashaId);
        when(beneficiaryRepo.getUserName(ashaId)).thenThrow(new RuntimeException("fail"));
        List<MdsrDTO> result = deathReportsService.getMdsrRecords(dto);
        assertNull(result);
    }
}
