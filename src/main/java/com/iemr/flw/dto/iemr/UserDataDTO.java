package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.util.List;

@Data
public class UserDataDTO<T> {

    private Integer userId;

    private List<T> entries;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<T> getEntries() {
        return entries;
    }

    public void setEntries(List<T> entries) {
        this.entries = entries;
    }
}
