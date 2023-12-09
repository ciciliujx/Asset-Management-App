package ui.tabs.components;

import model.Account;
import model.Asset;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;

// Citation: codes are adapted from https://stackoverflow.com/questions/29708147/custom-graph-java-swing

// Represents a histogram panel with a bar graph

public class HistogramPanel extends JPanel {
    private static String title = "Top 3 Assets";
    private int histogramHeight = 100;
    private int barWidth = 30;
    private int barGap = 7;

    private JPanel barPanel;
    private JPanel labelPanel;

    private List<Bar> bars = new ArrayList<>();

    private Account account;

    // EFFECTS: creates a histogram panel with bars and labels
    public HistogramPanel(Account account) {
        this.account = account;
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(10,10,10,10)));
        setLayout(new BorderLayout());

        barPanel = new JPanel(new GridLayout(1, 0, barGap, 0));
        barPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
        barPanel.setOpaque(true);
        barPanel.setBackground(new Color(190,206,228));

        labelPanel = new JPanel(new GridLayout(1, 0, barGap, 0));
        labelPanel.setBorder(new EmptyBorder(5, 10, 0, 10));

        add(barPanel, BorderLayout.CENTER);
        add(labelPanel, BorderLayout.PAGE_END);
    }

    // MODIFIES: this
    // EFFECTS: adds a bar to bars
    private void addHistogramColumn(String label, double value, Color color) {
        Bar bar = new Bar(label, value, color);
        bars.add(bar);
    }

    // MODIFIES: this
    // EFFECTS: lays out the histogram
    private void layoutHistogram() {
        barPanel.removeAll();
        labelPanel.removeAll();

        double maxValue = 0;

        for (Bar bar : bars) {
            maxValue = Math.max(maxValue, bar.getValue());
        }

        for (Bar bar : bars) {
            JLabel label = new JLabel(bar.getValue() + "");
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.TOP);
            label.setVerticalAlignment(JLabel.BOTTOM);
            double barHeight = (bar.getValue() * histogramHeight) / maxValue;
            Icon icon = new ColorIcon(bar.getColor(), barWidth, (int) barHeight);
            label.setIcon(icon);
            barPanel.add(label);

            JLabel barLabel = new JLabel(bar.getLabel());
            barLabel.setHorizontalAlignment(JLabel.CENTER);
            labelPanel.add(barLabel);
        }
    }

    // Represents a single bar
    private class Bar {
        private String label;
        private double value;
        private Color color;

        // EFFECTS: creates a bar with the given label, value, and color
        public Bar(String label, double value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public double getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }
    }

    // Represents a bar's color icon (rectangle)
    private class ColorIcon implements Icon {
        private int shadow = 3;

        private Color color;
        private int width;
        private int height;

        // EFFECTS: creates a color icon with the given color, width, and height
        public ColorIcon(Color color, int width, int height) {
            this.color = color;
            this.width = width;
            this.height = height;
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }

        // EFFECTS: creates a rectangle that represents the bar
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, width - shadow, height);
            g.setColor(Color.GRAY);
            g.fillRect(x + width - shadow, y + shadow, shadow, height - shadow);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds the top 3 assets to the bar graph with their names as labels and total gains as heights
    public void addAssetBar() {
        repaint();
        List<Asset> top3assets = account.top3Assets();
        List<Color> colors = Arrays.asList(new Color[]{Color.BLUE, Color.YELLOW, Color.RED});
        for (int i = 0; i < top3assets.size(); i++) {
            Asset next = top3assets.get(i);
            addHistogramColumn(next.getName(), next.getTotalGain(), colors.get(i));
        }
        layoutHistogram();
    }

    // MODIFIES: this
    // EFFECTS: removes all bars from the graph
    public void removeAssetBar() {
        bars.removeAll(bars);
    }
}