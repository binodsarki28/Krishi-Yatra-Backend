package com.krishiYatra.krishiYatra.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StockRepo extends JpaRepository<StockEntity, String> {
    Optional<StockEntity> findByStockSlug(String stockSlug);
}
