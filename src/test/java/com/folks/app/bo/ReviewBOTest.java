package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Review;
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
 * JUnit test cases for {@link ReviewBO}.
 *
 * <p>{@code fks_reviews} has no foreign key constraints, so a random booking id and plain
 * integers can be used in place of real Booking/User/Professional rows.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewBOTest {

    private static ReviewBO reviewBO;
    private static AppUser usr;

    private static String bookingId;
    private static Integer reviewId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        usr = new AppUserImpl(new UserPrincipal(map));
        reviewBO = new ReviewBO();
        bookingId = UUID.randomUUID().toString();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Review review = new Review();
        review.setBookingId(bookingId);
        review.setCustomerId(201);
        review.setProfessionalId(301);
        review.setRating((short) 5);
        review.setComment("Excellent service!");

        reviewBO.create(usr, review);

        reviewId = review.getReviewId();
        assertNotNull(reviewId);
        assertNotNull(review.getCreatedAt());
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Review r1 = new Review();
        r1.setBookingId(bookingId);
        r1.setCustomerId(202);
        r1.setProfessionalId(302);
        r1.setRating((short) 4);
        r1.setComment("Good work");

        Review r2 = new Review();
        r2.setBookingId(bookingId);
        r2.setCustomerId(203);
        r2.setProfessionalId(303);
        r2.setRating((short) 3);
        r2.setComment("Average");

        reviewBO.create(usr, Arrays.asList(r1, r2));

        assertNotNull(r1.getReviewId());
        assertNotNull(r2.getReviewId());
    }

    @Test
    @Order(3)
    public void testView() {
        Review review = reviewBO.view(usr, reviewId);

        assertEquals(reviewId, review.getReviewId());
        assertEquals(bookingId, review.getBookingId());
        assertEquals((short) 5, review.getRating());
        assertEquals("Excellent service!", review.getComment());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> reviewBO.view(usr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("bookingId", Arrays.asList(bookingId));

        List<Review> rows = reviewBO.viewAll(usr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> reviewId.equals(r.getReviewId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        Review update = new Review();
        update.setReviewId(reviewId);
        update.setBookingId(bookingId);
        update.setCustomerId(201);
        update.setProfessionalId(301);
        update.setRating((short) 2);
        update.setComment("Updated after complaint");

        Review modified = reviewBO.modify(usr, update);
        assertEquals((short) 2, modified.getRating());

        Review reloaded = reviewBO.view(usr, reviewId);
        assertEquals((short) 2, reloaded.getRating());
        assertEquals("Updated after complaint", reloaded.getComment());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        Review update = new Review();
        update.setReviewId(-999);
        update.setBookingId(bookingId);
        update.setCustomerId(1);
        update.setProfessionalId(1);

        assertThrows(IllegalArgumentException.class, () -> reviewBO.modify(usr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        Review removed = reviewBO.remove(usr, reviewId);
        assertEquals(reviewId, removed.getReviewId());

        assertThrows(IllegalArgumentException.class, () -> reviewBO.view(usr, reviewId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> reviewBO.remove(usr, -999));
    }
}
