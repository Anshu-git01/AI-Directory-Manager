import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class AIDirectoryManager {
    public static void main(String[] args) {
        // Ensure Swing components are created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("AI Based Directory Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setMinimumSize(new Dimension(600, 450));
        frame.setLocationRelativeTo(null); // Center the window

        // Create a custom gradient background panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(240, 248, 255), // Light Blue
                    0, getHeight(), new Color(173, 216, 230) // Soft Blue
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title panel with icon
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
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createLineBorder(new Color(200, 200, 230), 1)
        ));

        // Select Folder Button with icon
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

        // Create bottom panel with folder management icon
        JPanel bottomPanel = createBottomPanel();

        // Assemble main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        titlePanel.setOpaque(false);

        // Title icon
        ImageIcon icon = createFolderManagementIcon();
        JLabel iconLabel = new JLabel(icon);

        // Title label
        JLabel titleLabel = new JLabel("AI Based Directory Manager", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112)); // Midnight Blue

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        return titlePanel;
    }

    private static JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setOpaque(false);

        // Folder management icon
        ImageIcon icon = createFileOrganizationIcon();
        JLabel iconLabel = new JLabel(icon);

        // Description label
        JLabel descLabel = new JLabel("Intelligent File Organization");
        descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        descLabel.setForeground(new Color(70, 70, 110));

        bottomPanel.add(iconLabel);
        bottomPanel.add(descLabel);
        return bottomPanel;
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
        logArea.setBackground(new Color(240, 248, 255)); // Very Light Blue
        logArea.setForeground(new Color(25, 25, 112)); // Midnight Blue
        logArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(176, 196, 222), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        return logArea;
    }

    private static JButton createSelectFolderButton(JFrame frame, JTextArea logArea) {
        JButton btn = new JButton("Select Folder") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(100, 149, 237, 200)); // Cornflower Blue when pressed
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(100, 149, 237, 150)); // Lighter when hovered
                } else {
                    g.setColor(new Color(100, 149, 237)); // Cornflower Blue
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add folder icon
        ImageIcon icon = createFolderSelectIcon();
        btn.setIcon(icon);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(10);

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

    // Icon Creation Methods (Fallback if no image resources)
    private static ImageIcon createFolderManagementIcon() {
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Clear background
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, 50, 50);
        
        // Draw folder icon
        g2d.setColor(new Color(100, 149, 237)); // Cornflower Blue
        g2d.fillRoundRect(10, 20, 30, 20, 5, 5);
        g2d.setColor(new Color(65, 105, 225)); // Royal Blue
        g2d.drawRoundRect(10, 20, 30, 20, 5, 5);
        
        // Draw folder top
        g2d.fillRoundRect(15, 15, 20, 8, 3, 3);
        
        g2d.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createFileOrganizationIcon() {
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Clear background
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, 40, 40);
        
        // Draw stacked files
        g2d.setColor(new Color(173, 216, 230)); // Light Blue
        g2d.fillRect(10, 15, 20, 15);
        g2d.setColor(new Color(135, 206, 250)); // Sky Blue
        g2d.fillRect(12, 13, 20, 15);
        g2d.setColor(new Color(100, 149, 237)); // Cornflower Blue
        g2d.fillRect(14, 11, 20, 15);
        
        // Draw border
        g2d.setColor(new Color(70, 130, 180)); // Steel Blue
        g2d.drawRect(10, 15, 20, 15);
        g2d.drawRect(12, 13, 20, 15);
        g2d.drawRect(14, 11, 20, 15);
        
        g2d.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createFolderSelectIcon() {
        BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Clear background
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, 24, 24);
        
        // Draw folder icon
        g2d.setColor(new Color(255, 255, 255, 180)); // Semi-transparent white
        g2d.fillRoundRect(2, 8, 20, 14, 3, 3);
        g2d.setColor(new Color(100, 149, 237)); // Cornflower Blue
        g2d.drawRoundRect(2, 8, 20, 14, 3, 3);
        
        // Draw folder top
        g2d.fillRoundRect(5, 5, 14, 5, 2, 2);
        
        g2d.dispose();
        return new ImageIcon(image);
    }

    // FileManagementModule (Nested Static Class)
    static class FileManagementModule {
        private final String rootPath;
        private final List<File> files = new ArrayList<>();
        private final JTextArea log;
        private final AIClassificationModule classifier;

        public FileManagementModule(String rootPath, JTextArea log) {
            this.rootPath = rootPath;
            this.log = log;
            this.classifier = new AIClassificationModule();
        }

        public void scan() {
            File rootDir = new File(rootPath);
            if (!rootDir.exists() || !rootDir.isDirectory()) {
                SwingUtilities.invokeLater(() -> log.append("Invalid directory: " + rootPath + "\n"));
                return;
            }
            scanDirectory(rootDir);
        }

        private void scanDirectory(File dir) {
            File[] fileList = dir.listFiles();
            if (fileList == null) return;

            for (File file : fileList) {
                if (file.isDirectory()) {
                    scanDirectory(file);
                } else {
                    if (!file.canRead()) {
                        SwingUtilities.invokeLater(() -> log.append("Cannot read: " + file.getName() + "\n"));
                        continue;
                    }
                    if (file.length() > 50 * 1024 * 1024) {
                        SwingUtilities.invokeLater(() -> log.append("Skipped large file: " + file.getName() + "\n"));
                        continue;
                    }
                    files.add(file);
                    String fileType = determineFileType(file);
                    SwingUtilities.invokeLater(() -> log.append(String.format("Found: %-40s | Type: %s\n", file.getName(), fileType)));
                }
            }
        }

        private String determineFileType(File file) {
            String ext = getFileExtension(file);
            if (isImageFile(ext)) return "Image";
            if (isVideoFile(ext)) return "Video";
            if (isMachineSetupFile(ext)) return "Machine Setup";
            return classifier.classify(file.getPath());
        }

        public boolean organize() {
            Map<String, String> categories = new HashMap<>();
            // Image files
            addCategory(categories, ".jpg", "Images");
            addCategory(categories, ".jpeg", "Images");
            addCategory(categories, ".png", "Images");
            addCategory(categories, ".gif", "Images");
            addCategory(categories, ".webp", "Images");
            addCategory(categories, ".bmp", "Images");

            // Video files
            addCategory(categories, ".mp4", "Videos");
            addCategory(categories, ".mkv", "Videos");
            addCategory(categories, ".avi", "Videos");
            addCategory(categories, ".mov", "Videos");
            addCategory(categories, ".wmv", "Videos");
            addCategory(categories, ".flv", "Videos");
            addCategory(categories, ".webm", "Videos");
            addCategory(categories, ".3gp", "Videos");
            addCategory(categories, ".mpg", "Videos");
            addCategory(categories, ".mpeg", "Videos");

            // Machine setup files
            addCategory(categories, ".exe", "Machine_Setup");
            addCategory(categories, ".msi", "Machine_Setup");
            addCategory(categories, ".bat", "Machine_Setup");
            addCategory(categories, ".cmd", "Machine_Setup");
            addCategory(categories, ".reg", "Machine_Setup");
            addCategory(categories, ".iso", "Machine_Setup");
            addCategory(categories, ".vmdk", "Machine_Setup");
            addCategory(categories, ".vdi", "Machine_Setup");
            addCategory(categories, ".img", "Machine_Setup");

            // Document files with subfolders
            addCategory(categories, ".txt", "Documents/Text");
            addCategory(categories, ".pdf", "Documents/PDF");
            addCategory(categories, ".ppt", "Documents/Presentations");
            addCategory(categories, ".pptx", "Documents/Presentations");
            addCategory(categories, ".doc", "Documents/Word");
            addCategory(categories, ".docx", "Documents/Word");

            // Code files
            addCategory(categories, ".java", "Code");
            addCategory(categories, ".cpp", "Code");
            addCategory(categories, ".c", "Code");
            addCategory(categories, ".html", "Code");
            addCategory(categories, ".py", "Code");
            addCategory(categories, ".js", "Code");
            addCategory(categories, ".env", "Code");

            boolean filesMoved = false;

            for (File file : files) {
                String ext = getFileExtension(file);
                if (categories.containsKey(ext)) {
                    filesMoved |= moveFile(file, categories.get(ext));
                }
            }

            return filesMoved;
        }

        private boolean moveFile(File file, String targetPathStr) {
            File targetDir = new File(rootPath, targetPathStr);

            // Check if the file is already in the correct folder or subfolder
            String currentDirPath = file.getParentFile().getAbsolutePath();
            String targetDirPath = targetDir.getAbsolutePath();
            if (currentDirPath.equals(targetDirPath)) {
                SwingUtilities.invokeLater(() -> log.append(String.format("Skipped: %-40s | Already in %s folder\n", file.getName(), targetPathStr)));
                return false;
            }

            // Create the target directory if it doesn't exist
            if (!targetDir.exists() && !targetDir.mkdirs()) {
                SwingUtilities.invokeLater(() -> log.append("Failed to create directory: " + targetDir.getAbsolutePath() + "\n"));
                return false;
            }

            // Move the file to the target directory
            try {
                Path targetPath = targetDir.toPath().resolve(file.getName());
                Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                SwingUtilities.invokeLater(() -> log.append(String.format("Moved: %-40s -> %s\n", file.getName(), targetDir.getAbsolutePath())));
                return true;
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> log.append("Failed to move file: " + file.getName() + "\n"));
                return false;
            }
        }

        private void addCategory(Map<String, String> categories, String ext, String category) {
            categories.put(ext, category);
        }

        private String getFileExtension(File file) {
            String name = file.getName();
            int lastIndex = name.lastIndexOf(".");
            if (lastIndex == -1 || lastIndex == name.length() - 1) return "Unknown";
            return name.substring(lastIndex).toLowerCase();
        }

        private boolean isImageFile(String ext) {
            return ext.matches("^\\.(jpg|jpeg|png|gif|webp|bmp)$");
        }

        private boolean isVideoFile(String ext) {
            return ext.matches("^\\.(mp4|mkv|avi|mov|wmv|flv|webm|3gp|mpg|mpeg)$");
        }

        private boolean isMachineSetupFile(String ext) {
            return ext.matches("^\\.(exe|msi|bat|cmd|reg|iso|vmdk|vdi|img)$");
        }
    }

    // AIClassificationModule (Nested Static Class)
    static class AIClassificationModule {
        public String classify(String filePath) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String firstLine = br.readLine();
                if (firstLine == null) return "Empty File";
                
                firstLine = firstLine.toLowerCase();
                
                // Check for programming language specific indicators
                if (firstLine.contains("import java")) return "Code (Java)";
                if (firstLine.contains("#include")) return "Code (C/C++)";
                if (firstLine.contains("def ")) return "Code (Python)";
                if (firstLine.contains("console.log")) return "Code (JavaScript)";
                if (firstLine.contains("<?php")) return "Code (PHP)";
                
                // Check for document-like content
                if (firstLine.contains("<!doctype html")) return "HTML Document";
                if (firstLine.contains("subject:")) return "Email";
                
                // Additional classification logic can be added here
                if (firstLine.contains("class")) return "Possible Structured Document";
            } catch (IOException e) {
                return "Unreadable File";
            }
            
            return "General Document";
        }
    }
}
