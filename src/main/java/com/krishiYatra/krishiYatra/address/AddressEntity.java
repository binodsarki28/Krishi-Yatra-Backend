package com.krishiYatra.krishiYatra.address;

import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ADDRESS")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class AddressEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ADDRESS_GUID")
    private String addressId;

    @Column(name = "PROVINCE")
    private String province;

    @Column(name = "DISTRICT")
    private String district;

    @Column(name = "MUNICIPALITY")
    private String municipality;

    @Column(name = "CITY")
    private String city;

    @Column(name = "WARD_NO")
    private String wardNo;

    @Column(name = "STREET_NAME")
    private String streetName;

    @Column(name = "OTHER")
    private String other;

    @OneToOne
    @JoinColumn(name = "USER_GUID", referencedColumnName = "USER_GUID", unique = true)
    private UserEntity user;

}
