/**
 * RewardsApp - Main GUI application for the Rewards Tracker system.
 * This class implements a tabbed interface for managing customer reward points
 * with functionality for adding, checking, and redeeming points.
 */
package reward_app;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class RewardsApp extends JFrame {
    // Color constants for UI theming
    private static final Color BG_COLOR = new Color(248, 249, 250);      // Light background
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);   // Main theme color
    private static final Color UNSELECTED_BG = new Color(233, 236, 239); // Inactive tab color
    private static final Color TAB_TEXT = new Color(108, 117, 125);      // Tab text color
    private static final Color INPUT_TEXT = new Color(73, 80, 87);       // Input field text
    private static final Color BORDER_COLOR = new Color(206, 212, 218);  // Border elements
    private static final Color WHITE = new Color(255, 255, 255);         // White elements

    // Font definitions for consistent typography
    private static final Font HEADER_FONT = new Font("Century Gothic", Font.BOLD, 32);
    private static final Font TAB_FONT = new Font("Brandon Grotesque", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("Montserrat", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Brandon Grotesque", Font.BOLD, 16);
    
    private RewardsManager manager;
    private JTabbedPane tabbedPane;

    public RewardsApp() {
        manager = new RewardsManager();
        setupUI();
    }

    /**
     * Initializes the main UI components and sets up the application window.
     * Configures the header panel, tabs, and main content area.
     */
    private void setupUI() {
        setTitle("Rewards Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Header Panel
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(PRIMARY_BLUE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TAB_TEXT));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel headerLabel = new JLabel("Rewards Tracker", SwingConstants.CENTER);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Tabbed Pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 30;
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                int totalWidth = tabbedPane.getWidth();
                return (totalWidth-3) / 3;
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, 
                    int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
                // Do nothing - removes the focus dots
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Extend width to overlap with adjacent tabs
                if (isSelected) {
                    g2d.setColor(PRIMARY_BLUE);
                    g2d.fillRect(x - 1, y, w + 2, h);
                } else {
                    g2d.setColor(UNSELECTED_BG);
                    g2d.fillRect(x - 1, y, w + 2, h);
                }
            }
        });

        tabbedPane.setFont(TAB_FONT);
        UIManager.put("TabbedPane.selectedForeground", WHITE);
        UIManager.put("TabbedPane.foreground", TAB_TEXT);
        tabbedPane.setBorder(null);
        
        tabbedPane.addTab("Add Points", createAddPanel());
        tabbedPane.addTab("Check Balance", createCheckPanel());
        tabbedPane.addTab("Redeem", createSubtractPanel());

        // Add tab change listener
        tabbedPane.addChangeListener(e -> {
            Component[] components = tabbedPane.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    clearPanel((JPanel) comp);
                }
            }
        });

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        getContentPane().setBackground(WHITE);
    }

    /**
     * Creates a styled panel with consistent padding and background.
     * Used as a base container for all tab content.
     * @return JPanel configured with standard styling
     */
    private JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));
        panel.setBackground(BG_COLOR);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    /**
     * Creates the "Add Points" tab panel with input fields for phone number
     * and purchase amount, plus an action button to add points.
     * @return JPanel containing the add points interface
     */
    private JPanel createAddPanel() {
        JPanel panel = createStyledPanel();

        JTextField phoneField = createStyledTextField("(705)-123-4567");
        JTextField amountField = createStyledTextField("13.95");
        JButton addButton = createStyledButton("Add Points");

        addComponentToPanel(panel, "Customer's Phone Number (10 digits)", phoneField);
        addComponentToPanel(panel, "Purchase Amount ($)", amountField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(addButton);

        addButton.addActionListener(e -> {
            try {
                manager.addPoints(phoneField.getText(), 
                    Double.parseDouble(amountField.getText()));
                JOptionPane.showMessageDialog(this, "Points added successfully!");
                phoneField.setText("");
                amountField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Creates the "Check Balance" tab panel with phone number input
     * and display area for showing current points balance.
     * @return JPanel containing the check balance interface
     */
    private JPanel createCheckPanel() {
        JPanel panel = createStyledPanel();

        JTextField phoneField = createStyledTextField("(705)-123-4567");
        JButton checkButton = createStyledButton("Check Points");
        JLabel resultLabel = createStyledResultLabel();

        addComponentToPanel(panel, "Customer's Phone Number (10 digits)", phoneField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(checkButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(resultLabel);

        checkButton.addActionListener(e -> {
            try {
                int points = manager.checkPoints(phoneField.getText());
                resultLabel.setText(points == 0 ? 
                    "New customer - No points yet" : 
                    "Current points: " + points);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Creates the "Redeem Points" tab panel with fields for phone number
     * and points to redeem, plus a button to process redemption.
     * @return JPanel containing the points redemption interface
     */
    private JPanel createSubtractPanel() {
        JPanel panel = createStyledPanel();

        JTextField phoneField = createStyledTextField("(705)-123-4567");
        JTextField pointsField = createStyledTextField("Larger than 0");
        JButton redeemButton = createStyledButton("Redeem Points");

        addComponentToPanel(panel, "Customer's Phone Number (10 digits)", phoneField);
        addComponentToPanel(panel, "Points to Redeem", pointsField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(redeemButton);

        redeemButton.addActionListener(e -> {
            try {
                int points = Integer.parseInt(pointsField.getText());
                if (points <= 0) {
                    throw new IllegalArgumentException("Points must be greater than 0");
                }
                manager.subtractPoints(phoneField.getText(), points);
                JOptionPane.showMessageDialog(this, "Points redeemed successfully!");
                phoneField.setText("");
                pointsField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Creates a styled text field with placeholder text that shows when empty.
     * @param placeholder Text to display when field is empty
     * @return JTextField with custom styling and placeholder functionality
     */
    private JTextField createStyledTextField(final String placeholder) {
        final JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Color.GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    int padding = getInsets().left;
                    g2.drawString(placeholder, padding, g2.getFontMetrics().getMaxAscent() + padding);
                }
            }
        };

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                field.repaint();
            }
            @Override
            public void focusLost(FocusEvent evt) {
                field.repaint();
            }
        });

        field.setMaximumSize(new Dimension(400, 40));
        field.setFont(INPUT_FONT);
        field.setBackground(WHITE);
        field.setForeground(INPUT_TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    /**
     * Creates a styled button with hover effects and consistent appearance.
     * @param text Button label text
     * @return JButton with custom styling and hover animations
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? PRIMARY_BLUE.darker() : PRIMARY_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent evt) {
                        isHovered = true;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        repaint();
                    }
                    public void mouseExited(MouseEvent evt) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
        };

        button.setFont(BUTTON_FONT);
        button.setForeground(WHITE);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 50));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        return button;
    }

    /**
     * Creates a styled label for displaying results with consistent formatting.
     * @return JLabel configured with result styling
     */
    private JLabel createStyledResultLabel() {
        JLabel label = new JLabel("");
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(INPUT_TEXT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Adds a component to a panel with a label and consistent spacing.
     * @param panel Target panel to add components to
     * @param labelText Text for the label above the component
     * @param component The component to add
     */
    private void addComponentToPanel(JPanel panel, String labelText, JComponent component) {
        JLabel label = new JLabel(labelText);
        label.setFont(INPUT_FONT);
        label.setForeground(INPUT_TEXT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(component);
        panel.add(Box.createVerticalStrut(15));
    }

    /**
     * Clears all input fields and results in a panel when switching tabs.
     * @param panel The panel to clear
     */
    private void clearPanel(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                field.setText("");
                field.setFocusable(false);
                field.setFocusable(true);
                field.repaint();
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (!label.getText().contains("Phone Number") && 
                    !label.getText().contains("Purchase Amount") && 
                    !label.getText().contains("Points to Redeem")) {
                    label.setText("");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RewardsApp().setVisible(true));
    }
}