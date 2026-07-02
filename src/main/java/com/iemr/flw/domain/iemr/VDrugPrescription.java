package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "v_drugforprescription", schema = "db_iemr")
@Data
public class VDrugPrescription {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "itemID")
    private Integer itemID;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "Strength")
    private String strength;

    @Column(name = "unitOfMeasurement")
    private String unitOfMeasurement;

    @Column(name = "quantityInHand")
    private Long quantityInHand;

    @Column(name = "itemFormID")
    private Integer itemFormID;

    @Column(name = "facilityID")
    private Integer facilityID;
}
