package com.folks.app.util;

import com.folks.app.cache.impl.CategoryCache;
import com.folks.app.cache.impl.CityCache;
import com.folks.app.cache.impl.NeighbourhoodCache;
import com.folks.app.model.Address;
import com.folks.app.model.Document;
import com.folks.app.model.ProfessionalProfile;
import com.folks.app.model.User;

import java.util.List;

public class Validator {

    public static void validateUser(User user) {
        if (user.getFullName() == null || user.getFullName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (user.getPhone1() == null || user.getPhone1().isBlank()) {
            throw new IllegalArgumentException("Primary mobile number is required.");
        }
        if (! Constants.MOBILENUM_PATTERN.matcher(user.getPhone1()).matches()) {
            throw new IllegalArgumentException("Mobile number invalid.");
        }
        if (user.getPhone2() != null && ! user.getPhone2().isBlank()) {
            if (! Constants.MOBILENUM_PATTERN.matcher(user.getPhone2()).matches()) {
                throw new IllegalArgumentException("Mobile number invalid.");
            }
        }
    }

    public static void validateAddress(Address addr) {
        if (addr.getLabel() == null || addr.getLabel().isBlank()) {
            throw new IllegalArgumentException("Label is required.");
        }
        if (addr.getAddressLine1() == null || addr.getAddressLine1().isBlank()) {
            throw new IllegalArgumentException("Address line1 is required.");
        }
        if (addr.getNeighbourhoodId() == null) {
            throw new IllegalArgumentException("neighbourhoodId is required.");
        }
        if (! NeighbourhoodCache.getCache().contains(addr.getNeighbourhoodId())) {
            throw new IllegalArgumentException("Invalid neighbourhood specified");
        }
    }

    public static void validateProfessional(ProfessionalProfile profProfile) {
        // Validate Document
        List<Document> docList = profProfile.getDocuments();
        if (docList == null || docList.isEmpty()) {
            throw new IllegalArgumentException("At least one document is needed.");
        }
        for(Document doc : docList) {
            Validator.validateDocument(doc);
        }
        // Validate Address
        Address addr = profProfile.getAddress();
        if (addr == null ) {
            throw new IllegalArgumentException("Address is required.");
        }
        validateAddress(addr);
        
        // Validate Experience and expertise
        Short exp = profProfile.getExperienceYears();
        if (exp == null || exp <= 0) {
            throw new IllegalArgumentException("Valid experience in years is required.");
        }
        if (profProfile.getExpertise() == null || profProfile.getExpertise().isEmpty()) {
            throw new IllegalArgumentException("Expertise is required.");
        }
        for (Integer id : profProfile.getExpertise()) {
            if (! CategoryCache.getCache().contains(id)) {
                throw new IllegalArgumentException("Invalid category specified");
            }
        }
        
        // Validate serving areas for professional
        if (profProfile.getNeighbourhoodIds() == null || profProfile.getNeighbourhoodIds().isEmpty()) {
            throw new IllegalArgumentException("Serving localities are required");
        }
        for (Integer id : profProfile.getNeighbourhoodIds()) {
            if (id != -1 && ! NeighbourhoodCache.getCache().contains(id)) {
                throw new IllegalArgumentException("Invalid neighbourhood specified");
            }
            if (id == -1 && profProfile.getNeighbourhoodIds().size() > 1) {
                throw new IllegalArgumentException("You have already selected All Localities. No additional selection is required");
            }
            if (id == -1 && profProfile.getCityId() == null) {
                throw new IllegalArgumentException("Must provide city when selecting All Localities");
            }
        }
        if (profProfile.getCityId() != null && ! CityCache.getCache().contains(profProfile.getCityId())) {
            throw new IllegalArgumentException("Must provide a valid city");
        }
    }

    private static void validateDocument(Document doc) {
        if (doc.getDocumentNumber() == null || doc.getDocumentNumber().isEmpty()) {
            throw new IllegalArgumentException("Document number is needed.");
        }
        if (doc.getDocumentType() == null || doc.getDocumentType().isEmpty()) {
            throw new IllegalArgumentException("Document type is needed.");
        }
        if (doc.getNameOnDocument() == null || doc.getNameOnDocument().isEmpty()) {
            throw new IllegalArgumentException("Name on the document is needed.");
        }
    }
}
