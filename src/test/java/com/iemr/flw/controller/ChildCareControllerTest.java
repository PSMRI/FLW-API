package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.ChildCareService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChildCareControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ChildCareService childCareService;

    @InjectMocks
    private ChildCareController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void saveHbycRecords_success() throws Exception {
        List<HbycDTO> dtos = Collections.singletonList(new HbycDTO());
        when(childCareService.registerHBYC(any())).thenReturn("data");
        mockMvc.perform(post("/child-care/hbyc/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveHbycRecords_noRecordFound() throws Exception {
        List<HbycDTO> dtos = Collections.singletonList(new HbycDTO());
        when(childCareService.registerHBYC(any())).thenReturn(null);
        mockMvc.perform(post("/child-care/hbyc/saveAll")
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
    void saveHbycRecords_invalidRequest() throws Exception {
        mockMvc.perform(post("/child-care/hbyc/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveHbycRecords_exception() throws Exception {
        List<HbycDTO> dtos = Collections.singletonList(new HbycDTO());
        when(childCareService.registerHBYC(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/child-care/hbyc/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getHbycRecords_success() throws Exception {
        when(childCareService.getHbycRecords(any())).thenReturn(Collections.singletonList(new HbycDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/child-care/hbyc/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getHbycRecords_noRecordFound() throws Exception {
        when(childCareService.getHbycRecords(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/child-care/hbyc/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getHbycRecords_invalidRequest() throws Exception {
        mockMvc.perform(post("/child-care/hbyc/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getHbycRecords_exception() throws Exception {
        when(childCareService.getHbycRecords(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/child-care/hbyc/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveHBNCVisit_success() throws Exception {
        List<HbncRequestDTO> dtos = Collections.singletonList(new HbncRequestDTO());
        when(childCareService.saveHBNCDetails(any())).thenReturn("data");
        mockMvc.perform(post("/child-care/hbncVisit/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveHBNCVisit_noRecordFound() throws Exception {
        List<HbncRequestDTO> dtos = Collections.singletonList(new HbncRequestDTO());
        when(childCareService.saveHBNCDetails(any())).thenReturn(null);
        mockMvc.perform(post("/child-care/hbncVisit/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("Saving hbnc data to db failed");
                });
    }

    @Test
    void saveHBNCVisit_invalidRequest() throws Exception {
        mockMvc.perform(post("/child-care/hbncVisit/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("[]"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("Invalid/NULL request obj");
                });
    }

    @Test
    void saveHBNCVisit_exception() throws Exception {
        List<HbncRequestDTO> dtos = Collections.singletonList(new HbncRequestDTO());
        when(childCareService.saveHBNCDetails(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/child-care/hbncVisit/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getHBNCVisitDetails_success() throws Exception {
        when(childCareService.getHBNCDetails(any())).thenReturn(Collections.singletonList(new HbncRequestDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/child-care/hbncVisit/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getHBNCVisitDetails_noRecordFound() throws Exception {
        when(childCareService.getHBNCDetails(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/child-care/hbncVisit/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getHBNCVisitDetails_invalidRequest() throws Exception {
        mockMvc.perform(post("/child-care/hbncVisit/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getHBNCVisitDetails_exception() throws Exception {
        when(childCareService.getHBNCDetails(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/child-care/hbncVisit/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveChildVaccinationDetails_success() throws Exception {
        List<ChildVaccinationDTO> dtos = Collections.singletonList(new ChildVaccinationDTO());
        when(childCareService.saveChildVaccinationDetails(any())).thenReturn("data");
        mockMvc.perform(post("/child-care/vaccination/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveChildVaccinationDetails_noRecordFound() throws Exception {
        List<ChildVaccinationDTO> dtos = Collections.singletonList(new ChildVaccinationDTO());
        when(childCareService.saveChildVaccinationDetails(any())).thenReturn(null);
        mockMvc.perform(post("/child-care/vaccination/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("Saving Child vaccination data to db failed");
                });
    }

    @Test
    void saveChildVaccinationDetails_invalidRequest() throws Exception {
        mockMvc.perform(post("/child-care/vaccination/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("[]"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("Invalid/NULL request obj");
                });
    }

    @Test
    void saveChildVaccinationDetails_exception() throws Exception {
        List<ChildVaccinationDTO> dtos = Collections.singletonList(new ChildVaccinationDTO());
        when(childCareService.saveChildVaccinationDetails(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/child-care/vaccination/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getChildVaccinationDetails_success() throws Exception {
        when(childCareService.getChildVaccinationDetails(any())).thenReturn(Collections.singletonList(new ChildVaccinationDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/child-care/vaccination/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getChildVaccinationDetails_noRecordFound() throws Exception {
        when(childCareService.getChildVaccinationDetails(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/child-care/vaccination/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getChildVaccinationDetails_invalidRequest() throws Exception {
        mockMvc.perform(post("/child-care/vaccination/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getChildVaccinationDetails_exception() throws Exception {
        when(childCareService.getChildVaccinationDetails(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/child-care/vaccination/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllChildVaccines_success() throws Exception {
        when(childCareService.getAllChildVaccines(any())).thenReturn(Collections.singletonList(new VaccineDTO()));
        mockMvc.perform(get("/child-care/vaccine/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .param("category", "cat1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllChildVaccines_noRecordFound() throws Exception {
        when(childCareService.getAllChildVaccines(any())).thenReturn(null);
        mockMvc.perform(get("/child-care/vaccine/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .param("category", "cat1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllChildVaccines_exception() throws Exception {
        when(childCareService.getAllChildVaccines(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/child-care/vaccine/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .param("category", "cat1"))
                .andExpect(status().isOk());
    }
}
