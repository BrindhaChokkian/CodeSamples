package com.hedvig.test.contract.premiuminsurance.dao;

import org.springframework.data.repository.CrudRepository;

import com.hedvig.test.contract.premiuminsurance.entity.PremiumContract;

public interface PremiumContractRepository extends CrudRepository<PremiumContract, Integer>{
	PremiumContract findByContractId(Integer contractId);

}
