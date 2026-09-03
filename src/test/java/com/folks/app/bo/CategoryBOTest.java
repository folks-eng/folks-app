package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Category;
import com.folks.app.model.Service;
import com.folks.app.util.QueryParams;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.javalabs.decl.util.DateUtil;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author schan280
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryBOTest {

    private static CategoryBO categoryBO;
    private static ServiceBO serviceBO;
    private Category parent;

    @BeforeAll
    public static void setup() {
        categoryBO = new CategoryBO();
        serviceBO = new ServiceBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("sub", UUID.randomUUID().toString());
            map.put("jti", UUID.randomUUID().toString());

            AppUser usr = new AppUserImpl(new UserPrincipal(map));

            Category category = createSalonCategories();
            parent = category;
            
            categoryBO.create(usr, category);
            for (Category sub : category.getSubCategories()) {
                sub.setParentId(category.getCategoryId());
            }
            categoryBO.create(usr, category.getSubCategories());
            
            List<Service> services = createSalonServices(category.getSubCategories().get(0).getCategoryId()
                    , category.getSubCategories().get(1).getCategoryId()
                    , category.getSubCategories().get(2).getCategoryId());
            serviceBO.create(usr, services);
            
            assertEquals(3, category.getSubCategories().size());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }
    }

    @Test
    @Order(2)
    public void testView() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        AppUser usr = new AppUserImpl(new UserPrincipal(map));

        Category category = categoryBO.view(usr, 1);
        assertNotNull(category);
    }

    @Test
    @Order(3)
    public void testViewAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        AppUser usr = new AppUserImpl(new UserPrincipal(map));

        List<Category> categories = categoryBO.viewAll(usr, new QueryParams(new HashMap<>()));
        assertTrue(!categories.isEmpty());
    }

    @Test
    @Order(4)
    public void testViewAllHierarchy() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("sub", UUID.randomUUID().toString());
            map.put("jti", UUID.randomUUID().toString());

            AppUser usr = new AppUserImpl(new UserPrincipal(map));

            List<String> ids = List.of(String.valueOf(parent.getCategoryId()));
            Map<String, List<String>> params = new HashMap<>();
            params.put("id", ids);

            List<Category> categories = categoryBO.viewAllHierarchy(usr, new QueryParams(params));
            assertTrue(!categories.isEmpty());
            assertEquals(1, categories.size());
            assertEquals(3, categories.get(0).getSubCategories().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }
    }

    private Category createSalonCategories() {
        Timestamp now = new Timestamp(DateUtil.currentUTCDate().getTime());

        // Parent category
        Category salonMakeup = new Category();
        salonMakeup.setName("Salon & Makeup");
        salonMakeup.setIcon("scissors");
        salonMakeup.setTagLine(
                "Professional grooming and beauty services at home."
        );
        salonMakeup.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        salonMakeup.setParentId(null);
        salonMakeup.setCreatedAt(now);
        salonMakeup.setUpdatedAt(null);

        // Sub-category 1
        Category womensSalon = new Category();
        womensSalon.setName("Women's Salon");
        womensSalon.setIcon(null);
        womensSalon.setTagLine(null);
        womensSalon.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        womensSalon.setParentId(1);
        womensSalon.setCreatedAt(now);
        womensSalon.setUpdatedAt(null);

        // Sub-category 2
        Category mensSalon = new Category();
        mensSalon.setName("Men's Salon");
        mensSalon.setIcon(null);
        mensSalon.setTagLine(null);
        mensSalon.setImage(
                "https://images.unsplash.com/photo-1647140655214-e4a2d914971f?w=800&q=80&auto=format&fit=crop"
        );
        mensSalon.setParentId(1);
        mensSalon.setCreatedAt(now);
        mensSalon.setUpdatedAt(null);

        // Sub-category 3
        Category bridalPartyMakeup = new Category();
        bridalPartyMakeup.setName("Bridal & Party Makeup");
        bridalPartyMakeup.setIcon(null);
        bridalPartyMakeup.setTagLine(null);
        bridalPartyMakeup.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        bridalPartyMakeup.setParentId(1);
        bridalPartyMakeup.setCreatedAt(now);
        bridalPartyMakeup.setUpdatedAt(null);

        // Build hierarchy
        salonMakeup.setSubCategories(
                Arrays.asList(
                        womensSalon,
                        mensSalon,
                        bridalPartyMakeup
                )
        );
        return salonMakeup;
    }

    private List<Service> createSalonServices(Integer sub1, Integer sub2, Integer sub3) {
        Timestamp now = new Timestamp(DateUtil.currentUTCDate().getTime());

        List<Service> services = new ArrayList<>();

        // 1
        Service s1 = new Service();
        s1.setCategoryId(sub1);
        s1.setName("Fruit Facial Glow");
        s1.setDescription(
                "A refreshing fruit-based facial that brightens and hydrates tired skin."
        );
        s1.setBasePrice(799.00);
        s1.setDurationMinutes((short) 60);
        s1.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s1.setRatingAvg(4.80);
        s1.setReviews(2140);
        s1.setCurrency("INR");
        s1.setCreatedAt(now);
        s1.setUpdatedAt(null);
        services.add(s1);

        // 2
        Service s2 = new Service();
        s2.setCategoryId(sub1);
        s2.setName("Hair Spa & Care");
        s2.setDescription(
                "A deep-conditioning hair spa that repairs damage and restores natural shine."
        );
        s2.setBasePrice(899.00);
        s2.setDurationMinutes((short) 75);
        s2.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s2.setRatingAvg(4.70);
        s2.setReviews(1560);
        s2.setCurrency("INR");
        s2.setCreatedAt(now);
        s2.setUpdatedAt(null);
        services.add(s2);

        // 3
        Service s3 = new Service();
        s3.setCategoryId(sub1);
        s3.setName("Full Arms & Legs Waxing");
        s3.setDescription(
                "Smooth, salon-grade waxing for arms and legs using a gentle wax."
        );
        s3.setBasePrice(599.00);
        s3.setDurationMinutes((short) 45);
        s3.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s3.setRatingAvg(4.60);
        s3.setReviews(3200);
        s3.setCurrency("INR");
        s3.setCreatedAt(now);
        s3.setUpdatedAt(null);
        services.add(s3);

        // 4
        Service s4 = new Service();
        s4.setCategoryId(sub1);
        s4.setName("Threading (Eyebrows + Upper Lip)");
        s4.setDescription(
                "Quick, precise threading for perfectly shaped brows and upper lip."
        );
        s4.setBasePrice(149.00);
        s4.setDurationMinutes((short) 20);
        s4.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s4.setRatingAvg(4.50);
        s4.setReviews(4100);
        s4.setCurrency("INR");
        s4.setCreatedAt(now);
        s4.setUpdatedAt(null);
        services.add(s4);

        // 5
        Service s5 = new Service();
        s5.setCategoryId(sub1);
        s5.setName("Manicure & Pedicure");
        s5.setDescription(
                "A classic mani-pedi that leaves hands and feet soft, neat and polished."
        );
        s5.setBasePrice(649.00);
        s5.setDurationMinutes((short) 60);
        s5.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s5.setRatingAvg(4.60);
        s5.setReviews(2450);
        s5.setCurrency("INR");
        s5.setCreatedAt(now);
        s5.setUpdatedAt(null);
        services.add(s5);

        // 6
        Service s6 = new Service();
        s6.setCategoryId(sub1);
        s6.setName("Global Hair Colour");
        s6.setDescription(
                "Ammonia-friendly global colour application for full, even coverage."
        );
        s6.setBasePrice(1299.00);
        s6.setDurationMinutes((short) 90);
        s6.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s6.setRatingAvg(4.50);
        s6.setReviews(870);
        s6.setCurrency("INR");
        s6.setCreatedAt(now);
        s6.setUpdatedAt(null);
        services.add(s6);

        // 7
        Service s7 = new Service();
        s7.setCategoryId(sub2);
        s7.setName("Haircut & Styling");
        s7.setDescription(
                "A precision haircut and styling from an experienced men's stylist."
        );
        s7.setBasePrice(299.00);
        s7.setDurationMinutes((short) 30);
        s7.setImage(
                "https://images.unsplash.com/photo-1647140655214-e4a2d914971f?w=800&q=80&auto=format&fit=crop"
        );
        s7.setRatingAvg(4.70);
        s7.setReviews(5200);
        s7.setCurrency("INR");
        s7.setCreatedAt(now);
        s7.setUpdatedAt(null);
        services.add(s7);

        // 8
        Service s8 = new Service();
        s8.setCategoryId(sub2);
        s8.setName("Beard Shape-up & Trim");
        s8.setDescription(
                "Sharp beard shaping and trim to keep your look fresh."
        );
        s8.setBasePrice(199.00);
        s8.setDurationMinutes((short) 20);
        s8.setImage(
                "https://images.unsplash.com/photo-1647140655214-e4a2d914971f?w=800&q=80&auto=format&fit=crop"
        );
        s8.setRatingAvg(4.60);
        s8.setReviews(4700);
        s8.setCurrency("INR");
        s8.setCreatedAt(now);
        s8.setUpdatedAt(null);
        services.add(s8);

        // 9
        Service s9 = new Service();
        s9.setCategoryId(sub2);
        s9.setName("Head & Shoulder Massage");
        s9.setDescription(
                "A relaxing head and shoulder massage to relieve stress and tension."
        );
        s9.setBasePrice(399.00);
        s9.setDurationMinutes((short) 30);
        s9.setImage(
                "https://images.unsplash.com/photo-1647140655214-e4a2d914971f?w=800&q=80&auto=format&fit=crop"
        );
        s9.setRatingAvg(4.80);
        s9.setReviews(2300);
        s9.setCurrency("INR");
        s9.setCreatedAt(now);
        s9.setUpdatedAt(null);
        services.add(s9);

        // 10
        Service s10 = new Service();
        s10.setCategoryId(sub2);
        s10.setName("De-Tan Facial for Men");
        s10.setDescription(
                "A de-tan facial that clears dullness and refreshes sun-exposed skin."
        );
        s10.setBasePrice(549.00);
        s10.setDurationMinutes((short) 45);
        s10.setImage(
                "https://images.unsplash.com/photo-1647140655214-e4a2d914971f?w=800&q=80&auto=format&fit=crop"
        );
        s10.setRatingAvg(4.50);
        s10.setReviews(1340);
        s10.setCurrency("INR");
        s10.setCreatedAt(now);
        s10.setUpdatedAt(null);
        services.add(s10);

        // 11
        Service s11 = new Service();
        s11.setCategoryId(sub2);
        s11.setName("Beard & Hair Colour");
        s11.setDescription(
                "Natural-looking colour touch-up for greying hair and beard."
        );
        s11.setBasePrice(349.00);
        s11.setDurationMinutes((short) 30);
        s11.setImage(
                "https://images.unsplash.com/photo-1647140655214-e4a2d914971f?w=800&q=80&auto=format&fit=crop"
        );
        s11.setRatingAvg(4.40);
        s11.setReviews(980);
        s11.setCurrency("INR");
        s11.setCreatedAt(now);
        s11.setUpdatedAt(null);
        services.add(s11);

        // 12
        Service s12 = new Service();
        s12.setCategoryId(sub3);
        s12.setName("Party Makeup");
        s12.setDescription(
                "Camera-ready party makeup tailored to your outfit and occasion."
        );
        s12.setBasePrice(1499.00);
        s12.setDurationMinutes((short) 90);
        s12.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s12.setRatingAvg(4.90);
        s12.setReviews(860);
        s12.setCurrency("INR");
        s12.setCreatedAt(now);
        s12.setUpdatedAt(null);
        services.add(s12);

        // 13
        Service s13 = new Service();
        s13.setCategoryId(sub3);
        s13.setName("Bridal Makeup (HD)");
        s13.setDescription(
                "Long-lasting HD bridal makeup with draping and hairstyling included."
        );
        s13.setBasePrice(6999.00);
        s13.setDurationMinutes((short) 150);
        s13.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s13.setRatingAvg(4.90);
        s13.setReviews(410);
        s13.setCurrency("INR");
        s13.setCreatedAt(now);
        s13.setUpdatedAt(null);
        services.add(s13);

        // 14
        Service s14 = new Service();
        s14.setCategoryId(sub3);
        s14.setName("Nail Art & Manicure");
        s14.setDescription(
                "A gel manicure with custom nail art finished by a trained nail artist."
        );
        s14.setBasePrice(499.00);
        s14.setDurationMinutes((short) 40);
        s14.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s14.setRatingAvg(4.60);
        s14.setReviews(1980);
        s14.setCurrency("INR");
        s14.setCreatedAt(now);
        s14.setUpdatedAt(null);
        services.add(s14);

        // 15
        Service s15 = new Service();
        s15.setCategoryId(sub3);
        s15.setName("Engagement Makeup");
        s15.setDescription(
                "Soft-glam engagement makeup designed to photograph beautifully."
        );
        s15.setBasePrice(2999.00);
        s15.setDurationMinutes((short) 100);
        s15.setImage(
                "https://images.unsplash.com/photo-1634449571010-02389ed0f9b0?w=800&q=80&auto=format&fit=crop"
        );
        s15.setRatingAvg(4.80);
        s15.setReviews(320);
        s15.setCurrency("INR");
        s15.setCreatedAt(now);
        s15.setUpdatedAt(null);
        services.add(s15);

        return services;
    }
}
