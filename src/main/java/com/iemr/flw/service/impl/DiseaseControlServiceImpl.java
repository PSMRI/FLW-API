/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*
/*
* AMRIT – Accessible Medical Records via Integrated Technology
*/
package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.identity.RMNCHBeneficiaryDetailsRmnch;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.DiseaseType;
import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.masterEnum.StateCode;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.DiseaseControlService;
import com.iemr.flw.service.IncentiveLogicService;
import com.iemr.flw.service.UserService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

@Service
public class DiseaseControlServiceImpl implements DiseaseControlService {

    @Autowired
    private DiseaseMalariaRepository diseaseMalariaRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private DiseaseAESJERepository diseaseAESJERepository;
    @Autowired
    private DiseaseFilariasisRepository diseaseFilariasisRepository;
    @Autowired
    private DiseaseKalaAzarRepository diseaseKalaAzarRepository;

    @Autowired
    private DiseaseLeprosyRepository diseaseLeprosyRepository;

    @Autowired
    private LeprosyFollowUpRepository leprosyFollowUpRepository;

    @Autowired
    private IncentiveRecordRepo recordRepo;
    @Autowired
    private UserServiceRoleRepo userRepo;

    @Autowired
    private IncentivesRepo incentivesRepo;

    @Autowired
    private MosquitoNetRepository mosquitoNetRepository;


    @Autowired
    private ChronicDiseaseVisitRepository chronicDiseaseVisitRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IncentiveLogicService incentiveLogicService;

    @Autowired
    private UserService userService;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;


    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String saveMalaria(MalariaDTO diseaseControlDTO) {
        for (DiseaseMalariaDTO diseaseControlData : diseaseControlDTO.getMalariaLists()) {
            if (diseaseMalariaRepository.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateMalariaDisease(diseaseControlData);
            } else {
                if (diseaseControlDTO.getUserId() != null) {
                    diseaseControlData.setUserId(diseaseControlDTO.getUserId());
                }
                diseaseMalariaRepository.save(saveMalariaDisease(diseaseControlData));
                return "Data add successfully";

            }
        }
        return "Fail";
    }


    @Override
    public String saveKalaAzar(KalaAzarDTO diseaseControlDTO) {
        logger.info("Save request: " + diseaseControlDTO.toString());
        for (DiseaseKalaAzarDTO diseaseControlData : diseaseControlDTO.getKalaAzarLists()) {
            if (diseaseKalaAzarRepository.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateKalaAzarDisease(diseaseControlData);
            } else {
                if (diseaseControlDTO.getUserId() != null) {
                    diseaseControlData.setUserId(diseaseControlDTO.getUserId());
                }
                diseaseKalaAzarRepository.save(saveKalaAzarDisease(diseaseControlData));
                return "Data add successfully";

            }
        }
        return "Fail";

    }


    @Override
    public String saveAES(AesJeDTO diseaseControlDTO) {
        for (DiseaseAesjeDto diseaseControlData : diseaseControlDTO.getAesJeLists()) {
            if (diseaseAESJERepository.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateASEDisease(diseaseControlData);
            } else {
                if (diseaseControlDTO.getUserId() != null) {
                    diseaseControlData.setUserId(diseaseControlDTO.getUserId());
                }
                diseaseAESJERepository.save(saveASEDisease(diseaseControlData));
                return "Data add successfully";

            }
        }
        return "Fail";
    }

    @Override
    public String saveFilaria(FilariaDTO diseaseControlDTO) {

        for (DiseaseFilariasisDTO diseaseControlData : diseaseControlDTO.getFilariaLists()) {
            if (diseaseFilariasisRepository.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateFilaria(diseaseControlData);
            } else {
                if (diseaseControlDTO.getUserId() != null) {
                    diseaseControlData.setUserId(diseaseControlDTO.getUserId());
                }
                diseaseFilariasisRepository.save(saveFilariasisData(diseaseControlData));
                return "Data add successfully";

            }
        }
        return "Fail";
    }

    private ScreeningAesje saveASEDisease(DiseaseAesjeDto diseaseControlData) {
        // Create a new DiseaseAesje entity from the DTO data
        ScreeningAesje diseaseAesje = new ScreeningAesje();

        // Set the fields from DTO to entity
        diseaseAesje.setBenId(diseaseControlData.getBenId());
        diseaseAesje.setHouseHoldDetailsId(diseaseControlData.getHouseHoldDetailsId());
        diseaseAesje.setVisitDate(diseaseControlData.getVisitDate());
        diseaseAesje.setBeneficiaryStatus(diseaseControlData.getBeneficiaryStatus());
        diseaseAesje.setDateOfDeath(diseaseControlData.getDateOfDeath());
        diseaseAesje.setPlaceOfDeath(diseaseControlData.getPlaceOfDeath());
        diseaseAesje.setDiseaseTypeId(diseaseControlData.getDiseaseTypeId());
        diseaseAesje.setOtherPlaceOfDeath(diseaseControlData.getOtherPlaceOfDeath());
        diseaseAesje.setReasonForDeath(diseaseControlData.getReasonForDeath());
        diseaseAesje.setOtherReasonForDeath(diseaseControlData.getOtherReasonForDeath());
        diseaseAesje.setAesJeCaseStatus(diseaseControlData.getAesJeCaseStatus());
        diseaseAesje.setAesJeCaseCount(diseaseControlData.getAesJeCaseCount());
        diseaseAesje.setFollowUpPoint(diseaseControlData.getFollowUpPoint());
        diseaseAesje.setReferredTo(diseaseControlData.getReferredTo());
        diseaseAesje.setOtherReferredFacility(diseaseControlData.getOtherReferredFacility());
        diseaseAesje.setCreatedDate(new Timestamp(System.currentTimeMillis())); // Set current timestamp
        diseaseAesje.setCreatedBy(diseaseControlData.getCreatedBy());
        diseaseAesje.setBeneficiaryStatusId(diseaseControlData.getBeneficiaryStatusId());
        diseaseAesje.setReferToName(diseaseControlData.getReferToName());
        diseaseAesje.setUserId(diseaseControlData.getUserId());

        // Return the new entity to be saved
        return diseaseAesje;
    }

    private String updateASEDisease(DiseaseAesjeDto diseaseControlData) {
        // Fetch the existing record from the database using benId
        ScreeningAesje existingDiseaseAesje = diseaseAESJERepository.findByBenId(diseaseControlData.getBenId())
                .orElseThrow(() -> new RuntimeException("AES/JE record not found for benId: " + diseaseControlData.getBenId()));

        // Update the existing entity with new values from the DTO
        existingDiseaseAesje.setHouseHoldDetailsId(diseaseControlData.getHouseHoldDetailsId());
        existingDiseaseAesje.setVisitDate(diseaseControlData.getVisitDate());
        existingDiseaseAesje.setBeneficiaryStatus(diseaseControlData.getBeneficiaryStatus());
        existingDiseaseAesje.setDateOfDeath(diseaseControlData.getDateOfDeath());
        existingDiseaseAesje.setDiseaseTypeId(diseaseControlData.getDiseaseTypeId());
        existingDiseaseAesje.setPlaceOfDeath(diseaseControlData.getPlaceOfDeath());
        existingDiseaseAesje.setOtherPlaceOfDeath(diseaseControlData.getOtherPlaceOfDeath());
        existingDiseaseAesje.setReasonForDeath(diseaseControlData.getReasonForDeath());
        existingDiseaseAesje.setOtherReasonForDeath(diseaseControlData.getOtherReasonForDeath());
        existingDiseaseAesje.setAesJeCaseStatus(diseaseControlData.getAesJeCaseStatus());
        existingDiseaseAesje.setAesJeCaseCount(diseaseControlData.getAesJeCaseCount());
        existingDiseaseAesje.setFollowUpPoint(diseaseControlData.getFollowUpPoint());
        existingDiseaseAesje.setReferredTo(diseaseControlData.getReferredTo());
        existingDiseaseAesje.setOtherReferredFacility(diseaseControlData.getOtherReferredFacility());
        existingDiseaseAesje.setBeneficiaryStatusId(diseaseControlData.getBeneficiaryStatusId());
        existingDiseaseAesje.setReferToName(diseaseControlData.getReferToName());

        // If the userId is present, update it as well


        // Save the updated entity
        diseaseAESJERepository.save(existingDiseaseAesje);

        return "AES/JE data updated successfully";
    }


    private ScreeningFilariasis saveFilariasisData(DiseaseFilariasisDTO diseaseControlData) {
        // Create a new DiseaseFilariasis entity from the DTO data
        ScreeningFilariasis diseaseFilariasis = new ScreeningFilariasis();

        diseaseFilariasis.setBenId(diseaseControlData.getBenId());
        diseaseFilariasis.setHouseHoldDetailsId(diseaseControlData.getHouseHoldDetailsId());
        diseaseFilariasis.setSufferingFromFilariasis(diseaseControlData.getSufferingFromFilariasis());
        diseaseFilariasis.setAffectedBodyPart(diseaseControlData.getAffectedBodyPart());
        diseaseFilariasis.setMdaHomeVisitDate(diseaseControlData.getMdaHomeVisitDate());
        diseaseFilariasis.setDoseStatus(diseaseControlData.getDoseStatus());
        diseaseFilariasis.setDiseaseTypeId(diseaseControlData.getDiseaseTypeId());
        diseaseFilariasis.setFilariasisCaseCount(diseaseControlData.getFilariasisCaseCount());
        diseaseFilariasis.setOtherDoseStatusDetails(diseaseControlData.getOtherDoseStatusDetails());
        diseaseFilariasis.setMedicineSideEffect(diseaseControlData.getMedicineSideEffect());
        diseaseFilariasis.setOtherSideEffectDetails(diseaseControlData.getOtherSideEffectDetails());
        diseaseFilariasis.setCreatedDate(new Timestamp(System.currentTimeMillis())); // Set current timestamp
        diseaseFilariasis.setCreatedBy(diseaseControlData.getCreatedBy());
        diseaseFilariasis.setUserId(diseaseControlData.getUserId());

        // Return the new entity to be saved
        return diseaseFilariasis;
    }


