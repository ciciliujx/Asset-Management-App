package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    Asset a1;
    Asset a2;
    LocalDate today;

    @BeforeEach
    void runBefore() {
        a1 = new Asset("asset 1", 180, 5, 1000.0);
        a2 = new Asset("asset 2", 10000, 5, 1000);
        today = LocalDate.now();
    }

    @Test
    void testConstructor() {
        assertEquals("asset 1", a1.getName());
        assertEquals(180, a1.getTermToMaturity());
        assertEquals(0.05, a1.getInterestRate());
        assertEquals(1000, a1.getPrincipal());
        assertEquals(0, a1.getDaysHeld());
        assertEquals(0, a1.getFullReturn());
        assertTrue(a1.getActivatedStatus());
        assertEquals(0, a1.getTotalGain());
        assertEquals(today, a1.getInvestDate());
        assertEquals(today, a1.getMaturityDate());
    }

    @Test
    void testCalculateDaysHeld() {
        LocalDate targetInvestDate = today.minusDays(180);
        a1.setInvestDate(targetInvestDate.getMonthValue(),
                targetInvestDate.getDayOfMonth(),
                targetInvestDate.getYear()); // get mature today
        a1.calculateDaysHeld();
        assertEquals(180, a1.getDaysHeld());
    }

    @Test
    void testCalculateFullReturn() {
        double expectedReturn = Math.round(0.05 * 1000 / 365 * 180 * 100) / 100.0;
        assertEquals(expectedReturn, a1.calculateFullReturn());
    }

    @Test
    void testCalculateMaturityDate() {
        a1.setInvestDate(10, 1, 2023);
        LocalDate expectedDate = LocalDate.of(2024, 3, 29);
        assertEquals(expectedDate, a1.calculateMaturityDate());
    }

    @Test
    void testWithdrawPartlyBeforeMaturity() {
        a1.setInvestDate(5, 1, 2023);
        a1.withdrawEarly(900);
        assertEquals(100, a1.getPrincipal());
        assertTrue(a1.getActivatedStatus());
        assertEquals(1, a1.getWithdrawList().size());
        assertEquals(900, a1.getWithdrawList().get(0).getAmount());
        LocalDate expDate = LocalDate.now();
        assertEquals(expDate, a1.getWithdrawList().get(0).getDate());
    }

    @Test
    void testWithdrawAllOnTheDayOfMaturity() {
        LocalDate tempInvestDate = today.minusDays(a1.getTermToMaturity());
        int month = tempInvestDate.getMonthValue();
        int year = tempInvestDate.getYear();
        int day = tempInvestDate.getDayOfMonth();
        a1.setInvestDate(month, day, year); // get mature today

        a1.withdrawEarly(1000);
        assertEquals(0, a1.getPrincipal());
        assertEquals(1, a1.getWithdrawList().size());
        assertEquals(1000, a1.getWithdrawList().get(0).getAmount());
        assertEquals(today, a1.getWithdrawList().get(0).getDate());
        assertFalse(a1.getActivatedStatus());
    }

    @Test
    void testRefreshTotalGainAtCurrentDate() {
        a1.refreshTotalGain();
        assertEquals(0, a1.getTotalGain());
    }

    @Test
    void testRefreshTotalGainNoWithdrawalMatureDay() {
        LocalDate tempInvestDate = today.minusDays(a1.getTermToMaturity());
        int month = tempInvestDate.getMonthValue();
        int year = tempInvestDate.getYear();
        int day = tempInvestDate.getDayOfMonth();
        a1.setInvestDate(month, day, year);
        double expReturn = a1.calculateFullReturn();
        a1.refreshTotalGain();
        assertEquals(expReturn, a1.getTotalGain());
    }

    @Test
    void testRefreshTotalGainWithdrawEarlyOnceNotMature() {
        a2.setInvestDate(5, 1, 2023);
        a2.withdrawEarly(900);
        a2.getWithdrawList().get(0).setDate(9, 1, 2023);
        LocalDate withdrawDate = LocalDate.of(2023, 9, 1);
        int days = (int) ChronoUnit.DAYS.between(a2.getInvestDate(), withdrawDate);
        double withdrawInterest = Math.round(900 * days * 0.05 / 365 * 100) / 100.0;
        int daysHeld = a2.calculateDaysHeld();
        double fullTermInterest = Math.round(100 * daysHeld * 0.05 / 365 * 100) / 100.0;
        double expTotalGain = withdrawInterest + fullTermInterest;
        a2.refreshTotalGain();
        assertEquals(expTotalGain, a2.getTotalGain());
    }

    @Test
    void testRefreshTotalGainWithdrawEarlyMultipleTimesNotMature() {
        a2.setInvestDate(5, 1, 2023);
        a2.withdrawEarly(300);
        a2.withdrawEarly(500);
        a2.getWithdrawList().get(0).setDate(7, 1, 2023); //61 days
        a2.getWithdrawList().get(1).setDate(9, 1, 2023); //123 days
        double withdrawInterest1 = Math.round(300 * 61 * 0.05 / 365 * 100) / 100.0;
        double withdrawInterest2 = Math.round(500 * 123 * 0.05 / 365 * 100) / 100.0;
        int daysHeld = a2.calculateDaysHeld();
        double fullTermInterest = Math.round(200 * daysHeld * 0.05 / 365 * 100) / 100.0;
        double expTotalGain = withdrawInterest1 + withdrawInterest2 + fullTermInterest;
        a2.refreshTotalGain();
        assertEquals(expTotalGain, a2.getTotalGain());
    }

    @Test
    void testRefreshTotalGainWithdrawEarlyMatureDay() {
        LocalDate tempInvestDate = today.minusDays(a1.getTermToMaturity());
        int m1 = tempInvestDate.getMonthValue();
        int y1 = tempInvestDate.getYear();
        int d1 = tempInvestDate.getDayOfMonth();
        a1.setInvestDate(m1, d1, y1);
        a1.withdrawEarly(500);
        LocalDate withdrawDate = tempInvestDate.plusDays(145);
        int m2 = withdrawDate.getMonthValue();
        int y2 = withdrawDate.getYear();
        int d2 = withdrawDate.getDayOfMonth();
        a1.getWithdrawList().get(0).setDate(m2, d2, y2); //145 days
        double withdrawInterest = Math.round(500 * 145 * 0.05 / 365 * 100) / 100.0;
        double fullTermInterest = Math.round(500 * 180 * 0.05 / 365 * 100) / 100.0;
        double expTotalGain = withdrawInterest + fullTermInterest;
        a1.refreshTotalGain();
        assertEquals(expTotalGain, a1.getTotalGain());
    }

    @Test
    void testRefreshTotalGainExpired() {
        a1.setInvestDate(1, 1, 2023);
        a1.refreshTotalGain();
        assertEquals(a1.calculateFullReturn(), a1.getTotalGain());
    }

    @Test
    void testRefreshTotalGainWithdrawExpired() {
        a1.setInvestDate(1, 1, 2023);
        a1.withdrawEarly(500);
        a1.getWithdrawList().get(0).setDate(3, 1, 2023); //59 days
        double withdrawInterest = Math.round(500 * 59 * 0.05 / 365 * 100) / 100.0;
        double fullTermInterest = Math.round(500 * 180 * 0.05 / 365 * 100) / 100.0;
        double expTotalGain = withdrawInterest + fullTermInterest;
        a1.refreshTotalGain();
        assertEquals(expTotalGain, a1.getTotalGain());
    }

    @Test
    void testCalculatePotentialLoss() {
        int daysHeld = a1.getDaysHeld();
        double expLoss = Math.round(500 * (180 - daysHeld) * 0.05 / 365 * 100) / 100.0;
        assertEquals(expLoss, a1.calculatePotentialLoss(500));
    }

    @Test
    void testRefreshStatusInactive() {
        a1.setInvestDate(1, 1, 2023);
        a1.refreshStatus();
        assertFalse(a1.getActivatedStatus());
    }

    @Test
    void testRefreshStatusActive() {
        a1.setInvestDate(7, 1, 2023);
        a1.refreshStatus();
        assertTrue(a1.getActivatedStatus());
    }

    @Test
    void testRefreshStatusNoPrincipal() {
        a1.withdrawEarly(1000);
        a1.refreshStatus();
        assertFalse(a1.getActivatedStatus());
    }
}