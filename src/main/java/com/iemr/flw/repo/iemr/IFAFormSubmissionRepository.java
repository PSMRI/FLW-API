package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.IFAFormSubmissionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IFAFormSubmissionRepository extends JpaRepository<IFAFormSubmissionData, Long> {

    List<IFAFormSubmissionData> findByUserId(Integer userName);
}
