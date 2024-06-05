package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MaternalHealthService {

    String registerPregnantWoman(List<PregnantWomanDTO> pregnantWomanDTOs);

    List<PregnantWomanDTO> getPregnantWoman(GetBenRequestHandler dto);

    List<ANCVisitDTO> getANCVisits(GetBenRequestHandler dto);

    String saveANCVisit(List<ANCVisitDTO> ancVisitDTOs);

    List<PmsmaDTO> getPmsmaRecords(GetBenRequestHandler dto);

    String savePmsmaRecords(List<PmsmaDTO> pmsmaDTOs);

    List<PNCVisitDTO> getPNCVisits(GetBenRequestHandler dto);

    String savePNCVisit(List<PNCVisitDTO> pncVisitDTOs);

}
