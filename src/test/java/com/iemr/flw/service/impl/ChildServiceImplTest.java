package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.ChildRegister;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.ChildRegisterDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.ChildRegisterRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChildServiceImplTest {
    @Mock
    private ChildRegisterRepo childRepo;
    @Mock
    private BeneficiaryRepo beneficiaryRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ChildServiceImpl childService;

    @BeforeEach
    void setUp() {
        childService.modelMapper = modelMapper;
    }

    @Test
    void getByUserId_success() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        java.sql.Timestamp fromDate = java.sql.Timestamp.valueOf("2023-01-01 00:00:00");
        java.sql.Timestamp toDate = java.sql.Timestamp.valueOf("2023-12-31 00:00:00");
        when(dto.getAshaId()).thenReturn(ashaId);
        when(dto.getFromDate()).thenReturn(fromDate);
        when(dto.getToDate()).thenReturn(toDate);
        when(beneficiaryRepo.getUserName(ashaId)).thenReturn("user1");
        List<ChildRegister> childList = Arrays.asList(new ChildRegister(), new ChildRegister());
        when(childRepo.getChildDetailsForUser(eq("user1"), eq(fromDate), eq(toDate))).thenReturn(childList);
        when(modelMapper.map(any(ChildRegister.class), eq(ChildRegisterDTO.class))).thenReturn(new ChildRegisterDTO());
        String json = childService.getByUserId(dto);
        assertNotNull(json);
        ChildRegisterDTO[] arr = new Gson().fromJson(json, ChildRegisterDTO[].class);
        assertEquals(2, arr.length);
    }

    @Test
    void getByUserId_exception() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        when(dto.getAshaId()).thenReturn(ashaId);
        when(beneficiaryRepo.getUserName(anyInt())).thenThrow(new RuntimeException("fail"));
        String json = childService.getByUserId(dto);
        assertNull(json);
    }

    @Test
    void save_newChild_success() throws Exception {
        ChildRegisterDTO dto = new ChildRegisterDTO();
        dto.setBenId(1L);
        java.sql.Timestamp createdDate = java.sql.Timestamp.valueOf("2023-01-01 00:00:00");
        dto.setCreatedDate(createdDate);
        when(childRepo.findChildRegisterByBenIdAndCreatedDate(anyLong(), any(java.sql.Timestamp.class))).thenReturn(null);
        doAnswer(invocation -> {
            List<ChildRegister> list = invocation.getArgument(0);
            assertEquals(1, list.size());
            return null;
        }).when(childRepo).saveAll(anyList());
        // modelMapper.map returns void for (ChildRegisterDTO, ChildRegister)
    // No need to stub modelMapper.map for void methods; Mockito does nothing by default
        String result = childService.save(Collections.singletonList(dto));
        assertTrue(result.contains("no of child details saved: 1"));
    }

    @Test
    void save_existingChild_success() throws Exception {
        ChildRegisterDTO dto = new ChildRegisterDTO();
        dto.setBenId(2L);
        java.sql.Timestamp createdDate = java.sql.Timestamp.valueOf("2023-01-02 00:00:00");
        dto.setCreatedDate(createdDate);
        ChildRegister existing = new ChildRegister();
        existing.setId(99L);
        when(childRepo.findChildRegisterByBenIdAndCreatedDate(anyLong(), any(java.sql.Timestamp.class))).thenReturn(existing);
        doNothing().when(modelMapper).map(any(ChildRegisterDTO.class), any(ChildRegister.class));
        String result = childService.save(Collections.singletonList(dto));
        assertTrue(result.contains("no of child details saved: 1"));
    }

    @Test
    void save_exception() throws Exception {
        ChildRegisterDTO dto = new ChildRegisterDTO();
        dto.setBenId(3L);
        java.sql.Timestamp createdDate = java.sql.Timestamp.valueOf("2023-01-03 00:00:00");
        dto.setCreatedDate(createdDate);
        when(childRepo.findChildRegisterByBenIdAndCreatedDate(anyLong(), any(java.sql.Timestamp.class))).thenThrow(new RuntimeException("fail"));
        assertThrows(Exception.class, () -> childService.save(Collections.singletonList(dto)));
    }
}
