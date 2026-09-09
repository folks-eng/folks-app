package com.folks.app.cache;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author schan280
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CacheConfig {

    @XmlAttribute(name = "class", required = true)
    private String clazz;

    @XmlAttribute(required = false)
    private String entity;

    @XmlAttribute(required = false)
    private String query;

    @XmlAttribute(required = false)
    private String idProperty;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(String idProperty) {
        this.idProperty = idProperty;
    }

    
}