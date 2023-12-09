package ui.tabs;

import model.Account;
import model.Asset;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

// Represents the Asset Tab which displays all assets in the account

public class AssetTab extends Tab
        implements ActionListener, ListSelectionListener {

    private static final String removeString = "Remove";
    private JSplitPane splitPane;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JList list;
    private DefaultListModel listModel;
    private JScrollPane listScrollPane;
    private JButton refreshButton;
    private JButton removeButton;
    private JLabel infoLabel;

    // EFFECTS: constructs an asset tab with left and right panels on a split pane
    public AssetTab(Account account) {
        super(account);

        setUpInfoLabel();

        initLeftPanel();
        initRightPanel(infoLabel);

        placePanels();
    }

    // MODIFIES: this
    // EFFECTS: creates the info label
    private void setUpInfoLabel() {
        this.infoLabel = new JLabel() {
            public Dimension getPreferredSize() {
                return new Dimension(300, 200);
            }

            public Dimension getMinimumSize() {
                return new Dimension(300, 200);
            }

            public Dimension getMaximumSize() {
                return new Dimension(300, 200);
            }
        };
        infoLabel.setVerticalAlignment(SwingConstants.CENTER);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // MODIFIES: this
    // EFFECTS: places the left and right panels on split pane
    private void placePanels() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, rightPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        Dimension minimumSize = new Dimension(100, 50);
        leftPanel.setMinimumSize(minimumSize);
        rightPanel.setMinimumSize(minimumSize);

        add(splitPane);
    }

    // MODIFIES: this
    // EFFECTS: creates the left panel with the scroll panel and buttons
    private void initLeftPanel() {
        leftPanel = new JPanel();
        initScrollPane();
        initButton();

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BorderLayout());
        buttonPane.add(removeButton, BorderLayout.CENTER);
        buttonPane.add(refreshButton, BorderLayout.LINE_END);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 50, 5, 50));

        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        leftPanel.add(buttonPane, BorderLayout.PAGE_END);
    }

    // MODIFIES: this
    // EFFECTS: sets up the scrolling pane with a list of asset name on it
    private void initScrollPane() {
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(15);
        updateList();
        list.setSelectedIndex(0);
        listScrollPane = new JScrollPane(list);
        listScrollPane.setPreferredSize(new Dimension(180, 300));
    }

    // MODIFIES: this
    // EFFECTS: updates the list of asset
    private void updateList() {
        account.refresh();

        listModel.removeAllElements();
        for (Asset asset : account.getAccount()) {
            listModel.addElement(asset.getName());
        }
        list.setSelectedIndex(listModel.size() - 1);
    }

    // MODIFIES: this
    // EFFECTS: sets up the remove button and refresh button
    private void initButton() {
        removeButton = new JButton(removeString);
        removeButton.setActionCommand(removeString);
        removeButton.addActionListener(this);
        removeButton.setEnabled(true);
        removeButton.setToolTipText("Remove an asset permanently from the current account");

        ImageIcon refreshIcon = new ImageIcon("data/images/refresh.png");
        Image image = refreshIcon.getImage();
        Image newImg = image.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);
        refreshIcon = new ImageIcon(newImg);

        refreshButton = new JButton(refreshIcon);
        refreshButton.setMnemonic(KeyEvent.VK_R);
        refreshButton.setActionCommand("refresh");
        refreshButton.addActionListener(this);
        refreshButton.setEnabled(true);
        refreshButton.setOpaque(false);
        refreshButton.setContentAreaFilled(false);
        refreshButton.setBorderPainted(false);
    }

    // MODIFIES: this
    // EFFECTS: removes the selected asset from the list and user's account when remove button clicked;
    // or refreshes the scroll pane when refresh icon clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        if (removeString.equals(e.getActionCommand())) {
            int index = list.getSelectedIndex();
            listModel.remove(index);
            account.removeAsset(account.getAccount().get(index));

            int size = listModel.getSize();

            if (size == 0) { //Nobody's left, disable remove.
                removeButton.setEnabled(false);

            } else { //Select an index.
                if (index == listModel.getSize()) {
                    //removed item in last position
                    index--;
                }

                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        } else if (e.getActionCommand().equals("refresh")) {
            updateList();
        }
    }

    // MODIFIES: this
    // EFFECTS: creates the right panel with the info label
    private void initRightPanel(JLabel infoLabel) {
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Asset Details"),
                BorderFactory.createEmptyBorder(10,10,10,10)));
        rightPanel.add(infoLabel);
    }

    // MODIFIES: this
    // EFFECTS: displays the detailed information of an asset when the asset on the list is selected
    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList)e.getSource();
        int index = list.getSelectedIndex();
        if (index > -1) {
            Asset assetSelected = account.getAccount().get(index);
            updateInfoLabel(assetSelected);
        } else {
            infoLabel.setText("nothing is selected");
        }

    }

    // EFFECTS: renders the information label
    private void updateInfoLabel(Asset a) {
        a.refreshTotalGain();
        a.refreshStatus();

        String assetInfo = getInfoString(a);
        infoLabel.setText(assetInfo);
    }

    // EFFECTS: returns the information of an asset in String
    private String getInfoString(Asset a) {
        String assetInfo = "<html>\n"
                + "<font size=+2>" + a.getName() + "</font>\n"
                + "<ul>\n"
                + "<li><b>Current Total Gain: </b>" + "<i><font color=blue>" + a.getTotalGain() + "</font></i>\n"
                + "<li><b>Period-to-date Interest: </b>" + "<i><font color=blue>" + a.calculateFullReturn()
                + "</font></i>\n"
                + "<li><b>Days of Holding/Term to Maturity: </b>" + "<i><font color=blue>" + a.getDaysHeld()
                + "/" + a.getTermToMaturity() + "</font></i>\n"
                + "<li><b>Maturity Date: </b>" + "<i><font color=blue>" + a.calculateMaturityDate() + "</font></i>\n"
                + "<li><b>Principal: </b>" + "<i><font color=blue>" + a.getPrincipal() + "</font></i>\n"
                + "</ul>\n";
        return assetInfo;
    }
}
