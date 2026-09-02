package com.folks.app.util;

import org.javalabs.jpa.util.MD5HashGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author schan280
 */
public class MD5HashTest {
    
    @Test
    public void testGen() {
        String mobile = "9999999999";
        String email = "folks@folks-app.com";
        
        String hashed = MD5HashGenerator.digest(mobile, email).toLowerCase();
        
        int idx = 0;
        char[] arr = new char[hashed.length() + 4];
        for (byte i = 0; i < hashed.length(); i ++) {
            arr[idx ++] = hashed.charAt(i);
            if (i == 7 ||  i == 11 || i == 15 || i == 19) {
                arr[idx ++] = '-';
            }
        }
        String id = new String(arr);
        assertEquals(36, id.length());
    }
}