package com.krishiYatra.krishiYatra.stock.category;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Table(name = "CATEGORY")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CategoryEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private int categoryId;

    @Column(name = "CATEGORY_NAME", unique = true)
    private String categoryName;

    @JsonManagedReference
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubCategoryEntity> subCategories;

    @Column(nullable = false)
    private boolean active = true;
}
