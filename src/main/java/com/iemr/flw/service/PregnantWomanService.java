package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.ANCVisitDTO;
import com.iemr.flw.dto.iemr.PNCVisitDTO;
import com.iemr.flw.dto.iemr.PmsmaDTO;
import com.iemr.flw.dto.iemr.PregnantWomanDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PregnantWomanService {

    String registerPregnantWoman(List<PregnantWomanDTO> pregnantWomanDTOs);

    List<PregnantWomanDTO> getPregnantWoman(GetBenRequestHandler dto);

    List<ANCVisitDTO> getANCVisits(GetBenRequestHandler dto);

    String saveANCVisit(List<ANCVisitDTO> ancVisitDTOs);

    List<PmsmaDTO> getPmsmaRecords(GetBenRequestHandler dto);

    String savePmsmaRecords(List<PmsmaDTO> pmsmaDTOs);

    List<PNCVisitDTO> getPNCVisits(GetBenRequestHandler dto);

    String savePNCVisit(List<PNCVisitDTO> pncVisitDTOs);

}
