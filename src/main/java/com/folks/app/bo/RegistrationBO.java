package com.folks.app.bo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import io.vertx.redis.client.RedisOptions;

import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.folks.app.auth.AppUser;
import com.folks.app.model.RegistrationInfo;

/**
 *
 * @author schan280
 */
public class RegistrationBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationBO.class);

    private static final Pattern MOBILENUM_PATTERN = Pattern.compile("\\d{10}");

    private static final int EXPIRY_TIME_MSEC = 300000; //5 mins
    
    private Map<String, RegistrationInfo> otpCache;

    public RegistrationBO() {
        otpCache = new HashMap<>(); //TBD : any capacity
//        RedisOptions options = new RedisOptions()
//                .setConnectionString("redis://localhost:6379")
//                .setMaxPoolSize(8)
//                .setMaxPoolWaiting(32);
//
//        Redis client = Redis.createClient(vertx, options);
//        RedisAPI redisAPI = RedisAPI.api(client);
    }

    // Assume user is new
    // TBD : for existing user.
    public RegistrationInfo registerUser(AppUser user, RegistrationInfo regInfo) {
        String mobileNum = regInfo.getMobileNum();
        String otp = null;
        if( mobileNum != null && !mobileNum.trim().isEmpty()) {
            //validate input for mobile num 10 digits
            Matcher matcher = MOBILENUM_PATTERN.matcher(mobileNum);

            // Generate 4 digit random number, store it in Redis (e.g., using vertx-redis-client with a 5-minute TTL)
            if(matcher.matches()) {
                SecureRandom rand = new SecureRandom();
                otp = String.format("%04d", rand.nextInt(10000));
                regInfo.setOtp(otp);
                regInfo.setCreatedTime(System.currentTimeMillis());
                System.out.println(regInfo.getCreatedTime() + ":" +System.currentTimeMillis() +" Random number generated " +otp);
                storeOtp(regInfo);
                sendOtpSms(regInfo);
            }
            else {
                throw new IllegalArgumentException("Mobile number invalid.");
            }
        }
        else {
            throw new IllegalArgumentException("Mobile number is required.");
        }
        return regInfo;
    }

    private void storeOtp(RegistrationInfo regInfo) {
        otpCache.put(String.valueOf(regInfo.getMobileNum().trim()), regInfo);
    }

    private void sendOtpSms(RegistrationInfo regInfo) {
        // TBD
    }

    // TBD : Get otp for this mobile number from Redis.
    private RegistrationInfo retrieveOtpObj(RegistrationInfo input) {
        Optional<String> mobileNumOpt = Optional.ofNullable(input.getMobileNum());
        RegistrationInfo valFromCache = null;
                //.orElseThrow(() -> new DataNotFoundException("Mobile number is required."));
        if( mobileNumOpt.isPresent() && !mobileNumOpt.get().trim().isEmpty())
            valFromCache = otpCache.get(input.getMobileNum().trim());
        return valFromCache;
    }

    public boolean  verifyUser(AppUser user, RegistrationInfo input) {
        boolean verified = false;
        RegistrationInfo valFromCache = retrieveOtpObj(input);

        if(valFromCache != null) {
            String mobNum = valFromCache.getMobileNum();
            if (input.getOtp().equals(valFromCache.getOtp())) {
                long inUseTime = System.currentTimeMillis() - valFromCache.getCreatedTime();
                if(inUseTime < EXPIRY_TIME_MSEC )
                    verified = true;
            }
        }
        // If matched, and not expired, return true, else false. TBD:mark otp as used
        return verified;
    }

    public static void main(String[] args) {
        RegistrationInfo info = new RegistrationInfo();
       // RedisOptions options = new RedisOptions()
//                .setConnectionString("redis://localhost:6379")
//                .setMaxPoolSize(8)
//                .setMaxPoolWaiting(32);
//
//        Redis client = Redis.createClient(vertx, options);
//        RedisAPI redisAPI = RedisAPI.api(client);
    }

}
