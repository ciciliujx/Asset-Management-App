package ui;

import model.Account;
import model.EventLog;
import model.Event;
import persistence.JsonReader;
import persistence.JsonWriter;
import ui.tabs.AccountTab;
import ui.tabs.AddNewAssetTab;
import ui.tabs.AssetTab;
import ui.tabs.Tab;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;

// Represents the Graphical User Interface (GUI) of the application

public class WalesUI extends JFrame {
    private static final int ASSET_TAB_INDEX = 0;
    private static final int ADD_NEW_ASSET_TAB_INDEX = 1;
    private static final int ACCOUNT_TAB_INDEX = 2;
    private static final String JSON_STORE = "./data/account.json";

    private JFrame initialMenu;
    private JTabbedPane sidebar;
    private Tab assetTab;
    private Tab addNewAssetTab;
    private Tab accountTab;

    private Account account;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: constructs the console with a designated data repository, displays the initial menu
    private WalesUI() {
        super("Wales Asset");
        account = new Account("My Account");
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
        initialMenu = new InitialMenu();
    }

    // MODIFIES: this
    // EFFECTS: loads account from file
    private void loadAccount() {
        try {
            account = jsonReader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // MODIFIES: this
    // EFFECTS: displays the main page
    private void mainPage() {
        setSize(new Dimension(600, 420));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        saveWhenExit();

        sidebar = new JTabbedPane();
        sidebar.setTabPlacement(JTabbedPane.LEFT);

        loadTabs();
        add(sidebar);

        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: shows the option of saving data to file when close the window
    private void saveWhenExit() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {

                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Do you want to save your change to the data?", 
                        "Exit Program Message Box", JOptionPane.YES_NO_CANCEL_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    try {
                        jsonWriter.open();
                        jsonWriter.write(account);
                        jsonWriter.close();
                        printLog(EventLog.getInstance());
                        System.exit(0);
                    } catch (FileNotFoundException e) {
                        System.out.println("Unable to write to file: " + JSON_STORE);
                    }
                } else if (confirmed == JOptionPane.NO_OPTION) {
                    printLog(EventLog.getInstance());
                    System.out.println("Changes not saved.");
                    System.exit(0);
                }
            }
        });
    }

    // EFFECTS: prints all the events logged
    private void printLog(EventLog el) {
        for (Event next : el) {
            System.out.println(next.toString() + "\n");
        }
    }

    // MODIFIES: this
    // EFFECTS: loads the tabs into the sidebar
    private void loadTabs() {
        assetTab = new AssetTab(account);
        addNewAssetTab = new AddNewAssetTab(account);
        accountTab = new AccountTab(account);

        sidebar.add(assetTab, ASSET_TAB_INDEX);
        sidebar.setTitleAt(ASSET_TAB_INDEX, "Asset");
        sidebar.add(addNewAssetTab, ADD_NEW_ASSET_TAB_INDEX);
        sidebar.setTitleAt(ADD_NEW_ASSET_TAB_INDEX, "Add New Asset");
        sidebar.add(accountTab, ACCOUNT_TAB_INDEX);
        sidebar.setTitleAt(ACCOUNT_TAB_INDEX, "Account");
    }

    // Represents the initial menu when user starts the application
    private class InitialMenu extends JFrame implements ActionListener {
        private static final String INIT_GREETING = "Welcome to Wales Asset";
        private JLabel greeting;
        private JButton b1;
        private JButton b2;
        private JPanel buttonPane;

        // EFFECTS: constructs the initial menu with buttons
        public InitialMenu() {
            super("Wales Asset");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationByPlatform(true);
            setResizable(false);
            setSize(200, 250);
            setLayout(new GridLayout(2, 1));

            placeGreeting();
            placeButtons();

            setVisible(true);
        }

        // MODIFIES: this
        // EFFECTS: creates greeting at top of console
        private void placeGreeting() {
            greeting = new JLabel(INIT_GREETING, JLabel.CENTER);
            greeting.setSize(WIDTH, HEIGHT / 3);
            add(greeting);
        }

        // MODIFIES: this
        // EFFECTS: creates two buttons to choose whether to load data from file
        private void placeButtons() {
            b1 = new JButton("My Account");
            b1.setActionCommand("load");

            b2 = new JButton("Start a New Account");
            b2.setActionCommand("new");

            b1.addActionListener(this);
            b2.addActionListener(this);

            b1.setToolTipText("Load data from what you left off");
            b2.setToolTipText("Start as a new user");

            buttonPane = new JPanel(new BorderLayout());
            buttonPane.setBorder(new EmptyBorder(2, 3, 2, 3));
            JPanel layout = new JPanel(new GridBagLayout());
            layout.setBorder(new EmptyBorder(5, 5, 5, 5));
            JPanel btnPanel = new JPanel(new GridLayout(2, 1, 1, 2));
            btnPanel.add(b1);
            btnPanel.add(b2);
            layout.add(btnPanel);
            buttonPane.add(layout, BorderLayout.CENTER);

            add(buttonPane);
        }

        // MODIFIES: this
        // EFFECTS: loads data if applicable, displays the main page
        @Override
        public void actionPerformed(ActionEvent e) {
            initialMenu.dispose();
            if (e.getActionCommand().equals("load")) {
                loadAccount();
            }
            mainPage();
        }
    }

    public static void main(String[] args) {
        new WalesUI();
    }
}
