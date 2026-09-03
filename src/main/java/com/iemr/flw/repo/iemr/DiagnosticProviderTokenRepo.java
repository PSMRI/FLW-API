package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiagnosticProviderToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface DiagnosticProviderTokenRepo extends JpaRepository<DiagnosticProviderToken, Long> {

    Optional<DiagnosticProviderToken> findByProviderCodeAndTokenType(String providerCode, String tokenType);

    @Transactional
    @Modifying
    @Query("UPDATE DiagnosticProviderToken t SET t.vanSerialNo = t.id WHERE t.id = :id")
    void updateVanSerialNo(@Param("id") Long id);
}
