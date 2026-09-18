package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.EligibleCoupleDTO;
import com.iemr.flw.dto.iemr.EligibleCoupleTrackingDTO;
import com.iemr.flw.service.CoupleService;
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

class CoupleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CoupleService coupleService;

    @InjectMocks
    private CoupleController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void saveEligibleCouple_success() throws Exception {
        List<EligibleCoupleDTO> dtos = Collections.singletonList(new EligibleCoupleDTO());
        when(coupleService.registerEligibleCouple(any())).thenReturn("data");
        mockMvc.perform(post("/couple/register/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveEligibleCouple_noRecordFound() throws Exception {
        List<EligibleCoupleDTO> dtos = Collections.singletonList(new EligibleCoupleDTO());
        when(coupleService.registerEligibleCouple(any())).thenReturn(null);
        mockMvc.perform(post("/couple/register/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveEligibleCouple_invalidRequest() throws Exception {
        mockMvc.perform(post("/couple/register/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveEligibleCouple_exception() throws Exception {
        List<EligibleCoupleDTO> dtos = Collections.singletonList(new EligibleCoupleDTO());
        when(coupleService.registerEligibleCouple(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/couple/register/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveEligibleCoupleTracking_success() throws Exception {
        List<EligibleCoupleTrackingDTO> dtos = Collections.singletonList(new EligibleCoupleTrackingDTO());
        when(coupleService.registerEligibleCoupleTracking(any())).thenReturn("data");
        mockMvc.perform(post("/couple/tracking/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveEligibleCoupleTracking_noRecordFound() throws Exception {
        List<EligibleCoupleTrackingDTO> dtos = Collections.singletonList(new EligibleCoupleTrackingDTO());
        when(coupleService.registerEligibleCoupleTracking(any())).thenReturn(null);
        mockMvc.perform(post("/couple/tracking/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveEligibleCoupleTracking_invalidRequest() throws Exception {
        mockMvc.perform(post("/couple/tracking/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveEligibleCoupleTracking_exception() throws Exception {
        List<EligibleCoupleTrackingDTO> dtos = Collections.singletonList(new EligibleCoupleTrackingDTO());
        when(coupleService.registerEligibleCoupleTracking(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/couple/tracking/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getEligibleCouple_success() throws Exception {
        when(coupleService.getEligibleCoupleRegRecords(any())).thenReturn("data");
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/couple/register/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getEligibleCouple_noRecordFound() throws Exception {
        when(coupleService.getEligibleCoupleRegRecords(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/couple/register/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getEligibleCouple_invalidRequest() throws Exception {
        mockMvc.perform(post("/couple/register/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEligibleCouple_exception() throws Exception {
        when(coupleService.getEligibleCoupleRegRecords(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/couple/register/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getEligibleCoupleTracking_success() throws Exception {
        when(coupleService.getEligibleCoupleTracking(any())).thenReturn(Collections.singletonList(new EligibleCoupleTrackingDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/couple/tracking/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getEligibleCoupleTracking_noRecordFound() throws Exception {
        when(coupleService.getEligibleCoupleTracking(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/couple/tracking/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getEligibleCoupleTracking_invalidRequest() throws Exception {
        mockMvc.perform(post("/couple/tracking/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEligibleCoupleTracking_exception() throws Exception {
        when(coupleService.getEligibleCoupleTracking(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/couple/tracking/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }
}
