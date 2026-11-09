package com.iemr.flw.service.impl;

import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarymapping;
import com.iemr.flw.domain.iemr.TBScreening;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.DiagnosticOrderRepo;
import com.iemr.flw.repo.iemr.TBScreeningRepo;
import com.iemr.flw.service.CampConfigService;
import com.iemr.flw.service.DiagnosticOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StopTBReferralOrderServiceTest {

    private static final long REG_ID = 359055L;
    private static final long BEN_ID = 601413456541L;

    @Mock private DiagnosticOrderService diagnosticOrderService;
    @Mock private DiagnosticOrderRepo diagnosticOrderRepo;
    @Mock private TBScreeningRepo tbScreeningRepo;
    @Mock private BeneficiaryRepo beneficiaryRepo;
    @Mock private CampConfigService campConfigService;

    @InjectMocks private StopTBReferralOrderService service;

    private void givenBeneficiary(LocalDateTime screenedAt) {
        TBScreening screening = new TBScreening();
        screening.setBenRegID(REG_ID);
        screening.setVisitDate(Timestamp.valueOf(screenedAt));
        when(tbScreeningRepo.findLatestByBenRegID(eq(REG_ID), any(Pageable.class))).thenReturn(List.of(screening));
        when(beneficiaryRepo.getBenIdFromRegID(REG_ID)).thenReturn(BigInteger.valueOf(BEN_ID));

        RMNCHMBeneficiarymapping mapping = new RMNCHMBeneficiarymapping();
        mapping.setBenDetailsId(BigInteger.TEN);
        when(beneficiaryRepo.findByBenRegIdFromMapping(BigInteger.valueOf(REG_ID))).thenReturn(List.of(mapping));
        RMNCHMBeneficiarydetail detail = new RMNCHMBeneficiarydetail();
        detail.setFirstName(" Arun ");
        detail.setLastName(null);
        detail.setDob(Timestamp.valueOf(LocalDateTime.of(2019, 3, 4, 0, 0)));
        detail.setGender("Male");
        when(beneficiaryRepo.getDetailsById(BigInteger.TEN)).thenReturn(detail);
    }

    @Test
    void xrayReferralTodayWithNoOrderCreatesXrayOrder() throws Exception {
        givenBeneficiary(LocalDateTime.now());
        when(diagnosticOrderRepo.hasOrderTodayOrOpen(anyLong(), anyString(), any())).thenReturn(false);

        service.createOrders(new StopTBReferralOrderService.Referral(REG_ID, true, false));

        ArgumentCaptor<DiagnosticOrderRequestDto> captor = ArgumentCaptor.forClass(DiagnosticOrderRequestDto.class);
        verify(diagnosticOrderService, times(1)).createAndPushOrderAsSystem(captor.capture());
        DiagnosticOrderRequestDto request = captor.getValue();
        assertEquals(BEN_ID, request.getBeneficiaryId());
        assertEquals("XRAY_CHEST", request.getOrderType());
        assertEquals("STOP_TB_REFERRAL", request.getOrderEvent());
        assertEquals("Arun", request.getPatient().getFirstName());
        assertEquals("", request.getPatient().getLastName());
        assertEquals("2019-03-04", request.getPatient().getDateOfBirth());
        assertEquals("Male", request.getPatient().getSex());
    }

    @Test
    void sputumReferralCreatesMtbOrder() throws Exception {
        givenBeneficiary(LocalDateTime.now());
        when(diagnosticOrderRepo.hasOrderTodayOrOpen(anyLong(), anyString(), any())).thenReturn(false);

        service.createOrders(new StopTBReferralOrderService.Referral(REG_ID, false, true));

        ArgumentCaptor<DiagnosticOrderRequestDto> captor = ArgumentCaptor.forClass(DiagnosticOrderRequestDto.class);
        verify(diagnosticOrderService).createAndPushOrderAsSystem(captor.capture());
        assertEquals("MTB", captor.getValue().getOrderType());
    }

    @Test
    void existingOrderTodayOrOpenIsNotDuplicated() throws Exception {
        givenBeneficiary(LocalDateTime.now());
        when(diagnosticOrderRepo.hasOrderTodayOrOpen(eq(BEN_ID), eq("XRAY_CHEST"), any())).thenReturn(true);

        service.createOrders(new StopTBReferralOrderService.Referral(REG_ID, true, false));

        verify(diagnosticOrderService, never()).createAndPushOrderAsSystem(any());
    }

    @Test
    void screeningFromAnEarlierDayGetsNoOrder() throws Exception {
        givenBeneficiary(LocalDate.now().minusDays(1).atTime(17, 14));
        when(diagnosticOrderRepo.hasOrderTodayOrOpen(anyLong(), anyString(), any())).thenReturn(false);

        service.createOrders(new StopTBReferralOrderService.Referral(REG_ID, true, true));

        verify(diagnosticOrderService, never()).createAndPushOrderAsSystem(any());
    }

    @Test
    void vendorFailureIsContainedPerOrderType() throws Exception {
        givenBeneficiary(LocalDateTime.now());
        when(diagnosticOrderRepo.hasOrderTodayOrOpen(anyLong(), anyString(), any())).thenReturn(false);
        when(diagnosticOrderService.createAndPushOrderAsSystem(any())).thenThrow(new RuntimeException("device down"));

        service.createOrders(new StopTBReferralOrderService.Referral(REG_ID, true, true));

        verify(diagnosticOrderService, times(2)).createAndPushOrderAsSystem(any());
    }

    @Test
    void notACampHubDoesNothing() {
        when(campConfigService.isCampConfigured()).thenReturn(false);

        service.createAfterCommit(List.of(new StopTBReferralOrderService.Referral(REG_ID, true, true)));
        service.sweep();

        verifyNoInteractions(tbScreeningRepo, diagnosticOrderRepo, diagnosticOrderService, beneficiaryRepo);
    }

    @Test
    void sexMapping() {
        assertEquals("Male", StopTBReferralOrderService.sexOf("male", null));
        assertEquals("Female", StopTBReferralOrderService.sexOf(null, 2));
        assertEquals("Other", StopTBReferralOrderService.sexOf("Transgender", 3));
        assertEquals("Other", StopTBReferralOrderService.sexOf(null, null));
    }
}
