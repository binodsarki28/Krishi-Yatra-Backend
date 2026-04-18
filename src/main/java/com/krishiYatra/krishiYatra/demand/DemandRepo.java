package com.krishiYatra.krishiYatra.demand;

import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;

@Repository
public interface DemandRepo extends JpaRepository<DemandEntity, String> {
    long countByBuyer(BuyerEntity buyer);

    long countByAcceptedBy(FarmerEntity acceptedBy);

    long countByActiveTrue();
}
