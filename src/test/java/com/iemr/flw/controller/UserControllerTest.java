package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getUserRole_ValidUserIdAndRoleId_ReturnsAcceptedResponse() {
        Integer userId = 1;
        Integer roleId = 1;
        UserServiceRoleDTO expectedResponse = new UserServiceRoleDTO();
        // Set up userService.getUserRole to return expectedResponse
        when(userService.getUserRole(userId, roleId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = userController.getEligibleCouple(userId, roleId, "Authorization");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void getUserRole_InvalidUserIdAndRoleId_ReturnsInternalServerErrorResponse() {
        Integer userId = 999;
        Integer roleId = 999;

        when(userService.getUserRole(userId, roleId)).thenThrow(new RuntimeException("Invalid user or role ID"));

        // Act
        ResponseEntity<?> response = userController.getEligibleCouple(userId, roleId, "Authorization");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
