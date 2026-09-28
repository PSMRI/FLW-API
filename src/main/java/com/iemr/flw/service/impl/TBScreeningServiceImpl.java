package com.iemr.flw.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iemr.flw.domain.iemr.TBScreening;
import com.iemr.flw.dto.identity.GetBenRequestHandler;
import com.iemr.flw.dto.iemr.TBScreeningDTO;
import com.iemr.flw.dto.iemr.TBScreeningRequestDTO;
import com.iemr.flw.repo.identity.BeneficiaryRepo;
import com.iemr.flw.repo.iemr.TBScreeningRepo;
import com.iemr.flw.service.TBScreeningService;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TBScreeningServiceImpl implements TBScreeningService {

    private final Logger logger = LoggerFactory.getLogger(TBScreeningServiceImpl.class);
    @Autowired
    private TBScreeningRepo tbScreeningRepo;
    @Autowired
    private BeneficiaryRepo beneficiaryRepo;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public String getByBenId(Long benId, String authorisation) throws Exception {
        return null;
    }

    @Override
    public String save(TBScreeningRequestDTO requestDTO) throws Exception {
        if (requestDTO == null || requestDTO.getTbScreeningList() == null) {
            throw new IllegalArgumentException("TB screening list is required");
        }

        for (TBScreeningDTO dto : requestDTO.getTbScreeningList()) {
            TBScreening tbScreening =
                    tbScreeningRepo.getByUserIdAndBenId(
                            dto.getBenId(), requestDTO.getUserId());

            boolean isNew = tbScreening == null;

            if (isNew) {
                tbScreening = new TBScreening();
            }

            modelMapper.map(dto, tbScreening);

            // List<Integer> -> comma-separated String
            tbScreening.setKeyPopulationRiskFactorIds(
                    toRiskFactorIdsCsv(dto.getKeyPopulationRiskFactorIds()));

            // List<String> -> comma-separated String
            tbScreening.setKeyPopulationRiskFactors(
                    toRiskFactorsCsv(dto.getKeyPopulationRiskFactors()));

            tbScreening.setUserId(requestDTO.getUserId());

            tbScreening.setBenRegID(
                    beneficiaryRepo.getRegIDFromBenId(dto.getBenId()));

            if (isNew) {
                tbScreening.setId(null);

                tbScreening.setCreatedDate(
                        tbScreening.getVisitDate() != null
                                ? tbScreening.getVisitDate()
                                : new Timestamp(System.currentTimeMillis()));

                tbScreening.setCreatedBy(
                        requestDTO.getUserId() != null
                                ? requestDTO.getUserId().toString()
                                : null);
            }

            tbScreeningRepo.save(tbScreening);
        }

        return "no of tb screening items saved: "
                + requestDTO.getTbScreeningList().size();
    }

    @Override
    public String getByUserId(GetBenRequestHandler request) {
        try {
            List<TBScreeningDTO> dtos = new ArrayList<>();

            List<TBScreening> tbScreeningList =
                    tbScreeningRepo.getByUserId(request.getAshaId());

            for (TBScreening tbScreening : tbScreeningList) {
                TBScreeningDTO dto =
                        modelMapper.map(tbScreening, TBScreeningDTO.class);

                // Comma-separated String -> List<Integer>
                dto.setKeyPopulationRiskFactorIds(
                        fromRiskFactorIdsCsv(
                                tbScreening.getKeyPopulationRiskFactorIds()));

                // Comma-separated String -> List<String>
                dto.setKeyPopulationRiskFactors(
                        fromRiskFactorsCsv(
                                tbScreening.getKeyPopulationRiskFactors()));

                dtos.add(dto);
            }

            TBScreeningRequestDTO response = new TBScreeningRequestDTO();
            response.setTbScreeningList(dtos);
            response.setUserId(request.getAshaId());

            Gson gson = new GsonBuilder()
                    .setDateFormat("MMM dd, yyyy h:mm:ss a")
                    .create();

            return gson.toJson(response);

        } catch (Exception e) {
            logger.error("Failed to retrieve TB screening details", e);
            throw new IllegalStateException(
                    "Failed to retrieve TB screening details", e);
        }
    }

    @PostConstruct
    public void configureTBScreeningMappings() {
        TypeMap<TBScreeningDTO, TBScreening> saveMapping =
                modelMapper.getTypeMap(TBScreeningDTO.class, TBScreening.class);

        if (saveMapping == null) {
            saveMapping = modelMapper.createTypeMap(
                    TBScreeningDTO.class, TBScreening.class);
        }

        saveMapping.addMappings(mapper -> {
            mapper.skip(TBScreening::setKeyPopulationRiskFactorIds);
            mapper.skip(TBScreening::setKeyPopulationRiskFactors);

            // Managed by the service; preserve these on updates.
            mapper.skip(TBScreening::setId);
            mapper.skip(TBScreening::setCreatedDate);
            mapper.skip(TBScreening::setCreatedBy);
        });

        TypeMap<TBScreening, TBScreeningDTO> responseMapping =
                modelMapper.getTypeMap(TBScreening.class, TBScreeningDTO.class);

        if (responseMapping == null) {
            responseMapping = modelMapper.createTypeMap(
                    TBScreening.class, TBScreeningDTO.class);
        }

        responseMapping.addMappings(mapper -> {
            mapper.skip(TBScreeningDTO::setKeyPopulationRiskFactorIds);
            mapper.skip(TBScreeningDTO::setKeyPopulationRiskFactors);
        });
    }

    private String toRiskFactorIdsCsv(List<Integer> ids) {
        if (ids == null) {
            return null;
        }

        if (ids.stream().anyMatch(id -> id == null)) {
            throw new IllegalArgumentException(
                    "Risk factor IDs cannot contain null values");
        }

        return ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private String toRiskFactorsCsv(List<String> riskFactors) {
        if (riskFactors == null) {
            return null;
        }

        for (String riskFactor : riskFactors) {
            if (riskFactor == null
                    || riskFactor.isBlank()
                    || riskFactor.contains(",")) {
                throw new IllegalArgumentException(
                        "Risk factor codes cannot be null, blank, or contain commas");
            }
        }

        return riskFactors.stream()
                .map(String::trim)
                .collect(Collectors.joining(","));
    }

    private List<Integer> fromRiskFactorIdsCsv(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    private List<String> fromRiskFactorsCsv(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

}
