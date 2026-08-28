package com.folks.app.util;

import java.util.regex.Pattern;

public class Constants {

    public static final Pattern MOBILENUM_PATTERN = Pattern.compile("\\d{10}");

    public static final long OTP_EXPIRY_TIME_MSEC = 300000; //5 mins

    // Address
    public static final String DEFAULT_LABEL = "HOME";
    public static final short IS_DEFAULT_ADDR = 1;

    // Document
    public static final String  DEFAULT_DOC_TYPE = "AADHAAR";

    // PROFESSIONAL
    public static final short PROF_NOT_VERIFIED = 0;

    // PROFESSIONAL SERVICE
    public static final short PROF_SERVICE_ACTIVE = 1;

    //Booking
    public static final String BOOKING_ADDRESS = "booking.event.address";
}

