package com.krishiYatra.krishiYatra.demand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.common.enums.DemandStatus;

@Repository
public interface DemandRepo extends JpaRepository<DemandEntity, String> {
    // For Buyer Dashboard
    long countByBuyer(BuyerEntity buyer);
    
    // For Farmer Dashboard (Accepted demands they are fulfilling)
    long countByAcceptedBy(com.krishiYatra.krishiYatra.farmer.FarmerEntity acceptedBy);
    
    // For Admin Dashboard
    long countByActiveTrue();
}
