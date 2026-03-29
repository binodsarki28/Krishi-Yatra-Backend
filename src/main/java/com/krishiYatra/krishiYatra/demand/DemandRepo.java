package com.krishiYatra.krishiYatra.demand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandRepo extends JpaRepository<DemandEntity, String> {
}
