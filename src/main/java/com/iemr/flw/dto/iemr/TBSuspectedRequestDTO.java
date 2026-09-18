package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;

@Data
public class TBSuspectedRequestDTO {

    private Integer userId;

    private List<TBSuspectedDTO> tbSuspectedList;

    private Boolean fromStopTB;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getFromStopTB() {
        return fromStopTB;
    }

    public void setFromStopTB(Boolean fromStopTB) {
        this.fromStopTB = fromStopTB;
    }

    public List<TBSuspectedDTO> getTbSuspectedList() {
        return tbSuspectedList;
    }

    public void setTbSuspectedList(List<TBSuspectedDTO> tbSuspectedList) {
        this.tbSuspectedList = tbSuspectedList;
    }
}
