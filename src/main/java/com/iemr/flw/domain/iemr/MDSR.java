package com.iemr.flw.domain.iemr;

import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_mdsr", schema = "db_iemr")
@Data
public class MDSR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "death_date")
    private Timestamp dateOfDeath;

    @Column(name = "address")
    private String address;

    @Column(name = "husband_name")
    private String husbandName;

    @Column(name = "death_cause")
    private String causeOfDeath;

    @Column(name = "death_reason")
    private String reasonDeath;

    @Column(name = "investigation_date")
    private Timestamp investigationDate;

    @Column(name = "action_taken")
    private Boolean actionTaken;

    @Column(name = "signature")
    private String signature;

    @Column(name = "date_ic")
    private Timestamp dateIc;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "mdsr1_file")
    private String mdsr1File;

    @Column(name = "mdsr2_file")
    private String mdsr2File;

    @Column(name = "mdsr_death_cert_file")
    private String mdsrDeathCertFile;
}
