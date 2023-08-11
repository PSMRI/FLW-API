package com.iemr.flw.domain.iemr;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_hbyc", schema = "db_iemr")
@Data
public class HBYC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "sub_center_name")
    private String subcenterName;

    @Column(name = "primary_health_center_name")
    private String primaryHealthCenterName;

    @Column(name = "village_population")
    private Integer villagePopulation;

    @Column(name = "infant_population")
    private Integer infantPopulation;

    @Column(name = "visit_date")
    private Timestamp visitDate;

    @Column(name = "hbyc_age_category")
    private String hbycAgeCategory;

    @Column(name = "ors_packet_delivered")
    private Boolean orsPacketDelivered;

    @Column(name = "iron_folic_acid_given")
    private Boolean ironFolicAcidGiven;

    @Column(name = "is_vaccinated_by_age")
    private Boolean isVaccinatedByAge;

    @Column(name = "was_ill")
    private Boolean wasIll;

    @Column(name = "referred")
    private Boolean referred;

    @Column(name = "supplements_given")
    private Boolean supplementsGiven;

    @Column(name = "by_height_length")
    private Boolean byHeightLength;

    @Column(name = "children_weighing_less_referred")
    private Boolean childrenWeighingLessReferred;

    @Column(name = "weight_according_to_age")
    private Boolean weightAccordingToAge;

    @Column(name = "delay_in_development")
    private Boolean delayInDevelopment;

    @Column(name = "referred_to_health_institute")
    private Boolean referredToHealthInstitite;

    @Column(name = "vitaminA_supplements_given")
    private Boolean vitaminASupplementsGiven;

    @Column(name = "death_age")
    private String deathAge;

    @Column(name = "death_cause")
    private String deathCause;

    @Column(name = "qm_or_anm_informed")
    private Boolean qmOrAnmInformed;

    @Column(name = "death_place")
    private String  deathPlace;

    @Column(name = "supervisor_on")
    private Boolean superVisorOn;

    @Column(name = "ors_shortage")
    private Boolean orsShortage;

    @Column(name = "if_a_decreased")
    private Boolean ifaDecreased;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
