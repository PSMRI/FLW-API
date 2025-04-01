package com.iemr.flw.dto.iemr;


import com.iemr.flw.domain.iemr.MalariaData;
import lombok.Data;

import java.sql.Date;
import java.util.List;
@Data
public class MalariaDTO {
    Integer userId;
    List<MalariaData> malariaLists;



}


