package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.cache.impl.CityCache;
import com.folks.app.cache.impl.ProvinceCache;
import com.folks.app.model.Neighbourhood;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import org.javalabs.decl.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.folks.app.dao.NeighbourhoodDAO;
import com.folks.app.model.City;
import com.folks.app.model.Province;

/**
 *
 * @author schan280
 */
public class NeighbourhoodBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighbourhoodBO.class);
    
    private final NeighbourhoodDAO neighbourhoodDAO;

    public NeighbourhoodBO() {
        this.neighbourhoodDAO = DAOProxy.get(NeighbourhoodDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized NeighbourhoodBO: {}. NeighbourhoodDAO: {}", getClass().getSimpleName(), neighbourhoodDAO);
        }
    }

    public Neighbourhood create(AppUser usr, Neighbourhood neighbourhood) throws IllegalAccessException {
        // Only admin has the privilege to register a neighbourhood.
        ensureAdmin(usr);
        validateScope(usr, "neighbourhood:create");
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        if (neighbourhood.getCreatedAt() == null) {
            neighbourhood.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }
        
        neighbourhoodDAO.insert(neighbourhood);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("OperatingNeighbourhood created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return neighbourhood;
    }

    public void create(AppUser usr, List<Neighbourhood> records) throws IllegalAccessException {
        // Only admin has the privilege to register a neighbourhood.
        ensureAdmin(usr);
        validateScope(usr, "neighbourhood:create");
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        for (Neighbourhood neighbourhood : records) {
            if (neighbourhood.getCreatedAt() == null) {
                neighbourhood.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        
        neighbourhoodDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} OperatingNeighbourhood record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Neighbourhood modify(AppUser usr, Neighbourhood neighbourhood) throws IllegalAccessException {
        // Only admin has the privilege to register a neighbourhood.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Neighbourhood existing = neighbourhoodDAO.find(new Neighbourhood.NeighbourhoodPK(neighbourhood.getNeighbourhoodId()));
        if (existing == null) {
            throw new IllegalArgumentException("No neighbourhood found for identifier: " + neighbourhood.getNeighbourhoodId());
        }
        // Update attributes of existing record
        existing.setCityId(neighbourhood.getCityId());
        existing.setIsServiceable(neighbourhood.getIsServiceable());
        existing.setLatitude(neighbourhood.getLatitude());
        existing.setLongitude(neighbourhood.getLongitude());
        existing.setLocality(neighbourhood.getLocality());
        existing.setPincode(neighbourhood.getPincode());
        existing.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        neighbourhoodDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("OperatingNeighbourhood record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Neighbourhood> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Neighbourhood> records = neighbourhoodDAO.query(search);
        
        for (Neighbourhood record : records) {
            City city = CityCache.getCache().get(record.getCityId());
            Province province = ProvinceCache.getCache().get(city.getProvinceId());
            record.setProvince(province.getProvinceName());
            record.setCity(city.getCityName());
        }

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded neighbourhood record(s). Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
        return records;
    }

    public Neighbourhood view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Neighbourhood neighbourhood = neighbourhoodDAO.find(new Neighbourhood.NeighbourhoodPK(id));
        if (neighbourhood == null) {
            throw new IllegalArgumentException("No OperatingNeighbourhood found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched neighbourhood details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return neighbourhood;
    }

    public Neighbourhood remove(AppUser usr, Integer id) throws IllegalAccessException {
        // Only admin has the privilege to register a neighbourhood.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Neighbourhood neighbourhood = neighbourhoodDAO.find(new Neighbourhood.NeighbourhoodPK(id));

        if (neighbourhood == null) {
            throw new IllegalArgumentException("No neighbourhood found for id: " + id);
        }
        neighbourhoodDAO.delete(neighbourhood);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted OperatingNeighbourhood. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return neighbourhood;
    }
}
