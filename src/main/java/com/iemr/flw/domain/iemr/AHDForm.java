package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "ahd_form", schema = "db_iemr")
@Data
public class AHDForm {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @Column(name = "mobilized_for_ahd")
    private String mobilizedForAHD;

    @Column(name = "ahd_place")
    private String ahdPlace;

    @Column(name = "ahd_date")
    private String ahdDate;

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