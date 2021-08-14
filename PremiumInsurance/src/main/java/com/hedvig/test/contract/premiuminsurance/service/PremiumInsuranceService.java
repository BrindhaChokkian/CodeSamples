package com.hedvig.test.contract.premiuminsurance.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hedvig.test.contract.premiuminsurance.dao.PremiumContractRepository;
import com.hedvig.test.contract.premiuminsurance.dao.PremiumHistoryRepository;
import com.hedvig.test.contract.premiuminsurance.dao.PremiumMonthMetricsReport;
import com.hedvig.test.contract.premiuminsurance.dao.PremiumMonthReportRepository;
import com.hedvig.test.contract.premiuminsurance.entity.PremiumContract;
import com.hedvig.test.contract.premiuminsurance.entity.PremiumInsuranceHistory;
import com.hedvig.test.contract.premiuminsurance.entity.PremiumMonthReport;
import com.hedvig.test.contract.premiuminsurance.model.PremiumInsuranceRequest;
import com.hedvig.test.contract.premiuminsurance.model.PremiumMonthReportRequest;
import com.hedvig.test.contract.premiuminsurance.validator.PremiumInsuranceValidator;
import com.hedvig.test.contract.premiuminsurance.model.EventType;

@Service
public class PremiumInsuranceService {
	private static final Logger LOG = LogManager.getLogger(PremiumInsuranceService.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String ACTIVE_FLAG = "Y";
	private static final String INACTIVE_FLAG = "N";

	@Autowired
	private PremiumHistoryRepository premiumHistoryRepository;
	@Autowired
	private PremiumContractRepository premiumContractRepository;
	@Autowired
	private PremiumMonthReportRepository premiumMonthReportRepository;
	@Autowired
	private PremiumInsuranceValidator premiumInsuranceValidator;
	
	public String getPremiumMetricsReportByYear(PremiumMonthReportRequest request) {
		List<PremiumMonthMetricsReport> metrics = premiumMonthReportRepository.getAllMetricsByYear(Integer.parseInt(request.getYear()));
		StringBuilder sb = new StringBuilder();
		int maxSize = 20;
		String splitChar = " ";
		sb.append(rightPad(maxSize, "MONTH", splitChar));
		sb.append(rightPad(maxSize, "NO_OF_CONTRACTS", splitChar));
		sb.append(rightPad(maxSize, "AGWP", splitChar));
		sb.append(rightPad(maxSize, "EGWP", splitChar)).append("\n");
		metrics.stream().forEach(x -> {
			sb.append(rightPad(maxSize, x.getMonth(), splitChar));
			sb.append(rightPad(maxSize, x.getContractsCount(), splitChar));
			sb.append(rightPad(maxSize, x.getAgwp(), splitChar));
			sb.append(rightPad(maxSize, x.getEgwp(), splitChar)).append("\n");
		});
		return sb.toString();
	}
	
	private static String rightPad(int length, String str, String splitChar) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(splitChar);
		}
		if (str.length() < length) {
			return str+sb.substring(str.length());
		} else {
			return str;
		}
	}

	public void createPremiumRequest(PremiumInsuranceRequest request) {
		//Validate Request
		premiumInsuranceValidator.validatePremiumRequest(request);
		if (EventType.CONTRACT_CREATED_EVENT.getEventName().equals(request.getName())) {
			createPremiumInsuranceRequest(request);
			
		} else if (EventType.PREMIUM_INCREASED_EVENT.getEventName().equals(request.getName())
				|| EventType.PREMIUM_DECREASED_EVENT.getEventName().equals(request.getName())
				|| EventType.CONTRACT_TERMINATED_EVENT.getEventName().equals(request.getName())) {
			updatePremiumInsuranceRequest(request);
		}
	}
	
	public void createPremiumInsuranceRequest(PremiumInsuranceRequest request) {

		// Create entry in History Table
		PremiumInsuranceHistory premiumInsuranceHistory = getPremiumInsuranceHistory(request);
		premiumInsuranceHistory = premiumHistoryRepository.save(premiumInsuranceHistory);
		LOG.debug(request.getName() + " PremiumInsuranceHistory created for " + premiumInsuranceHistory.getId());
		
		// Create Contract details
		PremiumContract premiumContract = getPremiumContract(premiumInsuranceHistory);
		premiumContract = premiumContractRepository.save(premiumContract);
		
		// Create Month-wise metrics
		Calendar cal = Calendar.getInstance();
		cal.setTime(premiumInsuranceHistory.getDate());
		int month = cal.get(Calendar.MONTH);
		int prevAgwp = 0;
		List<PremiumMonthReport> premiumMonthReportList = new ArrayList<PremiumMonthReport>();
		for (int i = month, j = 0; j < 12; i++, j++) {
			cal.set(Calendar.MONTH, i);
			PremiumMonthReport premiumMonthReport = getPremiumMonthReport(premiumInsuranceHistory, cal, prevAgwp);
			premiumMonthReportList.add(premiumMonthReport);
			prevAgwp += premiumMonthReport.getCurrentPremiumAmount();
		}
		premiumMonthReportRepository.saveAll(premiumMonthReportList);
	
	}
	
	public void updatePremiumInsuranceRequest(PremiumInsuranceRequest request) {

		// Create entry in History Table
		PremiumInsuranceHistory premiumInsuranceHistory = getPremiumInsuranceHistory(request);
		premiumInsuranceHistory = premiumHistoryRepository.save(premiumInsuranceHistory);
		LOG.debug(request.getName() + " PremiumInsuranceHistory created for " + premiumInsuranceHistory.getId());
				
		// Update Contract details
		PremiumContract existingContract = premiumContractRepository.findByContractId(Integer.parseInt(request.getContractId()));
		if (EventType.PREMIUM_INCREASED_EVENT.getEventName().equals(request.getName())) {
			existingContract.setCurrentPremiumAmount(existingContract.getCurrentPremiumAmount() + request.getPremium());
			
		} else if (EventType.PREMIUM_DECREASED_EVENT.getEventName().equals(request.getName())) {
			existingContract.setCurrentPremiumAmount(existingContract.getCurrentPremiumAmount() - request.getPremium());
			
		} else if (EventType.CONTRACT_TERMINATED_EVENT.getEventName().equals(request.getName())) {
			existingContract.setCurrentPremiumAmount(0);
			existingContract.setEndDate(premiumInsuranceHistory.getDate());
		}
		premiumContractRepository.save(existingContract);
		
		// Update Month-wise metrics
		List<PremiumMonthReport> existingMonthReport = premiumMonthReportRepository
				.findByContractId(Integer.parseInt(request.getContractId()));
		if (existingMonthReport != null && !existingMonthReport.isEmpty()) {
			updateMetricsForPremiumChange(request, premiumInsuranceHistory, existingMonthReport);
		}

	}

	private void updateMetricsForPremiumChange(PremiumInsuranceRequest request,
			PremiumInsuranceHistory premiumInsuranceHistory, List<PremiumMonthReport> existingMonthReport) {
		Date priceChangeDate = premiumInsuranceHistory.getDate();
		Calendar cal = Calendar.getInstance();
		cal.setTime(priceChangeDate);
		int reqMonth = cal.get(Calendar.MONTH);
		int prevMonthAgwp = 0;

		Map<Boolean, List<PremiumMonthReport>> splitMonthReport = existingMonthReport.stream()
				.collect(Collectors.partitioningBy(s -> s.getPremiumMonth().getTime() >= priceChangeDate.getTime()));

		List<PremiumMonthReport> prevMonths = splitMonthReport.get(false);
		if (prevMonths != null && !prevMonths.isEmpty()) {
			PremiumMonthReport prevMonth = Collections.max(prevMonths, Comparator.comparing(PremiumMonthReport::getPremiumMonth));
			prevMonthAgwp = prevMonth.getAgwp();
			
			//Update EGWP for Termination request, if Month-end date given.
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(prevMonth.getPremiumMonth());
			int prevMonthDate = cal1.get(Calendar.MONTH);
			if (reqMonth == prevMonthDate && EventType.CONTRACT_TERMINATED_EVENT.getEventName().equals(request.getName())) {
				//prevMonth.setEgwp(prevMonth.getAgwp());
				//premiumMonthReportRepository.save(prevMonth);
			}
		}

		List<PremiumMonthReport> monthsToUpdate = splitMonthReport.get(true);
		if (monthsToUpdate != null && !monthsToUpdate.isEmpty()) {
			// Sort the months list to update AGWP and EGWP
			monthsToUpdate = monthsToUpdate.stream().sorted(Comparator.comparing(PremiumMonthReport::getPremiumMonth))
					.collect(Collectors.toList());
			for (PremiumMonthReport currMonth : monthsToUpdate) {
				if (EventType.PREMIUM_INCREASED_EVENT.getEventName().equals(request.getName())) {
					currMonth.setCurrentPremiumAmount(currMonth.getCurrentPremiumAmount() + request.getPremium());
					
				} else if (EventType.PREMIUM_DECREASED_EVENT.getEventName().equals(request.getName())) {
					currMonth.setCurrentPremiumAmount(currMonth.getCurrentPremiumAmount() - request.getPremium());
					
				} else if (EventType.CONTRACT_TERMINATED_EVENT.getEventName().equals(request.getName())) {
					currMonth.setCurrentPremiumAmount(0);
					currMonth.setActiveFlag(INACTIVE_FLAG);
				}
				
				currMonth.setAgwp(currMonth.getCurrentPremiumAmount() + prevMonthAgwp);
				currMonth.setEgwp((currMonth.getCurrentPremiumAmount() * (12 - reqMonth)) + prevMonthAgwp);
				prevMonthAgwp = currMonth.getAgwp();
				reqMonth++;
			}
			premiumMonthReportRepository.saveAll(monthsToUpdate);
		}
	}

	private PremiumMonthReport getPremiumMonthReport(PremiumInsuranceHistory premiumInsuranceHistory, Calendar cal, int agwp) {
		PremiumMonthReport premiumMonthReport = new PremiumMonthReport();
		premiumMonthReport.setContractId(premiumInsuranceHistory.getContractId());
		premiumMonthReport.setActiveFlag(ACTIVE_FLAG);
		premiumMonthReport.setCurrentPremiumAmount(premiumInsuranceHistory.getPremium());
		premiumMonthReport.setPremiumMonth(cal.getTime());
		premiumMonthReport.setAgwp(agwp + premiumInsuranceHistory.getPremium());
		premiumMonthReport.setEgwp(premiumInsuranceHistory.getPremium() * 12);
		return premiumMonthReport;
	}

	private PremiumInsuranceHistory getPremiumInsuranceHistory(PremiumInsuranceRequest request) {
		PremiumInsuranceHistory premiumInsuranceHistory = new PremiumInsuranceHistory();
		premiumInsuranceHistory.setName(request.getName());
		premiumInsuranceHistory.setContractId(Integer.parseInt(request.getContractId()));
		premiumInsuranceHistory.setPremium(request.getPremium());
		try {
			premiumInsuranceHistory.setDate(DATE_FORMAT.parse(request.getDate()));
		} catch (Exception e) {
			LOG.error(e);
		}
		return premiumInsuranceHistory;
	}

	private PremiumContract getPremiumContract(PremiumInsuranceHistory request) {
		PremiumContract premiumContract = new PremiumContract();
		premiumContract.setContractId(request.getContractId());
		premiumContract.setCurrentPremiumAmount(request.getPremium());
		premiumContract.setStartDate(request.getDate());
		return premiumContract;
	}
	
}
