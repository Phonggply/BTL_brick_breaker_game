package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import dao.PlayerDAO;
import model.LevelManager;

public class LevelSelectPanel extends JPanel {

    private PlayerDAO playerDAO;
    private LevelManager levelManager;
    private int currentPlayerId = 1;

    public LevelSelectPanel(GameFrame frame) {
        this.playerDAO = new PlayerDAO();
        this.levelManager = new LevelManager();
        this.currentPlayerId = frame.getCurrentPlayer() != null ? frame.getCurrentPlayer().getPlayerId() : 1;
        
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

        // Load Levels Asynchronously
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return playerDAO.getHighestLevel(currentPlayerId);
            }

            @Override
            protected void done() {
                try {
                    int highestLevel = get();
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
                    gridPanel.revalidate();
                    gridPanel.repaint();
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();

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
