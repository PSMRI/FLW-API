package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iemr.flw.domain.identity.CbacAdditionalDetails;
import com.iemr.flw.domain.identity.CbacDetails;
import com.iemr.flw.dto.identity.CbacDTO;
import com.iemr.flw.dto.identity.CbacStatus;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.identity.CbacAdditionalDetailRepo;
import com.iemr.flw.repo.identity.CbacRepo;
import com.iemr.flw.service.CbacService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CbacServiceImpl implements CbacService {

    private final Logger logger = LoggerFactory.getLogger(CbacServiceImpl.class);
    ObjectMapper mapper = new ObjectMapper();
    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private CbacRepo cbacRepo;
    @Autowired
    private CbacAdditionalDetailRepo cbacAddRepo;
    @Autowired
    private BeneficiaryRepo beneficiaryRepo;
    @Value("10")
    private String cbac_page_size;

    public String getByUserId(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            int pageSize = Integer.parseInt(cbac_page_size);
            int totalPage;
            PageRequest pageRequest = new PageRequest(dto.getPageNo(), pageSize);
            Page<CbacDetails> cbacList = cbacRepo.getAllByCreatedBy(user, dto.getFromDate(), dto.getToDate(), pageRequest);
            totalPage = cbacList.getTotalPages();

            List<CbacDTO> result = new ArrayList<>();
            cbacList.forEach(cbacDetails -> {
                BigInteger benId = beneficiaryRepo.getBenIdFromRegID(cbacDetails.getBeneficiaryRegId());
                CbacAdditionalDetails cbacAdditionalDetails = cbacAddRepo.findCbacAdditionalDetail(cbacDetails.getCbacDetailsId());
                CbacDTO cbacDTO = mapper.convertValue(cbacDetails, CbacDTO.class);
                if (benId != null) {
                    cbacDTO.setBeneficiaryId(benId.longValue());
                } else {
                    cbacDTO.setBeneficiaryId(0L);
                }
                if (cbacAdditionalDetails != null) {
                    cbacDTO.setCbacCloudy(cbacAdditionalDetails.getCbacCloudy());
                    cbacDTO.setCbacCloudyPosi(cbacAdditionalDetails.getCbacCloudyPosi());
                    cbacDTO.setCbacDiffreading(cbacAdditionalDetails.getCbacDiffreading());
                    cbacDTO.setCbacDiffreadingPosi(cbacAdditionalDetails.getCbacDiffreadingPosi());
                    cbacDTO.setCbacPainIneyes(cbacAdditionalDetails.getCbacPainIneyes());
                    cbacDTO.setCbacPainIneyesPosi(cbacAdditionalDetails.getCbacPainIneyesPosi());
                    cbacDTO.setCbacRednessIneyes(cbacAdditionalDetails.getCbacRednessIneyes());
                    cbacDTO.setCbacRednessIneyesPosi(cbacAdditionalDetails.getCbacRednessIneyesPosi());
                    cbacDTO.setCbacDiffInhearing(cbacAdditionalDetails.getCbacDiffInhearing());
                    cbacDTO.setCbacDiffInhearingPosi(cbacAdditionalDetails.getCbacDiffInhearingPosi());
                    cbacDTO.setCbacFeelingUnsteady(cbacAdditionalDetails.getCbacFeelingUnsteady());
                    cbacDTO.setCbacFeelingUnsteadyPosi(cbacAdditionalDetails.getCbacFeelingUnsteadyPosi());
                    cbacDTO.setCbacSufferPhysicalDisability(cbacAdditionalDetails.getCbacSufferPhysicalDisability());
                    cbacDTO.setCbacSufferPhysicalDisabilityPosi(cbacAdditionalDetails.getCbacSufferPhysicalDisabilityPosi());
                    cbacDTO.setCbacNeedingHelp(cbacAdditionalDetails.getCbacNeedingHelp());
                    cbacDTO.setCbacNeedingHelpPosi(cbacAdditionalDetails.getCbacNeedingHelpPosi());
                    cbacDTO.setCbacForgettingNames(cbacAdditionalDetails.getCbacForgettingNames());
                    cbacDTO.setCbacForgettingNamesPosi(cbacAdditionalDetails.getCbacForgettingNamesPosi());
                    cbacDTO.setCbacTinglingPalm(cbacAdditionalDetails.getCbacTinglingPalm());
                    cbacDTO.setCbacTinglingPalmPosi(cbacAdditionalDetails.getCbacTinglingPalmPosi());
                    cbacDTO.setCbacWhiteOrRedPatch(cbacAdditionalDetails.getCbacWhiteOrRedPatch());
                    cbacDTO.setCbacWhiteOrRedPatchPosi(cbacAdditionalDetails.getCbacWhiteOrRedPatchPosi());
                }
                result.add(cbacDTO);
            });
            Map<String, Object> response = new HashMap<>();
            response.put("data", result);
            response.put("pageSize", Integer.parseInt(cbac_page_size));
            response.put("totalPage", totalPage);
            return new Gson().toJson(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String save(List<CbacDTO> cbacList) {
        List<CbacAdditionalDetails> cbacAdditionalDetailsList = new ArrayList<>();
        List<CbacStatus> result = new ArrayList<>();
        cbacList.forEach(cbacDTO -> {
            Long benRegId = beneficiaryRepo.getRegIDFromBenId(cbacDTO.getBeneficiaryId());
            CbacDetails cbacDetails = modelMapper.map(cbacDTO, CbacDetails.class);
            cbacDetails.setBeneficiaryRegId(benRegId);

            CbacDetails existingCbac = cbacRepo.findCbacDetailsByBeneficiaryRegIdAndCreatedDate(
                    benRegId, cbacDetails.getCreatedDate());
            if (existingCbac == null) {
                cbacDetails.setCbacDetailsId(null);
                cbacDetails.setBeneficiaryId(null);
                CbacDetails savedCbac = cbacRepo.save(cbacDetails);
                result.add(new CbacStatus(cbacDTO.getBeneficiaryId(), cbacDTO.getCreatedDate(), "Success"));
                CbacAdditionalDetails cbacAdditionalDetails = modelMapper.map(cbacDTO, CbacAdditionalDetails.class);
                cbacAdditionalDetails.setCbacDetailsId(savedCbac.getCbacDetailsId());
                cbacAdditionalDetailsList.add(cbacAdditionalDetails);

            } else {
//                responseMessage.append("cbac with benRegId: " + cbacDTO.getBeneficiaryId() +
//                        " and createdDate " + cbacDetails.getCreatedDate() + " already exists.");
//                responseMessage.append(System.getProperty("line.separator"));
                result.add(new CbacStatus(cbacDTO.getBeneficiaryId(), cbacDTO.getCreatedDate(), "Fail"));
            }
        });
        if (cbacAdditionalDetailsList.size() > 0) {
            cbacAddRepo.save(cbacAdditionalDetailsList);
//            responseMessage.append(cbacAdditionalDetailsList.size() + " CBAC Details Saved!");
        }
//        return responseMessage.toString();
        return new Gson().toJson(result);
    }
}
