package com.iemr.flw.service.impl;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserServiceRoleRepo userServiceRoleRepo;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        assertThrows(IndexOutOfBoundsException.class, () -> userService.getUserDetail(456));
    }
}
