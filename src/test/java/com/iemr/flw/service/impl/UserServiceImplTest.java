package com.iemr.flw.service.impl;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserServiceRoleRepo userServiceRoleRepo;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetUserDetail_Success() {
        // Arrange
        int userId = 123;
        UserServiceRoleDTO expectedUserRoleDTO = new UserServiceRoleDTO();
        expectedUserRoleDTO.setUserId(userId);
        expectedUserRoleDTO.setRoleName("ROLE_USER");

        List<UserServiceRoleDTO> userRoleList = new ArrayList<>();
        userRoleList.add(expectedUserRoleDTO);

        when(userServiceRoleRepo.getUserRole(anyInt())).thenReturn(userRoleList);

        // Act
        UserServiceRoleDTO result = userService.getUserDetail(userId);

        // Assert
        assertEquals(expectedUserRoleDTO.getUserId(), result.getUserId());
        assertEquals(expectedUserRoleDTO.getRoleName(), result.getRoleName());
    }
}
