package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.TBConfirmedCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TBConfirmedTreatmentRepository
        extends JpaRepository<TBConfirmedCase, Integer> {

    List<TBConfirmedCase> findByBenId(Long benId);
    List<TBConfirmedCase> findByUserId(Integer benId);

}
