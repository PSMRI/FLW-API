package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.UserServiceRoleDTO;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getUserDetail_success() throws Exception {
        UserServiceRoleDTO dto = new UserServiceRoleDTO();
        when(userService.getUserDetail(anyInt())).thenReturn(dto);
        mockMvc.perform(get("/user/getUserDetail")
                .param("userId", "1")
                .header("Authorization", "Bearer token")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUserDetail_exception() throws Exception {
        when(userService.getUserDetail(anyInt())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/user/getUserDetail")
                .param("userId", "1")
                .header("Authorization", "Bearer token")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }
}
