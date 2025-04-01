package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.KalaAzarData;
import lombok.Data;

import java.sql.Date;
import java.util.List;
@Data
public class KalaAzarDTO {
   Integer userId;
   List<KalaAzarData> kalaAzarLists;
}

