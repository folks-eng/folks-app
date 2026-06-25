package com.folks.app.auth;

import java.util.List;

/**
 * Represents the Principal associated with {@link AppUser}.
 * 
 * <p>
 * Principal authentication is the process of proving your identity to the security enforcing components
 * of the system so that they can grant access to information and services based on who you are. 
 * This applies to both human users of the system as well as to applications.
 * 
 * <p>
 * This interface represents the abstract notion of a principal, which can be used to represent any entity,
 * such as an individual, a corporation, and a login id.
 *
 * @author schan280
 */
public interface Principal {
    
    /**
     * Return the name of the logged in user.
     * 
     * <p>
     * This attribute will be extracted post successful authentication of the requested user against
     * a 3rd party auth service (ldap, okta, etc). If this attribute is not present with the auth
     * service, the api will return null.
     * 
     * @return String
     */
    String name();
    
    /**
     * Return the email address of the logged in user.
     * 
     * <p>
     * This attribute will be extracted post successful authentication of the requested user against
     * a 3rd party auth service (ldap, okta, etc). If this attribute is not present with the auth
     * service, the api will return null.
     * 
     * @return String
     */
    String email();

    /**
     * Id of the logged in user.
     * @return String
     */
    String id();

    /**
     * The privilege or the role of the user.
     * 
     * <p>
     * This attribute will be populated post successful authentication of the requested user against
     * a 3rd party auth service (ldap, okta, etc). Currently, a user can be assigned a single role (admin, standard, etc).
     * This role is internal to application.
     * 
     * @return String
     */
    String priv();

    /**
     * Return the set of access control group this user is part of.
     * @return List
     */
    List<String> sub();
}
