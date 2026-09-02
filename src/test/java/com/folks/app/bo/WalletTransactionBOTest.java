package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.WalletTransaction;
import com.folks.app.util.QueryParams;
import java.math.BigDecimal;
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
 * JUnit test cases for {@link WalletTransactionBO}.
 *
 * <p>{@code txn_id} is a plain (non-generated) String primary key, so the test assigns one
 * explicitly. {@code fks_wallet_transactions} has no foreign key constraint on
 * {@code wallet_id}, so a random identifier can stand in for a real {@code Wallet} row.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletTransactionBOTest {

    private static WalletTransactionBO walletTransactionBO;
    private static AppUser usr;

    private static String walletId;
    private static String txnId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        usr = new AppUserImpl(new UserPrincipal(map));
        walletTransactionBO = new WalletTransactionBO();
        walletId = UUID.randomUUID().toString();
        txnId = UUID.randomUUID().toString();
    }

    @Test
    @Order(1)
    public void testCreate() {
        WalletTransaction txn = new WalletTransaction();
        txn.setTxnId(txnId);
        txn.setWalletId(walletId);
        txn.setAmount(new BigDecimal("250.00"));
        txn.setType(WalletTransaction.Type.CREDIT);

        walletTransactionBO.create(usr, txn);

        assertEquals(txnId, txn.getTxnId());
        assertNotNull(txn.getCreatedAt());
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        WalletTransaction t1 = new WalletTransaction();
        t1.setTxnId(UUID.randomUUID().toString());
        t1.setWalletId(walletId);
        t1.setAmount(new BigDecimal("50.00"));
        t1.setType(WalletTransaction.Type.DEBIT);

        WalletTransaction t2 = new WalletTransaction();
        t2.setTxnId(UUID.randomUUID().toString());
        t2.setWalletId(walletId);
        t2.setAmount(new BigDecimal("75.00"));
        t2.setType(WalletTransaction.Type.CREDIT);

        walletTransactionBO.create(usr, Arrays.asList(t1, t2));

        assertNotNull(walletTransactionBO.view(usr, t1.getTxnId()));
        assertNotNull(walletTransactionBO.view(usr, t2.getTxnId()));
    }

    @Test
    @Order(3)
    public void testView() {
        WalletTransaction txn = walletTransactionBO.view(usr, txnId);

        assertEquals(txnId, txn.getTxnId());
        assertEquals(walletId, txn.getWalletId());
        assertEquals(0, new BigDecimal("250.00").compareTo(txn.getAmount()));
        assertEquals(WalletTransaction.Type.CREDIT, txn.getType());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> walletTransactionBO.view(usr, UUID.randomUUID().toString()));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("walletId", Arrays.asList(walletId));

        List<WalletTransaction> rows = walletTransactionBO.viewAll(usr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> txnId.equals(r.getTxnId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        WalletTransaction update = new WalletTransaction();
        update.setTxnId(txnId);
        update.setWalletId(walletId);
        update.setAmount(new BigDecimal("300.00"));
        update.setType(WalletTransaction.Type.DEBIT);

        WalletTransaction modified = walletTransactionBO.modify(usr, update);
        assertEquals(WalletTransaction.Type.DEBIT, modified.getType());

        WalletTransaction reloaded = walletTransactionBO.view(usr, txnId);
        assertEquals(0, new BigDecimal("300.00").compareTo(reloaded.getAmount()));
        assertEquals(WalletTransaction.Type.DEBIT, reloaded.getType());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        WalletTransaction update = new WalletTransaction();
        update.setTxnId(UUID.randomUUID().toString());
        update.setWalletId(walletId);
        update.setAmount(BigDecimal.ONE);

        assertThrows(IllegalArgumentException.class, () -> walletTransactionBO.modify(usr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        WalletTransaction removed = walletTransactionBO.remove(usr, txnId);
        assertEquals(txnId, removed.getTxnId());

        assertThrows(IllegalArgumentException.class, () -> walletTransactionBO.view(usr, txnId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> walletTransactionBO.remove(usr, UUID.randomUUID().toString()));
    }
}
