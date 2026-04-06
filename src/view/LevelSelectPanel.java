package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import model.LevelManager;

public class LevelSelectPanel extends JPanel {

    private LevelManager levelManager;

    public LevelSelectPanel(GameFrame frame) {
        this.levelManager = new LevelManager();
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Header
        JLabel header = new JLabel("SELECT LEVEL");
        header.setFont(loadFont(25f));
        header.setForeground(Color.WHITE);
        header.setHorizontalAlignment(JLabel.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(header, BorderLayout.NORTH);

        // Level Grid
        JPanel gridPanel = new JPanel(new GridLayout(3, 4, 20, 20));
        gridPanel.setBackground(Color.BLACK);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        add(gridPanel, BorderLayout.CENTER);

        // Lấy highest level từ bộ nhớ máy (Caching) thay vì DB để nhanh hơn
        int highestLevel = frame.getCurrentPlayer() != null ? frame.getCurrentPlayer().getHighestLevel() : 1;
        int totalLevels = levelManager.getLevelCount();

        for (int i = 1; i <= totalLevels; i++) {
            final int levelIdx = i;
            JButton btn = new JButton("LEVEL " + i);
            btn.setFont(loadFont(10f));
            btn.setFocusPainted(false);

            if (i <= highestLevel) {
                btn.setBackground(new Color(50, 150, 50));
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> frame.showGameAtLevel(levelIdx - 1));
            } else {
                btn.setBackground(Color.DARK_GRAY);
                btn.setForeground(Color.LIGHT_GRAY);
                btn.setText("LOCKED");
                btn.setEnabled(false);
            }
            gridPanel.add(btn);
        }
        
        // Back Button
        JButton backBtn = new JButton("BACK TO MENU");
        backBtn.setFont(loadFont(12f));
        backBtn.setBackground(Color.RED);
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> frame.showMenu());
        add(backBtn, BorderLayout.SOUTH);
    }

    private Font loadFont(float size) {
        try {
            File fontFile = new File("assets/PressStart2P-Regular.ttf");
            if (fontFile.exists()) {
                return Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(size);
            }
        } catch (Exception e) {}
        return new Font("Arial", Font.BOLD, (int) size);
    }
}
