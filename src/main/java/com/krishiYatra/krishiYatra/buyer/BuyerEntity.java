package com.krishiYatra.krishiYatra.buyer;

import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "BUYERS")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class BuyerEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "BUYER_GUID")
    private String buyerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "CONSUMER_TYPE", nullable = false)
    private ConsumerType consumerType;

    @Column(name = "BUSINESS_NAME")
    private String businessName;

    @Column(name = "BUSINESS_LOCATION")
    private String businessLocation;

    @Column(name = "IS_VERIFIED")
    private boolean verified;

    @OneToOne
    @JoinColumn(name = "USER_GUID", nullable = false, unique = true)
    private UserEntity user;
}
