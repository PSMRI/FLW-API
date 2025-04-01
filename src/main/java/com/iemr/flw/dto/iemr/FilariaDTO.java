package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.FilariaData;
import lombok.Data;

import java.sql.Date;
import java.util.List;
@Data
public class FilariaDTO {
     Integer userId;
     List<FilariaData> filariaLists;
}

