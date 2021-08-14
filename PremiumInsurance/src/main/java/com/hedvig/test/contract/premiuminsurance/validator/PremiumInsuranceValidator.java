package com.hedvig.test.contract.premiuminsurance.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.hedvig.test.contract.premiuminsurance.dao.PremiumContractRepository;
import com.hedvig.test.contract.premiuminsurance.entity.PremiumContract;
import com.hedvig.test.contract.premiuminsurance.exception.BusinessValidationException;
import com.hedvig.test.contract.premiuminsurance.model.PremiumInsuranceRequest;
import com.hedvig.test.contract.premiuminsurance.model.EventType;

@Component
public class PremiumInsuranceValidator {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	private PremiumContractRepository premiumContractRepository;

	public void validatePremiumRequest(PremiumInsuranceRequest request) {
		if (EventType.CONTRACT_CREATED_EVENT.getEventName().equals(request.getName())) {
			if (premiumContractRepository.findByContractId(Integer.parseInt(request.getContractId())) != null) {
				throw new BusinessValidationException(HttpStatus.BAD_REQUEST, "Contract is already exists. ContractId "+request.getContractId());
			}
		} else {
			PremiumContract contract = premiumContractRepository
					.findByContractId(Integer.parseInt(request.getContractId()));
			if (contract == null) {
				throw new BusinessValidationException(HttpStatus.NOT_FOUND, "Contract is not available. ContractId "+request.getContractId());
			} else if (contract.getEndDate() != null) {
				throw new BusinessValidationException(HttpStatus.BAD_REQUEST, "Contract is already terminated. ContractId "+request.getContractId());
			} else if (EventType.PREMIUM_DECREASED_EVENT.getEventName().equals(request.getName())
					&& contract.getCurrentPremiumAmount() < request.getPremium()) {
				throw new BusinessValidationException(HttpStatus.BAD_REQUEST,
						"PriceDecreasedEvent amount is greater than available preminum amount. ContractId "+request.getContractId());
			}
		}
		// Common validations
		if(!EventType.CONTRACT_TERMINATED_EVENT.getEventName().equals(request.getName()) && request.getPremium() <= 0) {
			throw new BusinessValidationException(HttpStatus.BAD_REQUEST, "Invalid preminum amount. ContractId "+request.getContractId());
		}
		try {
			DATE_FORMAT.parse(request.getDate());
		} catch (ParseException e1) {
			throw new BusinessValidationException(HttpStatus.BAD_REQUEST, "Invalid date. ContractId "+request.getContractId());
		}
	}
}
