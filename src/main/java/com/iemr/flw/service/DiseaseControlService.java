package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.*;

public interface DiseaseControlService {
    public String saveMalaria(MalariaDTO diseaseControlDTO);
    public String saveKalaAzar(KalaAzarDTO kalaAzarDTO);
    public String saveAES(AesJeDTO aesJeDTO);
    public String saveFilaria(FilariaDTO filariaDTO);
    public String saveLeprosy(LeprosyDTO leprosyDTO);
    public Object getAllScreeningData(GetDiseaseRequestHandler getDiseaseRequestHandler);

}