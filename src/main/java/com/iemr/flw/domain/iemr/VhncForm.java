package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessorOrder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name = "vhnc_form",schema = "db_iemr")
@Data
public class VhncForm {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;
    
    @Column(name = "vhnc_date")
    private String vhncDate;
    
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

    @Column(name = "village_name")
    private String villageName;

    @Column(name = "anm")
    private Integer anm;

    @Column(name = "aww")
    private Integer aww;

    @Column(name = "no_of_pragnent_women")
    private Integer noOfPragnentWoment;

    @Column(name = "no_of_lacting_mother")
    private Integer noOfLactingMother;

    @Column(name = "no_of_committee")
    private Integer noOfCommittee;

    @Column(name = "followup_previous")
    private Boolean followupPrevius;

}