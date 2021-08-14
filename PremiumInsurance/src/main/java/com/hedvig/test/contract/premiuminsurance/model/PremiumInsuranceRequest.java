package com.hedvig.test.contract.premiuminsurance.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PremiumInsuranceRequest {
	@NotNull
	@Pattern(regexp = "ContractCreatedEvent|PriceDecreasedEvent|PriceIncreasedEvent|ContractTerminatedEvent", message = "Name input value is not acceptable one.")
	private String name;
	@NotNull
	@Pattern(regexp = "\\d+", message = "ContractId input should be number.")
	private String contractId;
	@JsonProperty("premium")
	@JsonAlias({ "premium", "premiumReduction", "premiumIncrease" })
	private Integer premium;
	@NotNull
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}", message = "Date format is not correct.")
	@JsonProperty("date")
	@JsonAlias({ "startDate", "atDate", "terminationDate" })
	private String date;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public Integer getPremium() {
		return premium;
	}

	public void setPremium(Integer premium) {
		this.premium = premium;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
