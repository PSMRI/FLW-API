package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "m_stoptb_chiefcomplaint", schema = "db_iemr")
@Data
public class StopTBChiefComplaintMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StopTBChiefComplaintID")
    private Integer stopTBChiefComplaintID;

    @Column(name = "ChiefComplaint")
    private String chiefComplaint;

    @Column(name = "ChiefComplaintDesc")
    private String chiefComplaintDesc;

    @Column(name = "Deleted")
    private Boolean deleted;

    @Column(name = "Processed")
    private String processed;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate", insertable = false, updatable = false)
    private Timestamp createdDate;

    @Column(name = "ModifiedBy")
    private String modifiedBy;

    @Column(name = "LastModDate", insertable = false, updatable = false)
    private Timestamp lastModDate;
}
