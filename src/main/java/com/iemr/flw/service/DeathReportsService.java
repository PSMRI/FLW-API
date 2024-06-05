package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.CdrDTO;
import com.iemr.flw.dto.iemr.MdsrDTO;

import java.util.List;

public interface DeathReportsService {

    String registerCDR(List<CdrDTO> cdrDTOS);

    List<CdrDTO> getCdrRecords(GetBenRequestHandler dto);

    String registerMDSR(List<MdsrDTO> mdsrDTOS);

    List<MdsrDTO> getMdsrRecords(GetBenRequestHandler requestDto);
}
