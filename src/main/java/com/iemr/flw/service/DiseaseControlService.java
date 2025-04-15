package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.*;

public interface DiseaseControlService {
    public String saveMalaria(MalariaDTO diseaseControlDTO);
    public String saveKalaAzar(KalaAzarDTO diseaseControlDTO);
    public String saveAES(AesJeDTO diseaseControlDTO);
    public String saveFilaria(FilariaDTO diseaseControlDTO);
    public String saveLeprosy(LeprosyDTO diseaseControlDTO);
    public Object getAllMalaria(GetDiseaseRequestHandler getDiseaseRequestHandler);
    public Object getAllKalaAzar(GetDiseaseRequestHandler getDiseaseRequestHandler);
    public Object getAllKalaAES(GetDiseaseRequestHandler getDiseaseRequestHandler);
    public Object getAllFilaria(GetDiseaseRequestHandler getDiseaseRequestHandler);
    public Object getAllLeprosy(GetDiseaseRequestHandler getDiseaseRequestHandler);
}