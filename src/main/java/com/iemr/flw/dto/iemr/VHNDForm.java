package com.iemr.flw.dto.iemr;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "VHND_form",schema = "db_iemr")
@Data
public class VHNDForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "vhnd_date")
    private String vhndDate;

    @Column(name = "place")
    private String place;

    @Column(name = "no_of_beneficiaries_attended")
    private Integer noOfBeneficiariesAttended;

    @Column(name = "image1")
    private String image1;

    @Column(name = "image2")
    private String image2;

    @Column(name = "form_type")
    private String formType;

    @Column(name = "created_by")
    private String createdBy;


}
