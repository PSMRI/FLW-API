package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tb_diagnostic_provider_token", schema = "db_iemr",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider_code", "token_type"}))
@Data
public class DiagnosticProviderToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_code", length = 50, nullable = false)
    private String providerCode;

    // "ACCESS", "REFRESH", "API_KEY" — extensible without schema change
    @Column(name = "token_type", length = 20, nullable = false)
    private String tokenType;

    @Column(name = "token_value", columnDefinition = "TEXT", nullable = false)
    private String tokenValue;

    @Column(name = "expires_at")
    private Timestamp expiresAt;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    @Column(name = "last_mod_date")
    private Timestamp lastModDate;
}
