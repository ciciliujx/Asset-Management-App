package ui;

import model.Account;
import model.Asset;
import persistence.JsonWriter;
import persistence.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class WalesApp {
    private static final String JSON_STORE = "./data/account.json";
    private Account account;
    private Scanner input;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: runs the Wales application
    public WalesApp() {
        System.out.println("\nWelcome to use Wales Asset! How can I help you today?");
        runWales();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runWales() {
        boolean keepGoing = true;
        String command;

        init();

        while (keepGoing) {
            displayMenu();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
            } else if (command.equals("as")) {
                commandOnAsset();
            } else if (command.equals("ac")) {
                displayAccountSummary();
            } else if (command.equals("s")) {
                saveAccount();
            } else if (command.equals("l")) {
                loadAccount();
            }
        }

        System.out.println("\nThank you for using Wales Asset today! Goodbye!");
    }

    // MODIFIES: this
    // EFFECTS: initializes an account with a sample asset
    private void init() {
        account = new Account("My Account");
        Asset assetSample = new Asset("Treasury Bill", 900, 5.09, 1000);
        assetSample.setInvestDate(1, 1, 2023);
        account.addAsset(assetSample);
        input = new Scanner(System.in);
        input.useDelimiter("\n");
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }

    // EFFECTS: displays menu of options to user
    private void displayMenu() {
        System.out.println("\nYou may select from:");
        System.out.println("\tas -> manage my assets");
        System.out.println("\tac -> access my account summary");
        System.out.println("\ts -> save account to file");
        System.out.println("\tl -> load account from file");
        System.out.println("\tq -> quit");
    }

    // MODIFIES: this
    // EFFECTS: displays all assets in the account
    private void displayAsset() {
        account.refresh();
        System.out.println("\nYour assets in the account:");
        String status = "Active";
        for (Asset a : account.getAccount()) {
            if (!a.getActivatedStatus()) {
                status = "Inactive";
            }
            System.out.println("\t" + a.getName() + ", invested on " + a.getInvestDate() + ", " + status);
        }
    }

    // EFFECTS: processes user input on assets
    private void commandOnAsset() {
        boolean keepGoing = true;
        String command;

        while (keepGoing) {
            displayAsset();
            displayAssetManagementMenu();
            command = input.next();

            if (command.equals("back")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }
    }

    // EFFECTS: displays menu of options to manage assets to user
    private void displayAssetManagementMenu() {
        System.out.println("\nYou may select from:");
        System.out.println("\tasset name -> manage this asset");
        System.out.println("\tadd -> add a new asset to your account");
        System.out.println("\tremove -> remove an inactive/empty asset");
        System.out.println("\tback -> go back to the parent menu");
    }

    // EFFECTS: process the command on asset
    private void processCommand(String command) {
        if (command.equals("add")) {
            addAssetToAccount();
        } else if (command.equals("remove")) {
            removeAssetFromAccount();
        } else {
            manageAsset(command);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a new asset to the account
    private void addAssetToAccount() {
        System.out.println("\nPlease enter the following information for your new asset: ");
        Asset newAsset = createNewAsset();
        account.addAsset(newAsset);
        LocalDate maturityDate = newAsset.calculateMaturityDate();
        double fullReturn = newAsset.calculateFullReturn();

        System.out.println("\nYou've successfully add asset:" + newAsset.getName() + " to the account!");
        System.out.println("Maturity Date: " + maturityDate);
        System.out.println("Period-to-date Interest: " + fullReturn);
    }

    // MODIFIES: this
    // EFFECTS: creates a new asset
    private Asset createNewAsset() {
        String name;
        double rate;
        int days;
        double principal;

        System.out.print("\nName (not been used): ");
        name = input.next();
        while (!validName(name)) {
            System.out.print("\nInvalid name. Enter a name that has never been used: ");
            name = input.next();
        }

        do {
            System.out.print("Annual Interest Rate (_.__%): ");
            rate = input.nextDouble();
        } while (rate <= 0);

        do {
            System.out.print("Term to Maturity (in days): ");
            days = input.nextInt();
        } while (days <= 0);

        do {
            System.out.print("Principal: ");
            principal = input.nextDouble();
        } while (principal <= 0);

        return new Asset(name, days, rate, principal);
    }

    // EFFECTS: returns false if the asset name has been used
    private boolean validName(String name) {
        for (Asset a : account.getAccount()) {
            if (name.equals(a.getName())) {
                return false;
            }
        }
        return true;
    }

    // MODIFIES: this
    // EFFECTS: removes the given asset from the account, does nothing if it is active
    private void removeAssetFromAccount() {
        System.out.print("\nPlease enter the name of the asset that you want to remove: ");
        String name = input.next();
        for (Asset a : account.getAccount()) {
            if (name.equals(a.getName())) {
                a.refreshStatus();
                if (a.getActivatedStatus()) {
                    System.out.println("Invalid removal! This asset is active and has non-zero principal.");
                } else {
                    account.removeAsset(a);
                    System.out.println("Successfully removed!");
                }
                return;
            }
        }
        System.out.println("Asset not found!");
    }

    // MODIFIES: this
    // EFFECTS: displays information and the option of withdrawal of an asset
    private void manageAsset(String name) {
        for (Asset a : account.getAccount()) {
            if (name.equals(a.getName())) {
                a.refreshTotalGain();
                a.refreshStatus();
                displayAssetInformation(a);
                displayWithdrawal(a);
                return;
            }
        }
        System.out.println("Asset not found! Please try again.");
    }

    // EFFECTS: displays information for the asset
    private void displayAssetInformation(Asset a) {
        System.out.println("\nHere is a summary of " + a.getName());
        System.out.println("\tCurrent Total Gain: " + a.getTotalGain());
        System.out.println("\tPeriod-to-date Interest: " + a.calculateFullReturn());
        System.out.println("\tDays of Holding/Term to Maturity: " + a.getDaysHeld() + "/"
                + a.getTermToMaturity());
        System.out.println("\tMaturity Date: " + a.getMaturityDate());
        System.out.println("\tPrincipal: " + a.getPrincipal());
    }

    // MODIFIES: this
    // EFFECTS: displays the option of withdrawal, withdraws the asset if applied
    private void displayWithdrawal(Asset a) {
        System.out.println("\nDo you want to withdraw and earn some interest today?");
        System.out.println("Select from:");
        System.out.println("\ty -> yes");
        System.out.println("\tn -> no");
        String command = input.next();

        if (command.equals("y")) {
            doWithdrawal(a);
        }
    }

    // MODIFIES: this
    // EFFECTS: conducts a withdrawal transaction
    private void doWithdrawal(Asset a) {
        System.out.print("Enter amount to withdraw: ");
        double amount = input.nextDouble();
        if (amount < 0.0) {
            System.out.println("Cannot withdraw negative amount.\n");
        } else if (a.getPrincipal() < amount) {
            System.out.println("Insufficient principal in the asset.\n");
        } else {
            withdrawAsset(a, amount);
        }
    }

    // MODIFIES: this
    // EFFECTS: Prompts the amount that the user may lose by withdrawing the given amount,
    // proceeds to withdrawal if applied
    private void withdrawAsset(Asset a, double amount) {
        double potentialLoss = a.calculatePotentialLoss(amount);
        System.out.println("You'll lose " + potentialLoss
                + " of interest earned if you withdraw before the end of your maturity period."
                + " Your maturity date is on " + a.getMaturityDate() + ".");
        System.out.println("Do you want to proceed? Select from: ");
        System.out.println("\ty -> yes");
        System.out.println("\tn -> no");
        String command = input.next();
        if (command.equals("y")) {
            a.withdrawEarly(amount);
            System.out.println("The current principal in your asset is " + a.getPrincipal());
        }
    }

    // MODIFIES: this
    // EFFECTS: displays account summary and the option to access the removed assets
    private void displayAccountSummary() {
        account.refresh();
        LocalDate today = LocalDate.now();
        int numAssets = account.numAssets();
        int numActiveAssets = account.numActiveAssets();
        double wealth = account.getTotalGain();
        List<String> top3assets = account.getTop3AssetsName();

        System.out.println("\nOn " + today + ",");
        System.out.println("You possess " + numActiveAssets + " active asset(s) out of " + numAssets
                + " total asset(s)");
        System.out.println("Your accumulated wealth is: " + wealth);
        System.out.println("Your top 3 assets are: " + top3assets);

        reviewRemovedAssets();
    }

    // EFFECTS: displays the list of names of the removed assets
    private void reviewRemovedAssets() {
        System.out.println("\nYou may select from: ");
        System.out.println("\tr -> review the removed assets");
        System.out.println("\tback -> go back to the parent menu");
        String command = input.next();
        if (command.equals("r")) {
            List<String> removedAssets = account.getNamesAssetsRemoved();
            if (removedAssets.isEmpty()) {
                System.out.println("No assets were removed.");
            } else {
                System.out.println(removedAssets);
            }
        } else {
            System.out.println("Invalid entry.");
        }
    }

    // EFFECTS: saves the account to file
    public void saveAccount() {
        try {
            jsonWriter.open();
            jsonWriter.write(this.account);
            jsonWriter.close();
            System.out.println("Saved " + account.getName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads account from file
    private void loadAccount() {
        try {
            account = jsonReader.read();
            System.out.println("Loaded " + account.getName() + " from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }
}
