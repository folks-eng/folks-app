package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.PaymentDAO;
import com.folks.app.model.Payment;
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
public class PaymentBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentBO.class);
    
    private final PaymentDAO paymentDAO;

    public PaymentBO() {
        this.paymentDAO = DAOProxy.get(PaymentDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. PaymentDAO: {}", getClass().getSimpleName(), paymentDAO);
        }
    }

    public Payment create(AppUser usr, Payment payment) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        
        paymentDAO.insert(payment);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Payment created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return payment;
    }

    public void create(AppUser usr, List<Payment> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        paymentDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Payment record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Payment modify(AppUser usr, Payment payment) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Payment existing = paymentDAO.find(new Payment.PaymentPK(payment.getPaymentId()));
        if (existing == null) {
            throw new IllegalArgumentException("No payment found for identifier: " + payment.getPaymentId());
        }
        // Update attributes of existing record
        existing.setBookingId(payment.getBookingId());
        existing.setAmount(payment.getAmount());
        existing.setPaymentMethod(payment.getPaymentMethod());
        existing.setPaymentStatus(payment.getPaymentStatus());
        existing.setTransactionRef(payment.getTransactionRef());
        existing.setPaidAt(payment.getPaidAt());

        paymentDAO.update(payment);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Payment record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Payment> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Payment> rows = paymentDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded payment record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Payment view(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Payment payment = paymentDAO.find(new Payment.PaymentPK(id));
        if (payment == null) {
            throw new IllegalArgumentException("No Payment found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched payment details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return payment;
    }

    public Payment remove(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Payment payment = paymentDAO.find(new Payment.PaymentPK(id));

        if (payment == null) {
            throw new IllegalArgumentException("No payment found for id: " + id);
        }
        paymentDAO.delete(payment);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Payment. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return payment;
    }
}