    private String updateFilaria(DiseaseFilariasisDTO diseaseControlData) {
        // Fetch the existing record from the database using benId
        ScreeningFilariasis existingDiseaseFilariasis = diseaseFilariasisRepository.findByBenId(diseaseControlData.getBenId())
                .orElseThrow(() -> new RuntimeException("Filariasis record not found for benId: " + diseaseControlData.getBenId()));

        // Update the existing entity with the new values from the DTO
        existingDiseaseFilariasis.setHouseHoldDetailsId(diseaseControlData.getHouseHoldDetailsId());
        existingDiseaseFilariasis.setSufferingFromFilariasis(diseaseControlData.getSufferingFromFilariasis());
        existingDiseaseFilariasis.setAffectedBodyPart(diseaseControlData.getAffectedBodyPart());
        existingDiseaseFilariasis.setMdaHomeVisitDate(diseaseControlData.getMdaHomeVisitDate());
        existingDiseaseFilariasis.setDoseStatus(diseaseControlData.getDoseStatus());
        existingDiseaseFilariasis.setDiseaseTypeId(diseaseControlData.getDiseaseTypeId());
        existingDiseaseFilariasis.setFilariasisCaseCount(diseaseControlData.getFilariasisCaseCount());
        existingDiseaseFilariasis.setOtherDoseStatusDetails(diseaseControlData.getOtherDoseStatusDetails());
        existingDiseaseFilariasis.setMedicineSideEffect(diseaseControlData.getMedicineSideEffect());
        existingDiseaseFilariasis.setOtherSideEffectDetails(diseaseControlData.getOtherSideEffectDetails());


        // Save the updated entity
        diseaseFilariasisRepository.save(existingDiseaseFilariasis);

        return "Filariasis data updated successfully";
    }


    @Override
    @Transactional
    public String saveLeprosy(LeprosyDTO diseaseControlDTO) {
        for (DiseaseLeprosyDTO diseaseControlData : diseaseControlDTO.getLeprosyLists()) {
            if (diseaseLeprosyRepository.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateLeprosyData(diseaseControlData);
            } else {
                if (diseaseControlDTO.getUserId() != null) {
                    diseaseControlData.setUserId(diseaseControlDTO.getUserId());
                }
                ScreeningLeprosy screeningLeprosy = diseaseLeprosyRepository.save(saveLeprosyData(diseaseControlData));
                if(screeningLeprosy.getIsConfirmed()){
                    IncentiveActivityRecord incentiveActivityRecord =
                            incentiveLogicService.incentiveForIdentificationLeprosy(
                                    screeningLeprosy.getBenId(),
                                    screeningLeprosy.getHomeVisitDate(),
                                    screeningLeprosy.getHomeVisitDate(),
                                    diseaseControlDTO.getUserId());

                    if (incentiveActivityRecord != null) {
                        logger.info("Incentive processed for Screening Leprosy  successfully. RecordId={}",
                                incentiveActivityRecord.getId());
                    } else {
                        logger.info("Incentive not created");
                    }
                }
                if (screeningLeprosy.getIsConfirmed()) {
                    if(screeningLeprosy.getTypeOfLeprosy()!=null){
                        if (screeningLeprosy.getTypeOfLeprosy().equals("PB (Paucibacillary)")) {
                            IncentiveActivityRecord incentiveActivityRecord =
                                    incentiveLogicService.incentiveForLeprosyPaucibacillaryConfirmed(
                                            screeningLeprosy.getBenId(),
                                            screeningLeprosy.getTreatmentEndDate(),
                                            screeningLeprosy.getTreatmentEndDate(),
                                            diseaseControlDTO.getUserId());

                            if (incentiveActivityRecord != null) {
                                logger.info("Incentive processed for Screening Leprosy  successfully. RecordId={}",
                                        incentiveActivityRecord.getId());
                            } else {
                                logger.info("Incentive not created");
                            }

                        }
                        if (screeningLeprosy.getTypeOfLeprosy().equals("MB (Multibacillary)")) {
                            IncentiveActivityRecord incentiveActivityRecord =
                                    incentiveLogicService.incentiveForLeprosyMultibacillaryConfirmed(
                                            screeningLeprosy.getBenId(),
                                            screeningLeprosy.getTreatmentEndDate(),
                                            screeningLeprosy.getTreatmentEndDate(),
                                            diseaseControlDTO.getUserId());

                            if (incentiveActivityRecord != null) {
                                logger.info("Incentive processed for Screening Leprosy  successfully. RecordId={}",
                                        incentiveActivityRecord.getId());
                            } else {
                                logger.info("Incentive not created");
                            }

                        }
                    }


                }

                return "Data add successfully";

            }
        }
        return "Fail";
    }

    private LeprosyFollowUp saveLeprosyFollowUpData(LeprosyFollowUpDTO data) {
        LeprosyFollowUp entity = new LeprosyFollowUp();

        entity.setBenId(data.getBenId());
        entity.setVisitNumber(data.getVisitNumber());
        entity.setFollowUpDate(data.getFollowUpDate());
        entity.setTreatmentStatus(data.getTreatmentStatus());
        entity.setMdtBlisterPackReceived(data.getMdtBlisterPackReceived());
        entity.setTreatmentCompleteDate(data.getTreatmentCompleteDate());
        entity.setRemarks(data.getRemarks());
        entity.setHomeVisitDate(data.getHomeVisitDate());
        entity.setLeprosySymptoms(data.getLeprosySymptoms());
        entity.setTypeOfLeprosy(data.getTypeOfLeprosy());
        entity.setLeprosySymptomsPosition(data.getLeprosySymptomsPosition());
        entity.setVisitLabel(data.getVisitLabel());
        entity.setLeprosyStatus(data.getLeprosyStatus());
        entity.setReferredTo(data.getReferredTo());
        entity.setReferToName(data.getReferToName());
        entity.setTreatmentEndDate(data.getTreatmentEndDate());
        entity.setMdtBlisterPackRecived(data.getMdtBlisterPackRecived());
        entity.setTreatmentStartDate(data.getTreatmentStartDate());

        // Audit fields
        entity.setCreatedBy(data.getCreatedBy());
        entity.setCreatedDate(
                data.getCreatedDate() != null ? data.getCreatedDate() : new Timestamp(System.currentTimeMillis()));
        entity.setModifiedBy(data.getModifiedBy());
        entity.setLastModDate(
                data.getLastModDate() != null ? data.getLastModDate() : new Timestamp(System.currentTimeMillis()));

        return entity;
    }

    private String updateLeprosyFollowUpData(LeprosyFollowUpDTO data, LeprosyFollowUp entity) {
        entity.setVisitNumber(data.getVisitNumber());
        entity.setFollowUpDate(data.getFollowUpDate());
        entity.setTreatmentStatus(data.getTreatmentStatus());
        entity.setMdtBlisterPackReceived(data.getMdtBlisterPackReceived());
        entity.setTreatmentCompleteDate(data.getTreatmentCompleteDate());
        entity.setRemarks(data.getRemarks());
        entity.setHomeVisitDate(data.getHomeVisitDate());
        entity.setLeprosySymptoms(data.getLeprosySymptoms());
        entity.setTypeOfLeprosy(data.getTypeOfLeprosy());
        entity.setLeprosySymptomsPosition(data.getLeprosySymptomsPosition());
        entity.setVisitLabel(data.getVisitLabel());
        entity.setLeprosyStatus(data.getLeprosyStatus());
        entity.setReferredTo(data.getReferredTo());
        entity.setReferToName(data.getReferToName());
        entity.setTreatmentEndDate(data.getTreatmentEndDate());
        entity.setMdtBlisterPackRecived(data.getMdtBlisterPackRecived());
        entity.setTreatmentStartDate(data.getTreatmentStartDate());

        // Update audit info
        entity.setModifiedBy(data.getModifiedBy());
        entity.setLastModDate(
                data.getLastModDate() != null ? data.getLastModDate() : new Timestamp(System.currentTimeMillis()));

        leprosyFollowUpRepository.save(entity);
        return "Follow-up data updated successfully";
    }

    @Override
    public String saveLeprosyFollowUp(LeprosyFollowUpDTO dto) {
        if (dto == null)
            return "Invalid data";
        LeprosyFollowUp entity = saveLeprosyFollowUpData(dto);
        leprosyFollowUpRepository.save(entity);
        return "Follow-up data added successfully";

    }

