package com.iemr.flw.service;

import com.iemr.flw.dto.DeliveryOutcomeDTO;

import java.util.List;

public interface DeliveryOutcomeService {

    String registerDeliveryOutcome(List<DeliveryOutcomeDTO> deliveryOutcomeDTOS);

    DeliveryOutcomeDTO getDeliveryOutcome(Long benId);
}
