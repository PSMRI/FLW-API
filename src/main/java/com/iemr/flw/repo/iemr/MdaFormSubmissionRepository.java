package com.iemr.flw.repo.iemr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iemr.flw.domain.iemr.MdaDistributionData;
import com.iemr.flw.dto.iemr.MdaFormSubmissionResponse;

import java.util.List;

@Repository
public interface MdaFormSubmissionRepository extends JpaRepository<MdaDistributionData, Long> {
    List<MdaDistributionData> findByCreatedBy(String createdBy);

    List<MdaDistributionData> findByUserName(String userName);

}