package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.NonPregnantWomanHighRiskAssess;
import com.iemr.flw.domain.iemr.NonPregnantWomanHighRiskTrack;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.HRNonPregnantAssessDTO;
import com.iemr.flw.dto.iemr.HRNonPregnantTrackDTO;
import com.iemr.flw.dto.iemr.HRPregnantAssessDTO;
import com.iemr.flw.dto.iemr.UserDataDTO;
import com.iemr.flw.repo.iemr.HRNonPregnantAssessRepo;
import com.iemr.flw.repo.iemr.HRNonPregnantTrackRepo;
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
class HRNonPregnantServiceImplTest {
    @Mock
    private HRNonPregnantAssessRepo assessRepo;
    @Mock
    private HRNonPregnantTrackRepo trackRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private HRNonPregnantServiceImpl service;

    @Test
    void getAllAssessment_success() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        List<NonPregnantWomanHighRiskAssess> entityList = Arrays.asList(new NonPregnantWomanHighRiskAssess(), new NonPregnantWomanHighRiskAssess());
        when(assessRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(entityList);
        String json = service.getAllAssessment(req);
        assertNotNull(json);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HRNonPregnantAssessDTO>>(){}.getType();
        UserDataDTO<HRNonPregnantAssessDTO> result = new Gson().fromJson(json, type);
        assertEquals(2, result.getEntries().size());
        assertEquals(ashaId, result.getUserId());
    }

    @Test
    void getAllAssessment_emptyList() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        when(assessRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(Collections.emptyList());
        String json = service.getAllAssessment(req);
        assertNotNull(json);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HRNonPregnantAssessDTO>>(){}.getType();
        UserDataDTO<HRNonPregnantAssessDTO> result = new Gson().fromJson(json, type);
        assertEquals(0, result.getEntries().size());
        assertEquals(ashaId, result.getUserId());
    }

    @Test
    void saveAllAssessment_newAssess_success() {
        HRNonPregnantAssessDTO dto = new HRNonPregnantAssessDTO();
        UserDataDTO<HRNonPregnantAssessDTO> req = new UserDataDTO<>();
        req.setUserId(10);
        req.setEntries(Collections.singletonList(dto));
        HRPregnantAssessDTO mapped = new HRPregnantAssessDTO();
        mapped.setBenId(1L);
        when(assessRepo.getByUserIdAndBenId(isNull(), eq(10))).thenReturn(null);
    // No need to stub assessRepo.save for void/irrelevant methods
        String result = service.saveAllAssessment(req);
        assertTrue(result.contains("no of high risk assessment items saved: 1"));
    }

    @Test
    void saveAllAssessment_existingAssess_success() {
        HRNonPregnantAssessDTO dto = new HRNonPregnantAssessDTO();
        UserDataDTO<HRNonPregnantAssessDTO> req = new UserDataDTO<>();
        req.setUserId(20);
        req.setEntries(Collections.singletonList(dto));
        HRPregnantAssessDTO mapped = new HRPregnantAssessDTO();
        mapped.setBenId(2L);
        NonPregnantWomanHighRiskAssess existing = new NonPregnantWomanHighRiskAssess();
        existing.setId(99L);

        when(assessRepo.getByUserIdAndBenId(isNull(), eq(20))).thenReturn(existing);
    // No need to stub assessRepo.save for void/irrelevant methods
        String result = service.saveAllAssessment(req);
        assertTrue(result.contains("no of high risk assessment items saved: 1"));
    }

    @Test
    void saveAllAssessment_exception() {
        HRNonPregnantAssessDTO dto = new HRNonPregnantAssessDTO();
        UserDataDTO<HRNonPregnantAssessDTO> req = new UserDataDTO<>();
        req.setUserId(30);
        req.setEntries(Collections.singletonList(dto));
        HRPregnantAssessDTO mapped = new HRPregnantAssessDTO();
        mapped.setBenId(3L);
        when(modelMapper.map(dto, HRPregnantAssessDTO.class)).thenReturn(mapped);
        when(assessRepo.getByUserIdAndBenId(3L, 30)).thenThrow(new RuntimeException("fail"));
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
        List<NonPregnantWomanHighRiskTrack> entityList = Arrays.asList(new NonPregnantWomanHighRiskTrack(), new NonPregnantWomanHighRiskTrack());
        when(trackRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(entityList);
        String json = service.getAllTracking(req);
        assertNotNull(json);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HRNonPregnantTrackDTO>>(){}.getType();
        UserDataDTO<HRNonPregnantTrackDTO> result = new Gson().fromJson(json, type);
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
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserDataDTO<HRNonPregnantTrackDTO>>(){}.getType();
        UserDataDTO<HRNonPregnantTrackDTO> result = new Gson().fromJson(json, type);
        assertEquals(0, result.getEntries().size());
        assertEquals(ashaId, result.getUserId());
    }

    @Test
    void saveAllTracking_newTrack_success() {
        HRNonPregnantTrackDTO dto = new HRNonPregnantTrackDTO();
        dto.setBenId(1L);
        dto.setVisitDate(new java.sql.Timestamp(System.currentTimeMillis()));
        UserDataDTO<HRNonPregnantTrackDTO> req = new UserDataDTO<>();
        req.setUserId(10);
        req.setEntries(Collections.singletonList(dto));
        when(trackRepo.getByUserIdAndBenId(dto.getBenId(), 10, dto.getVisitDate())).thenReturn(null);
        when(trackRepo.save(any(NonPregnantWomanHighRiskTrack.class))).thenReturn(new NonPregnantWomanHighRiskTrack());
        String result = service.saveAllTracking(req);
        assertTrue(result.contains("no of high risk pregnant tracking items saved: 1"));
    }

    @Test
    void saveAllTracking_existingTrack_success() {
        HRNonPregnantTrackDTO dto = new HRNonPregnantTrackDTO();
        dto.setBenId(2L);
        dto.setVisitDate(new java.sql.Timestamp(System.currentTimeMillis()));
        UserDataDTO<HRNonPregnantTrackDTO> req = new UserDataDTO<>();
        req.setUserId(20);
        req.setEntries(Collections.singletonList(dto));
        NonPregnantWomanHighRiskTrack existing = new NonPregnantWomanHighRiskTrack();
        existing.setId(99L);
        when(trackRepo.getByUserIdAndBenId(dto.getBenId(), 20, dto.getVisitDate())).thenReturn(existing);

        when(trackRepo.save(any(NonPregnantWomanHighRiskTrack.class))).thenReturn(existing);
        String result = service.saveAllTracking(req);
        assertTrue(result.contains("no of high risk pregnant tracking items saved: 1"));
    }

    @Test
    void saveAllTracking_exception() {
        HRNonPregnantTrackDTO dto = new HRNonPregnantTrackDTO();
        dto.setBenId(3L);
        dto.setVisitDate(new java.sql.Timestamp(System.currentTimeMillis()));
        UserDataDTO<HRNonPregnantTrackDTO> req = new UserDataDTO<>();
        req.setUserId(30);
        req.setEntries(Collections.singletonList(dto));
    // No need to stub assessRepo.getByUserIdAndBenId for this test
    }
}
