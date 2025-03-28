import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AIClassificationModule {
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
