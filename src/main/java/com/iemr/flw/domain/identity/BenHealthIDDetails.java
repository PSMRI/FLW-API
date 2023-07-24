package com.iemr.flw.domain.identity;

public class BenHealthIDDetails {

	Integer benHealthID;

	String healthIdNumber;

	private Integer providerServiceMapId;

	private Long beneficiaryRegID;

	private Long beneficiaryID;

	String healthId;

	String authenticationMode;

	public String getAuthenticationMode() {
		return authenticationMode;
	}

	public void setAuthenticationMode(String authenticationMode) {
		this.authenticationMode = authenticationMode;
	}

	public Integer getBenHealthID() {
		return benHealthID;
	}

	public void setBenHealthID(Integer benHealthID) {
		this.benHealthID = benHealthID;
	}

	public String getHealthIdNumber() {
		return healthIdNumber;
	}

	public void setHealthIdNumber(String healthIdNumber) {
		this.healthIdNumber = healthIdNumber;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapId;
	}

	public void setProviderServiceMapID(Integer providerServiceMapID) {
		this.providerServiceMapId = providerServiceMapID;
	}

	public Long getBeneficiaryRegId() {
		return beneficiaryRegID;
	}

	public void setBeneficiaryRegId(Long beneficiaryRegId) {
		this.beneficiaryRegID = beneficiaryRegId;
	}

	public String getHealthId() {
		return healthId;
	}

	public void setHealthId(String healthId) {
		this.healthId = healthId;
	}

	public Long getBeneficiaryID() {
		return beneficiaryID;
	}

	public void setBeneficiaryID(Long beneficiaryID) {
		this.beneficiaryID = beneficiaryID;
	}

}
