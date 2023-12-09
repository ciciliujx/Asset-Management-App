package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * A representation of a list of assets in a user's account.
 */

public class Account implements Writeable {
    List<Asset> account;
    List<Asset> assetsRemoved;
    String name;

    // EFFECTS: creates a new empty account without assets
    public Account(String name) {
        account = new ArrayList<>();
        assetsRemoved = new ArrayList<>();
        this.name = name;
    }

    // MODIFIES: this
    // EFFECTS: add the given asset to the account
    public void addAsset(Asset asset) {
        account.add(asset);
        EventLog.getInstance().logEvent(new Event(asset.getName() + " added to account."));
    }

    // REQUIRES: asset.getActivatedStatus == false or asset.getPrincipal == 0
    // MODIFIES: this, asset
    // EFFECTS: remove the given asset from the account;
    // adds the removed asset to the assetRemoved list;
    // deactivates the given asset
    public void removeAsset(Asset asset) {
        account.remove(asset);
        assetsRemoved.add(asset);
        asset.deActivate();
        EventLog.getInstance().logEvent(new Event(asset.getName() + " removed from account."));
    }

    // EFFECTS: returns the current number of assets in the account
    public int numAssets() {
        return account.size();
    }

    // EFFECTS: returns the number of active assets in the account
    public int numActiveAssets() {
        int result = 0;
        for (Asset a : account) {
            if (a.getActivatedStatus()) {
                result += 1;
            }
        }
        return result;
    }

    // MODIFIES: all Assets in Account, assetsRemoved
    // EFFECTS: refresh the status and total gain of all assets in the account and removed assets
    public void refresh() {
        if (!account.isEmpty()) {
            for (Asset a : account) {
                a.refreshStatus();
                a.refreshTotalGain();
                a.calculateDaysHeld();
            }
        }
        if (!assetsRemoved.isEmpty()) {
            for (Asset a : assetsRemoved) {
                a.refreshStatus();
                a.refreshTotalGain();
                a.calculateDaysHeld();
            }
        }
    }

    // EFFECTS: returns the total interest gain
    public double getTotalGain() {
        double gain = 0;
        if (!account.isEmpty()) {
            for (Asset a : account) {
                gain = gain + a.getTotalGain();
            }
        }
        if (!assetsRemoved.isEmpty()) {
            for (Asset a : assetsRemoved) {
                gain = gain + a.getTotalGain();
            }
        }
        return gain;
    }

    // EFFECTS: returns names of top 3 assets
    public List<String> getTop3AssetsName() {
        List<String> top3 = new ArrayList<>();
        List<Asset> top3Assets = top3Assets();
        for (Asset asset: top3Assets) {
            top3.add(asset.getName());
        }
        return top3;
    }

    // EFFECTS: returns the top three assets with the highest interests return
    //    to "today" among all assets presently in the account (take the de-activated
    //    assets into account) in the order of first, second, and third place;
    //    the later added asset ranks higher if there is a tie
    public List<Asset> top3Assets() {
        if (account.size() < 3) {
            return top3AssetsSpecial();
        }
        Asset temp = new Asset("base", 0, 0, 0);
        Asset first = account.get(0);
        Asset second = temp;
        Asset third = temp;
        for (int i = 1; i < account.size(); i++) {
            double gain = getGainforAsset(i);
            if (gain > first.getTotalGain()) {
                third = second;
                second = first;
                first = account.get(i);
            } else if (gain >= second.getTotalGain()) {
                third = second;
                second = account.get(i);
            } else if (gain >= third.getTotalGain()) {
                third = account.get(i);
            }
        }
        Asset[] result = new Asset[]{first, second, third};
        return Arrays.asList(result);
    }

    // EFFECTS: returns the top three assets with the highest interests return
    // when the account has less than 3 assets
    private List<Asset> top3AssetsSpecial() {
        List<Asset> top3 = new ArrayList<>();

        if (!account.isEmpty()) {
            if (account.size() == 1) {
                top3.add(account.get(0));
            } else {
                Asset asset1 = account.get(0);
                Asset asset2 = account.get(1);
                if (asset1.getTotalGain() >= asset2.getTotalGain()) {
                    top3.add(asset1);
                    top3.add(asset2);
                } else {
                    top3.add(asset2);
                    top3.add(asset1);
                }
            }
        }
        return top3;
    }

    // EFFECTS: gets the total gain of the asset at index i in the account
    private double getGainforAsset(int i) {
        return account.get(i).getTotalGain();
    }

    // EFFECTS: returns the list of assets in the account in the order they were added
    public List<Asset> getAccount() {
        return account;
    }

    // EFFECTS: returns the list of assets in assetsRemoved in the order they were removed
    public List<Asset> getAssetsRemoved() {
        return assetsRemoved;
    }

    public String getName() {
        return name;
    }

    // EFFECTS: returns all the names of removed assets in the order they were removed
    public List<String> getNamesAssetsRemoved() {
        List<String> names = new ArrayList<>();
        for (Asset a : assetsRemoved) {
            names.add(a.getName());
        }
        return names;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("user_name", name);
        json.put("accounts", accountsToJson());
        EventLog.getInstance().logEvent(new Event("Changes saved to file."));
        return json;
    }

    // EFFECTS: returns 2 account (account, assetsRemoved) as a JSON array
    private JSONArray accountsToJson() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonActiveAccount = new JSONObject();
        jsonActiveAccount.put("status", "current");
        jsonActiveAccount.put("assets", assetsToJson(this.account));
        JSONObject jsonRemovedAccount = new JSONObject();
        jsonRemovedAccount.put("status", "removed");
        jsonRemovedAccount.put("assets", assetsToJson(this.assetsRemoved));
        jsonArray.put(jsonActiveAccount);
        jsonArray.put(jsonRemovedAccount);
        return jsonArray;
    }

    // EFFECTS: returns assets in this account as a JSON array
    private JSONArray assetsToJson(List<Asset> account) {
        JSONArray jsonArray = new JSONArray();

        for (Asset a : account) {
            jsonArray.put(a.toJson());
        }

        return jsonArray;
    }
}
