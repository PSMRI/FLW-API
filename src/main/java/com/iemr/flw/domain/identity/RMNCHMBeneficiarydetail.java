package com.iemr.flw.domain.identity;

import com.google.gson.annotations.Expose;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "i_beneficiarydetails", schema = "db_identity")
@Data
public class RMNCHMBeneficiarydetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private BigInteger beneficiaryDetailsId;

	private Integer areaId;

	@Column(name = "beneficiaryRegID")
	private BigInteger BenRegId;

	@Column(length = 45)
	private String community;

	@Column(nullable = false, length = 50, updatable = false)
	private String createdBy;

	@Column(name = "CreatedDate")
	private Timestamp createdDate;

	@Column(name = "dob")
	private Timestamp dob;

	private Boolean deleted = false;

	@Column(length = 45)
	private String education;

	private Boolean emergencyRegistration;

	@Column(length = 50)
	private String fatherName;

	@Column(length = 20)
	private String firstName;

	@Column(length = 50)
	private String motherName;

	private String gender;

	@Column(name = "incomeStatusId")
	private Integer incomeStatusId;

	@Column(length = 45)
	private String incomeStatus;

	private Timestamp lastModDate;

	@Column(length = 20)
	private String lastName;

	@Column(length = 20)
	private String maritalstatus;

	@Column(name = "marriageDate")
	private Timestamp marriageDate;

	@Transient
	private Integer ageAtMarriage;

	@Column(name = "MiddleName", length = 20)
	private String middleName;

	@Column(length = 50)
	private String modifiedBy;

	private Integer occupationId;

	@Column(length = 45)
	private String occupation;

	private Integer phcId;

	@Column(length = 30)
	private String placeOfWork;

	private String literacyStatus;

	private Integer preferredLanguageId;

	@Column(length = 45)
	private String preferredLanguage;

	@Column(nullable = false, length = 4)
	private String processed = "N";

	@Column(length = 45)
	private BigInteger religionID;

	@Column(length = 45)
	private String religion;

	@Column(length = 300)
	private String remarks;

	private Boolean reserved;

	private Integer reservedById;

	@Column(length = 45)
	private String reservedFor;

	private Timestamp reservedOn;

	private BigInteger servicePointId;

	@Column(length = 45)
	private String sourceOfInfo;

	@Column(length = 50)
	private String spousename;

	@Column(length = 10)
	private String status;

	@Column(length = 10)
	private String title;

	private Integer zoneId;

	@Column(name = "GenderID")
	private Integer genderId;

	@Column(name = "MaritalStatusID")
	private Integer maritalstatusId;

	@Column(name = "TitleID")
	private Integer titleId;

	@Column(name = "CommunityID")
	private Integer communityId;

	@Column(name = "EducationID")
	private Integer educationId;

	@Column(name = "SexualOrientationID")
	private Integer sexualOrientationID;

	@Column(name = "SexualOrientationType")
	private String sexualOrientationType;

	@Column(name = "HIVStatus")
	private Integer isHIVPositive;

	@Column(name = "HealthCareWorkerId")
	private Integer healthCareWorkerId;

	private String healthCareWorker;

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

	@Transient
	private BigInteger benficieryid;

	@Expose
	@Transient
	private Integer ProviderServiceMapID;

}
