package com.hedvig.test.contract.premiuminsurance.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hedvig.test.contract.premiuminsurance.model.PremiumMonthReportRequest;
import com.hedvig.test.contract.premiuminsurance.service.PremiumInsuranceService;

@RestController
@RequestMapping("/api/v1/premiumreport")
public class PremiumMonthReportRestController {
	@Autowired
	private PremiumInsuranceService service;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> generateMonthwiseReport(
			@Validated @RequestBody(required = true) PremiumMonthReportRequest request) {
		String result = service.getPremiumMetricsReportByYear(request);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
}
