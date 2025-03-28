import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class FileManagementModule {
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
