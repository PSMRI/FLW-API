package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Type;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tb_referral_follow_up",schema = "db_iemr")
public class TbReferralFollowUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "household_id")
    private Long houseHoldId;

    @Column(name = "referred_on_date")
    private Timestamp referredOnDate;

    @Column(name = "follow_up_date")
    private Timestamp followUpDate;

    @Column(name = "follow_up_status")
    private String followUpStatus;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "synced_by")
    private String syncedBy;

    @Column(name = "user_id")
    private Integer userId;

}
