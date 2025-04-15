package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;
@Data
public class FilariaDTO {
     Integer userId;
     List<DiseaseFilariasisDTO> filariaLists;
}

