package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tb_diagnostic_result", schema = "db_iemr",
        uniqueConstraints = @UniqueConstraint(columnNames = {"diagnostic_order_id"}))
@Data
public class DiagnosticResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diagnostic_order_id")
    private Long diagnosticOrderId;

    @Column(name = "ben_reg_id")
    private Long benRegID;

    @Column(name = "provider_status", length = 20)
    private String providerStatus;

    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultSummary;

    @Column(name = "raw_response_json", columnDefinition = "LONGTEXT")
    private String rawResponseJson;

    @Column(name = "tb_presence")
    private Boolean tbPresence;

    @Column(name = "tb_confidence")
    private Double tbConfidence;

    @Column(name = "drug_resistance_presence")
    private Boolean drugResistancePresence;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private Timestamp createdDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @UpdateTimestamp
    @Column(name = "last_mod_date")
    private Timestamp lastModDate;

    @Column(name = "deleted")
    private Boolean deleted = false;
}
