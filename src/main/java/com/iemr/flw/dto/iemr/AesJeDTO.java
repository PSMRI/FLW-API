package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;
@Data
public class AesJeDTO {
    Integer userId;
    List<DiseaseAesjeDto> aesJeLists;
}

