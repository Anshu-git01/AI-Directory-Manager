import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Fjkldjsglkjdfklgager {
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
        
    }

    public static void main(String[] args) {
        
    }
}
