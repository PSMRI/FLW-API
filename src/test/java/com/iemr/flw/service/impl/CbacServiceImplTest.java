package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iemr.flw.domain.identity.CbacAdditionalDetails;
import com.iemr.flw.domain.identity.CbacDetails;
import com.iemr.flw.dto.identity.CbacDTO;
import com.iemr.flw.dto.identity.CbacStatus;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.identity.CbacAdditionalDetailRepo;
import com.iemr.flw.repo.identity.CbacRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CbacServiceImplTest {
    @Mock
    private CbacRepo cbacRepo;
    @Mock
    private CbacAdditionalDetailRepo cbacAddRepo;
    @Mock
    private BeneficiaryRepo beneficiaryRepo;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CbacServiceImpl cbacService;

    @BeforeEach
    void setUp() throws Exception {
        Field field = CbacServiceImpl.class.getDeclaredField("cbac_page_size");
        field.setAccessible(true);
        field.set(cbacService, "10");
    }

    @Test
    void getByUserId_success() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp toDate = Timestamp.valueOf("2023-12-31 00:00:00");
        int pageNo = 0;
        when(dto.getAshaId()).thenReturn(ashaId);
        when(dto.getFromDate()).thenReturn(fromDate);
        when(dto.getToDate()).thenReturn(toDate);
        when(dto.getPageNo()).thenReturn(pageNo);
        when(beneficiaryRepo.getUserName(ashaId)).thenReturn("user1");
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        // Create CbacDetails with proper IDs
        CbacDetails cbacDetails1 = new CbacDetails();
        cbacDetails1.setBeneficiaryRegId(100L);
        cbacDetails1.setCbacDetailsId(1L);
        CbacDetails cbacDetails2 = new CbacDetails();
        cbacDetails2.setBeneficiaryRegId(200L);
        cbacDetails2.setCbacDetailsId(2L);
        List<CbacDetails> cbacDetailsList = Arrays.asList(cbacDetails1, cbacDetails2);
        Page<CbacDetails> cbacPage = new org.springframework.data.domain.PageImpl<>(cbacDetailsList, pageRequest, cbacDetailsList.size());
        when(cbacRepo.getAllByCreatedBy("user1", fromDate, toDate, pageRequest)).thenReturn(cbacPage);
        when(beneficiaryRepo.getBenIdFromRegID(anyLong())).thenReturn(BigInteger.valueOf(1L));
        when(cbacAddRepo.findCbacAdditionalDetail(anyLong())).thenReturn(new CbacAdditionalDetails());
        when(mapper.convertValue(any(CbacDetails.class), eq(CbacDTO.class))).thenReturn(new CbacDTO());
        String json = cbacService.getByUserId(dto);
        assertNotNull(json);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = new Gson().fromJson(json, Map.class);
        assertTrue(result.containsKey("data"));
        assertTrue(result.containsKey("pageSize"));
        assertTrue(result.containsKey("totalPage"));
    }

    @Test
    void getByUserId_exception() {
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        when(beneficiaryRepo.getUserName(anyInt())).thenThrow(new RuntimeException("fail"));
        String json = cbacService.getByUserId(dto);
        assertNull(json);
    }

    @Test
    void save_new_success() {
        CbacDTO dto = new CbacDTO();
        dto.setBeneficiaryId(1L);
    java.sql.Timestamp createdDate = java.sql.Timestamp.valueOf("2023-08-16 00:00:00");
    dto.setCreatedDate(createdDate);
        List<CbacDTO> dtos = Collections.singletonList(dto);
        when(beneficiaryRepo.getRegIDFromBenId(1L)).thenReturn(2L);
        CbacDetails cbacDetails = new CbacDetails();
    cbacDetails.setCreatedDate(createdDate);
        when(modelMapper.map(dto, CbacDetails.class)).thenReturn(cbacDetails);
    when(cbacRepo.findCbacDetailsByBeneficiaryRegIdAndCreatedDate(2L, createdDate)).thenReturn(null);
        CbacDetails savedCbac = new CbacDetails();
        savedCbac.setCbacDetailsId(5L);
        when(cbacRepo.save(any(CbacDetails.class))).thenReturn(savedCbac);
        CbacAdditionalDetails cbacAdditionalDetails = new CbacAdditionalDetails();
        when(modelMapper.map(dto, CbacAdditionalDetails.class)).thenReturn(cbacAdditionalDetails);
    when(cbacAddRepo.saveAll(anyList())).thenReturn(Collections.emptyList());
        String json = cbacService.save(dtos);
        assertNotNull(json);
        CbacStatus[] arr = new Gson().fromJson(json, CbacStatus[].class);
        assertEquals(1, arr.length);
        assertEquals("Success", arr[0].getStatus());
    }

    @Test
    void save_existing_fail() {
        CbacDTO dto = new CbacDTO();
        dto.setBeneficiaryId(1L);
    java.sql.Timestamp createdDate = java.sql.Timestamp.valueOf("2023-08-16 00:00:00");
    dto.setCreatedDate(createdDate);
        List<CbacDTO> dtos = Collections.singletonList(dto);
        when(beneficiaryRepo.getRegIDFromBenId(1L)).thenReturn(2L);
        CbacDetails cbacDetails = new CbacDetails();
    cbacDetails.setCreatedDate(createdDate);
        when(modelMapper.map(dto, CbacDetails.class)).thenReturn(cbacDetails);
        CbacDetails existing = new CbacDetails();
    when(cbacRepo.findCbacDetailsByBeneficiaryRegIdAndCreatedDate(2L, createdDate)).thenReturn(existing);
        String json = cbacService.save(dtos);
        assertNotNull(json);
        CbacStatus[] arr = new Gson().fromJson(json, CbacStatus[].class);
        assertEquals(1, arr.length);
        assertEquals("Fail", arr[0].getStatus());
    }
}