    @Override
    public List<DiseaseGetLeprosyDTO> getAllLeprosyData(String createdBy) {
        logger.info("Fetching leprosy data for createdBy: " + createdBy);

        List<ScreeningLeprosy> leprosyList = diseaseLeprosyRepository.getByCreatedBy(createdBy);
        logger.info("Found " + leprosyList.size() + " leprosy records");

        List<DiseaseGetLeprosyDTO> dtos = new ArrayList<>();
        leprosyList.forEach(leprosy -> {
            dtos.add(modelMapper.map(leprosy, DiseaseGetLeprosyDTO.class));
            checkAndIncentive(leprosy,leprosy.getUserId());
        });

        return dtos;
    }
    private void checkAndIncentive(ScreeningLeprosy screeningLeprosy,Integer userId){
        if(screeningLeprosy.getIsConfirmed()){
            IncentiveActivityRecord incentiveActivityRecord =
                    incentiveLogicService.incentiveForIdentificationLeprosy(
                            screeningLeprosy.getBenId(),
                            screeningLeprosy.getHomeVisitDate(),
                            screeningLeprosy.getHomeVisitDate(),
                            userId);

            if (incentiveActivityRecord != null) {
                logger.info("Incentive processed for Screening Leprosy  successfully. RecordId={}",
                        incentiveActivityRecord.getId());
            } else {
                logger.info("Incentive not created");
            }
        }
        if (screeningLeprosy.getIsConfirmed()) {
            if(screeningLeprosy.getTypeOfLeprosy()!=null){
                if (screeningLeprosy.getTypeOfLeprosy().equals("PB (Paucibacillary)")) {
                    IncentiveActivityRecord incentiveActivityRecord =
                            incentiveLogicService.incentiveForLeprosyPaucibacillaryConfirmed(
                                    screeningLeprosy.getBenId(),
                                    screeningLeprosy.getTreatmentEndDate(),
                                    screeningLeprosy.getTreatmentEndDate(),
                                    userId);

                    if (incentiveActivityRecord != null) {
                        logger.info("Incentive processed for Screening Leprosy  successfully. RecordId={}",
                                incentiveActivityRecord.getId());
                    } else {
                        logger.info("Incentive not created");
                    }

                }
                if (screeningLeprosy.getTypeOfLeprosy().equals("MB (Multibacillary)")) {
                    IncentiveActivityRecord incentiveActivityRecord =
                            incentiveLogicService.incentiveForLeprosyMultibacillaryConfirmed(
                                    screeningLeprosy.getBenId(),
                                    screeningLeprosy.getTreatmentEndDate(),
                                    screeningLeprosy.getTreatmentEndDate(),
                                    userId);

                    if (incentiveActivityRecord != null) {
                        logger.info("Incentive processed for Screening Leprosy  successfully. RecordId={}",
                                incentiveActivityRecord.getId());
                    } else {
                        logger.info("Incentive not created");
                    }

                }
            }



        }
    }

    @Override
    public List<LeprosyGetFollowUpDTO> getAllLeprosyFollowUpData(String createdBy) {
        logger.info("Fetching leprosy data for createdBy: " + createdBy);

        List<LeprosyFollowUp> leprosyList = leprosyFollowUpRepository.getByCreatedBy(createdBy);
        logger.info("Found " + leprosyList.size() + " leprosy records");

        List<LeprosyGetFollowUpDTO> dtos = new ArrayList<>();
        leprosyList.forEach(leprosy -> {
            dtos.add(modelMapper.map(leprosy, LeprosyGetFollowUpDTO.class));
        });

        return dtos;
    }

    public Object getAllMalaria(GetDiseaseRequestHandler getDiseaseRequestHandler) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Fetch and filter malaria disease records
        List<ScreeningMalaria> filteredList = diseaseMalariaRepository.findAll().stream()
                .filter(disease -> Objects.equals(disease.getUserId(), getDiseaseRequestHandler.getAshaId()))
                .collect(Collectors.toList());

        // Check if the list is empty
        if (filteredList.isEmpty()) {
            return filteredList;
        }

        // Map to DTOs
        List<DiseaseMalariaDTO> dtoList = filteredList.stream().map(disease -> {

            DiseaseMalariaDTO dto = new DiseaseMalariaDTO();

            if (disease.getId() != null)
                dto.setId(disease.getId());

            if (disease.getBenId() != null)
                dto.setBenId(disease.getBenId());

            if (disease.getHouseHoldDetailsId() != null)
                dto.setHouseHoldDetailsId(disease.getHouseHoldDetailsId());

            if (disease.getScreeningDate() != null)
                dto.setScreeningDate(disease.getScreeningDate());

            if (disease.getBeneficiaryStatus() != null && !disease.getBeneficiaryStatus().trim().isEmpty())
                dto.setBeneficiaryStatus(disease.getBeneficiaryStatus());

            if (disease.getDateOfDeath() != null)
                dto.setDateOfDeath(disease.getDateOfDeath());

            if (disease.getPlaceOfDeath() != null && !disease.getPlaceOfDeath().trim().isEmpty())
                dto.setPlaceOfDeath(disease.getPlaceOfDeath());

            if (disease.getOtherPlaceOfDeath() != null && !disease.getOtherPlaceOfDeath().trim().isEmpty())
                dto.setOtherPlaceOfDeath(disease.getOtherPlaceOfDeath());

            if (disease.getReasonForDeath() != null && !disease.getReasonForDeath().trim().isEmpty())
                dto.setReasonForDeath(disease.getReasonForDeath());

            if (disease.getOtherReasonForDeath() != null && !disease.getOtherReasonForDeath().trim().isEmpty())
                dto.setOtherReasonForDeath(disease.getOtherReasonForDeath());

            if (disease.getCaseStatus() != null && !disease.getCaseStatus().trim().isEmpty())
                dto.setCaseStatus(disease.getCaseStatus());

            if (disease.getRapidDiagnosticTest() != null)
                dto.setRapidDiagnosticTest(disease.getRapidDiagnosticTest());

            if (disease.getDateOfRdt() != null)
                dto.setDateOfRdt(disease.getDateOfRdt());

            if (disease.getSlideTestPf() != null)
                dto.setSlideTestPf(disease.getSlideTestPf());

            if (disease.getSlideTestPv() != null)
                dto.setSlideTestPv(disease.getSlideTestPv());

            if (disease.getDateOfSlideTest() != null)
                dto.setDateOfSlideTest(disease.getDateOfSlideTest());

            if (disease.getSlideNo() != null && !disease.getSlideNo().trim().isEmpty())
                dto.setSlideNo(disease.getSlideNo());

            if (disease.getReferredTo() != null)
                dto.setReferredTo(disease.getReferredTo());

            if (disease.getOtherReferredFacility() != null && !disease.getOtherReferredFacility().trim().isEmpty())
                dto.setOtherReferredFacility(disease.getOtherReferredFacility());

            if (disease.getRemarks() != null && !disease.getRemarks().trim().isEmpty())
                dto.setRemarks(disease.getRemarks());

            if (disease.getMalariaSlideTestType() != null && !disease.getMalariaSlideTestType().trim().isEmpty())
                dto.setMalariaSlideTestType(disease.getMalariaSlideTestType());

            if (disease.getMalariaTestType() != null && !disease.getMalariaTestType().trim().isEmpty())
                dto.setMalariaTestType(disease.getMalariaTestType());

            if (disease.getDateOfVisitBySupervisor() != null)
                dto.setDateOfVisitBySupervisor(disease.getDateOfVisitBySupervisor());

            if (disease.getUserId() != null)
                dto.setUserId(disease.getUserId());

            if (disease.getDiseaseTypeId() != null)
                dto.setDiseaseTypeId(disease.getDiseaseTypeId());

            // Symptoms JSON
            try {
                if (disease.getSymptoms() != null &&
                        !disease.getSymptoms().trim().isEmpty()) {

                    MalariaSymptomsDTO symptomsDTO =
                            objectMapper.readValue(disease.getSymptoms(), MalariaSymptomsDTO.class);

                    dto.setFeverMoreThanTwoWeeks(symptomsDTO.isFeverMoreThanTwoWeeks());
                    dto.setFluLikeIllness(symptomsDTO.isFluLikeIllness());
                    dto.setShakingChills(symptomsDTO.isShakingChills());
                    dto.setHeadache(symptomsDTO.isHeadache());
                    dto.setMuscleAches(symptomsDTO.isMuscleAches());
                    dto.setTiredness(symptomsDTO.isTiredness());
                    dto.setNausea(symptomsDTO.isNausea());
                    dto.setVomiting(symptomsDTO.isVomiting());
                    dto.setDiarrhea(symptomsDTO.isDiarrhea());
                }
            } catch (Exception e) {
                logger.error("Error parsing symptoms for diseaseId={}",
                        disease.getId(), e);
            }

            return dto;
        }).collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public Object getAllScreeningData(GetDiseaseRequestHandler getDiseaseRequestHandler) {


        if (getDiseaseRequestHandler.getDiseaseTypeID() == DiseaseType.MALARIA.getId()) {
            return getAllMalaria(getDiseaseRequestHandler);

        } else if (getDiseaseRequestHandler.getDiseaseTypeID() == DiseaseType.KALA_AZAR.getId()) {
            return getAllKalaAzar(getDiseaseRequestHandler);

        } else if (getDiseaseRequestHandler.getDiseaseTypeID() == DiseaseType.AES_JE.getId()) {
            return getAllKalaAES(getDiseaseRequestHandler);

        } else if (getDiseaseRequestHandler.getDiseaseTypeID() == DiseaseType.FILARIA.getId()) {
            return getAllFilaria(getDiseaseRequestHandler);

        } else if (getDiseaseRequestHandler.getDiseaseTypeID() == DiseaseType.LEPROSY.getId()) {
            return getAllLeprosy(getDiseaseRequestHandler);

        }
        return "No data found";
    }

