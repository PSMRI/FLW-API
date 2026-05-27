package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.IfaDistribution;
import com.iemr.flw.domain.iemr.SamVisitResponseDTO;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;

import java.util.List;

public interface ChildCareService {

    String registerHBYC(List<HbycRequestDTO> hbycDTOs);

    List<HbycVisitResponseDTO> getHbycRecords(GetBenRequestHandler dto);

    List<HbncVisitResponseDTO> getHBNCDetails(GetBenRequestHandler dto);

    String saveHBNCDetails(List<HbncRequestDTO> hbncRequestDTOs);

    List<ChildVaccinationDTO> getChildVaccinationDetails(GetBenRequestHandler dto);

    String saveChildVaccinationDetails(List<ChildVaccinationDTO> childVaccinationDTOs);

    List<VaccineDTO> getAllChildVaccines(String category);

    String saveSamDetails(List<SamDTO> samRequest);

    List<SAMResponseDTO> getSamVisitsByBeneficiary(GetBenRequestHandler dto);

    String saveOrsDistributionDetails(List<OrsDistributionDTO> orsDistributionDTOS);

    List<OrsDistributionResponseDTO> getOrdDistrubtion(GetBenRequestHandler request);

    List<IfaDistribution> saveAllIfa(List<IfaDistributionDTO> dtoList);

    List<IfaDistributionDTO> getByBeneficiaryId(GetBenRequestHandler request);


}
