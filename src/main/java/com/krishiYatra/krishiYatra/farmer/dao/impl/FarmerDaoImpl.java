package com.krishiYatra.krishiYatra.farmer.dao.impl;

import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.dao.IFarmerDao;
import com.krishiYatra.krishiYatra.farmer.dto.FarmerListResponse;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.DaoUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FarmerDaoImpl implements IFarmerDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<FarmerListResponse> getAllFarmers(Map<String, String> allRequestParams, Pageable pageable) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<FarmerListResponse> cq = cb.createQuery(FarmerListResponse.class);
            Root<FarmerEntity> root = cq.from(FarmerEntity.class);
            Join<FarmerEntity, UserEntity> userJoin = root.join("user", JoinType.LEFT);

            List<Predicate> predicates = buildPredicates(root, userJoin, cb, allRequestParams);
            DaoUtil.setPredicateToArray(predicates, cq, cb);
            
            cq.select(cb.construct(FarmerListResponse.class,
                userJoin.get("fullName"),
                userJoin.get("username"),
                root.get("farmTypes"),
                root.get("farmLocation"),
                root.get("status"),
                userJoin.get("isActive")
            ));
            
            cq.orderBy(cb.desc(root.get("createdAt")));
            
            TypedQuery<FarmerListResponse> query = em.createQuery(cq);
            if (pageable != null && pageable.isPaged()) {
                query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                query.setMaxResults(pageable.getPageSize());
            }
            return query.getResultList();
        } catch (Exception ex) {
            log.error("Error fetching farmers", ex);
            return Collections.emptyList();
        }
    }

    private List<Predicate> buildPredicates(Root<FarmerEntity> root, Join<FarmerEntity, UserEntity> userJoin, CriteriaBuilder cb, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        if (params.containsKey("farmTypes")) {
            String farmTypes = params.get("farmTypes").toLowerCase();
            predicates.add(cb.like(cb.lower(root.get("farmTypes")), "%" + farmTypes + "%"));
        }
        if (params.containsKey("farmLocation")) {
            String farmLocation = params.get("farmLocation").toLowerCase();
            predicates.add(cb.like(cb.lower(root.get("farmLocation")), "%" + farmLocation + "%"));
        }
        if (params.containsKey("status")) {
            try {
                com.krishiYatra.krishiYatra.common.enums.VerificationStatus stat = com.krishiYatra.krishiYatra.common.enums.VerificationStatus.valueOf(params.get("status").toUpperCase());
                predicates.add(cb.equal(root.get("status"), stat));
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }
        if (params.containsKey("fullName")) {
            String fullName = params.get("fullName").toLowerCase();
            predicates.add(cb.like(cb.lower(userJoin.get("fullName")), "%" + fullName + "%"));
        }
        if (params.get("username") != null && !params.get("username").isEmpty()) {
            String username = params.get("username").toLowerCase();
            if (username.startsWith("@")) username = username.substring(1);
            predicates.add(cb.like(cb.lower(userJoin.get("username")), "%" + username + "%"));
        }
        return predicates;
    }
}
