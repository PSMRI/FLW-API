package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.*;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.repo.iemr.*;
import com.iemr.flw.service.DiseaseControlService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiseaseControlServiceImpl implements DiseaseControlService {
    @Autowired
    private DiseaseControlRepo diseaseControlRepo;
    @Autowired
    private MalariaControlRepo malariaControlRepo;

    @Autowired
    private KalazarControlRepo kalazarControlRepo;

    @Autowired
    private AesJeControlRepo aesJeControlRepo;

    @Autowired
    private FilariaControlRepo filariaControlRepo;
    @Autowired
    private LeprosyControlRepo leprosyControlRepo;

    @Override
    public String save(DiseaseControlDTO diseaseControlDTO) {
        for (DiseaseControl diseaseControlData : diseaseControlDTO.getDiseaseControlList()) {
            if (diseaseControlRepo.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return update(diseaseControlData);
            } else {

                diseaseControlRepo.save(saveData(diseaseControlData));
                return "Data add successfully";

            }
        }
        return "Fail";


    }

    @Override
    public String saveMalaria(MalariaDTO diseaseControlDTO) {
        for (MalariaData diseaseControlData : diseaseControlDTO.getMalariaLists()) {
            if (malariaControlRepo.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateMalaria(diseaseControlData);
            } else {
                if(diseaseControlDTO.getUserId()!=null){
                    diseaseControlData.setUserID(diseaseControlData.getUserID());
                }
                malariaControlRepo.save(diseaseControlData);
                return "Data add successfully";

            }
        }
        return "Fail";
    }


    @Override
    public String saveKalaAzar(KalaAzarDTO diseaseControlDTO) {
        for (KalaAzarData diseaseControlData : diseaseControlDTO.getKalaAzarLists()) {
            if (kalazarControlRepo.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateKalaAzar(diseaseControlData);
            } else {
                if(diseaseControlDTO.getUserId()!=null){
                    diseaseControlData.setUserID(diseaseControlData.getUserID());
                }
                kalazarControlRepo.save(diseaseControlData);
                return "Data add successfully";

            }
        }
        return "Fail";

    }

    @Override
    public String saveAES(AesJeDTO diseaseControlDTO) {
        for (AesJeData diseaseControlData : diseaseControlDTO.getAesJeLists()) {
            if (aesJeControlRepo.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateAesJe(diseaseControlData);
            } else {
                if(diseaseControlDTO.getUserId()!=null){
                    diseaseControlData.setUserID(diseaseControlData.getUserID());
                }
                aesJeControlRepo.save(diseaseControlData);
                return "Data add successfully";

            }
        }
        return "Fail";
    }

    @Override
    public String saveFilaria(FilariaDTO diseaseControlDTO) {
        for (FilariaData diseaseControlData : diseaseControlDTO.getFilariaLists()) {
            if (filariaControlRepo.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateFilaria(diseaseControlData);
            } else {
                if(diseaseControlDTO.getUserId()!=null){
                    diseaseControlData.setUserID(diseaseControlData.getUserID());
                }
                filariaControlRepo.save(diseaseControlData);
                return "Data add successfully";

            }
        }
        return "Fail";
    }

    @Override
    public String saveLeprosy(LeprosyDTO diseaseControlDTO) {
        for (LeprosyData diseaseControlData : diseaseControlDTO.getLeprosyLists()) {
            if (leprosyControlRepo.findByBenId(diseaseControlData.getBenId()).isPresent()) {
                return updateLeprosy(diseaseControlData);
            } else {
                if(diseaseControlDTO.getUserId()!=null){
                    diseaseControlData.setUserID(diseaseControlData.getUserID());
                }
                leprosyControlRepo.save(diseaseControlData);
                return "Data add successfully";

            }
        }
        return "Fail";
    }

    @Override
    public List<DiseaseControl> getAll(GetDiseaseRequestHandler getDiseaseRequestHandler) {
        return diseaseControlRepo.findAll().stream().filter(diseaseControl -> diseaseControl.getDiseaseTypeId()==getDiseaseRequestHandler.getDiseaseTypeID()).collect(Collectors.toList());
    }

    private DiseaseControl saveData(DiseaseControl diseaseControlDTO) {
        DiseaseControl diseaseControl = new DiseaseControl();
        diseaseControl.setBenId(diseaseControlDTO.getBenId());
        diseaseControl.setCaseDate(diseaseControlDTO.getCaseDate()); // Added
        diseaseControl.setCaseStatus(diseaseControlDTO.getCaseStatus());
        diseaseControl.setSymptoms(diseaseControlDTO.getSymptoms());
        diseaseControl.setMalariaCaseCount(diseaseControlDTO.getMalariaCaseCount());
        diseaseControl.setReferredTo(diseaseControlDTO.getReferredTo()); // Added
        diseaseControl.setOtherReferredTo(diseaseControlDTO.getOtherReferredTo());
        diseaseControl.setMalariaCaseStatusDate(diseaseControlDTO.getMalariaCaseStatusDate()); // Added
        diseaseControl.setRemarks(diseaseControlDTO.getRemarks());
        diseaseControl.setFollowUpPoint(diseaseControlDTO.getFollowUpPoint());
        diseaseControl.setFollowUpDate(diseaseControlDTO.getFollowUpDate());
        diseaseControl.setStatus(diseaseControlDTO.getStatus());
        diseaseControl.setBodyPart(diseaseControlDTO.getBodyPart());
        diseaseControl.setSufferingFromFilariasis(diseaseControlDTO.getSufferingFromFilariasis());
        diseaseControl.setOtherStatus(diseaseControlDTO.getOtherStatus());
        diseaseControl.setHomeVisitDate(diseaseControlDTO.getHomeVisitDate());
        diseaseControl.setLeprosyStatusDate(diseaseControlDTO.getLeprosyStatusDate());
        diseaseControl.setMedicineSideEffect(diseaseControlDTO.getMedicineSideEffect());
        diseaseControl.setDiseaseTypeId(diseaseControlDTO.getDiseaseTypeId());
        return diseaseControl;

    }

    private String updateMalaria(MalariaData updatedMalariaData) {
        return malariaControlRepo.findByBenId(updatedMalariaData.getBenId()).map(malariaData -> {
            malariaData.setId(updatedMalariaData.getId());
            malariaData.setBenId(updatedMalariaData.getBenId());
            malariaData.setIrsDate(updatedMalariaData.getIrsDate());
            malariaData.setFollowUp(updatedMalariaData.getFollowUp());
            malariaData.setRounds(updatedMalariaData.getRounds());
            malariaData.setRemarks(updatedMalariaData.getRemarks());
            malariaData.setMalariaCaseCount(updatedMalariaData.getMalariaCaseCount());
            malariaData.setMalariaCaseStatusDate(updatedMalariaData.getMalariaCaseStatusDate());
            malariaControlRepo.save(malariaData);
            return "Data update successfully";

        }).orElseThrow(() -> new RuntimeException("Data not found"));
    }

    private String updateKalaAzar(KalaAzarData updatedData) {
        return kalazarControlRepo.findByBenId(updatedData.getBenId()).map(kalaAzarData -> {
            kalaAzarData.setId(updatedData.getId());
            kalaAzarData.setBenId(updatedData.getBenId());
            kalaAzarData.setCaseDate(updatedData.getCaseDate());
            kalaAzarData.setCaseStatus(updatedData.getCaseStatus());
            kalaAzarData.setSymptoms(updatedData.getSymptoms());
            kalaAzarData.setMalariaCaseCount(updatedData.getMalariaCaseCount());
            kalaAzarData.setReferredTo(updatedData.getReferredTo());
            kalaAzarData.setKalaAzarCaseStatusDate(updatedData.getKalaAzarCaseStatusDate());
            kalaAzarData.setRemarks(updatedData.getRemarks());
            kalaAzarData.setFollowUpPoint(updatedData.getFollowUpPoint());
            kalaAzarData.setFollowUpDate(updatedData.getFollowUpDate());
            kalaAzarData.setStatus(updatedData.getStatus());
            kalazarControlRepo.save(kalaAzarData);
            return "Data updated successfully";
        }).orElseThrow(() -> new RuntimeException("Data not found"));
    }
    private String updateLeprosy(LeprosyData updateData) {
        return leprosyControlRepo.findByBenId(updateData.getBenId()).map(leprosyData -> {
            leprosyData.setId(updateData.getId());
            leprosyData.setDateOfHomeVisit(updateData.getDateOfHomeVisit());
            leprosyData.setLeprosyStatus(updateData.getLeprosyStatus());
            leprosyData.setReferredTo(updateData.getReferredTo());
            leprosyData.setOther(updateData.getOther());
            leprosyData.setLeprosyStatusDate(updateData.getLeprosyStatusDate());
            leprosyData.setTypeOfLeprosy(updateData.getTypeOfLeprosy());
            leprosyData.setFollowUpDate(updateData.getFollowUpDate());
            leprosyData.setStatus(updateData.getStatus());
            leprosyData.setRemark(updateData.getRemark());
            leprosyData.setDiseaseTypeID(updateData.getDiseaseTypeID());
            leprosyData.setBenId(updateData.getBenId());

            leprosyControlRepo.save(leprosyData);

            return "Data updated successfully";
        }).orElseThrow(() -> new RuntimeException("Data not found"));
    }

    private String updateAesJe(AesJeData updateData) {
        return aesJeControlRepo.findByBenId(updateData.getBenId()).map(aesJeData -> {
            aesJeData.setId(updateData.getId());
            aesJeData.setCaseDate(updateData.getCaseDate());
            aesJeData.setAesjeCaseStatus(updateData.getAesjeCaseStatus());
            aesJeData.setReferredTo(updateData.getReferredTo());
            aesJeData.setDiseaseTypeID(updateData.getDiseaseTypeID());
            aesJeData.setBenId(updateData.getBenId());

            // Save the updated data (if required, depending on your repo)
            aesJeControlRepo.save(aesJeData);

            return "Data updated successfully";
        }).orElseThrow(() -> new RuntimeException("Data not found"));
    }

    private String updateFilaria(FilariaData updateData) {
        return filariaControlRepo.findByBenId(updateData.getBenId()).map(filariaData -> {
            filariaData.setId(updateData.getId());
            filariaData.setSufferingFromFilariasis(updateData.getSufferingFromFilariasis());
            filariaData.setWhichPartOfBody(updateData.getWhichPartOfBody());
            filariaData.setHomeVisitDate(updateData.getHomeVisitDate());
            filariaData.setDecAndAlbendazoleDoseStatus(updateData.getDecAndAlbendazoleDoseStatus());
            filariaData.setMedicineSideEffect(updateData.getMedicineSideEffect());
            filariaData.setOther(updateData.getOther());
            filariaData.setDiseaseTypeID(updateData.getDiseaseTypeID());
            filariaData.setBenId(updateData.getBenId());

            // Save the updated data (if required, depending on your repo)
            filariaControlRepo.save(filariaData);

            return "Data updated successfully";
        }).orElseThrow(() -> new RuntimeException("Data not found"));
    }



    private String update(DiseaseControl diseaseControlDTO) {
        return diseaseControlRepo.findByBenId(diseaseControlDTO.getBenId()).map(diseaseControl -> {
            diseaseControl.setCaseDate(diseaseControlDTO.getCaseDate()); // Added
            diseaseControl.setCaseStatus(diseaseControlDTO.getCaseStatus());
            diseaseControl.setSymptoms(diseaseControlDTO.getSymptoms());
            diseaseControl.setMalariaCaseCount(diseaseControlDTO.getMalariaCaseCount());
            diseaseControl.setReferredTo(diseaseControlDTO.getReferredTo()); // Added
            diseaseControl.setOtherReferredTo(diseaseControlDTO.getOtherReferredTo());
            diseaseControl.setMalariaCaseStatusDate(diseaseControlDTO.getMalariaCaseStatusDate()); // Added
            diseaseControl.setRemarks(diseaseControlDTO.getRemarks());
            diseaseControl.setFollowUpPoint(diseaseControlDTO.getFollowUpPoint());
            diseaseControl.setFollowUpDate(diseaseControlDTO.getFollowUpDate());
            diseaseControl.setFollowUpDate(diseaseControlDTO.getFollowUpDate());
            diseaseControl.setStatus(diseaseControlDTO.getStatus());
            diseaseControl.setBodyPart(diseaseControlDTO.getBodyPart());
            diseaseControl.setSufferingFromFilariasis(diseaseControlDTO.getSufferingFromFilariasis());
            diseaseControl.setOtherStatus(diseaseControlDTO.getOtherStatus());
            diseaseControl.setHomeVisitDate(diseaseControlDTO.getHomeVisitDate());
            diseaseControl.setLeprosyStatusDate(diseaseControlDTO.getLeprosyStatusDate());
            diseaseControl.setMedicineSideEffect(diseaseControlDTO.getMedicineSideEffect());
            diseaseControl.setDiseaseTypeId(diseaseControlDTO.getDiseaseTypeId());
            diseaseControlRepo.save(diseaseControl);

            return "Data update successfully";

        }).orElseThrow(() -> new RuntimeException("Data not found"));

    }
}
