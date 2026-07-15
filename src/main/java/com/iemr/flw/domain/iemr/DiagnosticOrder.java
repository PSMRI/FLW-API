package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tb_diagnostic_order", schema = "db_iemr",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ben_reg_id", "visitCode", "order_type"}))
@Data
public class DiagnosticOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_event", length = 100)
    private String orderEvent;

    @Column(name = "ben_reg_id")
    private Long benRegID;

    @Column(name = "provider_service_map_id")
    private Integer providerServiceMapID;

    @Column(name = "provider_code", length = 50)
    private String providerCode;

    @Column(name = "order_type", length = 20)
    private String orderType;

    @Column(name = "external_order_id", unique = true, length = 100)
    private String externalOrderId;

    @Column(name = "provider_order_id", length = 100)
    private String providerOrderId;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "last_polled_at")
    private Timestamp lastPolledAt;

    @Column(name = "test_completed_at")
    private Timestamp testCompletedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "push_response_json", columnDefinition = "LONGTEXT")
    private String pushResponseJson;

    @Column(name = "patient_first_name", length = 100)
    private String patientFirstName;

    @Column(name = "patient_last_name", length = 100)
    private String patientLastName;

    @Column(name = "patient_date_of_birth", length = 10)
    private String patientDateOfBirth;

    @Column(name = "patient_sex", length = 10)
    private String patientSex;


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

    @Column(name = "vanID")
    private Integer vanID;

    @Column(name = "parkingPlaceID")
    private Integer parkingPlaceID;

    @Column(name = "visitCode")
    private Long visitCode;

    @Column(name = "processed")
    private String processed = "N";

    @Column(name = "vanSerialNo")
    private Long vanSerialNo;
}
