package com.iemr.flw.domain.iemr;

import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Table(name = "t_ors_distribution",schema = "db_iemr")
@Entity
@Data
public class OrsDistribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "household_id")
    private Long householdId;

    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "child_count")
    private String  childCount;

    @Column(name = "num_ors_packets")
    private String numOrsPackets;


    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private java.sql.Timestamp updatedAt;

}
