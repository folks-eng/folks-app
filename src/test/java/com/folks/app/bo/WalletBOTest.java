package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Wallet;
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
 * JUnit test cases for {@link WalletBO}.
 *
 * <p>{@code wallet_id} is a plain (non-generated) String primary key, so the test assigns one
 * explicitly. {@code fks_wallets} has no foreign key constraint on {@code user_id}. Note that
 * {@link WalletBO#create} does not auto-populate {@code createdAt}, so the test data sets it
 * explicitly.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletBOTest {

    private static WalletBO walletBO;
    private static AppUser usr;

    private static String walletId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        usr = new AppUserImpl(new UserPrincipal(map));
        walletBO = new WalletBO();
        walletId = UUID.randomUUID().toString();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setUserId(401);
        wallet.setBalance(500.00);
        wallet.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        walletBO.create(usr, wallet);

        assertEquals(walletId, wallet.getWalletId());
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Wallet w1 = new Wallet();
        w1.setWalletId(UUID.randomUUID().toString());
        w1.setUserId(402);
        w1.setBalance(100.00);
        w1.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        Wallet w2 = new Wallet();
        w2.setWalletId(UUID.randomUUID().toString());
        w2.setUserId(403);
        w2.setBalance(200.00);
        w2.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        walletBO.create(usr, Arrays.asList(w1, w2));

        assertNotNull(walletBO.view(usr, w1.getWalletId()));
        assertNotNull(walletBO.view(usr, w2.getWalletId()));
    }

    @Test
    @Order(3)
    public void testView() {
        Wallet wallet = walletBO.view(usr, walletId);

        assertEquals(walletId, wallet.getWalletId());
        assertEquals(401, wallet.getUserId());
        assertEquals(500.00, wallet.getBalance());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> walletBO.view(usr, UUID.randomUUID().toString()));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("userId", Arrays.asList("401"));

        List<Wallet> rows = walletBO.viewAll(usr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> walletId.equals(r.getWalletId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        Wallet update = new Wallet();
        update.setWalletId(walletId);
        update.setUserId(401);
        update.setBalance(750.50);

        Wallet modified = walletBO.modify(usr, update);
        assertEquals(750.50, modified.getBalance());

        Wallet reloaded = walletBO.view(usr, walletId);
        assertEquals(750.50, reloaded.getBalance());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        Wallet update = new Wallet();
        update.setWalletId(UUID.randomUUID().toString());
        update.setUserId(1);
        update.setBalance(0.0);

        assertThrows(IllegalArgumentException.class, () -> walletBO.modify(usr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        Wallet removed = walletBO.remove(usr, walletId);
        assertEquals(walletId, removed.getWalletId());

        assertThrows(IllegalArgumentException.class, () -> walletBO.view(usr, walletId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> walletBO.remove(usr, UUID.randomUUID().toString()));
    }
}
