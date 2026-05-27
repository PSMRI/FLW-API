package com.iemr.flw.dto.iemr;

import com.iemr.flw.domain.iemr.AdolescentHealth;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
@Data
public class AdolescentHealthDTO {
    @NotNull(message = "User ID cannot be null")
    Integer userId;
    @NotNull(message = "Adolescent health records cannot be null")
    @Size(min = 1, message = "At least one adolescent health record is required")
    List<AdolescentHealth> adolescentHealths;
}
