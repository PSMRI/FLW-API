package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.BenAnthropometryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenAnthropometryRepo extends JpaRepository<BenAnthropometryDetail, Long> {

    List<BenAnthropometryDetail> findByBeneficiaryRegIDOrderByCreatedDateDesc(Long beneficiaryRegID);

    BenAnthropometryDetail findByBenVisitID(Long benVisitID);
}
