package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaternalHealthServiceImplTest {

    @Mock
    private PregnantWomanRegisterRepo pregnantWomanRegisterRepo;
    @Mock
    private ANCVisitRepo ancVisitRepo;
    @Mock
    private AncCareRepo ancCareRepo;
    @Mock
    private PNCVisitRepo pncVisitRepo;
    @Mock
    private PNCCareRepo pncCareRepo;
    @Mock
    private BenVisitDetailsRepo benVisitDetailsRepo;
    @Mock
    private BeneficiaryRepo beneficiaryRepo;
    @Mock
    private PmsmaRepo pmsmaRepo;
    @Mock
    private IncentivesRepo incentivesRepo;
    @Mock
    private UserServiceRoleRepo userRepo;
    @Mock
    private IncentiveRecordRepo recordRepo;

    @InjectMocks
    private MaternalHealthServiceImpl service;

    private Timestamp currentTime;

    @BeforeEach
    void setUp() {
        currentTime = Timestamp.from(Instant.now());
    }

    // registerPregnantWoman tests
    @Test
    void testRegisterPregnantWoman_newRecord_success() {
        // Arrange
        PregnantWomanDTO dto = createPregnantWomanDTO();
        PregnantWomanRegister savedRegister = new PregnantWomanRegister();
        savedRegister.setId(1L);

        when(pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(dto.getBenId(), true))
                .thenReturn(null);
        when(pregnantWomanRegisterRepo.saveAll(anyList())).thenReturn(Arrays.asList(savedRegister));

        // Act
        String result = service.registerPregnantWoman(Arrays.asList(dto));

        // Assert
        assertEquals("no of pwr details saved: 1", result);
        verify(pregnantWomanRegisterRepo).findPregnantWomanRegisterByBenIdAndIsActive(dto.getBenId(), true);
        verify(pregnantWomanRegisterRepo).saveAll(anyList());
    }

    @Test
    void testRegisterPregnantWoman_existingRecord_success() {
        // Arrange
        PregnantWomanDTO dto = createPregnantWomanDTO();
        PregnantWomanRegister existingRegister = new PregnantWomanRegister();
        existingRegister.setId(5L);

        when(pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(dto.getBenId(), true))
                .thenReturn(existingRegister);
        when(pregnantWomanRegisterRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingRegister));

        // Act
        String result = service.registerPregnantWoman(Arrays.asList(dto));

        // Assert
        assertEquals("no of pwr details saved: 1", result);
        verify(pregnantWomanRegisterRepo).findPregnantWomanRegisterByBenIdAndIsActive(dto.getBenId(), true);
        verify(pregnantWomanRegisterRepo).saveAll(anyList());
    }

    @Test
    void testRegisterPregnantWoman_exception() {
        // Arrange
        PregnantWomanDTO dto = createPregnantWomanDTO();
        when(pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(dto.getBenId(), true))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = service.registerPregnantWoman(Arrays.asList(dto));

        // Assert
        assertNull(result);
    }

    // getPregnantWoman tests
    @Test
    void testGetPregnantWoman_success() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        String userName = "testUser";
        PregnantWomanRegister register = new PregnantWomanRegister();
        register.setId(1L);
        register.setBenId(123L);

        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenReturn(userName);
        when(pregnantWomanRegisterRepo.getPWRWithBen(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(register));

        // Act
        List<PregnantWomanDTO> result = service.getPregnantWoman(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(beneficiaryRepo).getUserName(dto.getAshaId());
        verify(pregnantWomanRegisterRepo).getPWRWithBen(userName, dto.getFromDate(), dto.getToDate());
    }

    @Test
    void testGetPregnantWoman_exception() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenThrow(new RuntimeException("Database error"));

        // Act
        List<PregnantWomanDTO> result = service.getPregnantWoman(dto);

        // Assert
        assertNull(result);
    }

    // getANCVisits tests
    @Test
    void testGetANCVisits_success() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        String userName = "testUser";
        ANCVisit ancVisit = new ANCVisit();
        ancVisit.setId(1L);
        ancVisit.setBenId(123L);

        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenReturn(userName);
        when(ancVisitRepo.getANCForPW(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(ancVisit));

        // Act
        List<ANCVisitDTO> result = service.getANCVisits(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(beneficiaryRepo).getUserName(dto.getAshaId());
        verify(ancVisitRepo).getANCForPW(userName, dto.getFromDate(), dto.getToDate());
    }

    @Test
    void testGetANCVisits_exception() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenThrow(new RuntimeException("Database error"));

        // Act
        List<ANCVisitDTO> result = service.getANCVisits(dto);

        // Assert
        assertNull(result);
    }

    // saveANCVisit tests
    @Test
    void testSaveANCVisit_newRecord_success() {
        // Arrange
        ANCVisitDTO dto = createANCVisitDTO();
        BenVisitDetail savedVisitDetail = new BenVisitDetail();
        savedVisitDetail.setBenVisitId(1L);

        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), dto.getAncVisit(), true))
                .thenReturn(null);
        when(beneficiaryRepo.getRegIDFromBenId(dto.getBenId())).thenReturn(100L);
        when(pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(dto.getBenId(), true))
                .thenReturn(null);
        when(benVisitDetailsRepo.save(any(BenVisitDetail.class))).thenReturn(savedVisitDetail);
        when(ancVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList(new ANCVisit()));
        when(ancCareRepo.saveAll(anyList())).thenReturn(Arrays.asList(new AncCare()));

        // Act
        String result = service.saveANCVisit(Arrays.asList(dto));

        // Assert
        assertEquals("no of anc details saved: 1", result);
        verify(ancVisitRepo).findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), dto.getAncVisit(), true);
        verify(beneficiaryRepo).getRegIDFromBenId(dto.getBenId());
        verify(benVisitDetailsRepo).save(any(BenVisitDetail.class));
        verify(ancVisitRepo).saveAll(anyList());
        verify(ancCareRepo).saveAll(anyList());
    }

    @Test
    void testSaveANCVisit_newRecordWithPregnantWomanRegister_success() {
        // Arrange
        ANCVisitDTO dto = createANCVisitDTO();
        PregnantWomanRegister pwr = new PregnantWomanRegister();
        pwr.setLmpDate(currentTime);
        BenVisitDetail savedVisitDetail = new BenVisitDetail();
        savedVisitDetail.setBenVisitId(1L);

        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), dto.getAncVisit(), true))
                .thenReturn(null);
        when(beneficiaryRepo.getRegIDFromBenId(dto.getBenId())).thenReturn(100L);
        when(pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(dto.getBenId(), true))
                .thenReturn(pwr);
        when(benVisitDetailsRepo.save(any(BenVisitDetail.class))).thenReturn(savedVisitDetail);
        when(ancVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList(new ANCVisit()));
        when(ancCareRepo.saveAll(anyList())).thenReturn(Arrays.asList(new AncCare()));

        // Act
        String result = service.saveANCVisit(Arrays.asList(dto));

        // Assert
        assertEquals("no of anc details saved: 1", result);
    }

    @Test
    void testSaveANCVisit_existingRecord_success() {
        // Arrange
        ANCVisitDTO dto = createANCVisitDTO();
        ANCVisit existingVisit = new ANCVisit();
        existingVisit.setId(5L);

        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), dto.getAncVisit(), true))
                .thenReturn(existingVisit);
        when(ancVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingVisit));
        when(ancCareRepo.saveAll(anyList())).thenReturn(Arrays.asList());

        // Act
        String result = service.saveANCVisit(Arrays.asList(dto));

        // Assert
        assertEquals("no of anc details saved: 1", result);
        verify(ancVisitRepo).findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), dto.getAncVisit(), true);
        verify(ancVisitRepo).saveAll(anyList());
    }

    @Test
    void testSaveANCVisit_withIncentives_anc1() {
        // Arrange
        ANCVisitDTO dto = createANCVisitDTO();
        dto.setAncVisit(1); // First ANC visit
        BenVisitDetail savedVisitDetail = new BenVisitDetail();
        savedVisitDetail.setBenVisitId(1L);
        
        ANCVisit savedAncVisit = new ANCVisit();
        savedAncVisit.setAncVisit(1);
        savedAncVisit.setBenId(dto.getBenId());
        savedAncVisit.setCreatedDate(currentTime);
        savedAncVisit.setCreatedBy("testUser");

        IncentiveActivity anc1Activity = new IncentiveActivity();
        anc1Activity.setId(1L);
        anc1Activity.setRate(100);

        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), dto.getAncVisit(), true))
                .thenReturn(null);
        when(beneficiaryRepo.getRegIDFromBenId(dto.getBenId())).thenReturn(100L);
        when(pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(dto.getBenId(), true))
                .thenReturn(null);
        when(benVisitDetailsRepo.save(any(BenVisitDetail.class))).thenReturn(savedVisitDetail);
        when(ancVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList(savedAncVisit));
        when(ancCareRepo.saveAll(anyList())).thenReturn(Arrays.asList(new AncCare()));
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("ANC-1", "MATERNAL HEALTH"))
                .thenReturn(anc1Activity);
        when(userRepo.getUserIdByName("testUser")).thenReturn(1);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(1L, currentTime, dto.getBenId()))
                .thenReturn(null);

        // Act
        String result = service.saveANCVisit(Arrays.asList(dto));

        // Assert
        assertEquals("no of anc details saved: 1", result);
        verify(recordRepo).save(any(IncentiveActivityRecord.class));
    }

    @Test
    void testSaveANCVisit_withIncentives_anc4() {
        // Arrange
        ANCVisitDTO dto = createANCVisitDTO();
        dto.setAncVisit(4); // Fourth ANC visit
        
        ANCVisit existingAncVisit = new ANCVisit();
        existingAncVisit.setId(1L);
        existingAncVisit.setAncVisit(4);
        existingAncVisit.setBenId(dto.getBenId());
        existingAncVisit.setCreatedDate(currentTime);
        existingAncVisit.setCreatedBy("testUser");

        IncentiveActivity ancFullActivity = new IncentiveActivity();
        ancFullActivity.setId(2L);
        ancFullActivity.setRate(200);

        // Create mock ANC visits for all 4 visits
        ANCVisit visit1 = new ANCVisit();
        visit1.setCreatedDate(currentTime);
        ANCVisit visit2 = new ANCVisit();
        ANCVisit visit3 = new ANCVisit();
        ANCVisit visit4 = new ANCVisit();
        visit4.setCreatedDate(currentTime);

        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), dto.getAncVisit(), true))
                .thenReturn(existingAncVisit);
        when(ancVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingAncVisit));
        when(ancCareRepo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("ANC-1", "MATERNAL HEALTH"))
                .thenReturn(new IncentiveActivity()); // Need to return a non-null activity for processing to continue
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("ANC-FULL", "MATERNAL HEALTH"))
                .thenReturn(ancFullActivity);
        when(userRepo.getUserIdByName("testUser")).thenReturn(1);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(2L, currentTime, dto.getBenId()))
                .thenReturn(null);
        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), 1, true))
                .thenReturn(visit1);
        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), 2, true))
                .thenReturn(visit2);
        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), 3, true))
                .thenReturn(visit3);
        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), 4, true))
                .thenReturn(visit4);

        // Act
        String result = service.saveANCVisit(Arrays.asList(dto));

        // Assert
        assertEquals("no of anc details saved: 1", result);
        verify(recordRepo).save(any(IncentiveActivityRecord.class));
    }

    @Test
    void testSaveANCVisit_exception() {
        // Arrange
        ANCVisitDTO dto = createANCVisitDTO();
        when(ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(dto.getBenId(), dto.getAncVisit(), true))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = service.saveANCVisit(Arrays.asList(dto));

        // Assert
        assertNull(result);
    }

    // getPmsmaRecords tests
    @Test
    void testGetPmsmaRecords_success() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        String userName = "testUser";
        PMSMA pmsma = new PMSMA();
        pmsma.setId(1L);
        pmsma.setBenId(123L);

        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenReturn(userName);
        when(pmsmaRepo.getAllPmsmaByAshaId(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(pmsma));

        // Act
        List<PmsmaDTO> result = service.getPmsmaRecords(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(beneficiaryRepo).getUserName(dto.getAshaId());
        verify(pmsmaRepo).getAllPmsmaByAshaId(userName, dto.getFromDate(), dto.getToDate());
    }

    @Test
    void testGetPmsmaRecords_exception() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenThrow(new RuntimeException("Database error"));

        // Act
        List<PmsmaDTO> result = service.getPmsmaRecords(dto);

        // Assert
        assertNull(result);
    }

    // savePmsmaRecords tests
    @Test
    void testSavePmsmaRecords_newRecord_success() {
        // Arrange
        PmsmaDTO dto = createPmsmaDTO();
        PMSMA savedPmsma = new PMSMA();
        savedPmsma.setId(1L);

        when(pmsmaRepo.findPMSMAByBenIdAndIsActive(dto.getBenId(), true)).thenReturn(null);
        when(pmsmaRepo.saveAll(anyList())).thenReturn(Arrays.asList(savedPmsma));

        // Act
        String result = service.savePmsmaRecords(Arrays.asList(dto));

        // Assert
        assertEquals("No. of PMSMA records saved: 1", result);
        verify(pmsmaRepo).findPMSMAByBenIdAndIsActive(dto.getBenId(), true);
        verify(pmsmaRepo).saveAll(anyList());
    }

    @Test
    void testSavePmsmaRecords_existingRecord_success() {
        // Arrange
        PmsmaDTO dto = createPmsmaDTO();
        PMSMA existingPmsma = new PMSMA();
        existingPmsma.setId(5L);

        when(pmsmaRepo.findPMSMAByBenIdAndIsActive(dto.getBenId(), true)).thenReturn(existingPmsma);
        when(pmsmaRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingPmsma));

        // Act
        String result = service.savePmsmaRecords(Arrays.asList(dto));

        // Assert
        assertEquals("No. of PMSMA records saved: 1", result);
        verify(pmsmaRepo).findPMSMAByBenIdAndIsActive(dto.getBenId(), true);
        verify(pmsmaRepo).saveAll(anyList());
    }

    @Test
    void testSavePmsmaRecords_exception() {
        // Arrange
        PmsmaDTO dto = createPmsmaDTO();
        when(pmsmaRepo.findPMSMAByBenIdAndIsActive(dto.getBenId(), true))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = service.savePmsmaRecords(Arrays.asList(dto));

        // Assert
        assertNull(result);
    }

    // getPNCVisits tests
    @Test
    void testGetPNCVisits_success() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        String userName = "testUser";
        PNCVisit pncVisit = new PNCVisit();
        pncVisit.setId(1L);
        pncVisit.setBenId(123L);

        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenReturn(userName);
        when(pncVisitRepo.getPNCForPW(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(pncVisit));

        // Act
        List<PNCVisitDTO> result = service.getPNCVisits(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(beneficiaryRepo).getUserName(dto.getAshaId());
        verify(pncVisitRepo).getPNCForPW(userName, dto.getFromDate(), dto.getToDate());
    }

    @Test
    void testGetPNCVisits_exception() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenThrow(new RuntimeException("Database error"));

        // Act
        List<PNCVisitDTO> result = service.getPNCVisits(dto);

        // Assert
        assertNull(result);
    }

    // savePNCVisit tests
    @Test
    void testSavePNCVisit_newRecord_success() {
        // Arrange
        PNCVisitDTO dto = createPNCVisitDTO();
        BenVisitDetail savedVisitDetail = new BenVisitDetail();
        savedVisitDetail.setBenVisitId(1L);

        when(pncVisitRepo.findPNCVisitByBenIdAndPncPeriodAndIsActive(dto.getBenId(), dto.getPncPeriod(), true))
                .thenReturn(null);
        when(beneficiaryRepo.getRegIDFromBenId(dto.getBenId())).thenReturn(100L);
        when(pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(dto.getBenId(), true))
                .thenReturn(null);
        when(benVisitDetailsRepo.save(any(BenVisitDetail.class))).thenReturn(savedVisitDetail);
        when(pncVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList(new PNCVisit()));
        when(pncCareRepo.saveAll(anyList())).thenReturn(Arrays.asList(new PNCCare()));

        // Act
        String result = service.savePNCVisit(Arrays.asList(dto));

        // Assert
        assertEquals("no of pnc details saved: 1", result);
        verify(pncVisitRepo).findPNCVisitByBenIdAndPncPeriodAndIsActive(dto.getBenId(), dto.getPncPeriod(), true);
        verify(beneficiaryRepo).getRegIDFromBenId(dto.getBenId());
        verify(benVisitDetailsRepo).save(any(BenVisitDetail.class));
        verify(pncVisitRepo).saveAll(anyList());
        verify(pncCareRepo).saveAll(anyList());
    }

    @Test
    void testSavePNCVisit_existingRecord_success() {
        // Arrange
        PNCVisitDTO dto = createPNCVisitDTO();
        PNCVisit existingVisit = new PNCVisit();
        existingVisit.setId(5L);

        when(pncVisitRepo.findPNCVisitByBenIdAndPncPeriodAndIsActive(dto.getBenId(), dto.getPncPeriod(), true))
                .thenReturn(existingVisit);
        when(pncVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingVisit));
        when(pncCareRepo.saveAll(anyList())).thenReturn(Arrays.asList());

        // Act
        String result = service.savePNCVisit(Arrays.asList(dto));

        // Assert
        assertEquals("no of pnc details saved: 1", result);
        verify(pncVisitRepo).findPNCVisitByBenIdAndPncPeriodAndIsActive(dto.getBenId(), dto.getPncPeriod(), true);
        verify(pncVisitRepo).saveAll(anyList());
    }

    @Test
    void testSavePNCVisit_exception() {
        // Arrange
        PNCVisitDTO dto = createPNCVisitDTO();
        when(pncVisitRepo.findPNCVisitByBenIdAndPncPeriodAndIsActive(dto.getBenId(), dto.getPncPeriod(), true))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = service.savePNCVisit(Arrays.asList(dto));

        // Assert
        assertNull(result);
    }

    // Helper methods to create test data
    private PregnantWomanDTO createPregnantWomanDTO() {
        PregnantWomanDTO dto = new PregnantWomanDTO();
        dto.setBenId(123L);
        dto.setLmpDate(currentTime);
        dto.setBloodGroup("A+");
        dto.setWeight(60);
        dto.setHeight(160);
        dto.setIsActive(true);
        dto.setCreatedBy("testUser");
        dto.setCreatedDate(currentTime);
        dto.setUpdatedBy("testUser");
        dto.setUpdatedDate(currentTime);
        return dto;
    }

    private GetBenRequestHandler createGetBenRequestHandler() {
        GetBenRequestHandler dto = new GetBenRequestHandler();
        dto.setAshaId(1);
        dto.setFromDate(currentTime);
        dto.setToDate(currentTime);
        return dto;
    }

    private ANCVisitDTO createANCVisitDTO() {
        ANCVisitDTO dto = new ANCVisitDTO();
        dto.setBenId(123L);
        dto.setAncDate(currentTime);
        dto.setAncVisit(1);
        dto.setWeightOfPW(65);
        dto.setBpSystolic(120);
        dto.setBpDiastolic(80);
        dto.setIsActive(true);
        dto.setCreatedDate(currentTime);
        dto.setCreatedBy("testUser");
        dto.setUpdatedDate(currentTime);
        dto.setUpdatedBy("testUser");
        dto.setProviderServiceMapID(1);
        return dto;
    }

    private PmsmaDTO createPmsmaDTO() {
        PmsmaDTO dto = new PmsmaDTO();
        dto.setBenId(123L);
        dto.setRchNumber("RCH123");
        dto.setHusbandName("Test Husband");
        dto.setAddress("Test Address");
        dto.setWeight(65);
        dto.setIsActive(true);
        dto.setCreatedBy("testUser");
        dto.setCreatedDate(currentTime);
        dto.setUpdatedDate(currentTime);
        dto.setUpdatedBy("testUser");
        return dto;
    }

    private PNCVisitDTO createPNCVisitDTO() {
        PNCVisitDTO dto = new PNCVisitDTO();
        dto.setBenId(123L);
        dto.setPncPeriod(0); // First day
        dto.setPncDate(currentTime);
        dto.setIfaTabsGiven(10);
        dto.setAnyContraceptionMethod(false);
        dto.setMotherDeath(false);
        dto.setIsActive(true);
        dto.setCreatedDate(currentTime);
        dto.setCreatedBy("testUser");
        dto.setUpdatedDate(currentTime);
        dto.setUpdatedBy("testUser");
        return dto;
    }
}
