package com.iemr.flw.service.impl;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.dto.iemr.VillageDetailDTO;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    private static final int PSM = 1745;

    @Mock
    private UserServiceRoleRepo userServiceRoleRepo;

    @Mock
    private FacilityDataService facilityDataService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private UserServiceRoleDTO role(String villageIds, String villageNames) {
        UserServiceRoleDTO dto = new UserServiceRoleDTO();
        dto.setProviderServiceMapId(PSM);
        dto.setVillageId(villageIds);
        dto.setVillageName(villageNames);
        return dto;
    }

    private void stopTbUser(int userId, UserServiceRoleDTO dto) {
        when(userServiceRoleRepo.getUserRole(userId)).thenReturn(Collections.singletonList(dto));
        List<Object[]> nikshayRows = new ArrayList<>();
        nikshayRows.add(new Object[]{"10", "20", null});
        when(userServiceRoleRepo.getNikshayMappingRows(userId, PSM)).thenReturn(nikshayRows);
        when(userServiceRoleRepo.findNikshayTuNames(any())).thenReturn(Collections.emptyList());
        when(userServiceRoleRepo.findNikshayFacilityNames(any())).thenReturn(Collections.emptyList());
    }

    private static Object[] village(int id, String name, String pinCode) {
        return new Object[]{id, name, pinCode};
    }

    @Test
    void getUserDetail_returnsUserRole() {
        UserServiceRoleDTO dto = new UserServiceRoleDTO();
        when(userServiceRoleRepo.getUserRole(123)).thenReturn(Collections.singletonList(dto));
        UserServiceRoleDTO result = userService.getUserDetail(123);
        assertSame(dto, result);
        verify(userServiceRoleRepo).getUserRole(123);
    }

    @Test
    void getUserDetail_emptyList_throwsException() {
        when(userServiceRoleRepo.getUserRole(456)).thenReturn(Collections.emptyList());
        assertThrows(IllegalStateException.class, () -> userService.getUserDetail(456));
    }

    @Test
    void villageDetails_carryMasterValues_andNullWhereMasterHasNone() {
        stopTbUser(1, role("6677,9625", "Advi Khanapur,Alkur"));
        List<Object[]> rows = new ArrayList<>();
        rows.add(village(6677, "Advi Khanapur", "506001"));
        rows.add(village(9625, "Alkur", null));
        when(userServiceRoleRepo.findNikshayVillageDetails(any())).thenReturn(rows);

        UserServiceRoleDTO result = userService.getUserDetail(1);

        // existing fields unchanged
        assertEquals("6677,9625", result.getVillageId());
        assertEquals("Advi Khanapur (6677),Alkur (9625)", result.getVillageName());

        List<VillageDetailDTO> details = result.getVillageDetails();
        assertEquals(2, details.size());
        assertEquals(new VillageDetailDTO(6677, "Advi Khanapur", "506001", null, null), details.get(0));
        assertEquals(new VillageDetailDTO(9625, "Alkur", null, null, null), details.get(1));
    }

    @Test
    void villageDetails_villageMissingFromMaster_returnedWithNulls() {
        stopTbUser(2, role("111,222", "One,Two"));
        List<Object[]> rows = new ArrayList<>();
        rows.add(village(111, "One", "560001"));
        when(userServiceRoleRepo.findNikshayVillageDetails(any())).thenReturn(rows);

        List<VillageDetailDTO> details = userService.getUserDetail(2).getVillageDetails();

        assertEquals(new VillageDetailDTO(111, "One", "560001", null, null), details.get(0));
        assertEquals(new VillageDetailDTO(222, "Two", null, null, null), details.get(1));
    }

    @Test
    void villageDetails_blankPinCode_becomesNull() {
        stopTbUser(3, role("5", "Five"));
        List<Object[]> rows = new ArrayList<>();
        rows.add(village(5, "Five", "  "));
        when(userServiceRoleRepo.findNikshayVillageDetails(any())).thenReturn(rows);

        VillageDetailDTO detail = userService.getUserDetail(3).getVillageDetails().get(0);

        assertNull(detail.getPinCode());
        assertNull(detail.getResidentialAreaId());
        assertNull(detail.getResidentialArea());
    }

    @Test
    void villageDetails_lookupFailure_doesNotFailLogin() {
        stopTbUser(4, role("7", "Seven"));
        when(userServiceRoleRepo.findNikshayVillageDetails(any())).thenThrow(new RuntimeException("db down"));

        UserServiceRoleDTO result = userService.getUserDetail(4);

        assertNull(result.getVillageDetails());
        assertEquals("7", result.getVillageId());
    }

    @Test
    void villageDetails_largeVillageList_isLookedUpInChunks() {
        String ids = IntStream.rangeClosed(1, 2500).mapToObj(String::valueOf).collect(Collectors.joining(","));
        String names = IntStream.rangeClosed(1, 2500).mapToObj(i -> "V" + i).collect(Collectors.joining(","));
        stopTbUser(5, role(ids, names));
        when(userServiceRoleRepo.findNikshayVillageDetails(any())).thenAnswer(inv -> {
            Collection<Integer> chunk = inv.getArgument(0);
            assertTrue(chunk.size() <= 1000);
            List<Object[]> rows = new ArrayList<>();
            for (Integer id : chunk) rows.add(village(id, "V" + id, "50" + (1000 + id % 9000)));
            return rows;
        });

        List<VillageDetailDTO> details = userService.getUserDetail(5).getVillageDetails();

        verify(userServiceRoleRepo, times(3)).findNikshayVillageDetails(any());
        assertEquals(2500, details.size());
        assertEquals(1, details.get(0).getVillageId());
        assertEquals(2500, details.get(2499).getVillageId());
        assertEquals("501501", details.get(500).getPinCode());
    }

    @Test
    void villageDetails_notSentForNonStopTbUser() {
        when(userServiceRoleRepo.getUserRole(6)).thenReturn(Collections.singletonList(role("1,2", "A,B")));
        when(userServiceRoleRepo.getNikshayMappingRows(6, PSM)).thenReturn(Collections.emptyList());

        UserServiceRoleDTO result = userService.getUserDetail(6);

        assertNull(result.getVillageDetails());
        verify(userServiceRoleRepo, never()).findNikshayVillageDetails(anyCollection());
    }
}
