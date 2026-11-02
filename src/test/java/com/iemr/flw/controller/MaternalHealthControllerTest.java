package com.iemr.flw.controller;

import com.google.gson.Gson;
import com.iemr.flw.controller.MaternalHealthController;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.service.ChildService;
import com.iemr.flw.service.DeliveryOutcomeService;
import com.iemr.flw.service.InfantService;
import com.iemr.flw.service.MaternalHealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MaternalHealthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MaternalHealthService maternalHealthService;
    @Mock
    private DeliveryOutcomeService deliveryOutcomeService;
    @Mock
    private InfantService infantService;
    @Mock
    private ChildService childService;

    @InjectMocks
    private MaternalHealthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void savePregnantWomanRegistrations_success() throws Exception {
        List<PregnantWomanDTO> dtos = Collections.singletonList(new PregnantWomanDTO());
        when(maternalHealthService.registerPregnantWoman(any())).thenReturn("success");
        mockMvc.perform(post("/maternalCare/pregnantWoman/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void savePregnantWomanRegistrations_invalidRequest() throws Exception {
        List<PregnantWomanDTO> dtos = Collections.emptyList();
        mockMvc.perform(post("/maternalCare/pregnantWoman/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getPregnantWomanList_success() throws Exception {
        when(maternalHealthService.getPregnantWoman(any())).thenReturn(Collections.singletonList(new PregnantWomanDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/pregnantWoman/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getPregnantWomanList_invalidRequest() throws Exception {
        mockMvc.perform(post("/maternalCare/pregnantWoman/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPregnantWomanList_noRecordFound() throws Exception {
        when(maternalHealthService.getPregnantWoman(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/pregnantWoman/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveANCVisit_success() throws Exception {
        List<ANCVisitDTO> dtos = Collections.singletonList(new ANCVisitDTO());
        when(maternalHealthService.saveANCVisit(any())).thenReturn("success");
        mockMvc.perform(post("/maternalCare/ancVisit/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveANCVisit_invalidRequest() throws Exception {
        List<ANCVisitDTO> dtos = Collections.emptyList();
        mockMvc.perform(post("/maternalCare/ancVisit/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getANCVisitDetails_success() throws Exception {
        when(maternalHealthService.getANCVisits(any())).thenReturn(Collections.singletonList(new ANCVisitDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/ancVisit/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getANCVisitDetails_invalidRequest() throws Exception {
        mockMvc.perform(post("/maternalCare/ancVisit/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getANCVisitDetails_noRecordFound() throws Exception {
        when(maternalHealthService.getANCVisits(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/ancVisit/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveDeliveryOutcome_success() throws Exception {
        List<DeliveryOutcomeDTO> dtos = Collections.singletonList(new DeliveryOutcomeDTO());
        when(deliveryOutcomeService.registerDeliveryOutcome(any())).thenReturn("success");
        mockMvc.perform(post("/maternalCare/deliveryOutcome/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveDeliveryOutcome_invalidRequest() throws Exception {
        List<DeliveryOutcomeDTO> dtos = Collections.emptyList();
        mockMvc.perform(post("/maternalCare/deliveryOutcome/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getDeliveryOutcome_success() throws Exception {
        when(deliveryOutcomeService.getDeliveryOutcome(any())).thenReturn(Collections.singletonList(new DeliveryOutcomeDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/deliveryOutcome/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getDeliveryOutcome_invalidRequest() throws Exception {
        mockMvc.perform(post("/maternalCare/deliveryOutcome/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDeliveryOutcome_noRecordFound() throws Exception {
        when(deliveryOutcomeService.getDeliveryOutcome(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/deliveryOutcome/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveInfantList_success() throws Exception {
        List<InfantRegisterDTO> dtos = Collections.singletonList(new InfantRegisterDTO());
        when(infantService.registerInfant(any())).thenReturn("success");
        mockMvc.perform(post("/maternalCare/infant/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveInfantList_invalidRequest() throws Exception {
        List<InfantRegisterDTO> dtos = Collections.emptyList();
        mockMvc.perform(post("/maternalCare/infant/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getInfantList_success() throws Exception {
        when(infantService.getInfantDetails(any())).thenReturn(Collections.singletonList(new InfantRegisterDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/infant/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getInfantList_invalidRequest() throws Exception {
        mockMvc.perform(post("/maternalCare/infant/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getInfantList_noRecordFound() throws Exception {
        when(infantService.getInfantDetails(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/infant/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllChildRegisterDetails_success() throws Exception {
        when(childService.getByUserId(any())).thenReturn("child");
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/child/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllChildRegisterDetails_invalidRequest() throws Exception {
        mockMvc.perform(post("/maternalCare/child/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllChildRegisterDetails_noRecordFound() throws Exception {
        when(childService.getByUserId(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/child/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllChildDetails_success() throws Exception {
        List<ChildRegisterDTO> dtos = Collections.singletonList(new ChildRegisterDTO());
        when(childService.save(any())).thenReturn("success");
        mockMvc.perform(post("/maternalCare/child/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllChildDetails_invalidRequest() throws Exception {
        List<ChildRegisterDTO> dtos = Collections.emptyList();
        mockMvc.perform(post("/maternalCare/child/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPmsmaDetails_success() throws Exception {
        when(maternalHealthService.getPmsmaRecords(any())).thenReturn(Collections.singletonList(new PmsmaDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/pmsma/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPmsmaDetails_invalidRequest() throws Exception {
        mockMvc.perform(post("/maternalCare/pmsma/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllPmsmaDetails_noRecordFound() throws Exception {
        when(maternalHealthService.getPmsmaRecords(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/pmsma/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllPmsmaRecords_success() throws Exception {
        List<PmsmaDTO> dtos = Collections.singletonList(new PmsmaDTO());
        when(maternalHealthService.savePmsmaRecords(any())).thenReturn("success");
        mockMvc.perform(post("/maternalCare/pmsma/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllPmsmaRecords_invalidRequest() throws Exception {
        List<PmsmaDTO> dtos = Collections.emptyList();
        mockMvc.perform(post("/maternalCare/pmsma/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void savePNCVisit_success() throws Exception {
        List<PNCVisitDTO> dtos = Collections.singletonList(new PNCVisitDTO());
        when(maternalHealthService.savePNCVisit(any())).thenReturn("success");
        mockMvc.perform(post("/maternalCare/pnc/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void savePNCVisit_invalidRequest() throws Exception {
        List<PNCVisitDTO> dtos = Collections.emptyList();
        mockMvc.perform(post("/maternalCare/pnc/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getPNCVisitDetails_success() throws Exception {
        when(maternalHealthService.getPNCVisits(any())).thenReturn(Collections.singletonList(new PNCVisitDTO()));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/pnc/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getPNCVisitDetails_invalidRequest() throws Exception {
        mockMvc.perform(post("/maternalCare/pnc/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPNCVisitDetails_noRecordFound() throws Exception {
        when(maternalHealthService.getPNCVisits(any())).thenReturn(null);
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/pnc/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void savePregnantWomanRegistrations_exception() throws Exception {
        List<PregnantWomanDTO> dtos = Collections.singletonList(new PregnantWomanDTO());
        when(maternalHealthService.registerPregnantWoman(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/maternalCare/pregnantWoman/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getPregnantWomanList_exception() throws Exception {
        when(maternalHealthService.getPregnantWoman(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/pregnantWoman/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveANCVisit_exception() throws Exception {
        List<ANCVisitDTO> dtos = Collections.singletonList(new ANCVisitDTO());
        when(maternalHealthService.saveANCVisit(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/maternalCare/ancVisit/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getANCVisitDetails_exception() throws Exception {
        when(maternalHealthService.getANCVisits(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/ancVisit/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveDeliveryOutcome_exception() throws Exception {
        List<DeliveryOutcomeDTO> dtos = Collections.singletonList(new DeliveryOutcomeDTO());
        when(deliveryOutcomeService.registerDeliveryOutcome(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/maternalCare/deliveryOutcome/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getDeliveryOutcome_exception() throws Exception {
        when(deliveryOutcomeService.getDeliveryOutcome(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/deliveryOutcome/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveInfantList_exception() throws Exception {
        List<InfantRegisterDTO> dtos = Collections.singletonList(new InfantRegisterDTO());
        when(infantService.registerInfant(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/maternalCare/infant/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getInfantList_exception() throws Exception {
        when(infantService.getInfantDetails(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/infant/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllChildRegisterDetails_exception() throws Exception {
        when(childService.getByUserId(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/child/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllChildDetails_exception() throws Exception {
        List<ChildRegisterDTO> dtos = Collections.singletonList(new ChildRegisterDTO());
        when(childService.save(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/maternalCare/child/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPmsmaDetails_exception() throws Exception {
        when(maternalHealthService.getPmsmaRecords(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/pmsma/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }

    @Test
    void saveAllPmsmaRecords_exception() throws Exception {
        List<PmsmaDTO> dtos = Collections.singletonList(new PmsmaDTO());
        when(maternalHealthService.savePmsmaRecords(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/maternalCare/pmsma/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void savePNCVisit_exception() throws Exception {
        List<PNCVisitDTO> dtos = Collections.singletonList(new PNCVisitDTO());
        when(maternalHealthService.savePNCVisit(any())).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/maternalCare/pnc/saveAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(dtos)))
                .andExpect(status().isOk());
    }

    @Test
    void getPNCVisitDetails_exception() throws Exception {
        when(maternalHealthService.getPNCVisits(any())).thenThrow(new RuntimeException("fail"));
        GetBenRequestHandler req = new GetBenRequestHandler();
        mockMvc.perform(post("/maternalCare/pnc/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(new Gson().toJson(req)))
                .andExpect(status().isOk());
    }
}
