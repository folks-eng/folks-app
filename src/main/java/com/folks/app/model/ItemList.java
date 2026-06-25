package com.folks.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) 
public class ItemList {
    
    private int total;
    private List<Object> items;
    private String canonicalLink;
    
    private boolean hasMore;
    private String nextLink;
    private String previousLink;
    
    public ItemList() {}
 
    public ItemList(int total, List<Object> items, String canonicalLink) {
        this.total = total;
        this.items = items;
        this.canonicalLink = canonicalLink;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Object> getItems() {
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }

    public String getCanonicalLink() {
        return canonicalLink;
    }

    public void setCanonicalLink(String canonicalLink) {
        this.canonicalLink = canonicalLink;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }

    public String getPreviousLink() {
        return previousLink;
    }

    public void setPreviousLink(String previousLink) {
        this.previousLink = previousLink;
    }
}

