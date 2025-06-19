package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.AdolescentHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public  interface  AdolescentHealthRepo extends JpaRepository<AdolescentHealth,Long> {
    Optional<AdolescentHealth> findByBenId(BigInteger benId);
    List<AdolescentHealth> findByUserId(Integer userId);
}
