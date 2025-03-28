import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FileManagerGUI {
    public static void main(String[] args) {
        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the main frame
        JFrame frame = new JFrame("AI Based Directory Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setMinimumSize(new Dimension(500, 350));
        frame.setLocationRelativeTo(null); // Center the window

        // Create a custom panel with a background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title panel
        JPanel titlePanel = createTitlePanel();

        // Center panel for button and log area
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Log area
        JTextArea logArea = createLogArea();
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Select Folder Button
        JButton selectFolderBtn = createSelectFolderButton(frame, logArea);

        // Add components to center panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(selectFolderBtn, gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        centerPanel.add(scrollPane, gbc);

        // Assemble main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add main panel to frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);

        // Title label
        JLabel titleLabel = new JLabel("AI Based Directory Manager", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 152, 219)); // Blue color

        titlePanel.add(titleLabel);
        return titlePanel;
    }

    private static JTextArea createLogArea() {
        JTextArea logArea = new JTextArea(12, 40) {
            @Override
            public void append(String str) {
                super.append(str);
                setCaretPosition(getDocument().getLength());
            }
        };
        logArea.setFont(new Font("Fira Code", Font.PLAIN, 14));
        logArea.setEditable(false);
        logArea.setBackground(new Color(248, 249, 250));
        logArea.setForeground(new Color(45, 52, 54));
        logArea.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233), 2));
        
        return logArea;
    }

    private static JButton createSelectFolderButton(JFrame frame, JTextArea logArea) {
        JButton btn = new JButton("Select Folder");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setBackground(new Color(255, 87, 34)); // Bright orange background
        btn.setForeground(new Color(33, 33, 33)); // Dark gray text for better contrast
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String folderPath = chooser.getSelectedFile().getPath();
                SwingUtilities.invokeLater(() -> logArea.append("Selected Folder: " + folderPath + "\n\n"));

                new Thread(() -> {
                    FileManagementModule fm = new FileManagementModule(folderPath, logArea);
                    fm.scan();
                    SwingUtilities.invokeLater(() -> logArea.append("\nOrganizing files...\n"));
                    boolean filesMoved = fm.organize();
                    SwingUtilities.invokeLater(() -> {
                        if (filesMoved) {
                            logArea.append("Files organized successfully!\n");
                        } else {
                            logArea.append("Everything is already organized!\n");
                        }
                    });
                }).start();
            }
        });

        return btn;
    }
}
