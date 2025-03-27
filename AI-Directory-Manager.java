import javax.swing.*;
import java.awt.event.*;

public class GUI {
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
