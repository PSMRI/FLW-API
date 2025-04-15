package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;
@Data
public class KalaAzarDTO {
   Integer userId;
   List<DiseaseKalaAzarDTO> kalaAzarLists;
}

