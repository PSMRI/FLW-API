package com.iemr.flw.service;

import com.iemr.flw.dto.ANCVisitDTO;
import com.iemr.flw.dto.PregnantWomanDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PregnantWomanService {

    String registerPregnantWoman(List<PregnantWomanDTO> pregnantWomanDTOs);

    PregnantWomanDTO getPregnantWoman(Long benId);

    List<ANCVisitDTO> getANCVisits(Long pwrId);

    String saveANCVisit(List<ANCVisitDTO> ancVisitDTOs);
}
