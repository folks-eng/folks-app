package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Conversation;
import com.folks.app.util.QueryParams;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
 * JUnit test cases for {@link ConversationBO}.
 *
 * <p>{@code fks_conversations} has no foreign key constraint on {@code booking_id}, so a random
 * identifier can be used in place of a real {@code Booking} row.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConversationBOTest {

    private static ConversationBO conversationBO;
    private static AppUser usr;

    private static String bookingId;
    private static Integer conversationId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        usr = new AppUserImpl(new UserPrincipal(map));
        conversationBO = new ConversationBO();
        bookingId = UUID.randomUUID().toString();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Conversation conversation = new Conversation();
        conversation.setBookingId(bookingId);

        conversationBO.create(usr, conversation);

        conversationId = conversation.getConversationId();
        assertNotNull(conversationId);
        assertNotNull(conversation.getCreatedAt());
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Conversation c1 = new Conversation();
        c1.setBookingId(UUID.randomUUID().toString());

        Conversation c2 = new Conversation();
        c2.setBookingId(UUID.randomUUID().toString());

        conversationBO.create(usr, Arrays.asList(c1, c2));

        assertNotNull(c1.getConversationId());
        assertNotNull(c2.getConversationId());
    }

    @Test
    @Order(3)
    public void testView() {
        Conversation conversation = conversationBO.view(usr, conversationId);

        assertEquals(conversationId, conversation.getConversationId());
        assertEquals(bookingId, conversation.getBookingId());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> conversationBO.view(usr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("bookingId", Arrays.asList(bookingId));

        List<Conversation> rows = conversationBO.viewAll(usr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> conversationId.equals(r.getConversationId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        String newBookingId = UUID.randomUUID().toString();

        Conversation update = new Conversation();
        update.setConversationId(conversationId);
        update.setBookingId(newBookingId);

        Conversation modified = conversationBO.modify(usr, update);
        assertEquals(newBookingId, modified.getBookingId());

        Conversation reloaded = conversationBO.view(usr, conversationId);
        assertEquals(newBookingId, reloaded.getBookingId());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        Conversation update = new Conversation();
        update.setConversationId(-999);
        update.setBookingId(bookingId);

        assertThrows(IllegalArgumentException.class, () -> conversationBO.modify(usr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        Conversation removed = conversationBO.remove(usr, conversationId);
        assertEquals(conversationId, removed.getConversationId());

        assertThrows(IllegalArgumentException.class, () -> conversationBO.view(usr, conversationId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> conversationBO.remove(usr, -999));
    }
}
