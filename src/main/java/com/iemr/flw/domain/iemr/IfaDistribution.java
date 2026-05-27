package com.iemr.flw.domain.iemr;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import lombok.Data;
import org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;

@Entity
@Table(name = "t_ifa_distribution")
@Data
public class IfaDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "house_hold_id")
    private Long houseHoldId;

    @Column(name = "form_id")
    private String formId;

    @Column(name = "visit_date")
    private String visitDate; // kept as String because "N/A" possible

    @Column(name = "ifa_provision_date")
    private LocalDate ifaProvisionDate;

    @Column(name = "mcp_card_upload", columnDefinition = "TEXT")
    private String mcpCardUpload;

    @Column(name = "ifa_bottle_count")
    private String  ifaBottleCount;
}
