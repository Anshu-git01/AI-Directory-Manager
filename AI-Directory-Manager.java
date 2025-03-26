import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
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
       
    }

    private String getFileExtension(File file) {
        
    }

    public static void main(String[] args) {
        
    }
}

