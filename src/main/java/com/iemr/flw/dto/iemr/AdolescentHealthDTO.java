package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.AdolescentHealth;
import lombok.Data;

import java.util.List;
@Data
public class AdolescentHealthDTO {
    Integer userId;
    List<AdolescentHealth> adolescentHealths;
}
