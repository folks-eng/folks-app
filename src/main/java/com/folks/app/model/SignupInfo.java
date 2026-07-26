package com.folks.app.model;

import java.io.Serializable;
import java.util.Objects;


/**
 * This class has the OTP and related info for User signup via mobile or email.
 *
 * @author schan280
 */
public class SignupInfo implements Serializable, Cloneable {

    private String mobileNum;

    private String otp;

    private long createdTime;

    private String email;

    public SignupInfo() {}

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return this.otp;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long time) {
        this.createdTime = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.mobileNum);
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
        final String other = (String)obj;
        if (! Objects.equals(this.mobileNum, other)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return mobileNum + ":" + otp;
    }
}