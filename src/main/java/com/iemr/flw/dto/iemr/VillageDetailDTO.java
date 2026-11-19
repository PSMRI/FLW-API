package com.iemr.flw.dto.iemr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One assigned village's master Pincode in getUserDetail (Stop TB only); null where the
 * village master has none. residentialAreaId / residentialArea are always null for now
 * (no village-level source yet) and are kept so the response shape matches the app.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VillageDetailDTO {
    private Integer villageId;
    private String villageName;
    private String pinCode;
    private Integer residentialAreaId;
    private String residentialArea;
}
