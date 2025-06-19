package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.IRSRound;
import com.iemr.flw.dto.iemr.IRSRoundDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IRSRoundService {
     List<IRSRound> addRounds(List<IRSRoundDTO> dtos);

    List<IRSRound> getRounds(Long householdId);
}
