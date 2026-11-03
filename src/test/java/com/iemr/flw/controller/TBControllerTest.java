package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.dto.iemr.TBSuspectedRequestDTO;
import com.iemr.flw.service.TBScreeningService;
import com.iemr.flw.service.TBSuspectedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TBControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TBScreeningService tbScreeningService;
    @Mock
    private TBSuspectedService tbSuspectedService;

    @InjectMocks
    private TBController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllScreeningByUserId_success() throws Exception {
        when(tbScreeningService.getByUserId(any())).thenReturn("data");
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/tb/screening/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}")) 
                .andExpect(status().isOk());
    }

    @Test
    void getAllScreeningByUserId_noRecordFound() throws Exception {
        when(tbScreeningService.getByUserId(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/tb/screening/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("No record found");
                });
    }

    @Test
    void getAllScreeningByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/tb/screening/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllScreeningByUserId_exception() throws Exception {
        when(tbScreeningService.getByUserId(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/tb/screening/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllScreeningByUserId_success() throws Exception {
        when(tbScreeningService.save(any())).thenReturn("data");
        TBScreeningRequestDTO req = new TBScreeningRequestDTO();
        mockMvc.perform(post("/tb/screening/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllScreeningByUserId_noRecordFound() throws Exception {
        when(tbScreeningService.save(any())).thenReturn(null);
        TBScreeningRequestDTO req = new TBScreeningRequestDTO();
        mockMvc.perform(post("/tb/screening/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("No record found");
                });
    }

    @Test
    void saveAllScreeningByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/tb/screening/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveAllScreeningByUserId_exception() throws Exception {
        when(tbScreeningService.save(any())).thenThrow(new RuntimeException("fail"));
        TBScreeningRequestDTO req = new TBScreeningRequestDTO();
        mockMvc.perform(post("/tb/screening/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllSuspectedByUserId_success() throws Exception {
        when(tbSuspectedService.getByUserId(any())).thenReturn("data");
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/tb/suspected/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllSuspectedByUserId_noRecordFound() throws Exception {
        when(tbSuspectedService.getByUserId(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/tb/suspected/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("No record found");
                });
    }

    @Test
    void getAllSuspectedByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/tb/suspected/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllSuspectedByUserId_exception() throws Exception {
        when(tbSuspectedService.getByUserId(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/tb/suspected/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllSuspectedByUserId_success() throws Exception {
        when(tbSuspectedService.save(any())).thenReturn("data");
        TBSuspectedRequestDTO req = new TBSuspectedRequestDTO();
        mockMvc.perform(post("/tb/suspected/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllSuspectedByUserId_noRecordFound() throws Exception {
        when(tbSuspectedService.save(any())).thenReturn(null);
        TBSuspectedRequestDTO req = new TBSuspectedRequestDTO();
        mockMvc.perform(post("/tb/suspected/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("No record found");
                });
    }

    @Test
    void saveAllSuspectedByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/tb/suspected/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveAllSuspectedByUserId_exception() throws Exception {
        when(tbSuspectedService.save(any())).thenThrow(new RuntimeException("fail"));
        TBSuspectedRequestDTO req = new TBSuspectedRequestDTO();
        mockMvc.perform(post("/tb/suspected/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
}
