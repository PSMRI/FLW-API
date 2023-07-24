package com.iemr.flw.service.impl;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    void getUserRole_ValidUserIdAndRoleId_ReturnsUserRoleDTO() {
        Integer userId = 1;
        Integer roleId = 1;
        UserServiceRoleDTO expectedUserRole = new UserServiceRoleDTO();
        // Set up the repository getUserRole method to return expectedUserRole
        when(userServiceRoleRepo.getUserRole(userId, roleId)).thenReturn(expectedUserRole);

        // Act
        UserServiceRoleDTO result = userService.getUserRole(userId, roleId);

        // Assert
        assertEquals(expectedUserRole, result);
    }

    @Test
    void getUserRole_UserRoleNotFound_ReturnsNull() {
        Integer userId = 1;
        Integer roleId = 1;
        // Set up the repository getUserRole method to return null
        when(userServiceRoleRepo.getUserRole(userId, roleId)).thenReturn(null);

        // Act
        UserServiceRoleDTO result = userService.getUserRole(userId, roleId);

        // Assert
        assertEquals(null, result);
    }
}

