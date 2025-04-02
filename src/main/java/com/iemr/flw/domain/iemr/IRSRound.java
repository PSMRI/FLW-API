package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Date;

@Entity
@Data
@Table(name = "irs_round",schema = "db_iemr")
@AllArgsConstructor
public class IRSRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "round")
    private Date date;

    @Column(name = "irs_date")
    private int rounds;
    
    @Column(name = "householdId")
    private BigInteger household;
}