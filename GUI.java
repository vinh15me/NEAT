import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GUI extends JFrame
{
    public GUI()
    {
        super("Welcome to File Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 150);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton b1 = new JButton("Add organizing rule");
        JButton b2 = new JButton("Delete organizing rule");
        JButton b3 = new JButton("Edit Rule");
        JButton b4 = new JButton("Auto Sort");
        JButton b5 = new JButton("Exit Program");

        // Button 1: show message
        b1.addActionListener((ActionEvent e) -> addRuleWindow()); // openSortWindow());
        // Button 2: counter
        b2.addActionListener((ActionEvent e) ->
                JOptionPane.showMessageDialog(this, "Which rules do you want to delete?", "Deleting Rules",
                        JOptionPane.INFORMATION_MESSAGE));

        // Button 3: toggle color
        b3.addActionListener((ActionEvent e) ->
                JOptionPane.showMessageDialog(this, "Which rules do you want to edit?", "Edit Rules",
                        JOptionPane.INFORMATION_MESSAGE));

        // Button 4: open file chooser
        b4.addActionListener((ActionEvent e) -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this,
                        "You chose:\n" + chooser.getSelectedFile().getAbsolutePath(),
                        "File Selected", JOptionPane.PLAIN_MESSAGE);
            }
        });

        // Button 5: exit
        b5.addActionListener((ActionEvent e) -> System.exit(0));

        panel.add(b1);
        panel.add(b2);
        panel.add(b3);
        panel.add(b4);
        panel.add(b5);

        setContentPane(panel);
    }

    private void addRuleWindow() {
        JFrame frame = new JFrame("Add rule");
        frame.setSize(500, 200);
        frame.setLocationRelativeTo(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel textPanel = new JPanel(new GridLayout(3, 2, 0, 10));
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        textPanel.add(new JLabel("Enter a regular expression:"));
        textPanel.add(new JTextField(30));

        textPanel.add(new JLabel("Source folder:"));
        textPanel.add(new JTextField(30));

        textPanel.add(new JLabel("Destination folder:"));
        textPanel.add(new JTextField(30));

        frame.add(textPanel);
        frame.setVisible(true);
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
}