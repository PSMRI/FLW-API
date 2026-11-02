package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
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
class IncentiveServiceImplTest {
    @Mock
    private IncentivesRepo incentivesRepo;
    @Mock
    private IncentiveRecordRepo recordRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private IncentiveServiceImpl incentiveService;

    @Test
    void saveIncentivesMaster_newActivity_success() {
        IncentiveActivityDTO dto = new IncentiveActivityDTO();
        dto.setName("TestName");
        dto.setGroup("TestGroup");
        List<IncentiveActivityDTO> dtos = Collections.singletonList(dto);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("TestName", "TestGroup")).thenReturn(null);
        doNothing().when(modelMapper).map(any(IncentiveActivityDTO.class), any(IncentiveActivity.class));
        when(incentivesRepo.save(any(IncentiveActivity.class))).thenReturn(new IncentiveActivity());
        String result = incentiveService.saveIncentivesMaster(dtos);
        assertNotNull(result);
        assertTrue(result.contains("saved master data for"));
    }

    @Test
    void saveIncentivesMaster_existingActivity_success() {
        IncentiveActivityDTO dto = new IncentiveActivityDTO();
        dto.setName("TestName2");
        dto.setGroup("TestGroup2");
        List<IncentiveActivityDTO> dtos = Collections.singletonList(dto);
        IncentiveActivity existing = new IncentiveActivity();
        existing.setId(99L);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("TestName2", "TestGroup2")).thenReturn(existing);
        doNothing().when(modelMapper).map(any(IncentiveActivityDTO.class), any(IncentiveActivity.class));
        when(incentivesRepo.save(any(IncentiveActivity.class))).thenReturn(existing);
        String result = incentiveService.saveIncentivesMaster(dtos);
        assertNotNull(result);
        assertTrue(result.contains("saved master data for"));
    }

    @Test
    void saveIncentivesMaster_exception() {
        IncentiveActivityDTO dto = new IncentiveActivityDTO();
        dto.setName("TestName3");
        dto.setGroup("TestGroup3");
        List<IncentiveActivityDTO> dtos = Collections.singletonList(dto);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("TestName3", "TestGroup3")).thenThrow(new RuntimeException("fail"));
        String result = incentiveService.saveIncentivesMaster(dtos);
        assertNull(result);
    }

    @Test
    void getIncentiveMaster_success() {
        IncentiveActivity activity = new IncentiveActivity();
        List<IncentiveActivity> activities = Arrays.asList(activity, activity);
        when(incentivesRepo.findAll()).thenReturn(activities);
        when(modelMapper.map(any(IncentiveActivity.class), eq(IncentiveActivityDTO.class))).thenReturn(new IncentiveActivityDTO());
        String json = incentiveService.getIncentiveMaster(new IncentiveRequestDTO());
        assertNotNull(json);
        IncentiveActivityDTO[] arr = new Gson().fromJson(json, IncentiveActivityDTO[].class);
        assertEquals(2, arr.length);
    }

    @Test
    void getIncentiveMaster_exception() {
        when(incentivesRepo.findAll()).thenThrow(new RuntimeException("fail"));
        String result = incentiveService.getIncentiveMaster(new IncentiveRequestDTO());
        assertNull(result);
    }

    @Test
    void getAllIncentivesByUserId_success() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        List<IncentiveActivityRecord> entities = Arrays.asList(new IncentiveActivityRecord(), new IncentiveActivityRecord());
        when(recordRepo.findRecordsByAsha(ashaId, fromDate, toDate)).thenReturn(entities);
        when(modelMapper.map(any(IncentiveActivityRecord.class), eq(IncentiveRecordDTO.class))).thenReturn(new IncentiveRecordDTO());
        String json = incentiveService.getAllIncentivesByUserId(req);
        assertNotNull(json);
        IncentiveRecordDTO[] arr = new Gson().fromJson(json, IncentiveRecordDTO[].class);
        assertEquals(2, arr.length);
    }

    @Test
    void getAllIncentivesByUserId_emptyList() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        when(recordRepo.findRecordsByAsha(ashaId, fromDate, toDate)).thenReturn(Collections.emptyList());
        String json = incentiveService.getAllIncentivesByUserId(req);
        assertNotNull(json);
        IncentiveRecordDTO[] arr = new Gson().fromJson(json, IncentiveRecordDTO[].class);
        assertEquals(0, arr.length);
    }
}
