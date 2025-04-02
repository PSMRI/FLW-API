package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.IRSRound;
import com.iemr.flw.dto.iemr.IRSRoundDTO;
import com.iemr.flw.repo.iemr.IRSRoundRepo;
import com.iemr.flw.service.IRSRoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class IRSRoundServiceImpl implements IRSRoundService {
    @Autowired
    private IRSRoundRepo irsRoundRepository;
    @Override
    public List<IRSRound> addRounds(List<IRSRoundDTO> dtos) {
        List<IRSRound> rounds = new ArrayList<>();
        for (IRSRoundDTO dto : dtos) {

            IRSRound round = new IRSRound(null, dto.getDate(), dto.getRounds(), dto.getHouseholdId());
            rounds.add(round);
        }
        return irsRoundRepository.saveAll(rounds);
    }

    @Override
    public List<IRSRound> getRounds(Long householdId) {
        return irsRoundRepository.findByHouseholdId(householdId);
    }
}
