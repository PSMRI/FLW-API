package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.DiseaseControl;
import com.iemr.flw.dto.iemr.DiseaseControlDTO;
import org.apache.commons.collections4.Put;

import java.util.List;

public interface DiseaseControlService {
    public String save(List<DiseaseControlDTO> diseaseControlDTO);
    public List<DiseaseControl> getAll();
}
