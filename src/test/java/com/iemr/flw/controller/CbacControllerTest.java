package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.dto.identity.CbacDTO;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.service.CbacService;
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

class CbacControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CbacService cbacService;

    @InjectMocks
    private CbacController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllCbacDetailsByUserId_success() throws Exception {
        when(cbacService.getByUserId(any())).thenReturn("data");
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/cbac/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllCbacDetailsByUserId_noRecordFound() throws Exception {
        when(cbacService.getByUserId(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/cbac/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllCbacDetailsByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/cbac/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllCbacDetailsByUserId_exception() throws Exception {
        when(cbacService.getByUserId(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/cbac/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllCbacDetailsByUserId_success() throws Exception {
        List<CbacDTO> dtos = Collections.singletonList(new CbacDTO());
        when(cbacService.save(any())).thenReturn("data");
        mockMvc.perform(post("/cbac/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllCbacDetailsByUserId_noRecordFound() throws Exception {
        List<CbacDTO> dtos = Collections.singletonList(new CbacDTO());
        when(cbacService.save(any())).thenReturn(null);
        mockMvc.perform(post("/cbac/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllCbacDetailsByUserId_invalidRequest() throws Exception {
        List<CbacDTO> dtos = Collections.emptyList();
        mockMvc.perform(post("/cbac/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllCbacDetailsByUserId_exception() throws Exception {
        List<CbacDTO> dtos = Collections.singletonList(new CbacDTO());
        when(cbacService.save(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/cbac/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }
}
