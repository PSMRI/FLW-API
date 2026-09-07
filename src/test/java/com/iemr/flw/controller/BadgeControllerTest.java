package com.iemr.flw.controller;

import com.iemr.flw.dto.iemr.BadgeEarnedDTO;
import com.iemr.flw.service.BadgeService;
import com.iemr.flw.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BadgeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BadgeService badgeService;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new BadgeController(badgeService, jwtUtil)).build();
    }

    @Test
    void config_returnsKeyValueMapUnderConfig() throws Exception {
        when(badgeService.getConfig()).thenReturn(Map.of("milestones.steady_syncer", "2,4,6,8"));

        mockMvc.perform(get("/badges/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.config['milestones.steady_syncer']").value("2,4,6,8"));
    }

    @Test
    void earned_usesUserIdFromJwtNotFromBody() throws Exception {
        when(jwtUtil.extractUserId("tok")).thenReturn(960);
        when(badgeService.saveEarned(eq(960), any())).thenReturn(1);

        mockMvc.perform(post("/badges/earned")
                        .header("JwtToken", "tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":12345,\"badges\":[{\"badgeId\":\"steady_syncer\",\"level\":2,\"earnedAt\":1}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inserted").value(1));

        ArgumentCaptor<List<BadgeEarnedDTO>> captor = ArgumentCaptor.forClass(List.class);
        verify(badgeService).saveEarned(eq(960), captor.capture());
        assertEquals("steady_syncer", captor.getValue().get(0).getBadgeId());
    }

    @Test
    void getEarned_returnsListUnderEarned() throws Exception {
        when(jwtUtil.extractUserId("tok")).thenReturn(960);
        when(badgeService.getEarned(960)).thenReturn(List.of(new BadgeEarnedDTO("timely_reporter", 1, 5L)));

        mockMvc.perform(get("/badges/earned").header("JwtToken", "tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.earned[0].badgeId").value("timely_reporter"))
                .andExpect(jsonPath("$.earned[0].level").value(1));
    }

    @Test
    void invalidJwt_returns500WithErrorShape() throws Exception {
        when(jwtUtil.extractUserId("bad")).thenThrow(new RuntimeException("Invalid JWT token."));

        mockMvc.perform(get("/badges/earned").header("JwtToken", "bad"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("Error"));
    }
}
