package com.iemr.flw.dto.iemr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IRSRoundDTO {
    private Long id;
    private Date date;
    private int rounds;
    private BigInteger householdId;
}