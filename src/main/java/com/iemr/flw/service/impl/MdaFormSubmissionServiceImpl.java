package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.MdaDistributionData;
import com.iemr.flw.dto.iemr.MdaFormFieldsDTO;
import com.iemr.flw.dto.iemr.MdaFormSubmissionRequest;
import com.iemr.flw.dto.iemr.MdaFormSubmissionResponse;
import com.iemr.flw.repo.iemr.MdaFormSubmissionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MdaFormSubmissionServiceImpl implements MdaFormSubmissionService {

    @Autowired
    private final MdaFormSubmissionRepository repository;

    @Override
    public String saveFormData(List<MdaFormSubmissionRequest> requests) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            List<MdaDistributionData> entities = new ArrayList<>();
            for (MdaFormSubmissionRequest req : requests) {
                LocalDate visitLocalDate = LocalDate.parse(req.getVisitDate(), formatter);
                Timestamp visitDate = Timestamp.valueOf(visitLocalDate.atStartOfDay());

                LocalDate getMdaDistributionDateLocalDate = LocalDate.parse(req.getFields().getMda_distribution_date(), formatter);
                Timestamp getMdaDistributionDate = Timestamp.valueOf(getMdaDistributionDateLocalDate.atStartOfDay());
                MdaDistributionData data = MdaDistributionData.builder()
                        .beneficiaryId(req.getBeneficiaryId())
                        .formId(req.getFormId())
                        .houseHoldId(req.getHouseHoldId())
                        .userName(req.getUserName())
                        .visitDate(visitDate)
                        .mdaDistributionDate(getMdaDistributionDate)
                        .isMedicineDistributed(req.getFields().getIs_medicine_distributed())
                        .createdBy(req.getUserName())
                        .modifiedBy(req.getUserName())
                        .build();
                entities.add(data);
            }
            repository.saveAll(entities);
            return "MDA form data saved successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while saving form data: " + e.getMessage();
        }
    }

    @Override
    public List<MdaFormSubmissionResponse> getFormDataByUserName(String userName) {
        List<MdaDistributionData> records = repository.findByUserName(userName);

        List<MdaFormSubmissionResponse> responses = new ArrayList<>();

        for (MdaDistributionData entity : records) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String mdaformattedDate = sdf.format(entity.getMdaDistributionDate());
                String visitformattedDate = sdf.format(entity.getVisitDate());

                MdaFormFieldsDTO fieldsDTO = MdaFormFieldsDTO.builder()
                        .mda_distribution_date(mdaformattedDate)
                        .is_medicine_distributed(entity.getIsMedicineDistributed())
                        .build();

                responses.add(MdaFormSubmissionResponse.builder()
                        .formId(entity.getFormId())
                        .houseHoldId(entity.getHouseHoldId())
                        .beneficiaryId(entity.getBeneficiaryId())
                        .visitDate(visitformattedDate)
                        .createdBy(entity.getCreatedBy())
                        .createdAt(entity.getCreatedDate())
                        .fields(fieldsDTO)
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return responses;
    }
}
