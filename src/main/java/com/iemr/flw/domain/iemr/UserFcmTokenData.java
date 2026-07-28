package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_fcm_tokens", schema = "db_iemr")
@Data
public class UserFcmTokenData {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;
    @Column(name = "user_id")
    private  Integer userId;
    @Column(name = "token")
    private String token;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
