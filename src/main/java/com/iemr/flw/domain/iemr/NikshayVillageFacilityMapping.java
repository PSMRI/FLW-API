package com.iemr.flw.domain.iemr;

import jakarta.persistence.*;

/**
 * FLW-API's own read copy of Admin-API's m_nikshay_village_facility_mapping —
 * links an existing AMRIT village to the Nikshay Facility/TU it falls under.
 * Read-only from this side; the table itself is owned/populated by the
 * Nikshay location import (see Admin-API).
 */
@Entity
@Table(name = "m_nikshay_village_facility_mapping", schema = "db_iemr")
public class NikshayVillageFacilityMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MappingID")
    private Integer mappingID;

    @Column(name = "AmritVillageID")
    private Integer amritVillageID;

    @Column(name = "NikshayFacilityID")
    private Integer nikshayFacilityID;

    @Column(name = "NikshayTUID")
    private Integer nikshayTUID;

    public Integer getMappingID() { return mappingID; }
    public Integer getAmritVillageID() { return amritVillageID; }
    public Integer getNikshayFacilityID() { return nikshayFacilityID; }
    public Integer getNikshayTUID() { return nikshayTUID; }
}
