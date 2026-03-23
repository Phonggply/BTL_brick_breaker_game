package BTL_brick_breaker_game.src.view;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class Menu extends JPanel {

    private GameFrame frame;

    public Menu(GameFrame frame) {
        this.frame = frame;

        setLayout(null);
        setBackground(Color.BLACK);

        // ===== LOAD FONT =====
        Font titleFont = loadFont(30f);
        Font buttonFont = loadFont(15f);

        // ===== TITLE =====
        JLabel title = new JLabel("BRICK BREAKER");
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        title.setBounds(150, 100, 500, 50);
        title.setHorizontalAlignment(JLabel.CENTER);
        add(title);

        // ===== START BUTTON =====
        JButton startBtn = new JButton("START");
        startBtn.setFont(buttonFont);
        startBtn.setBounds(300, 250, 200, 50);
        startBtn.addActionListener(e -> this.frame.showGame());
        styleButton(startBtn);
        add(startBtn);

        // ===== EXIT BUTTON =====
        JButton exitBtn = new JButton("EXIT");
        exitBtn.setFont(buttonFont);
        exitBtn.setBounds(300, 320, 200, 50);
        styleButton(exitBtn);
        add(exitBtn);

        // ===== ACTION =====
        startBtn.addActionListener(e -> frame.showGame());
        exitBtn.addActionListener(e -> System.exit(0));
    }

    // ===== LOAD FONT =====
    private Font loadFont(float size) {
        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("assets/PressStart2P-Regular.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.BOLD, (int) size);
        }
    }

    // ===== STYLE BUTTON =====
    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(Color.BLACK);
        btn.setForeground(Color.GREEN);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.YELLOW);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.GREEN);
            }
        });
    }
}
