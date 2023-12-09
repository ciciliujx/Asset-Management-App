package Persistence;

import model.Asset;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {

    protected void checkAsset(String name, int days, double r, double p, LocalDate date, Asset a) {
        assertEquals(name, a.getName());
        assertEquals(days, a.getTermToMaturity());
        assertEquals(r, a.getInterestRate());
        assertEquals(p, a.getPrincipal());
        assertEquals(date, a.getInvestDate());
    }
}
