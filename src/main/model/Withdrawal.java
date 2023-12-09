package model;

import org.json.JSONObject;

import java.time.LocalDate;

/*
 * A representation of a withdrawal with the corresponding amount and the date of withdrawal.
 */

public class Withdrawal implements Writeable {
    private double amount;
    private LocalDate date;

    // REQUIRES: amount <= asset.getPrincipal(), 2 decimal places;
    // EFFECTS: creates a withdrawal instance with the given amount and today's date
    public Withdrawal(double amount) {
        this.amount = amount;
        this.date = LocalDate.now();
    }

    public void setDate(int month, int day, int year) {
        this.date = LocalDate.of(year, month, day);
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("date", String.valueOf(date));
        jsonObject.put("amount", amount);
        return jsonObject;
    }
}
