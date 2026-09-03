package com.folks.app.util;

import com.folks.app.model.Address;
import com.folks.app.model.Document;
import com.folks.app.model.ProfessionalProfile;
import com.folks.app.model.User;

import java.util.Arrays;
import java.util.List;

public class Validator {

    public static void validateUser(User user) {

        String name = user.getFullName();
        String mandatoryContact = user.getPhone1();
        String optionalContact = user.getPhone1();
        String email = user.getEmail();
        User.Status status = user.getStatus();
        User.Role role = user.getRole();

        if (status != null) {
            String statusStr = status.name();
            //System.out.println("Status in validate " +status.name());
            List<String> validStatusList = Arrays.asList("ACTIVE", "INACTIVE", "BLOCKED");
            if (!validStatusList.contains(statusStr)) {
                throw new IllegalArgumentException("Status entered is invalid.");
            }
        }
        if (role != null) {
            String roleStr = role.name();
            //System.out.println("Status in validate " +status.name());
            List<String> validRoleList = Arrays.asList("CUSTOMER", "PROFESSIONAL", "ADMIN");
            if (!validRoleList.contains(roleStr)) {
                throw new IllegalArgumentException("Role entered is invalid.");
            }
        }
        if (mandatoryContact == null || mandatoryContact.trim().isEmpty()) {
            throw new IllegalArgumentException("Primary mobile number is required.");
        }
        if (!Constants.MOBILENUM_PATTERN.matcher(mandatoryContact).matches()) {
            throw new IllegalArgumentException("Mobile number invalid.");
        }
        if (optionalContact != null && !optionalContact.trim().isEmpty()) {
            if (!Constants.MOBILENUM_PATTERN.matcher(optionalContact).matches()) {
                throw new IllegalArgumentException("Mobile number invalid.");
            }
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        } else {
            //TBD : verify email by sending mail
        }
    }

    public static void validateAddress(Address addr) {
        String line1 = addr.getAddressLine1();
        if (line1 == null || line1.trim().isEmpty()) {
            throw new IllegalArgumentException("Address line1 is required.");
        }
        String city = addr.getCity();
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City is required.");
        }
        String state = addr.getState();
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("State is required.");
        }
        Integer pinCode = addr.getPincode();
        if (pinCode == null || pinCode.toString().isEmpty()) {
            throw new IllegalArgumentException("PinCode is required.");
        }
        // TBD
//        String label = addr.getLabel();
//        if (label == null || label.toString().isEmpty()) {
//            throw new IllegalArgumentException("Label is required.");
//        }
    }

    public static void validateProf(ProfessionalProfile profProfile) {
        Short exp = profProfile.getExperienceYears();
        if (exp == null || exp.toString().isEmpty()) {
            throw new IllegalArgumentException("Experience is years is required.");
        }
        if (profProfile.getExpertise() == null || profProfile.getExpertise().isEmpty()) {
            throw new IllegalArgumentException("Expertise is required.");
        }
        //Document
        List<Document> docList = profProfile.getDocuments();
        if (docList == null || docList.isEmpty()) {
            throw new IllegalArgumentException("At least one document is needed.");
        }
        for(Document doc : docList) {
            Validator.validateDocument(doc);
        }
        //Address
        Address addr = profProfile.getAddress();
        if (addr == null ) {
            throw new IllegalArgumentException("Address is required.");
        }
        Validator.validateAddress(addr);
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
