package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.ChildCareService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChildCareServiceImpl implements ChildCareService {

    private final Logger logger = LoggerFactory.getLogger(ChildCareServiceImpl.class);

    @Autowired
    private HbycRepo hbycRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    @Autowired
    private HbncVisitRepo hbncVisitRepo;


    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentiveRecordRepo recordRepo;

    @Autowired
    private ChildVaccinationRepo childVaccinationRepo;

    @Autowired
    private VaccineRepo vaccineRepo;

    ObjectMapper mapper = new ObjectMapper();

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public String registerHBYC(List<HbycDTO> hbycDTOs) {
        try {
            List<HBYC> hbycList = new ArrayList<>();
            hbycDTOs.forEach(it -> {
                HBYC hbyc =
                        hbycRepo.findHBYCByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());

                if (hbyc != null) {
                    Long id = hbyc.getId();
                    modelMapper.map(it, hbyc);
                    hbyc.setId(id);
                } else {
                    hbyc = new HBYC();
                    modelMapper.map(it, hbyc);
                    hbyc.setId(null);
                }
                hbycList.add(hbyc);
            });
            hbycRepo.saveAll(hbycList);
            return "no of hbyc details saved: " + hbycDTOs.size();
        } catch (Exception e) {
            logger.info("error while saving hbyc details: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<HbycDTO> getHbycRecords(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<HBYC> hbycList =
                    hbycRepo.getAllHbycByBenId(user, dto.getFromDate(), dto.getToDate());
            return hbycList.stream()
                    .map(hbyc -> mapper.convertValue(hbyc, HbycDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.info("error while fetching hbyc details: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<HbncVisitResponseDTO> getHBNCDetails(GetBenRequestHandler dto) {
        List<HbncVisitResponseDTO> result = new ArrayList<>();
        try {
            List<HbncVisit> hbncVisits = hbncVisitRepo.findAll();

            for (HbncVisit visit : hbncVisits) {
                HbncVisitResponseDTO responseDTO = new HbncVisitResponseDTO();
                responseDTO.setId(visit.getId());
                responseDTO.setBeneficiaryId(visit.getBeneficiaryId()); // Update with actual value
                responseDTO.setHouseHoldId(visit.getHouseHoldId());   // Update with actual value
                responseDTO.setVisitDate(visit.getVisit_date().split(" ")[0]); // Format visit.getVisitDate()

                // Convert all fields to Map
                Map<String, Object> fields = new HashMap<>();
                addIfValid(fields, "visit_day", visit.getVisit_day());
                addIfValid(fields, "due_date", visit.getDue_date());
                addIfValid(fields, "is_baby_alive", convert(visit.getIs_baby_alive()));
                addIfValid(fields, "date_of_death", visit.getDate_of_death());
                addIfValid(fields, "reason_for_death", visit.getReasonForDeath());
                addIfValid(fields, "place_of_death", visit.getPlace_of_death());
                addIfValid(fields, "other_place_of_death", visit.getOther_place_of_death());
                addIfValid(fields, "baby_weight", visit.getBaby_weight());
                addIfValid(fields, "urine_passed", convert(visit.getUrine_passed()));
                addIfValid(fields, "stool_passed", convert(visit.getStool_passed()));
                addIfValid(fields, "diarrhoea", convert(visit.getDiarrhoea()));
                addIfValid(fields, "vomiting", convert(visit.getVomiting()));
                addIfValid(fields, "convulsions", convert(visit.getConvulsions()));
                addIfValid(fields, "activity", visit.getActivity());
                addIfValid(fields, "sucking", visit.getSucking());
                addIfValid(fields, "breathing", visit.getBreathing());
                addIfValid(fields, "chest_indrawing", visit.getChest_indrawing());
                addIfValid(fields, "temperature", visit.getTemperature());
                addIfValid(fields, "jaundice", convert(visit.getJaundice()));
                addIfValid(fields, "umbilical_stump", visit.getUmbilical_stump());
                addIfValid(fields, "discharged_from_sncu", convert(visit.getDischarged_from_sncu()));
                addIfValid(fields, "discharge_summary_upload", visit.getDischarge_summary_upload());

                // Add more fields as required

                responseDTO.setFields(fields);
                result.add(responseDTO);
            }

        } catch (Exception e) {
            logger.error("Error in getHBNCDetails: ", e);
        }
        return result;
    }
    private void addIfValid(Map<String, Object> map, String key, Object value) {
        if (value == null) return;

        if (value instanceof String && ((String) value).trim().isEmpty()) return;

        map.put(key, value);
    }
    private String convert(Boolean value) {
        if (value == null) return null;
        return value ? "Yes" : "No";
    }
    private String convert(Object value) {
        if (value == null) return null;
        if (value instanceof Boolean) return (Boolean) value ? "Yes" : "No";
        return value.toString();
    }



    private String convert(String value) {
        return "true".equalsIgnoreCase(value) ? "Yes" : "No";
    }


    @Override
    public String saveHBNCDetails(List<HbncRequestDTO> hbncRequestDTOs) {
        try {
            List<HbncVisit> hbncList = new ArrayList<>();

            hbncRequestDTOs.forEach(it -> {
                if (it.getVisitDate() != null) {

                    HbncVisitDTO hbncVisitDTO = it.getFields();
                    hbncVisitDTO.setVisit_date(it.getVisitDate());
                    HbncVisit hbncVisit = hbncVisitRepo.findByBeneficiaryIdAndVisit_day(it.getBeneficiaryId(), hbncVisitDTO.getVisit_day());

                    if (hbncVisit != null) {
                        Long id = hbncVisit.getId();
                        modelMapper.map(hbncVisitDTO, hbncVisit);
                        hbncVisit.setId(id);
                    } else {
                        hbncVisit = new HbncVisit();
                        modelMapper.map(hbncVisitDTO, hbncVisit);
                        hbncVisit.setBeneficiaryId(it.getBeneficiaryId());
                        hbncVisit.setHouseHoldId(it.getHouseHoldId());
                        hbncVisit.setId(null);
                    }
                    hbncList.add(hbncVisit);
                }
            });


            hbncVisitRepo.saveAll(hbncList);
            //checkAndAddHbncIncentives(hbncList);


            logger.info("HBNC details saved");
            return "no of hbnc details saved: " + (hbncList.size());
        } catch (Exception e) {
            logger.info("Saving HBNC details failed with error : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ChildVaccinationDTO> getChildVaccinationDetails(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<ChildVaccination> vaccinationDetails = childVaccinationRepo.getChildVaccinationDetails(user, dto.getFromDate(), dto.getToDate());

            List<ChildVaccinationDTO> result = new ArrayList<>();
            vaccinationDetails.forEach(childVaccination -> {
                ChildVaccinationDTO vaccinationDTO = mapper.convertValue(childVaccination, ChildVaccinationDTO.class);
                BigInteger benId = beneficiaryRepo.getBenIdFromRegID(childVaccination.getBeneficiaryRegId());
                vaccinationDTO.setBeneficiaryId(benId.longValue());

                result.add(vaccinationDTO);
            });
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String saveChildVaccinationDetails(List<ChildVaccinationDTO> childVaccinationDTOs) {
        try {
            List<ChildVaccination> vaccinationList = new ArrayList<>();
            childVaccinationDTOs.forEach(it -> {
                Long benRegId = beneficiaryRepo.getRegIDFromBenId(it.getBeneficiaryId());

                ChildVaccination vaccination =
                        childVaccinationRepo.findChildVaccinationByBeneficiaryRegIdAndCreatedDateAndVaccineName(benRegId, it.getCreatedDate(), it.getVaccineName());

                if (vaccination != null) {
                    long id = vaccination.getId();
                    modelMapper.map(it, vaccination);
                    vaccination.setId(id);
                } else {
                    vaccination = new ChildVaccination();
                    modelMapper.map(it, vaccination);
                    vaccination.setBeneficiaryRegId(benRegId);
                    vaccination.setProcessed("N");
                }
                vaccinationList.add(vaccination);
            });
            childVaccinationRepo.saveAll(vaccinationList);
            checkAndAddIncentives(vaccinationList);
            logger.info("Child Vaccination details saved");
            return "No of child vaccination details saved: " + vaccinationList.size();
        } catch (Exception e) {
            logger.info("Saving Child Vaccination details failed with error : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<VaccineDTO> getAllChildVaccines(String category) {
        try {
            List<Vaccine> vaccines = vaccineRepo.getAllByCategory(category);

            List<VaccineDTO> result = new ArrayList<>();
            vaccines.forEach(vaccine -> {
                VaccineDTO vaccineDTO = mapper.convertValue(vaccine, VaccineDTO.class);
                switch (vaccineDTO.getImmunizationService()) {
                    case "Birth Dose Vaccines":
                        vaccineDTO.setImmunizationService("BIRTH");
                        break;
                    case "6 Weeks Vaccines":
                        vaccineDTO.setImmunizationService("WEEK_6");
                        break;
                    case "10 Weeks Vaccines":
                        vaccineDTO.setImmunizationService("WEEK_10");
                        break;
                    case "14 Weeks Vaccines":
                        vaccineDTO.setImmunizationService("WEEK_14");
                        break;
                    case "9-12 Months":
                        vaccineDTO.setImmunizationService("MONTH_9_12");
                        break;
                    case "16-24 Months Vaccines":
                        vaccineDTO.setImmunizationService("MONTH_16_24");
                        break;
                    case "5-6 Years Vaccine":
                        vaccineDTO.setImmunizationService("YEAR_5_6");
                        break;
                    case "10 Years Vaccine":
                        vaccineDTO.setImmunizationService("YEAR_10");
                        break;
                    case "16 Years Vaccine":
                        vaccineDTO.setImmunizationService("YEAR_16");
                        break;
                    default:
                        vaccineDTO.setImmunizationService("CATCH_UP");
                        break;
                }
                result.add(vaccineDTO);
            });
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private void checkAndAddHbncIncentives(List<HbncVisit> hbncVisits) {
        hbncVisits.forEach(hbncVisit -> {
            boolean isVisitDone = List.of("1st Day", "3rd Day", "7th Day", "42nd Day")
                    .stream()
                    .allMatch(hbncVisits::contains);

            boolean isBabyDisChargeSNCUA = hbncVisits.stream()
                    .anyMatch(v -> Boolean.TRUE.equals(v.getDischarged_from_sncu()));
            Long benId = beneficiaryRepo.getBenIdFromRegID(hbncVisit.getBeneficiaryId()).longValue();
            Integer userId = userRepo.getUserIdByName(hbncVisit.getCreatedBy());
            if (isVisitDone) {
                IncentiveActivity visitActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("HBNC_VISIT", "HBNC");

                createIncentiveRecordforHbncVisit(hbncVisit, benId, userId, visitActivity);

            }
            if (isBabyDisChargeSNCUA) {
                IncentiveActivity babyDisChargeSNCUAActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("BABY_DISCHARGES_SNCU", "HBNC");

                createIncentiveRecordforHbncVisit(hbncVisit, benId, userId, babyDisChargeSNCUAActivity);

            }


        });


    }

    private void checkAndAddIncentives(List<ChildVaccination> vaccinationList) {

        vaccinationList.forEach(vaccination -> {
            Long benId = beneficiaryRepo.getBenIdFromRegID(vaccination.getBeneficiaryRegId()).longValue();
            Integer userId = userRepo.getUserIdByName(vaccination.getCreatedBy());
            Integer immunizationServiceId = getImmunizationServiceIdForVaccine(vaccination.getVaccineId().shortValue());
            if (immunizationServiceId < 6) {
                IncentiveActivity immunizationActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_0_1", "IMMUNIZATION");
                if (immunizationActivity != null && childVaccinationRepo.getFirstYearVaccineCountForBenId(vaccination.getBeneficiaryRegId())
                        .equals(childVaccinationRepo.getFirstYearVaccineCount())) {
                    createIncentiveRecord(vaccination, benId, userId, immunizationActivity);
                }
            } else if (immunizationServiceId == 7) {
                IncentiveActivity immunizationActivity2 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_1_2", "IMMUNIZATION");
                if (immunizationActivity2 != null && childVaccinationRepo.getSecondYearVaccineCountForBenId(vaccination.getBeneficiaryRegId())
                        .equals(childVaccinationRepo.getSecondYearVaccineCount())) {
                    createIncentiveRecord(vaccination, benId, userId, immunizationActivity2);
                }
            } else if (immunizationServiceId == 8) {
                IncentiveActivity immunizationActivity5 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_5", "IMMUNIZATION");
                if (immunizationActivity5 != null && childVaccinationRepo.checkDptVaccinatedUser(vaccination.getBeneficiaryRegId()) == 1) {
                    createIncentiveRecord(vaccination, benId, userId, immunizationActivity5);
                }
            }
        });
    }

    private void createIncentiveRecord(ChildVaccination vaccination, Long benId, Integer userId, IncentiveActivity immunizationActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), vaccination.getCreatedDate(), benId);
        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(immunizationActivity.getId());
            record.setCreatedDate(vaccination.getCreatedDate());
            record.setCreatedBy(vaccination.getCreatedBy());
            record.setName(immunizationActivity.getName());
            record.setStartDate(vaccination.getCreatedDate());
            record.setEndDate(vaccination.getCreatedDate());
            record.setUpdatedDate(vaccination.getCreatedDate());
            record.setUpdatedBy(vaccination.getCreatedBy());
            record.setBenId(benId);
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(immunizationActivity.getRate()));
            recordRepo.save(record);
        }
    }

    private void createIncentiveRecordforHbncVisit(HbncVisit hbncVisit, Long benId, Integer userId, IncentiveActivity immunizationActivity) {
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(immunizationActivity.getId(), hbncVisit.getCreatedDate(), benId);
        if (record == null) {
            record = new IncentiveActivityRecord();
            record.setActivityId(immunizationActivity.getId());
            record.setCreatedDate(hbncVisit.getCreatedDate());
            record.setCreatedBy(hbncVisit.getCreatedBy());
            record.setName(immunizationActivity.getName());
            record.setStartDate(hbncVisit.getCreatedDate());
            record.setEndDate(hbncVisit.getCreatedDate());
            record.setUpdatedDate(hbncVisit.getCreatedDate());
            record.setUpdatedBy(hbncVisit.getCreatedBy());
            record.setBenId(benId);
            record.setAshaId(userId);
            record.setAmount(Long.valueOf(immunizationActivity.getRate()));
            recordRepo.save(record);
        }
    }

    private Integer getImmunizationServiceIdForVaccine(Short vaccineId) {
        return vaccineRepo.getImmunizationServiceIdByVaccineId(vaccineId);
    }
}
