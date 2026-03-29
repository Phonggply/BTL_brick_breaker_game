package main;

import javax.swing.SwingUtilities;
import view.GameFrame;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo Database SQLite trước khi mở giao diện
        dao.DatabaseInitializer.initialize();
        
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}
