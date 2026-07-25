package com.folks.app.util;

import java.util.regex.Pattern;

public class Constants {
    public static final Pattern MOBILENUM_PATTERN = Pattern.compile("\\d{10}");
    public static final long OTP_EXPIRY_TIME_MSEC = 300000; //5 mins
}
