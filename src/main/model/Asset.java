package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

/*
 * A representation of an asset that has a fixed interest rate, fixed maturity term,
 * and the principal (amount invested) which can be withdrawn before maturity with
 * foreseen interest loss.
 */

public class Asset implements Writeable {
    private static final int DAYS_IN_YEAR = 365;
    private static final DecimalFormat df = new DecimalFormat("0.0000");

    private String name;
    private LocalDate investDate;
    private LocalDate maturityDate;
    private double principal; // in CAD
    private int daysHeld;
    private double fullReturn;
    private boolean isActive;
    private int termToMaturity; // in days
    private double interestRate; // annually, in percentage
    private double totalGain;
    private List<Withdrawal> withdrawList;

    // REQUIRES: termToMaturity > 0, interestRate > 0 and <=2 decimal places, principal > 0 and <= 2 decimal places
    // EFFECTS: constructs a new active asset with the given term to maturity, interest rate,
    // initial principal, zero days held, zero full return, zero total gain,
    // the investment date and maturity date set to today, and an empty list of withdrawal
    public Asset(String name, int termToMaturity, double interestRate, double principal) {
        this.name = name;
        this.termToMaturity = termToMaturity;
        double interestRateInDecimal = interestRate / 100;
        this.interestRate = Double.parseDouble(df.format(interestRateInDecimal));
        this.principal = principal;

        daysHeld = 0;
        fullReturn = 0;
        isActive = true;
        totalGain = 0.0;
        investDate = investDate.now();
        maturityDate = investDate.now();
        withdrawList = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: updates and returns the days that the asset has been held
    public int calculateDaysHeld() {
        LocalDate currentDate = LocalDate.now();
        daysHeld = (int) ChronoUnit.DAYS.between(investDate, currentDate);
        return daysHeld;
    }

    // MODIFIES: this
    // EFFECTS: updates and returns period-to-date interest with the current principal
    public double calculateFullReturn() {
        fullReturn = calculateReturn(termToMaturity, principal);
        return fullReturn;
    }

    // MODIFIES: this
    // EFFECTS: updates and returns the date of maturity
    public LocalDate calculateMaturityDate() {
        maturityDate = investDate.plusDays(termToMaturity);
        return maturityDate;
    }

    // REQUIRES: days > 0, amount > 0
    // Effects: returns the interest return for the given days and the given amount
    private double calculateReturn(int days, double amount) {
        double dailyInterestRate = interestRate / DAYS_IN_YEAR;
        double interest = amount * dailyInterestRate * days;
        return Math.round(interest * 100) / 100.0;
    }

    // REQUIRES: principal > 0; 0 < amount <= principal; getActiveStatus == true
    // MODIFIES: this
    // EFFECTS: adds the withdrawal attempt to the list of withdrawal;
    // decreases principal by the given amount; deactivates the asset if the principal becomes 0
    public void withdrawEarly(double amount) {
        Withdrawal newWithdrawal = new Withdrawal(amount);
        withdrawList.add(newWithdrawal);
        principal -= amount;
        if (getPrincipal() == 0) {
            deActivate();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the possible total interests gain to "today";
    // if it is expired, uses the total gain got before
    public void refreshTotalGain() {
        double result;
        if (isExpired()) {
            result = calculateFullReturn();
        } else {
            result = calculateCurrentGain(principal);
        }
        for (Withdrawal w : getWithdrawList()) {
            int days = (int) ChronoUnit.DAYS.between(investDate, w.getDate());
            result += calculateReturn(days, w.getAmount());
        }
        totalGain = result;
    }

    // REQUIRES: getActiveStatus == true
    // EFFECTS: returns the interest to today (exclude the principal);
    private double calculateCurrentGain(double amount) {
        int days = calculateDaysHeld();
        return calculateReturn(days, amount);
    }

    // EFFECTS: returns the potential loss if withdraw "today" before maturity
    public double calculatePotentialLoss(double amount) {
        return calculateReturn(termToMaturity, amount) - calculateCurrentGain(amount);
    }

    // MODIFIES: this
    // EFFECTS: deactivates the asset if the asset has expired
    public void refreshStatus() {
        if (isExpired() || principal == 0) {
            deActivate();
        }
    }

    // EFFECTS: returns true if the asset has expired (strictly passed the maturity date)
    private boolean isExpired() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(maturityDate);
    }

    // REQUIRES: 1 <= month <= 12, day takes integers from 1 to 28/29/30/31 based on the month
    // MODIFIES: this
    // EFFECTS: sets the investment date to given month, day, year;
    // updates the maturity date accordingly
    public void setInvestDate(int month, int day, int year) {
        this.investDate = LocalDate.of(year, month, day);
        this.maturityDate = calculateMaturityDate();
    }

    public void deActivate() {
        this.isActive = false;
    }

    public String getName() {
        return name;
    }

    public double getPrincipal() {
        return principal;
    }

    public double getTotalGain() {
        return totalGain;
    }

    public int getTermToMaturity() {
        return termToMaturity;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public boolean getActivatedStatus() {
        return isActive;
    }

    public int getDaysHeld() {
        return daysHeld;
    }

    public double getFullReturn() {
        return fullReturn;
    }

    public LocalDate getInvestDate() {
        return investDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public List<Withdrawal> getWithdrawList() {
        return withdrawList;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("features", fieldsToJson());
        return json;
    }

    // EFFECTS: returns fields as JSON Array
    private JSONObject fieldsToJson() {
        JSONObject features = new JSONObject();
        features.put("interestRate", interestRate);
        features.put("termToMaturity", termToMaturity);
        features.put("investDate", investDate);
        features.put("principal", principal);
        features.put("withdrawList", withdrawListToJson());
        return features;
    }

    // EFFECTS: returns withdrawals a JSON Array
    private JSONArray withdrawListToJson() {
        JSONArray withdrawals = new JSONArray();
        for (Withdrawal w: withdrawList) {
            withdrawals.put(w.toJson());
        }
        return withdrawals;
    }

}
