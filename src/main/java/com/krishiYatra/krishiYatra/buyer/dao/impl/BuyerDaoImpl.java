package com.krishiYatra.krishiYatra.buyer.dao.impl;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.buyer.dao.IBuyerDao;
import com.krishiYatra.krishiYatra.buyer.dto.BuyerListResponse;
import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.DaoUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BuyerDaoImpl implements IBuyerDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<BuyerListResponse> getAllBuyers(Map<String, String> allRequestParams, Pageable pageable) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<BuyerListResponse> cq = cb.createQuery(BuyerListResponse.class);
            Root<BuyerEntity> root = cq.from(BuyerEntity.class);
            Join<BuyerEntity, UserEntity> userJoin = root.join("user", JoinType.LEFT);

            List<Predicate> predicates = buildPredicates(root, userJoin, cb, allRequestParams);
            DaoUtil.setPredicateToArray(predicates, cq, cb);
            
            cq.select(cb.construct(BuyerListResponse.class,
                userJoin.get("fullName"),
                userJoin.get("username"),
                root.get("consumerType"),
                root.get("businessLocation"),
                root.get("status"),
                userJoin.get("isActive")
            ));
            
            cq.orderBy(cb.desc(root.get("createdAt")));
            
            TypedQuery<BuyerListResponse> query = em.createQuery(cq);
            if (pageable != null && pageable.isPaged()) {
                query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                query.setMaxResults(pageable.getPageSize());
            }
            return query.getResultList();
        } catch (Exception ex) {
            log.error("Error fetching buyers", ex);
            return Collections.emptyList();
        }
    }

    private List<Predicate> buildPredicates(Root<BuyerEntity> root, Join<BuyerEntity, UserEntity> userJoin, CriteriaBuilder cb, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        if (params.containsKey("businessLocation")) {
            String businessLocation = params.get("businessLocation").toLowerCase();
            predicates.add(cb.like(cb.lower(root.get("businessLocation")), "%" + businessLocation + "%"));
        }
        if (params.containsKey("consumerType") && !params.get("consumerType").isEmpty()) {
            try {
                String cType = params.get("consumerType").toUpperCase();
                predicates.add(cb.equal(root.get("consumerType"), ConsumerType.valueOf(cType)));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid consumerType provided: {}", params.get("consumerType"));
                predicates.add(cb.equal(cb.literal(1), cb.literal(0)));
            }
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
