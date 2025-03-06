package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.DiseaseControl;
import com.iemr.flw.dto.iemr.DiseaseControlDTO;
import com.iemr.flw.repo.iemr.DiseaseControlRepo;
import com.iemr.flw.service.DiseaseControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiseaseControlServiceImpl implements DiseaseControlService {
    @Autowired
    private DiseaseControlRepo diseaseControlRepo;

    @Override
    public String save(List<DiseaseControlDTO> diseaseControlDTO) {
        for(DiseaseControlDTO diseaseControlData: diseaseControlDTO){
            if(diseaseControlRepo.findByBenId(diseaseControlData.getBenId()).isPresent()){
                return   update(diseaseControlData);
            }else {
                diseaseControlRepo.save(saveData(diseaseControlData));
                return   "Data add successfully";

            }
        }
        return "Fail";


    }

    private DiseaseControl  saveData(DiseaseControlDTO diseaseControlDTO){
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
        return diseaseControl;

    }
    private String  update(DiseaseControlDTO diseaseControlDTO){
        return  diseaseControlRepo.findByBenId(diseaseControlDTO.getBenId()).map(diseaseControl -> {
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
            return "Data update successfully";

        }).orElseThrow(()->new RuntimeException("Data not found"));

    }
}
