package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "uwin_session_record",schema = "db_iemr")
public class UwinSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "asha_id", nullable = false)
    private Integer ashaId;

    @Column(name = "date", nullable = false)
    private Timestamp date;

    @Column(name = "session_date", nullable = false)
    private Timestamp sessionDate;

    @Column(name = "place", nullable = false)
    private String place;

    @Column(name = "participants", nullable = false)
    private Integer participants;

    @Lob
    @Column(name = "attachments_json", columnDefinition = "LONGTEXT")
    private String attachmentsJson;

    @Column(name = "created_by")
    private String createdBy;


}
