package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;

import java.util.List;

public interface DeliveryOutcomeService {

    String registerDeliveryOutcome(List<DeliveryOutcomeDTO> deliveryOutcomeDTOS);

    List<DeliveryOutcomeDTO> getDeliveryOutcome(GetBenRequestHandler dto);
}
