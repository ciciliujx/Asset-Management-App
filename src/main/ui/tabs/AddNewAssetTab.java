package ui.tabs;

import model.Account;
import model.Asset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Represents the Add New Asset Tab

public class AddNewAssetTab extends Tab
        implements PropertyChangeListener, ActionListener {
    private JButton addButton;

    //Values for the fields
    private String name = "e.g., gic";
    private double principal = 0;
    private double rate = 0;
    private int termToMaturity = 0;
    private int gain = 0;
    private LocalDate maturityDate = LocalDate.now();

    //Labels to identify the fields
    private JLabel nameLabel;
    private JLabel principalLabel;
    private JLabel rateLabel;
    private JLabel termToMaturityLabel;
    private JLabel gainLabel;
    private JLabel maturityDateLabel;

    //Strings for the labels & button
    private static String nameString = "Name: ";
    private static String principalString = "Principal Amount: ";
    private static String rateString = "Annual Interest Rate (%): ";
    private static String termToMaturityString = "Term to Maturity (days): ";
    private static String gainString = "Period-to-date Interest: ";
    private static String maturityDateString = "Maturity Date: ";
    private static String addButtonString = "Add";

    //Fields for data entry
    private JFormattedTextField nameField;
    private JFormattedTextField principalField;
    private JFormattedTextField rateField;
    private JFormattedTextField termToMaturityField;
    private JFormattedTextField gainField;
    private JFormattedTextField maturityDateField;

    //Formats to format and parse numbers/dates
    private NumberFormat principalFormat;
    private NumberFormat percentFormat;
    private NumberFormat gainFormat;
    private DateTimeFormatter dateFormat;

    // EFFECTS: creates the add new asset tab with labels, fields, and the button
    public AddNewAssetTab(Account account) {
        super(account);
        setUpFormats();
        setUpLabels();
        setUpTextFields();
        pairLabelTextField();
        setUpButton();

        JPanel labelPane = getLabelPanel();
        JPanel fieldPane = getFieldPanel();
        JPanel buttonPane = getButtonPanel();

        placePanels(labelPane, fieldPane, buttonPane);
    }

    // MODIFIES: this
    // EFFECTS: puts the panels in this panel, labels on the left, text fields on the right, button on the bottom.
    private void placePanels(JPanel labelPane, JPanel fieldPane, JPanel buttonPane) {
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(labelPane, BorderLayout.CENTER);
        add(fieldPane, BorderLayout.LINE_END);
        add(buttonPane, BorderLayout.PAGE_END);
    }

    // MODIFIES: this
    // EFFECTS: lays out the button in a panel
    private JPanel getButtonPanel() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BorderLayout());
        buttonPane.add(addButton, BorderLayout.CENTER);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 150, 5, 150));
        return buttonPane;
    }

    // MODIFIES: this
    // EFFECTS: lays out the text fields in a panel
    private JPanel getFieldPanel() {
        JPanel fieldPane = new JPanel(new GridLayout(0,1));
        fieldPane.add(nameField);
        fieldPane.add(principalField);
        fieldPane.add(rateField);
        fieldPane.add(termToMaturityField);
        fieldPane.add(gainField);
        fieldPane.add(maturityDateField);
        return fieldPane;
    }

    // MODIFIES: this
    // EFFECTS: lays out the labels in a panel
    private JPanel getLabelPanel() {
        JPanel labelPane = new JPanel(new GridLayout(0,1));
        labelPane.add(nameLabel);
        labelPane.add(principalLabel);
        labelPane.add(rateLabel);
        labelPane.add(termToMaturityLabel);
        labelPane.add(gainLabel);
        labelPane.add(maturityDateLabel);
        return labelPane;
    }

    // MODIFIES: this
    // EFFECTS: tells accessibility tools about label/textfield pairs.
    private void pairLabelTextField() {
        nameLabel.setLabelFor(nameField);
        principalLabel.setLabelFor(principalField);
        rateLabel.setLabelFor(rateField);
        termToMaturityLabel.setLabelFor(termToMaturityField);
        gainLabel.setLabelFor(gainField);
        maturityDateLabel.setLabelFor(maturityDateField);
    }

    // MODIFIES: this
    // EFFECTS: creates and sets up text fields
    private void setUpTextFields() {
        nameField = new JFormattedTextField();
        nameField.setText(name);
        setTextFieldProperty(nameField);

        principalField = new JFormattedTextField(principalFormat);
        principalField.setValue(new Double(principal));
        setTextFieldProperty(principalField);

        rateField = new JFormattedTextField(percentFormat);
        rateField.setValue(new Double(rate));
        setTextFieldProperty(rateField);

        termToMaturityField = new JFormattedTextField();
        termToMaturityField.setValue(new Integer(termToMaturity));
        setTextFieldProperty(termToMaturityField);

        gainField = new JFormattedTextField(gainFormat);
        gainField.setValue(new Double(gain));
        gainField.setColumns(10);
        gainField.setEditable(false);
        gainField.setForeground(Color.blue);

        maturityDateField = new JFormattedTextField(dateFormat);
        maturityDateField.setValue(maturityDate);
        maturityDateField.setColumns(10);
        maturityDateField.setEditable(false);
        maturityDateField.setForeground(Color.blue);
    }

    // MODIFIES: this
    // EFFECTS: sets the column and action listener of the text field
    private void setTextFieldProperty(JFormattedTextField field) {
        field.setColumns(10);
        field.addPropertyChangeListener("value", this);
    }

    // MODIFIES: this
    // EFFECTS: creates and sets up labels
    private void setUpLabels() {
        nameLabel = new JLabel(nameString);
        principalLabel = new JLabel(principalString);
        rateLabel = new JLabel(rateString);
        termToMaturityLabel = new JLabel(termToMaturityString);
        gainLabel = new JLabel(gainString);
        maturityDateLabel = new JLabel(maturityDateString);
    }

    // MODIFIES: this
    // EFFECTS: sets up formats
    private void setUpFormats() {
        principalFormat = NumberFormat.getNumberInstance();
        principalFormat.setMinimumFractionDigits(2);

        percentFormat = NumberFormat.getNumberInstance();
        percentFormat.setMinimumFractionDigits(2);

        gainFormat = NumberFormat.getCurrencyInstance();

        dateFormat = DateTimeFormatter.ISO_LOCAL_DATE;
    }

    // MODIFIES: this
    // EFFECTS: creates and sets up the add button
    private void setUpButton() {
        addButton = new JButton(addButtonString);
        addButton.setActionCommand(addButtonString);
        addButton.addActionListener(this);
        addButton.setEnabled(true);
        addButton.setToolTipText("Add the asset to the current account");
    }

    // MODIFIES: this
    // EFFECTS: updates the fields when the value changes
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        Object source = e.getSource();
        if (source == nameField) {
            name = nameField.getText().toString();
        } else if (source == principalField) {
            principal = ((Number)principalField.getValue()).doubleValue();
        } else if (source == rateField) {
            rate = ((Number)rateField.getValue()).doubleValue();
        } else if (source == termToMaturityField) {
            termToMaturity = ((Number)termToMaturityField.getValue()).intValue();
        }
        Asset temp = new Asset(name, termToMaturity, rate, principal);

        double gain = temp.calculateFullReturn();
        gainField.setValue(new Double(gain));

        LocalDate maturityDate = temp.calculateMaturityDate();
        maturityDateField.setValue(maturityDate);
    }

    // MODIFIES: this
    // EFFECTS: adds the asset to the account when button clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        if (addButtonString.equals(e.getActionCommand())) {
            Asset newAsset = new Asset(name, termToMaturity, rate, principal);
            account.addAsset(newAsset);
        }
    }
}
