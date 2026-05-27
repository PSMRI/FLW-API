package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.MosquitoNetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MosquitoNetRepository extends JpaRepository<MosquitoNetEntity, Long> {
    List<MosquitoNetEntity> findByUserId(Integer userId);
}
