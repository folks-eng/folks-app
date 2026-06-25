package com.folks.app.auth;

/**
 * Implementation of {@link AppUser}.
 *
 * @author schan280
 */
public class AppUserImpl implements AppUser {
    
    private final Principal principle;

    public AppUserImpl(Principal principle) {
        this.principle = principle;
    }

    @Override
    public Principal principal() {
        return principle;
    }
}
