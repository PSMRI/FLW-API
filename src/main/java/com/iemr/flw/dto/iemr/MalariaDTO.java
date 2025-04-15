package com.iemr.flw.dto.iemr;


import lombok.Data;

import java.util.List;
@Data
public class MalariaDTO {
    Integer userId;
    List<DiseaseMalariaDTO> malariaLists;



}


