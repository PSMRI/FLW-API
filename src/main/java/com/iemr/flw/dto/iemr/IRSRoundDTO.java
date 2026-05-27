package com.iemr.flw.dto.iemr;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IRSRoundDTO {
    private Long id;
    @NotNull(message = "Date cannot be null")
    private LocalDate date;
    @Min(value = 1, message = "Rounds must be at least 1")
    private int rounds;
    @NotNull(message = "Household ID cannot be null")
    private Long householdId;
}