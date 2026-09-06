package com.folks.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_countries")
@IdClass(Country.CountryPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Country.selectAll", query = "SELECT * FROM fks_countries")
})
public class Country implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id", nullable = false, updatable = false, precision = 32)
    private Integer countryId;

    @Column(name = "country_code", nullable = false, updatable = true, length = 3)
    private String countryCode;

    @Column(name = "country_name", nullable = true, updatable = true, length = 128)
    private String countryName;

    @Column(name = "language_code", nullable = false, updatable = true, length = 3)
    private String languageCode;

    @Column(name = "language", nullable = false, updatable = true, length = 30)
    private String language;

    @Column(name = "locale_code", nullable = false, updatable = true, length = 8)
    private String localeCode;

    @Column(name = "currency_code", nullable = false, updatable = true, length = 3)
    private String currencyCode;

    @Column(name = "currency", nullable = false, updatable = true, length = 20)
    private String currency;

    @Column(name = "timezone", nullable = false, updatable = true, length = 32)
    private String timezone;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public Country() {}

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getCountryId() {
        return this.countryId;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getLocaleCode() {
        return this.localeCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public static class CountryPK {

        private Integer countryId;

        public CountryPK() {}

        public CountryPK(Integer countryId) {
            this.countryId = countryId;
        }

        public void setCountryId(Integer countryId) {
            this.countryId = countryId;
        }

        public Integer getCountryId() {
            return this.countryId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.countryId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CountryPK other = (CountryPK)obj;
            if (! Objects.equals(this.countryId, other.countryId)) {
                return false;
            }
            return true;
        }

    }
}