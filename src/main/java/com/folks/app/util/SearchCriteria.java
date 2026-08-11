package com.folks.app.util;

import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public interface SearchCriteria {
    
    static SearchCriteria from(QueryParams params) {
        SearchCriteriaImpl search = new SearchCriteriaImpl();
        search.params(params);
        
        return search;
    }
    
    static SearchCriteria from(QueryParams params, Integer userId) {
        SearchCriteriaImpl search = new SearchCriteriaImpl();
        search.params(params, userId);
        
        return search;
    }
    
    /**
     * Return a map of search key parameters.
     * @return Map
     */
    Map<String, List<Object>> params();
    
    /**
     * Indicate if historical data needs to be fetched.
     * @return boolean
     */
    Boolean history();
    
    /**
     * Return the order by attribute to be used to sort the filtered records.
     * @return String
     */
    String orderBy();
    
    /**
     * If the filtered records to be sorted in ascending order.
     * @return Boolean
     */
    Boolean asc();
    
    /**
     * The start offset.
     * @return Integer
     */
    Integer offset();
    
    /**
     * Number of records to be fetched.
     * @return Integer
     */
    Integer limit();
    
}
