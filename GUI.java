import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.JTextComponent;

public class GUI extends JFrame
{
    private static final Theme LIGHT = new Theme(
            new Color(245, 247, 250),
            Color.WHITE,
            new Color(210, 216, 226),
            new Color(33, 37, 41),
            new Color(92, 102, 112),
            new Color(31, 98, 180),
            new Color(224, 234, 249),
            new Color(213, 150, 54),
            new Color(250, 239, 220),
            new Color(30, 150, 90),
            new Color(182, 44, 44)
    );

    private static final Theme DARK = new Theme(
            new Color(23, 26, 31),
            new Color(30, 34, 40),
            new Color(58, 65, 78),
            new Color(232, 236, 241),
            new Color(190, 200, 215),
            new Color(99, 141, 255),
            new Color(80, 104, 160),
            new Color(234, 182, 83),
            new Color(140, 105, 55),
            new Color(74, 191, 129),
            new Color(221, 99, 99)
    );

    private Theme theme = LIGHT;
    private boolean isDark = false;

    public JLabel rules;
    private final DefaultListModel<Rule> ruleListModel = new DefaultListModel<>();
    private final JList<Rule> ruleList = new JList<>(ruleListModel);
    private final JLabel statusLabel = new JLabel("Ready");
    private JPanel rootPanel;
    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private JPanel headerPanel;
    private JPanel centerPanel;
    private JPanel statusBarPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JScrollPane listScroller;
    private JToggleButton themeToggle;
    private JButton addRuleButton;
    private JButton deleteRuleButton;
    private JButton editRuleButton;
    private JButton runAllButton;
    private JButton sortOptionsButton;   // <-- add this
    private JButton exitButton;
    private JLabel hintLabel;
    private final List<AbstractButton> themedButtons = new ArrayList<>();
    private final HashMap<AbstractButton, Boolean> buttonPrimaries = new HashMap<>();

    public GUI()
    {
        super("Welcome to File Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(null);

        rootPanel = new BackgroundPanel();
        rootPanel.setLayout(new BorderLayout(15, 15));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        rootPanel.setBackground(theme.surface());

        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new MatteBorder(0, 0, 0, 1, theme.stroke()));
        sidebarPanel.setBackground(theme.card());
        sidebarPanel.setPreferredSize(new Dimension(230, 0));

        headerPanel = new GradientPanel();
        headerPanel.setLayout(new BorderLayout(10, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        headerPanel.setBackground(theme.card());
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, theme.stroke()));

        titleLabel = new JLabel("File Organizer");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        subtitleLabel = new JLabel("Create regex rules to keep your folders tidy.");
        subtitleLabel.setForeground(theme.textSubdued());
        titleLabel.setForeground(theme.textPrimary());

        JPanel titleWrap = new JPanel(new GridLayout(2, 1));
        titleWrap.setOpaque(false);
        titleWrap.add(titleLabel);
        titleWrap.add(subtitleLabel);

        themeToggle = new JToggleButton("Dark Mode");
        addRuleButton = new JButton("Add Rule");
        deleteRuleButton = new JButton("Delete Rule");
        editRuleButton = new JButton("Edit Rule");
        runAllButton = new JButton("Auto Sort");
        sortOptionsButton = new JButton("Sort Options");   // <-- new
        exitButton = new JButton("Exit");

        addRuleButton.setToolTipText("Create a new regex-based organizing rule");
        deleteRuleButton.setToolTipText("Remove an existing rule");
        editRuleButton.setToolTipText("Edit an existing rule");
        runAllButton.setToolTipText("Run all rules now (Auto Sort)");
        sortOptionsButton.setToolTipText("Open sort options window");   // << NEW
        themeToggle.setToolTipText("Toggle between light and dark themes");

        styleButton(addRuleButton, true);
        styleButton(deleteRuleButton, false);
        styleButton(editRuleButton, false);
        styleButton(runAllButton, true);
        styleButton(sortOptionsButton, false);   // << NEW
        styleButton(exitButton, false);
        styleButton(themeToggle, false);

        addRuleButton.addActionListener((ActionEvent e) -> addRuleWindow());
        deleteRuleButton.addActionListener((ActionEvent e) -> deleteRuleWindow());
        editRuleButton.addActionListener((ActionEvent e) -> editRuleWindow());
        runAllButton.addActionListener((ActionEvent e) -> {
            boolean success = Engine.sort();
            String message = success ? "Auto-sort completed." : "Auto-sort failed. Check your rules and log.";
            showStatus(message, !success);
            JOptionPane.showMessageDialog(this, message, success ? "Sort complete" : "Sort error",
                    success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });
        sortOptionsButton.addActionListener((ActionEvent e) -> openSortWindow());  // << ADD THIS
        exitButton.addActionListener((ActionEvent e) -> System.exit(0));

        headerPanel.add(titleWrap, BorderLayout.WEST);

        ruleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ruleList.setVisibleRowCount(8);
        ruleList.setCellRenderer(new RuleCellRenderer());
        ruleList.setBorder(new MatteBorder(1, 1, 1, 1, theme.stroke()));
        ruleList.setBackground(theme.card());
        ruleList.setSelectionBackground(theme.accentLight());
        ruleList.setSelectionForeground(theme.textPrimary());

        listScroller = new JScrollPane(ruleList);
        listScroller.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(theme.stroke()), "Rule Preview"));
        listScroller.getViewport().setBackground(theme.card());
        listScroller.setBackground(theme.card());

