package com.iemr.flw.dto.iemr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IFAFormFieldsDTO {
    private String visit_date;
    private String  ifa_provided;
    private Double ifa_quantity;
}
