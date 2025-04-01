package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.AesJeData;
import lombok.Data;

import java.sql.Date;
import java.util.List;
@Data
public class AesJeDTO {
    Integer userId;
    List<AesJeData> aesJeLists;
}

