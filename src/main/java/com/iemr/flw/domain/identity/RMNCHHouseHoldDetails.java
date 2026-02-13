package com.iemr.flw.domain.identity;

import com.google.gson.annotations.Expose;
import lombok.Data;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "i_householddetails", schema = "db_identity")
@Data
public class RMNCHHouseHoldDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "houseHoldDetailsId", insertable = false, updatable = false)
	private Long houseHoldDetailsId;

	@Expose
	@Column(name = "BeneficiaryRegID")
	private Long BenRegId;

	@Expose
	@Column(name = "Countyid")
	private Integer Countyid;

	@Expose
	@Column(name = "availabilityofToilet")
	private String availabilityofToilet;

	@Expose
	@Column(name = "availabilityofToiletId")
	private Integer availabilityofToiletId;

	@Expose
	@Column(name = "avalabilityofElectricity")
	private String avalabilityofElectricity;

	@Expose
	@Column(name = "avalabilityofElectricityId")
	private Integer avalabilityofElectricityId;

	@Expose
	@Column(name = "block")
	private String block;

	@Expose
	@Column(name = "bpl_aplId")
	private Integer bpl_aplId;

	@Expose
	@Column(name = "colony")
	private String colony;

	@Expose
	@Column(name = "district")
	private String district;

	@Expose
	@Column(name = "districtid")
	private Integer districtid;

	@Expose
	@Column(name = "districtname")
	private String districtname;

	@Expose
	@Column(name = "familyHeadName")
	private String familyHeadName;

	@Expose
	@Column(name = "familyHeadPhoneNo")
	private String familyHeadPhoneNo;

	@Expose
	@Column(name = "familyName")
	private String familyName;

	@Expose
	@Column(name = "fuelUsed")
	private String fuelUsed;

	@Expose
	@Column(name = "fuelUsedId")
	private Integer fuelUsedId;

	@Expose
	@Column(name = "houseOwnerShip")
	private String houseOwnerShip;

	@Expose
	@Column(name = "houseOwnerShipId")
	private Integer houseOwnerShipId;

	@Expose
	@Column(name = "houseType")
	private String houseType;

	@Expose
	@Column(name = "houseTypeId")
	private Integer houseTypeId;

	@Expose
	@Column(name = "houseno")
	private String houseno;

	@Expose
	@Column(name = "houseNum")
	private String houseNum;
	@Expose
	@Column(name = "houseoldId")
	private Long houseoldId;

	@Expose
	@Column(name = "landIrregatedId")
	private Integer landIrregatedId;

	@Expose
	@Column(name = "landOwnedId")
	private Integer landOwnedId;

	@Expose
	@Column(name = "liveStockOwnerShipId")
	private Integer liveStockOwnerShipId;

	@Expose
	@Column(name = "motarizedVehicleId")
	private Integer motarizedVehicleId;

	@Expose
	@Column(name = "other_availabilityofToilet")
	private String other_availabilityofToilet;

	@Expose
	@Column(name = "other_avalabilityofElectricity")
	private String other_avalabilityofElectricity;

	@Expose
	@Column(name = "other_fuelUsed")
	private String other_fuelUsed;

	@Expose
	@Column(name = "other_houseType")
	private String other_houseType;

	@Expose
	@Column(name = "other_motarizedVehicle")
	private String other_motarizedVehicle;

	@Expose
	@Column(name = "other_residentialArea")
	private String other_residentialArea;

	@Expose
	@Column(name = "other_sourceofDrinkingWater")
	private String other_sourceofDrinkingWater;

	@Expose
	@Column(name = "pincode")
	private Integer pincode;

	@Expose
	@Column(name = "rationCardDetails")
	private String rationCardDetails;

	@Expose
	@Column(name = "residentialArea")
	private String residentialArea;

	@Expose
	@Column(name = "residentialAreaId")
	private Integer residentialAreaId;

	@Expose
	@Column(name = "seperateKitchen")
	private String seperateKitchen;

	@Expose
	@Column(name = "seperateKitchenId")
	private Integer seperateKitchenId;

	@Expose
	@Column(name = "serverUpdatedStatus")
	private Integer serverUpdatedStatus;

	@Expose
	@Column(name = "sourceofDrinkingWater")
	private String sourceofDrinkingWater;

	@Expose
	@Column(name = "sourceofDrinkingWaterId")
	private Integer sourceofDrinkingWaterId;

	@Expose
	@Column(name = "state")
	private String state;

	@Expose
	@Column(name = "stateid")
	private Integer stateid;

	@Expose
	@Column(name = "street")
	private String street;

	@Expose
	@Column(name = "type_bpl_apl")
	private String type_bpl_apl;

	@Expose
	@Column(name = "village")
	private String village;

	@Expose
	@Column(name = "villageid")
	private Integer villageid;

	@Expose
	@Column(name = "Deleted")
	private Boolean deleted;

	@Expose
	@Column(name = "Processed")
	private String Processed = "N";

	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;

	@Expose
	@Column(name = "CreatedDate")
	private Timestamp createdDate;

	@Expose
	@Column(name = "Reserved")
	private Boolean reserved;

	@Expose
	@Column(name = "ReservedFor")
	private String reservedFor;

	@Expose
	@Column(name = "ReservedOn")
	private String reservedOn;

	@Expose
	@Column(name = "ReservedById")
	private Integer reservedById;

	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy;

	@Expose
	@Column(name = "LastModDate")
	private Timestamp lastModDate;

	@Expose
	@Column(name = "VanSerialNo")
	private Long id;

	@Expose
	@Column(name = "VanID")
	private Integer VanID;

	@Expose
	@Column(name = "VehicalNo")
	private String vehicalNo;

	@Expose
	@Column(name = "ParkingPlaceID")
	private Integer parkingPlaceID;

	@Expose
	@Column(name = "SyncedBy")
	private String syncedBy;

	@Expose
	@Column(name = "SyncedDate")
	private Timestamp syncedDate;

	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer ProviderServiceMapID;

	@Expose
	@Column(name = "deviceId")
	private Integer deviceId;

	@Expose
	@Column(name = "beneficiaryId")
	private Integer beneficiaryId;

	// new fields, 30-06-2021

	@Expose
	@Column(name = "MotarizedVehicle")
	private String motarizedVehicle;

	@Expose
	@Column(name = "LiveStockOwnerShip")
	private String liveStockOwnerShip;

	@Expose
	@Column(name = "LandOwned")
	private String landOwned;

	@Expose
	@Column(name = "Blockid")
	private Integer blockid;

	@Expose
	@Column(name = "landIrregated")
	private String landIrregated;

	// 19-09-2021
	@Expose
	@Column(name = "wardNo")
	private String wardNo;
	@Expose
	@Column(name = "wardName")
	private String wardName;
	@Expose
	@Column(name = "mohallaName")
	private String mohallaName;

	@Expose
	@Column(name = "isDeactivate")
	private Boolean isDeactivate;

}
