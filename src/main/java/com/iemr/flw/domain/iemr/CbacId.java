package com.iemr.flw.domain.iemr;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;

public class CbacId implements Serializable {
    private Long beneficiaryRegId;
    private Timestamp createdDate;

    @Basic
    @Column(name = "BeneficiaryRegID")
    public Long getBeneficiaryRegId() {
        return beneficiaryRegId;
    }

    public void setBeneficiaryRegId(Long beneficiaryRegId) {
        this.beneficiaryRegId = beneficiaryRegId;
    }

    @Basic
    @Column(name = "CreatedDate")
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

}
