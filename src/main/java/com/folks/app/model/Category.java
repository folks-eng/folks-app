package com.folks.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_categories")
@IdClass(Category.CategoryPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Category.selectAll", query = "SELECT * FROM fks_categories")
})
public class Category implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false, updatable = false, precision = 32)
    private Integer categoryId;

    @Column(name = "name", nullable = false, updatable = true, length = 128)
    private String name;

    @Column(name = "parent_id", nullable = true, updatable = true, precision = 32)
    private Integer parentId;

    public Category() {}

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryId() {
        return this.categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getParentId() {
        return this.parentId;
    }

    public static class CategoryPK {

        private Integer categoryId;

        public CategoryPK() {}

        public CategoryPK(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public Integer getCategoryId() {
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