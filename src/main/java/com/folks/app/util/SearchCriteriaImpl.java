package com.folks.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class SearchCriteriaImpl implements SearchCriteria {
    
    private Boolean history = Boolean.FALSE;
    private Boolean asc = Boolean.TRUE;
    private String orderBy;
    private Integer offset = 0;
    private Integer limit = 100;
    
    private Map<String, List<Object>> params = new HashMap<>();
    
    SearchCriteriaImpl() {}
    
    public SearchCriteriaImpl params(QueryParams params) {
        Map<String, List<String>> tmp = params.entries();
        
        for (Map.Entry<String, List<String>> me : tmp.entrySet()) {
            String name = me.getKey();
            List<String> values = me.getValue();
            
            // Convert the name from camel case to snake case.
            name = name.replaceAll("(?<!^)(?=[A-Z])", "_").toLowerCase();
            
            // Convert to appropriate data type.
            if (! values.isEmpty()) {
                if (values.get(0).matches("\\d+")) {
                    List<Object> genValues = new ArrayList<>(values.size());
                    for (String val : values) {
                        genValues.add(Integer.valueOf(val));
                    }
                    this.params.put(name, genValues);
                }
                else if (values.get(0).equalsIgnoreCase("true") || values.get(0).equalsIgnoreCase("false")) {
                    List<Object> genValues = new ArrayList<>(values.size());
                    for (String val : values) {
                        genValues.add(Boolean.valueOf(val));
                    }
                    this.params.put(name, genValues);
                }
                else if (values.get(0).matches("^[+-]?\\d*\\.\\d+$")) {
                    List<Object> genValues = new ArrayList<>(values.size());
                    for (String val : values) {
                        genValues.add(Double.valueOf(val));
                    }
                    this.params.put(name, genValues);
                }
                else {
                    this.params.put(name, new ArrayList<Object>(values));
                }
            }
        }
        
        this.offset = params.offset();
        this.limit = params.limit();
        
        return this;
    }
    
    public SearchCriteriaImpl history(Boolean history) {
        this.history = history;
        return this;
    }
    
    public SearchCriteriaImpl asc(Boolean asc) {
        this.asc = asc;
        return this;
    }
    
    public SearchCriteriaImpl orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }
    
    public SearchCriteriaImpl offset(Integer offset) {
        this.offset = offset;
        return this;
    }
    
    public SearchCriteriaImpl limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Map<String, List<Object>> params() {
        return params;
    }

    @Override
    public Boolean history() {
        return history;
    }

    @Override
    public String orderBy() {
        return orderBy;
    }

    @Override
    public Boolean asc() {
        return asc;
    }

    @Override
    public Integer offset() {
        return offset;
    }

    @Override
    public Integer limit() {
        return limit;
    }
    
}
