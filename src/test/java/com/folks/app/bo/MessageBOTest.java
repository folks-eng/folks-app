package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Conversation;
import com.folks.app.model.Message;
import com.folks.app.util.QueryParams;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.javalabs.decl.util.DateUtil;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit test cases for {@link MessageBO}.
 *
 * <p>{@code fks_messages} has a foreign key on {@code conversation_id}, so a real
 * {@code Conversation} row is created via {@link ConversationBO} in {@code setup()}. Note that
 * {@link MessageBO#create} does not auto-populate {@code createdAt}/{@code sentAt}, so the test
 * data sets them explicitly.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageBOTest {

    private static MessageBO messageBO;
    private static AppUser usr;

    private static Integer conversationId;
    private static Integer messageId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        usr = new AppUserImpl(new UserPrincipal(map));
        messageBO = new MessageBO();

        ConversationBO conversationBO = new ConversationBO();
        Conversation conversation = new Conversation();
        conversation.setBookingId(UUID.randomUUID().toString());
        conversationBO.create(usr, conversation);
        conversationId = conversation.getConversationId();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(201);
        message.setMessageText("Hi, I'm on my way!");
        message.setSentAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        message.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        messageBO.create(usr, message);

        messageId = message.getMessageId();
        assertNotNull(messageId);
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Timestamp now = new Timestamp(DateUtil.currentUTCDate().getTime());

        Message m1 = new Message();
        m1.setConversationId(conversationId);
        m1.setSenderId(202);
        m1.setMessageText("Sure, see you soon");
        m1.setSentAt(now);
        m1.setCreatedAt(now);

        Message m2 = new Message();
        m2.setConversationId(conversationId);
        m2.setSenderId(201);
        m2.setMessageText("Running 5 mins late");
        m2.setSentAt(now);
        m2.setCreatedAt(now);

        messageBO.create(usr, Arrays.asList(m1, m2));

        assertNotNull(m1.getMessageId());
        assertNotNull(m2.getMessageId());
    }

    @Test
    @Order(3)
    public void testView() {
        Message message = messageBO.view(usr, messageId);

        assertEquals(messageId, message.getMessageId());
        assertEquals(conversationId, message.getConversationId());
        assertEquals(201, message.getSenderId());
        assertEquals("Hi, I'm on my way!", message.getMessageText());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> messageBO.view(usr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("conversationId", Arrays.asList(String.valueOf(conversationId)));

        List<Message> rows = messageBO.viewAll(usr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> messageId.equals(r.getMessageId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        Message update = new Message();
        update.setMessageId(messageId);
        update.setConversationId(conversationId);
        update.setSenderId(201);
        update.setMessageText("Edited: I'm on my way!");
        update.setSentAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        Message modified = messageBO.modify(usr, update);
        assertEquals("Edited: I'm on my way!", modified.getMessageText());

        Message reloaded = messageBO.view(usr, messageId);
        assertEquals("Edited: I'm on my way!", reloaded.getMessageText());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        Message update = new Message();
        update.setMessageId(-999);
        update.setConversationId(conversationId);
        update.setSenderId(1);
        update.setSentAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        assertThrows(IllegalArgumentException.class, () -> messageBO.modify(usr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        Message removed = messageBO.remove(usr, messageId);
        assertEquals(messageId, removed.getMessageId());

        assertThrows(IllegalArgumentException.class, () -> messageBO.view(usr, messageId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> messageBO.remove(usr, -999));
    }
}
