package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.IncentiveActivityDTO;
import com.iemr.flw.dto.iemr.IncentiveRequestDTO;
import com.iemr.flw.service.IncentiveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IncentiveControllerTest {
    private MockMvc mockMvc;

    @Mock
    private IncentiveService incentiveService;

    @InjectMocks
    private IncentiveController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void saveIncentiveMasterData_success() throws Exception {
        List<IncentiveActivityDTO> dtos = Collections.singletonList(new IncentiveActivityDTO());
        when(incentiveService.saveIncentivesMaster(any())).thenReturn("data");
        mockMvc.perform(post("/incentive/masterData/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveIncentiveMasterData_noRecordFound() throws Exception {
        List<IncentiveActivityDTO> dtos = Collections.singletonList(new IncentiveActivityDTO());
        when(incentiveService.saveIncentivesMaster(any())).thenReturn(null);
        mockMvc.perform(post("/incentive/masterData/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveIncentiveMasterData_invalidRequest() throws Exception {
        mockMvc.perform(post("/incentive/masterData/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveIncentiveMasterData_exception() throws Exception {
        List<IncentiveActivityDTO> dtos = Collections.singletonList(new IncentiveActivityDTO());
        when(incentiveService.saveIncentivesMaster(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/incentive/masterData/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveIncentiveMasterData_getAll_success() throws Exception {
        IncentiveRequestDTO req = new IncentiveRequestDTO();
        when(incentiveService.getIncentiveMaster(any())).thenReturn("data");
        mockMvc.perform(post("/incentive/masterData/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveIncentiveMasterData_getAll_noRecordFound() throws Exception {
        IncentiveRequestDTO req = new IncentiveRequestDTO();
        when(incentiveService.getIncentiveMaster(any())).thenReturn(null);
        mockMvc.perform(post("/incentive/masterData/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveIncentiveMasterData_getAll_invalidRequest() throws Exception {
        mockMvc.perform(post("/incentive/masterData/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveIncentiveMasterData_getAll_exception() throws Exception {
        when(incentiveService.getIncentiveMaster(any())).thenThrow(new RuntimeException("fail"));
        IncentiveRequestDTO req = new IncentiveRequestDTO();
        mockMvc.perform(post("/incentive/masterData/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllIncentivesByUserId_success() throws Exception {
        GetBenRequestHandler req = new GetBenRequestHandler();
        when(incentiveService.getAllIncentivesByUserId(any())).thenReturn("data");
        mockMvc.perform(post("/incentive/fetchUserData")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllIncentivesByUserId_noRecordFound() throws Exception {
        GetBenRequestHandler req = new GetBenRequestHandler();
        when(incentiveService.getAllIncentivesByUserId(any())).thenReturn(null);
        mockMvc.perform(post("/incentive/fetchUserData")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllIncentivesByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/incentive/fetchUserData")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllIncentivesByUserId_exception() throws Exception {
        when(incentiveService.getAllIncentivesByUserId(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/incentive/fetchUserData")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }
}
