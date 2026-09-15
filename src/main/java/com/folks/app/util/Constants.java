package com.folks.app.util;

import java.util.regex.Pattern;

public class Constants {

    public static final Pattern MOBILENUM_PATTERN = Pattern.compile("\\d{10}");

    public static final long OTP_EXPIRY_TIME_MSEC = 300000; //5 mins

    // Viewing records from db
    public static final int DEFAULT_SEARCH_LIMIT = 100;

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

    public static final String VIEWALL_BOOKINGS_CUST = """
                SELECT a.booking_id
                        , a.scheduled_at
                        , a.time_slot
                        , a.status
                        , a.payment_method
                        , a.total_amount
                        , a.created_at
                        , b.service_id
                        , b.name
                        , c.address_line1
                        , c.address_line2
                        , c.city
                        , c.pincode
                        , a.professional_id
                        , COALESCE(e.full_name, 'Professional not assigned') AS professional_name
                        , COALESCE(e.phone1, '') AS phone1
                  FROM fks_bookings a
                 INNER JOIN fks_services b ON (a.service_id = b.service_id)
                 INNER JOIN fks_addresses c ON (a.address_id = c.address_id)
                 LEFT OUTER JOIN fks_professionals d ON (a.professional_id = d.professional_id)
                 LEFT OUTER JOIN fks_users e ON (d.user_id = e.user_id AND e.role = ?)
                 WHERE a.customer_id = ?
                 ORDER BY a.created_at DESC;""";

    public static final String VIEWALL_BOOKINGS_PROF = """
                SELECT b.booking_id
                        , b.scheduled_at
                        , b.time_slot
                        , b.status
                        , b.payment_method
                        , b.total_amount
                        , b.created_at
                        , b.service_id
                        , s.name as service_name
                        , a.address_line1 as customer_addr
                        , a.address_line2
                        , a.city
                        , a.pincode
                        , u.user_id AS customer_id
						, u.full_name AS customer_name
						, u.phone1 AS customer_contact
                  FROM fks_bookings b
                 INNER JOIN fks_services s ON (b.service_id = s.service_id)
                 INNER JOIN fks_addresses a ON (b.address_id = a.address_id)
                 INNER JOIN fks_users u ON (b.customer_id = u.user_id AND u.role = ?)
                 WHERE b.professional_id = ?
                 ORDER BY b.created_at DESC;""";
}

