package persistence;

/*
 * A representation of a reader that reads account from JSON data stored in file
 * Code influced by the JsonSerizalizationDemo https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
 */

import model.Account;
import model.Asset;
import model.Withdrawal;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads account from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Account read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseAccount(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses account from JSON object and returns it
    private Account parseAccount(JSONObject jsonObject) {
        String name = jsonObject.getString("user_name");
        Account ac = new Account(name);
        addAccounts(ac, jsonObject);
        return ac;
    }

    // MODIFIES: ac
    // EFFECTS: parses active and removed accounts from JSON object and adds them to the main account
    private void addAccounts(Account ac, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("accounts");
        for (Object json : jsonArray) {
            JSONObject nextAccount = (JSONObject) json;
            String status = nextAccount.getString("status");
            addAssets(ac, nextAccount, status);
        }
    }

    // MODIFIES: ac
    // EFFECTS: parses assets from JSON object and adds it to the account
    private void addAssets(Account ac, JSONObject jsonObject, String status) {
        JSONArray jsonArray = jsonObject.getJSONArray("assets");
        for (Object json : jsonArray) {
            JSONObject nextAsset = (JSONObject) json;
            addAsset(ac, nextAsset, status);
        }
    }

    // MODIFIES: ac
    // EFFECTS: parses asset from JSON object and adds it to the related account (active/removed)
    private void addAsset(Account ac, JSONObject jsonObject, String status) {
        String name = jsonObject.getString("name");
        JSONObject features = jsonObject.getJSONObject("features");

        double interestRate = features.getDouble("interestRate") * 100;
        int termToMaturity = features.getInt("termToMaturity");
        double principal = features.getDouble("principal");
        LocalDate investDate = LocalDate.parse(features.getString("investDate"));
        Asset asset = new Asset(name, termToMaturity, interestRate, principal);
        asset.setInvestDate(investDate.getMonthValue(), investDate.getDayOfMonth(), investDate.getYear());
        addWithdraw(asset, features);

        ac.addAsset(asset);

        if (status.equals("removed")) {
            ac.removeAsset(asset);
        }
    }

    // MODIFIES: as
    // EFFECTS: parses withdrawList from JSON array and adds to the asset
    private void addWithdraw(Asset as, JSONObject jsonObject) {
        List<Withdrawal> withdrawList = as.getWithdrawList();
        JSONArray jsonArray = jsonObject.getJSONArray("withdrawList");

        for (Object json : jsonArray) {
            JSONObject nextWithdraw = (JSONObject) json;
            LocalDate withdrawDate = LocalDate.parse(nextWithdraw.getString("date"));
            double amount = nextWithdraw.getDouble("amount");
            Withdrawal withdraw = new Withdrawal(amount);
            withdraw.setDate(withdrawDate.getMonthValue(), withdrawDate.getDayOfMonth(), withdrawDate.getYear());

            withdrawList.add(withdraw);
        }
    }
}
