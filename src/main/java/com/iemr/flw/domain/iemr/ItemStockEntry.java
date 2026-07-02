package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "t_itemstockentry", schema = "db_iemr")
@Data
public class ItemStockEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ItemStockEntryID")
    private Long itemStockEntryID;

    @Column(name = "FacilityID")
    private Integer facilityID;

    @Column(name = "ItemID")
    private Integer itemID;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "QuantityInHand")
    private Integer quantityInHand;

    @Column(name = "BatchNo")
    private String batchNo;

    @Column(name = "ExpiryDate")
    private Date expiryDate;

    @Column(name = "Deleted")
    private Boolean deleted;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "VanID")
    private Long vanID;

    @Column(name = "ParkingPlaceID")
    private Long parkingPlaceID;
}
