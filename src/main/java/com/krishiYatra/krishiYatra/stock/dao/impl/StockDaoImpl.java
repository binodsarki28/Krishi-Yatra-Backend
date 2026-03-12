package com.krishiYatra.krishiYatra.stock.dao.impl;

import com.krishiYatra.krishiYatra.stock.StockEntity;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import com.krishiYatra.krishiYatra.stock.dao.IStockDao;
import com.krishiYatra.krishiYatra.stock.dto.StockListResponse;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.utils.DaoUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StockDaoImpl implements IStockDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<StockListResponse> getAllStocks(Map<String, String> params) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StockListResponse> cq = cb.createQuery(StockListResponse.class);
        Root<StockEntity> root = cq.from(StockEntity.class);
        
        Join<StockEntity, CategoryEntity> catJoin = root.join("category", JoinType.LEFT);
        Join<StockEntity, SubCategoryEntity> subCatJoin = root.join("subCategory", JoinType.LEFT);
        Join<StockEntity, FarmerEntity> farmerJoin = root.join("farmer", JoinType.LEFT);
        Join<FarmerEntity, UserEntity> userJoin = farmerJoin.join("user", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        
        if (params.containsKey("all") && "true".equalsIgnoreCase(params.get("all"))) {
            // Include all, don't add active predicate
        } else if (params.containsKey("active")) {
            predicates.add(cb.equal(root.get("active"), Boolean.parseBoolean(params.get("active"))));
        } else {
            predicates.add(cb.equal(root.get("active"), true));
        }

        if (params.containsKey("stockName")) {
            predicates.add(cb.like(cb.lower(root.get("stockName")), "%" + params.get("stockName").toLowerCase() + "%"));
        }
        if (params.containsKey("productName")) {
            predicates.add(cb.like(cb.lower(root.get("productName")), "%" + params.get("productName").toLowerCase() + "%"));
        }
        if (params.containsKey("categoryId")) {
            predicates.add(cb.equal(catJoin.get("categoryId"), params.get("categoryId")));
        }
        if (params.containsKey("subCategoryId")) {
            predicates.add(cb.equal(subCatJoin.get("subCategoryId"), params.get("subCategoryId")));
        }
        if (params.containsKey("farmerId")) {
            predicates.add(cb.equal(farmerJoin.get("farmerId"), params.get("farmerId")));
        }
        if (params.containsKey("farmLocation")) {
            predicates.add(cb.like(cb.lower(farmerJoin.get("farmLocation")), "%" + params.get("farmLocation").toLowerCase() + "%"));
        }
        if (params.containsKey("farmName")) {
            predicates.add(cb.like(cb.lower(farmerJoin.get("farmName")), "%" + params.get("farmName").toLowerCase() + "%"));
        }

        if (params.containsKey("minPrice")) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerUnit"), Double.parseDouble(params.get("minPrice"))));
        }
        if (params.containsKey("maxPrice")) {
            predicates.add(cb.lessThanOrEqualTo(root.get("pricePerUnit"), Double.parseDouble(params.get("maxPrice"))));
        }

        DaoUtil.setPredicateToArray(predicates, cq, cb);

        cq.select(cb.construct(StockListResponse.class,
                root.get("stockName"),
                root.get("productName"),
                root.get("stockSlug"),
                root.get("quantity"),
                root.get("pricePerUnit"),
                subCatJoin.get("subCategoryName"),
                catJoin.get("categoryName"),
                userJoin.get("fullName"),
                root.get("active")
        ));

        cq.orderBy(cb.desc(root.get("createdAt")));

        return em.createQuery(cq).getResultList();
    }
}
