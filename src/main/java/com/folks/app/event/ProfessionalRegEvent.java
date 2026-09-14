package com.folks.app.event;

import com.folks.app.model.ProfessionalProfile;

/**
 *
 * @author sudip
 */
public class ProfessionalRegEvent {
    
    private ProfessionalProfile propfProfile;
    
    public ProfessionalRegEvent() {}

    public ProfessionalRegEvent(ProfessionalProfile propfProfile) {
        this.propfProfile = propfProfile;
    }

    public ProfessionalProfile getPropfProfile() {
        return propfProfile;
    }

    public void setPropfProfile(ProfessionalProfile propfProfile) {
        this.propfProfile = propfProfile;
    }

    @Override
    public String toString() {
        return "[Profile: " + propfProfile + "]";
    }
}
