package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.DiagnosticOrder;
import com.iemr.flw.dto.DiagnosticOrderRequestDto;
import com.iemr.flw.dto.DiagnosticOrderResultDto;
import com.iemr.flw.dto.DiagnosticOrderStatusSummaryDto;
import com.iemr.flw.integration.provider.DiagnosticPollResult;

import java.util.List;

public interface DiagnosticOrderService {

    DiagnosticOrder createAndPushOrder(DiagnosticOrderRequestDto request) throws Exception;

    DiagnosticOrderResultDto processResult(DiagnosticOrder order, DiagnosticPollResult result) throws Exception;

    DiagnosticPollResult pollOnce(DiagnosticOrder order) throws Exception;

    DiagnosticOrderResultDto triggerManualPoll(Long benRegID, String orderType) throws Exception;

    DiagnosticOrder markTestCompleted(Long benRegID, String orderType) throws Exception;

    DiagnosticOrderResultDto getOrderResult(Long benRegID, String orderType);

    DiagnosticOrder getOrder(Long benRegID, String orderType) throws Exception;

    List<DiagnosticOrder> getOrdersByBenRegId(Long benRegID) throws Exception;

    DiagnosticOrderStatusSummaryDto getOrderStatusSummary(String orderType, Integer villageId, Integer providerServiceMapId);
}
