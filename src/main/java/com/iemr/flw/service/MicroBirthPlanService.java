package com.iemr.flw.service;

import com.iemr.flw.domain.iemr.MicroBirthPlan;
import com.iemr.flw.dto.iemr.MicroBirthPlanDTO;
import com.iemr.flw.repo.iemr.MicroBirthPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface MicroBirthPlanService {
    public MicroBirthPlan createMicroBirthPlan(MicroBirthPlanDTO birthPlan);
    public List<MicroBirthPlan> getAllMicroBirthPlans(Integer userId);




}