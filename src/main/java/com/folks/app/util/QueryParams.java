package com.folks.app.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that encapsulates the http query parameter set and their corresponding values.
 *
 * @author schan280
 */
public final class QueryParams {
    
    private final Map<String, List<String>> params;
    
    private Boolean expand = Boolean.FALSE;
    private Integer offset = 0;
    private Integer limit = 100;
    
    public QueryParams(Map<String, List<String>> params) {
        this.params = params;
        if (this.params.containsKey("offset")) {
            offset = Integer.valueOf(this.params.remove("offset").get(0));
        }
        if (this.params.containsKey("limit")) {
            limit = Integer.valueOf(this.params.remove("limit").get(0));
        }
        if (this.params.containsKey("expand")) {
            expand = Boolean.valueOf(this.params.remove("expand").get(0));
        }
    }
    
    public Boolean contains(String key) {
        return params.containsKey(key);
    }
    
    public Set<String> keys() {
        return params.keySet();
    }
    
    /**
     * Retrieve the parameter value for this key.
     * If no value is found, then this method will return null.
     * 
     * @param key       Param key.
     * @return  Value against this key.
     */
    public String param(String key) {
        if (params == null) {
            throw new IllegalStateException("Query paramter map is not yet populated");
        }
        List<String> tmp = params.get(key);
        if (tmp != null && ! tmp.isEmpty()) {
            return tmp.get(0);
        }
        return null;
    }
    
    /**
     * Retrieve the parameter values for this key.
     * If no value is found, then this method will return null.
     * 
     * @param key       Param key.
     * @return  Value against this key.
     */
    public List<String> params(String key) {
        if (params == null) {
            throw new IllegalStateException("Query paramter map is not yet populated");
        }
        return params.get(key);
    }
    
    public Boolean isEmpty() {
        return params.isEmpty();
    }
    
    public Map<String, List<String>> entries() {
        return params;
    }

    public Boolean expand() {
        return expand;
    }
    
    public Integer offset() {
        return offset;
    }

    public Integer limit() {
        return limit;
    }

    @Override
    public String toString() {
        return params.toString();
    }
}
