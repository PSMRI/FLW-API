package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;

@Data
public class TBSuspectedRequestDTO {

    private Integer userId;

    private List<TBSuspectedDTO> tbSuspectedList;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<TBSuspectedDTO> getTbSuspectedList() {
        return tbSuspectedList;
    }

    public void setTbSuspectedList(List<TBSuspectedDTO> tbSuspectedList) {
        this.tbSuspectedList = tbSuspectedList;
    }
}
