package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "m_incentive_activity_lang_mapping",schema = "db_iemr")
public class IncentiveActivityLangMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_name")
    private String name;

    @Column(name = "activity_description")
    private String description;

    @Column(name = "payment_parameter")
    private String paymentParam;

    @Column(name = "rate_per_activity")
    private Integer rate;

    @Column(name = "group_name")
    private String group;


    @Column(name = "created_date")
    private Timestamp createdDate;


    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "assame_activity_description")
    private String assameActivityDescription;

    @Column(name = "hindi_activity_description")
    private String hindiActivityDescription;


}
