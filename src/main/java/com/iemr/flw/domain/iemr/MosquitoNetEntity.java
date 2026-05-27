package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "i_mobilization_mosquito_net",schema = "db_iemr")
public class MosquitoNetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "beneficiary_id")
    private Long beneficiaryId;
    @Column(name = "household_id")
    private Long houseHoldId;
    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "user_name")
    private String userName;


    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "is_net_distributed")
    private String isNetDistributed;

}
