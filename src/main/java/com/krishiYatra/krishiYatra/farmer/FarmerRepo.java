package com.krishiYatra.krishiYatra.farmer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerRepo extends JpaRepository<FarmerEntity, String> {

}
