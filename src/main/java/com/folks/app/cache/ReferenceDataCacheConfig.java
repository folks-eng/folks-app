package com.folks.app.cache;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 *
 * @author schan280
 */
@XmlRootElement(name = "reference-data-cache")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReferenceDataCacheConfig {

    @XmlElement(name = "cache")
    private List<CacheConfig> caches;

    public List<CacheConfig> getCaches() {
        return caches;
    }

    public void setCaches(List<CacheConfig> caches) {
        this.caches = caches;
    }
}