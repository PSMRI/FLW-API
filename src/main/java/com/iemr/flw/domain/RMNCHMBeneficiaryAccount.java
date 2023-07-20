package com.iemr.flw.domain;

import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "i_beneficiaryaccount", schema = "db_identity")
@Data
public class RMNCHMBeneficiaryAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BenAccountID")
	private BigInteger benAccountID;

	@Column(name = "BankName", length = 100)
	private String nameOfBank;

	@Column(name = "branchName", length = 100)
	private String branchName;

	@Column(name = "IFSCCode", length = 20)
	private String ifscCode;

	@Column(name = "AccountNo", length = 50)
	private String bankAccount;

	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted = false;

	@Column(name = "Processed")
	private String processed = "N";

	@Column(name = "CreatedBy", length = 50, updatable = false)
	private String createdBy;

	@Column(name = "CreatedDate", updatable = false)
	private Timestamp createdDate;

	@Column(name = "Reserved")
	private Boolean reserved;

	@Column(name = "ReservedFor", length = 45)
	private String reservedFor;

	@Column(name = "ReservedOn", length = 45)
	private String reservedOn;

	@Column(name = "ReservedById")
	private Integer reservedById;

	@Column(name = "ModifiedBy", length = 50)
	private String modifiedBy;

	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;

	@Column(name = "VehicalNo", length = 30)
	private String vehicleNo;

	@Column(name = "ReservedForChange", length = 30)
	private String reservedForChange;

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
