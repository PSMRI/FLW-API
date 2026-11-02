package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.TBSuspected;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBSuspectedDTO;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import com.iemr.flw.repo.iemr.TBSuspectedRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TBSuspectedServiceImplTest {
    @Mock
    private TBSuspectedRepo tbSuspectedRepo;

    @InjectMocks
    private TBSuspectedServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getByBenId_alwaysReturnsNull() throws Exception {
        assertNull(service.getByBenId(1L, "auth"));
    }

    @Test
    void save_newAndExistingSuspected() throws Exception {
        TBSuspectedDTO dto1 = new TBSuspectedDTO();
        dto1.setBenId(1L);
        TBSuspectedDTO dto2 = new TBSuspectedDTO();
        dto2.setBenId(2L);
        TBSuspectedRequestDTO req = new TBSuspectedRequestDTO();
        req.setUserId(100); // Integer
        req.setTbSuspectedList(Arrays.asList(dto1, dto2));

        // First is new, second is existing
        when(tbSuspectedRepo.getByUserIdAndBenId(1L, 100)).thenReturn(null);
        TBSuspected existing = new TBSuspected();
        existing.setId(99L);
        when(tbSuspectedRepo.getByUserIdAndBenId(2L, 100)).thenReturn(existing);

        String result = service.save(req);
        assertTrue(result.contains("no of tb suspected items saved:2"));
        verify(tbSuspectedRepo, times(2)).save(any(TBSuspected.class));
    }

    @Test
    void getByUserId_returnsJsonWithCorrectUserIdAndList() {
        GetBenRequestHandler req = new GetBenRequestHandler();
        req.setAshaId(123); // Integer
        Timestamp from = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp to = Timestamp.valueOf("2023-12-31 00:00:00");
        req.setFromDate(from);
        req.setToDate(to);
        TBSuspected suspected = new TBSuspected();
        suspected.setId(1L);
        when(tbSuspectedRepo.getByUserId(123, from, to)).thenReturn(Collections.singletonList(suspected));
        String json = service.getByUserId(req);
        assertTrue(json.contains("\"userId\":123"));
        assertTrue(json.contains("tbSuspectedList"));
    }

    @Test
    void getByUserId_emptyList() {
        GetBenRequestHandler req = new GetBenRequestHandler();
        req.setAshaId(123); // Integer
        Timestamp from = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp to = Timestamp.valueOf("2023-12-31 00:00:00");
        req.setFromDate(from);
        req.setToDate(to);
        when(tbSuspectedRepo.getByUserId(anyInt(), any(Timestamp.class), any(Timestamp.class))).thenReturn(Collections.emptyList());
        String json = service.getByUserId(req);
        assertTrue(json.contains("tbSuspectedList"));
    }
}
