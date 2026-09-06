package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.model.Country;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import org.javalabs.decl.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.folks.app.dao.CountryDAO;

/**
 *
 * @author schan280
 */
public class CountryBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryBO.class);
    
    private final CountryDAO cityDAO;

    public CountryBO() {
        this.cityDAO = DAOProxy.get(CountryDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized CountryBO: {}. OperatingCountryDAO: {}", getClass().getSimpleName(), cityDAO);
        }
    }

    public Country create(AppUser usr, Country country) throws IllegalAccessException {
        // Only admin has the privilege to register a country.
        ensureAdmin(usr);
        validateScope(usr, "country:create");
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        if (country.getCreatedAt() == null) {
            country.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }
        
        cityDAO.insert(country);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("OperatingCountry created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return country;
    }

    public void create(AppUser usr, List<Country> records) throws IllegalAccessException {
        // Only admin has the privilege to register a country.
        ensureAdmin(usr);
        validateScope(usr, "country:create");
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        for (Country country : records) {
            if (country.getCreatedAt() == null) {
                country.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        
        cityDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} OperatingCountry record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Country modify(AppUser usr, Country country) throws IllegalAccessException {
        // Only admin has the privilege to register a country.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Country existing = cityDAO.find(new Country.CountryPK(country.getCountryId()));
        if (existing == null) {
            throw new IllegalArgumentException("No country found for identifier: " + country.getCountryId());
        }
        // Update attributes of existing record
        existing.setCountryName(country.getCountryName());
        existing.setCurrencyCode(country.getCurrencyCode());
        existing.setCurrency(country.getCurrency());
        existing.setLanguageCode(country.getLanguageCode());
        existing.setLanguage(country.getLanguage());
        existing.setLocaleCode(country.getLocaleCode());
        existing.setTimezone(country.getTimezone());
        existing.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        cityDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("OperatingCountry record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Country> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Country> rows = cityDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded country record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Country view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Country country = cityDAO.find(new Country.CountryPK(id));
        if (country == null) {
            throw new IllegalArgumentException("No OperatingCountry found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched country details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return country;
    }

    public Country remove(AppUser usr, Integer id) throws IllegalAccessException {
        // Only admin has the privilege to register a country.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Country country = cityDAO.find(new Country.CountryPK(id));

        if (country == null) {
            throw new IllegalArgumentException("No country found for id: " + id);
        }
        cityDAO.delete(country);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted OperatingCountry. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return country;
    }
}
