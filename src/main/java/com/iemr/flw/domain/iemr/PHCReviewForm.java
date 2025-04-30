package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "phc_review_meeting", schema = "db_iemr")
@Data
public class PHCReviewForm {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @Column(name = "phc_review_date")
    private String phcReviewDate;

    @Column(name = "place")
    private String place;

    @Column(name = "no_of_beneficiaries_attended")
    private Integer noOfBeneficiariesAttended;

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