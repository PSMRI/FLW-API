package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "irs_round", schema = "db_iemr")
@AllArgsConstructor
@NoArgsConstructor
public class IRSRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "irs_date")
    private LocalDate date;

    @Column(name = "round")
    private int rounds;

    @Column(name = "householdId")
    private Long householdId;

}