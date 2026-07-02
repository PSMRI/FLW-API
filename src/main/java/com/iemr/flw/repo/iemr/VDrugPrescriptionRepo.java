package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.VDrugPrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VDrugPrescriptionRepo extends JpaRepository<VDrugPrescription, Integer> {

    List<VDrugPrescription> findByFacilityID(Integer facilityID);
}
