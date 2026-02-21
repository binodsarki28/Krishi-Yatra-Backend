package com.krishiYatra.krishiYatra.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepo extends JpaRepository<DeliveryEntity, String> {
}
