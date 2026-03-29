package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import dao.PlayerDAO;

public class Menu extends JPanel {

    private Image bgImage;
    private PlayerDAO playerDAO;
    private int currentPlayerId = 1; // Mặc định người chơi 1

    private JLabel coinsLabel;
    private JLabel gemsLabel;
    private JButton levelBtn;
    private JButton shopBtn;
    private JButton startBtn;
    private JButton exitBtn;

    public Menu(GameFrame frame) {
        this.playerDAO = new PlayerDAO();
        setLayout(null);
        setBackground(Color.BLACK);
        
        loadResources();

        Font titleFont = loadFont(30f);
        Font buttonFont = loadFont(15f);
        Font statsFont = loadFont(12f);

        // ===== TITLE =====
        JLabel title = new JLabel("BRICK BREAKER");
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(JLabel.CENTER);
        add(title);

        // ===== STATS (Coins & Gems) =====
        coinsLabel = new JLabel("COINS: 0");
        coinsLabel.setFont(statsFont);
        coinsLabel.setForeground(Color.YELLOW);
        add(coinsLabel);

        gemsLabel = new JLabel("GEMS: 0");
        gemsLabel.setFont(statsFont);
        gemsLabel.setForeground(Color.CYAN);
        add(gemsLabel);

        // ===== START BUTTON =====
        startBtn = new JButton("START GAME");
        startBtn.setFont(buttonFont);
        styleButton(startBtn, Color.GREEN);
        add(startBtn);

        // ===== LEVEL BUTTON =====
        levelBtn = new JButton("LEVEL: 1");
        levelBtn.setFont(buttonFont);
        styleButton(levelBtn, Color.ORANGE);
        add(levelBtn);

        // ===== SHOP BUTTON =====
        shopBtn = new JButton("SHOP");
        shopBtn.setFont(buttonFont);
        styleButton(shopBtn, Color.PINK);
        add(shopBtn);

        // ===== EXIT BUTTON =====
        exitBtn = new JButton("EXIT");
        exitBtn.setFont(buttonFont);
        styleButton(exitBtn, Color.RED);
        add(exitBtn);

        // Tự động căn chỉnh khi resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                layoutComponents();
            }
        });

        // ===== ACTIONS =====
        startBtn.addActionListener(e -> frame.showGame());
        levelBtn.addActionListener(e -> frame.showLevelSelect());
        shopBtn.addActionListener(e -> frame.showShop());
        exitBtn.addActionListener(e -> System.exit(0));
        
        // Cập nhật dữ liệu lần đầu
        refreshPlayerData();
    }

    private void layoutComponents() {
        int w = getWidth();
        int h = getHeight();
        int centerX = w / 2;

        JLabel title = (JLabel) getComponent(0);
        title.setBounds(centerX - 250, 60, 500, 50);

        // Stats ở góc trên bên phải
        coinsLabel.setBounds(w - 200, 20, 180, 30);
        gemsLabel.setBounds(w - 200, 50, 180, 30);

        // Các nút ở giữa
        int btnW = 250, btnH = 45, spacing = 15;
        startBtn.setBounds(centerX - btnW/2, h/2 - 100, btnW, btnH);
        levelBtn.setBounds(centerX - btnW/2, h/2 - 100 + (btnH + spacing), btnW, btnH);
        shopBtn.setBounds(centerX - btnW/2, h/2 - 100 + (btnH + spacing) * 2, btnW, btnH);
        exitBtn.setBounds(centerX - btnW/2, h/2 - 100 + (btnH + spacing) * 3, btnW, btnH);
    }

    public void refreshPlayerData() {
        int[] balance = playerDAO.checkBalance(currentPlayerId);
        int highestLevel = playerDAO.getHighestLevel(currentPlayerId);
        
        coinsLabel.setText("COINS: " + balance[0]);
        gemsLabel.setText("GEMS: " + balance[1]);
        levelBtn.setText("LEVEL: " + highestLevel);
    }

    private void loadResources() {
        try {
            File bgFile = new File("assets/menubg.png");
            if (bgFile.exists()) bgImage = ImageIO.read(bgFile);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh nền Menu!");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
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

    private void styleButton(JButton btn, Color hoverColor) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.WHITE);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(hoverColor);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.WHITE);
            }
        });
    }
}
