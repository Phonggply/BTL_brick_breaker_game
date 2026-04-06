package main;

import javax.swing.SwingUtilities;
import view.GameFrame;

public class Main {
    public static void main(String[] args) {
        dao.DatabaseInitializer.initialize();
        
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}
