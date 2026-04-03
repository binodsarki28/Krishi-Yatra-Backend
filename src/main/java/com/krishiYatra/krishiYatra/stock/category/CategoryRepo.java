package com.krishiYatra.krishiYatra.stock.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<CategoryEntity, Integer> {
    Optional<CategoryEntity> findByCategoryName(String categoryName);
    boolean existsByCategoryName(String categoryName);
    List<CategoryEntity> findByActiveTrue();
}
