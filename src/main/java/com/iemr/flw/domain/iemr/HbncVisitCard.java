package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_hbnc_visit_card", schema = "db_iemr")
@Data
public class HbncVisitCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "visit_no")
    private Integer visitNo;

    @Column(name = "asha_name")
    private String ashaName;

    @Column(name = "village_name")
    private String villageName;

    @Column(name = "sub_center_name")
    private String subCenterName;

    @Column(name = "block_name")
    private String blockName;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "delivery_date")
    private Timestamp dateOfDelivery;

    @Column(name = "delivery_place")
    private String placeOfDelivery;

    @Column(name = "baby_gender")
    private String babyGender;

    @Column(name = "delivery_type")
    private String typeOfDelivery;

    @Column(name = "still_birth")
    private Boolean stillBirth;

    @Column(name = "started_breast_feeding")
    private String startedBreastFeeding;

    @Column(name = "mother_discharge_date")
    private Timestamp dischargeDateMother;

    @Column(name = "baby_discharge_date")
    private Timestamp dischargeDateBaby;

    @Column(name = "weight")
    private Integer weightInGrams;

    @Column(name = "birth_registration")
    private Boolean registrationOfBirth;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
