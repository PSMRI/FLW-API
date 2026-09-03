package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BenPhysicalVitalDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenPhysicalVitalRepo extends JpaRepository<BenPhysicalVitalDetail, Long> {

    List<BenPhysicalVitalDetail> findByBeneficiaryRegIDOrderByCreatedDateDesc(Long beneficiaryRegID);

    BenPhysicalVitalDetail findByBenVisitID(Long benVisitID);
}
