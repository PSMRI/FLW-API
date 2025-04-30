package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.VHNDForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VhndRepo extends JpaRepository<VHNDForm ,Integer> {

}