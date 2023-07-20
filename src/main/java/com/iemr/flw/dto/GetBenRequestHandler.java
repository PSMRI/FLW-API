package com.iemr.flw.dto;

import java.sql.Timestamp;

public class GetBenRequestHandler {
	private Integer villageID;
	private Timestamp fromDate;
	private Timestamp toDate;
	private Integer pageNo;
	private Integer userId;
	private String userName;

	private Integer AshaId;

	public Integer getAshaId() {
		return AshaId;
	}

	public void setAshaId(Integer ashaId) {
		AshaId = ashaId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getVillageID() {
		return villageID;
	}

	public void setVillageID(Integer villageID) {
		this.villageID = villageID;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public Timestamp getToDate() {
		return toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

}
