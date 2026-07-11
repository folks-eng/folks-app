package com.folks.app.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.Response;
import org.javalabs.decl.util.MapperUtil;
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

    private static final String EXPIRY_TIME_SEC = "300"; //5 mins

    private final RedisAPI redisAPI;

    private final ObjectMapper mapper;

//    public RegistrationBO() {
//        otpCache = new HashMap<>();
//    }

    public RegistrationBO(RedisAPI redisAPI) {
//        RedisOptions options = new RedisOptions()
//                .setConnectionString("redis://localhost:6379")
//                .setMaxPoolSize(8)
//                .setMaxPoolWaiting(32);
//
//        Redis client = Redis.createClient(vertx, options);
        this.redisAPI = redisAPI;
        this.mapper = new ObjectMapper();
    }

    // Assume user is new
    // TBD : for existing user.
    public Future<RegistrationInfo> registerUser(AppUser user, RegistrationInfo regInfo) {
        String mobileNum = regInfo.getMobileNum();
        String otp = null;
        // 1. Synchronous validation
        if (mobileNum == null || mobileNum.trim().isEmpty()) {
            return Future.failedFuture(new IllegalArgumentException("Mobile number is required."));
        }
        if (!MOBILENUM_PATTERN.matcher(mobileNum).matches()) {
            return Future.failedFuture(new IllegalArgumentException("Mobile number invalid." + mobileNum));
        }

        // 2. Prepare data, Generate 4 digit random number, store it in Redis
        SecureRandom rand = new SecureRandom();
        otp = String.format("%04d", rand.nextInt(10000));
        regInfo.setOtp(otp);
        //regInfo.setCreatedTime(System.currentTimeMillis());
        System.out.println(" OTP generated " +regInfo.getOtp());

        //storeOtp(regInfo);
        // 3. Chain asynchronous operations
        return storeRegInfo(regInfo)
                .compose(v -> {
                    // Now that storage to Redis is complete, send the SMS
                    return sendOtpSms(regInfo);
                })
                .map(v -> regInfo); // Finally return the info object if everything succeeded
    }

    private Future<Void> storeRegInfo(RegistrationInfo regInfo) {
        try {
            String jsonString = mapper.writeValueAsString(regInfo);
            //byte[] jsonBytes = MapperUtil.encode(regInfo);
            //jsonString = new String(jsonBytes);
            // Use setex to handle expiration automatically in Redis (e.g., 5 minutes)
            return redisAPI.setex(regInfo.getMobileNum(), EXPIRY_TIME_SEC, jsonString).mapEmpty();
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }

    // Get otp for this mobile number from Redis.
    private Future<RegistrationInfo> retrieveRegInfo(RegistrationInfo regInfo) {
        String mobileNum = regInfo.getMobileNum();
        return redisAPI.get(mobileNum)  //Redis returns null if key expired
                .map(response -> {
                    if (response == null)
                        return null; // key not found
                    try {
                        return mapper.readValue(response.toString(), RegistrationInfo.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to deserialize RegistrationInfo", e);
                    }
                });
    }

    // input has mobile, otp
    public Future<Boolean>  verifyUser(AppUser user, RegistrationInfo input) {
        boolean verified = false;
        String providedOtp = input.getOtp();
        retrieveRegInfo(input);

        // Retrieve from Redis asynchronously
        return retrieveRegInfo(input).map(storedInfo -> {
            // 1. Check if record exists (Redis returns null if key expired)
            if (storedInfo == null) {
                System.out.println("Key does not exist or expired");
                return false;
            }
            // 2. Compare OTPs
            System.out.println(providedOtp + ":" +storedInfo.getOtp());
            return storedInfo.getOtp().equals(providedOtp);
        });
    }

    private Future<RegistrationInfo> sendOtpSms(RegistrationInfo regInfo) {
        return Future.succeededFuture(regInfo);
    }

//    private void storeOtp(RegistrationInfo regInfo) {
//        otpCache.put(String.valueOf(regInfo.getMobileNum().trim()), regInfo);
//    }
//
//
//
//    // Get otp for this mobile number from Map.
//    private RegistrationInfo retrieveOtpObj(RegistrationInfo input) {
//        Optional<String> mobileNumOpt = Optional.ofNullable(input.getMobileNum());
//        RegistrationInfo valFromCache = null;
//        //.orElseThrow(() -> new DataNotFoundException("Mobile number is required."));
//        if( mobileNumOpt.isPresent() && !mobileNumOpt.get().trim().isEmpty())
//            valFromCache = otpCache.get(input.getMobileNum().trim());
//        return valFromCache;
//    }

    public static void main(String[] args) {
    }

}
