package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleServiceImplTest {
    @Mock
    private EligibleCoupleRegisterRepo eligibleCoupleRegisterRepo;
    @Mock
    private EligibleCoupleTrackingRepo eligibleCoupleTrackingRepo;
    @Mock
    private IncentivesRepo incentivesRepo;
    @Mock
    private UserServiceRoleRepo userRepo;
    @Mock
    private IncentiveRecordRepo recordRepo;
    @Mock
    private BeneficiaryRepo beneficiaryRepo;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CoupleServiceImpl coupleService;

    @BeforeEach
    void setUp() throws Exception {
        // Set up ObjectMapper and ModelMapper fields using reflection
        Field mapperField = CoupleServiceImpl.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        mapperField.set(coupleService, mapper);

        Field modelMapperField = CoupleServiceImpl.class.getDeclaredField("modelMapper");
        modelMapperField.setAccessible(true);
        modelMapperField.set(coupleService, modelMapper);
    }

    @Test
    void registerEligibleCouple_existingECRWithIncentives_success() {
        // Arrange
        EligibleCoupleDTO dto = new EligibleCoupleDTO();
        dto.setBenId(1L);
        dto.setNumLiveChildren(1);
        dto.setMarriageFirstChildGap(3);
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleDTO> dtoList = Arrays.asList(dto);

        EligibleCoupleRegister existingECR = new EligibleCoupleRegister();
        existingECR.setId(1L);
        existingECR.setNumLiveChildren(0);

        IncentiveActivity activity = new IncentiveActivity();
        activity.setId(1L);
        activity.getName();
        activity.setRate(100);

        when(eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(1L)).thenReturn(existingECR);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("MARRIAGE_1st_CHILD_GAP", "FAMILY PLANNING"))
                .thenReturn(activity);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong()))
                .thenReturn(null);
        when(userRepo.getUserIdByName("testUser")).thenReturn(123);
        when(eligibleCoupleRegisterRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingECR));
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Act
        String result = coupleService.registerEligibleCouple(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ecr details saved: 1", result);
        verify(eligibleCoupleRegisterRepo).findEligibleCoupleRegisterByBenId(1L);
        verify(incentivesRepo).findIncentiveMasterByNameAndGroup("MARRIAGE_1st_CHILD_GAP", "FAMILY PLANNING");
        verify(recordRepo).findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong());
        verify(userRepo).getUserIdByName("testUser");
        verify(eligibleCoupleRegisterRepo).saveAll(anyList());
        verify(recordRepo).saveAll(anyList());
        verify(modelMapper).map(dto, existingECR);
    }

    @Test
    void registerEligibleCouple_secondChildGapIncentive_success() {
        // Arrange
        EligibleCoupleDTO dto = new EligibleCoupleDTO();
        dto.setBenId(1L);
        dto.setNumLiveChildren(2);
        dto.setMarriageFirstChildGap(2);
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleDTO> dtoList = Arrays.asList(dto);

        EligibleCoupleRegister existingECR = new EligibleCoupleRegister();
        existingECR.setId(1L);
        existingECR.setNumLiveChildren(1);

        IncentiveActivity activity = new IncentiveActivity();
        activity.setId(2L);
        activity.setName("Second child gap");
        activity.setRate(200);

        when(eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(1L)).thenReturn(existingECR);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("1st_2nd_CHILD_GAP", "FAMILY PLANNING"))
                .thenReturn(activity);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong()))
                .thenReturn(null);
        when(userRepo.getUserIdByName("testUser")).thenReturn(123);
        when(eligibleCoupleRegisterRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingECR));
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Act
        String result = coupleService.registerEligibleCouple(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ecr details saved: 1", result);
        verify(incentivesRepo).findIncentiveMasterByNameAndGroup("1st_2nd_CHILD_GAP", "FAMILY PLANNING");
    }

    @Test
    void registerEligibleCouple_newECR_success() {
        // Arrange
        EligibleCoupleDTO dto = new EligibleCoupleDTO();
        dto.setBenId(1L);
        dto.setNumLiveChildren(0);
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleDTO> dtoList = Arrays.asList(dto);

        EligibleCoupleRegister newECR = new EligibleCoupleRegister();
        newECR.setId(null);

        when(eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(1L)).thenReturn(null);
        when(eligibleCoupleRegisterRepo.saveAll(anyList())).thenReturn(Arrays.asList(newECR));
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        doAnswer(invocation -> {
            EligibleCoupleDTO source = invocation.getArgument(0);
            EligibleCoupleRegister target = invocation.getArgument(1);
            target.setCreatedBy(source.getCreatedBy());
            return null;
        }).when(modelMapper).map(eq(dto), any(EligibleCoupleRegister.class));

        // Act
        String result = coupleService.registerEligibleCouple(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ecr details saved: 1", result);
        verify(eligibleCoupleRegisterRepo).findEligibleCoupleRegisterByBenId(1L);
        verify(eligibleCoupleRegisterRepo).saveAll(anyList());
        verify(recordRepo).saveAll(anyList());
        verify(modelMapper).map(eq(dto), any(EligibleCoupleRegister.class));
    }

    @Test
    void registerEligibleCouple_existingRecord_noIncentive() {
        // Arrange
        EligibleCoupleDTO dto = new EligibleCoupleDTO();
        dto.setBenId(1L);
        dto.setNumLiveChildren(1);
        dto.setMarriageFirstChildGap(3);
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleDTO> dtoList = Arrays.asList(dto);

        EligibleCoupleRegister existingECR = new EligibleCoupleRegister();
        existingECR.setId(1L);
        existingECR.setNumLiveChildren(0);

        IncentiveActivity activity = new IncentiveActivity();
        activity.setId(1L);
        activity.setName("Marriage gap");
        activity.setRate(100);

        IncentiveActivityRecord existingRecord = new IncentiveActivityRecord();

        when(eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(1L)).thenReturn(existingECR);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("MARRIAGE_1st_CHILD_GAP", "FAMILY PLANNING"))
                .thenReturn(activity);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong()))
                .thenReturn(existingRecord);
        when(userRepo.getUserIdByName("testUser")).thenReturn(123);
        when(eligibleCoupleRegisterRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingECR));
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Act
        String result = coupleService.registerEligibleCouple(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ecr details saved: 1", result);
        verify(recordRepo).findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong());
        // getUserIdByName should still be called even for existing record due to service logic
        verify(userRepo).getUserIdByName("testUser");
    }

    @Test
    void registerEligibleCouple_exception_returnsError() {
        // Arrange
        EligibleCoupleDTO dto = new EligibleCoupleDTO();
        dto.setBenId(1L);
        List<EligibleCoupleDTO> dtoList = Arrays.asList(dto);

        when(eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(1L))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = coupleService.registerEligibleCouple(dtoList);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("error while saving ecr details: "));
        assertTrue(result.contains("Database error"));
    }

    @Test
    void registerEligibleCoupleTracking_newECTWithAntaraIncentive_success() {
        // Arrange
        EligibleCoupleTrackingDTO dto = new EligibleCoupleTrackingDTO();
        dto.setBenId(1L);
        dto.setVisitDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setMethodOfContraception("ANTRA Injection");
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleTrackingDTO> dtoList = Arrays.asList(dto);

        EligibleCoupleTracking newECT = new EligibleCoupleTracking();

        IncentiveActivity antaraActivity = new IncentiveActivity();
        antaraActivity.setId(1L);
        antaraActivity.setName("ANTARA_PROG_1");
        antaraActivity.setRate(500);

        when(eligibleCoupleTrackingRepo.findActiveEligibleCoupleTrackingByBenId(1L, dto.getVisitDate()))
                .thenReturn(null);
        when(userRepo.getUserIdByName("testUser")).thenReturn(123);
        when(eligibleCoupleTrackingRepo.findCouplesHadAntara(1L)).thenReturn(new ArrayList<>());
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("ANTARA_PROG_1", "FAMILY PLANNING"))
                .thenReturn(antaraActivity);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong()))
                .thenReturn(null);
        when(eligibleCoupleTrackingRepo.saveAll(anyList())).thenReturn(Arrays.asList(newECT));
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        doAnswer(invocation -> {
            EligibleCoupleTrackingDTO source = invocation.getArgument(0);
            EligibleCoupleTracking target = invocation.getArgument(1);
            target.setCreatedBy(source.getCreatedBy());
            target.setMethodOfContraception(source.getMethodOfContraception());
            target.setBenId(source.getBenId());
            target.setCreatedDate(source.getCreatedDate());
            return null;
        }).when(modelMapper).map(eq(dto), any(EligibleCoupleTracking.class));

        // Act
        String result = coupleService.registerEligibleCoupleTracking(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ect details saved: 1", result);
        verify(eligibleCoupleTrackingRepo).findActiveEligibleCoupleTrackingByBenId(1L, dto.getVisitDate());
        verify(userRepo).getUserIdByName("testUser");
        verify(eligibleCoupleTrackingRepo).findCouplesHadAntara(1L);
        verify(incentivesRepo).findIncentiveMasterByNameAndGroup("ANTARA_PROG_1", "FAMILY PLANNING");
        verify(recordRepo).findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong());
        verify(eligibleCoupleTrackingRepo).saveAll(anyList());
        verify(recordRepo).saveAll(anyList());
    }

    @Test
    void registerEligibleCoupleTracking_existingECTWithAntaraProg2_success() {
        // Arrange
        EligibleCoupleTrackingDTO dto = new EligibleCoupleTrackingDTO();
        dto.setBenId(1L);
        dto.setVisitDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setMethodOfContraception("ANTRA Injection");
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleTrackingDTO> dtoList = Arrays.asList(dto);

        EligibleCoupleTracking existingECT = new EligibleCoupleTracking();
        existingECT.setId(1L);

        List<EligibleCoupleTracking> antaraHistory = Arrays.asList(new EligibleCoupleTracking());

        IncentiveActivity antaraActivity2 = new IncentiveActivity();
        antaraActivity2.setId(2L);
        antaraActivity2.setName("ANTARA_PROG_2");
        antaraActivity2.setRate(600);

        when(eligibleCoupleTrackingRepo.findActiveEligibleCoupleTrackingByBenId(1L, dto.getVisitDate()))
                .thenReturn(existingECT);
        when(userRepo.getUserIdByName("testUser")).thenReturn(123);
        when(eligibleCoupleTrackingRepo.findCouplesHadAntara(1L)).thenReturn(antaraHistory);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("ANTARA_PROG_2", "FAMILY PLANNING"))
                .thenReturn(antaraActivity2);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong()))
                .thenReturn(null);
        when(eligibleCoupleTrackingRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingECT));
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        doAnswer(invocation -> {
            EligibleCoupleTrackingDTO source = invocation.getArgument(0);
            EligibleCoupleTracking target = invocation.getArgument(1);
            target.setCreatedBy(source.getCreatedBy());
            target.setMethodOfContraception(source.getMethodOfContraception());
            target.setBenId(source.getBenId());
            target.setCreatedDate(source.getCreatedDate());
            return null;
        }).when(modelMapper).map(dto, existingECT);

        // Act
        String result = coupleService.registerEligibleCoupleTracking(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ect details saved: 1", result);
        verify(incentivesRepo).findIncentiveMasterByNameAndGroup("ANTARA_PROG_2", "FAMILY PLANNING");
        verify(modelMapper).map(dto, existingECT);
    }

    @Test
    void registerEligibleCoupleTracking_antaraProg3And4_success() {
        // Test for ANTARA_PROG_3 (numAntaraDosage = 2)
        EligibleCoupleTrackingDTO dto1 = new EligibleCoupleTrackingDTO();
        dto1.setBenId(1L);
        dto1.setVisitDate(Timestamp.valueOf(LocalDateTime.now()));
        dto1.setMethodOfContraception("ANTRA Injection");
        dto1.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto1.setCreatedBy("testUser");

        List<EligibleCoupleTracking> antaraHistory3 = Arrays.asList(
            new EligibleCoupleTracking(), new EligibleCoupleTracking()
        );

        IncentiveActivity antaraActivity3 = new IncentiveActivity();
        antaraActivity3.setId(3L);
        antaraActivity3.setName("ANTARA_PROG_3");
        antaraActivity3.setRate(700);

        when(eligibleCoupleTrackingRepo.findActiveEligibleCoupleTrackingByBenId(1L, dto1.getVisitDate()))
                .thenReturn(null);
        when(userRepo.getUserIdByName("testUser")).thenReturn(123);
        when(eligibleCoupleTrackingRepo.findCouplesHadAntara(1L)).thenReturn(antaraHistory3);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("ANTARA_PROG_3", "FAMILY PLANNING"))
                .thenReturn(antaraActivity3);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong()))
                .thenReturn(null);
        when(eligibleCoupleTrackingRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        doAnswer(invocation -> {
            EligibleCoupleTrackingDTO source = invocation.getArgument(0);
            EligibleCoupleTracking target = invocation.getArgument(1);
            target.setCreatedBy(source.getCreatedBy());
            target.setMethodOfContraception(source.getMethodOfContraception());
            target.setBenId(source.getBenId());
            target.setCreatedDate(source.getCreatedDate());
            return null;
        }).when(modelMapper).map(eq(dto1), any(EligibleCoupleTracking.class));

        String result1 = coupleService.registerEligibleCoupleTracking(Arrays.asList(dto1));
        assertNotNull(result1);
        verify(incentivesRepo).findIncentiveMasterByNameAndGroup("ANTARA_PROG_3", "FAMILY PLANNING");

        // Test for ANTARA_PROG_4 (numAntaraDosage = 3)
        reset(eligibleCoupleTrackingRepo);
        reset(incentivesRepo);
        reset(recordRepo);
        reset(userRepo);
        
        EligibleCoupleTrackingDTO dto2 = new EligibleCoupleTrackingDTO();
        dto2.setBenId(2L);
        dto2.setVisitDate(Timestamp.valueOf(LocalDateTime.now()));
        dto2.setMethodOfContraception("ANTRA Injection");
        dto2.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto2.setCreatedBy("testUser");

        List<EligibleCoupleTracking> antaraHistory4 = Arrays.asList(
            new EligibleCoupleTracking(), new EligibleCoupleTracking(), new EligibleCoupleTracking()
        );

        IncentiveActivity antaraActivity4 = new IncentiveActivity();
        antaraActivity4.setId(4L);
        antaraActivity4.setName("ANTARA_PROG_4");
        antaraActivity4.setRate(800);

        when(eligibleCoupleTrackingRepo.findActiveEligibleCoupleTrackingByBenId(2L, dto2.getVisitDate()))
                .thenReturn(null);
        when(userRepo.getUserIdByName("testUser")).thenReturn(123);
        when(eligibleCoupleTrackingRepo.findCouplesHadAntara(2L)).thenReturn(antaraHistory4);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("ANTARA_PROG_4", "FAMILY PLANNING"))
                .thenReturn(antaraActivity4);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong()))
                .thenReturn(null);
        when(eligibleCoupleTrackingRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        doAnswer(invocation -> {
            EligibleCoupleTrackingDTO source = invocation.getArgument(0);
            EligibleCoupleTracking target = invocation.getArgument(1);
            target.setCreatedBy(source.getCreatedBy());
            target.setMethodOfContraception(source.getMethodOfContraception());
            target.setBenId(source.getBenId());
            target.setCreatedDate(source.getCreatedDate());
            return null;
        }).when(modelMapper).map(eq(dto2), any(EligibleCoupleTracking.class));

        String result2 = coupleService.registerEligibleCoupleTracking(Arrays.asList(dto2));
        assertNotNull(result2);
        verify(incentivesRepo).findIncentiveMasterByNameAndGroup("ANTARA_PROG_4", "FAMILY PLANNING");
    }

    @Test
    void registerEligibleCoupleTracking_noIncentiveForNonAntara_success() {
        // Arrange
        EligibleCoupleTrackingDTO dto = new EligibleCoupleTrackingDTO();
        dto.setBenId(1L);
        dto.setVisitDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setMethodOfContraception("Other method");
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleTrackingDTO> dtoList = Arrays.asList(dto);

        when(eligibleCoupleTrackingRepo.findActiveEligibleCoupleTrackingByBenId(1L, dto.getVisitDate()))
                .thenReturn(null);
        when(userRepo.getUserIdByName("testUser")).thenReturn(123);
        when(eligibleCoupleTrackingRepo.findCouplesHadAntara(1L)).thenReturn(new ArrayList<>());
        when(eligibleCoupleTrackingRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        doAnswer(invocation -> {
            EligibleCoupleTrackingDTO source = invocation.getArgument(0);
            EligibleCoupleTracking target = invocation.getArgument(1);
            target.setCreatedBy(source.getCreatedBy());
            target.setMethodOfContraception(source.getMethodOfContraception());
            target.setBenId(source.getBenId());
            target.setCreatedDate(source.getCreatedDate());
            return null;
        }).when(modelMapper).map(eq(dto), any(EligibleCoupleTracking.class));

        // Act
        String result = coupleService.registerEligibleCoupleTracking(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ect details saved: 1", result);
        verify(incentivesRepo, never()).findIncentiveMasterByNameAndGroup(anyString(), anyString());
    }

    @Test
    void registerEligibleCoupleTracking_exception_returnsError() {
        // Arrange
        EligibleCoupleTrackingDTO dto = new EligibleCoupleTrackingDTO();
        dto.setBenId(1L);
        List<EligibleCoupleTrackingDTO> dtoList = Arrays.asList(dto);

        when(eligibleCoupleTrackingRepo.findActiveEligibleCoupleTrackingByBenId(anyLong(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = coupleService.registerEligibleCoupleTracking(dtoList);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("error while saving ect details: "));
        assertTrue(result.contains("Database error"));
    }

    @Test
    void getEligibleCoupleRegRecords_success() {
        // Arrange
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf(LocalDateTime.now().minusDays(30));
        Timestamp toDate = Timestamp.valueOf(LocalDateTime.now());
        
        when(dto.getAshaId()).thenReturn(ashaId);
        when(dto.getFromDate()).thenReturn(fromDate);
        when(dto.getToDate()).thenReturn(toDate);
        
        String userName = "testUser";
        List<EligibleCoupleRegister> registerList = Arrays.asList(new EligibleCoupleRegister());
        EligibleCoupleDTO mappedDTO = new EligibleCoupleDTO();
        
        when(beneficiaryRepo.getUserName(ashaId)).thenReturn(userName);
        when(eligibleCoupleRegisterRepo.getECRegRecords(userName, fromDate, toDate)).thenReturn(registerList);
        when(mapper.convertValue(any(EligibleCoupleRegister.class), eq(EligibleCoupleDTO.class)))
                .thenReturn(mappedDTO);

        // Act
        String result = coupleService.getEligibleCoupleRegRecords(dto);

        // Assert
        assertNotNull(result);
        @SuppressWarnings("unchecked")
        List<EligibleCoupleDTO> resultList = new Gson().fromJson(result, List.class);
        assertEquals(1, resultList.size());
        
        verify(beneficiaryRepo).getUserName(ashaId);
        verify(eligibleCoupleRegisterRepo).getECRegRecords(userName, fromDate, toDate);
        verify(mapper).convertValue(any(EligibleCoupleRegister.class), eq(EligibleCoupleDTO.class));
    }

    @Test
    void getEligibleCoupleRegRecords_exception_returnsNull() {
        // Arrange
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        when(dto.getAshaId()).thenReturn(123);
        when(beneficiaryRepo.getUserName(123)).thenThrow(new RuntimeException("Database error"));

        // Act
        String result = coupleService.getEligibleCoupleRegRecords(dto);

        // Assert
        assertNull(result);
        verify(beneficiaryRepo).getUserName(123);
    }

    @Test
    void getEligibleCoupleTracking_success() {
        // Arrange
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        Integer ashaId = 123;
        Timestamp fromDate = Timestamp.valueOf(LocalDateTime.now().minusDays(30));
        Timestamp toDate = Timestamp.valueOf(LocalDateTime.now());
        
        when(dto.getAshaId()).thenReturn(ashaId);
        when(dto.getFromDate()).thenReturn(fromDate);
        when(dto.getToDate()).thenReturn(toDate);
        
        String userName = "testUser";
        List<EligibleCoupleTracking> trackingList = Arrays.asList(new EligibleCoupleTracking());
        EligibleCoupleTrackingDTO mappedDTO = new EligibleCoupleTrackingDTO();
        
        when(beneficiaryRepo.getUserName(ashaId)).thenReturn(userName);
        when(eligibleCoupleTrackingRepo.getECTrackRecords(userName, fromDate, toDate)).thenReturn(trackingList);
        when(mapper.convertValue(any(EligibleCoupleTracking.class), eq(EligibleCoupleTrackingDTO.class)))
                .thenReturn(mappedDTO);

        // Act
        List<EligibleCoupleTrackingDTO> result = coupleService.getEligibleCoupleTracking(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mappedDTO, result.get(0));
        
        verify(beneficiaryRepo).getUserName(ashaId);
        verify(eligibleCoupleTrackingRepo).getECTrackRecords(userName, fromDate, toDate);
        verify(mapper).convertValue(any(EligibleCoupleTracking.class), eq(EligibleCoupleTrackingDTO.class));
    }

    @Test
    void getEligibleCoupleTracking_exception_returnsNull() {
        // Arrange
        GetBenRequestHandler dto = mock(GetBenRequestHandler.class);
        when(dto.getAshaId()).thenReturn(123);
        when(beneficiaryRepo.getUserName(123)).thenThrow(new RuntimeException("Database error"));

        // Act
        List<EligibleCoupleTrackingDTO> result = coupleService.getEligibleCoupleTracking(dto);

        // Assert
        assertNull(result);
        verify(beneficiaryRepo).getUserName(123);
    }

    @Test
    void registerEligibleCoupleTracking_nullIncentiveActivity_noRecordAdded() {
        // Arrange
        EligibleCoupleTrackingDTO dto = new EligibleCoupleTrackingDTO();
        dto.setBenId(1L);
        dto.setVisitDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setMethodOfContraception("ANTRA Injection");
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleTrackingDTO> dtoList = Arrays.asList(dto);

        when(eligibleCoupleTrackingRepo.findActiveEligibleCoupleTrackingByBenId(1L, dto.getVisitDate()))
                .thenReturn(null);
        when(userRepo.getUserIdByName("testUser")).thenReturn(123);
        when(eligibleCoupleTrackingRepo.findCouplesHadAntara(1L)).thenReturn(new ArrayList<>());
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("ANTARA_PROG_1", "FAMILY PLANNING"))
                .thenReturn(null);
        when(eligibleCoupleTrackingRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());
        doAnswer(invocation -> {
            EligibleCoupleTrackingDTO source = invocation.getArgument(0);
            EligibleCoupleTracking target = invocation.getArgument(1);
            target.setCreatedBy(source.getCreatedBy());
            target.setMethodOfContraception(source.getMethodOfContraception());
            target.setBenId(source.getBenId());
            target.setCreatedDate(source.getCreatedDate());
            return null;
        }).when(modelMapper).map(eq(dto), any(EligibleCoupleTracking.class));

        // Act
        String result = coupleService.registerEligibleCoupleTracking(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ect details saved: 1", result);
        verify(incentivesRepo).findIncentiveMasterByNameAndGroup("ANTARA_PROG_1", "FAMILY PLANNING");
        verify(recordRepo, never()).findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong());
    }

    @Test
    void registerEligibleCouple_nullIncentiveActivity_noRecordCreated() {
        // Arrange
        EligibleCoupleDTO dto = new EligibleCoupleDTO();
        dto.setBenId(1L);
        dto.setNumLiveChildren(1);
        dto.setMarriageFirstChildGap(3);
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleDTO> dtoList = Arrays.asList(dto);

        EligibleCoupleRegister existingECR = new EligibleCoupleRegister();
        existingECR.setId(1L);
        existingECR.setNumLiveChildren(0);

        when(eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(1L)).thenReturn(existingECR);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("MARRIAGE_1st_CHILD_GAP", "FAMILY PLANNING"))
                .thenReturn(null);
        when(eligibleCoupleRegisterRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingECR));
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Act
        String result = coupleService.registerEligibleCouple(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ecr details saved: 1", result);
        verify(incentivesRepo).findIncentiveMasterByNameAndGroup("MARRIAGE_1st_CHILD_GAP", "FAMILY PLANNING");
        verify(recordRepo, never()).findRecordByActivityIdCreatedDateBenId(anyLong(), any(Timestamp.class), anyLong());
    }

    @Test
    void registerEligibleCouple_existingECRNullNumLiveChildren_newRecord() {
        // Arrange
        EligibleCoupleDTO dto = new EligibleCoupleDTO();
        dto.setBenId(1L);
        dto.setNumLiveChildren(1);
        dto.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        dto.setCreatedBy("testUser");
        List<EligibleCoupleDTO> dtoList = Arrays.asList(dto);

        EligibleCoupleRegister existingECR = new EligibleCoupleRegister();
        existingECR.setId(1L);
        existingECR.setNumLiveChildren(null); // null numLiveChildren should trigger new record creation

        when(eligibleCoupleRegisterRepo.findEligibleCoupleRegisterByBenId(1L)).thenReturn(existingECR);
        when(eligibleCoupleRegisterRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingECR));
        when(recordRepo.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Act
        String result = coupleService.registerEligibleCouple(dtoList);

        // Assert
        assertNotNull(result);
        assertEquals("no of ecr details saved: 1", result);
        // Should not call incentive methods because numLiveChildren is null
        verify(incentivesRepo, never()).findIncentiveMasterByNameAndGroup(anyString(), anyString());
        verify(modelMapper).map(eq(dto), any(EligibleCoupleRegister.class));
    }
}
