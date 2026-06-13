package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.iemr.ChildRegister;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.domain.iemr.UserServiceRole;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.ChildRegisterDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.ChildRegisterRepo;
import com.iemr.flw.repo.iemr.UserServiceRoleRepo;
import com.iemr.flw.service.ChildService;
import com.iemr.flw.service.IncentiveLogicService;
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
public class ChildServiceImpl implements ChildService {

    private final Logger logger = LoggerFactory.getLogger(CbacServiceImpl.class);
    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private ChildRegisterRepo childRepo;
    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private IncentiveLogicService incentiveLogicService;

    @Autowired
    private UserServiceRoleRepo userServiceRoleRepo;

    public String getByUserId(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<ChildRegister> childRegisterList = childRepo.getChildDetailsForUser(user, dto.getFromDate(), dto.getToDate());

//            List<ChildRegisterDTO> result = new ArrayList<>();
//            childRegisterList.forEach(childRegister -> {
//                ChildRegisterDTO childDTO = modelMapper.map(childRegister, ChildRegisterDTO.class);
//                result.add(childDTO);
//            });
            for (ChildRegister childRegister : childRegisterList) {
                processFirstChildIncentive(childRegister);
            }
            List<ChildRegisterDTO> result = childRegisterList.stream()
                    .map(childRegister -> modelMapper.map(childRegister, ChildRegisterDTO.class))
                    .collect(Collectors.toList());
            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
            return gson.toJson(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String save(List<ChildRegisterDTO> childRegisterDTOs) throws Exception {
        List<ChildRegister> listToBeSaved = new ArrayList<>();
        childRegisterDTOs.forEach(childRegisterDTO ->
        {
            ChildRegister childRegister =
                    childRepo.findChildRegisterByBenIdAndCreatedDate(childRegisterDTO.getBenId(), childRegisterDTO.getCreatedDate());

            if (childRegister == null) {
                childRegister = new ChildRegister();
                modelMapper.map(childRegisterDTO, childRegister);
                childRegister.setId(null);
            } else {
                Long id = childRegister.getId();
                modelMapper.map(childRegisterDTO, childRegister);
                childRegister.setId(id);
            }
            listToBeSaved.add(childRegister);
        });
        childRepo.saveAll(listToBeSaved);
        for (ChildRegister childRegister : listToBeSaved) {
            processFirstChildIncentive(childRegister);
        }
        for (ChildRegister childRegister : listToBeSaved) {
            processSecondChildGapIncentive(childRegister);
        }
        return "no of child details saved: " + childRegisterDTOs.size();
    }

    public void processFirstChildIncentive(ChildRegister childRegister) {

        Long benId = childRegister.getBenId();

        RMNCHBeneficiaryDetailsRmnch beneficiary =
                beneficiaryRepo.findByBenficieryid(benId).orElse(null);

      logger.info("Child ben Id:" + beneficiary.getBenficieryid());

        if (beneficiary == null || beneficiary.getDateMarriage() == null) {
            return;
        }

      // First child validation
        Long childCount = childRepo.countByBenId(benId);

        if (childCount != 1) {
            return;
        }

        Timestamp marriageDate = beneficiary.getDateMarriage();

       // Temporary: using createdDate as child DOB
        Timestamp childBirthDate = childRegister.getCreatedDate();

        long diffMillis = childBirthDate.getTime() - marriageDate.getTime();

        long days = diffMillis / (1000 * 60 * 60 * 24);

       // >= 2 years
        if (days >= 730) {

            Integer userId =
                    userServiceRoleRepo.getUserIdByName(childRegister.getCreatedBy());

            incentiveLogicService.incentiveForChildBirthGap(
                    benId,
                    childBirthDate,
                    childBirthDate,
                    userId
            );
        }
    }

    private void processSecondChildGapIncentive(ChildRegister currentChild) {

        Long benId = currentChild.getBenId();

        // Total children count
        Long childCount = childRepo.countByBenId(benId);

        // Applicable only for second child
        if (childCount != 2) {
            return;
        }

        List<ChildRegister> children =
                childRepo.findByBenIdOrderByCreatedDateAsc(benId);

        if (children.size() < 2) {
            return;
        }

        // First child
        ChildRegister firstChild = children.get(0);

        // Second child
        ChildRegister secondChild = children.get(1);

        Timestamp firstChildDob = firstChild.getCreatedDate();

        Timestamp secondChildDob = secondChild.getCreatedDate();

        long diffMillis =
                secondChildDob.getTime() - firstChildDob.getTime();

        long days = diffMillis / (1000 * 60 * 60 * 24);

        // >= 3 years
        if (days >= 1095) {

            Integer userId =
                    userServiceRoleRepo.getUserIdByName(
                            currentChild.getCreatedBy());

            incentiveLogicService.incentiveForSecondChildGap(
                    benId,
                    secondChildDob,
                    secondChildDob,
                    userId
            );
        }
    }
}
