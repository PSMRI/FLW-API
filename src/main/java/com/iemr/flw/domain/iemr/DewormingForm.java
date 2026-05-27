package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "deworming_form", schema = "db_iemr")
@Data
public class DewormingForm {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @Column(name = "deworming_done")
    private String dewormingDone;

    @Column(name = "deworming_date")
    private String dewormingDate;

    @Column(name = "deworming_location")
    private String dewormingLocation;

    @Column(name = "age_group")
    private Integer ageGroup;

    @Column(name = "image1")
    private String image1;

    @Column(name = "image2")
    private String image2;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private Timestamp createdDate;

    @Column(name = "form_type")
    private String formType;
}