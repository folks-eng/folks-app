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
     * Return the JWT ID. 
     * 
     * <p>
     * It is a standard registered claim defined in the JWT Specification (RFC 7519) that provides a unique identifier
     * for that specific token.
     * 
     * <ul>
     *   <li>Purpose: It serves as a unique serial number for the token to prevent replay attacks.</li>
     *   <li>Value Format: It is a case-sensitive string. It must be unique so that the same identifier is never assigned
     *       to a different token. Developers usually use a UUID</li>
     *   <li>Optionality: Including a jti claim is optional, but it is critical if you need to track or revoke individual tokens</li>
     * </ul>
     * 
     * @return String
     */
    String jti();

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
     * 
     * <p>
     * a standard registered claim defined in the JWT Specification (RFC 7519) that uniquely identifies the principal,
     * such as a user, an organization, or a device—that the token is about.
     * 
     * <ul>
     *   <li>Purpose: It tells your application who or what the token belongs to (like a unique user ID or account number).</li>
     *   <li>Value Format: It is a case-sensitive string. It can be a standard database ID (e.g., "123456"), a Universally Unique Identifier (UUID), or a URI.</li>
     *   <li>Uniqueness: The value must be unique in the context of the issuer or globally unique.</li>
     *   <li>Optionality: Including a sub claim is optional under the official rules, but it is heavily recommended and widely used.</li>
     * </ul>
     * 
     * @return String
     */
    String sub();
    
    /**
     * Return the scope of the token.
     * 
     * <p>
     * OAuth 2.0, scope is a mechanism used to limit an application's access to a user's account. It defines what
     * permissions or actions the token grants to the bearer, rather than identifying who the user is.
     * 
     * <ul>
     *   <li>Purpose: It specifies authorization boundaries (e.g., "read-only access" or "write access").</li>
     *   <li>Value Format: It is typically a single case-sensitive string containing a space-separated list of specific permissions.</li>
     *   <li>Optionality: It is optional but serves as the core authorization standard for OAuth 2.0 and OpenID Connect (OIDC)</li>
     * </ul>
     * 
     * @return List
     */
    List<String> scopes();
}
