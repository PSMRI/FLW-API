package com.iemr.flw.domain.iemr;

import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * One row per real-world Stop TB camp encounter (a beneficiary seen on a given calendar day).
 * Mirrors MMU's t_benvisitdetail (visitNo, insert-only history) but scoped entirely to Stop TB's
 * own tables — never written to or read from MMU/HWC's shared visit/outreach infrastructure.
 */
@Entity
@Table(name = "tb_stoptb_visit", schema = "db_iemr")
@Data
public class TBStopVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiary_reg_id")
    private Long beneficiaryRegID;

    @Column(name = "visit_no")
    private Integer visitNo;

    @CreationTimestamp
    @Column(name = "visit_date", updatable = false)
    private Timestamp visitDate;

    @Column(name = "provider_service_map_id")
    private Integer providerServiceMapID;

    @Column(name = "created_by")
    private String createdBy;

    // Role-handoff flags — only nurseCompletedFlag is populated today (Stop TB has no
    // Doctor/Counsellor screens yet). Reserved so a future worklist can query against
    // this table without a schema change later.
    @Column(name = "nurse_completed_flag")
    private String nurseCompletedFlag = "N";

    @Column(name = "doctor_completed_flag")
    private String doctorCompletedFlag = "N";

    @Column(name = "counsellor_completed_flag")
    private String counsellorCompletedFlag = "N";

    // Sync fields — same vanID/vanSerialNo/processed discipline as every other Stop TB table,
    // required so two camp laptops can each generate local visit rows without colliding at
    // central (de-duplication key is (vanSerialNo, vanID), not the local auto-increment id).
    @Column(name = "vanID")
    private Integer vanID;

    @Column(name = "parkingPlaceID")
    private Integer parkingPlaceID;

    @Column(name = "processed")
    private String processed = "N";

    @Column(name = "vanSerialNo")
    private Long vanSerialNo;

    // Stamped by the generic local-to-central DataSyncRepository job (MMU/HWC/TM-API convention)
    // when this row is actually pushed — required for tb_stoptb_visit to be syncable by that
    // same generic mechanism without code changes there.
    @Column(name = "SyncedDate")
    private Timestamp syncedDate;

    @Column(name = "Syncedby")
    private String syncedby;

    @Column(name = "SyncFailureReason")
    private String syncFailureReason;
}
