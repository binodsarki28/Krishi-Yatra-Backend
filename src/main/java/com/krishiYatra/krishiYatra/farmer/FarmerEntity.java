package com.krishiYatra.krishiYatra.farmer;

import com.krishiYatra.krishiYatra.common.enums.FarmType;
import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Table(name = "FARMERS")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class FarmerEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "FARMER_GUID")
    private String farmerId;

    @Column(name = "FARM_TYPES")
    private String farmTypes;

    @Column(name = "FARM_NAME")
    private String farmName;

    @Column(name = "FARM_LOCATION")
    private String farmLocation;

    @Column(name = "FARM_AREA")
    private Double farmArea;

    @Column(name = "IS_VERIFIED")
    private boolean isVerified;

    @OneToOne
    @JoinColumn(name = "USER_GUID", nullable = false, unique = true)
    private UserEntity user;
}
