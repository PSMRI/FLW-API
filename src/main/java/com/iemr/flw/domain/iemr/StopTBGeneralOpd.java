package com.iemr.flw.domain.iemr;

import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tb_stoptb_general_opd", schema = "db_iemr")
@Data
public class StopTBGeneralOpd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_reg_id")
    private Long benRegID;

    @Column(name = "provider_service_map_id")
    private Integer providerServiceMapID;

    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    // JSON array of selected drug names
    @Column(name = "medication", columnDefinition = "TEXT")
    private String medication;

    @Column(name = "dosage")
    private String dosage;

    // Once daily | Twice daily | Thrice daily | SoS
    @Column(name = "frequency")
    private String frequency;

    // 1 day | 2 days | 3 days | 5 days | 7 days
    @Column(name = "duration")
    private String duration;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

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

    // Sync fields
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

    @Column(name = "benVisitID")
    private Long benVisitID;

    @Column(name = "chief_complaint_id")
    private Integer chiefComplaintID;

    @Column(name = "item_id")
    private Integer itemID;

    @Column(name = "dispensed_qty")
    private Integer dispensedQty;
}
