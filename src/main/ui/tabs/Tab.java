package ui.tabs;

import model.Account;

import javax.swing.*;
import java.awt.*;

// Represents an abstract tab that can be extended by other tabs and access the user's account

public abstract class Tab extends JPanel {

    protected Account account;

    // EFFECTS: creates a new tab with the given account
    public Tab(Account account) {
        super(new BorderLayout());
        this.account = account;
    }
}
