package com.folks.app.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folks.app.model.SignupInfo;
import com.folks.app.model.SmsServiceResponse;
import com.folks.app.util.Constants;
import io.vertx.core.Future;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.model.ServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.folks.app.auth.AppUser;

/**
 *
 * @author schan280
 */
public class SignupBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SignupBO.class);

    private final RedisAPI redisAPI;

    public SignupBO(RedisAPI redisAPI) {
        this.redisAPI = redisAPI;
       // this.mapper = new ObjectMapper();
    }

    // Assume user is new
    // TBD : for existing user.
    public Future<SmsServiceResponse> genOtp(AppUser user, SignupInfo regInfo) {
        String mobileNum = regInfo.getMobileNum();
        String otp = null;
        // 1. Synchronous validation
        if (mobileNum == null || mobileNum.trim().isEmpty()) {
            return Future.failedFuture(new IllegalArgumentException("Mobile number is required."));
        }
        if (!Constants.MOBILENUM_PATTERN.matcher(mobileNum).matches()) {
            return Future.failedFuture(new IllegalArgumentException("Mobile number invalid." + mobileNum));
        }

        // 2. Prepare data, Generate 4 digit random number, store it in Redis (MobileNum vs RegInfo obj)
        SecureRandom rand = new SecureRandom();
        otp = String.format("%04d", rand.nextInt(10000));
        regInfo.setOtp(otp);
        regInfo.setCreatedTime(System.currentTimeMillis());
        LOGGER.info(" OTP generated " +regInfo.getOtp());
        System.out.println(" OTP generated " +regInfo.getOtp());

        // 3. Chain asynchronous operations
        return storeRegInfo(regInfo)
                //.compose(v -> Future.<SmsServiceResponse>succeededFuture(null));
                .compose(v -> {
                        //Now that storage to Redis is complete, send the SMS
                        return sendOtpSms(regInfo);
                });
        // Return the info object if everything succeeded
                //.map(v -> regInfo);
    }

    private Future<Void> storeRegInfo(SignupInfo regInfo) {
        try {
            //String jsonString = mapper.writeValueAsString(regInfo);
            byte[] jsonBytes = MapperUtil.encode(regInfo);
            String jsonString = new String(jsonBytes);

            //return redisAPI.setex(regInfo.getMobileNum(), EXPIRY_TIME_SEC, jsonString).mapEmpty();
            // If setex is used, then we cannot diff between calling /verify without generating OTP.
            return redisAPI.set(List.of(regInfo.getMobileNum(), jsonString))
                    .onSuccess(response -> System.out.println("Stored to REDIS " +jsonString))
                    .onFailure(err -> System.err.println("Failed to store to Redis: " + err.getMessage()))
                    .mapEmpty();
        } catch (RuntimeException e) {
            return Future.failedFuture(e);
        }
    }

    // input has mobile, otp
    public Future<ServerMessage> verifyUser(AppUser user, SignupInfo input) {
        boolean verified = false;
        ServerMessage msg = new ServerMessage();
        String providedOtp = input.getOtp();
        //retrieveRegInfo(input);

        // Retrieve from Redis asynchronously
        return retrieveRegInfo(input).map(storedInfo -> {
            // 1. Check if record exists (Redis returns null if key expired)
            if (storedInfo == null) {
                // System.out.println("Otp does not exist.");
                msg.setCode(HttpURLConnection.HTTP_UNAUTHORIZED);
                msg.setMessage("Otp does not exist.");
                return msg;
            }
            else {
                // 2. Check OTP expiry
                if( (System.currentTimeMillis() - storedInfo.getCreatedTime()) > Constants.OTP_EXPIRY_TIME_MSEC) {
                    //System.out.println("OTP expired");
                    msg.setCode(HttpURLConnection.HTTP_UNAUTHORIZED);
                    msg.setMessage("OTP expired");
                    return msg;
                }
                else {
                    System.out.println(providedOtp + ":" + storedInfo.getOtp());
                    if(providedOtp.equals(storedInfo.getOtp())) {
                        msg.setCode(HttpURLConnection.HTTP_OK);
                        msg.setMessage("Otp Verified Successfully.");
                        return msg;
                    }
                    else {
                        msg.setCode(HttpURLConnection.HTTP_UNAUTHORIZED);
                        msg.setMessage("Otp invalid. ");
                        return msg;
                    }
                }
            }
        });
    }

    // Get otp for this mobile number from Redis.
    private Future<SignupInfo> retrieveRegInfo(SignupInfo regInfo) {
        String mobileNum = regInfo.getMobileNum();
        //System.out.println("In retrieveRegInfo, mobile " +regInfo.getMobileNum());
        return redisAPI.get(mobileNum)
                .onSuccess(response -> System.out.println("Retrieved from Redis: "
                                + (response == null ? "null (key not found)" : response.toString())))
                .onFailure(err -> System.err.println("Redis GET failed for " + mobileNum + ": " + err.getMessage()))
                .map(response -> {
                    if (response == null)
                        return null; // key not found
                    try {
                        return MapperUtil.decode(response.toBytes(), SignupInfo.class);
                        //return mapper.readValue(response.toString(), SignupInfo.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to deserialize SignupInfo", e);
                    }
                });
    }

    private Future<SmsServiceResponse> sendOtpSms(SignupInfo regInfo) {
        SmsServiceResponse smsResponse = new SmsServiceResponse();
        try{
           String url = "https://api.twilio.com/2010-04-01/Accounts/AC6652c6042fce71b1e04ad41e9c308403/Messages.json";
           String user = "AC6652c6042fce71b1e04ad41e9c308403";     // Replace with provider Account SID
           String authToken = "248467df4befcbb0013b8c26d73bc3cf"; // Replace with provider Auth Token

           // 1. Prepare form data
           Map<String, String> formData = Map.of(
                   "To", "+919980377574", //Replace with regInfo.getMobileNum()
                   "From", "+14782238323",
                   "Body", "Testing with Otp " + regInfo.getOtp()// Replace with Template ID and otp
           );
           String formBody = formData.entrySet().stream()
                   .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                           URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                   .collect(Collectors.joining("&"));
           //From=%2B14782238323&To=%2B919980377574&Body=Testing+from+vertx
           System.out.println("In Sending SMS, Form body: " +formBody);

           // 2. Create Basic Auth Header
           String auth = user + ":" + authToken;
           String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

           // 3. Build and send request
           HttpRequest request = HttpRequest.newBuilder()
                   .uri(URI.create(url))
                   .header("Authorization", "Basic " + encodedAuth)
                   .header("Content-Type", "application/x-www-form-urlencoded")
                   .timeout(Duration.of(5, ChronoUnit.SECONDS))
                   .POST(HttpRequest.BodyPublishers.ofString(formBody))
                   .build();

           HttpClient client = HttpClient.newHttpClient();
           HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
           String respString = response.body();
           System.out.println(" SMS Response: " + respString);

           ObjectMapper objectMapper = new ObjectMapper();
           // Parse the JSON string into a tree structure
           JsonNode rootNode = objectMapper.readTree(respString);

           // Extract values by key names and cast them to specific types.
            smsResponse.setHttpStatusCode(String.valueOf(response.statusCode()));
           smsResponse.setErrorCode(rootNode.get("error_code").asText());
           smsResponse.setErrorMsg(rootNode.get("error_message").asText());
            System.out.println("RETURNING AFTER SENDING SMS");
           return Future.succeededFuture(smsResponse);
       } catch (Exception e) {
           e.printStackTrace();
           return Future.failedFuture(e.getCause());
       }
    }

    public static void main(String[] args) {
        try {
            String url = "https://api.twilio.com/2010-04-01/Accounts/AC6652c6042fce71b1e04ad41e9c308403/Messages.json";
            String user = "AC6652c6042fce71b1e04ad41e9c308403";     // Replace with your Twilio Account SID
            String authToken = "248467df4befcbb0013b8c26d73bc3cf"; // Replace with your Twilio Auth Token

            // 1. Prepare form data
            Map<String, String> formData = Map.of(
                    "To", "+919980377574",
                    "From", "+14782238323",
                    "Body", "Testing from vertx"
            );
            String formBody = formData.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                            URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
            System.out.println("Form body: " +formBody);

            // 2. Create Basic Auth Header
            String auth = user + ":" + authToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            // 3. Build and send request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .timeout(Duration.of(5, ChronoUnit.SECONDS))
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
           HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code: " + response.statusCode());
            String respString = response.body();
            System.out.println(" Response: " + respString);

            ObjectMapper objectMapper = new ObjectMapper();

            // Parse the JSON string into a tree structure
            JsonNode rootNode = objectMapper.readTree(respString);

            // Extract values by key names and cast them to specific types
            JsonNode errorCode = rootNode.get("error_code");
            System.out.println(errorCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
