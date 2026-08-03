package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
import com.iemr.flw.dto.DiagnosticOrderStatusSummaryDto;
import com.iemr.flw.dto.VendorHealthDto;
import com.iemr.flw.integration.provider.DiagnosticPollResult;

import java.util.List;

public interface DiagnosticOrderService {

    DiagnosticOrder createAndPushOrder(DiagnosticOrderRequestDto request) throws Exception;

    DiagnosticOrderResultDto processResult(DiagnosticOrder order, DiagnosticPollResult result) throws Exception;

    DiagnosticPollResult pollOnce(DiagnosticOrder order) throws Exception;

    DiagnosticOrderResultDto triggerManualPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception;

    DiagnosticOrder retryPoll(Long beneficiaryId, String orderType, Long visitCode) throws Exception;

    DiagnosticOrderResultDto getOrderResult(Long beneficiaryId, String orderType, Long visitCode);

    DiagnosticOrder getOrder(Long beneficiaryId, String orderType, Long visitCode) throws Exception;

    List<DiagnosticOrder> getOrdersByBeneficiaryId(Long beneficiaryId) throws Exception;

    DiagnosticOrderStatusSummaryDto getOrderStatusSummary(String orderType, Integer villageId, Integer providerServiceMapId);

    VendorHealthDto checkVendorHealth(String orderType) throws Exception;
}
