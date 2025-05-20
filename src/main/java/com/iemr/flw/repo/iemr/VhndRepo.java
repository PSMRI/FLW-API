package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.VHNDForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VhndRepo extends JpaRepository<VHNDForm ,Integer> {


    List<? extends Object> findByUserId(long longValue);
}