package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.identity.CbacDetails;
import com.iemr.flw.dto.identity.CbacDTO;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.identity.CbacRepo;
import com.iemr.flw.service.CbacService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CbacServiceImpl implements CbacService {

    @Autowired
    private CbacRepo cbacRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    private final Logger logger = LoggerFactory.getLogger(CbacServiceImpl.class);

    ObjectMapper mapper = new ObjectMapper();

    public List<CbacDTO> getByUserId(GetBenRequestHandler dto) {
        try {
            String user = beneficiaryRepo.getUserName(dto.getAshaId());
            List<CbacDetails> cbacList = cbacRepo.getAllByCreatedBy(user, dto.getFromDate(), dto.getToDate());

            List<CbacDTO> result = new ArrayList<>();
            cbacList.forEach(cbacDetails -> {
                BigInteger benId = beneficiaryRepo.getBenIdFromRegID(cbacDetails.getBeneficiaryRegId());
                CbacDTO cbacDTO = mapper.convertValue(cbacDetails, CbacDTO.class);
                if (benId != null) {
                    cbacDTO.setBeneficiaryId(benId.longValue());
                } else {
                    cbacDTO.setBeneficiaryId(0L);
                }
                result.add(cbacDTO);
            });
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String save(List<CbacDTO> cbacList) {
        List<CbacDetails> cbacDetailsToBeSaved = new ArrayList<>();
        StringBuilder responseMessage = new StringBuilder();
        cbacList.forEach(cbacDTO -> {
            Long benRegId = beneficiaryRepo.getRegIDFromBenId(cbacDTO.getBeneficiaryId());
            CbacDetails cbacDetails = mapper.convertValue(cbacDTO, CbacDetails.class);
            cbacDetails.setBeneficiaryRegId(benRegId);
            CbacDetails existingCbac = cbacRepo.findCbacDetailsByBeneficiaryRegIdAndCreatedDate(
                    benRegId, cbacDetails.getCreatedDate());
            if (existingCbac == null) {
                cbacDetails.setCbacDetailsId(null);
                cbacDetails.setBeneficiaryId(null);
                cbacDetailsToBeSaved.add(cbacDetails);
            } else {
                responseMessage.append("cbac with benRegId: " + cbacDetails.getBeneficiaryRegId() +
                        " and createdDate " + cbacDetails.getCreatedDate() + " already exists.");
                responseMessage.append(System.getProperty("line.separator"));
            }
        });
        if (cbacDetailsToBeSaved.size() > 0) {
            cbacRepo.save(cbacDetailsToBeSaved);
            responseMessage.append(cbacDetailsToBeSaved.size() + " CBAC Details Saved!");
        }
        return responseMessage.toString();
    }
}
