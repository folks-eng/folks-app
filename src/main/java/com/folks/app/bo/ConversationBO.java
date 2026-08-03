package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.ConversationDAO;
import com.folks.app.model.Conversation;
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
public class ConversationBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversationBO.class);
    
    private final ConversationDAO conversationDAO;

    public ConversationBO() {
        this.conversationDAO = DAOProxy.get(ConversationDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. ConversationDAO: {}", getClass().getSimpleName(), conversationDAO);
        }
    }

    public Conversation create(AppUser usr, Conversation conversation) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        if (conversation.getCreatedAt() == null) {
            conversation.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }

        conversationDAO.insert(conversation);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Conversation created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return conversation;
    }

    public void create(AppUser usr, List<Conversation> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        for (Conversation conversation : records) {
            if (conversation.getCreatedAt() == null) {
                conversation.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        conversationDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Conversation record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Conversation modify(AppUser usr, Conversation conversation) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Conversation existing = conversationDAO.find(new Conversation.ConversationPK(conversation.getConversationId()));
        if (existing == null) {
            throw new IllegalArgumentException("No conversation found for identifier: " + conversation.getConversationId());
        }
        // Update attributes of existing record
        existing.setBookingId(conversation.getBookingId());

        conversationDAO.update(conversation);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Conversation record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Conversation> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Conversation> rows = conversationDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded conversation record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Conversation view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Conversation conversation = conversationDAO.find(new Conversation.ConversationPK(id));
        if (conversation == null) {
            throw new IllegalArgumentException("No Conversation found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched conversation details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return conversation;
    }

    public Conversation remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Conversation conversation = conversationDAO.find(new Conversation.ConversationPK(id));

        if (conversation == null) {
            throw new IllegalArgumentException("No conversation found for id: " + id);
        }
        conversationDAO.delete(conversation);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Conversation. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return conversation;
    }
}
