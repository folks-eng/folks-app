package com.folks.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_categories")
@IdClass(Category.CategoryPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Category.selectAll", query = "SELECT * FROM fks_categories")
})
public class Category implements Serializable, Cloneable {

    @Id
    @Column(name = "category_id", nullable = false, updatable = false, precision = 64)
    private Long categoryId;

    @Column(name = "name", nullable = false, updatable = true, length = 128)
    private String name;

    @Column(name = "parent_id", nullable = true, updatable = true, precision = 64)
    private BigInteger parentId;

    public Category() {}

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public BigInteger getParentId() {
        return this.parentId;
    }

    public static class CategoryPK {

        private Long categoryId;

        public CategoryPK() {}

        public CategoryPK(Long categoryId) {
            this.categoryId = categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public Long getCategoryId() {
            return this.categoryId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.categoryId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CategoryPK other = (CategoryPK)obj;
            if (! Objects.equals(this.categoryId, other.categoryId)) {
                return false;
            }
            return true;
        }

    }
}