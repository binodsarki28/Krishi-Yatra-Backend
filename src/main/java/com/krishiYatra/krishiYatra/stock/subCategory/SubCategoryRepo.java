package com.krishiYatra.krishiYatra.stock.subCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepo extends JpaRepository<SubCategoryEntity, Integer> {
    List<SubCategoryEntity> findByCategory_CategoryIdAndActiveTrue(int categoryId);
    List<SubCategoryEntity> findByActiveTrue();
}