    public Object getAllKalaAzar(GetDiseaseRequestHandler getDiseaseRequestHandler) {

        // Fetch and filter Kala Azar disease records
        List<ScreeningKalaAzar> filteredList = diseaseKalaAzarRepository.findAll().stream()
                .filter(disease -> (Objects.equals(disease.getUserId(), getDiseaseRequestHandler.getAshaId())))
                .collect(Collectors.toList());

        // Check if the list is empty
        if (filteredList.isEmpty()) {
            return filteredList;
        }

        // Map to DTOs
        List<DiseaseKalaAzarDTO> dtoList = filteredList.stream().map(disease -> {

            DiseaseKalaAzarDTO dto = new DiseaseKalaAzarDTO();

            if (disease.getId() != null)
                dto.setId(disease.getId());

            if (disease.getBenId() != null)
                dto.setBenId(disease.getBenId());

            if (disease.getHouseHoldDetailsId() != null)
                dto.setHouseHoldDetailsId(disease.getHouseHoldDetailsId());

            if (disease.getVisitDate() != null)
                dto.setVisitDate(disease.getVisitDate());

            if (disease.getBeneficiaryStatus() != null && !disease.getBeneficiaryStatus().trim().isEmpty())
                dto.setBeneficiaryStatus(disease.getBeneficiaryStatus());

            if (disease.getDateOfDeath() != null)
                dto.setDateOfDeath(disease.getDateOfDeath());

            if (disease.getPlaceOfDeath() != null && !disease.getPlaceOfDeath().trim().isEmpty())
                dto.setPlaceOfDeath(disease.getPlaceOfDeath());

            if (disease.getOtherPlaceOfDeath() != null && !disease.getOtherPlaceOfDeath().trim().isEmpty())
                dto.setOtherPlaceOfDeath(disease.getOtherPlaceOfDeath());

            if (disease.getReasonForDeath() != null && !disease.getReasonForDeath().trim().isEmpty())
                dto.setReasonForDeath(disease.getReasonForDeath());

            if (disease.getOtherReasonForDeath() != null && !disease.getOtherReasonForDeath().trim().isEmpty())
                dto.setOtherReasonForDeath(disease.getOtherReasonForDeath());

            if (disease.getKalaAzarCaseStatus() != null && !disease.getKalaAzarCaseStatus().trim().isEmpty())
                dto.setKalaAzarCaseStatus(disease.getKalaAzarCaseStatus());

            if (disease.getKalaAzarCaseCount() != null)
                dto.setKalaAzarCaseCount(disease.getKalaAzarCaseCount());

            if (disease.getRapidDiagnosticTest() != null)
                dto.setRapidDiagnosticTest(disease.getRapidDiagnosticTest());

            if (disease.getDateOfRdt() != null)
                dto.setDateOfRdt(disease.getDateOfRdt());

            if (disease.getFollowUpPoint() != null)
                dto.setFollowUpPoint(disease.getFollowUpPoint());

            if (disease.getReferredTo() != null && !disease.getReferredTo().trim().isEmpty())
                dto.setReferredTo(disease.getReferredTo());

            if (disease.getOtherReferredFacility() != null && !disease.getOtherReferredFacility().trim().isEmpty())
                dto.setOtherReferredFacility(disease.getOtherReferredFacility());

            if (disease.getCreatedDate() != null)
                dto.setCreatedDate(disease.getCreatedDate());

            if (disease.getCreatedBy() != null && !disease.getCreatedBy().trim().isEmpty())
                dto.setCreatedBy(disease.getCreatedBy());

            if (disease.getBeneficiaryStatusId() != null)
                dto.setBeneficiaryStatusId(disease.getBeneficiaryStatusId());

            if (disease.getReferToName() != null && !disease.getReferToName().trim().isEmpty())
                dto.setReferToName(disease.getReferToName());

            if (disease.getUserId() != null)
                dto.setUserId(disease.getUserId());

            if (disease.getDiseaseTypeId() != null)
                dto.setDiseaseTypeId(disease.getDiseaseTypeId());

            return dto;
        }).collect(Collectors.toList());

        return dtoList;
    }


    public Object getAllKalaAES(GetDiseaseRequestHandler getDiseaseRequestHandler) {
        if (diseaseAESJERepository.findAll().isEmpty()) {
            return diseaseAESJERepository.findAll();
        }

        return diseaseAESJERepository.findAll().stream().filter(diseaseAesje -> Objects.equals(diseaseAesje.getUserId(), getDiseaseRequestHandler.getAshaId())).collect(Collectors.toList());
    }


    public Object getAllFilaria(GetDiseaseRequestHandler getDiseaseRequestHandler) {

        // Fetch and filter Filaria disease records
        List<ScreeningFilariasis> filteredList = diseaseFilariasisRepository.findAll().stream().filter(screeningFilariasis -> Objects.equals(screeningFilariasis.getUserId(), getDiseaseRequestHandler.getAshaId())).collect(Collectors.toList());

        // Check if the list is empty
        if (filteredList.isEmpty()) {
            return Collections.singletonMap("message", "No data found for Filaria.");
        }

        // Map to DTOs
        List<DiseaseFilariasisDTO> dtoList = filteredList.stream().map(disease -> {

            DiseaseFilariasisDTO dto = new DiseaseFilariasisDTO();

            if (disease.getId() != null)
                dto.setId(disease.getId());

            if (disease.getBenId() != null)
                dto.setBenId(disease.getBenId());

            if (disease.getHouseHoldDetailsId() != null)
                dto.setHouseHoldDetailsId(disease.getHouseHoldDetailsId());

            if (disease.getSufferingFromFilariasis() != null)
                dto.setSufferingFromFilariasis(disease.getSufferingFromFilariasis());

            if (disease.getAffectedBodyPart() != null &&
                    !disease.getAffectedBodyPart().trim().isEmpty())
                dto.setAffectedBodyPart(disease.getAffectedBodyPart());

            if (disease.getMdaHomeVisitDate() != null)
                dto.setMdaHomeVisitDate(disease.getMdaHomeVisitDate());

            if (disease.getDoseStatus() != null &&
                    !disease.getDoseStatus().trim().isEmpty())
                dto.setDoseStatus(disease.getDoseStatus());

            if (disease.getFilariasisCaseCount() != null)
                dto.setFilariasisCaseCount(disease.getFilariasisCaseCount());

            if (disease.getOtherDoseStatusDetails() != null &&
                    !disease.getOtherDoseStatusDetails().trim().isEmpty())
                dto.setOtherDoseStatusDetails(disease.getOtherDoseStatusDetails());

            if (disease.getMedicineSideEffect() != null)
                dto.setMedicineSideEffect(disease.getMedicineSideEffect());

            if (disease.getOtherSideEffectDetails() != null &&
                    !disease.getOtherSideEffectDetails().trim().isEmpty())
                dto.setOtherSideEffectDetails(disease.getOtherSideEffectDetails());

            if (disease.getCreatedDate() != null)
                dto.setCreatedDate(disease.getCreatedDate());

            if (disease.getCreatedBy() != null &&
                    !disease.getCreatedBy().trim().isEmpty())
                dto.setCreatedBy(disease.getCreatedBy());

            if (disease.getUserId() != null)
                dto.setUserId(disease.getUserId());

            return dto;

        }).collect(Collectors.toList());

        return dtoList;
    }


    public Object getAllLeprosy(GetDiseaseRequestHandler getDiseaseRequestHandler) {

        // Fetch and filter Leprosy disease records
        List<ScreeningLeprosy> filteredList = diseaseLeprosyRepository.findAll().stream()
                .filter(disease -> Objects.equals(disease.getUserId(), getDiseaseRequestHandler.getAshaId()))
                .collect(Collectors.toList());

        // Check if the list is empty
        if (filteredList.isEmpty()) {
            return filteredList;
        }

        // Map to DTOs
        List<DiseaseLeprosyDTO> dtoList = filteredList.stream().map(disease -> {
            DiseaseLeprosyDTO dto = new DiseaseLeprosyDTO();
            if (disease.getId() != null)
                dto.setId(disease.getId());

            if (disease.getBenId() != null)
                dto.setBenId(disease.getBenId());

            if (disease.getHouseHoldDetailsId() != null)
                dto.setHouseHoldDetailsId(disease.getHouseHoldDetailsId());

            if (disease.getHomeVisitDate() != null)
                dto.setHomeVisitDate(disease.getHomeVisitDate());

            if (disease.getLeprosyStatus() != null && !disease.getLeprosyStatus().trim().isEmpty())
                dto.setLeprosyStatus(disease.getLeprosyStatus());

            if (disease.getReferredTo() != null && !disease.getReferredTo().trim().isEmpty())
                dto.setReferredTo(disease.getReferredTo());

            if (disease.getOtherReferredTo() != null && !disease.getOtherReferredTo().trim().isEmpty())
                dto.setOtherReferredTo(disease.getOtherReferredTo());

            if (disease.getLeprosyStatusDate() != null)
                dto.setLeprosyStatusDate(disease.getLeprosyStatusDate());

            if (disease.getTypeOfLeprosy() != null && !disease.getTypeOfLeprosy().trim().isEmpty())
                dto.setTypeOfLeprosy(disease.getTypeOfLeprosy());

            if (disease.getFollowUpDate() != null)
                dto.setFollowUpDate(disease.getFollowUpDate());

            if (disease.getLeprosyStatus() != null && !disease.getLeprosyStatus().trim().isEmpty())
                dto.setBeneficiaryStatus(disease.getLeprosyStatus());

            if (disease.getRemark() != null && !disease.getRemark().trim().isEmpty())
                dto.setRemark(disease.getRemark());

            if (disease.getUserId() != null)
                dto.setUserId(disease.getUserId());


            return dto;
        }).collect(Collectors.toList());

        return dtoList;
    }


