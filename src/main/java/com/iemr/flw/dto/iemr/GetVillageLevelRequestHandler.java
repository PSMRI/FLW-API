package com.iemr.flw.dto.iemr;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class GetVillageLevelRequestHandler {
	private String formType;
	private Integer villageID;
	private Timestamp fromDate;
	private Timestamp toDate;
	private Integer pageNo;
	private Integer userId;
	private String userName;
	private Integer ashaId;



}