package com.iemr.flw.domain.identity;

import com.google.gson.annotations.Expose;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "i_bornbirthdeatils", schema = "db_identity")
@Data
public class RMNCHBornBirthDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "BornBirthDeatilsId", insertable = false, updatable = false)
	private Long bornBirthDeatilsId;

	@Expose
	@Column(name = "BeneficiaryRegID")
	private Long BenRegId;

	@Expose
	@Column(name = "beneficiaryId")
	private BigInteger beneficiaryId;

	@Expose
	@Column(name = "countryid")
	private Integer Countyid;

	@Expose
	@Column(name = "bcdBatchNo")
	private String bcdBatchNo;

	@Expose
	@Column(name = "bcgDate")
	private Timestamp bcgDate;

	@Expose
	@Column(name = "bcgGivenDueDate")
	private Timestamp bcgGivenDueDate;

	@Expose
	@Column(name = "birthDefects")
	private String birthDefects;

	@Expose
	@Column(name = "birthDefectsOthers")
	private String birthDefectsOthers;

	@Expose
	@Column(name = "birthDefectsid")
	private Integer birthDefectsid;

	@Expose
	@Column(name = "birthDosage")
	private String birthDosage;

	@Expose
	@Column(name = "birthDosageid")
	private Integer birthDosageid;

	@Expose
	@Column(name = "birthPlace")
	private String birthPlace;

	@Expose
	@Column(name = "birthPlaceid")
	private Integer birthPlaceid;

	@Expose
	@Column(name = "childName")
	private String childName;

	@Expose
	@Column(name = "complecations")
	private String complecations;

	@Expose
	@Column(name = "complecationsid")
	private Integer complecationsid;

	@Expose
	@Column(name = "conductedDelivery")
	private String conductedDelivery;

	@Expose
	@Column(name = "conductedDeliveryOther")
	private String conductedDeliveryOther;

	@Expose
	@Column(name = "conductedDeliveryid")
	private Integer conductedDeliveryid;

	@Expose
	@Column(name = "corticosteroidGivenMother")
	private String corticosteroidGivenMother;

	@Expose
	@Column(name = "corticosteroidGivenMotherid")
	private Integer corticosteroidGivenMotherid;

	@Expose
	@Column(name = "criedImmediately")
	private String criedImmediately;

	@Expose
	@Column(name = "criedImmediatelyid")
	private Integer criedImmediatelyid;

	@Expose
	@Column(name = "deliveryType")
	private String deliveryType;

	@Expose
	@Column(name = "deliveryTypeOther")
	private String deliveryTypeOther;

	@Expose
	@Column(name = "deliveryTypeid")
	private Integer deliveryTypeid;

	@Expose
	@Column(name = "facilityName")
	private String facilityName;

	@Expose
	@Column(name = "facilityOther")
	private String facilityOther;

	@Expose
	@Column(name = "facilityid")
	private Integer facilityid;

	@Expose
	@Column(name = "feedingStarted")
	private String feedingStarted;

	@Expose
	@Column(name = "feedingStartedid")
	private Integer feedingStartedid;

	@Expose
	@Column(name = "gestationalAge")
	private String gestationalAge;

	@Expose
	@Column(name = "gestationalAgeid")
	private Integer gestationalAgeid;

	@Expose
	@Column(name = "heightAtBirth")
	private Integer heightAtBirth;

	@Expose
	@Column(name = "hptDate")
	private Timestamp hptDate;

	@Expose
	@Column(name = "hptGivenDueDate")
	private Timestamp hptGivenDueDate;

	@Expose
	@Column(name = "hptdBatchNo")
	private String hptdBatchNo;

	@Expose
	@Column(name = "hptdBatchID")
	private Integer hptdBatchID;

	@Expose
	@Column(name = "opvBatchNo")
	private String opvBatchNo;

	@Expose
	@Column(name = "opvDate")
	private Timestamp opvDate;

	@Expose
	@Column(name = "opvGivenDueDate")
	private Timestamp opvGivenDueDate;

	@Expose
	@Column(name = "placeName")
	private String placeName;

	@Expose
	@Column(name = "serverUpdatedStatus")
	private Integer serverUpdatedStatus;

	@Expose
	@Column(name = "term")
	private String term;

	@Expose
	@Column(name = "termid")
	private Integer termid;

	@Expose
	@Column(name = "vitaminkBatchNo")
	private String vitaminkBatchNo;

	@Expose
	@Column(name = "vitaminkDate")
	private Timestamp vitaminkDate;

	@Expose
	@Column(name = "vitaminkGivenDueDate")
	private Timestamp vitaminkGivenDueDate;

	@Expose
	@Column(name = "weightAtBirth")
	private BigDecimal weightAtBirth;

	@Expose
	@Column(name = "stateid")
	private Integer stateid;

	@Expose
	@Column(name = "districtid")
	private Integer districtid;

	@Expose
	@Column(name = "villageid")
	private Integer villageid;

	@Expose
	@Column(name = "Deleted")
	private Boolean Deleted;

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
	@Column(name = "ComplicationsOther")
	private String complicationsOther;

	@Expose
	@Column(name = "birthBCG")
	private Boolean birthBCG;

	@Expose
	@Column(name = "birthHepB")
	private Boolean birthHepB;

	@Expose
	@Column(name = "birthOPV")
	private Boolean birthOPV;

	@Expose
	@Column(name = "birthCertificateFileFrontView")
	private String birthCertificateFileFrontView;

	@Expose
	@Column(name = "birthCertificateFileBackView")
	private String birthCertificateFileBackView;





}
