package com.folks.app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Kakoli Sen
 */

public class ProfessionalMaster implements Serializable, Cloneable {

    private String extUserId;

    private String bio;

    private Short experienceYears;

    private BigDecimal ratingAvg;

    private Document document;

    private Address address;

    /******************* Category, sub-category fields **********/
    private List<Integer> subCategories;

    public ProfessionalMaster() {}

    public void setExtUserId(String userId) {
        this.extUserId = userId;
    }

    public String getExtUserId() {
        return this.extUserId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setExperienceYears(Short experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Short getExperienceYears() {
        return this.experienceYears;
    }

    public BigDecimal getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(BigDecimal ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Integer> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<Integer> subCategories) {
        this.subCategories = subCategories;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.extUserId);
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
        String id = ((ProfessionalMaster) obj).getExtUserId();
        if (!this.extUserId.equals(id)) {
            return false;
        }
        return true;
    }

}