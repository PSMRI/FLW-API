package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineRepo extends JpaRepository<Vaccine, Short> {

    List<Vaccine> getAllByCategory(String category);
}
