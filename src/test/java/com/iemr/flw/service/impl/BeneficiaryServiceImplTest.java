package com.iemr.flw.service.impl;

import com.iemr.flw.domain.identity.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.identity.HouseHoldRepo;
import com.iemr.flw.utils.config.ConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficiaryServiceImplTest {

    @Mock
    private BeneficiaryRepo beneficiaryRepo;

    @Mock
    private HouseHoldRepo houseHoldRepo;

    @InjectMocks
    private BeneficiaryServiceImpl service;

    private GetBenRequestHandler request;
    private RMNCHMBeneficiaryaddress address;
    private RMNCHMBeneficiarymapping mapping;
    private final String authorization = "Bearer test-token";
    private final Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    @BeforeEach
    void setUp() {
        // Set the property value for page size
        ReflectionTestUtils.setField(service, "door_to_door_page_size", "10");
        
        // Setup common test data
        request = new GetBenRequestHandler();
        request.setAshaId(1);
        request.setPageNo(0);
        request.setUserName("testUser");

        address = createTestAddress();
        mapping = createTestMapping();
    }

    // getBenData tests
    @Test
    void testGetBenData_withValidRequest_success() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        setupMappingDataMocks();
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"data\""));
        assertTrue(result.contains("\"pageSize\":10"));
        assertTrue(result.contains("\"totalPage\":1"));

        // Verify repository calls
        verify(beneficiaryRepo).getUserName(request.getAshaId());
        verify(beneficiaryRepo).getBenDataByUser("testUser", pageRequest);
        verify(beneficiaryRepo).getByAddressID(address.getId());
    }

    @Test
    void testGetBenData_withDateRange_success() throws Exception {
        // Arrange
        Timestamp fromDate = Timestamp.valueOf(LocalDate.now().minusDays(7).atStartOfDay());
        Timestamp toDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataWithinDates("testUser", fromDate, toDate, pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        setupMappingDataMocks();
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"data\""));

        // Verify repository calls
        verify(beneficiaryRepo).getUserName(request.getAshaId());
        verify(beneficiaryRepo).getBenDataWithinDates("testUser", fromDate, toDate, pageRequest);
    }

    @Test
    void testGetBenData_nullAshaId_throwsException() {
        // Arrange
        request.setAshaId(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            service.getBenData(request, authorization);
        });
        assertEquals("Invalid/missing village details", exception.getMessage());
    }

    @Test
    void testGetBenData_invalidPageNo_throwsException() {
        // Arrange
        request.setPageNo(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            service.getBenData(request, authorization);
        });
        assertEquals("Invalid page no", exception.getMessage());
    }

    @Test
    void testGetBenData_userNameNotFound_throwsException() {
        // Arrange
        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            service.getBenData(request, authorization);
        });
        assertEquals("Asha details not found, please contact administrator", exception.getMessage());

        verify(beneficiaryRepo).getUserName(request.getAshaId());
    }

    @Test
    void testGetBenData_emptyUserName_throwsException() {
        // Arrange
        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            service.getBenData(request, authorization);
        });
        assertEquals("Asha details not found, please contact administrator", exception.getMessage());
    }

    @Test
    void testGetBenData_noResultsFound_returnsNull() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> emptyList = new ArrayList<>();
        Page<RMNCHMBeneficiaryaddress> emptyPage = new PageImpl<>(emptyList, pageRequest, 0);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(emptyPage);

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetBenData_repositoryException_throwsException() {
        // Arrange
        when(beneficiaryRepo.getUserName(request.getAshaId())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            service.getBenData(request, authorization);
        });
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testGetBenData_nullMapping_skipsProcessing() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(null);

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"data\":[]"));
        verify(beneficiaryRepo).getByAddressID(address.getId());
    }

    @Test
    void testGetBenData_withCompleteMapping_success() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        setupCompleteEntityMocks();
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"beneficiaryDetails\""));
        assertTrue(result.contains("\"householdDetails\""));
        assertTrue(result.contains("\"bornbirthDeatils\""));

        // Verify all entity retrievals
        verify(beneficiaryRepo).getDetailsById(mapping.getBenDetailsId());
        verify(beneficiaryRepo).getAccountById(mapping.getBenAccountID());
        verify(beneficiaryRepo).getImageById(mapping.getBenImageId().longValue());
        verify(beneficiaryRepo).getAddressById(mapping.getBenAddressId());
        verify(beneficiaryRepo).getContactById(mapping.getBenContactsId());
    }

    @Test
    void testGetBenData_ageCalculation_years() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        // Create detail with DOB 25 years ago
        RMNCHMBeneficiarydetail detail = createTestBeneficiaryDetail();
        LocalDate birthDate = LocalDate.now().minusYears(25).minusMonths(3);
        detail.setDob(Timestamp.valueOf(birthDate.atStartOfDay()));
        
        when(beneficiaryRepo.getDetailsById(mapping.getBenDetailsId())).thenReturn(detail);
        setupOtherEntityMocks();
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Years"));
    }

    @Test
    void testGetBenData_ageCalculation_months() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        // Create detail with DOB 6 months ago
        RMNCHMBeneficiarydetail detail = createTestBeneficiaryDetail();
        LocalDate birthDate = LocalDate.now().minusMonths(6).minusDays(15);
        detail.setDob(Timestamp.valueOf(birthDate.atStartOfDay()));
        
        when(beneficiaryRepo.getDetailsById(mapping.getBenDetailsId())).thenReturn(detail);
        setupOtherEntityMocks();
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Months") || result.contains("Month"));
    }

    @Test
    void testGetBenData_ageCalculation_days() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        // Create detail with DOB 15 days ago
        RMNCHMBeneficiarydetail detail = createTestBeneficiaryDetail();
        LocalDate birthDate = LocalDate.now().minusDays(15);
        detail.setDob(Timestamp.valueOf(birthDate.atStartOfDay()));
        
        when(beneficiaryRepo.getDetailsById(mapping.getBenDetailsId())).thenReturn(detail);
        setupOtherEntityMocks();
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Days") || result.contains("Day"));
    }

    @Test
    void testGetBenData_relatedBeneficiaryIds_parsing() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        // Create RMNCH details with related beneficiary IDs
        RMNCHBeneficiaryDetailsRmnch rmnchDetails = createTestRMNCHBeneficiaryDetails();
        rmnchDetails.setRelatedBeneficiaryIdsDB("123,456,789");
        
        when(beneficiaryRepo.getDetailsByRegID(mapping.getBenRegId().longValue())).thenReturn(rmnchDetails);
        
        // Setup minimal required mocks (avoid unnecessary stubbings)
        when(beneficiaryRepo.getDetailsById(mapping.getBenDetailsId())).thenReturn(createTestBeneficiaryDetail());
        when(beneficiaryRepo.getBenIdFromRegID(mapping.getBenRegId().longValue())).thenReturn(BigInteger.valueOf(1000L));
        when(beneficiaryRepo.getBornBirthByRegID(mapping.getBenRegId().longValue())).thenReturn(createTestBornBirthDetails());
        when(houseHoldRepo.getByHouseHoldID(rmnchDetails.getHouseoldId())).thenReturn(createTestHouseHoldDetails());
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        verify(beneficiaryRepo).getDetailsByRegID(mapping.getBenRegId().longValue());
    }

    @Test
    void testGetBenData_withHealthIdFetch_success() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        setupMappingDataMocks();
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"data\""));
        // Note: Health ID fetch happens in a try-catch block and doesn't affect the main flow
    }

    @Test
    void testGetBenData_healthIdFetchException_continuesProcessing() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        setupMappingDataMocks();
        setupHealthDetailsMocks();

        // Mock ConfigProperties to throw exception
        try (MockedStatic<ConfigProperties> configMock = mockStatic(ConfigProperties.class)) {
            configMock.when(() -> ConfigProperties.getPropertyByName(anyString())).thenThrow(new RuntimeException("Config error"));

            // Act
            String result = service.getBenData(request, authorization);

            // Assert - Should still return valid result despite health ID fetch failure
            assertNotNull(result);
            assertTrue(result.contains("\"data\""));
        }
    }

    @Test
    void testGetBenData_getBenHealthDetails_withValidData() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        setupMappingDataMocks();

        // Setup health details with valid data
        Object[] healthIdData = {"test-health-id", "12345"};
        Object[] healthIdNumber = {healthIdData};
        when(beneficiaryRepo.getBenHealthIdNumber(mapping.getBenRegId())).thenReturn(healthIdNumber);
        
        List<Object[]> healthDetailsList = new ArrayList<>();
        Object[] healthDetails = {"health-id-123", "health-number-456", true};
        healthDetailsList.add(healthDetails);
        when(beneficiaryRepo.getBenHealthDetails("test-health-id")).thenReturn(new ArrayList<>(healthDetailsList));

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"abhaHealthDetails\""));
        verify(beneficiaryRepo).getBenHealthIdNumber(mapping.getBenRegId());
        verify(beneficiaryRepo).getBenHealthDetails("test-health-id");
    }

    @Test
    void testGetBenData_getBenHealthDetails_emptyHealthDetails() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        setupMappingDataMocks();

        // Setup health details with valid health ID but no health details
        Object[] healthIdData = {"test-health-id", "12345"};
        Object[] healthIdNumber = {healthIdData};
        when(beneficiaryRepo.getBenHealthIdNumber(mapping.getBenRegId())).thenReturn(healthIdNumber);
        when(beneficiaryRepo.getBenHealthDetails("test-health-id")).thenReturn(new ArrayList<>());

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        verify(beneficiaryRepo).getBenHealthIdNumber(mapping.getBenRegId());
        verify(beneficiaryRepo).getBenHealthDetails("test-health-id");
    }

    @Test
    void testGetBenData_getBenHealthDetails_nullHealthIdNumber() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        setupMappingDataMocks();

        // Setup health details with null health ID number
        Object[] healthIdData = {null, "12345"};
        Object[] healthIdNumber = {healthIdData};
        when(beneficiaryRepo.getBenHealthIdNumber(mapping.getBenRegId())).thenReturn(healthIdNumber);

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        verify(beneficiaryRepo).getBenHealthIdNumber(mapping.getBenRegId());
        verify(beneficiaryRepo, never()).getBenHealthDetails(anyString());
    }

    @Test
    void testGetBenData_nullBenRegId_skipsHealthDetails() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);
        
        RMNCHMBeneficiarymapping nullRegIdMapping = createTestMapping();
        nullRegIdMapping.setBenRegId(null);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(nullRegIdMapping);

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        verify(beneficiaryRepo, never()).getBenHealthIdNumber(any());
        verify(beneficiaryRepo, never()).getDetailsByRegID(anyLong());
        verify(beneficiaryRepo, never()).getBornBirthByRegID(anyLong());
    }

    @Test
    void testGetBenData_partialMappingData_handlesNulls() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        // Create mapping with only some fields set
        RMNCHMBeneficiarymapping partialMapping = new RMNCHMBeneficiarymapping();
        partialMapping.setBenRegId(BigInteger.valueOf(100L));
        partialMapping.setBenDetailsId(BigInteger.valueOf(101L));
        // Leave other IDs null

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(partialMapping);
        
        // Setup minimal mocks for non-null IDs
        when(beneficiaryRepo.getDetailsById(partialMapping.getBenDetailsId())).thenReturn(createTestBeneficiaryDetail());
        when(beneficiaryRepo.getBenIdFromRegID(partialMapping.getBenRegId().longValue())).thenReturn(BigInteger.valueOf(1000L));
        when(beneficiaryRepo.getDetailsByRegID(partialMapping.getBenRegId().longValue())).thenReturn(createTestRMNCHBeneficiaryDetails());
        when(beneficiaryRepo.getBornBirthByRegID(partialMapping.getBenRegId().longValue())).thenReturn(createTestBornBirthDetails());
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        verify(beneficiaryRepo).getDetailsById(partialMapping.getBenDetailsId());
        verify(beneficiaryRepo, never()).getAccountById(any());
        verify(beneficiaryRepo, never()).getImageById(anyLong());
        verify(beneficiaryRepo, never()).getAddressById(any());
        verify(beneficiaryRepo, never()).getContactById(any());
    }

    @Test
    void testGetBenData_addressExceptionHandling_continuesProcessing() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        RMNCHMBeneficiaryaddress problematicAddress = createTestAddress();
        problematicAddress.setId(BigInteger.valueOf(999L));
        
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address, problematicAddress);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 2);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        when(beneficiaryRepo.getByAddressID(problematicAddress.getId())).thenThrow(new RuntimeException("Database error"));
        
        setupMappingDataMocks();
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"data\""));
        // Should have 1 successful result despite one address failing
        verify(beneficiaryRepo).getByAddressID(address.getId());
        verify(beneficiaryRepo).getByAddressID(problematicAddress.getId());
    }

    @Test
    void testGetBenData_nullDob_skipsAgeCalculation() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<RMNCHMBeneficiaryaddress> addressList = Arrays.asList(address);
        Page<RMNCHMBeneficiaryaddress> page = new PageImpl<>(addressList, pageRequest, 1);

        when(beneficiaryRepo.getUserName(request.getAshaId())).thenReturn("testUser");
        when(beneficiaryRepo.getBenDataByUser("testUser", pageRequest)).thenReturn(page);
        when(beneficiaryRepo.getByAddressID(address.getId())).thenReturn(mapping);
        
        // Create detail with null DOB
        RMNCHMBeneficiarydetail detail = createTestBeneficiaryDetail();
        detail.setDob(null);
        
        when(beneficiaryRepo.getDetailsById(mapping.getBenDetailsId())).thenReturn(detail);
        setupOtherEntityMocks();
        setupHealthDetailsMocks();

        // Act
        String result = service.getBenData(request, authorization);

        // Assert
        assertNotNull(result);
        // Should still process successfully without age calculation
        assertTrue(result.contains("\"beneficiaryDetails\""));
    }

    // Helper methods to create test data
    private RMNCHMBeneficiaryaddress createTestAddress() {
        RMNCHMBeneficiaryaddress addr = new RMNCHMBeneficiaryaddress();
        addr.setId(BigInteger.valueOf(1L));
        addr.setVanID(1);
        addr.setCreatedBy("testUser");
        addr.setPermCountry("India");
        addr.setPermState("Test State");
        addr.setDistrictnamePerm("Test District");
        addr.setPermSubDistrict("Test Block");
        addr.setVillagenamePerm("Test Village");
        addr.setPermServicePoint("Test Service Point");
        addr.setPermZone("Test Zone");
        addr.setPermAddrLine1("Address Line 1");
        addr.setPermAddrLine2("Address Line 2");
        addr.setPermAddrLine3("Address Line 3");
        addr.setCountyid(1);
        addr.setStatePerm(1);
        addr.setDistrictidPerm(1);
        addr.setPermSubDistrictId(1);
        addr.setVillageidPerm(1);
        addr.setPermServicePointId(1);
        addr.setPermZoneID(1);
        return addr;
    }

    private RMNCHMBeneficiarymapping createTestMapping() {
        RMNCHMBeneficiarymapping map = new RMNCHMBeneficiarymapping();
        map.setBenRegId(BigInteger.valueOf(100L));
        map.setBenDetailsId(BigInteger.valueOf(101L));
        map.setBenAccountID(BigInteger.valueOf(102L));
        map.setBenImageId(BigInteger.valueOf(103L));
        map.setBenAddressId(BigInteger.valueOf(104L));
        map.setBenContactsId(BigInteger.valueOf(105L));
        return map;
    }

    private RMNCHMBeneficiarydetail createTestBeneficiaryDetail() {
        RMNCHMBeneficiarydetail detail = new RMNCHMBeneficiarydetail();
        detail.setFirstName("John");
        detail.setLastName("Doe");
        detail.setFatherName("Father Name");
        detail.setMotherName("Mother Name");
        detail.setSpousename("Spouse Name");
        detail.setGender("Male");
        detail.setGenderId(1);
        detail.setDob(currentTime);
        detail.setMaritalstatus("Married");
        detail.setMaritalstatusId(2);
        detail.setMarriageDate(currentTime);
        detail.setReligion("Hindu");
        detail.setReligionID(BigInteger.valueOf(1L));
        detail.setCommunity("General");
        detail.setCommunityId(1);
        detail.setLiteracyStatus("Literate");
        detail.setCreatedBy("testUser");
        return detail;
    }

    private RMNCHMBeneficiaryAccount createTestBeneficiaryAccount() {
        RMNCHMBeneficiaryAccount account = new RMNCHMBeneficiaryAccount();
        account.setNameOfBank("Test Bank");
        account.setBranchName("Test Branch");
        account.setIfscCode("TEST0001");
        account.setBankAccount("1234567890");
        return account;
    }

    private RMNCHMBeneficiaryImage createTestBeneficiaryImage() {
        RMNCHMBeneficiaryImage image = new RMNCHMBeneficiaryImage();
        image.setUser_image("base64encodedimage");
        return image;
    }

    private RMNCHMBeneficiarycontact createTestBeneficiaryContact() {
        RMNCHMBeneficiarycontact contact = new RMNCHMBeneficiarycontact();
        contact.setPreferredPhoneNum("9876543210");
        return contact;
    }

    private RMNCHBeneficiaryDetailsRmnch createTestRMNCHBeneficiaryDetails() {
        RMNCHBeneficiaryDetailsRmnch rmnchDetails = new RMNCHBeneficiaryDetailsRmnch();
        rmnchDetails.setHouseoldId(200L);
        rmnchDetails.setCreatedBy("testUser");
        return rmnchDetails;
    }

    private RMNCHHouseHoldDetails createTestHouseHoldDetails() {
        RMNCHHouseHoldDetails houseHold = new RMNCHHouseHoldDetails();
        houseHold.setId(200L);
        return houseHold;
    }

    private RMNCHBornBirthDetails createTestBornBirthDetails() {
        RMNCHBornBirthDetails birthDetails = new RMNCHBornBirthDetails();
        birthDetails.setBenRegId(100L);
        return birthDetails;
    }

    private void setupMappingDataMocks() {
        RMNCHMBeneficiarydetail detail = createTestBeneficiaryDetail();
        RMNCHMBeneficiaryAccount account = createTestBeneficiaryAccount();
        RMNCHMBeneficiaryImage image = createTestBeneficiaryImage();
        RMNCHMBeneficiarycontact contact = createTestBeneficiaryContact();
        RMNCHBeneficiaryDetailsRmnch rmnchDetails = createTestRMNCHBeneficiaryDetails();
        RMNCHHouseHoldDetails houseHold = createTestHouseHoldDetails();
        RMNCHBornBirthDetails birthDetails = createTestBornBirthDetails();

        when(beneficiaryRepo.getDetailsById(mapping.getBenDetailsId())).thenReturn(detail);
        when(beneficiaryRepo.getAccountById(mapping.getBenAccountID())).thenReturn(account);
        when(beneficiaryRepo.getImageById(mapping.getBenImageId().longValue())).thenReturn(image);
        when(beneficiaryRepo.getAddressById(mapping.getBenAddressId())).thenReturn(address);
        when(beneficiaryRepo.getContactById(mapping.getBenContactsId())).thenReturn(contact);
        when(beneficiaryRepo.getBenIdFromRegID(mapping.getBenRegId().longValue())).thenReturn(BigInteger.valueOf(1000L));
        when(beneficiaryRepo.getDetailsByRegID(mapping.getBenRegId().longValue())).thenReturn(rmnchDetails);
        when(beneficiaryRepo.getBornBirthByRegID(mapping.getBenRegId().longValue())).thenReturn(birthDetails);
        when(houseHoldRepo.getByHouseHoldID(rmnchDetails.getHouseoldId())).thenReturn(houseHold);
        when(beneficiaryRepo.getUserIDByUserName("testUser")).thenReturn(123);
    }

    private void setupCompleteEntityMocks() {
        setupMappingDataMocks();
    }

    private void setupOtherEntityMocks() {
        RMNCHMBeneficiaryAccount account = createTestBeneficiaryAccount();
        RMNCHMBeneficiaryImage image = createTestBeneficiaryImage();
        RMNCHMBeneficiarycontact contact = createTestBeneficiaryContact();
        RMNCHBeneficiaryDetailsRmnch rmnchDetails = createTestRMNCHBeneficiaryDetails();
        RMNCHHouseHoldDetails houseHold = createTestHouseHoldDetails();
        RMNCHBornBirthDetails birthDetails = createTestBornBirthDetails();

        when(beneficiaryRepo.getAccountById(mapping.getBenAccountID())).thenReturn(account);
        when(beneficiaryRepo.getImageById(mapping.getBenImageId().longValue())).thenReturn(image);
        when(beneficiaryRepo.getAddressById(mapping.getBenAddressId())).thenReturn(address);
        when(beneficiaryRepo.getContactById(mapping.getBenContactsId())).thenReturn(contact);
        when(beneficiaryRepo.getBenIdFromRegID(mapping.getBenRegId().longValue())).thenReturn(BigInteger.valueOf(1000L));
        when(beneficiaryRepo.getDetailsByRegID(mapping.getBenRegId().longValue())).thenReturn(rmnchDetails);
        when(beneficiaryRepo.getBornBirthByRegID(mapping.getBenRegId().longValue())).thenReturn(birthDetails);
        when(houseHoldRepo.getByHouseHoldID(rmnchDetails.getHouseoldId())).thenReturn(houseHold);
        when(beneficiaryRepo.getUserIDByUserName("testUser")).thenReturn(123);
    }

    private void setupHealthDetailsMocks() {
        Object[] healthIdData = {"test-health-id", "12345"};
        Object[] healthIdNumber = {healthIdData};
        when(beneficiaryRepo.getBenHealthIdNumber(mapping.getBenRegId())).thenReturn(healthIdNumber);
        when(beneficiaryRepo.getBenHealthDetails("test-health-id")).thenReturn(new ArrayList<>());
    }
}
