package com.folks.app.util;

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
    
    private Map<String, List<String>> params = new HashMap<>();
    
    SearchCriteriaImpl() {}
    
    public SearchCriteriaImpl params(QueryParams params) {
        this.params = params.entries();
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
    public Map<String, List<String>> params() {
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
