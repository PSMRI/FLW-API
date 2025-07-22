package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.HbncVisit;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;

import java.util.List;

public interface ChildCareService {

    String registerHBYC(List<HbycDTO> hbycDTOs);

    List<HbycDTO> getHbycRecords(GetBenRequestHandler dto);

    List<HbncVisitResponseDTO> getHBNCDetails(GetBenRequestHandler dto);

    String saveHBNCDetails(List<HbncRequestDTO> hbncRequestDTOs);

    List<ChildVaccinationDTO> getChildVaccinationDetails(GetBenRequestHandler dto);

    String saveChildVaccinationDetails(List<ChildVaccinationDTO> childVaccinationDTOs);

    List<VaccineDTO> getAllChildVaccines(String category);
}
