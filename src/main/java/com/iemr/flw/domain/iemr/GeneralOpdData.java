/*
* AMRIT – Accessible Medical Records via Integrated Technology 
* Integrated EHR (Electronic Health Records) Solution 
*
* Copyright (C) "Piramal Swasthya Management and Research Institute" 
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
* @@ -0,0 +1,212 @@
/*
* AMRIT – Accessible Medical Records via Integrated Technology
*/

package com.iemr.flw.domain.iemr;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.google.gson.annotations.Expose;
import lombok.Data;

@Data
@Entity
@Table(name = "i_ben_flow_outreach")
public class GeneralOpdData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "ben_flow_id")
	private Long benFlowID;
   
	@Expose
	@Column(name = "beneficiary_reg_id")
	private Long beneficiaryRegID;

	@Expose
	@Column(name = "beneficiary_visit_id")
	private Long benVisitID;

	@Expose
	@Column(name = "beneficiary_visit_code")
	private Long visitCode;

	@Expose
	@Column(name = "visit_reason")
	private String VisitReason;

	@Expose
	@Column(name = "visit_category")
	private String VisitCategory;

	@Expose
	@Column(name = "visit_no")
	private Short benVisitNo;

	@Expose
	@Column(name = "nurse_flag")
	private Short nurseFlag;

	@Expose
	@Column(name = "doctor_flag")
	private Short doctorFlag;

	@Expose
	@Column(name = "pharmacist_flag")
	private Short pharmacist_flag;

	@Expose
	@Column(name = "lab_technician_flag")
	private Short lab_technician_flag;

	@Expose
	@Column(name = "radiologist_flag")
	private Short radiologist_flag;

	@Expose
	@Column(name = "oncologist_flag")
	private Short oncologist_flag;

	@Expose
	@Column(name = "specialist_flag")
	private Short specialist_flag;

	@Expose
	@Column(name = "TC_SpecialistLabFlag")
	private Short tC_SpecialistLabFlag;

	@Expose
	@Column(name = "created_by")
	private String agentId;

	@Expose
	@Column(name = "created_date", insertable = false, updatable = false)
	private Timestamp visitDate;

	@Expose
	@Column(name = "modified_by")
	private String modified_by;

	@Expose
	@Column(name = "modified_date", insertable = false)
	private Timestamp modified_date;

	@Expose
	@Column(name = "ben_name")
	private String benName;

	@Expose
	@Column(name = "deleted", insertable = false)
	private Boolean deleted;

	@Transient
	private String firstName;
	@Transient
	private String lastName;

	@Expose
	@Column(name = "ben_age")
	private String age;

	@Expose
	@Column(name = "ben_age_val")
	private Integer ben_age_val;

	@Expose
	@Column(name = "ben_dob")
	private Timestamp dOB;

	@Expose
	@Column(name = "ben_gender_val")
	private Short genderID;
	@Expose
	@Column(name = "ben_gender")
	private String genderName;
	@Expose
	@Column(name = "ben_phone_no")
	private String preferredPhoneNum;
	@Expose
	@Column(name = "father_name")
	private String fatherName;
//	@Expose
//	@Column(name = "benQuickbloxID")
//	private Long benQuickbloxID;
	@Expose
	@Column(name = "spouse_name")
	private String spouseName;

	@Expose
	@Column(name = "district")
	private String districtName;
	@Expose
	@Column(name = "servicePoint")
	private String servicePointName;

	@Expose
	@Column(name = "registrationDate")
	private Timestamp registrationDate;

	@Expose
	@Column(name = "visitDate")
	private Timestamp benVisitDate;

	@Expose
	@Column(name = "consultationDate")
	private Timestamp consultationDate;

	@Expose
	@Column(name = "consultantID")
	private Integer consultantID;

	@Expose
	@Column(name = "consultantName")
	private String consultantName;

	@Expose
	@Column(name = "visitSession")
	private Integer visitSession;

	@Expose
	@Column(name = "servicePointID")
	private Integer servicePointID;

	@Expose
	@Column(name = "districtID")
	private Integer districtID;

	@Expose
	@Column(name = "villageID")
	private Integer villageID;

	@Expose
	@Column(name = "vanID")
	private Integer vanID;

	@Expose
	@Column(name = "beneficiary_id")
	private Long beneficiaryId;

	@Expose
	@Column(name = "village")
	private String  village;


}
