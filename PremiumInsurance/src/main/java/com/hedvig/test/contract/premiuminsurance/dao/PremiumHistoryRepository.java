package com.hedvig.test.contract.premiuminsurance.dao;

import org.springframework.data.repository.CrudRepository;

import com.hedvig.test.contract.premiuminsurance.entity.PremiumInsuranceHistory;

public interface PremiumHistoryRepository extends CrudRepository<PremiumInsuranceHistory, Integer>{

}
