package com.krishiYatra.krishiYatra.order.dao.impl;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import com.krishiYatra.krishiYatra.order.OrderEntity;
import com.krishiYatra.krishiYatra.order.dao.IOrderDao;
import com.krishiYatra.krishiYatra.order.dto.OrderResponse;
import com.krishiYatra.krishiYatra.order.mapper.OrderMapper;
import com.krishiYatra.krishiYatra.user.UserEntity;
import com.krishiYatra.krishiYatra.utils.DaoUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import jakarta.persistence.TypedQuery;

@Component
@RequiredArgsConstructor
public class OrderDaoImpl implements IOrderDao {

    @PersistenceContext
    private EntityManager em;

    private final OrderMapper orderMapper;

    @Override
    public List<OrderResponse> getAllOrders(Map<String, String> params, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderEntity> cq = cb.createQuery(OrderEntity.class);
        Root<OrderEntity> root = cq.from(OrderEntity.class);
        
        List<Predicate> predicates = buildPredicates(cb, root, params);
        DaoUtil.setPredicateToArray(predicates, cq, cb);

        cq.select(root);
        // Default sort by CreatedAt DESC
        cq.orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<OrderEntity> query = em.createQuery(cq);

        if (pageable != null && pageable.isPaged()) {
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());
        }

        return query.getResultList().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<OrderEntity> root, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();

        Join<OrderEntity, FarmerEntity> farmerJoin = root.join("farmer", JoinType.LEFT);
        Join<FarmerEntity, UserEntity> farmerUserJoin = farmerJoin.join("user", JoinType.LEFT);
        
        Join<OrderEntity, BuyerEntity> buyerJoin = root.join("buyer", JoinType.LEFT);
        Join<BuyerEntity, UserEntity> buyerUserJoin = buyerJoin.join("user", JoinType.LEFT);
        
        Join<OrderEntity, DeliveryEntity> deliveryJoin = root.join("delivery", JoinType.LEFT);
        Join<DeliveryEntity, UserEntity> deliveryUserJoin = deliveryJoin.join("user", JoinType.LEFT);

        // Role based filtering from Controller (Internal IDs)
        if (params.containsKey("filterFarmerUserId")) {
            predicates.add(cb.equal(farmerUserJoin.get("userId"), params.get("filterFarmerUserId")));
        }
        if (params.containsKey("filterBuyerUserId")) {
            predicates.add(cb.equal(buyerUserJoin.get("userId"), params.get("filterBuyerUserId")));
        }
        if (params.containsKey("filterDeliveryUserId")) {
            predicates.add(cb.equal(deliveryUserJoin.get("userId"), params.get("filterDeliveryUserId")));
        }

        // Specific search fields
        if (params.containsKey("orderId") && !params.get("orderId").trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("orderId")), "%" + params.get("orderId").trim().toLowerCase() + "%"));
        }
        if (params.containsKey("productName") && !params.get("productName").trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("stock").get("productName")), "%" + params.get("productName").trim().toLowerCase() + "%"));
        }
        if (params.containsKey("buyer") && !params.get("buyer").trim().isEmpty()) {
            String buyer = params.get("buyer").trim().toLowerCase();
            predicates.add(cb.or(
                cb.like(cb.lower(buyerUserJoin.get("fullName")), "%" + buyer + "%"),
                cb.like(cb.lower(buyerUserJoin.get("username")), "%" + buyer + "%")
            ));
        }
        if (params.containsKey("farmer") && !params.get("farmer").trim().isEmpty()) {
            String farmer = params.get("farmer").trim().toLowerCase();
            predicates.add(cb.or(
                cb.like(cb.lower(farmerUserJoin.get("fullName")), "%" + farmer + "%"),
                cb.like(cb.lower(farmerUserJoin.get("username")), "%" + farmer + "%")
            ));
        }
        if (params.containsKey("delivery") && !params.get("delivery").trim().isEmpty()) {
            String delivery = params.get("delivery").trim().toLowerCase();
            predicates.add(cb.or(
                cb.like(cb.lower(deliveryUserJoin.get("fullName")), "%" + delivery + "%"),
                cb.like(cb.lower(deliveryUserJoin.get("username")), "%" + delivery + "%")
            ));
        }

        // Global Search (Fallback for general search bar)
        if (params.containsKey("search") && !params.get("search").trim().isEmpty()) {
            String search = "%" + params.get("search").trim().toLowerCase() + "%";
            List<Predicate> sP = new ArrayList<>();
            
            sP.add(cb.like(cb.lower(root.get("orderId")), search));
            sP.add(cb.like(cb.lower(root.get("stock").get("productName")), search));
            sP.add(cb.like(cb.lower(farmerUserJoin.get("fullName")), search));
            sP.add(cb.like(cb.lower(farmerUserJoin.get("username")), search));
            sP.add(cb.like(cb.lower(buyerUserJoin.get("fullName")), search));
            sP.add(cb.like(cb.lower(buyerUserJoin.get("username")), search));
            sP.add(cb.like(cb.lower(deliveryUserJoin.get("fullName")), search));
            sP.add(cb.like(cb.lower(deliveryUserJoin.get("username")), search));
            
            predicates.add(cb.or(sP.toArray(new Predicate[0])));
        }

        // Status filter
        if (params.containsKey("status") && !params.get("status").trim().isEmpty()) {
            try {
                OrderStatus status = OrderStatus.valueOf(params.get("status").toUpperCase());
                predicates.add(cb.equal(root.get("orderStatus"), status));
            } catch(IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        // Ignore statuses
        if (params.containsKey("excludeStatus")) {
             try {
                OrderStatus exStatus = OrderStatus.valueOf(params.get("excludeStatus").toUpperCase());
                predicates.add(cb.notEqual(root.get("orderStatus"), exStatus));
            } catch(IllegalArgumentException e) {}
        }
        
        // Exclude delivered/completed generically
        if (params.containsKey("isPendingList") && "true".equalsIgnoreCase(params.get("isPendingList"))) {
             predicates.add(cb.notEqual(root.get("orderStatus"), OrderStatus.DELIVERED));
        }

        return predicates;
    }
}
