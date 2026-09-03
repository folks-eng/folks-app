package com.folks.app.util;

import java.sql.Date;
import java.sql.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class SearchCriteriaImpl implements SearchCriteria {
    
    private Boolean fetchDependency = Boolean.FALSE;
    private Boolean history = Boolean.FALSE;
    private Boolean asc = Boolean.TRUE;
    private String operator = "and";
    private String orderBy;
    private Integer offset = 0;
    private Integer limit = Constants.DEFAULT_SEARCH_LIMIT;
    
    private final Map<String, List<Object>> params = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchCriteriaImpl.class);
    
    SearchCriteriaImpl() {}
    
    public SearchCriteriaImpl params(QueryParams params, String key, Integer userId) {
        Map<String, List<String>> tmp = params.entries();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Search params {}", params);
        }

        List<String> ops = tmp.remove("operator");
        List<String> orderBy = tmp.remove("orderBy");
        List<String> asc = tmp.remove("asc");
        List<String> deps = tmp.remove("fetchDependency");
        
        if (ops != null) {
            this.operator = ops.get(0).toLowerCase();
        }
        if (orderBy != null) {
            this.orderBy = orderBy.get(0);
        }
        if (asc != null) {
            this.asc = Boolean.valueOf(asc.get(0));
        }
        if (deps != null) {
            this.fetchDependency = Boolean.valueOf(deps.get(0));
        }
        for (Map.Entry<String, List<String>> me : tmp.entrySet()) {
            String name = me.getKey();
            List<String> values = me.getValue();
            
            // Convert the name from camel case to snake case.
            name = name.replaceAll("(?<!^)(?=[A-Z])", "_").toLowerCase();
            
            // Convert to appropriate data type.
            if (! values.isEmpty()) {
                if (values.get(0).matches("\\d+") && ! name.equals("phone1") && ! name.equals("phone2")) {
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
                else if (values.get(0).matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
                    List<Object> genValues = new ArrayList<>(values.size());
                    for (String val : values) {
                        genValues.add(Date.valueOf(val));
                    }
                    this.params.put(name, genValues);
                }
                else if (values.get(0).matches("^(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$")) {
                    List<Object> genValues = new ArrayList<>(values.size());
                    for (String val : values) {
                        genValues.add(Time.valueOf(val));
                    }
                    this.params.put(name, genValues);
                }
                else {
                    this.params.put(name, new ArrayList<Object>(values));
                }
            }
        }
        if (key != null && userId != null) {
            key = key.replaceAll("(?<!^)(?=[A-Z])", "_").toLowerCase();
            this.params.put(key, Arrays.asList(userId));
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

    @Override
    public Boolean fetchDependency() {
        return fetchDependency;
    }

    @Override
    public String operator() {
        return operator;
    }
    
}
