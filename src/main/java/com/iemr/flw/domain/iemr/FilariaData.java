package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Date;

@Entity
@Data
@Table(name = "disease_filariasis",schema = "db_iemr")
public class FilariaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "suffering_from_filariasis")
    private Boolean sufferingFromFilariasis;

    @Column(name = "which_part_of_body")
    private String whichPartOfBody;

    @Column(name = "home_visit_date")
    private Date homeVisitDate;

    @Column(name = "dec_and_albendazole_dose_status")
    private String decAndAlbendazoleDoseStatus;

    @Column(name = "medicine_side_effect")
    private String medicineSideEffect;

    @Column(name = "other")
    private String other;

    @Column(name = "diseaseTypeID")
    private Integer diseaseTypeID;

    @Column(name = "ben_id")
    private BigInteger benId;
    @Column(name = "userID")
    Integer userID;
}

