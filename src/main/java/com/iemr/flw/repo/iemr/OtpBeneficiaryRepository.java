package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.OtpBeneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface OtpBeneficiaryRepository extends JpaRepository<OtpBeneficiary, Long> {
    Optional<OtpBeneficiary> findByPhoneNumberAndOtp(String phoneNumber, Integer otp);
}
