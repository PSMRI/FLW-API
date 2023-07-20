package com.iemr.flw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserServiceRoleDTO {
    private Integer userId;
    private String name;
    private String userName;
    private Integer stateId;
    private String stateName;
    private Integer workingDistrictId;
    private String workingDistrictName;
    private Short serviceProviderId;
    private Integer roleId;
    private String roleName;
    private Integer providerServiceMapId;
    private Integer blockId;
    private String blockName;
    private String villageId;
    private String villageName;

    public UserServiceRoleDTO(Integer userId, String name, String userName, Integer stateId, String stateName, Integer workingDistrictId, String workingDistrictName, Short serviceProviderId, Integer roleId, String roleName, Integer providerServiceMapId, Integer blockId, String blockName, String villageId, String villageName) {
        this.userId = userId;
        this.name = name;
        this.userName = userName;
        this.stateId = stateId;
        this.stateName = stateName;
        this.workingDistrictId = workingDistrictId;
        this.workingDistrictName = workingDistrictName;
        this.serviceProviderId = serviceProviderId;
        this.roleId = roleId;
        this.roleName = roleName;
        this.providerServiceMapId = providerServiceMapId;
        this.blockId = blockId;
        this.blockName = blockName;
        this.villageId = villageId;
        this.villageName = villageName;
    }

    public UserServiceRoleDTO() {

    }
}
