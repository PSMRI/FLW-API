package com.iemr.flw.service.impl;

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
import java.util.ArrayList;
import java.util.List;
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
    private HbncVisitCardRepo hbncVisitCardRepo;

    @Autowired
    private HbncPart1Repo hbncPart1Repo;

    @Autowired
    private HbncPart2repo hbncPart2repo;

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
            hbycDTOs.forEach(it ->{
                HBYC hbyc =
                        hbycRepo.findHBYCByBenIdAndCreatedDate(it.getBenId(), it.getCreatedDate());

                if(hbyc != null) {
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
            hbycRepo.save(hbycList);
            return "no of hbyc details saved: " + hbycDTOs.size();
        } catch (Exception e) {
            logger.info("error while saving hbyc details: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<HbycDTO> getHbycRecords(GetBenRequestHandler dto) {
        try{
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
    public List<HbncRequestDTO> getHBNCDetails(GetBenRequestHandler dto) {
        try {
            List<HbncRequestDTO> result = new ArrayList<>();
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<HbncVisit> hbncVisits = hbncVisitRepo.getHbncVisitDetails(user, dto.getFromDate(), dto.getToDate());
            hbncVisits.forEach( hbnc -> {
                HbncVisitDTO hbncVisitDTO = mapper.convertValue(hbnc, HbncVisitDTO.class);
                HbncRequestDTO hbncRequestDTO = new HbncRequestDTO();
                hbncRequestDTO.setId(hbncVisitDTO.getId());
                hbncRequestDTO.setBenId(hbncVisitDTO.getBenId());
                hbncRequestDTO.setHomeVisitDate(hbncVisitDTO.getVisitNo());
                hbncRequestDTO.setHbncVisitDTO(hbncVisitDTO);
                result.add(hbncRequestDTO);
            });

            List<HbncVisitCard> hbncVisitCards = hbncVisitCardRepo.getHbncVisitCardDetails(user, dto.getFromDate(), dto.getToDate());
            hbncVisitCards.forEach( hbnc -> {
                HbncVisitCardDTO hbncVisitCardDTO = mapper.convertValue(hbnc, HbncVisitCardDTO.class);
                HbncRequestDTO hbncRequestDTO = new HbncRequestDTO();
                hbncRequestDTO.setId(hbncVisitCardDTO.getId());
                hbncRequestDTO.setBenId(hbncVisitCardDTO.getBenId());
                hbncRequestDTO.setHomeVisitDate(hbncVisitCardDTO.getVisitNo());
                hbncRequestDTO.setHbncVisitCardDTO(hbncVisitCardDTO);
                result.add(hbncRequestDTO);
            });

            List<HbncPart1> hbncPart1List = hbncPart1Repo.getHbncPart1Details(user, dto.getFromDate(), dto.getToDate());
            hbncPart1List.forEach( hbnc -> {
                HbncPart1DTO hbncPart1DTO = mapper.convertValue(hbnc, HbncPart1DTO.class);
                HbncRequestDTO hbncRequestDTO = new HbncRequestDTO();
                hbncRequestDTO.setId(hbncPart1DTO.getId());
                hbncRequestDTO.setBenId(hbncPart1DTO.getBenId());
                hbncRequestDTO.setHomeVisitDate(hbncPart1DTO.getVisitNo());
                hbncRequestDTO.setHbncPart1DTO(hbncPart1DTO);
                result.add(hbncRequestDTO);
            });

            List<HbncPart2> hbncPart2List = hbncPart2repo.getHbncPart2Details(user, dto.getFromDate(), dto.getToDate());
            hbncPart2List.forEach( hbnc -> {
                HbncPart2DTO hbncPart2DTO = mapper.convertValue(hbnc, HbncPart2DTO.class);
                HbncRequestDTO hbncRequestDTO = new HbncRequestDTO();
                hbncRequestDTO.setId(hbncPart2DTO.getId());
                hbncRequestDTO.setBenId(hbncPart2DTO.getBenId());
                hbncRequestDTO.setHomeVisitDate(hbncPart2DTO.getVisitNo());
                hbncRequestDTO.setHbncPart2DTO(hbncPart2DTO);
                result.add(hbncRequestDTO);
            });

            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String saveHBNCDetails(List<HbncRequestDTO> hbncRequestDTOs) {
        try {
            List<HbncVisit> hbncList = new ArrayList<>();
            List<HbncVisitCard> hbncCardList = new ArrayList<>();
            List<HbncPart1> hbncPart1List = new ArrayList<>();
            List<HbncPart2> hbncPart2List = new ArrayList<>();
            hbncRequestDTOs.forEach(it -> {
                if(it.getHbncVisitDTO() != null) {
                    HbncVisitDTO hbncVisitDTO = it.getHbncVisitDTO();
                    hbncVisitDTO.setId(it.getId());
                    hbncVisitDTO.setBenId(it.getBenId());
                    hbncVisitDTO.setVisitNo(it.getHomeVisitDate());
                    HbncVisit hbncVisit =
                            hbncVisitRepo.findHbncVisitByBenIdAndVisitNo(hbncVisitDTO.getBenId(), hbncVisitDTO.getVisitNo());

                    if (hbncVisit != null) {
                        Long id = hbncVisit.getId();
                        modelMapper.map(hbncVisitDTO, hbncVisit);
                        hbncVisit.setId(id);
                    } else {
                        hbncVisit = new HbncVisit();
                        modelMapper.map(hbncVisitDTO, hbncVisit);
                        hbncVisit.setId(null);
                    }
                    hbncList.add(hbncVisit);
                } else if(it.getHbncVisitCardDTO() != null) {
                    HbncVisitCardDTO hbncVisitCardDTO = it.getHbncVisitCardDTO();
                    hbncVisitCardDTO.setId(it.getId());
                    hbncVisitCardDTO.setBenId(it.getBenId());
                    hbncVisitCardDTO.setVisitNo(it.getHomeVisitDate());
                    HbncVisitCard hbncVisitCard =
                            hbncVisitCardRepo.findHbncVisitCardByBenIdAndVisitNo(hbncVisitCardDTO.getBenId(), hbncVisitCardDTO.getVisitNo());

                    if (hbncVisitCard != null) {
                        Long id = hbncVisitCard.getId();
                        modelMapper.map(hbncVisitCardDTO, hbncVisitCard);
                        hbncVisitCard.setId(id);
                    } else {
                        hbncVisitCard = new HbncVisitCard();
                        modelMapper.map(hbncVisitCardDTO, hbncVisitCard);
                        hbncVisitCard.setId(null);
                    }
                    hbncCardList.add(hbncVisitCard);
                } else if(it.getHbncPart1DTO() != null) {
                    HbncPart1DTO hbncPart1DTO = it.getHbncPart1DTO();
                    hbncPart1DTO.setId(it.getId());
                    hbncPart1DTO.setBenId(it.getBenId());
                    hbncPart1DTO.setVisitNo(it.getHomeVisitDate());
                    HbncPart1 hbncPart1 =
                            hbncPart1Repo.findHbncPart1ByBenIdAndVisitNo(hbncPart1DTO.getBenId(), hbncPart1DTO.getVisitNo());

                    if (hbncPart1 != null) {
                        Long id = hbncPart1.getId();
                        modelMapper.map(hbncPart1DTO, hbncPart1);
                        hbncPart1.setId(id);
                    } else {
                        hbncPart1 = new HbncPart1();
                        modelMapper.map(hbncPart1DTO, hbncPart1);
                        hbncPart1.setId(null);
                    }
                    hbncPart1List.add(hbncPart1);
                } else if(it.getHbncPart2DTO() != null) {
                    HbncPart2DTO hbncPart2DTO = it.getHbncPart2DTO();
                    hbncPart2DTO.setId(it.getId());
                    hbncPart2DTO.setBenId(it.getBenId());
                    hbncPart2DTO.setVisitNo(it.getHomeVisitDate());
                    HbncPart2 hbncPart2 =
                            hbncPart2repo.findHbncPart2ByBenIdAndVisitNo(hbncPart2DTO.getBenId(), hbncPart2DTO.getVisitNo());

                    if (hbncPart2 != null) {
                        Long id = hbncPart2.getId();
                        modelMapper.map(hbncPart2DTO, hbncPart2);
                        hbncPart2.setId(id);
                    } else {
                        hbncPart2 = new HbncPart2();
                        modelMapper.map(hbncPart2DTO, hbncPart2);
                        hbncPart2.setId(null);
                    }
                    hbncPart2List.add(hbncPart2);
                }
            });
            hbncVisitRepo.save(hbncList);
            hbncVisitCardRepo.save(hbncCardList);
            hbncPart1Repo.save(hbncPart1List);
            hbncPart2repo.save(hbncPart2List);
            logger.info("HBNC details saved");
            return "no of hbnc details saved: " + (hbncList.size() + hbncCardList.size() +
                    hbncPart1List.size() + hbncPart2List.size());
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
            childVaccinationRepo.save(vaccinationList);
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
    private void checkAndAddIncentives(List<ChildVaccination> vaccinationList) {

        vaccinationList.forEach( vaccination -> {
            Long benId = beneficiaryRepo.getBenIdFromRegID(vaccination.getBeneficiaryRegId()).longValue();
            Integer userId = userRepo.getUserIdByName(vaccination.getCreatedBy());
            Integer immunizationServiceId = getImmunizationServiceIdForVaccine(vaccination.getVaccineId().shortValue());
            if(immunizationServiceId < 6) {
                IncentiveActivity immunizationActivity =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_0_1", "IMMUNIZATION");
                if (immunizationActivity != null && childVaccinationRepo.getFirstYearVaccineCountForBenId(vaccination.getBeneficiaryRegId())
                        .equals(childVaccinationRepo.getFirstYearVaccineCount())) {
                    createIncentiveRecord(vaccination, benId, userId, immunizationActivity);
                }
            } else if(immunizationServiceId == 7) {
                IncentiveActivity immunizationActivity2 =
                        incentivesRepo.findIncentiveMasterByNameAndGroup("IMMUNIZATION_1_2", "IMMUNIZATION");
                if (immunizationActivity2 != null && childVaccinationRepo.getSecondYearVaccineCountForBenId(vaccination.getBeneficiaryRegId())
                        .equals(childVaccinationRepo.getSecondYearVaccineCount())) {
                    createIncentiveRecord(vaccination, benId, userId, immunizationActivity2);
                }
            } else if(immunizationServiceId == 8) {
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

    private Integer getImmunizationServiceIdForVaccine(Short vaccineId) {
        return vaccineRepo.getImmunizationServiceIdByVaccineId(vaccineId);
    }
}
