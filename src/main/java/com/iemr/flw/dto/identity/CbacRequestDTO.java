package com.iemr.flw.dto.identity;

import lombok.Data;

import java.util.List;

@Data
public class CbacRequestDTO {
    private String userName;

    private List<CbacDTO> cbacDTOList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<CbacDTO> getCbacDTOList() {
        return cbacDTOList;
    }

    public void setCbacDTOList(List<CbacDTO> cbacDTOList) {
        this.cbacDTOList = cbacDTOList;
    }
}
