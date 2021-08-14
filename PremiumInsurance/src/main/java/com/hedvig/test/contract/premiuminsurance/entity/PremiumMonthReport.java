package com.hedvig.test.contract.premiuminsurance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(name = "premium_month_report")
public class PremiumMonthReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "contract_id")
	private int contractId;
	@Column(name = "active_flag")
	private String activeFlag;
	@Column(name = "current_premium_amount")
	private int currentPremiumAmount;
	@Column(name = "premium_month")
	@Temporal(TemporalType.DATE)
	private Date premiumMonth;
	private int agwp;
	private int egwp;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getContractId() {
		return contractId;
	}
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	public String getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}
	public int getCurrentPremiumAmount() {
		return currentPremiumAmount;
	}
	public void setCurrentPremiumAmount(int currentPremiumAmount) {
		this.currentPremiumAmount = currentPremiumAmount;
	}
	public Date getPremiumMonth() {
		return premiumMonth;
	}
	public void setPremiumMonth(Date premiumMonth) {
		this.premiumMonth = premiumMonth;
	}
	public int getAgwp() {
		return agwp;
	}
	public void setAgwp(int agwp) {
		this.agwp = agwp;
	}
	public int getEgwp() {
		return egwp;
	}
	public void setEgwp(int egwp) {
		this.egwp = egwp;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
