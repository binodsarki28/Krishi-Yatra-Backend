package com.krishiYatra.krishiYatra.stock.category;

import com.krishiYatra.krishiYatra.address.AddressEntity;
import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "CATEGORY")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CategoryEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CATEGORY_GUID")
    private String categoryId;

    @Column(name = "CATEGORY_NAME", unique = true)
    private String categoryName;

    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private SubCategoryEntity subCategory;
}
