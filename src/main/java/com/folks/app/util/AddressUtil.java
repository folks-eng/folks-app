package com.folks.app.util;

import com.folks.app.cache.impl.CityCache;
import com.folks.app.cache.impl.NeighbourhoodCache;
import com.folks.app.cache.impl.ProvinceCache;
import com.folks.app.model.Address;
import com.folks.app.model.City;
import com.folks.app.model.Neighbourhood;
import com.folks.app.model.Province;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sudip
 */
public class AddressUtil {
    
    private static final Map<String, String> ABBREVIATIONS = Map.of(
            "apt", "apartment",
            "fl", "flat",
            "no", "number",
            "#", "number",
            "rd", "road",
            "st", "street",
            "str", "street",
            "blk", "block",
            "bldg", "building"
    );
    
    public static Boolean compare(String s1, String s2) {
        List<String> tokens_1 = normalize(s1);
        List<String> tokens_2 = normalize(s2);
        
        if (tokens_1.size() != tokens_2.size()) {
            return Boolean.FALSE;
        }
        
        for (int i = 0; i < tokens_1.size(); i ++) {
            String t1 = tokens_1.get(i);
            String t2 = tokens_2.get(i);
            if (! t1.equals(t2)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
    
    public static void enrich(Address address) {
        Neighbourhood nbhood = NeighbourhoodCache.getCache().get(address.getNeighbourhoodId());
        City city = CityCache.getCache().get(nbhood.getCityId());
        Province province = ProvinceCache.getCache().get(city.getProvinceId());

        address.setProvince(province.getProvinceName());
        address.setCity(city.getCityName());
        address.setLocality(nbhood.getLocality());
        address.setPincode(nbhood.getPincode());
    }

    public static List<String> normalize(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        // Unicode normalization
        String value = Normalizer.normalize(input, Normalizer.Form.NFKC);

        // Lowercase
        value = value.toLowerCase();

        // Replace punctuation/separators with spaces
        value = value.replaceAll("[^a-z0-9]+", " ");

        // Collapse whitespace
        value = value.replaceAll("\\s+", " ").trim();

        // Normalize individual tokens
        List<String> result = new ArrayList<>();

        for (String token : value.split(" ")) {
            String replacement = ABBREVIATIONS.getOrDefault(token, token);
            result.add(replacement);
        }
        Collections.sort(result);
        
        return result;
    }
}
