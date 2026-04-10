package com.krishiYatra.krishiYatra.stock;

import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepo extends JpaRepository<StockEntity, String> {
    Optional<StockEntity> findByStockSlug(String stockSlug);
    List<StockEntity> findByFarmer_FarmerId(String farmerId);
    
    long countByFarmer(FarmerEntity farmer);
    long countByFarmerAndActive(FarmerEntity farmer, boolean active);
    long countByFarmerAndQuantityLessThanEqual(FarmerEntity farmer, Double quantity);
    
    @org.springframework.data.jpa.repository.Query("SELECT s.category.categoryName, COUNT(s) FROM StockEntity s WHERE s.farmer = :farmer GROUP BY s.category.categoryName")
    List<Object[]> countStocksByCategory(@org.springframework.data.repository.query.Param("farmer") FarmerEntity farmer);
}
