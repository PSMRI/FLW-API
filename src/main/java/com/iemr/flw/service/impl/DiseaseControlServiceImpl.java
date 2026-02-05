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
import com.iemr.flw.controller.CoupleController;
import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.masterEnum.DiseaseType;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.DiseaseControlService;
import com.iemr.flw.service.IncentiveLogicService;
import com.iemr.flw.utils.JwtUtil;
import com.iemr.flw.utils.exception.IEMRException;
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
    public String saveLeprosy(LeprosyDTO diseaseControlDTO, String token) {
        for (DiseaseLeprosyDTO diseaseControlData : diseaseControlDTO.getLeprosyLists()) {
            if (diseaseLeprosyRepository.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateLeprosyData(diseaseControlData);
            } else {
                if (diseaseControlDTO.getUserId() != null) {
                    diseaseControlData.setUserId(diseaseControlDTO.getUserId());
                }
                ScreeningLeprosy screeningLeprosy = diseaseLeprosyRepository.save(saveLeprosyData(diseaseControlData));
                if (screeningLeprosy != null) {
                    if (screeningLeprosy.getIsConfirmed()) {
                        if (screeningLeprosy.getTypeOfLeprosy().toLowerCase().equals("PB (Paucibacillary)")) {
                            incentiveLogicService.incentiveForLeprosyConfirmed(screeningLeprosy.getBenId(), screeningLeprosy.getTreatmentStartDate(), screeningLeprosy.getTreatmentEndDate(), token);
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
        });

        return dtos;
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
                .filter(disease -> Objects.equals(disease.getUserId(), getDiseaseRequestHandler.getUserId()))
                .collect(Collectors.toList());

        // Check if the list is empty
        if (filteredList.isEmpty()) {
            return Collections.singletonMap("message", "No data found for Malaria.");
        }

        // Map to DTOs
        List<DiseaseMalariaDTO> dtoList = filteredList.stream().map(disease -> {
            DiseaseMalariaDTO dto = new DiseaseMalariaDTO();

            // Map fields from DiseaseMalaria to DTO
            dto.setId(disease.getId());
            dto.setBenId(disease.getBenId());
            dto.setHouseHoldDetailsId(disease.getHouseHoldDetailsId());
            dto.setScreeningDate(disease.getScreeningDate());
            dto.setBeneficiaryStatus(disease.getBeneficiaryStatus());
            dto.setDateOfDeath(disease.getDateOfDeath());
            dto.setPlaceOfDeath(disease.getPlaceOfDeath());
            dto.setOtherPlaceOfDeath(disease.getOtherPlaceOfDeath());
            dto.setReasonForDeath(disease.getReasonForDeath());
            dto.setOtherReasonForDeath(disease.getOtherReasonForDeath());
            dto.setCaseStatus(disease.getCaseStatus());
            dto.setRapidDiagnosticTest(disease.getRapidDiagnosticTest());
            dto.setDateOfRdt(disease.getDateOfRdt());
            dto.setSlideTestPf(disease.getSlideTestPf());
            dto.setSlideTestPv(disease.getSlideTestPv());
            dto.setDateOfSlideTest(disease.getDateOfSlideTest());
            dto.setSlideNo(disease.getSlideNo());
            dto.setReferredTo(disease.getReferredTo());
            dto.setOtherReferredFacility(disease.getOtherReferredFacility());
            dto.setRemarks(disease.getRemarks());
            dto.setDateOfVisitBySupervisor(disease.getDateOfVisitBySupervisor());
            dto.setUserId(disease.getUserId());
            dto.setDiseaseTypeId(disease.getDiseaseTypeId());

            // Parse symptoms (if present)
            try {
                if (disease.getSymptoms() != null && !disease.getSymptoms().isEmpty()) {
                    MalariaSymptomsDTO symptomsDTO = objectMapper.readValue(disease.getSymptoms(), MalariaSymptomsDTO.class);
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
                throw new RuntimeException("Error parsing symptoms JSON for Malaria Disease ID: " + disease.getId(), e);
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
                .filter(disease -> (Objects.equals(disease.getUserId(), getDiseaseRequestHandler.getUserId())))
                .collect(Collectors.toList());

        // Check if the list is empty
        if (filteredList.isEmpty()) {
            return Collections.singletonMap("message", "No data found for Kala Azar.");
        }

        // Map to DTOs
        List<DiseaseKalaAzarDTO> dtoList = filteredList.stream().map(disease -> {
            DiseaseKalaAzarDTO dto = new DiseaseKalaAzarDTO();
            dto.setId(disease.getId());
            dto.setBenId(disease.getBenId());
            dto.setHouseHoldDetailsId(disease.getHouseHoldDetailsId());
            dto.setVisitDate(disease.getVisitDate());
            dto.setBeneficiaryStatus(disease.getBeneficiaryStatus());
            dto.setDateOfDeath(disease.getDateOfDeath());
            dto.setPlaceOfDeath(disease.getPlaceOfDeath());
            dto.setOtherPlaceOfDeath(disease.getOtherPlaceOfDeath());
            dto.setReasonForDeath(disease.getReasonForDeath());
            dto.setOtherReasonForDeath(disease.getOtherReasonForDeath());
            dto.setKalaAzarCaseStatus(disease.getKalaAzarCaseStatus());
            dto.setKalaAzarCaseCount(disease.getKalaAzarCaseCount());
            dto.setRapidDiagnosticTest(disease.getRapidDiagnosticTest());
            dto.setDateOfRdt(disease.getDateOfRdt());
            dto.setFollowUpPoint(disease.getFollowUpPoint());
            dto.setReferredTo(disease.getReferredTo());
            dto.setOtherReferredFacility(disease.getOtherReferredFacility());
            dto.setCreatedDate(disease.getCreatedDate());
            dto.setCreatedBy(disease.getCreatedBy());
            dto.setBeneficiaryStatusId(disease.getBeneficiaryStatusId());
            dto.setReferToName(disease.getReferToName());
            dto.setUserId(disease.getUserId());
            dto.setDiseaseTypeId(disease.getDiseaseTypeId());

            return dto;
        }).collect(Collectors.toList());

        return dtoList;
    }


    public Object getAllKalaAES(GetDiseaseRequestHandler getDiseaseRequestHandler) {
        if (diseaseAESJERepository.findAll().isEmpty()) {
            return Collections.singletonMap("message", "No data found for AES.");
        }

        return diseaseAESJERepository.findAll().stream().filter(diseaseAesje -> Objects.equals(diseaseAesje.getUserId(), getDiseaseRequestHandler.getUserId())).collect(Collectors.toList());
    }


    public Object getAllFilaria(GetDiseaseRequestHandler getDiseaseRequestHandler) {

        // Fetch and filter Filaria disease records
        List<ScreeningFilariasis> filteredList = diseaseFilariasisRepository.findAll().stream().filter(screeningFilariasis -> Objects.equals(screeningFilariasis.getUserId(), getDiseaseRequestHandler.getUserId())).collect(Collectors.toList());

        // Check if the list is empty
        if (filteredList.isEmpty()) {
            return Collections.singletonMap("message", "No data found for Filaria.");
        }

        // Map to DTOs
        List<DiseaseFilariasisDTO> dtoList = filteredList.stream().map(disease -> {
            DiseaseFilariasisDTO dto = new DiseaseFilariasisDTO();
            dto.setId(disease.getId());
            dto.setBenId(disease.getBenId());
            dto.setHouseHoldDetailsId(disease.getHouseHoldDetailsId());
            dto.setSufferingFromFilariasis(disease.getSufferingFromFilariasis());
            dto.setAffectedBodyPart(disease.getAffectedBodyPart());
            dto.setMdaHomeVisitDate(disease.getMdaHomeVisitDate());
            dto.setDoseStatus(disease.getDoseStatus());
            dto.setFilariasisCaseCount(disease.getFilariasisCaseCount());
            dto.setOtherDoseStatusDetails(disease.getOtherDoseStatusDetails());
            dto.setMedicineSideEffect(disease.getMedicineSideEffect());
            dto.setOtherSideEffectDetails(disease.getOtherSideEffectDetails());
            dto.setCreatedDate(disease.getCreatedDate());
            dto.setCreatedBy(disease.getCreatedBy());
            dto.setUserId(disease.getUserId());

            return dto;
        }).collect(Collectors.toList());

        return dtoList;
    }


    public Object getAllLeprosy(GetDiseaseRequestHandler getDiseaseRequestHandler) {

        // Fetch and filter Leprosy disease records
        List<ScreeningLeprosy> filteredList = diseaseLeprosyRepository.findAll().stream()
                .filter(disease -> Objects.equals(disease.getUserId(), getDiseaseRequestHandler.getUserId()))
                .collect(Collectors.toList());

        // Check if the list is empty
        if (filteredList.isEmpty()) {
            return Collections.singletonMap("message", "No data found for Leprosy.");
        }

        // Map to DTOs
        List<DiseaseLeprosyDTO> dtoList = filteredList.stream().map(disease -> {
            DiseaseLeprosyDTO dto = new DiseaseLeprosyDTO();
            dto.setId(disease.getId());
            dto.setBenId(disease.getBenId());
            dto.setHouseHoldDetailsId(disease.getHouseHoldDetailsId());
            dto.setHomeVisitDate(disease.getHomeVisitDate());
            dto.setLeprosyStatus(disease.getLeprosyStatus());
            dto.setReferredTo(disease.getReferredTo());
            dto.setOtherReferredTo(disease.getOtherReferredTo());
            dto.setLeprosyStatusDate(disease.getLeprosyStatusDate());
            dto.setTypeOfLeprosy(disease.getTypeOfLeprosy());
            dto.setFollowUpDate(disease.getFollowUpDate());
            dto.setBeneficiaryStatus(disease.getLeprosyStatus());
            dto.setRemark(disease.getRemark());
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
        diseaseLeprosy.setTypeOfLeprosy(diseaseControlData.getTypeOfLeprosy());
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

            entity.setBeneficiaryId(dto.getBeneficiaryId());
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


    private void checkAndAddIncentives(ScreeningMalaria diseaseScreening) {
        IncentiveActivity diseaseScreeningActivity;
        if (Objects.equals(diseaseScreening.getCaseStatus(), "Confirmed Case")) {
            diseaseScreeningActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("MALARIA_1", "DISEASECONTROL");

        } else {
            diseaseScreeningActivity = incentivesRepo.findIncentiveMasterByNameAndGroup("MALARIA_2", "DISEASECONTROL");

        }


        if (diseaseScreeningActivity != null) {
            IncentiveActivityRecord record = recordRepo
                    .findRecordByActivityIdCreatedDateBenId(diseaseScreeningActivity.getId(), Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()), diseaseScreening.getBenId().longValue());
            if (record == null) {
                if (Objects.equals(diseaseScreening.getCaseStatus(), "Confirmed Case")) {
                    record = new IncentiveActivityRecord();
                    record.setActivityId(diseaseScreeningActivity.getId());
                    record.setCreatedDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                    record.setCreatedBy(diseaseScreening.getCreatedBy());
                    record.setStartDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                    record.setEndDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                    record.setUpdatedDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                    record.setUpdatedBy(diseaseScreening.getCreatedBy());
                    record.setBenId(diseaseScreening.getBenId().longValue());
                    record.setAshaId(diseaseScreening.getUserId());
                    record.setName(diseaseScreeningActivity.getName());
                    record.setAmount(Long.valueOf(diseaseScreeningActivity.getRate()));
                    recordRepo.save(record);
                } else {
                    record = new IncentiveActivityRecord();
                    record.setActivityId(diseaseScreeningActivity.getId());
                    record.setCreatedDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                    record.setCreatedBy(diseaseScreening.getCreatedBy());
                    record.setStartDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                    record.setEndDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                    record.setUpdatedDate(Timestamp.valueOf(diseaseScreening.getCreatedDate().toString()));
                    record.setUpdatedBy(diseaseScreening.getCreatedBy());
                    record.setBenId(diseaseScreening.getBenId().longValue());
                    record.setName(diseaseScreeningActivity.getName());
                    record.setAshaId(diseaseScreening.getUserId());
                    record.setAmount(Long.valueOf(diseaseScreeningActivity.getRate()));
                    recordRepo.save(record);
                }

            }
        }
    }


    @Override
    public List<ChronicDiseaseVisitDTO> saveChronicDiseaseVisit(
            List<ChronicDiseaseVisitDTO> requestList, String token) throws IEMRException {

        List<ChronicDiseaseVisitDTO> responseList = new ArrayList<>();

        for (ChronicDiseaseVisitDTO dto : requestList) {

            ChronicDiseaseVisitEntity entity = new ChronicDiseaseVisitEntity();

            entity.setBenId(dto.getBenId());
            entity.setHhId(dto.getHhId());
            entity.setFormId(dto.getFormId());
            entity.setVersion(dto.getVersion());
            entity.setVisitNo(dto.getVisitNo());
            entity.setFollowUpNo(dto.getFollowUpNo());
            entity.setFollowUpDate(dto.getFollowUpDate());
            entity.setDiagnosisCodes(dto.getDiagnosisCodes());
            entity.setFormDataJson(dto.getFormDataJson());
            entity.setUserID(jwtUtil.extractUserId(token));
            entity.setCreatedBy(jwtUtil.extractUsername(token));
            entity.setUpdatedBy(jwtUtil.extractUserId(token));


            if (dto.getTreatmentStartDate() != null) {
                entity.setTreatmentStartDate(
                        LocalDate.parse(dto.getTreatmentStartDate())
                );
            }

            ChronicDiseaseVisitEntity savedEntity =
                    chronicDiseaseVisitRepository.save(entity);

            dto.setId(savedEntity.getId());
            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public List<ChronicDiseaseVisitDTO> getCdtfVisits(GetBenRequestHandler getBenRequestHandler) {

        List<ChronicDiseaseVisitEntity> entityList = chronicDiseaseVisitRepository.findByUserID(getBenRequestHandler.getAshaId());

        List<ChronicDiseaseVisitDTO> dtoList = new ArrayList<>();

        for (ChronicDiseaseVisitEntity entity : entityList) {

            ChronicDiseaseVisitDTO dto = new ChronicDiseaseVisitDTO();

            dto.setId(entity.getId());
            dto.setBenId(entity.getBenId());
            dto.setHhId(entity.getHhId());
            dto.setFormId(entity.getFormId());
            dto.setVersion(entity.getVersion());
            dto.setVisitNo(entity.getVisitNo());
            dto.setFollowUpNo(entity.getFollowUpNo());
            dto.setFollowUpDate(entity.getFollowUpDate());
            dto.setDiagnosisCodes(entity.getDiagnosisCodes());
            dto.setFormDataJson(entity.getFormDataJson());

            if (entity.getTreatmentStartDate() != null) {
                dto.setTreatmentStartDate(
                        entity.getTreatmentStartDate().toString()
                );
            }

            dtoList.add(dto);
        }

        return dtoList;
    }

}