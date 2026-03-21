package com.krishiYatra.krishiYatra.delivery.dao.impl;

import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.delivery.dao.IDeliveryDao;
import com.krishiYatra.krishiYatra.delivery.dto.DeliveryListResponse;
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
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeliveryDaoImpl implements IDeliveryDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<DeliveryListResponse> getAllDeliveries(Map<String, String> allRequestParams, Pageable pageable) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<DeliveryListResponse> cq = cb.createQuery(DeliveryListResponse.class);
            Root<DeliveryEntity> root = cq.from(DeliveryEntity.class);
            Join<DeliveryEntity, UserEntity> userJoin = root.join("user", JoinType.LEFT);

            List<Predicate> predicates = buildPredicates(root, userJoin, cb, allRequestParams);
            DaoUtil.setPredicateToArray(predicates, cq, cb);
            
            cq.select(cb.construct(DeliveryListResponse.class,
                userJoin.get("fullName"),
                userJoin.get("username"),
                root.get("vehicleType"),
                root.get("vehicleBrand"),
                root.get("status"),
                userJoin.get("isActive")
            ));
            
            cq.orderBy(cb.desc(root.get("createdAt")));
            
            TypedQuery<DeliveryListResponse> query = em.createQuery(cq);
            if (pageable != null && pageable.isPaged()) {
                query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                query.setMaxResults(pageable.getPageSize());
            }
            return query.getResultList();
        } catch (Exception ex) {
            log.error("Error fetching deliveries", ex);
            return Collections.emptyList();
        }
    }

    private List<Predicate> buildPredicates(Root<DeliveryEntity> root, Join<DeliveryEntity, UserEntity> userJoin, CriteriaBuilder cb, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        if (params.containsKey("vehicleBrand") && !params.get("vehicleBrand").isEmpty()) {
            String vehicleBrand = params.get("vehicleBrand").toLowerCase();
            predicates.add(cb.like(cb.lower(root.get("vehicleBrand")), "%" + vehicleBrand + "%"));
        }
        if (params.containsKey("vehicleType") && !params.get("vehicleType").isEmpty()) {
            try {
                String vType = params.get("vehicleType").toUpperCase();
                predicates.add(cb.equal(root.get("vehicleType"), VehicleType.valueOf(vType)));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid vehicleType provided: {}", params.get("vehicleType"));
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
