package com.iemr.flw.controller;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.service.BeneficiaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BeneficiaryControllerTest {
    private MockMvc mockMvc;

    @Mock
    private BeneficiaryService beneficiaryService;

    @InjectMocks
    private BeneficiaryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getBeneficiaryDataByAsha_success() throws Exception {
        GetBenRequestHandler req = new GetBenRequestHandler();
        when(beneficiaryService.getBenData(any(), anyString())).thenReturn("data");
        mockMvc.perform(post("/beneficiary/getBeneficiaryData")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void getBeneficiaryDataByAsha_noRecordFound() throws Exception {
        GetBenRequestHandler req = new GetBenRequestHandler();
        when(beneficiaryService.getBenData(any(), anyString())).thenReturn(null);
        mockMvc.perform(post("/beneficiary/getBeneficiaryData")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void getBeneficiaryDataByAsha_invalidRequest() throws Exception {
        mockMvc.perform(post("/beneficiary/getBeneficiaryData")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBeneficiaryDataByAsha_exception() throws Exception {
        when(beneficiaryService.getBenData(any(), anyString())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/beneficiary/getBeneficiaryData")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{}"))
                .andExpect(status().isOk());
    }
}
