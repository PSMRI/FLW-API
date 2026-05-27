package com.iemr.flw.dto.iemr;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MdaFormFieldsDTO {
    private String mda_distribution_date;
    private String is_medicine_distributed;
}