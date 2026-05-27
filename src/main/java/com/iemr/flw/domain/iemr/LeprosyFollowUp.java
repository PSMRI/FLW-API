package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "leprosy_follow_up", schema = "db_iemr")
@Data
public class LeprosyFollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_number")
    private Integer visitNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "follow_up_date")
    private Date followUpDate;

    @Column(name = "treatment_status", length = 100)
    private String treatmentStatus;

    @Column(name = "mdt_blister_pack_received", length = 100)
    private String mdtBlisterPackReceived;

    @Temporal(TemporalType.DATE)
    @Column(name = "treatment_complete_date")
    private Date treatmentCompleteDate;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Temporal(TemporalType.DATE)
    @Column(name = "home_visit_date")
    private Date homeVisitDate;

    @Column(name = "leprosy_symptoms", length = 255)
    private String leprosySymptoms;

    @Column(name = "type_of_leprosy", length = 100)
    private String typeOfLeprosy;

    @Column(name = "leprosy_symptoms_position")
    private Integer leprosySymptomsPosition;

    @Column(name = "visit_label", length = 100)
    private String visitLabel;

    @Column(name = "leprosy_status", length = 100)
    private String leprosyStatus;

    @Column(name = "referred_to", length = 255)
    private String referredTo;

    @Column(name = "refer_to_name", length = 255)
    private String referToName;

    @Temporal(TemporalType.DATE)
    @Column(name = "treatment_end_date")
    private Date treatmentEndDate;

    @Column(name = "mdt_blister_pack_recived", length = 100)
    private String mdtBlisterPackRecived;

    @Temporal(TemporalType.DATE)
    @Column(name = "treatment_start_date")
    private Date treatmentStartDate;

    // Audit fields
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @Column(name = "last_mod_date")
    private Timestamp lastModDate;
}