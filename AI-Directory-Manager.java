import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class FileManagerGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("File Manager");
        JButton btn = new JButton("Select Folder");
        JTextArea logArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(logArea);
        logArea.setEditable(false);

        btn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String folderPath = chooser.getSelectedFile().getPath();
                SwingUtilities.invokeLater(() -> logArea.append("Selected Folder: " + folderPath + "\n"));
                
                new Thread(() -> {
                    FileManager fm = new FileManager(folderPath, logArea);
                    fm.scan();
                    fm.organize();
                }).start();
            }
        });

        frame.setLayout(new FlowLayout());
        frame.add(btn);
        frame.add(scrollPane);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class FileManager {
    private final String rootPath;
    private final List<File> files = new ArrayList<>();
    private final JTextArea log;

    public FileManager(String rootPath, JTextArea log) {
        this.rootPath = rootPath;
        this.log = log;
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
        
        AIClassifier classifier = new AIClassifier();
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
                SwingUtilities.invokeLater(() -> log.append("Found: " + file.getName() + " - " + classifier.classify(file.getPath()) + "\n"));
            }
        }
    }

    public void organize() {
        Map<String, String> categories = new HashMap<>();
        categories.put(".jpg", "Images");
        categories.put(".png", "Images");
        categories.put(".pdf", "Documents");
        categories.put(".txt", "Documents");
        categories.put(".java", "Code");
        categories.put(".cpp", "Code");
        
        for (File file : files) {
            String ext = getFileExtension(file);
            if (categories.containsKey(ext)) {
                File targetDir = new File(rootPath, categories.get(ext));
                if (!targetDir.exists() && !targetDir.mkdirs()) {
                    SwingUtilities.invokeLater(() -> log.append("Failed to create directory: " + targetDir.getAbsolutePath() + "\n"));
                    continue;
                }
                try {
                    Path targetPath = targetDir.toPath().resolve(file.getName());
                    Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    SwingUtilities.invokeLater(() -> log.append("Moved: " + file.getName() + " -> " + targetDir.getAbsolutePath() + "\n"));
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> log.append("Failed to move file: " + file.getName() + "\n"));
                }
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf(".");
        if (lastIndex == -1 || lastIndex == name.length() - 1) return "Unknown";
        return name.substring(lastIndex).toLowerCase();
    }
}
class AIClassifier {
    public String classify(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String firstLine = br.readLine();
            if (firstLine == null) return "Empty File";
            firstLine = firstLine.toLowerCase();
            if (firstLine.contains("import")) return "Code (Java)";
            if (firstLine.contains("#include")) return "Code (C++)";
            if (firstLine.contains("class")) return "Possible Java Code";
        } catch (IOException e) {
            return "Unknown File Type";
        }
        return "General Document";
    }
}