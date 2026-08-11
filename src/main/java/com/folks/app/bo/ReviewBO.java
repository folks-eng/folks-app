package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.ReviewDAO;
import com.folks.app.model.Review;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ReviewBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewBO.class);
    
    private final ReviewDAO reviewDAO;

    public ReviewBO() {
        this.reviewDAO = DAOProxy.get(ReviewDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. ReviewDAO: {}", getClass().getSimpleName(), reviewDAO);
        }
    }

    public Review create(AppUser usr, Review review) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }

        reviewDAO.insert(review);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Review created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return review;
    }

    public void create(AppUser usr, List<Review> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        for (Review review : records) {
            if (review.getCreatedAt() == null) {
                review.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        reviewDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Review record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Review modify(AppUser usr, Review review) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Review existing = reviewDAO.find(new Review.ReviewPK(review.getReviewId()));
        if (existing == null) {
            throw new IllegalArgumentException("No review found for identifier: " + review.getReviewId());
        }
        // Update attributes of existing record
        existing.setBookingId(review.getBookingId());
        existing.setCustomerId(review.getCustomerId());
        existing.setProfessionalId(review.getProfessionalId());
        existing.setRating(review.getRating());
        existing.setComment(review.getComment());

        reviewDAO.update(review);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Review record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Review> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Review> rows = reviewDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded review record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Review view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Review review = reviewDAO.find(new Review.ReviewPK(id));
        if (review == null) {
            throw new IllegalArgumentException("No Review found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched review details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return review;
    }

    public Review remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Review review = reviewDAO.find(new Review.ReviewPK(id));

        if (review == null) {
            throw new IllegalArgumentException("No review found for id: " + id);
        }
        reviewDAO.delete(review);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Review. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return review;
    }
}
