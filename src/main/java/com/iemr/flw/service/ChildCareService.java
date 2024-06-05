package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.ChildVaccinationDTO;
import com.iemr.flw.dto.iemr.HbncRequestDTO;
import com.iemr.flw.dto.iemr.HbycDTO;
import com.iemr.flw.dto.iemr.VaccineDTO;

import java.util.List;

public interface ChildCareService {

    String registerHBYC(List<HbycDTO> hbycDTOs);

    List<HbycDTO> getHbycRecords(GetBenRequestHandler dto);

    List<HbncRequestDTO> getHBNCDetails(GetBenRequestHandler dto);

    String saveHBNCDetails(List<HbncRequestDTO> hbncRequestDTOs);

    List<ChildVaccinationDTO> getChildVaccinationDetails(GetBenRequestHandler dto);

    String saveChildVaccinationDetails(List<ChildVaccinationDTO> childVaccinationDTOs);

    List<VaccineDTO> getAllChildVaccines(String category);
}
