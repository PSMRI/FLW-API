package com.iemr.flw.dto.iemr;


import lombok.Data;

import java.util.List;

@Data
public class TBScreeningRequestDTO {

    private Integer userId;

    private List<TBScreeningDTO> tbScreeningList;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<TBScreeningDTO> getTbScreeningList() {
        return tbScreeningList;
    }

    public void setTbScreeningList(List<TBScreeningDTO> tbScreeningList) {
        this.tbScreeningList = tbScreeningList;
    }
}
