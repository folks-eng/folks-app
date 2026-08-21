package com.folks.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.javalabs.jpa.annotation.Inner;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_categories")
@IdClass(Category.CategoryPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Category.selectAll", query = "SELECT * FROM fks_categories"),
    @NamedNativeQuery(name = "Category.selectCategoryAndServices"
            , query = "SELECT a.category_id, a.name AS category_name, a.icon, a.image AS category_image, a.tag_line, b.category_id AS sub_category_id, b.name AS sub_category_name, b.image AS sub_category_image, b.parent_id, c.category_id AS srvc_category_id, c.service_id, c.name AS service_name, c.description, c.base_price, c.currency, c.duration_minutes, c.image, c.rating_avg, c.reviews" +
                    "\n  FROM fks_categories a" +
                    "\n INNER JOIN fks_categories b ON (a.category_id = b.parent_id)" +
                    "\n INNER JOIN fks_services c ON (b.category_id = c.category_id)" + 
                    "\n WHERE 1 = ? OR a.category_id IN (:ids)" +
                    "\n ORDER BY a.category_id, b.category_id, c.service_id")
})
public class Category implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false, updatable = false, precision = 32)
    private Integer categoryId;

    @Column(name = "name", nullable = false, updatable = true, length = 128)
    private String name;

    @Column(name = "icon", nullable = false, updatable = true, length = 16)
    private String icon;

    @Column(name = "tag_line", nullable = false, updatable = true, length = 128)
    private String tagLine;

    @Column(name = "image", nullable = false, updatable = true, length = 128)
    private String image;

    @Column(name = "parent_id", nullable = true, updatable = true, precision = 32)
    private Integer parentId;
    
    @Inner
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<Category> subCategories;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey()
            , name = "parent_id"
            , table = "fks_categories"
            , referencedColumnName = "category_id")
    private Category parent;
    
    @Inner
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private List<Service> services;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
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