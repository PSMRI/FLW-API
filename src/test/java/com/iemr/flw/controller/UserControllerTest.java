package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
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
    void testGetUserDetail_Success() {
        // Arrange
        Integer userId = 123;
        UserServiceRoleDTO userServiceRoleDTO = new UserServiceRoleDTO();
        userServiceRoleDTO.setUserId(userId);

        when(userService.getUserDetail(anyInt())).thenReturn(userServiceRoleDTO);

        // Act
        ResponseEntity<?> responseEntity = userController.getUserDetail(userId, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.getSuccess());
        assertNull(apiResponse.getMessage());

        assertEquals(userServiceRoleDTO, apiResponse.getData());

        verify(userService, times(1)).getUserDetail(userId);
    }

    @Test
    void testGetUserDetail_Exception() {
        // Arrange
        Integer userId = 123;

        when(userService.getUserDetail(anyInt())).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> responseEntity = userController.getUserDetail(userId, "AuthorizationToken");

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.getSuccess());
        assertNotNull(apiResponse.getMessage());

        assertNull(apiResponse.getData());

        verify(userService, times(1)).getUserDetail(userId);
    }
}
