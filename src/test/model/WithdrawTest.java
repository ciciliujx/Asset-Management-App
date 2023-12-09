package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WithdrawTest {
    Withdrawal testWithdraw;
    LocalDate today;

    @BeforeEach
    void runBefore() {
        testWithdraw = new Withdrawal(100);
        today = LocalDate.now();
    }

    @Test
    void testConstructor() {
        assertEquals(100, testWithdraw.getAmount());
        assertEquals(today, testWithdraw.getDate());
    }
}
