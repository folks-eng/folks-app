package com.folks.app.util;

import com.folks.app.model.Address;
import com.folks.app.model.Professional;
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
        String label = addr.getLabel();
        if (label == null || label.toString().isEmpty()) {
            throw new IllegalArgumentException("Label is required.");
        }
    }

    public static void validate(ProfessionalProfile profMaster) {
        Short exp = profMaster.getExperienceYears();

        if (exp == null || exp.toString().isEmpty()) {
            throw new IllegalArgumentException("Experience is years is required.");
        }

        //Document
        if (profMaster.getDocuments() == null || profMaster.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("Document type is required.");
        }
        if (profMaster.getDocuments().get(0).getDocumentNumber() == null || profMaster.getDocuments().get(0).getDocumentNumber().isEmpty()) {
            throw new IllegalArgumentException("Document number is required.");
        }

        //Address
        if (profMaster.getAddress().getAddressLine1() == null || profMaster.getAddress().getAddressLine1().isEmpty()) {
            throw new IllegalArgumentException("Address line is required.");
        }
        if (profMaster.getAddress().getPincode() == null || profMaster.getAddress().getPincode() == 0) {
            throw new IllegalArgumentException("Pin code is required.");
        }

        if (profMaster.getExpertise() == null || profMaster.getExpertise().isEmpty()) {
            throw new IllegalArgumentException("Expertise is required.");
        }
    }
}
