package com.folks.app.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Kakoli Sen
 */
public class ProfessionalProfile implements Serializable, Cloneable {
    
    private String applicationId;

    // A short description including experience about professional.
    private String bio;

    // Identity verification.
    // A list of documents professional has to submit for background verification.
    private List<Document> documents;

    // Address details
    // Local address of the professional.
    private Address address;

    // Professional details
    // Overall year of experience working as a professional
    private Short experienceYears;
    
    // Nearby cities that this professional is willing to travel
    private String servingCities;
    
    // City
    private Integer cityId;
    
    // Localities the professional serves.
    private List<Integer> neighbourhoodIds;

    // Areas of expertise (at the sub-category level).
    // It essentially contains the sub-category-ids.
    private List<Integer> expertise;

    public ProfessionalProfile() {}

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getServingCities() {
        return servingCities;
    }

    public void setServingCities(String servingCities) {
        this.servingCities = servingCities;
    }

    public List<Integer> getExpertise() {
        return expertise;
    }

    public void setExpertise(List<Integer> expertise) {
        this.expertise = expertise;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public List<Integer> getNeighbourhoodIds() {
        return neighbourhoodIds;
    }

    public void setNeighbourhoodIds(List<Integer> neighbourhoodIds) {
        this.neighbourhoodIds = neighbourhoodIds;
    }

}