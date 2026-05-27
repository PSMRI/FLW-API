package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.domain.iemr.InfantRegister;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.InfantRegisterDTO;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.IncentiveRecordRepo;
import com.iemr.flw.repo.iemr.IncentivesRepo;
import com.iemr.flw.repo.iemr.InfantRegisterRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.InfantService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfantServiceImpl implements InfantService {

    private final Logger logger = LoggerFactory.getLogger(InfantServiceImpl.class);

    @Autowired
    private InfantRegisterRepo infantRegisterRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private IncentiveRecordRepo incentiveRecordRepo;

    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Override
    public String registerInfant(List<InfantRegisterDTO> infantRegisterDTOs) {

        try {
            List<InfantRegister> infantList = new ArrayList<>();
            infantRegisterDTOs.forEach(it -> {
                InfantRegister infantRegister = infantRegisterRepo.findInfantRegisterByBenIdAndBabyIndexAndIsActive(it.getBenId(), it.getBabyIndex(), true);

                if (infantRegister != null) {
                    Long id = infantRegister.getId();
                    modelMapper.map(it, infantRegister);
                    infantRegister.setId(id);
                } else {
                    infantRegister = new InfantRegister();
                    modelMapper.map(it, infantRegister);
                    infantRegister.setId(null);
                }
                infantList.add(infantRegister);
            });
            infantRegisterRepo.saveAll(infantList);
            checkAndGenerateIncentive(infantList);
            return "no of infant register details saved: " + infantList.size();
        } catch (Exception e) {
            logger.info("error while saving infant register details: " + e.getMessage());
        }
        return null;
    }

    private void checkAndGenerateIncentive(List<InfantRegister> infantList) {
        IncentiveActivity incentiveActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("HBNC_0_42_DAYS", GroupName.CHILD_HEALTH.getDisplayName());
        IncentiveActivity incentiveActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("HBNC_0_42_DAYS", GroupName.ACTIVITY.getDisplayName());

        infantList.forEach(infantRegister -> {
            if(incentiveActivityAM!=null){
                if(infantRegister.getIsSNCU().equals("Yes")){
                    addIsSncuIncentive(incentiveActivityAM,infantRegister.getBenId(),infantRegister.getCreatedBy(),infantRegister.getCreatedDate());
                }

            }

            if(incentiveActivityCH!=null){
                if(infantRegister.getIsSNCU().equals("Yes")){
                    addIsSncuIncentive(incentiveActivityCH,infantRegister.getBenId(),infantRegister.getCreatedBy(),infantRegister.getCreatedDate());

                }
            }
        });
    }

    private void addIsSncuIncentive(IncentiveActivity activity, Long benId, String createdBy, Timestamp createdDate) {
        IncentiveActivityRecord incentiveActivityRecord = incentiveRecordRepo.findRecordByActivityIdCreatedDateBenId(activity.getId(),createdDate,benId);

        if(incentiveActivityRecord==null){
            incentiveActivityRecord = new IncentiveActivityRecord();
            incentiveActivityRecord.setActivityId(activity.getId());
            incentiveActivityRecord.setCreatedDate(createdDate);
            incentiveActivityRecord.setCreatedBy(createdBy);
            incentiveActivityRecord.setStartDate(createdDate);
            incentiveActivityRecord.setEndDate(createdDate);
            incentiveActivityRecord.setUpdatedDate(createdDate);
            incentiveActivityRecord.setUpdatedBy(createdBy);
            incentiveActivityRecord.setBenId(benId);
            incentiveActivityRecord.setAshaId(userServiceRoleRepo.getUserIdByName(createdBy));
            incentiveActivityRecord.setAmount(Long.valueOf(activity.getRate()));
            incentiveRecordRepo.save(incentiveActivityRecord);
        }
    }

    @Override
    public List<InfantRegisterDTO> getInfantDetails(GetBenRequestHandler dto) {
        try{
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<InfantRegister> infantRegisterList =
                    infantRegisterRepo.getInfantDetailsForUser(user, dto.getFromDate(), dto.getToDate());

            return infantRegisterList.stream()
                    .map(infantRegister -> mapper.convertValue(infantRegister, InfantRegisterDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.info("error while fetching infant register details: " + e.getMessage());
        }
        return null;
    }
}
