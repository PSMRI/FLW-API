package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.DiseaseControl;
import com.iemr.flw.dto.iemr.DiseaseControlDTO;
import org.apache.commons.collections4.Put;

public interface DiseaseControlService {
    public DiseaseControl save(DiseaseControlDTO diseaseControlDTO);
}
