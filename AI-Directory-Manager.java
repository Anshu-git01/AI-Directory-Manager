import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

class GUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("File Manager");
        JButton btn = new JButton("Select Folder");

        btn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "Folder: " + chooser.getSelectedFile().getPath());
            }
        });

        frame.add(btn);
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class FileManager {
    private final String rootPath;
    private final List<File> files = new ArrayList<>();

    public FileManager(String rootPath) {
        this.rootPath = rootPath;
    }

    public void scan() {
        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.out.println("Invalid directory: " + rootPath);
            return;
        }
        scanDirectory(rootDir);
    }

    private void scanDirectory(File dir) {
        File[] fileList = dir.listFiles();
        if (fileList == null) return; // Handle null case

        for (File file : fileList) {
            if (file.isDirectory()) {
                scanDirectory(file);
            } else {
                files.add(file);
            }
        }
    }

    public void organize() {
        Map<String, String> categories = Map.of(
            ".jpg", "Images", ".png", "Images",
            ".pdf", "Documents", ".txt", "Documents",
            ".java", "Code"
        );

        for (File file : files) {
            String ext = getFileExtension(file);
            if (categories.containsKey(ext)) {
                File targetDir = new File(rootPath, categories.get(ext));
                if (!targetDir.exists()) targetDir.mkdirs();

                try {
                    Files.move(file.toPath(), Paths.get(targetDir.getAbsolutePath(), file.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println("Failed to move file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf(".");
        return (lastIndex == -1) ? "" : name.substring(lastIndex).toLowerCase();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java FileManager <directory_path>");
            return;
        }
        FileManager fm = new FileManager(args[0]);
        fm.scan();
        fm.organize();
    }
}





