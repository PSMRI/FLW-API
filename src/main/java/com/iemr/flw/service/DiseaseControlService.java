/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*
/*
* AMRIT – Accessible Medical Records via Integrated Technology
*/
package com.iemr.flw.service;

import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.*;
import com.iemr.flw.utils.exception.IEMRException;

import java.util.List;

public interface DiseaseControlService {
    public String saveMalaria(MalariaDTO diseaseControlDTO);
    public String saveKalaAzar(KalaAzarDTO kalaAzarDTO);
    public String saveAES(AesJeDTO aesJeDTO);
    public String saveFilaria(FilariaDTO filariaDTO);
    public String saveLeprosy(LeprosyDTO leprosyDTO);
    public Object getAllScreeningData(GetDiseaseRequestHandler getDiseaseRequestHandler);

    public List<MosquitoNetDTO> saveMosquitoMobilizationNet(List<MosquitoNetDTO> mosquitoNetDTOList);

    List<MosquitoNetDTO> getAllMosquitoMobilizationNet(Integer userId);
    List<DiseaseGetLeprosyDTO> getAllLeprosyData(String createdBy);

     public String saveLeprosyFollowUp(LeprosyFollowUpDTO leprosyDTO);
     List<LeprosyGetFollowUpDTO> getAllLeprosyFollowUpData(String createdBy);

    List<ChronicDiseaseVisitDTO> saveChronicDiseaseVisit(List<ChronicDiseaseVisitDTO> requestList,String token) throws IEMRException;

    List<ChronicDiseaseVisitDTO> getCdtfVisits(GetBenRequestHandler getBenRequestHandler);
}