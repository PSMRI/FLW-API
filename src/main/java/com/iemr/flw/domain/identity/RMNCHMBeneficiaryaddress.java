package com.iemr.flw.domain.identity;

import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "i_beneficiaryaddress", schema = "db_identity")
@Data
public class RMNCHMBeneficiaryaddress implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	private BigInteger benAddressID;

	@Column(nullable = false, length = 50)
	private String createdBy;

	@Column(name = "CreatedDate", updatable = false)
	private Timestamp createdDate;

	@Column(length = 50)
	private String currAddrLine1;

	@Column(length = 50)
	private String currAddrLine2;

	@Column(length = 50)
	private String currAddrLine3;

	@Column(length = 15)
	private String currCountry;

	private Integer currCountryId;

	@Column(name = "currDistrict")
	private String districtname;

	@Column(name = "currDistrictId")
	private Integer districtid;

	@Column(length = 250)
	private String currAddressValue;

	private String currPinCode;

	@Column(length = 30)
	private String currState;

	@Column(name = "currStateId")
	private Integer stateid;

	@Column(length = 50)
	private String currSubDistrict;

	private Integer currSubDistrictId;

	@Column(name = "currVillage")
	private String villagename;

	private String currHabitation;

	@Column(name = "currVillageId")
	private Integer villageid;

	private Boolean deleted = false;

	@Column(length = 50)
	private String emerAddrLine1;

	@Column(length = 50)
	private String emerAddrLine2;

	@Column(length = 50)
	private String emerAddrLine3;

	@Column(length = 15)
	private String emerCountry;

	private Integer emerCountryId;

	@Column(length = 50)
	private String emerDistrict;

	private Integer emerDistrictId;

	@Column(length = 250)
	private String emerAddressValue;

	private String emerPinCode;

	@Column(length = 30)
	private String emerState;

	private Integer emerStateId;

	@Column(length = 50)
	private String emerSubDistrict;

	private Integer emerSubDistrictId;

	@Column(length = 100)
	private String emerVillage;

	private String emerHabitation;

	private Integer emerVillageId;

	private Timestamp lastModDate;

	@Column(length = 50)
	private String modifiedBy;

	@Column(length = 50)
	private String permAddrLine1;

	@Column(length = 50)
	private String permAddrLine2;

	@Column(length = 50)
	private String permAddrLine3;

	@Column(length = 250)
	private String permAddressValue;

	@Column(length = 15)
	private String permCountry;

	@Column(name = "PermCountryId")
	private Integer Countyid;

	@Column(length = 50, name = "PermDistrict")
	private String districtnamePerm;

	@Column(name = "PermDistrictId")
	private Integer districtidPerm;

	private String permPinCode;

	@Column(length = 30)
	private String permState;

	@Column(name = "PermStateId")
	private Integer statePerm;

	@Column(length = 50)
	private String permSubDistrict;

	private Integer permSubDistrictId;

	@Column(length = 100, name = "PermVillage")
	private String villagenamePerm;

	private String permHabitation;

	@Column(name = "PermVillageId")
	private Integer villageidPerm;

	@Column(name = "PermZoneID")
	private Integer permZoneID;

	@Column(name = "PermZone")
	private String permZone;

	@Column(name = "PermAreaId")
	private Integer permAreaId;

	@Column(name = "PermArea")
	private String permArea;

	@Column(name = "PermServicePointId")
	private Integer permServicePointId;

	@Column(name = "PermServicePoint")
	private String permServicePoint;

	@Column(name = "CurrZoneID")
	private Integer currZoneID;

	@Column(name = "CurrZone")
	private String currZone;

	@Column(name = "CurrAreaId")
	private Integer currAreaId;

	@Column(name = "CurrArea")
	private String currArea;

	@Column(name = "CurrServicePointId")
	private Integer currServicePointId;

	@Column(name = "CurrServicePoint")
	private String currServicePoint;

	@Column(name = "EmerZoneID")
	private Integer emerZoneID;

	@Column(name = "EmerZone")
	private String emerZone;

	@Column(name = "EmerAreaId")
	private Integer emerAreaId;

	@Column(name = "EmerArea")
	private String emerArea;

	@Column(name = "EmerServicePointId")
	private Integer emerServicePointId;

	@Column(name = "EmerServicePoint")
	private String emerServicePoint;

	@Column(nullable = false, length = 4)
	private String processed = "N";

	private Boolean reserved;

	private Integer reservedById;

	@Column(length = 45)
	private String reservedFor;

	private Timestamp reservedOn;

	@Expose
	@Column(name = "vanID", updatable = false)
	private Integer VanID;

	@Expose
	@Column(name = "parkingPlaceID", updatable = false)
	private Integer parkingPlaceID;

	@Expose
	@Column(name = "VanSerialNo", updatable = false)
	private BigInteger id;

	@Expose
	@Column(name = "SyncedBy")
	private String SyncedBy;

	@Expose
	@Column(name = "SyncedDate")
	private Timestamp SyncedDate;
}