        rules = new JLabel("Rules (0)");
        rules.setFont(rules.getFont().deriveFont(Font.BOLD));
        rules.setForeground(theme.textPrimary());

        centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setBackground(theme.surface());
        centerPanel.add(rules, BorderLayout.NORTH);
        centerPanel.add(listScroller, BorderLayout.CENTER);

        statusBarPanel = new JPanel(new BorderLayout());
        statusBarPanel.setBorder(new MatteBorder(1, 0, 0, 0, theme.stroke()));
        statusBarPanel.setBackground(theme.card());
        statusLabel.setBorder(new EmptyBorder(6, 8, 6, 8));
        statusLabel.setForeground(theme.textSubdued());
        statusBarPanel.add(statusLabel, BorderLayout.WEST);

        mainContentPanel = new JPanel(new BorderLayout(12, 12));
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);
        mainContentPanel.add(statusBarPanel, BorderLayout.SOUTH);

        buildSidebar();

        rootPanel.add(sidebarPanel, BorderLayout.WEST);
        rootPanel.add(mainContentPanel, BorderLayout.CENTER);

        themeToggle.addActionListener((ActionEvent e) -> {
            isDark = !isDark;
            themeToggle.setText(isDark ? "Light Mode" : "Dark Mode");
            theme = isDark ? DARK : LIGHT;
            applyTheme();
        });

        setContentPane(rootPanel);
        syncRuleList();
    }

    private void updateRulesText() {
        LinkedList<Rule> temp = Engine.getRules();
        rules.setText("Rules (" + temp.size() + ")");
        ruleList.repaint();
    }

    private void deleteRuleWindow() {
        if (ruleListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no rules to delete yet.", "Nothing to delete",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Delete rule", true);
        dialog.setSize(520, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        textPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        textPanel.setBackground(theme.card());

        JLabel prompt = new JLabel("Select the rule you want to delete:");
        styleLabel(prompt);
        textPanel.add(prompt);
        JComboBox<String> ruleSelector = new JComboBox<>();
        styleCombo(ruleSelector);
        for (int i = 0; i < ruleListModel.size(); i++) {
            ruleSelector.addItem(formatRuleSummary(ruleListModel.get(i), i));
        }
        textPanel.add(ruleSelector);

        JButton deleteButton = new JButton("Delete Rule");
        styleButton(deleteButton, true);
        deleteButton.addActionListener((ActionEvent e) -> {
            int index = ruleSelector.getSelectedIndex();
            if (index >= 0) {
                Engine.deleteRule(index + 1);
                syncRuleList();
                dialog.dispose();
                showStatus("Deleted rule " + (index + 1), false);
            }
        });
        textPanel.add(deleteButton);

        dialog.add(textPanel);
        dialog.setVisible(true);
    }

    private void editRuleWindow() {
        if (ruleListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no rules to edit yet.", "Nothing to edit",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Edit rule", true);
        dialog.setSize(640, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel textPanel = new JPanel(new GridLayout(6, 2, 10, 12));
        textPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        textPanel.setBackground(theme.card());

        JLabel whichLabel = new JLabel("Select the rule to edit:");
        styleLabel(whichLabel);
        textPanel.add(whichLabel);
        JComboBox<String> ruleSelector = new JComboBox<>();
        styleCombo(ruleSelector);
        for (int i = 0; i < ruleListModel.size(); i++) {
            ruleSelector.addItem(formatRuleSummary(ruleListModel.get(i), i));
        }
        textPanel.add(ruleSelector);

        JLabel regexLabel = new JLabel("Regular expression:");
        styleLabel(regexLabel);
        textPanel.add(regexLabel);
        JTextField pattern = new JTextField(10);
        styleInput(pattern);
        textPanel.add(pattern);

        JLabel sourceLabel = new JLabel("Source folder:");
        styleLabel(sourceLabel);
        textPanel.add(sourceLabel);
        JTextField sourceFolder = new JTextField(10);
        styleInput(sourceFolder);
        JPanel sourceRow = new JPanel(new BorderLayout(6, 0));
        sourceRow.setOpaque(false);
        JButton sourceBrowse = new JButton("Browse");
        styleButton(sourceBrowse, false);
        sourceBrowse.addActionListener(e -> chooseDirectory(sourceFolder, dialog));
        sourceRow.add(sourceFolder, BorderLayout.CENTER);
        sourceRow.add(sourceBrowse, BorderLayout.EAST);
        textPanel.add(sourceRow);

        JLabel destLabel = new JLabel("Destination folder:");
        styleLabel(destLabel);
        textPanel.add(destLabel);
        JTextField destinationFolder = new JTextField(10);
        styleInput(destinationFolder);
        JPanel destRow = new JPanel(new BorderLayout(6, 0));
        destRow.setOpaque(false);
        JButton destBrowse = new JButton("Browse");
        styleButton(destBrowse, false);
        destBrowse.addActionListener(e -> chooseDirectory(destinationFolder, dialog));
        destRow.add(destinationFolder, BorderLayout.CENTER);
        destRow.add(destBrowse, BorderLayout.EAST);
        textPanel.add(destRow);

        Runnable populate = () -> {
            int idx = ruleSelector.getSelectedIndex();
            if (idx >= 0) {
                Rule r = ruleListModel.get(idx);
                pattern.setText(r.getPattern().pattern());
                sourceFolder.setText(r.getOrigin().getAbsolutePath());
                destinationFolder.setText(r.getDestination().getAbsolutePath());
            }
        };
        populate.run();
        ruleSelector.addActionListener(e -> populate.run());

        JButton saveButton = new JButton("Save");
        styleButton(saveButton, true);
        saveButton.addActionListener((ActionEvent e) -> {
            int idx = ruleSelector.getSelectedIndex();
            if (idx < 0) {
                showStatus("Select a rule to edit.", true);
                return;
            }
            String patternText = pattern.getText().trim();
            String sourceText = sourceFolder.getText().trim();
            String destinationText = destinationFolder.getText().trim();

            if (patternText.isEmpty() || sourceText.isEmpty() || destinationText.isEmpty()) {
                showStatus("All fields are required.", true);
                JOptionPane.showMessageDialog(dialog, "Please fill out all fields.", "Missing info",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Pattern compiledPattern;
            try {
                compiledPattern = Pattern.compile(patternText);
            } catch (PatternSyntaxException ex) {
                showStatus("Invalid regular expression.", true);
                JOptionPane.showMessageDialog(dialog, "Invalid regular expression: " + ex.getMessage(), "Regex error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            File src = new File(sourceText);
            File dest = new File(destinationText);
            if (!src.exists() || !src.isDirectory()) {
                showStatus("Source folder must exist.", true);
                JOptionPane.showMessageDialog(dialog, "Source folder does not exist.", "Folder error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!dest.exists() || !dest.isDirectory()) {
                showStatus("Destination folder must exist.", true);
                JOptionPane.showMessageDialog(dialog, "Destination folder does not exist.", "Folder error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Engine.editRule(idx + 1, new Rule(src, dest, compiledPattern));
            syncRuleList();
            showStatus("Updated rule " + (idx + 1), false);
            dialog.dispose();
        });
        textPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, false);
        cancelButton.addActionListener((ActionEvent e) -> dialog.dispose());
        textPanel.add(cancelButton);

        dialog.add(textPanel);
        dialog.setVisible(true);
    }
    private void addRuleWindow() {
        JDialog dialog = new JDialog(this, "Add rule", true);
        dialog.setSize(520, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel textPanel = new JPanel(new GridLayout(5, 2, 10, 12));
        textPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        textPanel.setBackground(theme.card());

        JLabel regexLabel = new JLabel("Enter a regular expression:");
        styleLabel(regexLabel);
        textPanel.add(regexLabel);
        JTextField pattern = new JTextField(10);
        styleInput(pattern);
        textPanel.add(pattern);

        JLabel sourceLabel = new JLabel("Source folder:");
        styleLabel(sourceLabel);
        textPanel.add(sourceLabel);
        JTextField sourceFolder = new JTextField(10);
        styleInput(sourceFolder);
        JPanel sourceRow = new JPanel(new BorderLayout(6, 0));
        sourceRow.setOpaque(false);
        JButton sourceBrowse = new JButton("Browse");
        styleButton(sourceBrowse, false);
        sourceBrowse.addActionListener(e -> chooseDirectory(sourceFolder, dialog));
        sourceRow.add(sourceFolder, BorderLayout.CENTER);
        sourceRow.add(sourceBrowse, BorderLayout.EAST);
        textPanel.add(sourceRow);

        JLabel destLabel = new JLabel("Destination folder:");
        styleLabel(destLabel);
        textPanel.add(destLabel);
        JTextField destinationFolder = new JTextField(10);
        styleInput(destinationFolder);
        JPanel destRow = new JPanel(new BorderLayout(6, 0));
        destRow.setOpaque(false);
        JButton destBrowse = new JButton("Browse");
        styleButton(destBrowse, false);
        destBrowse.addActionListener(e -> chooseDirectory(destinationFolder, dialog));
        destRow.add(destinationFolder, BorderLayout.CENTER);
        destRow.add(destBrowse, BorderLayout.EAST);
        textPanel.add(destRow);

        JLabel helper = new JLabel("Tip: use regex like \".*\\.pdf\" to move all PDFs.");
        helper.setForeground(theme.textSubdued());
        textPanel.add(helper);
        textPanel.add(new JLabel()); // spacer

        JButton addButton = new JButton("Add Rule");
        styleButton(addButton, true);
        addButton.addActionListener((ActionEvent e) -> {
            String patternText = pattern.getText().trim();
            String sourceText = sourceFolder.getText().trim();
            String destinationText = destinationFolder.getText().trim();

            if (patternText.isEmpty() || sourceText.isEmpty() || destinationText.isEmpty()) {
                showStatus("All fields are required.", true);
                JOptionPane.showMessageDialog(dialog, "Please fill out all fields.", "Missing info",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Pattern compiledPattern;
            try {
                compiledPattern = Pattern.compile(patternText);
            } catch (PatternSyntaxException ex) {
                showStatus("Invalid regular expression.", true);
                JOptionPane.showMessageDialog(dialog, "Invalid regular expression: " + ex.getMessage(), "Regex error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            File src = new File(sourceText);
            File dest = new File(destinationText);
            if (!src.exists() || !src.isDirectory()) {
                showStatus("Source folder must exist.", true);
                JOptionPane.showMessageDialog(dialog, "Source folder does not exist.", "Folder error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!dest.exists() || !dest.isDirectory()) {
                showStatus("Destination folder must exist.", true);
                JOptionPane.showMessageDialog(dialog, "Destination folder does not exist.", "Folder error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Engine.addRule(new Rule(src, dest, compiledPattern));
            syncRuleList();
            pattern.setText("");
            sourceFolder.setText("");
            destinationFolder.setText("");
            showStatus("Added rule for pattern \"" + patternText + "\"", false);
            dialog.dispose();
        });
        textPanel.add(addButton);

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, false);
        cancelButton.addActionListener((ActionEvent e) -> dialog.dispose());
        textPanel.add(cancelButton);

        dialog.add(textPanel);
        dialog.setVisible(true);
    }

    private void openSortWindow() {
        JFrame sortFrame = new JFrame("Sort Options");
        sortFrame.setSize(500, 100);
        sortFrame.setLocationRelativeTo(this);
        sortFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel sortPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        sortPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton alphabeticalButton = new JButton("Alphabetical Order");
        JButton dateButton = new JButton("By Date");
        JButton exitButton = new JButton("Exit Window");

        alphabeticalButton.addActionListener((ActionEvent e) ->
                JOptionPane.showMessageDialog(sortFrame, "Sorting alphabetically...", "Action",
                        JOptionPane.INFORMATION_MESSAGE));

        dateButton.addActionListener((ActionEvent e) ->
                JOptionPane.showMessageDialog(sortFrame, "Sorting by date...", "Action",
                        JOptionPane.INFORMATION_MESSAGE));

        exitButton.addActionListener((ActionEvent e) -> sortFrame.dispose());

        sortPanel.add(alphabeticalButton);
        sortPanel.add(dateButton);
        sortPanel.add(exitButton);

        sortFrame.setContentPane(sortPanel);
        sortFrame.setVisible(true);
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        EventQueue.invokeLater(() -> new GUI().setVisible(true));
    }

    private void syncRuleList() {
        ruleListModel.clear();
        for (Rule rule : Engine.getRules()) {
            ruleListModel.addElement(rule);
        }
        updateRulesText();
    }

    private void buildSidebar() {
        sidebarPanel.removeAll();
        JLabel navLabel = new JLabel("Actions");
        styleLabel(navLabel);
        navLabel.setFont(navLabel.getFont().deriveFont(Font.BOLD, 16f));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(navLabel);
        sidebarPanel.add(Box.createVerticalStrut(10));

        hintLabel = new JLabel("Hint: type /* to add a rule");
        styleLabel(hintLabel);
        hintLabel.setFont(hintLabel.getFont().deriveFont(11f));
        hintLabel.setForeground(theme.textSubdued());
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(hintLabel);

        addRuleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        editRuleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteRuleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        runAllButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sortOptionsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        themeToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        exitButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebarPanel.add(addRuleButton);
        sidebarPanel.add(Box.createVerticalStrut(6));
        sidebarPanel.add(editRuleButton);
        sidebarPanel.add(Box.createVerticalStrut(6));
        sidebarPanel.add(deleteRuleButton);
        sidebarPanel.add(Box.createVerticalStrut(6));
        sidebarPanel.add(runAllButton);
        sidebarPanel.add(Box.createVerticalStrut(6));               // << NEW
        sidebarPanel.add(sortOptionsButton);

        sidebarPanel.add(Box.createVerticalStrut(12));
        JSeparator sep = new JSeparator();
        sep.setForeground(theme.stroke());
        sep.setBackground(theme.stroke());
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebarPanel.add(sep);
        sidebarPanel.add(Box.createVerticalStrut(12));

        JLabel themeLabel = new JLabel("Appearance");
        styleLabel(themeLabel);
        themeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(themeLabel);
        sidebarPanel.add(Box.createVerticalStrut(6));
        sidebarPanel.add(themeToggle);

        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(exitButton);
    }

    private void applyTheme() {
        rootPanel.setBackground(theme.surface());
        headerPanel.setBackground(theme.card());
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, theme.stroke()));
        centerPanel.setBackground(theme.surface());
        statusBarPanel.setBackground(theme.card());
        statusBarPanel.setBorder(new MatteBorder(1, 0, 0, 0, theme.stroke()));
        statusLabel.setForeground(theme.textSubdued());
        titleLabel.setForeground(theme.textPrimary());
        subtitleLabel.setForeground(theme.textSubdued());
        rules.setForeground(theme.textPrimary());
        sidebarPanel.setBackground(theme.card());
        sidebarPanel.setBorder(new MatteBorder(0, 0, 0, 1, theme.stroke()));

        ruleList.setBackground(theme.card());
        ruleList.setBorder(new MatteBorder(1, 1, 1, 1, theme.stroke()));
        listScroller.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(theme.stroke()), "Rule Preview"));
        listScroller.getViewport().setBackground(theme.card());
        listScroller.setBackground(theme.card());
        ruleList.setSelectionBackground(theme.accentLight());
        ruleList.setSelectionForeground(theme.textPrimary());

        for (AbstractButton b : themedButtons) {
            Boolean primary = buttonPrimaries.getOrDefault(b, false);
            styleButton(b, primary);
        }

        repaint();
        revalidate();
    }

    private String formatRuleSummary(Rule rule, int index) {
        return "Rule " + (index + 1) + ": " + rule.getPattern() + " (" + rule.getOrigin().getName() + " -> " + rule.getDestination().getName() + ")";
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setForeground(isError ? theme.error() : theme.success());
        statusLabel.setText(message);
    }

    private void styleButton(AbstractButton button, boolean primary) {
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setFont(button.getFont().deriveFont(Font.BOLD, button.getFont().getSize2D()));
        button.setBorder(new RoundedBorder(12, theme.stroke()));
        button.setMargin(new Insets(8, 14, 8, 14));
        button.setUI(new RoundedButtonUI(primary));

        Color baseBg = primary ? theme.accent() : lighten(theme.card(), isDark ? 24 : 12);
        Color baseFg = primary ? Color.WHITE : theme.textPrimary();
        Color hoverBg = primary ? lighten(theme.accent(), 18) : lighten(theme.accentLight(), isDark ? 18 : 10);
        Color hoverFg = primary ? Color.WHITE : theme.accent();

        button.setBackground(baseBg);
        button.setForeground(baseFg);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBg);
                button.setForeground(hoverFg);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(baseBg);
                button.setForeground(baseFg);
            }
        });

        buttonPrimaries.put(button, primary);
        if (!themedButtons.contains(button)) {
            themedButtons.add(button);
        }
    }

    private void styleLabel(JLabel label) {
        label.setForeground(theme.textPrimary());
    }

    private void styleInput(JTextComponent field) {
        field.setBackground(theme.card());
        field.setForeground(theme.textPrimary());
        field.setCaretColor(theme.accent());
        field.setBorder(new MatteBorder(1, 1, 1, 1, theme.stroke()));
    }

    private void styleCombo(JComboBox<?> comboBox) {
        comboBox.setBackground(theme.card());
        comboBox.setForeground(theme.textPrimary());
        comboBox.setBorder(new MatteBorder(1, 1, 1, 1, theme.stroke()));
    }

    private class RuleCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Rule rule) {
                String fromBg = rgba(theme.accentLight(), isDark ? 0.45f : 0.8f);
                String toBg = rgba(theme.secondaryLight(), isDark ? 0.45f : 0.8f);
                String text = "<html><span style='color:" + toHex(theme.textPrimary()) + ";'><b>Rule " + (index + 1) + ":</b> Move <code style='color:"
                        + toHex(theme.accent()) + ";'>" + rule.getPattern() +
                        "</code><br><span style='color:" + toHex(theme.textSubdued()) + ";'>From: <span style='background-color:" + fromBg + "; color:"
                        + toHex(theme.textPrimary()) + "; padding:2px 4px; border-radius:4px;'>"
                        + rule.getOrigin().getAbsolutePath() +
                        "</span><br>To: <span style='background-color:" + toBg + "; color:" + toHex(theme.textPrimary()) + "; padding:2px 4px; border-radius:4px;'>"
                        + rule.getDestination().getAbsolutePath() + "</span></span></span></html>";
                label.setText(text);
                label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                label.setBackground(isSelected ? list.getSelectionBackground() : theme.card());
                label.setForeground(isSelected ? list.getSelectionForeground() : theme.textPrimary());
            }
            return label;
        }
    }

    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int width = getWidth();
            int height = getHeight();
            GradientPaint paint = new GradientPaint(0, 0, theme.accentLight(), width, height, theme.secondaryLight());
            g2.setPaint(paint);
            g2.fillRect(0, 0, width, height);
            g2.dispose();
        }
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color overlay = new Color(theme.stroke().getRed(), theme.stroke().getGreen(), theme.stroke().getBlue(), 30);
            g2.setColor(overlay);
            g2.fillOval(-w / 3, h / 3, w / 2, h / 2);
            g2.fillOval(w / 2, -h / 4, w / 2, h / 2);

            g2.dispose();
        }
    }

    private record Theme(Color surface, Color card, Color stroke, Color textPrimary, Color textSubdued,
                         Color accent, Color accentLight, Color secondary, Color secondaryLight,
                         Color success, Color error) {
    }

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static String rgba(Color color, float alpha) {
        float a = Math.max(0f, Math.min(1f, alpha));
        return "rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + a + ")";
    }

    private void chooseDirectory(JTextField targetField, Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select folder");
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
            targetField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private Color lighten(Color color, int amount) {
        int r = Math.min(255, color.getRed() + amount);
        int g = Math.min(255, color.getGreen() + amount);
        int b = Math.min(255, color.getBlue() + amount);
        return new Color(r, g, b, color.getAlpha());
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int pad = Math.max(1, radius / 3);
            return new Insets(pad, pad, pad, pad);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            int pad = Math.max(1, radius / 3);
            insets.left = insets.right = insets.top = insets.bottom = pad;
            return insets;
        }
    }

    private class RoundedButtonUI extends BasicButtonUI {
        private final boolean primary;

        RoundedButtonUI(boolean primary) {
            this.primary = primary;
        }

        @Override
public void paint(Graphics g, JComponent c) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int w = c.getWidth();
    int h = c.getHeight();

    Color base = primary ? theme.accent() : lighten(theme.card(), isDark ? 24 : 12);
    Color base2 = primary ? lighten(theme.accent(), 20) : lighten(theme.card(), isDark ? 40 : 20);
    Color border = lighten(theme.stroke(), isDark ? 10 : 0);

    GradientPaint gp = new GradientPaint(0, 0, base, 0, h, base2);
    g2.setPaint(gp);
    g2.fillRoundRect(0, 0, w, h, 16, 16);

    g2.setColor(border);
    g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

    g2.dispose();
    super.paint(g, c);
}
    }
}