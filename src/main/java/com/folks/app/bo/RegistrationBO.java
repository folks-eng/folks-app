package com.folks.app.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folks.app.model.SmsServiceResponse;
import io.vertx.core.Future;
import io.vertx.redis.client.RedisAPI;
import org.javalabs.decl.util.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import io.vertx.redis.client.RedisOptions;

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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public RegistrationBO(RedisAPI redisAPI) {
        this.redisAPI = redisAPI;
       // this.mapper = new ObjectMapper();
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

        // 3. Chain asynchronous operations
        return storeRegInfo(regInfo)
                .compose(v -> {
                    // Now that storage to Redis is complete, send the SMS
                    System.out.println("SENDING SMS");
                    return sendOtpSms(regInfo);
                })
                .map(v -> regInfo); // Finally return the info object if everything succeeded
    }

    private Future<Void> storeRegInfo(RegistrationInfo regInfo) {
        try {
            //String jsonString = mapper.writeValueAsString(regInfo);
            byte[] jsonBytes = MapperUtil.encode(regInfo);
            String jsonString = new String(jsonBytes);
            // Use setex to handle expiration automatically in Redis (e.g., 5 minutes)
            return redisAPI.setex(regInfo.getMobileNum(), EXPIRY_TIME_SEC, jsonString).mapEmpty();
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }

    private Future<SmsServiceResponse> sendOtpSms(RegistrationInfo regInfo) {
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
           String respString = response.body();
           System.out.println(" Response: " + respString);

           ObjectMapper objectMapper = new ObjectMapper();
           // Parse the JSON string into a tree structure
           JsonNode rootNode = objectMapper.readTree(respString);

           // Extract values by key names and cast them to specific types
             smsResponse.setStatusCode(String.valueOf(response.statusCode()));

            System.out.println("Status Code: " + response.statusCode());
           String errorCode = rootNode.get("error_code").asText();
           smsResponse.setErrorCode(errorCode);
           smsResponse.setErrorMsg(rootNode.get("error_message").asText());
            System.out.println("RETURNEING AFTER SENDING");
           return Future.succeededFuture(smsResponse);
       } catch (Exception e) {
           e.printStackTrace();
           return Future.failedFuture(e.getCause());
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
                         return MapperUtil.decode(response.toBytes(), RegistrationInfo.class);
                         //return mapper.readValue(response.toString(), RegistrationInfo.class);
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
