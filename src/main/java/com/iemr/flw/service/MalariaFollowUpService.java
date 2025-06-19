package com.iemr.flw.service;

import com.iemr.flw.dto.iemr.MalariaFollowListUpDTO;
import com.iemr.flw.dto.iemr.MalariaFollowUpDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MalariaFollowUpService {


    public Boolean saveFollowUp(MalariaFollowUpDTO dto) ;


    List<MalariaFollowListUpDTO> getByUserId(Integer userId);
}
