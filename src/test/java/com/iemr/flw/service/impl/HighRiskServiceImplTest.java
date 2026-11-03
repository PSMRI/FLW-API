package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.HighRiskAssess;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HighRiskAssessDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;
import com.iemr.flw.repo.iemr.HighRiskAssessRepo;
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
class HighRiskServiceImplTest {
    @Mock
    private HighRiskAssessRepo assessRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private HighRiskServiceImpl highRiskService;

    @Test
    void getAllAssessments_success() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        List<HighRiskAssess> entityList = Arrays.asList(new HighRiskAssess(), new HighRiskAssess());
        when(assessRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(entityList);
        String json = highRiskService.getAllAssessments(req);
        assertNotNull(json);
    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HighRiskAssessDTO>>(){}.getType();
    UserDataDTO<HighRiskAssessDTO> result = new Gson().fromJson(json, type);
    assertEquals(2, result.getEntries().size());
    assertEquals(ashaId, result.getUserId());
    }

    @Test
    void getAllAssessments_emptyList() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        when(assessRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(Collections.emptyList());
        String json = highRiskService.getAllAssessments(req);
        assertNotNull(json);
    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HighRiskAssessDTO>>(){}.getType();
    UserDataDTO<HighRiskAssessDTO> result = new Gson().fromJson(json, type);
    assertEquals(0, result.getEntries().size());
    assertEquals(ashaId, result.getUserId());
    }

    @Test
    void saveAllAssessment_newAssess_success() {
        HighRiskAssessDTO dto = new HighRiskAssessDTO();
        dto.setBenId(1L);
        UserDataDTO<HighRiskAssessDTO> req = new UserDataDTO<>();
        req.setUserId(10);
        req.setEntries(Collections.singletonList(dto));
        HighRiskAssess mapped = new HighRiskAssess();
        mapped.setBenId(1L);
    // No need to stub modelMapper.map(dto, HighRiskAssessDTO.class) as its result is not used
        when(assessRepo.getByUserIdAndBenId(1L, 10)).thenReturn(null);
        when(assessRepo.save(any(HighRiskAssess.class))).thenReturn(new HighRiskAssess());
        String result = highRiskService.saveAllAssessment(req);
        assertTrue(result.contains("no of high risk assessment items saved: 1"));
    }

    @Test
    void saveAllAssessment_existingAssess_success() {
        HighRiskAssessDTO dto = new HighRiskAssessDTO();
        dto.setBenId(2L);
        UserDataDTO<HighRiskAssessDTO> req = new UserDataDTO<>();
        req.setUserId(20);
        req.setEntries(Collections.singletonList(dto));
        HighRiskAssess mapped = new HighRiskAssess();
        mapped.setBenId(2L);
        HighRiskAssess existing = new HighRiskAssess();
        existing.setId(99L);
    // No need to stub modelMapper.map(dto, HighRiskAssessDTO.class) as its result is not used
        when(assessRepo.getByUserIdAndBenId(2L, 20)).thenReturn(existing);
        when(assessRepo.save(any(HighRiskAssess.class))).thenReturn(existing);
        String result = highRiskService.saveAllAssessment(req);
        assertTrue(result.contains("no of high risk assessment items saved: 1"));
    }

    @Test
    void saveAllAssessment_exception() {
        HighRiskAssessDTO dto = new HighRiskAssessDTO();
        dto.setBenId(3L);
        UserDataDTO<HighRiskAssessDTO> req = new UserDataDTO<>();
        req.setUserId(30);
        req.setEntries(Collections.singletonList(dto));
    // No need to stub modelMapper.map(dto, HighRiskAssessDTO.class) as its result is not used
        when(assessRepo.getByUserIdAndBenId(3L, 30)).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> highRiskService.saveAllAssessment(req));
    }
}
