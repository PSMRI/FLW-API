package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.IncentiveLogicService;
import com.iemr.flw.service.IncentiveService;
import com.iemr.flw.service.MaternalHealthService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaternalHealthServiceImpl implements MaternalHealthService {

    private final Logger logger = LoggerFactory.getLogger(MaternalHealthServiceImpl.class);

    @Autowired
    PregnantWomanRegisterRepo pregnantWomanRegisterRepo;

    @Autowired
    private ANCVisitRepo ancVisitRepo;

    @Autowired
    private AncCareRepo ancCareRepo;

    @Autowired
    private PNCVisitRepo pncVisitRepo;

    @Autowired
    private PNCCareRepo pncCareRepo;

    @Autowired
    private BenVisitDetailsRepo benVisitDetailsRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private PmsmaRepo pmsmaRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IncentiveLogicService incentiveLogicService;


    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();


    @Autowired
    private SMSServiceImpl smsServiceImpl;

    @Autowired
    private AncCounsellingCareRepo ancCounsellingCareRepo;


    public static final List<String> PNC_PERIODS =
            Arrays.asList("1st Day", "3rd Day", "7th Day", "14th Day", "21st Day", "28th Day", "42nd Day");

    @Override
    public String registerPregnantWoman(List<PregnantWomanDTO> pregnantWomanDTOs) {

        try {
            List<PregnantWomanRegister> pwrList = new ArrayList<>();
            pregnantWomanDTOs.forEach(it -> {
                PregnantWomanRegister pwr =
                        pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(it.getBenId(), true);

                if (pwr != null) {
                    Long id = pwr.getId();
                    modelMapper.map(it, pwr);
                    pwr.setId(id);
                } else {
                    pwr = new PregnantWomanRegister();
                    modelMapper.map(it, pwr);
                    pwr.setId(null);
                }
                pwrList.add(pwr);
            });
            pregnantWomanRegisterRepo.saveAll(pwrList);

            logger.info(pwrList.size() + " Pregnant Woman details saved");
            return "no of pwr details saved: " + pwrList.size();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<PregnantWomanDTO> getPregnantWoman(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<PregnantWomanRegister> pregnantWomanRegisterList =
                    pregnantWomanRegisterRepo.getPWRWithBen(user, dto.getFromDate(), dto.getToDate());

            return pregnantWomanRegisterList.stream()
                    .map(pregnantWomanRegister -> mapper.convertValue(pregnantWomanRegister, PregnantWomanDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<ANCVisitDTO> getANCVisits(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<ANCVisit> ancVisits = ancVisitRepo.getANCForPW(user, dto.getFromDate(), dto.getToDate());
            return ancVisits.stream()
                    .map(anc -> mapper.convertValue(anc, ANCVisitDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String saveANCVisit(List<ANCVisitDTO> ancVisitDTOs) {
        try {
            List<ANCVisit> ancList = new ArrayList<>();
            List<AncCare> ancCareList = new ArrayList<>();
            ancVisitDTOs.forEach(it -> {
                ANCVisit ancVisit =
                        ancVisitRepo.findANCVisitByBenIdAndAncVisitAndIsActive(it.getBenId(), it.getAncVisit(), true);

                if (ancVisit != null) {
                    Long id = ancVisit.getId();
                    modelMapper.map(it, ancVisit);
                    ancVisit.setId(id);
                } else {
                    ancVisit = new ANCVisit();
                    modelMapper.map(it, ancVisit);
                    ancVisit.setId(null);

                    Long benRegId = beneficiaryRepo.getRegIDFromBenId(it.getBenId());

                    // Saving data in BenVisitDetails table
                    PregnantWomanRegister pwr = pregnantWomanRegisterRepo.findPregnantWomanRegisterByBenIdAndIsActive(it.getBenId(), true);
                    BenVisitDetail benVisitDetail = new BenVisitDetail();
                    modelMapper.map(it, benVisitDetail);
                    benVisitDetail.setBeneficiaryRegId(benRegId);
                    benVisitDetail.setVisitCategory("ANC");
                    benVisitDetail.setVisitReason("Follow Up");
                    benVisitDetail.setPregnancyStatus("Yes");
                    benVisitDetail.setProcessed("N");
                    benVisitDetail.setModifiedBy(it.getUpdatedBy());
                    benVisitDetail.setLastModDate(it.getUpdatedDate());
                    benVisitDetail.setProviderServiceMapID(it.getProviderServiceMapID());
                    benVisitDetail = benVisitDetailsRepo.save(benVisitDetail);

                    // Saving Data in AncCare table
                    AncCare ancCare = new AncCare();
                    modelMapper.map(it, ancCare);
                    ancCare.setBenVisitId(benVisitDetail.getBenVisitId());
                    ancCare.setBeneficiaryRegId(benRegId);
                    if (pwr != null) {
                        ancCare.setLastMenstrualPeriodLmp(pwr.getLmpDate());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(pwr.getLmpDate());
                        cal.add(Calendar.DAY_OF_WEEK, 280);
                        ancCare.setExpectedDateofDelivery(new Timestamp(cal.getTime().getTime()));
                    }
                    ancCare.setTrimesterNumber(it.getAncVisit().shortValue());
                    ancCare.setModifiedBy(it.getUpdatedBy());
                    ancCare.setLastModDate(it.getUpdatedDate());
                    ancCare.setProcessed("N");
                    ancCareList.add(ancCare);
                }
                ancList.add(ancVisit);
            });

            ancVisitRepo.saveAll(ancList);
            ancCareRepo.saveAll(ancCareList);
            checkAndAddIncentives(ancList);
            logger.info("ANC visit details saved");
            return "no of anc details saved: " + ancList.size();
        } catch (Exception e) {
            logger.info("Saving ANC visit details failed with error : " + e.getMessage());
            logger.info("Saving ANC visit details failed with error : " + e);
        }
        return null;
    }

    @Override
    public List<PmsmaDTO> getPmsmaRecords(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<PMSMA> pmsmaList = pmsmaRepo.getAllPmsmaByAshaId(user, dto.getFromDate(), dto.getToDate());
            return pmsmaList.stream()
                    .map(pmsma -> mapper.convertValue(pmsma, PmsmaDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String savePmsmaRecords(List<PmsmaDTO> pmsmaDTOs) {
        try {
            List<PMSMA> pmsmaList = new ArrayList<>();
            pmsmaDTOs.forEach(it -> {
                PMSMA pmsma =
                        pmsmaRepo.findPMSMAByBenIdAndIsActive(it.getBenId(), true);
                if (pmsma != null) {
                    Long id = pmsma.getId();
                    modelMapper.map(it, pmsma);
                    pmsma.setId(id);
                } else {
                    pmsma = new PMSMA();
                    modelMapper.map(it, pmsma);
                    pmsma.setId(null);
                }
                pmsmaList.add(pmsma);
            });
            pmsmaRepo.saveAll(pmsmaList);
            logger.info("PMSMA details saved");
            checkAndAddHighRisk(pmsmaList);

            return "No. of PMSMA records saved: " + pmsmaList.size();
        } catch (Exception e) {
            logger.info("Saving PMSMA details failed with error : " + e.getMessage());
        }
        return null;
    }

    private void checkAndAddHighRisk(List<PMSMA> pmsmaList) {
        IncentiveActivity incentiveActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("EPMSMA_HRP_IDENTIFIED", GroupName.MATERNAL_HEALTH.getDisplayName());
        IncentiveActivity incentiveActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("EPMSMA_HRP_IDENTIFIED", GroupName.ACTIVITY.getDisplayName());
        if (incentiveActivityAM != null) {
            pmsmaList.forEach(pmsma -> {
                if (pmsma.getHighRiskPregnant()) {
                    addIncentiveForHighRisk(incentiveActivityAM, pmsma);

                }

            });
        }

        if (incentiveActivityCH != null) {
            pmsmaList.forEach(pmsma -> {
                if (pmsma.getHighRiskPregnant()) {
                    addIncentiveForHighRisk(incentiveActivityCH, pmsma);

                }

            });
        }

    }

    private void addIncentiveForHighRisk(IncentiveActivity incentiveActivity, PMSMA pmsma) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(incentiveActivity.getId(), pmsma.getCreatedDate(), pmsma.getBenId());
        // get bene details

        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(incentiveActivity.getId());
            record.setCreatedDate(pmsma.getVisitDate());
            record.setCreatedBy(pmsma.getCreatedBy());
            record.setStartDate(pmsma.getVisitDate());
            record.setEndDate(pmsma.getVisitDate());
            record.setUpdatedDate(pmsma.getVisitDate());
            record.setUpdatedBy(pmsma.getCreatedBy());
            record.setBenId(pmsma.getBenId());
            record.setAshaId(userRepo.getUserIdByName(pmsma.getCreatedBy()));
            record.setAmount(Long.valueOf(incentiveActivity.getRate()));
            recordRepo.save(record);
        }
    }

    @Override
    public List<PNCVisitDTO> getPNCVisits(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<PNCVisit> pncVisits = pncVisitRepo.getPNCForPW(user);
            return pncVisits.stream()
                    .map(pnc -> mapper.convertValue(pnc, PNCVisitDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String savePNCVisit(List<PNCVisitDTO> pncVisitDTOs) {
        try {
            List<PNCVisit> pncList = new ArrayList<>();
            List<PNCCare> pncCareList = new ArrayList<>();
            pncVisitDTOs.forEach(it -> {
                PNCVisit pncVisit =
                        pncVisitRepo.findPNCVisitByBenIdAndPncPeriodAndIsActive(it.getBenId(), it.getPncPeriod(), true);

                if (pncVisit != null) {
                    Long id = pncVisit.getId();
                    modelMapper.map(it, pncVisit);
                    pncVisit.setId(id);
                } else {
                    pncVisit = new PNCVisit();
                    modelMapper.map(it, pncVisit);
                    pncVisit.setId(null);

                    Long benRegId = beneficiaryRepo.getRegIDFromBenId(it.getBenId());

                    // Saving data in BenVisitDetails table
                    BenVisitDetail benVisitDetail = new BenVisitDetail();
                    modelMapper.map(it, benVisitDetail);
                    benVisitDetail.setBeneficiaryRegId(benRegId);
                    benVisitDetail.setVisitCategory("PNC");
                    benVisitDetail.setVisitReason("Follow Up");
                    benVisitDetail.setPregnancyStatus("No");
                    benVisitDetail.setProcessed("N");
                    benVisitDetail.setModifiedBy(it.getUpdatedBy());
                    benVisitDetail.setLastModDate(it.getUpdatedDate());
                    benVisitDetail = benVisitDetailsRepo.save(benVisitDetail);

                    // Saving Data in AncCare table
                    PNCCare pncCare = new PNCCare();
                    modelMapper.map(it, pncCare);
                    pncCare.setBenVisitId(benVisitDetail.getBenVisitId());
                    pncCare.setBeneficiaryRegId(benRegId);
                    pncCare.setVisitNo((short) PNC_PERIODS.indexOf(it.getPncPeriod()));
                    pncCare.setModifiedBy(it.getUpdatedBy());
                    pncCare.setLastModDate(it.getUpdatedDate());
                    pncCare.setProcessed("N");
                    pncCareList.add(pncCare);
                    checkAndAddAntaraIncentive(pncList, pncVisit);
                }
                pncList.add(pncVisit);
            });
            pncVisitRepo.saveAll(pncList);
            pncCareRepo.saveAll(pncCareList);
            logger.info("PNC visit details saved");
            return "no of pnc details saved: " + pncList.size();
        } catch (Exception e) {
            logger.info("Saving PNC visit details failed with error : " + e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public String saveANCVisitQuestions(List<AncCounsellingCareDTO> dtos, String authorization) throws IEMRException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<AncCounsellingCare> entities = new ArrayList<>();

        for (AncCounsellingCareDTO dto : dtos) {

            if (!StringUtils.hasText(dto.getVisitDate())) {
                throw new IllegalArgumentException("visitDate is mandatory");
            }

            AncCounsellingCareListDTO fields = dto.getFields();
            if (fields == null) {
                throw new IllegalArgumentException("fields object is mandatory");
            }

            LocalDate visitDate;
            try {
                visitDate = LocalDate.parse(dto.getVisitDate(), formatter);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid visitDate format, expected dd-MM-yyyy");
            }

            LocalDate homeVisitDate;
            try {
                homeVisitDate = StringUtils.hasText(fields.getHomeVisitDate())
                        ? LocalDate.parse(fields.getHomeVisitDate(), formatter)
                        : visitDate; // ✅ fallback
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid home_visit_date format, expected dd-MM-yyyy");
            }

            AncCounsellingCare entity = new AncCounsellingCare();
            entity.setBeneficiaryId(dto.getBeneficiaryId());
            entity.setAncVisitId(0L);
            entity.setVisitDate(visitDate);
            entity.setHomeVisitDate(homeVisitDate);

            entity.setSelectAll(yesNoToBoolean(fields.getSelectAll()));
            entity.setSwelling(yesNoToBoolean(fields.getSwelling()));
            entity.setHighBp(yesNoToBoolean(fields.getHighBp()));
            entity.setConvulsions(yesNoToBoolean(fields.getConvulsions()));
            entity.setAnemia(yesNoToBoolean(fields.getAnemia()));
            entity.setReducedFetalMovement(yesNoToBoolean(fields.getReducedFetalMovement()));
            entity.setAgeRisk(yesNoToBoolean(fields.getAgeRisk()));
            entity.setChildGap(yesNoToBoolean(fields.getChildGap()));
            entity.setShortHeight(yesNoToBoolean(fields.getShortHeight()));
            entity.setPrePregWeight(yesNoToBoolean(fields.getPrePregWeight()));
            entity.setBleeding(yesNoToBoolean(fields.getBleeding()));
            entity.setMiscarriageHistory(yesNoToBoolean(fields.getMiscarriageHistory()));
            entity.setFourPlusDelivery(yesNoToBoolean(fields.getFourPlusDelivery()));
            entity.setFirstDelivery(yesNoToBoolean(fields.getFirstDelivery()));
            entity.setTwinPregnancy(yesNoToBoolean(fields.getTwinPregnancy()));
            entity.setCSectionHistory(yesNoToBoolean(fields.getCSectionHistory()));
            entity.setPreExistingDisease(yesNoToBoolean(fields.getPreExistingDisease()));
            entity.setFeverMalaria(yesNoToBoolean(fields.getFeverMalaria()));
            entity.setJaundice(yesNoToBoolean(fields.getJaundice()));
            entity.setSickleCell(yesNoToBoolean(fields.getSickleCell()));
            entity.setProlongedLabor(yesNoToBoolean(fields.getProlongedLabor()));
            entity.setMalpresentation(yesNoToBoolean(fields.getMalpresentation()));

            entity.setUserId(jwtUtil.extractUserId(authorization));
            entity.setCreatedBy(jwtUtil.extractUsername(authorization));
            entity.setUpdatedBy(jwtUtil.extractUsername(authorization));

            entities.add(entity);
        }

        ancCounsellingCareRepo.saveAll(entities);

        return "ANC Counselling & Care data saved successfully";
    }


    @Override
    public List<AncCounsellingCareResponseDTO> getANCCounselling(GetBenRequestHandler requestDTO) {

        List<AncCounsellingCare> entities =
                ancCounsellingCareRepo.findAllByUserId(requestDTO.getAshaId());

     List<AncCounsellingCareResponseDTO> responseDTOList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (AncCounsellingCare entity : entities) {

            AncCounsellingCareResponseDTO responseDTO = new AncCounsellingCareResponseDTO();
            responseDTO.setFormId("anc_form_001");
            responseDTO.setBeneficiaryId(entity.getBeneficiaryId()); // Update with actual value
            responseDTO.setVisitDate(entity.getVisitDate().format(formatter)); // Format visit.getVisitDate()

            // 🔹 fields map
            Map<String, Object> fields = new HashMap<>();

            fields.put("home_visit_date",
                    entity.getHomeVisitDate() != null
                            ? entity.getHomeVisitDate().format(formatter)
                            : null);

            fields.put("select_all", booleanToYesNo(entity.getSelectAll()));
            fields.put("swelling", booleanToYesNo(entity.getSwelling()));
            fields.put("high_bp", booleanToYesNo(entity.getHighBp()));
            fields.put("convulsions", booleanToYesNo(entity.getConvulsions()));
            fields.put("anemia", booleanToYesNo(entity.getAnemia()));
            fields.put("reduced_fetal_movement", booleanToYesNo(entity.getReducedFetalMovement()));
            fields.put("age_risk", booleanToYesNo(entity.getAgeRisk()));
            fields.put("child_gap", booleanToYesNo(entity.getChildGap()));
            fields.put("short_height", booleanToYesNo(entity.getShortHeight()));
            fields.put("pre_preg_weight", booleanToYesNo(entity.getPrePregWeight()));
            fields.put("bleeding", booleanToYesNo(entity.getBleeding()));
            fields.put("miscarriage_history", booleanToYesNo(entity.getMiscarriageHistory()));
            fields.put("four_plus_delivery", booleanToYesNo(entity.getFourPlusDelivery()));
            fields.put("first_delivery", booleanToYesNo(entity.getFirstDelivery()));
            fields.put("twin_pregnancy", booleanToYesNo(entity.getTwinPregnancy()));
            fields.put("c_section_history", booleanToYesNo(entity.getCSectionHistory()));
            fields.put("pre_existing_disease", booleanToYesNo(entity.getPreExistingDisease()));
            fields.put("fever_malaria", booleanToYesNo(entity.getFeverMalaria()));
            fields.put("jaundice", booleanToYesNo(entity.getJaundice()));
            fields.put("sickle_cell", booleanToYesNo(entity.getSickleCell()));
            fields.put("prolonged_labor", booleanToYesNo(entity.getProlongedLabor()));
            fields.put("malpresentation", booleanToYesNo(entity.getMalpresentation()));

            responseDTO.setFields(fields);
            responseDTOList.add(responseDTO);




        }
        return responseDTOList;

    }

    private String booleanToYesNo(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Yes" : "No";
    }



    private Boolean yesNoToBoolean(String value) {
        return "Yes".equalsIgnoreCase(value);
    }


    private void checkAndAddAntaraIncentive(List<PNCVisit> recordList, PNCVisit ect) {
        Integer userId = userRepo.getUserIdByName(ect.getCreatedBy());
        logger.info("ContraceptionMethod:" + ect.getContraceptionMethod());

        if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("MALE STERILIZATION")) {

            IncentiveActivity maleSterilizationActivityAM =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MALE_STER", GroupName.FAMILY_PLANNING.getDisplayName());

            IncentiveActivity maleSterilizationActivityCH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MALE_STER", GroupName.ACTIVITY.getDisplayName());
            if (maleSterilizationActivityAM != null) {
                addIncenticeRecord(recordList, ect, userId, maleSterilizationActivityAM);
            }
            if (maleSterilizationActivityCH != null) {
                addIncenticeRecord(recordList, ect, userId, maleSterilizationActivityCH);
            }

        } else if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("FEMALE STERILIZATION")) {

            IncentiveActivity femaleSterilizationActivityAM =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_FEMALE_STER", GroupName.FAMILY_PLANNING.getDisplayName());

            IncentiveActivity femaleSterilizationActivityCH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_FEMALE_STER", GroupName.ACTIVITY.getDisplayName());
            if (femaleSterilizationActivityAM != null) {
                addIncenticeRecord(recordList, ect, userId, femaleSterilizationActivityAM);
            }

            if (femaleSterilizationActivityCH != null) {
                addIncenticeRecord(recordList, ect, userId, femaleSterilizationActivityCH);
            }
        } else if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("MiniLap")) {

            IncentiveActivity miniLapActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_MINILAP", GroupName.FAMILY_PLANNING.getDisplayName());
            if (miniLapActivity != null) {
                addIncenticeRecord(recordList, ect, userId, miniLapActivity);
            }
        } else if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("Condom")) {

            IncentiveActivity comdomActivity =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_CONDOM", GroupName.FAMILY_PLANNING.getDisplayName());
            if (comdomActivity != null) {
                addIncenticeRecord(recordList, ect, userId, comdomActivity);
            }
        } else if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("POST PARTUM IUCD (PPIUCD) WITHIN 48 HRS OF DELIVERY")) {

            IncentiveActivity PPIUCDActivityAM =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_PPIUCD", GroupName.FAMILY_PLANNING.getDisplayName());

            IncentiveActivity PPIUCDActivityCH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_PPIUCD", GroupName.ACTIVITY.getDisplayName());
            if (PPIUCDActivityAM != null) {
                addIncenticeRecord(recordList, ect, userId, PPIUCDActivityAM);
            }

            if (PPIUCDActivityCH != null) {
                addIncenticeRecord(recordList, ect, userId, PPIUCDActivityCH);
            }
        } else if (ect.getContraceptionMethod() != null && ect.getContraceptionMethod().equals("POST PARTUM STERILIZATION (PPS)")) {

            IncentiveActivity ppsActivityAM =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_PPS", GroupName.FAMILY_PLANNING.getDisplayName());

            IncentiveActivity ppsActivityCH =
                    incentivesRepo.findIncentiveMasterByNameAndGroup("FP_PPS", GroupName.ACTIVITY.getDisplayName());
            if (ppsActivityAM != null) {
                addIncenticeRecord(recordList, ect, userId, ppsActivityAM);
            }

            if (ppsActivityCH != null) {
                addIncenticeRecord(recordList, ect, userId, ppsActivityCH);
            }
        }

    }

    private void addIncenticeRecord(List<PNCVisit> recordList, PNCVisit ect, Integer userId, IncentiveActivity antaraActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(antaraActivity.getId(), ect.getCreatedDate(), ect.getBenId());
        // get bene details

        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(antaraActivity.getId());
            record.setCreatedDate(ect.getPncDate());
            record.setCreatedBy(ect.getCreatedBy());
            record.setStartDate(ect.getPncDate());
            record.setEndDate(ect.getPncDate());
            record.setUpdatedDate(ect.getPncDate());
            record.setUpdatedBy(ect.getCreatedBy());
            record.setBenId(ect.getBenId());
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(antaraActivity.getRate()));
            recordRepo.save(record);
        }
    }


    private void checkAndAddIncentives(List<ANCVisit> ancList) {

        IncentiveActivity anc1Activity =
                incentivesRepo.findIncentiveMasterByNameAndGroup("ANC_REGISTRATION_1ST_TRIM", GroupName.MATERNAL_HEALTH.getDisplayName());

        IncentiveActivity ancFullActivityAM =
                incentivesRepo.findIncentiveMasterByNameAndGroup("FULL_ANC", GroupName.MATERNAL_HEALTH.getDisplayName());

        IncentiveActivity ancFullActivityCH =
                incentivesRepo.findIncentiveMasterByNameAndGroup("ANC_FOUR_CHECKUPS_SUPPORT", GroupName.ACTIVITY.getDisplayName());

        IncentiveActivity comprehensiveAbortionActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("COMPREHENSIVE_ABORTION_CARE", GroupName.MATERNAL_HEALTH.getDisplayName());
        IncentiveActivity comprehensiveAbortionActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("COMPREHENSIVE_ABORTION_CARE", GroupName.ACTIVITY.getDisplayName());

        IncentiveActivity identifiedHrpActivityAM = incentivesRepo.findIncentiveMasterByNameAndGroup("EPMSMA_HRP_IDENTIFIED", GroupName.MATERNAL_HEALTH.getDisplayName());
        IncentiveActivity identifiedHrpActivityCH = incentivesRepo.findIncentiveMasterByNameAndGroup("EPMSMA_HRP_IDENTIFIED", GroupName.ACTIVITY.getDisplayName());

        IncentiveActivity paiucdActivityAM =
                incentivesRepo.findIncentiveMasterByNameAndGroup("FP_PAIUCD", GroupName.FAMILY_PLANNING.getDisplayName());

        IncentiveActivity paiucdActivityCH =
                incentivesRepo.findIncentiveMasterByNameAndGroup("FP_PAIUCD", GroupName.ACTIVITY.getDisplayName());

        ancList.forEach(ancVisit -> {
            if (paiucdActivityAM != null) {
                if (ancVisit.getIsPaiucd()!= null) {
                    if (ancVisit.getIsPaiucd().equals("Yes")) {
                        recordAncRelatedIncentive(paiucdActivityAM, ancVisit);

                    }
                }

            }

            if (paiucdActivityCH != null) {

                if (ancVisit.getIsPaiucd()!= null) {
                    if (ancVisit.getIsPaiucd().equals("Yes")) {

                        recordAncRelatedIncentive(paiucdActivityCH, ancVisit);

                    }
                }


            }
            if (anc1Activity != null) {
                if (ancVisit.getAncVisit() != null) {

                    if (ancVisit.getAncVisit() == 1) {
                        recordAncRelatedIncentive(anc1Activity, ancVisit);
                    }
                }
            }
            if (ancFullActivityAM != null) {
                if (ancVisit.getAncVisit() != null && ancVisit.getAncVisit() == 4) {
                    recordFullAncIncentive(ancFullActivityAM, ancVisit);
                }
            }
            if (ancFullActivityCH != null) {
                if (ancVisit.getAncVisit() != null) {
                    if (ancVisit.getAncVisit() == 4) {
                        recordAncRelatedIncentive(ancFullActivityCH, ancVisit);
                    }
                }

            }
            if (comprehensiveAbortionActivityAM != null) {
                if (ancVisit.getIsAborted() != null) {
                    if (ancVisit.getIsAborted()) {
                        recordAncRelatedIncentive(comprehensiveAbortionActivityAM, ancVisit);
                    }
                }

            }

            if (comprehensiveAbortionActivityCH != null) {
                if (ancVisit.getIsAborted() != null) {
                    if (ancVisit.getIsAborted()) {
                        recordAncRelatedIncentive(comprehensiveAbortionActivityCH, ancVisit);
                    }
                }

            }
            if (identifiedHrpActivityAM != null) {
                if (ancVisit.getIsHrpConfirmed() != null) {
                    if (ancVisit.getIsHrpConfirmed()) {
                        recordAncRelatedIncentive(identifiedHrpActivityAM, ancVisit);
                    }
                }

            }

            if (identifiedHrpActivityCH != null) {
                if (ancVisit.getIsHrpConfirmed() != null) {
                    if (ancVisit.getIsHrpConfirmed()) {
                        recordAncRelatedIncentive(identifiedHrpActivityCH, ancVisit);
                    }
                }

            }

        });


    }

    private void recordAncRelatedIncentive(IncentiveActivity incentiveActivity, ANCVisit ancVisit) {

        if (ancVisit.getAncDate() == null || ancVisit.getLmpDate() == null) {
            return;
        }

        LocalDate ancLocalDate = ancVisit.getAncDate().toLocalDateTime().toLocalDate();
        LocalDate lmpLocalDate = ancVisit.getLmpDate().toLocalDateTime().toLocalDate();

        long weeksBetween = ChronoUnit.WEEKS.between(lmpLocalDate, ancLocalDate);

        if (weeksBetween < 0 || weeksBetween >= 12) {
            return;
        }

        IncentiveActivityRecord record = recordRepo.findRecordByActivityIdCreatedDateBenId(
                incentiveActivity.getId(),
                ancVisit.getCreatedDate(),
                ancVisit.getBenId()
        );

        Integer userId = userRepo.getUserIdByName(ancVisit.getCreatedBy());

        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(incentiveActivity.getId());
            record.setCreatedDate(ancVisit.getCreatedDate());
            record.setCreatedBy(ancVisit.getCreatedBy());
            record.setUpdatedDate(ancVisit.getCreatedDate());
            record.setUpdatedBy(ancVisit.getCreatedBy());
            record.setStartDate(ancVisit.getLmpDate());
            record.setEndDate(ancVisit.getLmpDate());
            record.setBenId(ancVisit.getBenId());
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(incentiveActivity.getRate()));
            recordRepo.save(record);
        }
    }

    private void recordFullAncIncentive(IncentiveActivity incentiveActivity, ANCVisit ancVisit) {

        // ✅ Once per case check — already record hai toh return
        IncentiveActivityRecord existRecord = recordRepo.findRecordByActivityIdCreatedDateBenId(
                incentiveActivity.getId(),
                ancVisit.getCreatedDate(),
                ancVisit.getBenId()
        );


        if (existRecord != null) {
            return;
        }

        // ✅ userId null check
        Integer userId = userRepo.getUserIdByName(ancVisit.getCreatedBy());
        if (userId == null) {
            return;
        }

        IncentiveActivityRecord record = new IncentiveActivityRecord();
        record.setActivityId(incentiveActivity.getId());
        record.setCreatedDate(ancVisit.getAncDate());
        record.setCreatedBy(ancVisit.getCreatedBy());
        record.setUpdatedDate(ancVisit.getAncDate());
        record.setUpdatedBy(ancVisit.getCreatedBy());
        record.setStartDate(ancVisit.getAncDate());
        record.setEndDate(ancVisit.getAncDate());
        record.setBenId(ancVisit.getBenId());
        record.setAshaId(userId);
        record.setAmount(Long.valueOf(incentiveActivity.getRate()));
        recordRepo.save(record);
    }

    public void sendAncDueTomorrowNotifications(String ashaId) {
        try {
            GetBenRequestHandler request = new GetBenRequestHandler();
            request.setAshaId(Integer.valueOf(ashaId));
            request.setFromDate(Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay())); // Tomorrow at 00:00:00
            request.setToDate(Timestamp.valueOf(LocalDate.now().plusDays(1).atTime(LocalTime.MAX))); // Tomorrow at 23:59:59.999999999


            List<ANCVisitDTO> ancList = getANCVisits(request);

            if (ancList != null) {
                for (ANCVisitDTO anc : ancList) {
                    if (anc.getAncDate() != null && anc.getAncDate().toLocalDateTime().toLocalDate().isEqual(LocalDate.now().plusDays(1))) {
                        String ancType = anc.getAbortionType(); // ANC1, ANC2, etc.
                        String body = "Reminder: Scheduled ANC check-up (" + ancType + ") is due tomorrow.";
                        String redirectPath = "/work-plan/anc/" + ancType.toLowerCase();
                        String appType = "FLW_APP"; // or "ASHAA_APP", based on user type
                        String topic = "All"; // or some user/topic identifier
                        String title = "ANC Reminder";


                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error in sending ANC reminder notifications: {}", e.getMessage());
        }
    }


}
