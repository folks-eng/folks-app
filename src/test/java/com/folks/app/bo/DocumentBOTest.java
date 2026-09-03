package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Document;
import com.folks.app.model.User;
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
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit test cases for {@link DocumentBO}.
 *
 * <p>{@code fks_documents} has no foreign key constraint on {@code user_id}, but
 * {@link DocumentBO#viewAll} internally resolves the calling user via {@code fetchUser}, so a
 * real {@code User} row is created via {@link UserBO} in {@code setup()}. Note that
 * {@link DocumentBO#create} does not auto-populate {@code createdAt}, so the test data sets it
 * explicitly.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentBOTest {

    private static DocumentBO documentBO;
    private static AppUser customerUsr;

    private static Integer userId;
    private static String applicationId;
    private static Integer documentId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> adminMap = new HashMap<>();
        adminMap.put("sub", UUID.randomUUID().toString());
        adminMap.put("name", "Admin User");
        adminMap.put("priv", "admin");
        adminMap.put("scope", "user:create|user:query");
        adminMap.put("jti", UUID.randomUUID().toString());
        AppUser adminUser = new AppUserImpl(new UserPrincipal(adminMap));

        User user = new User();
        user.setFullName("Priya Sharma");
        user.setPhone1("9812345401");
        user.setEmail("priya.sharma.docs@folks.test");

        try {
            new UserBO().create(adminUser, user);
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
        userId = user.getUserId();

        Map<String, Object> map = new HashMap<>();
        map.put("sub", user.getExternalId());
        map.put("jti", UUID.randomUUID().toString());
        customerUsr = new AppUserImpl(new UserPrincipal(map));

        documentBO = new DocumentBO();
        applicationId = UUID.randomUUID().toString();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Document document = new Document();
        document.setUserId(userId);
        document.setApplicationId(applicationId);
        document.setDocumentType("AADHAAR");
        document.setDocumentNumber("1234-5678-9012");
        document.setNameOnDocument("Priya Sharma");
        document.setVerificationStatus(Document.Verificationstatus.PENDING);
        document.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        documentBO.create(customerUsr, document);

        documentId = document.getDocumentId();
        assertNotNull(documentId);
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Timestamp now = new Timestamp(DateUtil.currentUTCDate().getTime());

        Document d1 = new Document();
        d1.setUserId(userId);
        d1.setApplicationId(applicationId);
        d1.setDocumentType("PAN");
        d1.setDocumentNumber("ABCDE1234F");
        d1.setNameOnDocument("Priya Sharma");
        d1.setVerificationStatus(Document.Verificationstatus.PENDING);
        d1.setCreatedAt(now);

        Document d2 = new Document();
        d2.setUserId(userId);
        d2.setApplicationId(applicationId);
        d2.setDocumentType("DRIVING_LICENSE");
        d2.setDocumentNumber("DL-998877");
        d2.setNameOnDocument("Priya Sharma");
        d2.setVerificationStatus(Document.Verificationstatus.PENDING);
        d2.setCreatedAt(now);

        documentBO.create(customerUsr, Arrays.asList(d1, d2));

        assertNotNull(d1.getDocumentId());
        assertNotNull(d2.getDocumentId());
    }

    @Test
    @Order(3)
    public void testView() {
        Document document = documentBO.view(customerUsr, documentId);

        assertEquals(documentId, document.getDocumentId());
        assertEquals(userId, document.getUserId());
        assertEquals("AADHAAR", document.getDocumentType());
        assertEquals(Document.Verificationstatus.PENDING, document.getVerificationStatus());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> documentBO.view(customerUsr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();

        List<Document> rows = documentBO.viewAll(customerUsr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> documentId.equals(r.getDocumentId())));
        assertTrue(rows.stream().allMatch(r -> userId.equals(r.getUserId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        Document update = new Document();
        update.setDocumentId(documentId);
        update.setUserId(userId);
        update.setDocumentType("AADHAAR");
        update.setDocumentUrl("https://storage.folks.test/docs/aadhaar.pdf");
        update.setVerificationStatus(Document.Verificationstatus.APPROVED);
        update.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        Document modified = documentBO.modify(customerUsr, update);
        assertEquals(Document.Verificationstatus.APPROVED, modified.getVerificationStatus());

        Document reloaded = documentBO.view(customerUsr, documentId);
        assertEquals(Document.Verificationstatus.APPROVED, reloaded.getVerificationStatus());
        assertEquals("https://storage.folks.test/docs/aadhaar.pdf", reloaded.getDocumentUrl());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        Document update = new Document();
        update.setDocumentId(-999);
        update.setUserId(userId);

        assertThrows(IllegalArgumentException.class, () -> documentBO.modify(customerUsr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        Document removed = documentBO.remove(customerUsr, documentId);
        assertEquals(documentId, removed.getDocumentId());

        assertThrows(IllegalArgumentException.class, () -> documentBO.view(customerUsr, documentId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> documentBO.remove(customerUsr, -999));
    }
}
