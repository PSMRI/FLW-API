package com.iemr.flw.service.incentiveLogicRespo;


import com.iemr.flw.domain.iemr.ChildVaccination;
import com.iemr.flw.domain.iemr.IncentiveActivityRecord;

public interface IncentiveForImmunizationService {
    // ================== IMMUNIZATION ==================

    IncentiveActivityRecord incentiveForFullImmunization0To1Year(ChildVaccination vaccination, Long benId, Integer userId);

    IncentiveActivityRecord incentiveForFullImmunization1To2Year(ChildVaccination vaccination, Long benId, Integer userId);

    IncentiveActivityRecord incentiveForDpt5Year(ChildVaccination vaccination, Long benId, Integer userId);
}
