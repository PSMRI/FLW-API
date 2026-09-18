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

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChildCareServiceImplTest {

    @Mock
    private HbycRepo hbycRepo;
    @Mock
    private BeneficiaryRepo beneficiaryRepo;
    @Mock
    private HbncVisitRepo hbncVisitRepo;
    @Mock
    private HbncVisitCardRepo hbncVisitCardRepo;
    @Mock
    private HbncPart1Repo hbncPart1Repo;
    @Mock
    private HbncPart2repo hbncPart2repo;
    @Mock
    private IncentivesRepo incentivesRepo;
    @Mock
    private UserServiceRoleRepo userRepo;
    @Mock
    private IncentiveRecordRepo recordRepo;
    @Mock
    private ChildVaccinationRepo childVaccinationRepo;
    @Mock
    private VaccineRepo vaccineRepo;

    @InjectMocks
    private ChildCareServiceImpl service;

    private Timestamp currentTime;

    @BeforeEach
    void setUp() {
        currentTime = Timestamp.from(Instant.now());
    }

    // registerHBYC tests
    @Test
    void testRegisterHBYC_newRecord_success() {
        // Arrange
        HbycDTO dto = createHbycDTO();
        HBYC savedHbyc = new HBYC();
        savedHbyc.setId(1L);

        when(hbycRepo.findHBYCByBenIdAndCreatedDate(dto.getBenId(), dto.getCreatedDate()))
                .thenReturn(null);
        when(hbycRepo.saveAll(anyList())).thenReturn(Arrays.asList(savedHbyc));

        // Act
        String result = service.registerHBYC(Arrays.asList(dto));

        // Assert
        assertEquals("no of hbyc details saved: 1", result);
        verify(hbycRepo).findHBYCByBenIdAndCreatedDate(dto.getBenId(), dto.getCreatedDate());
        verify(hbycRepo).saveAll(anyList());
    }

    @Test
    void testRegisterHBYC_existingRecord_success() {
        // Arrange
        HbycDTO dto = createHbycDTO();
        HBYC existingHbyc = new HBYC();
        existingHbyc.setId(5L);

        when(hbycRepo.findHBYCByBenIdAndCreatedDate(dto.getBenId(), dto.getCreatedDate()))
                .thenReturn(existingHbyc);
        when(hbycRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingHbyc));

        // Act
        String result = service.registerHBYC(Arrays.asList(dto));

        // Assert
        assertEquals("no of hbyc details saved: 1", result);
        verify(hbycRepo).findHBYCByBenIdAndCreatedDate(dto.getBenId(), dto.getCreatedDate());
        verify(hbycRepo).saveAll(anyList());
    }

    @Test
    void testRegisterHBYC_exception() {
        // Arrange
        HbycDTO dto = createHbycDTO();
        when(hbycRepo.findHBYCByBenIdAndCreatedDate(dto.getBenId(), dto.getCreatedDate()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = service.registerHBYC(Arrays.asList(dto));

        // Assert
        assertNull(result);
    }

    // getHbycRecords tests
    @Test
    void testGetHbycRecords_success() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        String userName = "testUser";
        HBYC hbyc = new HBYC();
        hbyc.setId(1L);
        hbyc.setBenId(123L);

        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenReturn(userName);
        when(hbycRepo.getAllHbycByBenId(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(hbyc));

        // Act
        List<HbycDTO> result = service.getHbycRecords(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(beneficiaryRepo).getUserName(dto.getAshaId());
        verify(hbycRepo).getAllHbycByBenId(userName, dto.getFromDate(), dto.getToDate());
    }

    @Test
    void testGetHbycRecords_exception() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenThrow(new RuntimeException("Database error"));

        // Act
        List<HbycDTO> result = service.getHbycRecords(dto);

        // Assert
        assertNull(result);
    }

    // getHBNCDetails tests
    @Test
    void testGetHBNCDetails_success() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        String userName = "testUser";
        
        HbncVisit hbncVisit = new HbncVisit();
        hbncVisit.setId(1L);
        hbncVisit.setBenId(123L);
        hbncVisit.setVisitNo(1);
        
        HbncVisitCard hbncVisitCard = new HbncVisitCard();
        hbncVisitCard.setId(2L);
        hbncVisitCard.setBenId(124L);
        hbncVisitCard.setVisitNo(2);
        
        HbncPart1 hbncPart1 = new HbncPart1();
        hbncPart1.setId(3L);
        hbncPart1.setBenId(125L);
        hbncPart1.setVisitNo(1);
        
        HbncPart2 hbncPart2 = new HbncPart2();
        hbncPart2.setId(4L);
        hbncPart2.setBenId(126L);
        hbncPart2.setVisitNo(2);

        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenReturn(userName);
        when(hbncVisitRepo.getHbncVisitDetails(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(hbncVisit));
        when(hbncVisitCardRepo.getHbncVisitCardDetails(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(hbncVisitCard));
        when(hbncPart1Repo.getHbncPart1Details(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(hbncPart1));
        when(hbncPart2repo.getHbncPart2Details(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(hbncPart2));

        // Act
        List<HbncRequestDTO> result = service.getHBNCDetails(dto);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        verify(beneficiaryRepo).getUserName(dto.getAshaId());
        verify(hbncVisitRepo).getHbncVisitDetails(userName, dto.getFromDate(), dto.getToDate());
        verify(hbncVisitCardRepo).getHbncVisitCardDetails(userName, dto.getFromDate(), dto.getToDate());
        verify(hbncPart1Repo).getHbncPart1Details(userName, dto.getFromDate(), dto.getToDate());
        verify(hbncPart2repo).getHbncPart2Details(userName, dto.getFromDate(), dto.getToDate());
    }

    @Test
    void testGetHBNCDetails_exception() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenThrow(new RuntimeException("Database error"));

        // Act
        List<HbncRequestDTO> result = service.getHBNCDetails(dto);

        // Assert
        assertNull(result);
    }

    // saveHBNCDetails tests
    @Test
    void testSaveHBNCDetails_hbncVisitDTO_newRecord_success() {
        // Arrange
        HbncRequestDTO requestDTO = createHbncRequestDTOWithVisit();
        when(hbncVisitRepo.findHbncVisitByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate()))
                .thenReturn(null);
        when(hbncVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList(new HbncVisit()));
        when(hbncVisitCardRepo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncPart1Repo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncPart2repo.saveAll(anyList())).thenReturn(Arrays.asList());

        // Act
        String result = service.saveHBNCDetails(Arrays.asList(requestDTO));

        // Assert
        assertEquals("no of hbnc details saved: 1", result);
        verify(hbncVisitRepo).findHbncVisitByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate());
        verify(hbncVisitRepo).saveAll(anyList());
    }

    @Test
    void testSaveHBNCDetails_hbncVisitDTO_existingRecord_success() {
        // Arrange
        HbncRequestDTO requestDTO = createHbncRequestDTOWithVisit();
        HbncVisit existingVisit = new HbncVisit();
        existingVisit.setId(5L);
        
        when(hbncVisitRepo.findHbncVisitByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate()))
                .thenReturn(existingVisit);
        when(hbncVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingVisit));
        when(hbncVisitCardRepo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncPart1Repo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncPart2repo.saveAll(anyList())).thenReturn(Arrays.asList());

        // Act
        String result = service.saveHBNCDetails(Arrays.asList(requestDTO));

        // Assert
        assertEquals("no of hbnc details saved: 1", result);
        verify(hbncVisitRepo).findHbncVisitByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate());
        verify(hbncVisitRepo).saveAll(anyList());
    }

    @Test
    void testSaveHBNCDetails_hbncVisitCardDTO_newRecord_success() {
        // Arrange
        HbncRequestDTO requestDTO = createHbncRequestDTOWithVisitCard();
        when(hbncVisitCardRepo.findHbncVisitCardByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate()))
                .thenReturn(null);
        when(hbncVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncVisitCardRepo.saveAll(anyList())).thenReturn(Arrays.asList(new HbncVisitCard()));
        when(hbncPart1Repo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncPart2repo.saveAll(anyList())).thenReturn(Arrays.asList());

        // Act
        String result = service.saveHBNCDetails(Arrays.asList(requestDTO));

        // Assert
        assertEquals("no of hbnc details saved: 1", result);
        verify(hbncVisitCardRepo).findHbncVisitCardByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate());
        verify(hbncVisitCardRepo).saveAll(anyList());
    }

    @Test
    void testSaveHBNCDetails_hbncPart1DTO_newRecord_success() {
        // Arrange
        HbncRequestDTO requestDTO = createHbncRequestDTOWithPart1();
        when(hbncPart1Repo.findHbncPart1ByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate()))
                .thenReturn(null);
        when(hbncVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncVisitCardRepo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncPart1Repo.saveAll(anyList())).thenReturn(Arrays.asList(new HbncPart1()));
        when(hbncPart2repo.saveAll(anyList())).thenReturn(Arrays.asList());

        // Act
        String result = service.saveHBNCDetails(Arrays.asList(requestDTO));

        // Assert
        assertEquals("no of hbnc details saved: 1", result);
        verify(hbncPart1Repo).findHbncPart1ByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate());
        verify(hbncPart1Repo).saveAll(anyList());
    }

    @Test
    void testSaveHBNCDetails_hbncPart2DTO_newRecord_success() {
        // Arrange
        HbncRequestDTO requestDTO = createHbncRequestDTOWithPart2();
        when(hbncPart2repo.findHbncPart2ByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate()))
                .thenReturn(null);
        when(hbncVisitRepo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncVisitCardRepo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncPart1Repo.saveAll(anyList())).thenReturn(Arrays.asList());
        when(hbncPart2repo.saveAll(anyList())).thenReturn(Arrays.asList(new HbncPart2()));

        // Act
        String result = service.saveHBNCDetails(Arrays.asList(requestDTO));

        // Assert
        assertEquals("no of hbnc details saved: 1", result);
        verify(hbncPart2repo).findHbncPart2ByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate());
        verify(hbncPart2repo).saveAll(anyList());
    }

    @Test
    void testSaveHBNCDetails_exception() {
        // Arrange
        HbncRequestDTO requestDTO = createHbncRequestDTOWithVisit();
        when(hbncVisitRepo.findHbncVisitByBenIdAndVisitNo(requestDTO.getBenId(), requestDTO.getHomeVisitDate()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = service.saveHBNCDetails(Arrays.asList(requestDTO));

        // Assert
        assertNull(result);
    }

    // getChildVaccinationDetails tests
    @Test
    void testGetChildVaccinationDetails_success() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        String userName = "testUser";
        ChildVaccination vaccination = new ChildVaccination();
        vaccination.setId(1L);
        vaccination.setBeneficiaryRegId(100L);
        vaccination.setVaccineName("BCG");

        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenReturn(userName);
        when(childVaccinationRepo.getChildVaccinationDetails(userName, dto.getFromDate(), dto.getToDate()))
                .thenReturn(Arrays.asList(vaccination));
        when(beneficiaryRepo.getBenIdFromRegID(vaccination.getBeneficiaryRegId()))
                .thenReturn(BigInteger.valueOf(123L));

        // Act
        List<ChildVaccinationDTO> result = service.getChildVaccinationDetails(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123L, result.get(0).getBeneficiaryId());
        verify(beneficiaryRepo).getUserName(dto.getAshaId());
        verify(childVaccinationRepo).getChildVaccinationDetails(userName, dto.getFromDate(), dto.getToDate());
        verify(beneficiaryRepo).getBenIdFromRegID(vaccination.getBeneficiaryRegId());
    }

    @Test
    void testGetChildVaccinationDetails_exception() {
        // Arrange
        GetBenRequestHandler dto = createGetBenRequestHandler();
        when(beneficiaryRepo.getUserName(dto.getAshaId())).thenThrow(new RuntimeException("Database error"));

        // Act
        List<ChildVaccinationDTO> result = service.getChildVaccinationDetails(dto);

        // Assert
        assertNull(result);
    }

    // saveChildVaccinationDetails tests
    @Test
    void testSaveChildVaccinationDetails_newRecord_success() {
        // Arrange
        ChildVaccinationDTO dto = createChildVaccinationDTO();
        Long benRegId = 100L;
        ChildVaccination savedVaccination = new ChildVaccination();
        savedVaccination.setId(1L);
        savedVaccination.setBeneficiaryRegId(benRegId);
        savedVaccination.setVaccineId(dto.getVaccineId());
        savedVaccination.setCreatedBy(dto.getCreatedBy());
        savedVaccination.setCreatedDate(dto.getCreatedDate());

        when(beneficiaryRepo.getRegIDFromBenId(dto.getBeneficiaryId())).thenReturn(benRegId);
        when(childVaccinationRepo.findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(
                benRegId, dto.getCreatedDate(), dto.getVaccineName()))
                .thenReturn(null);
        when(childVaccinationRepo.saveAll(anyList())).thenReturn(Arrays.asList(savedVaccination));
        // Mocks for checkAndAddIncentives method
        when(beneficiaryRepo.getBenIdFromRegID(benRegId)).thenReturn(BigInteger.valueOf(dto.getBeneficiaryId()));
        when(userRepo.getUserIdByName(dto.getCreatedBy())).thenReturn(1);
        when(vaccineRepo.getImmunizationServiceIdByVaccineId(dto.getVaccineId().shortValue())).thenReturn(1);
        // Additional mocks for incentive logic (immunizationServiceId < 6)
        IncentiveActivity incentiveActivity = new IncentiveActivity();
        incentiveActivity.setId(1L);
        incentiveActivity.setName("IMMUNIZATION_0_1");
        incentiveActivity.setRate(100);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_0_1", "IMMUNIZATION")).thenReturn(incentiveActivity);
        when(childVaccinationRepo.getFirstYearVaccineCountForBenId(benRegId)).thenReturn(5);
        when(childVaccinationRepo.getFirstYearVaccineCount()).thenReturn(5);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(eq(1L), any(Timestamp.class), eq(dto.getBeneficiaryId()))).thenReturn(null);
        when(recordRepo.save(any(IncentiveActivityRecord.class))).thenReturn(new IncentiveActivityRecord());

        // Act
        String result = service.saveChildVaccinationDetails(Arrays.asList(dto));

        // Assert
        assertEquals("No of child vaccination details saved: 1", result);
        verify(beneficiaryRepo).getRegIDFromBenId(dto.getBeneficiaryId());
        verify(childVaccinationRepo).findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(
                benRegId, dto.getCreatedDate(), dto.getVaccineName());
        verify(childVaccinationRepo).saveAll(anyList());
    }

    @Test
    void testSaveChildVaccinationDetails_existingRecord_success() {
        // Arrange
        ChildVaccinationDTO dto = createChildVaccinationDTO();
        Long benRegId = 100L;
        ChildVaccination existingVaccination = new ChildVaccination();
        existingVaccination.setId(5L);
        existingVaccination.setBeneficiaryRegId(benRegId);
        existingVaccination.setVaccineId(dto.getVaccineId());
        existingVaccination.setCreatedBy(dto.getCreatedBy());
        existingVaccination.setCreatedDate(dto.getCreatedDate());

        when(beneficiaryRepo.getRegIDFromBenId(dto.getBeneficiaryId())).thenReturn(benRegId);
        when(childVaccinationRepo.findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(
                benRegId, dto.getCreatedDate(), dto.getVaccineName()))
                .thenReturn(existingVaccination);
        when(childVaccinationRepo.saveAll(anyList())).thenReturn(Arrays.asList(existingVaccination));
        // Mocks for checkAndAddIncentives method
        when(beneficiaryRepo.getBenIdFromRegID(benRegId)).thenReturn(BigInteger.valueOf(dto.getBeneficiaryId()));
        when(userRepo.getUserIdByName(dto.getCreatedBy())).thenReturn(1);
        when(vaccineRepo.getImmunizationServiceIdByVaccineId(dto.getVaccineId().shortValue())).thenReturn(1);
        // Additional mocks for incentive logic (immunizationServiceId < 6)
        IncentiveActivity incentiveActivity = new IncentiveActivity();
        incentiveActivity.setId(1L);
        incentiveActivity.setName("IMMUNIZATION_0_1");
        incentiveActivity.setRate(100);
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_0_1", "IMMUNIZATION")).thenReturn(incentiveActivity);
        when(childVaccinationRepo.getFirstYearVaccineCountForBenId(benRegId)).thenReturn(5);
        when(childVaccinationRepo.getFirstYearVaccineCount()).thenReturn(5);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(eq(1L), any(Timestamp.class), eq(dto.getBeneficiaryId()))).thenReturn(null);
        when(recordRepo.save(any(IncentiveActivityRecord.class))).thenReturn(new IncentiveActivityRecord());

        // Act
        String result = service.saveChildVaccinationDetails(Arrays.asList(dto));

        // Assert
        assertEquals("No of child vaccination details saved: 1", result);
        verify(beneficiaryRepo).getRegIDFromBenId(dto.getBeneficiaryId());
        verify(childVaccinationRepo).findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(
                benRegId, dto.getCreatedDate(), dto.getVaccineName());
        verify(childVaccinationRepo).saveAll(anyList());
    }

    @Test
    void testSaveChildVaccinationDetails_withIncentives_firstYear_success() {
        // Arrange
        ChildVaccinationDTO dto = createChildVaccinationDTO();
        dto.setVaccineId(1); // First year vaccine
        Long benRegId = 100L;
        ChildVaccination savedVaccination = new ChildVaccination();
        savedVaccination.setId(1L);
        savedVaccination.setBeneficiaryRegId(benRegId);
        savedVaccination.setVaccineId(1);
        savedVaccination.setCreatedDate(currentTime);
        savedVaccination.setCreatedBy("testUser");

        IncentiveActivity incentiveActivity = new IncentiveActivity();
        incentiveActivity.setId(1L);
        incentiveActivity.setRate(100);

        when(beneficiaryRepo.getRegIDFromBenId(dto.getBeneficiaryId())).thenReturn(benRegId);
        when(childVaccinationRepo.findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(
                benRegId, dto.getCreatedDate(), dto.getVaccineName()))
                .thenReturn(null);
        when(childVaccinationRepo.saveAll(anyList())).thenReturn(Arrays.asList(savedVaccination));
        when(beneficiaryRepo.getBenIdFromRegID(benRegId)).thenReturn(BigInteger.valueOf(123L));
        when(userRepo.getUserIdByName("testUser")).thenReturn(1);
        when(vaccineRepo.getImmunizationServiceIdByVaccineId((short) 1)).thenReturn(1); // First year vaccine
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_0_1", "IMMUNIZATION"))
                .thenReturn(incentiveActivity);
        when(childVaccinationRepo.getFirstYearVaccineCountForBenId(benRegId)).thenReturn(5);
        when(childVaccinationRepo.getFirstYearVaccineCount()).thenReturn(5);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(1L, currentTime, 123L))
                .thenReturn(null);

        // Act
        String result = service.saveChildVaccinationDetails(Arrays.asList(dto));

        // Assert
        assertEquals("No of child vaccination details saved: 1", result);
        verify(recordRepo).save(any(IncentiveActivityRecord.class));
    }

    @Test
    void testSaveChildVaccinationDetails_withIncentives_secondYear_success() {
        // Arrange
        ChildVaccinationDTO dto = createChildVaccinationDTO();
        dto.setVaccineId(7); // Second year vaccine
        Long benRegId = 100L;
        ChildVaccination savedVaccination = new ChildVaccination();
        savedVaccination.setId(1L);
        savedVaccination.setBeneficiaryRegId(benRegId);
        savedVaccination.setVaccineId(7);
        savedVaccination.setCreatedDate(currentTime);
        savedVaccination.setCreatedBy("testUser");

        IncentiveActivity incentiveActivity = new IncentiveActivity();
        incentiveActivity.setId(2L);
        incentiveActivity.setRate(150);

        when(beneficiaryRepo.getRegIDFromBenId(dto.getBeneficiaryId())).thenReturn(benRegId);
        when(childVaccinationRepo.findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(
                benRegId, dto.getCreatedDate(), dto.getVaccineName()))
                .thenReturn(null);
        when(childVaccinationRepo.saveAll(anyList())).thenReturn(Arrays.asList(savedVaccination));
        when(beneficiaryRepo.getBenIdFromRegID(benRegId)).thenReturn(BigInteger.valueOf(123L));
        when(userRepo.getUserIdByName("testUser")).thenReturn(1);
        when(vaccineRepo.getImmunizationServiceIdByVaccineId((short) 7)).thenReturn(7); // Second year vaccine
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_1_2", "IMMUNIZATION"))
                .thenReturn(incentiveActivity);
        when(childVaccinationRepo.getSecondYearVaccineCountForBenId(benRegId)).thenReturn(3);
        when(childVaccinationRepo.getSecondYearVaccineCount()).thenReturn(3);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(2L, currentTime, 123L))
                .thenReturn(null);

        // Act
        String result = service.saveChildVaccinationDetails(Arrays.asList(dto));

        // Assert
        assertEquals("No of child vaccination details saved: 1", result);
        verify(recordRepo).save(any(IncentiveActivityRecord.class));
    }

    @Test
    void testSaveChildVaccinationDetails_withIncentives_fifthYear_success() {
        // Arrange
        ChildVaccinationDTO dto = createChildVaccinationDTO();
        dto.setVaccineId(8); // Fifth year vaccine
        Long benRegId = 100L;
        ChildVaccination savedVaccination = new ChildVaccination();
        savedVaccination.setId(1L);
        savedVaccination.setBeneficiaryRegId(benRegId);
        savedVaccination.setVaccineId(8);
        savedVaccination.setCreatedDate(currentTime);
        savedVaccination.setCreatedBy("testUser");

        IncentiveActivity incentiveActivity = new IncentiveActivity();
        incentiveActivity.setId(3L);
        incentiveActivity.setRate(200);

        when(beneficiaryRepo.getRegIDFromBenId(dto.getBeneficiaryId())).thenReturn(benRegId);
        when(childVaccinationRepo.findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(
                benRegId, dto.getCreatedDate(), dto.getVaccineName()))
                .thenReturn(null);
        when(childVaccinationRepo.saveAll(anyList())).thenReturn(Arrays.asList(savedVaccination));
        when(beneficiaryRepo.getBenIdFromRegID(benRegId)).thenReturn(BigInteger.valueOf(123L));
        when(userRepo.getUserIdByName("testUser")).thenReturn(1);
        when(vaccineRepo.getImmunizationServiceIdByVaccineId((short) 8)).thenReturn(8); // Fifth year vaccine
        when(incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_5", "IMMUNIZATION"))
                .thenReturn(incentiveActivity);
        when(childVaccinationRepo.checkDptVaccinatedUser(benRegId)).thenReturn(1);
        when(recordRepo.findRecordByActivityIdCreatedDateBenId(3L, currentTime, 123L))
                .thenReturn(null);

        // Act
        String result = service.saveChildVaccinationDetails(Arrays.asList(dto));

        // Assert
        assertEquals("No of child vaccination details saved: 1", result);
        verify(recordRepo).save(any(IncentiveActivityRecord.class));
    }

    @Test
    void testSaveChildVaccinationDetails_exception() {
        // Arrange
        ChildVaccinationDTO dto = createChildVaccinationDTO();
        when(beneficiaryRepo.getRegIDFromBenId(dto.getBeneficiaryId()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        String result = service.saveChildVaccinationDetails(Arrays.asList(dto));

        // Assert
        assertNull(result);
    }

    // getAllChildVaccines tests
    @Test
    void testGetAllChildVaccines_success() {
        // Arrange
        String category = "CHILD";
        Vaccine vaccine1 = createVaccine("Birth Dose Vaccines");
        Vaccine vaccine2 = createVaccine("6 Weeks Vaccines");
        Vaccine vaccine3 = createVaccine("10 Weeks Vaccines");
        Vaccine vaccine4 = createVaccine("14 Weeks Vaccines");
        Vaccine vaccine5 = createVaccine("9-12 Months");
        Vaccine vaccine6 = createVaccine("16-24 Months Vaccines");
        Vaccine vaccine7 = createVaccine("5-6 Years Vaccine");
        Vaccine vaccine8 = createVaccine("10 Years Vaccine");
        Vaccine vaccine9 = createVaccine("16 Years Vaccine");
        Vaccine vaccine10 = createVaccine("Unknown Category");

        when(vaccineRepo.getAllByCategory(category)).thenReturn(Arrays.asList(
                vaccine1, vaccine2, vaccine3, vaccine4, vaccine5,
                vaccine6, vaccine7, vaccine8, vaccine9, vaccine10
        ));

        // Act
        List<VaccineDTO> result = service.getAllChildVaccines(category);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.size());
        
        // Verify immunization service mappings
        assertEquals("BIRTH", result.get(0).getImmunizationService());
        assertEquals("WEEK_6", result.get(1).getImmunizationService());
        assertEquals("WEEK_10", result.get(2).getImmunizationService());
        assertEquals("WEEK_14", result.get(3).getImmunizationService());
        assertEquals("MONTH_9_12", result.get(4).getImmunizationService());
        assertEquals("MONTH_16_24", result.get(5).getImmunizationService());
        assertEquals("YEAR_5_6", result.get(6).getImmunizationService());
        assertEquals("YEAR_10", result.get(7).getImmunizationService());
        assertEquals("YEAR_16", result.get(8).getImmunizationService());
        assertEquals("CATCH_UP", result.get(9).getImmunizationService());
        
        verify(vaccineRepo).getAllByCategory(category);
    }

    @Test
    void testGetAllChildVaccines_exception() {
        // Arrange
        String category = "CHILD";
        when(vaccineRepo.getAllByCategory(category)).thenThrow(new RuntimeException("Database error"));

        // Act
        List<VaccineDTO> result = service.getAllChildVaccines(category);

        // Assert
        assertNull(result);
    }

    // Helper methods to create test data
    private HbycDTO createHbycDTO() {
        HbycDTO dto = new HbycDTO();
        dto.setBenId(123L);
        dto.setCreatedDate(currentTime);
        dto.setCreatedBy("testUser");
        dto.setUpdatedDate(currentTime);
        dto.setUpdatedBy("testUser");
        return dto;
    }

    private GetBenRequestHandler createGetBenRequestHandler() {
        GetBenRequestHandler dto = new GetBenRequestHandler();
        dto.setAshaId(1);
        dto.setFromDate(currentTime);
        dto.setToDate(currentTime);
        return dto;
    }

    private HbncRequestDTO createHbncRequestDTOWithVisit() {
        HbncRequestDTO dto = new HbncRequestDTO();
        dto.setId(1L);
        dto.setBenId(123L);
        dto.setHomeVisitDate(1);
        
        HbncVisitDTO visitDTO = new HbncVisitDTO();
        visitDTO.setBenId(123L);
        visitDTO.setVisitNo(1);
        dto.setHbncVisitDTO(visitDTO);
        
        return dto;
    }

    private HbncRequestDTO createHbncRequestDTOWithVisitCard() {
        HbncRequestDTO dto = new HbncRequestDTO();
        dto.setId(2L);
        dto.setBenId(124L);
        dto.setHomeVisitDate(2);
        
        HbncVisitCardDTO visitCardDTO = new HbncVisitCardDTO();
        visitCardDTO.setBenId(124L);
        visitCardDTO.setVisitNo(2);
        dto.setHbncVisitCardDTO(visitCardDTO);
        
        return dto;
    }

    private HbncRequestDTO createHbncRequestDTOWithPart1() {
        HbncRequestDTO dto = new HbncRequestDTO();
        dto.setId(3L);
        dto.setBenId(125L);
        dto.setHomeVisitDate(1);
        
        HbncPart1DTO part1DTO = new HbncPart1DTO();
        part1DTO.setBenId(125L);
        part1DTO.setVisitNo(1);
        dto.setHbncPart1DTO(part1DTO);
        
        return dto;
    }

    private HbncRequestDTO createHbncRequestDTOWithPart2() {
        HbncRequestDTO dto = new HbncRequestDTO();
        dto.setId(4L);
        dto.setBenId(126L);
        dto.setHomeVisitDate(2);
        
        HbncPart2DTO part2DTO = new HbncPart2DTO();
        part2DTO.setBenId(126L);
        part2DTO.setVisitNo(2);
        dto.setHbncPart2DTO(part2DTO);
        
        return dto;
    }

    private ChildVaccinationDTO createChildVaccinationDTO() {
        ChildVaccinationDTO dto = new ChildVaccinationDTO();
        dto.setBeneficiaryId(123L);
        dto.setVaccineName("BCG");
        dto.setVaccineId(1);
        dto.setCreatedDate(currentTime);
        dto.setCreatedBy("testUser");
        dto.setLastModDate(currentTime);
        dto.setModifiedBy("testUser");
        return dto;
    }

    private Vaccine createVaccine(String immunizationService) {
        Vaccine vaccine = new Vaccine();
        vaccine.setVaccineId((short) 1);
        vaccine.setVaccineName("Test Vaccine");
        vaccine.setImmunizationService(immunizationService);
        return vaccine;
    }
}
