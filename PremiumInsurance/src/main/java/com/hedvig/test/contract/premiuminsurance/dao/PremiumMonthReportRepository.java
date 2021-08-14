package com.hedvig.test.contract.premiuminsurance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.hedvig.test.contract.premiuminsurance.entity.PremiumMonthReport;

public interface PremiumMonthReportRepository extends CrudRepository<PremiumMonthReport, Integer> {

	List<PremiumMonthReport> findByContractId(Integer contractId);

	@Query("select date_format(premiumMonth,'%b') as month, (select count(activeFlag) from PremiumMonthReport r1 "
			+ "where r1.premiumMonth=r.premiumMonth and activeFlag='Y') as contractsCount, sum(agwp) as agwp, sum(egwp) "
			+ "as egwp from PremiumMonthReport r where year(premiumMonth) = :year group by premiumMonth order by premiumMonth")
	List<PremiumMonthMetricsReport> getAllMetricsByYear(@Param("year") int year);

}
