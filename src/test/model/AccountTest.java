package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    Account testAccount;
    Asset a1;
    Asset a2;
    Asset a3;
    Asset a4;
    LocalDate today;
    double expGain1;
    double expGain2;
    double expGain3;

    @BeforeEach
    void runBefore() {
        testAccount = new Account("Cici");
        today = LocalDate.now();

        a1 = new Asset("asset1", 180, 5, 1000);
        a2 = new Asset("asset2", 365, 8, 2000);
        a3 = new Asset("asset3", 90, 3, 1500);
        a4 = new Asset("asset4", 45, 2, 4000);

        a1.setInvestDate(1, 1, 2023); // mature/expired
        a2.setInvestDate(1, 1, 2023); // non-mature
        LocalDate targetInvestDate = today.minusDays(90);
        a3.setInvestDate(targetInvestDate.getMonthValue(),
                targetInvestDate.getDayOfMonth(),
                targetInvestDate.getYear()); // get mature today

        expGain1 = Math.round(1000 * 0.05 / 365 * 180 * 100) / 100.0;
        expGain2 = Math.round(2000 * 0.08 / 365 * a2.calculateDaysHeld() * 100) / 100.0;
        expGain3 = Math.round(1500 * 0.03 / 365 * 90 * 100) / 100.0;
    }

    @Test
    void testConstructor() {
        assertEquals(0, testAccount.getAccount().size());
        assertEquals(0, testAccount.getNamesAssetsRemoved().size());
    }

    @Test
    void testAddAsset() {
        testAccount.addAsset(a1);
        assertEquals(1, testAccount.getAccount().size());
        assertTrue(testAccount.getAccount().contains(a1));
    }

    @Test
    void testRemoveAsset() {
        testAccount.addAsset(a1);
        testAccount.addAsset(a2);
        testAccount.removeAsset(a1);
        assertEquals(1, testAccount.getAccount().size());
        assertTrue(testAccount.getAccount().contains(a2));
        assertEquals(1, testAccount.getNamesAssetsRemoved().size());
        assertEquals("asset1", testAccount.getNamesAssetsRemoved().get(0));
    }

    @Test
    void testNumAssets() {
        testAccount.addAsset(a1);
        testAccount.addAsset(a2);
        testAccount.addAsset(a3);
        assertEquals(3, testAccount.numAssets());
    }

    @Test
    void testNumActiveAssets() {
        a1.deActivate();
        testAccount.addAsset(a1);
        testAccount.addAsset(a2);
        assertEquals(1, testAccount.numActiveAssets());
    }

    @Test
    void testRefreshEmptyAccount() {
        testAccount.refresh();
        assertEquals(0, testAccount.getAccount().size());
    }

    @Test
    void testRefreshNonEmptyAccount() {
        testAccount.addAsset(a1);
        testAccount.addAsset(a2);
        testAccount.addAsset(a3);
        testAccount.removeAsset(a1);
        testAccount.refresh();

        assertFalse(a1.getActivatedStatus());
        assertTrue(a2.getActivatedStatus());
        assertTrue(a3.getActivatedStatus());

        assertEquals(expGain1, a1.getTotalGain());
        assertEquals(expGain2, a2.getTotalGain());
        assertEquals(expGain3, a3.getTotalGain());

        int expDaysHeld = (int) ChronoUnit.DAYS.between(LocalDate.of(2023, 1, 1), today);
        assertEquals(expDaysHeld, a1.getDaysHeld());
        assertEquals(expDaysHeld, a2.getDaysHeld());
        assertEquals(90, a3.getDaysHeld());
    }

    @Test
    void testTotalGainEmptyAssets() {
        assertEquals(0, testAccount.getTotalGain());
    }

    @Test
    void testTotalGainEmptyRemovedAssets() {
        testAccount.addAsset(a1);
        testAccount.addAsset(a2);
        testAccount.addAsset(a3);
        testAccount.refresh();

        double expWealth = expGain1 + expGain2 + expGain3;
        assertEquals(expWealth, testAccount.getTotalGain());
    }

    @Test
    void testTotalGainRemoveAssets() {
        testAccount.addAsset(a1);
        testAccount.addAsset(a2);
        testAccount.addAsset(a3);
        testAccount.removeAsset(a1);
        testAccount.refresh();

        double expWealth = expGain1 + expGain2 + expGain3;
        assertEquals(expWealth, testAccount.getTotalGain());
    }

    @Test
    void testTop3AssetsInEmptyAssets() {
        assertTrue(testAccount.getTop3AssetsName().isEmpty());
    }

    @Test
    void testTop3Assets1Asset() {
        testAccount.addAsset(a1);
        assertTrue(testAccount.getTop3AssetsName().contains(a1.getName()));
        assertEquals(1, testAccount.getTop3AssetsName().size());
    }

    @Test
    void testTop3Assets2Assets1() {
        testAccount.addAsset(a1);
        testAccount.addAsset(a2);
        testAccount.refresh();
        List<String> expList = new ArrayList<>();
        expList.add("asset2");
        expList.add("asset1");
        assertEquals(expList, testAccount.getTop3AssetsName());
    }

    @Test
    void testTop3Assets2Assets2() {
        testAccount.addAsset(a2);
        testAccount.addAsset(a1);
        testAccount.refresh();
        List<String> expList = new ArrayList<>();
        expList.add("asset2");
        expList.add("asset1");
        assertEquals(expList, testAccount.getTop3AssetsName());
    }

    @Test
    void testTop3Assets3Assets() {
        testAccount.addAsset(a1);
        testAccount.addAsset(a2);
        testAccount.addAsset(a3);
        testAccount.refresh();

        List<String> expList = new ArrayList<>();
        expList.add("asset2");
        expList.add("asset1");
        expList.add("asset3");
        assertEquals(expList, testAccount.getTop3AssetsName());
    }

    @Test
    void testTop3Assets5Assets() {
        testAccount.addAsset(a4);
        testAccount.addAsset(a2);
        testAccount.addAsset(a1);
        testAccount.addAsset(a3);
        testAccount.addAsset(a4);
        testAccount.refresh();

        List<String> expList = new ArrayList<>();
        expList.add("asset2");
        expList.add("asset1");
        expList.add("asset3");
        assertEquals(expList, testAccount.getTop3AssetsName());
    }
}