    private ScreeningKalaAzar saveKalaAzarDisease(DiseaseKalaAzarDTO dto) {
        logger.info("KalaAzarRequest: " + dto);
        ScreeningKalaAzar entity = new ScreeningKalaAzar();

        entity.setBenId(dto.getBenId());
        entity.setHouseHoldDetailsId(dto.getHouseHoldDetailsId());
        entity.setVisitDate(dto.getVisitDate());
        entity.setBeneficiaryStatus(dto.getBeneficiaryStatus());
        entity.setDateOfDeath(dto.getDateOfDeath());
        entity.setPlaceOfDeath(dto.getPlaceOfDeath());
        entity.setOtherPlaceOfDeath(dto.getOtherPlaceOfDeath());
        entity.setReasonForDeath(dto.getReasonForDeath());
        entity.setOtherReasonForDeath(dto.getOtherReasonForDeath());
        entity.setKalaAzarCaseStatus(dto.getKalaAzarCaseStatus());
        entity.setDiseaseTypeId(dto.getDiseaseTypeId());
        entity.setKalaAzarCaseCount(dto.getKalaAzarCaseCount());
        entity.setRapidDiagnosticTest(dto.getRapidDiagnosticTest());
        entity.setDateOfRdt(dto.getDateOfRdt());
        entity.setFollowUpPoint(dto.getFollowUpPoint());
        entity.setReferredTo(dto.getReferredTo());
        entity.setOtherReferredFacility(dto.getOtherReferredFacility());
        entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));  // or dto.getCreatedDate()
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setBeneficiaryStatusId(dto.getBeneficiaryStatusId());
        entity.setReferToName(dto.getReferToName());
        entity.setUserId(dto.getUserId());


        ScreeningKalaAzar saved = diseaseKalaAzarRepository.save(entity);

        return saved; // You can also return a custom response or DTO
    }


    private String updateKalaAzarDisease(DiseaseKalaAzarDTO dto) {
        Optional<ScreeningKalaAzar> optional = diseaseKalaAzarRepository.findByBenId(dto.getBenId());

        if (!optional.isPresent()) {
            return "Record not found with ID: " + dto.getId();
        }

        ScreeningKalaAzar entity = optional.get();

        // Update fields
        entity.setBenId(dto.getBenId());
        entity.setHouseHoldDetailsId(dto.getHouseHoldDetailsId());
        entity.setVisitDate(dto.getVisitDate());
        entity.setBeneficiaryStatus(dto.getBeneficiaryStatus());
        entity.setDateOfDeath(dto.getDateOfDeath());
        entity.setPlaceOfDeath(dto.getPlaceOfDeath());
        entity.setDiseaseTypeId(dto.getDiseaseTypeId());
        entity.setOtherPlaceOfDeath(dto.getOtherPlaceOfDeath());
        entity.setReasonForDeath(dto.getReasonForDeath());
        entity.setOtherReasonForDeath(dto.getOtherReasonForDeath());
        entity.setKalaAzarCaseStatus(dto.getKalaAzarCaseStatus());
        entity.setKalaAzarCaseCount(dto.getKalaAzarCaseCount());
        entity.setRapidDiagnosticTest(dto.getRapidDiagnosticTest());
        entity.setDateOfRdt(dto.getDateOfRdt());
        entity.setFollowUpPoint(dto.getFollowUpPoint());
        entity.setReferredTo(dto.getReferredTo());
        entity.setOtherReferredFacility(dto.getOtherReferredFacility());
        entity.setCreatedBy(dto.getCreatedBy());
        // You can also update createdDate if required
        entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        entity.setReferToName(dto.getReferToName());
        entity.setBeneficiaryStatusId(dto.getBeneficiaryStatusId());
        entity.setUserId(dto.getUserId());

        diseaseKalaAzarRepository.save(entity);

        return "Kala Azar record updated successfully!";
    }


    private ScreeningLeprosy saveLeprosyData(DiseaseLeprosyDTO diseaseControlData) {
        ScreeningLeprosy diseaseLeprosy = new ScreeningLeprosy();

        // Setting the values from the DTO to the entity
        diseaseLeprosy.setBenId(diseaseControlData.getBenId());
        diseaseLeprosy.setHouseHoldDetailsId(diseaseControlData.getHouseHoldDetailsId());
        diseaseLeprosy.setHomeVisitDate(diseaseControlData.getHomeVisitDate());
        diseaseLeprosy.setLeprosyStatus(diseaseControlData.getLeprosyStatus());
        diseaseLeprosy.setReferredTo(diseaseControlData.getReferredTo());
        diseaseLeprosy.setDiseaseTypeId(diseaseControlData.getDiseaseTypeId());
        diseaseLeprosy.setOtherReferredTo(diseaseControlData.getOtherReferredTo());
        diseaseLeprosy.setLeprosyStatusDate(diseaseControlData.getLeprosyStatusDate());
        if(diseaseControlData.getTypeOfLeprosy()!=null){
            diseaseLeprosy.setTypeOfLeprosy(diseaseControlData.getTypeOfLeprosy());

        }
        diseaseLeprosy.setFollowUpDate(diseaseControlData.getFollowUpDate());
        diseaseLeprosy.setBeneficiaryStatus(diseaseControlData.getBeneficiaryStatus());
        diseaseLeprosy.setBeneficiaryStatusId(diseaseControlData.getBeneficiaryStatusId());
        diseaseLeprosy.setReferToName(diseaseControlData.getReferToName());
        diseaseLeprosy.setPlaceOfDeath(diseaseControlData.getPlaceOfDeath());
        diseaseLeprosy.setDateOfDeath(diseaseControlData.getDateOfDeath());
        diseaseLeprosy.setOtherPlaceOfDeath(diseaseControlData.getOtherPlaceOfDeath());
        diseaseLeprosy.setOtherReasonForDeath(diseaseControlData.getOtherReasonForDeath());
        diseaseLeprosy.setRemark(diseaseControlData.getRemark());
        diseaseLeprosy.setUserId(diseaseControlData.getUserId());

        diseaseLeprosy.setLeprosySymptoms(diseaseControlData.getLeprosySymptoms());
        diseaseLeprosy.setLeprosySymptomsPosition(diseaseControlData.getLeprosySymptomsPosition());
        diseaseLeprosy.setLerosyStatusPosition(diseaseControlData.getLerosyStatusPosition());
        diseaseLeprosy.setCurrentVisitNumber(diseaseControlData.getCurrentVisitNumber());
        diseaseLeprosy.setVisitLabel(diseaseControlData.getVisitLabel());
        diseaseLeprosy.setVisitNumber(diseaseControlData.getVisitNumber());
        diseaseLeprosy.setIsConfirmed(diseaseControlData.getIsConfirmed());
        diseaseLeprosy.setLeprosyState(diseaseControlData.getLeprosyState());
        diseaseLeprosy.setTreatmentStartDate(diseaseControlData.getTreatmentStartDate());
        diseaseLeprosy.setTotalFollowUpMonthsRequired(diseaseControlData.getTotalFollowUpMonthsRequired());
        diseaseLeprosy.setTreatmentEndDate(diseaseControlData.getTreatmentEndDate());
        diseaseLeprosy.setMdtBlisterPackRecived(diseaseControlData.getMdtBlisterPackRecived());
        diseaseLeprosy.setTreatmentStatus(diseaseControlData.getTreatmentStatus());
        diseaseLeprosy.setCreatedBy(diseaseControlData.getCreatedBy());
        diseaseLeprosy.setCreatedDate(diseaseControlData.getCreatedDate());
        diseaseLeprosy.setModifiedBy(diseaseControlData.getModifiedBy());
        diseaseLeprosy.setLastModDate(diseaseControlData.getLastModDate());

        diseaseLeprosy.setRecurrentUlcerationId(diseaseControlData.getRecurrentUlcerationId());
        diseaseLeprosy.setRecurrentTinglingId(diseaseControlData.getRecurrentTinglingId());
        diseaseLeprosy.setHypopigmentedPatchId(diseaseControlData.getHypopigmentedPatchId());
        diseaseLeprosy.setThickenedSkinId(diseaseControlData.getThickenedSkinId());
        diseaseLeprosy.setSkinNodulesId(diseaseControlData.getSkinNodulesId());
        diseaseLeprosy.setSkinPatchDiscolorationId(diseaseControlData.getSkinPatchDiscolorationId());
        diseaseLeprosy.setRecurrentNumbnessId(diseaseControlData.getRecurrentNumbnessId());
        diseaseLeprosy.setClawingFingersId(diseaseControlData.getClawingFingersId());
        diseaseLeprosy.setTinglingNumbnessExtremitiesId(diseaseControlData.getTinglingNumbnessExtremitiesId());
        diseaseLeprosy.setInabilityCloseEyelidId(diseaseControlData.getInabilityCloseEyelidId());
        diseaseLeprosy.setDifficultyHoldingObjectsId(diseaseControlData.getDifficultyHoldingObjectsId());
        diseaseLeprosy.setWeaknessFeetId(diseaseControlData.getWeaknessFeetId());

        diseaseLeprosy.setRecurrentUlceration(diseaseControlData.getRecurrentUlceration());
        diseaseLeprosy.setRecurrentTingling(diseaseControlData.getRecurrentTingling());
        diseaseLeprosy.setHypopigmentedPatch(diseaseControlData.getHypopigmentedPatch());
        diseaseLeprosy.setThickenedSkin(diseaseControlData.getThickenedSkin());
        diseaseLeprosy.setSkinNodules(diseaseControlData.getSkinNodules());
        diseaseLeprosy.setSkinPatchDiscoloration(diseaseControlData.getSkinPatchDiscoloration());
        diseaseLeprosy.setRecurrentNumbness(diseaseControlData.getRecurrentNumbness());
        diseaseLeprosy.setClawingFingers(diseaseControlData.getClawingFingers());
        diseaseLeprosy.setTinglingNumbnessExtremities(diseaseControlData.getTinglingNumbnessExtremities());
        diseaseLeprosy.setInabilityCloseEyelid(diseaseControlData.getInabilityCloseEyelid());
        diseaseLeprosy.setDifficultyHoldingObjects(diseaseControlData.getDifficultyHoldingObjects());
        diseaseLeprosy.setWeaknessFeet(diseaseControlData.getWeaknessFeet());


        return diseaseLeprosy;
    }

    private String updateLeprosyData(DiseaseLeprosyDTO diseaseControlData) {
        // Fetch the existing record from the database using the benId
        ScreeningLeprosy existingDiseaseLeprosy = diseaseLeprosyRepository.findByBenId(diseaseControlData.getBenId())
                .orElseThrow(() -> new RuntimeException("Leprosy record not found for benId: " + diseaseControlData.getBenId()));

        // Update the fields from the DTO to the existing entity
        existingDiseaseLeprosy.setHouseHoldDetailsId(diseaseControlData.getHouseHoldDetailsId());
        existingDiseaseLeprosy.setHomeVisitDate(diseaseControlData.getHomeVisitDate());
        existingDiseaseLeprosy.setLeprosyStatus(diseaseControlData.getLeprosyStatus());
        existingDiseaseLeprosy.setReferredTo(diseaseControlData.getReferredTo());
        existingDiseaseLeprosy.setDiseaseTypeId(diseaseControlData.getDiseaseTypeId());
        existingDiseaseLeprosy.setOtherReferredTo(diseaseControlData.getOtherReferredTo());
        existingDiseaseLeprosy.setLeprosyStatusDate(diseaseControlData.getLeprosyStatusDate());
        existingDiseaseLeprosy.setTypeOfLeprosy(diseaseControlData.getTypeOfLeprosy());
        existingDiseaseLeprosy.setFollowUpDate(diseaseControlData.getFollowUpDate());
        existingDiseaseLeprosy.setBeneficiaryStatus(diseaseControlData.getBeneficiaryStatus());
        existingDiseaseLeprosy.setBeneficiaryStatusId(diseaseControlData.getBeneficiaryStatusId());
        existingDiseaseLeprosy.setReferToName(diseaseControlData.getReferToName());
        existingDiseaseLeprosy.setPlaceOfDeath(diseaseControlData.getPlaceOfDeath());
        existingDiseaseLeprosy.setDateOfDeath(diseaseControlData.getDateOfDeath());
        existingDiseaseLeprosy.setOtherPlaceOfDeath(diseaseControlData.getOtherPlaceOfDeath());
        existingDiseaseLeprosy.setOtherReasonForDeath(diseaseControlData.getOtherReasonForDeath());
        existingDiseaseLeprosy.setRemark(diseaseControlData.getRemark());

        existingDiseaseLeprosy.setLeprosySymptoms(diseaseControlData.getLeprosySymptoms());
        existingDiseaseLeprosy.setLeprosySymptomsPosition(diseaseControlData.getLeprosySymptomsPosition());
        existingDiseaseLeprosy.setLerosyStatusPosition(diseaseControlData.getLerosyStatusPosition());
        existingDiseaseLeprosy.setCurrentVisitNumber(diseaseControlData.getCurrentVisitNumber());
        existingDiseaseLeprosy.setVisitLabel(diseaseControlData.getVisitLabel());
        existingDiseaseLeprosy.setVisitNumber(diseaseControlData.getVisitNumber());
        existingDiseaseLeprosy.setIsConfirmed(diseaseControlData.getIsConfirmed());
        existingDiseaseLeprosy.setLeprosyState(diseaseControlData.getLeprosyState());
        existingDiseaseLeprosy.setTreatmentStartDate(diseaseControlData.getTreatmentStartDate());
        existingDiseaseLeprosy.setTotalFollowUpMonthsRequired(diseaseControlData.getTotalFollowUpMonthsRequired());
        existingDiseaseLeprosy.setTreatmentEndDate(diseaseControlData.getTreatmentEndDate());
        existingDiseaseLeprosy.setMdtBlisterPackRecived(diseaseControlData.getMdtBlisterPackRecived());
        existingDiseaseLeprosy.setTreatmentStatus(diseaseControlData.getTreatmentStatus());

        existingDiseaseLeprosy.setModifiedBy(diseaseControlData.getModifiedBy());
        existingDiseaseLeprosy.setLastModDate(diseaseControlData.getLastModDate());

        existingDiseaseLeprosy.setRecurrentUlcerationId(diseaseControlData.getRecurrentUlcerationId());
        existingDiseaseLeprosy.setRecurrentTinglingId(diseaseControlData.getRecurrentTinglingId());
        existingDiseaseLeprosy.setHypopigmentedPatchId(diseaseControlData.getHypopigmentedPatchId());
        existingDiseaseLeprosy.setThickenedSkinId(diseaseControlData.getThickenedSkinId());
        existingDiseaseLeprosy.setSkinNodulesId(diseaseControlData.getSkinNodulesId());
        existingDiseaseLeprosy.setSkinPatchDiscolorationId(diseaseControlData.getSkinPatchDiscolorationId());
        existingDiseaseLeprosy.setRecurrentNumbnessId(diseaseControlData.getRecurrentNumbnessId());
        existingDiseaseLeprosy.setClawingFingersId(diseaseControlData.getClawingFingersId());
        existingDiseaseLeprosy.setTinglingNumbnessExtremitiesId(diseaseControlData.getTinglingNumbnessExtremitiesId());
        existingDiseaseLeprosy.setInabilityCloseEyelidId(diseaseControlData.getInabilityCloseEyelidId());
        existingDiseaseLeprosy.setDifficultyHoldingObjectsId(diseaseControlData.getDifficultyHoldingObjectsId());
        existingDiseaseLeprosy.setWeaknessFeetId(diseaseControlData.getWeaknessFeetId());

        existingDiseaseLeprosy.setRecurrentUlceration(diseaseControlData.getRecurrentUlceration());
        existingDiseaseLeprosy.setRecurrentTingling(diseaseControlData.getRecurrentTingling());
        existingDiseaseLeprosy.setHypopigmentedPatch(diseaseControlData.getHypopigmentedPatch());
        existingDiseaseLeprosy.setThickenedSkin(diseaseControlData.getThickenedSkin());
        existingDiseaseLeprosy.setSkinNodules(diseaseControlData.getSkinNodules());
        existingDiseaseLeprosy.setSkinPatchDiscoloration(diseaseControlData.getSkinPatchDiscoloration());
        existingDiseaseLeprosy.setRecurrentNumbness(diseaseControlData.getRecurrentNumbness());
        existingDiseaseLeprosy.setClawingFingers(diseaseControlData.getClawingFingers());
        existingDiseaseLeprosy.setTinglingNumbnessExtremities(diseaseControlData.getTinglingNumbnessExtremities());
        existingDiseaseLeprosy.setInabilityCloseEyelid(diseaseControlData.getInabilityCloseEyelid());
        existingDiseaseLeprosy.setDifficultyHoldingObjects(diseaseControlData.getDifficultyHoldingObjects());
        existingDiseaseLeprosy.setWeaknessFeet(diseaseControlData.getWeaknessFeet());


        diseaseLeprosyRepository.save(existingDiseaseLeprosy);
        // Return the updated entity
        return "Data update successfully";
    }

    // Save Malaria
    private ScreeningMalaria saveMalariaDisease(DiseaseMalariaDTO requestData) {
        ScreeningMalaria diseaseScreening = new ScreeningMalaria();

        diseaseScreening.setBenId(requestData.getBenId());
        diseaseScreening.setHouseHoldDetailsId(requestData.getHouseHoldDetailsId());
        diseaseScreening.setScreeningDate(requestData.getScreeningDate());
        diseaseScreening.setBeneficiaryStatus(requestData.getBeneficiaryStatus());
        diseaseScreening.setDateOfDeath(requestData.getDateOfDeath());
        diseaseScreening.setPlaceOfDeath(requestData.getPlaceOfDeath());
        diseaseScreening.setOtherPlaceOfDeath(requestData.getOtherPlaceOfDeath());
        diseaseScreening.setReasonForDeath(requestData.getReasonForDeath());
        diseaseScreening.setOtherReasonForDeath(requestData.getOtherReasonForDeath());
        diseaseScreening.setDiseaseTypeId(requestData.getDiseaseTypeId());
        diseaseScreening.setSymptoms(convertSelecteddiseaseScreeningToJson(requestData)); // Convert specific fields to JSON
        diseaseScreening.setUserId(requestData.getUserId());
        diseaseScreening.setCaseStatus(requestData.getCaseStatus());
        diseaseScreening.setRapidDiagnosticTest(requestData.getRapidDiagnosticTest());
        diseaseScreening.setDateOfRdt(requestData.getDateOfRdt());
        diseaseScreening.setSlideTestPf(requestData.getSlideTestPf());
        diseaseScreening.setSlideTestPv(requestData.getSlideTestPv());
        diseaseScreening.setDateOfSlideTest(requestData.getDateOfSlideTest());
        diseaseScreening.setSlideNo(requestData.getSlideNo());
        diseaseScreening.setMalariaTestType(requestData.getMalariaTestType());
        diseaseScreening.setMalariaSlideTestType(requestData.getMalariaSlideTestType());
        diseaseScreening.setReferredTo(requestData.getReferredTo());
        diseaseScreening.setOtherReferredFacility(requestData.getOtherReferredFacility());
        diseaseScreening.setRemarks(requestData.getRemarks());
        diseaseScreening.setDateOfVisitBySupervisor(requestData.getDateOfVisitBySupervisor());
        diseaseScreening.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        checkAndAddIncentives(diseaseScreening);

        return diseaseMalariaRepository.save(diseaseScreening);
    }

    // Update Malaria
    private String updateMalariaDisease(DiseaseMalariaDTO requestData) {
        return diseaseMalariaRepository.findByBenId(requestData.getBenId()).map(diseaseScreening -> {
            diseaseScreening.setBenId(requestData.getBenId());
            diseaseScreening.setHouseHoldDetailsId(requestData.getHouseHoldDetailsId());
            diseaseScreening.setScreeningDate(requestData.getScreeningDate());
            diseaseScreening.setBeneficiaryStatus(requestData.getBeneficiaryStatus());
            diseaseScreening.setDateOfDeath(requestData.getDateOfDeath());
            diseaseScreening.setPlaceOfDeath(requestData.getPlaceOfDeath());
            diseaseScreening.setOtherPlaceOfDeath(requestData.getOtherPlaceOfDeath());
            diseaseScreening.setReasonForDeath(requestData.getReasonForDeath());
            diseaseScreening.setOtherReasonForDeath(requestData.getOtherReasonForDeath());
            diseaseScreening.setDiseaseTypeId(requestData.getDiseaseTypeId());
            diseaseScreening.setSymptoms(convertSelecteddiseaseScreeningToJson(requestData)); // Convert specific fields to JSON
            diseaseScreening.setUserId(requestData.getUserId());
            diseaseScreening.setCaseStatus(requestData.getCaseStatus());
            diseaseScreening.setRapidDiagnosticTest(requestData.getRapidDiagnosticTest());
            diseaseScreening.setDateOfRdt(requestData.getDateOfRdt());
            diseaseScreening.setSlideTestPf(requestData.getSlideTestPf());
            diseaseScreening.setSlideTestPv(requestData.getSlideTestPv());
            diseaseScreening.setDateOfSlideTest(requestData.getDateOfSlideTest());
            diseaseScreening.setSlideNo(requestData.getSlideNo());
            diseaseScreening.setReferredTo(requestData.getReferredTo());
            diseaseScreening.setOtherReferredFacility(requestData.getOtherReferredFacility());
            diseaseScreening.setMalariaSlideTestType(requestData.getMalariaSlideTestType());
            diseaseScreening.setMalariaTestType(requestData.getMalariaTestType());
            diseaseScreening.setRemarks(requestData.getRemarks());
            diseaseScreening.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
            diseaseScreening.setDateOfVisitBySupervisor(requestData.getDateOfVisitBySupervisor());
            diseaseScreening.setReferToName(requestData.getReferToName());
            diseaseScreening.setCaseStatusId(requestData.getCaseStatusId());
            diseaseMalariaRepository.save(diseaseScreening);
            return "Data update successfully";

        }).orElseThrow(() -> new RuntimeException("Data not found"));
    }


    private String convertSelecteddiseaseScreeningToJson(DiseaseMalariaDTO requestData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> diseaseScreeningMap = new HashMap<>();
            diseaseScreeningMap.put("nausea", requestData.isNausea());
            diseaseScreeningMap.put("diarrhea", requestData.isDiarrhea());
            diseaseScreeningMap.put("tiredness", requestData.isTiredness());
            diseaseScreeningMap.put("vomiting", requestData.isVomiting());
            diseaseScreeningMap.put("headache", requestData.isHeadache());
            diseaseScreeningMap.put("feverMoreThanTwoWeeks", requestData.isFeverMoreThanTwoWeeks());
            diseaseScreeningMap.put("fluLikeIllness", requestData.isFluLikeIllness());
            diseaseScreeningMap.put("shakingChills", requestData.isShakingChills());

            return objectMapper.writeValueAsString(diseaseScreeningMap);
        } catch (Exception e) {
            throw new RuntimeException("Error converting selected diseaseScreening fields to JSON", e);
        }
    }

    @Override
    public List<MosquitoNetDTO> saveMosquitoMobilizationNet(List<MosquitoNetDTO> mosquitoNetDTOList) {

        // DTO → Entity
        List<MosquitoNetEntity> entityList = mosquitoNetDTOList.stream().map(dto -> {

            MosquitoNetEntity entity = new MosquitoNetEntity();
            if(!beneficiaryRepo.findByHouseoldId(dto.getHouseHoldId()).isEmpty()){
                entity.setBeneficiaryId(beneficiaryRepo.findByHouseoldId(dto.getHouseHoldId()).get(0).getBenficieryid());

            }
            entity.setHouseHoldId(dto.getHouseHoldId());

            // ✅ String → LocalDate conversion
            if (dto.getVisitDate() != null && !dto.getVisitDate().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                entity.setVisitDate(LocalDate.parse(dto.getVisitDate(), formatter));
            }


            entity.setUserName(dto.getUserName());
            entity.setUserId(userRepo.getUserIdByName(dto.getUserName()));

            // ✅ Safe null handling for fields
            if (dto.getFields() != null) {
                entity.setIsNetDistributed(dto.getFields().getIs_net_distributed());
            } else {
                entity.setIsNetDistributed(null);
            }

            return entity;

        }).collect(Collectors.toList());


        // ✅ Save all
        List<MosquitoNetEntity> savedEntities = mosquitoNetRepository.saveAll(entityList);


        // ✅ Entity → DTO return
        return savedEntities.stream().map(entity -> {

            MosquitoNetDTO dto = new MosquitoNetDTO();

            dto.setBeneficiaryId(entity.getBeneficiaryId());
            dto.setHouseHoldId(entity.getHouseHoldId());

            // ✅ LocalDate → String (to avoid type mismatch)

            if (entity.getVisitDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                dto.setVisitDate(entity.getVisitDate().format(formatter));
            }
            dto.setUserName(entity.getUserName());

            // ✅ Return is_net_distributed inside dto.fields (if needed)
            if (dto.getFields() != null) {
                dto.getFields().setIs_net_distributed(entity.getIsNetDistributed());
            }
            checkAndAddIncentives(entity);
            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public List<MosquitoNetDTO> getAllMosquitoMobilizationNet(Integer userId) {

        List<MosquitoNetEntity> entityList = mosquitoNetRepository.findByUserId(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return entityList.stream().map(entity -> {
            MosquitoNetDTO dto = new MosquitoNetDTO();
            dto.setId(entity.getId());
            dto.setBeneficiaryId(entity.getBeneficiaryId());
            dto.setHouseHoldId(entity.getHouseHoldId());

            if (entity.getVisitDate() != null) {
                dto.setVisitDate(entity.getVisitDate().format(formatter));
            }

            dto.setUserName(entity.getUserName());
            MosquitoNetListDTO mosquitoNetListDTO = new MosquitoNetListDTO();
            mosquitoNetListDTO.setIs_net_distributed(entity.getIsNetDistributed());
            mosquitoNetListDTO.setVisit_date(entity.getVisitDate().format(formatter));

            dto.setFields(mosquitoNetListDTO);


            return dto;
        }).collect(Collectors.toList());
    }

    private void checkAndAddIncentives(MosquitoNetEntity mosquitoNetEntity) {
        Integer stateId = userService.getUserDetail(mosquitoNetEntity.getUserId()).getStateId();
        IncentiveActivity activityMobilizingMosquitoNets = incentivesRepo.findIncentiveMasterByNameAndGroup("MOSQUITO_NET_DISTRIBUTION_MOBILIZATION", GroupName.ACTIVITY.getDisplayName());
        if(stateId.equals(StateCode.CG.getStateCode())){
            addIncentive(activityMobilizingMosquitoNets, mosquitoNetEntity);

        }

    }

    private void checkAndAddIncentives(ScreeningMalaria diseaseScreening) {
        Integer stateId = userService.getUserDetail(diseaseScreening.getUserId()).getStateId();
        IncentiveActivity diseaseScreeningActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("NVBDCP_MALARIA_TREATMENT", GroupName.UMBRELLA_PROGRAMMES.getDisplayName());
        IncentiveActivity diseaseScreeningActivityCG = incentivesRepo.findIncentiveMasterByNameAndGroup("NVBDCP_MALARIA_TREATMENT", GroupName.ACTIVITY.getDisplayName());
        IncentiveActivity incentiveActivityForCollectSlideAM = incentivesRepo.findIncentiveMasterByNameAndGroup("NVBDCP_SLIDE_COLLECTION", GroupName.UMBRELLA_PROGRAMMES.getDisplayName());
        IncentiveActivity incentiveActivityForCollectSlideCG = incentivesRepo.findIncentiveMasterByNameAndGroup("NVBDCP_SLIDE_COLLECTION", GroupName.ACTIVITY.getDisplayName());

        if (diseaseScreeningActivity != null) {
            if(stateId.equals(StateCode.AM.getStateCode())){
                if (Objects.equals(diseaseScreening.getCaseStatus(), "Confirmed")) {
                    addIncentive(diseaseScreeningActivity, diseaseScreening);

                }
            }
            if(stateId.equals(StateCode.CG.getStateCode())){
                if (Objects.equals(diseaseScreening.getCaseStatus(), "Confirmed")) {
                    addIncentive(diseaseScreeningActivityCG, diseaseScreening);

                }
            }

        }

        if (diseaseScreening.getCaseStatus().equals("Suspected")) {
            if (!diseaseScreening.getMalariaTestType().isEmpty()) {
                if (stateId.equals(StateCode.AM.getStateCode())) {
                    if (incentiveActivityForCollectSlideAM != null) {
                        addIncentive(incentiveActivityForCollectSlideAM, diseaseScreening);

                    }
                }

                if (stateId.equals(StateCode.CG.getStateCode())) {
                    if (incentiveActivityForCollectSlideCG != null) {
                        addIncentive(incentiveActivityForCollectSlideCG, diseaseScreening);

                    }
                }


            }

        }
    }
    private void addIncentive(IncentiveActivity diseaseScreeningActivity, MosquitoNetEntity diseaseScreening) {

        Timestamp visitTimestamp =
                Timestamp.valueOf(diseaseScreening.getVisitDate().atStartOfDay());
        IncentiveActivityRecord record = recordRepo
                .findRecordByActivityIdCreatedDateBenId(
                        diseaseScreeningActivity.getId(),
                        visitTimestamp,
                        diseaseScreening.getBeneficiaryId());

        if (record == null) {

            record = new IncentiveActivityRecord();

            record.setActivityId(diseaseScreeningActivity.getId());
            record.setCreatedDate(visitTimestamp);
            record.setCreatedBy(userService.getUserDetail(diseaseScreening.getUserId()).getUserName());
            record.setStartDate(visitTimestamp);
            record.setEndDate(visitTimestamp);
            record.setUpdatedDate(visitTimestamp);
            record.setUpdatedBy(userService.getUserDetail(diseaseScreening.getUserId()).getUserName());
            record.setBenId(diseaseScreening.getBeneficiaryId().longValue());
            record.setAshaId(diseaseScreening.getUserId());
            record.setAmount(Long.valueOf(diseaseScreeningActivity.getRate()));
            record.setIsEligible(true);

            recordRepo.save(record);
        }

    }


    private void addIncentive(IncentiveActivity diseaseScreeningActivity, ScreeningMalaria diseaseScreening) {
        try {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(diseaseScreeningActivity.getId(), Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()), diseaseScreening.getBenId().longValue());
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(diseaseScreeningActivity.getId());
                record.setCreatedDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                record.setCreatedBy(userService.getUserDetail(diseaseScreening.getUserId()).getUserName());
                record.setStartDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                record.setEndDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                record.setUpdatedDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                record.setUpdatedBy(userService.getUserDetail(diseaseScreening.getUserId()).getUserName());
                record.setUpdatedBy(userService.getUserDetail(diseaseScreening.getUserId()).getUserName());
                record.setBenId(diseaseScreening.getBenId().longValue());
                record.setAshaId(diseaseScreening.getUserId());
                record.setAmount(Long.valueOf(diseaseScreeningActivity.getRate()));
                record.setIsEligible(true);
                recordRepo.save(record);
                logger.info("Incentive for {}"+diseaseScreeningActivity.getDescription());

            }
        }catch (Exception e){
            logger.info("Fail to generate Incentive for {}"+diseaseScreeningActivity.getDescription()+ "Exception"+e.getMessage());

        }

    }


    @Override
    public List<ChronicDiseaseVisitDTO> saveChronicDiseaseVisit(
            List<ChronicDiseaseVisitDTO> requestList, String token) throws IEMRException {
        Integer userId =  jwtUtil.extractUserId(token);
       String  userName= userRepo.getUserNamedByUserId(userId);

        List<ChronicDiseaseVisitDTO> responseList = new ArrayList<>();

        for (ChronicDiseaseVisitDTO dto : requestList) {

            ChronicDiseaseVisitEntity entity = new ChronicDiseaseVisitEntity();

            entity.setBenId(dto.getBenId());
            entity.setHhId(dto.getHhId());
            entity.setFormId(dto.getFormId());
            entity.setVersion(dto.getVersion());
            entity.setVisitNo(dto.getVisitNo());
            entity.setFollowUpNo(dto.getFollowUpNo());
            if (dto.getFollowUpDate() != null) {
                entity.setFollowUpDate(dto.getFollowUpDate());

            }
            entity.setDiagnosisCodes(dto.getDiagnosisCodes());
            entity.setFormDataJson(dto.getFormDataJson());
            entity.setUserID(userId);
            entity.setCreatedBy(userName);
            entity.setUpdatedBy(userId);


            if (dto.getTreatmentStartDate() != null) {
                entity.setTreatmentStartDate(
                        LocalDate.parse(dto.getTreatmentStartDate())
                );
            }

            ChronicDiseaseVisitEntity savedEntity =
                    chronicDiseaseVisitRepository.save(entity);

            dto.setId(savedEntity.getId());
            responseList.add(dto);
            checkIncentive(savedEntity, savedEntity.getUserID());


        }

        return responseList;
    }

    private void checkIncentive(ChronicDiseaseVisitEntity chronicDiseaseVisitEntity, Integer ashaId) {
        String userName = userRepo.getUserNamedByUserId(ashaId);
        Integer stateId = userService.getUserDetail(ashaId).getStateId();
        IncentiveActivity incentiveActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("NCD_FOLLOWUP_TREATMENT", GroupName.NCD.getDisplayName());
        IncentiveActivity incentiveActivityCG = incentivesRepo.findIncentiveMasterByNameAndGroup("NCD_FOLLOWUP_TREATMENT", GroupName.ACTIVITY.getDisplayName());
        logger.info("incentiveActivity:" + incentiveActivity.getId());
        if (incentiveActivity != null) {
            if (chronicDiseaseVisitEntity.getFollowUpNo() != null
                    && chronicDiseaseVisitEntity.getCreatedDate() != null
                    && chronicDiseaseVisitEntity.getDiagnosisCodes() != null) {

                List<String> targetDiseases = Arrays.asList(
                        "Hypertension (BP)",
                        "Diabetes (DM)",
                        "Cancer"
                );

                List<String> diagnosisList = Arrays.asList(
                        chronicDiseaseVisitEntity.getDiagnosisCodes().split(",")
                );

                boolean matchFound = diagnosisList.stream()
                        .map(String::trim)
                        .anyMatch(targetDiseases::contains);

                if (matchFound && Integer.valueOf(6).equals(chronicDiseaseVisitEntity.getFollowUpNo())) {
                    LocalDateTime localDateTime = chronicDiseaseVisitEntity.getFollowUpDate().atStartOfDay();

                    Timestamp followUpTimestamp = Timestamp.valueOf(localDateTime);
                    if(stateId.equals(StateCode.AM.getStateCode())){
                        addNCDFolloupIncentiveRecord(
                                incentiveActivity,
                                ashaId,
                                chronicDiseaseVisitEntity.getBenId(),
                                followUpTimestamp,
                                userName
                        );
                    }
                    if(stateId.equals(StateCode.CG.getStateCode())){
                        addNCDFolloupIncentiveRecord(
                                incentiveActivityCG,
                                ashaId,
                                chronicDiseaseVisitEntity.getBenId(),
                                followUpTimestamp,
                                userName
                        );
                    }

                }
            }
        }


    }

    private void addNCDFolloupIncentiveRecord(IncentiveActivity incentiveActivity, Integer ashaId,
                                              Long benId, Timestamp createdDate, String userName) {
        try {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(incentiveActivity.getId(), createdDate, benId);

            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            if (record == null) {
                record = new IncentiveActivityRecord();
                record.setActivityId(incentiveActivity.getId());
                record.setCreatedDate(createdDate);
                record.setCreatedBy(userName);
                record.setStartDate(createdDate);
                record.setEndDate(createdDate);
                record.setUpdatedDate(now);
                record.setUpdatedBy(userName);
                record.setBenId(benId);
                record.setAshaId(ashaId);
                record.setAmount(Long.valueOf(incentiveActivity.getRate()));
                record.setIsEligible(true);
                recordRepo.save(record);
            }
        } catch (Exception e) {
            logger.error("Fail to save IncentiveActivityRecord " + e.getMessage());
        }


    }

    @Override
    public List<ChronicDiseaseVisitDTO> getCdtfVisits(GetBenRequestHandler getBenRequestHandler) {
        List<ChronicDiseaseVisitDTO> dtoList = new ArrayList<>();

        try {

            List<ChronicDiseaseVisitEntity> entityList = chronicDiseaseVisitRepository.findByUserID(getBenRequestHandler.getAshaId());


            for (ChronicDiseaseVisitEntity entity : entityList) {

                ChronicDiseaseVisitDTO dto = new ChronicDiseaseVisitDTO();

                dto.setId(entity.getId());
                dto.setBenId(entity.getBenId());
                dto.setHhId(entity.getHhId());
                dto.setFormId(entity.getFormId());
                dto.setVersion(entity.getVersion());
                dto.setVisitNo(entity.getVisitNo());
                dto.setFollowUpNo(entity.getFollowUpNo());
                if (entity.getFollowUpDate() != null) {
                    dto.setFollowUpDate(entity.getFollowUpDate());

                }
                dto.setDiagnosisCodes(entity.getDiagnosisCodes());
                dto.setFormDataJson(entity.getFormDataJson());

                if (entity.getTreatmentStartDate() != null) {
                    dto.setTreatmentStartDate(
                            entity.getTreatmentStartDate().toString()
                    );
                }
                checkIncentive(entity, entity.getUserID());
                dtoList.add(dto);
            }
        } catch (Exception e) {

        }

        return dtoList;
    }

}