package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.DiagnosticProviderToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagnosticProviderTokenRepo extends JpaRepository<DiagnosticProviderToken, Long> {

    Optional<DiagnosticProviderToken> findByProviderCodeAndTokenType(String providerCode, String tokenType);
}
