package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "t_cdr", schema = "db_iemr")
@Data
public class CDR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "address")
    private String address;

    @Column(name = "house_no")
    private String houseNo;

    @Column(name = "colony")
    private String colony;

    @Column(name = "pincode")
    private Integer pincode;

    @Column(name = "landmarks")
    private String landmarks;

    @Column(name = "landline")
    private Long landline;

    @Column(name = "mobile")
    private Long mobile;

    @Column(name = "death_date")
    private Timestamp deathDate;

    @Column(name = "death_place")
    private String placeOfDeath;

    @Column(name = "informant")
    private String nameOfInformant;

    @Column(name = "death_time")
    private String deathTime;

    @Column(name = "signature")
    private String signature;

    @Column(name = "notification_date")
    private Timestamp notificationDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
