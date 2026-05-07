package com.iemr.flw.domain.iemr;

import lombok.Data;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "i_ben_flow_outreach")
@Data
public class BenFlowStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ben_flow_id")
    private Long benFlowID;

    @Column(name = "beneficiary_reg_id")
    private Long beneficiaryRegID;

    @Column(name = "beneficiary_id")
    private Long beneficiaryID;

    @Column(name = "visit_category")
    private String visitCategory;

    @Column(name = "nurse_flag")
    private Short nurseFlag;

    @Column(name = "doctor_flag")
    private Short doctorFlag;

    @Column(name = "pharmacist_flag")
    private Short pharmacistFlag;

    @Column(name = "providerServiceMapID")
    private Integer providerServiceMapId;

    @Column(name = "vanID")
    private Integer vanID;

    @Column(name = "ben_name")
    private String benName;

    @Column(name = "ben_age_val")
    private Integer benAgeVal;

    @Column(name = "ben_dob")
    private Timestamp dob;

    @Column(name = "ben_gender_val")
    private Short genderID;

    @Column(name = "ben_gender")
    private String genderName;

    @Column(name = "ben_phone_no")
    private String preferredPhoneNum;

    @Column(name = "districtID")
    private Integer districtID;

    @Column(name = "district")
    private String districtName;

    @Column(name = "villageID")
    private Integer villageID;

    @Column(name = "village")
    private String villageName;

    @Column(name = "registrationDate")
    private Timestamp registrationDate;

    @Column(name = "created_by")
    private String agentId;

    @Column(name = "created_date", insertable = false, updatable = false)
    private Timestamp visitDate;

    @Column(name = "deleted", insertable = false)
    private Boolean deleted;
}
