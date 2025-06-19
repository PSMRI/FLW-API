package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.IRSRound;
import com.iemr.flw.dto.iemr.IRSRoundDTO;
import com.iemr.flw.repo.iemr.IRSRoundRepo;
import com.iemr.flw.service.IRSRoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IRSRoundServiceImpl implements IRSRoundService {
    @Autowired
    private IRSRoundRepo irsRoundRepository;

    @Override
    public List<IRSRound> addRounds(List<IRSRoundDTO> dtos) {
        // Validate input DTOs
        for (IRSRoundDTO dto : dtos) {
            if (dto.getDate() == null || dto.getHouseholdId() == null) {
                throw new IllegalArgumentException("Date and household ID are required");
            }
        }
        return irsRoundRepository.saveAll(
                dtos.stream()
                        .map(dto -> new IRSRound(null, dto.getDate(), dto.getRounds(), dto.getHouseholdId()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<IRSRound> getRounds(Long householdId) {
        return irsRoundRepository.findByHouseholdId(householdId);
    }
}
