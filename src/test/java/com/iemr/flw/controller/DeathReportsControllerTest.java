package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.CdrDTO;
import com.iemr.flw.dto.iemr.MdsrDTO;
import com.iemr.flw.service.DeathReportsService;
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

class DeathReportsControllerTest {
    private MockMvc mockMvc;

    @Mock
    private DeathReportsService deathReportsService;

    @InjectMocks
    private DeathReportsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void saveCdrRecords_success() throws Exception {
        List<CdrDTO> dtos = Collections.singletonList(new CdrDTO());
        when(deathReportsService.registerCDR(any())).thenReturn("data");
        mockMvc.perform(post("/death-reports/cdr/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveCdrRecords_noRecordFound() throws Exception {
        List<CdrDTO> dtos = Collections.singletonList(new CdrDTO());
        when(deathReportsService.registerCDR(any())).thenReturn(null);
        mockMvc.perform(post("/death-reports/cdr/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("No record found");
                });
    }

    @Test
    void saveCdrRecords_invalidRequest() throws Exception {
        mockMvc.perform(post("/death-reports/cdr/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCdrRecords_exception() throws Exception {
        List<CdrDTO> dtos = Collections.singletonList(new CdrDTO());
        when(deathReportsService.registerCDR(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/death-reports/cdr/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveMdsrRecords_success() throws Exception {
        List<MdsrDTO> dtos = Collections.singletonList(new MdsrDTO());
        when(deathReportsService.registerMDSR(any())).thenReturn("data");
        mockMvc.perform(post("/death-reports/mdsr/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveMdsrRecords_noRecordFound() throws Exception {
        List<MdsrDTO> dtos = Collections.singletonList(new MdsrDTO());
        when(deathReportsService.registerMDSR(any())).thenReturn(null);
        mockMvc.perform(post("/death-reports/mdsr/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("No record found");
                });
    }

    @Test
    void saveMdsrRecords_invalidRequest() throws Exception {
        mockMvc.perform(post("/death-reports/mdsr/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveMdsrRecords_exception() throws Exception {
        List<MdsrDTO> dtos = Collections.singletonList(new MdsrDTO());
        when(deathReportsService.registerMDSR(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/death-reports/mdsr/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getCdrRecords_success() throws Exception {
        when(deathReportsService.getCdrRecords(any())).thenReturn(Collections.singletonList(new CdrDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/death-reports/cdr/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getCdrRecords_noRecordFound() throws Exception {
        when(deathReportsService.getCdrRecords(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/death-reports/cdr/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
        
    }

    @Test
    void getCdrRecords_invalidRequest() throws Exception {
        mockMvc.perform(post("/death-reports/cdr/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCdrRecords_exception() throws Exception {
        when(deathReportsService.getCdrRecords(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/death-reports/cdr/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getMdsrRecords_success() throws Exception {
        when(deathReportsService.getMdsrRecords(any())).thenReturn(Collections.singletonList(new MdsrDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/death-reports/mdsr/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getMdsrRecords_noRecordFound() throws Exception {
        when(deathReportsService.getMdsrRecords(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/death-reports/mdsr/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getMdsrRecords_invalidRequest() throws Exception {
        mockMvc.perform(post("/death-reports/mdsr/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMdsrRecords_exception() throws Exception {
        when(deathReportsService.getMdsrRecords(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/death-reports/mdsr/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }
}
