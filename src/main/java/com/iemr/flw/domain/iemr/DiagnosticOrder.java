package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tb_diagnostic_order", schema = "db_iemr")
@Data
public class DiagnosticOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_event", length = 100)
    private String orderEvent;

    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @Column(name = "provider_service_name", length = 50)
    private String providerServiceName;

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

    @Column(name = "retried_at")
    private Timestamp retriedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // Why the order was closed, one column per test (see DiagnosticCloseStage): xray, mtb, or mdrrif
    // for an MTB/MTB_PLUS order whose MTB was positive but whose MDR RIF test wasn't done. Set through
    // setReasonToClose(stage, reason). The old reason_to_close column is no longer mapped.
    @Column(name = "reason_to_close_xray", columnDefinition = "TEXT")
    private String reasonToCloseXray;

    @Column(name = "reason_to_close_mtb", columnDefinition = "TEXT")
    private String reasonToCloseMTB;

    @Column(name = "reason_to_close_mdrrif", columnDefinition = "TEXT")
    private String reasonToCloseMDRRIF;

    @Column(name = "push_response_json", columnDefinition = "LONGTEXT")
    private String pushResponseJson;

    @Column(name = "cancel_response_json", columnDefinition = "LONGTEXT")
    private String cancelResponseJson;

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

    @Column(name = "manually_entered_by")
    private String manuallyEnteredBy;

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

    @Column(name = "vanSerialNo", updatable = false)
    private Long vanSerialNo;

    @Column(name = "synced_by")
    private String syncedBy;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "synced_date")
    private Timestamp syncedDate;

    public void setReasonToClose(com.iemr.flw.masterEnum.DiagnosticCloseStage stage, String reason) {
        switch (stage) {
            case XRAY -> reasonToCloseXray = reason;
            case MTB -> reasonToCloseMTB = reason;
            case RIF -> reasonToCloseMDRRIF = reason;
        }
    }
}
