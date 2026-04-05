package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import dao.PlayerDAO;

public class Menu extends JPanel {

    private Image bgImage;
    private PlayerDAO playerDAO;
    private GameFrame frame;

    private JLabel coinsLabel;
    private JLabel gemsLabel;
    private JButton levelBtn;
    private JButton shopBtn;
    private JButton rankBtn;
    private JButton startBtn;
    private JButton exitBtn;

    private int highestLevel = 1;

    public Menu(GameFrame frame) {
        this.frame = frame;
        this.playerDAO = new PlayerDAO();
        setLayout(null);
        setBackground(Color.BLACK);
        
        loadResources();

        Font titleFont = loadFont(30f);
        Font buttonFont = loadFont(15f);
        Font statsFont = loadFont(12f);
        
        // Cập nhật dữ liệu người chơi
        refreshPlayerData();
        
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

        // ===== RANKING BUTTON =====
        rankBtn = new JButton("RANKING");
        rankBtn.setFont(buttonFont);
        styleButton(rankBtn, Color.YELLOW);
        add(rankBtn);

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
        startBtn.addActionListener(e -> frame.showGameAtLevel(highestLevel - 1));
        levelBtn.addActionListener(e -> frame.showLevelSelect());
        shopBtn.addActionListener(e -> frame.showShop());
        rankBtn.addActionListener(e -> frame.showLeaderboard());
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
        int btnW = 250, btnH = 45, spacing = 10;
        int startY = h/2 - 120;
        startBtn.setBounds(centerX - btnW/2, startY, btnW, btnH);
        levelBtn.setBounds(centerX - btnW/2, startY + (btnH + spacing), btnW, btnH);
        shopBtn.setBounds(centerX - btnW/2, startY + (btnH + spacing) * 2, btnW, btnH);
        rankBtn.setBounds(centerX - btnW/2, startY + (btnH + spacing) * 3, btnW, btnH);
        exitBtn.setBounds(centerX - btnW/2, startY + (btnH + spacing) * 4, btnW, btnH);
    }

    public void refreshPlayerData() {
        if (frame.getCurrentPlayer() == null) return;
        int playerId = frame.getCurrentPlayer().getPlayerId();
        
        if (coinsLabel != null) coinsLabel.setText("COINS: ...");
        if (gemsLabel != null) gemsLabel.setText("GEMS: ...");
        if (levelBtn != null) levelBtn.setText("LEVEL: ...");

        new SwingWorker<int[], Void>() {
            private int highestLevel;
            @Override
            protected int[] doInBackground() throws Exception {
                highestLevel = playerDAO.getHighestLevel(playerId);
                return playerDAO.checkBalance(playerId);
            }

            @Override
            protected void done() {
                try {
                    int[] balance = get();
                    if (coinsLabel != null) coinsLabel.setText("COINS: " + balance[0]);
                    if (gemsLabel != null) gemsLabel.setText("GEMS: " + balance[1]);
                    if (levelBtn != null) levelBtn.setText("LEVEL: " + highestLevel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void loadResources() {
        try {
            File bgFile = new File("assets/menubg.png");
            if (bgFile.exists()) {
                bgImage = ImageIO.read(bgFile);
            } else {
                System.err.println("Cảnh báo: Không tìm thấy ảnh nền Menu tại " + bgFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
