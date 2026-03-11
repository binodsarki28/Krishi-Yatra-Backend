package com.krishiYatra.krishiYatra.delivery;

import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "DELIVERY")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class DeliveryEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "DELIVERY_GUID")
    private String deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "VEHICLE_TYPE")
    private VehicleType vehicleType;

    @Column(name = "VEHICLE_BRAND")
    private String vehicleBrand;

    @Column(name = "NUMBER_PLATE")
    private String numberPlate;

    @Column(name = "LICENSE_NUMBER")
    private String licenseNumber;

    @Column(name = "VEHICLE_PHOTO")
    private String vehiclePhoto;

    @Column(name = "LICENSE_PHOTO")
    private String licensePhoto;

    @Column(name = "IS_VERIFIED")
    private boolean verified;

    @OneToOne
    @JoinColumn(name = "USER_GUID", nullable = false, unique = true)
    private UserEntity user;
}
