package com.hedvig.test.contract.premiuminsurance.model;

public enum EventType {
	CONTRACT_CREATED_EVENT("ContractCreatedEvent"), PREMIUM_INCREASED_EVENT("PriceIncreasedEvent"),
	PREMIUM_DECREASED_EVENT("PriceDecreasedEvent"), CONTRACT_TERMINATED_EVENT("ContractTerminatedEvent");

	private String eventName;

	EventType(String name) {
		this.eventName = name;
	}

	public String getEventName() {
		return eventName;
	}
}
