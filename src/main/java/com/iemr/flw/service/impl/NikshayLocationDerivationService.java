
package com.iemr.flw.service.impl;

import com.iemr.flw.domain.iemr.BenFlowStatus;
import com.iemr.flw.domain.iemr.NikshayVillageFacilityMapping;
import com.iemr.flw.repo.iemr.BenFlowStatusRepo;
import com.iemr.flw.repo.iemr.NikshayVillageFacilityMappingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Stop TB / Nikshay — auto-derives a beneficiary's Nikshay TU/Facility/Village
 * from their existing village (already known from registration), so a field
 * worker never needs to select this themselves when saving a case. Called
 * once, at the moment a TB Suspected/Diagnostics case is saved, so the result
 * is a snapshot of what was true then — not re-derived later if the
 * beneficiary's own village record changes afterwards.
 */
@Service
public class NikshayLocationDerivationService {

    @Autowired
    private BenFlowStatusRepo benFlowStatusRepo;

    @Autowired
    private NikshayVillageFacilityMappingRepo nikshayVillageFacilityMappingRepo;

    public static class NikshayLocationScope {
        public Integer villageID;
        public Integer facilityID;
        public Integer tuID;
    }

    /**
     * Returns null if the beneficiary has no known village, or their village
     * has no confirmed match in the Nikshay location master yet (both are
     * flagged gaps, not blocking — the case still saves, just without a
     * Nikshay tag until the gap is resolved).
     */
    public NikshayLocationScope deriveForBeneficiary(Long beneficiaryRegID) {
        if (beneficiaryRegID == null) {
            return null;
        }
        List<BenFlowStatus> flows = benFlowStatusRepo.findByBeneficiaryRegID(beneficiaryRegID);
        if (flows == null || flows.isEmpty()) {
            return null;
        }
        Integer villageID = flows.get(0).getVillageID();
        if (villageID == null) {
            return null;
        }
        List<NikshayVillageFacilityMapping> mappings = nikshayVillageFacilityMappingRepo.findByAmritVillageID(villageID);
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }
        NikshayVillageFacilityMapping mapping = mappings.get(0);
        NikshayLocationScope scope = new NikshayLocationScope();
        scope.villageID = villageID;
        scope.facilityID = mapping.getNikshayFacilityID();
        scope.tuID = mapping.getNikshayTUID();
        return scope;
    }
}
