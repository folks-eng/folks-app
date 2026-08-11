package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.JobStatusDAO;
import com.folks.app.model.JobStatus;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class JobStatusBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JobStatusBO.class);
    
    private final JobStatusDAO jobStatusDAO;

    public JobStatusBO() {
        this.jobStatusDAO = DAOProxy.get(JobStatusDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. JobStatusDAO: {}", getClass().getSimpleName(), jobStatusDAO);
        }
    }

    public JobStatus create(AppUser usr, JobStatus jobStatus) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        
        jobStatusDAO.insert(jobStatus);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("JobStatus created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return jobStatus;
    }

    public void create(AppUser usr, List<JobStatus> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        jobStatusDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} JobStatus record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public JobStatus modify(AppUser usr, JobStatus jobStatus) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        JobStatus existing = jobStatusDAO.find(new JobStatus.JobStatusPK(jobStatus.getLogId()));
        if (existing == null) {
            throw new IllegalArgumentException("No jobStatus found for identifier: " + jobStatus.getLogId());
        }
        // Update attributes of existing record
        existing.setBookingId(jobStatus.getBookingId());
        existing.setStatus(jobStatus.getStatus());
        existing.setUpdatedBy(jobStatus.getUpdatedBy());

        jobStatusDAO.update(jobStatus);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("JobStatus record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<JobStatus> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<JobStatus> rows = jobStatusDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded jobStatus record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public JobStatus view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        JobStatus jobStatus = jobStatusDAO.find(new JobStatus.JobStatusPK(id));
        if (jobStatus == null) {
            throw new IllegalArgumentException("No JobStatus found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched jobStatus details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return jobStatus;
    }

    public JobStatus remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        JobStatus jobStatus = jobStatusDAO.find(new JobStatus.JobStatusPK(id));

        if (jobStatus == null) {
            throw new IllegalArgumentException("No jobStatus found for id: " + id);
        }
        jobStatusDAO.delete(jobStatus);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted JobStatus. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return jobStatus;
    }
}
