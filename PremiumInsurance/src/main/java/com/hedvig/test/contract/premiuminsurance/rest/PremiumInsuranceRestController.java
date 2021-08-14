package com.hedvig.test.contract.premiuminsurance.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hedvig.test.contract.premiuminsurance.model.PremiumInsuranceRequest;
import com.hedvig.test.contract.premiuminsurance.service.PremiumInsuranceService;

@RestController
@RequestMapping("/api/v1/premiuminsurance")
public class PremiumInsuranceRestController {
	@Autowired
	private PremiumInsuranceService service;

	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> createPremiumInsuranceDetails(
			@Validated @RequestBody(required = true) PremiumInsuranceRequest request) {
		service.createPremiumRequest(request);
		return ResponseEntity.status(HttpStatus.OK).body(Boolean.TRUE);
	}
	
	@RequestMapping(path = "/bulkrequest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> createBulkPremiumInsuranceDetails(
			@Validated @RequestBody(required = true) List<PremiumInsuranceRequest> requestList) {
		if(requestList != null) {
			for(PremiumInsuranceRequest request: requestList) {
				service.createPremiumRequest(request);
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(Boolean.TRUE);
	}
	
}
