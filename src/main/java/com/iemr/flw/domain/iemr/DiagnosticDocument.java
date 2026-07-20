package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tb_diagnostic_document", schema = "db_iemr",
        uniqueConstraints = @UniqueConstraint(columnNames = {"diagnostic_order_id", "document_type"}),
        indexes = {
        @Index(name = "idx_diagnostic_document_ben_reg_id", columnList = "ben_reg_id"),
        @Index(name = "idx_diagnostic_document_order_type", columnList = "order_type"),
        @Index(name = "idx_diagnostic_document_epoch_time", columnList = "epoch_time")
})
@Data
public class DiagnosticDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diagnostic_order_id")
    private Long diagnosticOrderId;

    @Column(name = "ben_reg_id")
    private Long benRegID;

    @Column(name = "order_type", length = 20)
    private String orderType;

    @Column(name = "asset_type", length = 50)
    private String assetType;

    @Column(name = "document_type", length = 30)
    private String documentType;

    @Column(name = "epoch_time")
    private Long epochTime;

    @Column(name = "stored_file_name", length = 150)
    private String storedFileName;

    @Column(name = "stored_path", length = 255)
    private String storedPath;

    @Column(name = "sha256_hash", length = 64)
    private String sha256Hash;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

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