package com.iemr.flw.domain.iemr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_mda_distribution_data", schema = "db_iemr")
public class MdaDistributionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BeneficiaryId")
    private Long beneficiaryId;

    @Column(name = "HouseHoldId", nullable = false)
    private Long houseHoldId;

    @Column(name = "FormId")
    private String formId;


    @Column(name = "VisitDate")
    private Timestamp visitDate;

    @Column(name = "UserName")
    private String userName;

    @Column(name = "MdaDistributionDate")
    private Timestamp mdaDistributionDate;

    @Column(name = "IsMedicineDistributed")
    private String isMedicineDistributed;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "CreatedDate")
    @CreatedDate
    private Timestamp createdDate;

    @Column(name = "ModifiedBy")
    private String modifiedBy;

    @Column(name = "LastModDate")
    private Timestamp lastModDate;
}
