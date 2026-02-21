package com.krishiYatra.krishiYatra.buyer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepo extends JpaRepository<BuyerEntity, String> {
}
