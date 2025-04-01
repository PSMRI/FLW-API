package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.DiseaseControl;
import com.iemr.flw.dto.iemr.*;
import org.apache.commons.collections4.Put;

import java.math.BigInteger;
import java.util.List;

public interface DiseaseControlService {
    public String save(DiseaseControlDTO diseaseControlDTO);
    public String saveMalaria(MalariaDTO diseaseControlDTO);
    public String saveKalaAzar(KalaAzarDTO diseaseControlDTO);
    public String saveAES(AesJeDTO diseaseControlDTO);
    public String saveFilaria(FilariaDTO diseaseControlDTO);
    public String saveLeprosy(LeprosyDTO diseaseControlDTO);
    public List<DiseaseControl> getAll(GetDiseaseRequestHandler getDiseaseRequestHandler);
}
