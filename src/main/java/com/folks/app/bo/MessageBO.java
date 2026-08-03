package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.MessageDAO;
import com.folks.app.model.Message;
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
public class MessageBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBO.class);
    
    private final MessageDAO messageDAO;

    public MessageBO() {
        this.messageDAO = DAOProxy.get(MessageDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. MessageDAO: {}", getClass().getSimpleName(), messageDAO);
        }
    }

    public Message create(AppUser usr, Message message) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        
        messageDAO.insert(message);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Message created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return message;
    }

    public void create(AppUser usr, List<Message> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        messageDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Message record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Message modify(AppUser usr, Message message) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Message existing = messageDAO.find(new Message.MessagePK(message.getMessageId()));
        if (existing == null) {
            throw new IllegalArgumentException("No message found for identifier: " + message.getMessageId());
        }
        // Update attributes of existing record
        existing.setConversationId(message.getConversationId());
        existing.setSenderId(message.getSenderId());
        existing.setMessageText(message.getMessageText());
        existing.setSentAt(message.getSentAt());

        messageDAO.update(message);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Message record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Message> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Message> rows = messageDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded message record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Message view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Message message = messageDAO.find(new Message.MessagePK(id));
        if (message == null) {
            throw new IllegalArgumentException("No Message found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched message details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return message;
    }

    public Message remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Message message = messageDAO.find(new Message.MessagePK(id));

        if (message == null) {
            throw new IllegalArgumentException("No message found for id: " + id);
        }
        messageDAO.delete(message);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Message. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return message;
    }
}
