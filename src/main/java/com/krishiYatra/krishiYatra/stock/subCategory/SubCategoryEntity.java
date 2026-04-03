package com.krishiYatra.krishiYatra.stock.subCategory;

import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "SUB_CATEGORY")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class SubCategoryEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUB_CATEGORY_ID")
    private int subCategoryId;

    @Column(name = "SUB_CATEGORY_NAME", unique = true)
    private String subCategoryName;

    @com.fasterxml.jackson.annotation.JsonBackReference
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private CategoryEntity category;

    @Column(nullable = false)
    private boolean active = true;
}