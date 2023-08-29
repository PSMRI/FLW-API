package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.DeliveryOutcome;
import com.iemr.flw.domain.iemr.EligibleCoupleTracking;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.DeliveryOutcomeDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.DeliveryOutcomeRepo;
import com.iemr.flw.service.DeliveryOutcomeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DeliveryOutcomeServiceImpl implements DeliveryOutcomeService {

    @Autowired
    private DeliveryOutcomeRepo deliveryOutcomeRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    private final Logger logger = LoggerFactory.getLogger(DeliveryOutcomeServiceImpl.class);

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public String registerDeliveryOutcome(List<DeliveryOutcomeDTO> deliveryOutcomeDTOS) {

        try {
            List<DeliveryOutcome> delOutList = new ArrayList<>();
            deliveryOutcomeDTOS.forEach(it -> {
                DeliveryOutcome deliveryoutcome = deliveryOutcomeRepo.findDeliveryOutcomeByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());

                if (deliveryoutcome != null) {
                    Long id = deliveryoutcome.getId();
                    modelMapper.map(it, deliveryoutcome);
                    deliveryoutcome.setId(id);
                } else {
                    deliveryoutcome = new DeliveryOutcome();
                    modelMapper.map(it, deliveryoutcome);
                    deliveryoutcome.setId(null);
                }
                delOutList.add(deliveryoutcome);
            });
            deliveryOutcomeRepo.save(delOutList);
            return "no of delivery outcome details saved: " + deliveryOutcomeDTOS.size();
        } catch (Exception e) {
            return "error while saving delivery outcome details: " + e.getMessage();
        }
    }
    @Override
    public List<DeliveryOutcomeDTO> getDeliveryOutcome(GetBenRequestHandler dto) {
        try{
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<DeliveryOutcome> deliveryOutcomeList = deliveryOutcomeRepo.getDeliveryOutcomeByBenId(user, dto.getFromDate(), dto.getToDate());
            return deliveryOutcomeList.stream()
                    .map(deliveryOutcome -> mapper.convertValue(deliveryOutcome, DeliveryOutcomeDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
