package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.repo.iemr.ANCVisitRepo;
import com.iemr.flw.repo.iemr.PregnantWomanRegisterRepo;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MaternalHealthServiceImplTest {

    @Mock
    private PregnantWomanRegisterRepo pregnantWomanRegisterRepo;

    @Mock
    private ANCVisitRepo ancVisitRepo;

    @InjectMocks
    private MaternalHealthServiceImpl pregnantWomanService;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    void testRegisterPregnantWoman_Success() {
//        // Arrange
//        List<PregnantWomanDTO> pregnantWomanDTOs = new ArrayList<>();
//        pregnantWomanDTOs.add(new PregnantWomanDTO());
//
//        // Act
//        String result = pregnantWomanService.registerPregnantWoman(pregnantWomanDTOs);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("saved successfully", result);
//
//        verify(pregnantWomanRegisterRepo, times(1)).save(anyList());
//    }
//
//    @Test
//    void testGetPregnantWoman_Success() {
//        // Arrange
//        GetBenRequestHandler dto = new GetBenRequestHandler();
//        dto.setAshaId(123);
//        dto.setFromDate(new Timestamp((new Date()).getTime()));
//        dto.setToDate(new Timestamp((new Date()).getTime()));
//
//        List<PregnantWomanRegister> pregnantWomanRegisterList = new ArrayList<>();
//        pregnantWomanRegisterList.add(new PregnantWomanRegister());
//
//        when(pregnantWomanRegisterRepo.getPWRWithBen(dto.getAshaId(), dto.getFromDate(), dto.getToDate())).thenReturn(pregnantWomanRegisterList);
//
//        // Act
//        List<PregnantWomanDTO> result = pregnantWomanService.getPregnantWoman(dto);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(pregnantWomanRegisterList.size(), result.size());
//
//        verify(pregnantWomanRegisterRepo, times(1)).getPWRWithBen(dto.getAshaId(), dto.getFromDate(), dto.getToDate());
//    }
//
//    @Test
//    void testGetANCVisits_Success() {
//        // Arrange
//        GetBenRequestHandler dto = new GetBenRequestHandler();
//        dto.setAshaId(123);
//        dto.setFromDate(new Timestamp((new Date()).getTime()));
//        dto.setToDate(new Timestamp((new Date()).getTime()));
//
//        List<ANCVisit> ancVisits = new ArrayList<>();
//        ancVisits.add(new ANCVisit());
//
//        when(ancVisitRepo.getANCForPW(dto.getAshaId(), dto.getFromDate(), dto.getToDate())).thenReturn(ancVisits);
//
//        // Act
//        List<ANCVisitDTO> result = pregnantWomanService.getANCVisits(dto);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(ancVisits.size(), result.size());
//
//        verify(ancVisitRepo, times(1)).getANCForPW(dto.getAshaId(), dto.getFromDate(), dto.getToDate());
//    }
//
//    @Test
//    void testSaveANCVisit_Success() {
//        // Arrange
//        List<ANCVisitDTO> ancVisitDTOs = new ArrayList<>();
//        ancVisitDTOs.add(new ANCVisitDTO());
//
//        // Act
//        String result = pregnantWomanService.saveANCVisit(ancVisitDTOs);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("saved successfully", result);
//
//        verify(ancVisitRepo, times(1)).save(anyList());
//    }
}
