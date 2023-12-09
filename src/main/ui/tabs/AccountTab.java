package ui.tabs;

import model.Account;
import ui.tabs.components.HistogramPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Represents the account tab where user can see the account information and asset performance summary

public class AccountTab extends Tab implements ActionListener {
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 100;
    private JPanel refreshPanel;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private HistogramPanel histogramPanel;
    private JButton refreshButton;
    private JPanel summaryPane;
    private JLabel totalGain;
    private JLabel numAssets;
    private JLabel numActiveAssets;
    private String accountString = "My Account";
    private String gainString = "Total Gain: ";
    private String assetNumberString = "Asset(s): ";
    private String activeAssetNumberString = "Active Asset(s): ";

    // EFFECTS: creates the account tab with the refresh icon, the top panel, and the bottom panel
    public AccountTab(Account account) {
        super(account);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        account.refresh();

        initRefreshPanel();
        initTopPanel();
        initBottomPanel();

        placePanels();
    }

    // MODIFIES: this
    // EFFECTS: places panels: refresh panel on the very top, top panel in the medium, bottom panel on the bottom
    private void placePanels() {
        add(refreshPanel);
        add(topPanel);
        add(bottomPanel);
    }

    // MODIFIES: this
    // EFFECTS: sets up the refresh panel with the refresh button
    private void initRefreshPanel() {
        setUpRefreshButton();
        refreshPanel = new JPanel(new BorderLayout());
        refreshPanel.add(refreshButton, BorderLayout.LINE_END);
        refreshPanel.setSize(400, 15);
    }

    // MODIFIES: this
    // EFFECTS: sets up the refresh button
    private void setUpRefreshButton() {
        ImageIcon refreshIcon = new ImageIcon("data/images/refresh.png");
        Image image = refreshIcon.getImage();
        Image newImg = image.getScaledInstance(15, 15,  Image.SCALE_SMOOTH);
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
    // EFFECTS: sets up the top panel with the avatar panel on the left and summary panel on the right
    private void initTopPanel() {
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        topPanel.setSize(400, 200);

        JLabel avatar = getAvatar();
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel accountNamePane = getAccountNamePane();
        JPanel avatarPane = new JPanel();
        avatarPane.setLayout(new BorderLayout());
        avatarPane.add(avatar, BorderLayout.CENTER);
        avatarPane.add(accountNamePane, BorderLayout.PAGE_END);
        avatarPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        GridLayout layout = new GridLayout(3, 2);
        summaryPane = new JPanel(layout);
        setUpSummaryPanel();
        updateSummaryPanel();
        JPanel wrapperPane = new JPanel(new BorderLayout());
        wrapperPane.setSize(200, 200);
        wrapperPane.add(summaryPane, BorderLayout.LINE_END);
        wrapperPane.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 20));

        topPanel.add(avatarPane);
        topPanel.add(wrapperPane);
    }

    // MODIFIES: this
    // EFFECTS: sets up the summary panel
    private void setUpSummaryPanel() {
        totalGain = new JLabel();
        numAssets = new JLabel();
        numActiveAssets = new JLabel();

        summaryPane.add(new JLabel(gainString));
        summaryPane.add(totalGain);
        summaryPane.add(new JLabel(assetNumberString));
        summaryPane.add(numAssets);
        summaryPane.add(new JLabel(activeAssetNumberString));
        summaryPane.add(numActiveAssets);
    }

    // MODIFIES: this
    // EFFECTS: adds summary elements to the account summary panel
    private void updateSummaryPanel() {
        account.refresh();
        totalGain.setText(String.format("%.2f", account.getTotalGain()));
        numAssets.setText(String.valueOf(account.numAssets()));
        numActiveAssets.setText(String.valueOf(account.numActiveAssets()));
    }

    // EFFECTS: creates and returns the panel that contains the account name
    private JPanel getAccountNamePane() {
        JLabel accountName = new JLabel(accountString);
        accountName.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel accountNamePane = new JPanel(new BorderLayout());
        accountNamePane.add(accountName, BorderLayout.CENTER);
        accountNamePane.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
        return accountNamePane;
    }

    // EFFECTS: creates and returns the avatar
    private JLabel getAvatar() {
        String path = "data/images/avatar.png";
        try {
            BufferedImage avatar = ImageIO.read(new File(path));
            JLabel avatarLabel = new JLabel(new ImageIcon(avatar.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT,
                    Image.SCALE_FAST)));
            return avatarLabel;
        } catch (IOException e) {
            System.out.println("Can't read from: " + path);
        }
        return null;
    }

    // MODIFIES: this
    // EFFECTS: sets up the bottom panel with the bar graph
    private void initBottomPanel() {
        histogramPanel = new HistogramPanel(account);
        updateBar();
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        bottomPanel.add(histogramPanel);
    }

    // MODIFIES: this
    // EFFECTS: updates the bars
    private void updateBar() {
        account.refresh();
        histogramPanel.removeAssetBar();
        histogramPanel.addAssetBar();
    }

    // MODIFIES: this
    // EFFECTS: updates the summary panel and the bars when refresh button clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("refresh")) {
            updateSummaryPanel();
            updateBar();
        }
    }
}
