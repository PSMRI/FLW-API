package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.DeliveryOutcome;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;
import com.iemr.flw.repo.iemr.DeliveryOutcomeRepo;
import com.iemr.flw.service.DeliveryOutcomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DeliveryOutcomeServiceImpl implements DeliveryOutcomeService {

    @Autowired
    private DeliveryOutcomeRepo deliveryOutcomeRepo;

    private final Logger logger = LoggerFactory.getLogger(DeliveryOutcomeServiceImpl.class);

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String registerDeliveryOutcome(List<DeliveryOutcomeDTO> deliveryOutcomeDTOS) {

        try {
            List<DeliveryOutcome> delOutList = new ArrayList<>();
            deliveryOutcomeDTOS.forEach(it -> {
                DeliveryOutcome deliveryOutcome =
                        mapper.convertValue(it, DeliveryOutcome.class);
                delOutList.add(deliveryOutcome);
            });
            deliveryOutcomeRepo.save(delOutList);

            logger.info("Delivery Outcome details saved");
            return "saved successfully";
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public DeliveryOutcomeDTO getDeliveryOutcome(Long benId) {
        try{
            DeliveryOutcome deliveryOutcome = deliveryOutcomeRepo.getDeliveryOutcomeByBenId(benId);
            return mapper.convertValue(deliveryOutcome, DeliveryOutcomeDTO.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
