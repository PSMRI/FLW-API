package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.identity.RMNCHMBeneficiarydetail;
import com.iemr.flw.domain.iemr.IncentiveActivity;
import com.iemr.flw.domain.iemr.IncentiveActivityLangMapping;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;
import com.iemr.flw.domain.iemr.IncentivePendingActivity;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.IncentiveName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.service.MaaMeetingService;
import com.iemr.flw.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IncentiveServiceImpl implements IncentiveService {
    private final Logger logger = LoggerFactory.getLogger(ChildCareServiceImpl.class);

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private IncentiveActivityLangMappingRepo incentiveActivityLangMappingRepo;

    @Autowired
    IncentivesRepo incentivesRepo;

    @Autowired
    IncentiveRecordRepo recordRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IncentivePendingActivityRepository incentivePendingActivityRepository;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private MaaMeetingService maaMeetingService;

    @Autowired
    private ChildCareServiceImpl childCareService;

    @Override
    public String saveIncentivesMaster(List<IncentiveActivityDTO> activityDTOS) {
        try {
            activityDTOS.forEach(activityDTO -> {
                IncentiveActivity activity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup(activityDTO.getName(), activityDTO.getGroup());

                if (activity == null) {
                    activity = new IncentiveActivity();
                    modelMapper.map(activityDTO, activity);
                } else {
                    Long id = activity.getId();
                    modelMapper.map(activityDTO, activity);
                    activity.setId(id);
                }
                incentivesRepo.save(activity);
            });
            String saved = "";
            activityDTOS.forEach(dto -> saved.concat(dto.getGroup() + ": " + dto.getName()));
            return "saved master data for " + saved;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String getIncentiveMaster(IncentiveRequestDTO incentiveRequestDTO) {
        try {

            List<IncentiveActivity> incs = new ArrayList<>();
            if (incentiveRequestDTO.getState() == StateCode.CG.getStateCode()) {
                incs = incentivesRepo.findAll().stream().filter(incentiveActivity -> incentiveActivity.getGroup().equals("ACTIVITY")).collect(Collectors.toList());

            } else {
                incs = incentivesRepo.findAll().stream().filter(incentiveActivity -> !incentiveActivity.getGroup().equals("ACTIVITY")).collect(Collectors.toList());

            }
            List<IncentiveActivityDTO> dtos = incs.stream().map(inc -> {
                IncentiveActivityDTO dto = modelMapper.map(inc, IncentiveActivityDTO.class);

                // Fetch language mapping
                IncentiveActivityLangMapping mapping = incentiveActivityLangMappingRepo
                        .findByIdAndName(inc.getId(), inc.getName());


                if (mapping != null) {
                    dto.setName(mapping.getName());
                    dto.setGroupName(mapping.getGroup());
                    if (Objects.equals(incentiveRequestDTO.getLangCode(), "en")) {
                        dto.setDescription(inc.getDescription());


                    } else if (Objects.equals(incentiveRequestDTO.getLangCode(), "as")) {

                        if (mapping.getAssameActivityDescription() != null) {
                            dto.setDescription(mapping.getAssameActivityDescription());

                        } else {
                            dto.setDescription(mapping.getDescription());

                        }

                    } else if (Objects.equals(incentiveRequestDTO.getLangCode(), "hi")) {
                        if (mapping.getHindiActivityDescription() != null) {
                            dto.setDescription(mapping.getHindiActivityDescription());

                        } else {
                            dto.setDescription(mapping.getDescription());

                        }

                    }

                } else {
                    dto.setGroupName(inc.getGroup());

                }

                return dto;
            }).collect(Collectors.toList());

            Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();

            return gson.toJson(dtos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String getAllIncentivesByUserId(GetBenRequestHandler request) {

        if (request.getVillageID() != StateCode.CG.getStateCode()) {
            checkMonthlyAshaIncentive(request.getAshaId());
        }

        List<IncentiveRecordDTO> dtos = new ArrayList<>();
        List<IncentiveActivityRecord> entities = recordRepo.findRecordsByAsha(request.getAshaId());
        if (request.getVillageID() == StateCode.CG.getStateCode()) {
            entities = entities.stream().filter(entitie -> incentivesRepo.findIncentiveMasterById(entitie.getActivityId(), true) != null).collect(Collectors.toList());

        } else {
            entities = entities.stream().filter(entitie -> incentivesRepo.findIncentiveMasterById(entitie.getActivityId(), false) != null).collect(Collectors.toList());

        }
        entities.forEach(entry -> {
            if (entry.getName() == null) {
                if (entry.getBenId() != 0 && entry.getBenId() > 0L) {
                    Long regId = beneficiaryRepo.getBenRegIdFromBenId(entry.getBenId());
                    logger.info("rmnchBeneficiaryDetailsRmnch" + regId);
                    BigInteger benDetailId = beneficiaryRepo.findByBenRegIdFromMapping(BigInteger.valueOf(regId)).getBenDetailsId();
                    RMNCHMBeneficiarydetail rmnchBeneficiaryDetails = beneficiaryRepo.findByBeneficiaryDetailsId(benDetailId);
                    String beneName = rmnchBeneficiaryDetails.getFirstName() + " " + rmnchBeneficiaryDetails.getLastName();
                    entry.setName(beneName);

                } else {
                    entry.setName("");

                }

            }

            dtos.add(modelMapper.map(entry, IncentiveRecordDTO.class));

        });

        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy h:mm:ss a").create();
        return gson.toJson(dtos);
    }

    @Override
    public String updateIncentive(PendingActivityDTO pendingActivityDTO) {

        if (pendingActivityDTO == null) {
            return "Invalid request";
        }

        IncentiveName incentiveName;
        try {
            incentiveName = IncentiveName.valueOf(pendingActivityDTO.getName());
        } catch (IllegalArgumentException e) {
            return "Invalid incentive name";
        }

        IncentiveActivity incentiveActivity =
                incentivesRepo.findIncentiveMasterByNameAndGroup(
                        pendingActivityDTO.getName(),
                        pendingActivityDTO.getModuleName()
                );

        if (incentiveActivity == null) {
            return null;
        }

        Optional<IncentivePendingActivity> optionalPendingActivity =
                incentivePendingActivityRepository
                        .findByMincentiveIdAndActivityId(
                                pendingActivityDTO.getId(),
                                incentiveActivity.getId()
                        );

        if (!optionalPendingActivity.isPresent()) {
            return null;
        }

        IncentivePendingActivity existingActivity = optionalPendingActivity.get();

        if (!existingActivity.getActivityId().equals(pendingActivityDTO.getId())) {
            return null;
        }

        if (pendingActivityDTO.getImages() == null || pendingActivityDTO.getImages().isEmpty()) {
            return null;
        }

        try {
            MaaMeetingRequestDTO maaMeetingRequestDTO = new MaaMeetingRequestDTO();
            maaMeetingRequestDTO.setMeetingImages(
                    pendingActivityDTO.getImages().toArray(new MultipartFile[0])
            );

            // ✅ ENUM BASED CHECK
            if(pendingActivityDTO.getModuleName().equals(GroupName.ACTIVITY)){
                if (incentiveName == IncentiveName.MAA_QUARTERLY_MEETING) {

                    maaMeetingService.updateMeetingFromFileUpload(
                            maaMeetingRequestDTO,
                            pendingActivityDTO.getId()
                    );
                }
            }else {
                if (incentiveName == IncentiveName.MAA_QUARTERLY_MEETING) {

                    maaMeetingService.updateMeetingFromFileUpload(
                            maaMeetingRequestDTO,
                            pendingActivityDTO.getId()
                    );
                }

                if (incentiveName == IncentiveName.HBNC_0_42_DAYS) {

                    childCareService.updateHbncFromFileUpload(
                            pendingActivityDTO.getImages().toArray(new MultipartFile[0]),
                            pendingActivityDTO.getId(),
                            existingActivity.getRecordId()

                    );
                }


            }




        } catch (Exception e) {
            return e.getMessage();
        }

        return null;
    }

    private void checkMonthlyAshaIncentive(Integer ashaId) {
        IncentiveActivity MOBILEBILLREIMB_ACTIVITY = incentivesRepo.findIncentiveMasterByNameAndGroup("MOBILE_BILL_REIMB", GroupName.OTHER_INCENTIVES.getDisplayName());
        IncentiveActivity ADDITIONAL_ASHA_INCENTIVE = incentivesRepo.findIncentiveMasterByNameAndGroup("ADDITIONAL_ASHA_INCENTIVE", GroupName.ADDITIONAL_INCENTIVE.getDisplayName());
        IncentiveActivity ASHA_MONTHLY_ROUTINE = incentivesRepo.findIncentiveMasterByNameAndGroup("ASHA_MONTHLY_ROUTINE", GroupName.ASHA_MONTHLY_ROUTINE.getDisplayName());
        if (MOBILEBILLREIMB_ACTIVITY != null) {
            addMonthlyAshaIncentiveRecord(MOBILEBILLREIMB_ACTIVITY, ashaId);
        }
        if (ADDITIONAL_ASHA_INCENTIVE != null) {
            addMonthlyAshaIncentiveRecord(ADDITIONAL_ASHA_INCENTIVE, ashaId);

        }

        if (ASHA_MONTHLY_ROUTINE != null) {
            addMonthlyAshaIncentiveRecord(ASHA_MONTHLY_ROUTINE, ashaId);

        }
    }

    private void addMonthlyAshaIncentiveRecord(IncentiveActivity incentiveActivity, Integer ashaId) {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        Timestamp startOfMonth = Timestamp.valueOf(LocalDate.now().withDayOfMonth(1).atStartOfDay());
        Timestamp endOfMonth = Timestamp.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59));

        IncentiveActivityRecord record = recordRepo.findRecordByActivityIdCreatedDateBenId(
                incentiveActivity.getId(),
                startOfMonth,
                endOfMonth,
                0L,
                ashaId
        );


        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(incentiveActivity.getId());
            record.setCreatedDate(timestamp);
            record.setCreatedBy(userRepo.getUserNamedByUserId(ashaId));
            record.setStartDate(timestamp);
            record.setEndDate(timestamp);
            record.setUpdatedDate(timestamp);
            record.setUpdatedBy(userRepo.getUserNamedByUserId(ashaId));
            record.setBenId(0L);
            record.setAshaId(ashaId);
            record.setAmount(Long.valueOf(incentiveActivity.getRate()));
            recordRepo.save(record);
        }
    }


}
