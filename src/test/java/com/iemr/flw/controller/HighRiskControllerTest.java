package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.HighRiskNonPregnantService;
import com.iemr.flw.service.HighRiskPregnantService;
import com.iemr.flw.service.HighRiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HighRiskControllerTest {
    private MockMvc mockMvc;

    @Mock
    private HighRiskNonPregnantService highRiskNonPregnantService;
    @Mock
    private HighRiskPregnantService highRiskPregnantService;
    @Mock
    private HighRiskService highRiskService;

    @InjectMocks
    private HighRiskController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // Pregnant Assess
    @Test
    void getAllPregnantAssessByUserId_success() throws Exception {
        when(highRiskPregnantService.getAllAssessments(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/pregnant/assess/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void getAllPregnantAssessByUserId_noRecordFound() throws Exception {
        when(highRiskPregnantService.getAllAssessments(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/pregnant/assess/getAll")
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
    void getAllPregnantAssessByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/pregnant/assess/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAllPregnantAssessByUserId_exception() throws Exception {
        when(highRiskPregnantService.getAllAssessments(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/pregnant/assess/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllPregnantAssessByUserId_success() throws Exception {
        when(highRiskPregnantService.saveAllAssessment(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/pregnant/assess/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllPregnantAssessByUserId_noRecordFound() throws Exception {
        when(highRiskPregnantService.saveAllAssessment(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/pregnant/assess/saveAll")
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
    void saveAllPregnantAssessByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/pregnant/assess/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void saveAllPregnantAssessByUserId_exception() throws Exception {
        when(highRiskPregnantService.saveAllAssessment(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/pregnant/assess/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    // Assess
    @Test
    void getAllAssessByUserId_success() throws Exception {
        when(highRiskService.getAllAssessments(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/assess/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void getAllAssessByUserId_noRecordFound() throws Exception {
        when(highRiskService.getAllAssessments(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/assess/getAll")
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
    void getAllAssessByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/assess/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAllAssessByUserId_exception() throws Exception {
        when(highRiskService.getAllAssessments(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/assess/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllAssessByUserId_success() throws Exception {
        when(highRiskService.saveAllAssessment(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/assess/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllAssessByUserId_noRecordFound() throws Exception {
        when(highRiskService.saveAllAssessment(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/assess/saveAll")
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
    void saveAllAssessByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/assess/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void saveAllAssessByUserId_exception() throws Exception {
        when(highRiskService.saveAllAssessment(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/assess/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    // Pregnant Track
    @Test
    void getAllPregnantTrackByUserId_success() throws Exception {
        when(highRiskPregnantService.getAllTracking(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/pregnant/track/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void getAllPregnantTrackByUserId_noRecordFound() throws Exception {
        when(highRiskPregnantService.getAllTracking(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/pregnant/track/getAll")
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
    void getAllPregnantTrackByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/pregnant/track/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAllPregnantTrackByUserId_exception() throws Exception {
        when(highRiskPregnantService.getAllTracking(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/pregnant/track/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllPregnantTrackByUserId_success() throws Exception {
        when(highRiskPregnantService.saveAllTracking(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/pregnant/track/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllPregnantTrackByUserId_noRecordFound() throws Exception {
        when(highRiskPregnantService.saveAllTracking(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/pregnant/track/saveAll")
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
    void saveAllPregnantTrackByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/pregnant/track/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void saveAllPregnantTrackByUserId_exception() throws Exception {
        when(highRiskPregnantService.saveAllTracking(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/pregnant/track/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    // NonPregnant Assess
    @Test
    void getAllNonPregnantAssessByUserId_success() throws Exception {
        when(highRiskNonPregnantService.getAllAssessment(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/nonPregnant/assess/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void getAllNonPregnantAssessByUserId_noRecordFound() throws Exception {
        when(highRiskNonPregnantService.getAllAssessment(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/nonPregnant/assess/getAll")
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
    void getAllNonPregnantAssessByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/nonPregnant/assess/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAllNonPregnantAssessByUserId_exception() throws Exception {
        when(highRiskNonPregnantService.getAllAssessment(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/nonPregnant/assess/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllNonPregnantAssessByUserId_success() throws Exception {
        when(highRiskNonPregnantService.saveAllAssessment(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/nonPregnant/assess/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllNonPregnantAssessByUserId_noRecordFound() throws Exception {
        when(highRiskNonPregnantService.saveAllAssessment(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/nonPregnant/assess/saveAll")
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
    void saveAllNonPregnantAssessByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/nonPregnant/assess/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void saveAllNonPregnantAssessByUserId_exception() throws Exception {
        when(highRiskNonPregnantService.saveAllAssessment(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/nonPregnant/assess/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    // NonPregnant Track
    @Test
    void getAllNonPregnantTrackByUserId_success() throws Exception {
        when(highRiskNonPregnantService.getAllTracking(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/nonPregnant/track/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void getAllNonPregnantTrackByUserId_noRecordFound() throws Exception {
        when(highRiskNonPregnantService.getAllTracking(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/nonPregnant/track/getAll")
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
    void getAllNonPregnantTrackByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/nonPregnant/track/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAllNonPregnantTrackByUserId_exception() throws Exception {
        when(highRiskNonPregnantService.getAllTracking(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/nonPregnant/track/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllNonPregnantTrackByUserId_success() throws Exception {
        when(highRiskNonPregnantService.saveAllTracking(any())).thenReturn("data");
        mockMvc.perform(post("/highRisk/nonPregnant/track/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
    @Test
    void saveAllNonPregnantTrackByUserId_noRecordFound() throws Exception {
        when(highRiskNonPregnantService.saveAllTracking(any())).thenReturn(null);
        mockMvc.perform(post("/highRisk/nonPregnant/track/saveAll")
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
    void saveAllNonPregnantTrackByUserId_invalidRequest() throws Exception {
        mockMvc.perform(post("/highRisk/nonPregnant/track/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void saveAllNonPregnantTrackByUserId_exception() throws Exception {
        when(highRiskNonPregnantService.saveAllTracking(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/highRisk/nonPregnant/track/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
}
