package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.TBScreening;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBScreeningDTO;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.repo.iemr.TBScreeningRepo;
import org.junit.jupiter.api.BeforeEach;
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
class TBScreeningServiceImplTest {
    @Mock
    private TBScreeningRepo tbScreeningRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private TBScreeningServiceImpl tbScreeningService;

    @BeforeEach
    void setUp() {
        // inject mock modelMapper
        // Reflection is not needed as modelMapper is final, but we can set via constructor if needed
    }

    @Test
    void getByBenId_returnsNull() throws Exception {
        String result = tbScreeningService.getByBenId(1L, "auth");
        assertNull(result);
    }

    @Test
    void save_newTBScreening_success() throws Exception {
        TBScreeningDTO dto = new TBScreeningDTO();
        dto.setBenId(1L);
        TBScreeningRequestDTO req = new TBScreeningRequestDTO();
        req.setUserId(10);
        req.setTbScreeningList(Collections.singletonList(dto));
        when(tbScreeningRepo.getByUserIdAndBenId(1L, 10)).thenReturn(null);
        when(tbScreeningRepo.save(any(TBScreening.class))).thenReturn(new TBScreening());
        String result = tbScreeningService.save(req);
        assertTrue(result.contains("no of tb screening items saved: 1"));
    }

    @Test
    void save_existingTBScreening_success() throws Exception {
        TBScreeningDTO dto = new TBScreeningDTO();
        dto.setBenId(2L);
        TBScreeningRequestDTO req = new TBScreeningRequestDTO();
        req.setUserId(20);
        req.setTbScreeningList(Collections.singletonList(dto));
        TBScreening existing = new TBScreening();
        existing.setId(99L);
        when(tbScreeningRepo.getByUserIdAndBenId(2L, 20)).thenReturn(existing);
        when(tbScreeningRepo.save(any(TBScreening.class))).thenReturn(existing);
        String result = tbScreeningService.save(req);
        assertTrue(result.contains("no of tb screening items saved: 1"));
    }

    @Test
    void save_exception() throws Exception {
        TBScreeningDTO dto = new TBScreeningDTO();
        dto.setBenId(3L);
        TBScreeningRequestDTO req = new TBScreeningRequestDTO();
        req.setUserId(30);
        req.setTbScreeningList(Collections.singletonList(dto));
        when(tbScreeningRepo.getByUserIdAndBenId(3L, 30)).thenThrow(new RuntimeException("fail"));
        assertThrows(Exception.class, () -> tbScreeningService.save(req));
    }

    @Test
    void getByUserId_success() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        List<TBScreening> tbList = Arrays.asList(new TBScreening(), new TBScreening());
        when(tbScreeningRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(tbList);
        String json = tbScreeningService.getByUserId(req);
        assertNotNull(json);
        TBScreeningRequestDTO result = new Gson().fromJson(json, TBScreeningRequestDTO.class);
        assertEquals(2, result.getTbScreeningList().size());
        assertEquals(ashaId, result.getUserId());
    }

    @Test
    void getByUserId_emptyList() {
        GetBenRequestHandler req = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        when(req.getAshaId()).thenReturn(ashaId);
        when(req.getFromDate()).thenReturn(fromDate);
        when(req.getToDate()).thenReturn(toDate);
        when(tbScreeningRepo.getByUserId(ashaId, fromDate, toDate)).thenReturn(Collections.emptyList());
        String json = tbScreeningService.getByUserId(req);
        assertNotNull(json);
        TBScreeningRequestDTO result = new Gson().fromJson(json, TBScreeningRequestDTO.class);
        assertEquals(0, result.getTbScreeningList().size());
        assertEquals(ashaId, result.getUserId());
    }
}
