package com.iemr.flw.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iemr.flw.domain.identity.CbacDetails;
import com.iemr.flw.dto.identity.CbacDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.identity.CbacRepo;
import com.iemr.flw.service.CbacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CbacServiceImpl implements CbacService {

    @Autowired
    private CbacRepo cbacRepo;

    @Autowired
    private BeneficiaryRepo beneficiaryRepo;

    ObjectMapper mapper = new ObjectMapper();

    public List<CbacDTO> getByUserId(Integer userId) {
        String user = beneficiaryRepo.getUserName(userId);
        List<CbacDetails> cbacList = cbacRepo.getAllByCreatedBy(user);

        List<CbacDTO> result = new ArrayList<>();
        cbacList.forEach(cbacDetails -> {
            CbacDTO cbacDTO = mapper.convertValue(cbacDetails, CbacDTO.class);
            result.add(cbacDTO);
        });
        return result;
    }

    public String save(List<CbacDTO> cbacList, String user) {
        List<CbacDetails> cbacDetailsToBeSaved = new ArrayList<>();
        StringBuilder responseMessage = new StringBuilder();
        cbacList.forEach(cbacDTO -> {
            CbacDetails cbacDetails= mapper.convertValue(cbacDTO, CbacDetails.class);
            CbacDetails existingCbac = cbacRepo.findCbacDetailsByBeneficiaryRegIdAndCreatedDate(
                    cbacDetails.getBeneficiaryRegId(), cbacDetails.getCreatedDate());
            if(existingCbac == null) {
                if(user != null && user != "") {
                    cbacDetails.setCreatedBy(user);
                }
                cbacDetailsToBeSaved.add(cbacDetails);
            } else {
                responseMessage.append("cbac with benRegId: " + cbacDetails.getBeneficiaryRegId() +
                        " and createdDate " + cbacDetails.getCreatedDate() + " already exists \n");
            }
        });
        if (cbacDetailsToBeSaved.size() > 0) {
            cbacRepo.save(cbacDetailsToBeSaved);
            responseMessage.append(cbacDetailsToBeSaved.size() + " CBAC Details Saved!");
        }
        return responseMessage.toString();
    }
}
