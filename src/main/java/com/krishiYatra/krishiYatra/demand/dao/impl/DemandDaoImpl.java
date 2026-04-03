package com.krishiYatra.krishiYatra.demand.dao.impl;

import com.krishiYatra.krishiYatra.common.enums.DemandStatus;
import com.krishiYatra.krishiYatra.demand.DemandEntity;
import com.krishiYatra.krishiYatra.demand.dao.IDemandDao;
import com.krishiYatra.krishiYatra.demand.dto.DemandResponse;
import com.krishiYatra.krishiYatra.demand.mapper.DemandMapper;
import com.krishiYatra.krishiYatra.utils.DaoUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DemandDaoImpl implements IDemandDao {

    @PersistenceContext
    private EntityManager em;

    private final DemandMapper demandMapper;

    @Override
    public List<DemandResponse> getDemands(Map<String, String> params, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DemandEntity> cq = cb.createQuery(DemandEntity.class);
        Root<DemandEntity> root = cq.from(DemandEntity.class);

        List<Predicate> predicates = buildPredicates(cb, root, params);
        DaoUtil.setPredicateToArray(predicates, cq, cb);

        cq.select(root);
        cq.orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<DemandEntity> query = em.createQuery(cq);
        if (pageable != null && pageable.isPaged()) {
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());
        }

        return query.getResultList().stream()
                .map(demandMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countDemands(Map<String, String> params) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<DemandEntity> root = cq.from(DemandEntity.class);

        List<Predicate> predicates = buildPredicates(cb, root, params);
        DaoUtil.setPredicateToArray(predicates, cq, cb);

        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<DemandEntity> root, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();

        // Active filter (true by default)
        boolean active = params.get("active") == null || Boolean.parseBoolean(params.get("active"));
        predicates.add(cb.equal(root.get("active"), active));

        if (params.containsKey("categoryId") && !params.get("categoryId").isEmpty()) {
            predicates.add(cb.equal(root.get("category").get("categoryId"), params.get("categoryId")));
        }

        if (params.containsKey("subCategoryId") && !params.get("subCategoryId").isEmpty()) {
            predicates.add(cb.equal(root.get("subCategory").get("subCategoryId"), params.get("subCategoryId")));
        }

        if (params.containsKey("status") && !params.get("status").isEmpty()) {
            try {
                DemandStatus status = DemandStatus.valueOf(params.get("status").toUpperCase());
                predicates.add(cb.equal(root.get("status"), status));
            } catch (Exception ignored) {}
        }

        if (params.containsKey("buyerId") && !params.get("buyerId").isEmpty()) {
            predicates.add(cb.equal(root.get("buyer").get("buyerId"), params.get("buyerId")));
        }

        if (params.containsKey("farmerGuid") && !params.get("farmerGuid").isEmpty()) {
            predicates.add(cb.equal(root.get("acceptedBy").get("farmerId"), params.get("farmerGuid")));
        }

        return predicates;
    }
}
