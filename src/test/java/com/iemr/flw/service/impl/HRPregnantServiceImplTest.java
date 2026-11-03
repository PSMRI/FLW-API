package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.PregnantWomanHighRiskAssess;
import com.iemr.flw.domain.iemr.PregnantWomanHighRiskTrack;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HRPregnantAssessDTO;
import com.iemr.flw.dto.iemr.HRPregnantTrackDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;
import com.iemr.flw.repo.iemr.HRPregnantAssessRepo;
import com.iemr.flw.repo.iemr.HRPregnantTrackRepo;
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
class HRPregnantServiceImplTest {
    @Mock
    private HRPregnantAssessRepo assessRepo;
    @Mock
    private HRPregnantTrackRepo trackRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private HRPregnantServiceImpl service;

    @Test
    void getAllAssessments_success() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        List<PregnantWomanHighRiskAssess> entityList = Arrays.asList(new PregnantWomanHighRiskAssess(), new PregnantWomanHighRiskAssess());
        when(assessRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(entityList);
        String json = service.getAllAssessments(req);
        assertNotNull(json);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HRPregnantAssessDTO>>(){}.getType();
        UserDataDTO<HRPregnantAssessDTO> result = new Gson().fromJson(json, type);
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
        String json = service.getAllAssessments(req);
        assertNotNull(json);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HRPregnantAssessDTO>>(){}.getType();
        UserDataDTO<HRPregnantAssessDTO> result = new Gson().fromJson(json, type);
        assertEquals(0, result.getEntries().size());
        assertEquals(ashaId, result.getUserId());
    }

    @Test
    void saveAllAssessment_newAssess_success() {
        HRPregnantAssessDTO dto = new HRPregnantAssessDTO();
        UserDataDTO<HRPregnantAssessDTO> req = new UserDataDTO<>();
        req.setUserId(10);
        req.setEntries(Collections.singletonList(dto));
        HRPregnantAssessDTO mapped = new HRPregnantAssessDTO();
        mapped.setBenId(1L);
        when(assessRepo.getByUserIdAndBenId(isNull(), eq(10))).thenReturn(null);
        String result = service.saveAllAssessment(req);
        assertTrue(result.contains("no of high risk assessment items saved: 1"));
    }

    @Test
    void saveAllAssessment_existingAssess_success() {
        HRPregnantAssessDTO dto = new HRPregnantAssessDTO();
        UserDataDTO<HRPregnantAssessDTO> req = new UserDataDTO<>();
        req.setUserId(20);
        req.setEntries(Collections.singletonList(dto));
        HRPregnantAssessDTO mapped = new HRPregnantAssessDTO();
        mapped.setBenId(2L);
        PregnantWomanHighRiskAssess existing = new PregnantWomanHighRiskAssess();
        existing.setId(99L);
        when(assessRepo.getByUserIdAndBenId(isNull(), eq(20))).thenReturn(existing);
        String result = service.saveAllAssessment(req);
        assertTrue(result.contains("no of high risk assessment items saved: 1"));
    }

    @Test
    void saveAllAssessment_exception() {
        HRPregnantAssessDTO dto = new HRPregnantAssessDTO();
        UserDataDTO<HRPregnantAssessDTO> req = new UserDataDTO<>();
        req.setUserId(30);
        req.setEntries(Collections.singletonList(dto));
        HRPregnantAssessDTO mapped = new HRPregnantAssessDTO();
        mapped.setBenId(3L);
        when(assessRepo.getByUserIdAndBenId(isNull(), eq(30))).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> service.saveAllAssessment(req));
    }

    @Test
    void getAllTracking_success() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        List<PregnantWomanHighRiskTrack> entityList = Arrays.asList(new PregnantWomanHighRiskTrack(), new PregnantWomanHighRiskTrack());
        when(trackRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(entityList);
        String json = service.getAllTracking(req);
        assertNotNull(json);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HRPregnantTrackDTO>>(){}.getType();
        UserDataDTO<HRPregnantTrackDTO> result = new Gson().fromJson(json, type);
        assertEquals(2, result.getEntries().size());
        assertEquals(ashaId, result.getUserId());
    }

    @Test
    void getAllTracking_emptyList() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        when(trackRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(Collections.emptyList());
        String json = service.getAllTracking(req);
        assertNotNull(json);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HRPregnantTrackDTO>>(){}.getType();
        UserDataDTO<HRPregnantTrackDTO> result = new Gson().fromJson(json, type);
        assertEquals(0, result.getEntries().size());
        assertEquals(ashaId, result.getUserId());
    }

    @Test
    void saveAllTracking_newTrack_success() {
        HRPregnantTrackDTO dto = new HRPregnantTrackDTO();
        dto.setBenId(1L);
    dto.setVisit("2023-08-16");
        UserDataDTO<HRPregnantTrackDTO> req = new UserDataDTO<>();
        req.setUserId(10);
        req.setEntries(Collections.singletonList(dto));
        when(trackRepo.getByUserIdAndBenId(dto.getBenId(), 10, dto.getVisit())).thenReturn(null);
        String result = service.saveAllTracking(req);
        assertTrue(result.contains("no of high risk pregnant tracking items saved: 1"));
    }

    @Test
    void saveAllTracking_existingTrack_success() {
        HRPregnantTrackDTO dto = new HRPregnantTrackDTO();
        dto.setBenId(2L);
    dto.setVisit("2023-08-16");
        UserDataDTO<HRPregnantTrackDTO> req = new UserDataDTO<>();
        req.setUserId(20);
        req.setEntries(Collections.singletonList(dto));
        PregnantWomanHighRiskTrack existing = new PregnantWomanHighRiskTrack();
        existing.setId(99L);
        when(trackRepo.getByUserIdAndBenId(dto.getBenId(), 20, dto.getVisit())).thenReturn(existing);
        String result = service.saveAllTracking(req);
        assertTrue(result.contains("no of high risk pregnant tracking items saved: 1"));
    }

    @Test
    void saveAllTracking_exception() {
        HRPregnantTrackDTO dto = new HRPregnantTrackDTO();
        dto.setBenId(3L);
    dto.setVisit("2023-08-16");
        UserDataDTO<HRPregnantTrackDTO> req = new UserDataDTO<>();
        req.setUserId(30);
        req.setEntries(Collections.singletonList(dto));
        when(trackRepo.getByUserIdAndBenId(dto.getBenId(), 30, dto.getVisit())).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> service.saveAllTracking(req));
    }
}
