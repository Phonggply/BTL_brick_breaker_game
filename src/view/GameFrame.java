package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import dao.entities.Player;

public class GameFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Menu menuPanel;
    private GamePanel gamePanel;
    private LoginPanel loginPanel;
    private Player currentPlayer;
    private boolean isFullScreen = false;
    private GraphicsDevice graphicsDevice;

    public GameFrame() {
        setTitle("Brick Breaker Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setSize(800, 600);
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
        setResizable(true);

        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "LOGIN");

        add(mainPanel);
        
        // Bắt đầu bằng màn hình đăng nhập
        cardLayout.show(mainPanel, "LOGIN");

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    toggleFullScreen();
                }
            }
        });

        setVisible(true);
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void showMenu() {
        if (menuPanel == null) {
            menuPanel = new Menu(this);
            mainPanel.add(menuPanel, "MENU");
        }
        menuPanel.refreshPlayerData();
        cardLayout.show(mainPanel, "MENU");
        menuPanel.requestFocusInWindow();
    }

    public void showGame() {
        if (gamePanel != null) {
            gamePanel.stopGame();
            mainPanel.remove(gamePanel);
        }
        gamePanel = new GamePanel(0, currentPlayer.getPlayerId());
        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
    }

    public void showGameAtLevel(int levelIndex) {
        if (gamePanel != null) {
            gamePanel.stopGame();
            mainPanel.remove(gamePanel);
        }
        gamePanel = new GamePanel(levelIndex, currentPlayer.getPlayerId());
        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
    }

    public void showShop() {
        ShopPanel shopPanel = new ShopPanel(this, currentPlayer.getPlayerId());
        mainPanel.add(shopPanel, "SHOP");
        cardLayout.show(mainPanel, "SHOP");
    }

    public void showLevelSelect() {
        LevelSelectPanel levelPanel = new LevelSelectPanel(this);
        mainPanel.add(levelPanel, "LEVEL_SELECT");
        cardLayout.show(mainPanel, "LEVEL_SELECT");
    }

    public void toggleFullScreen() {
        isFullScreen = !isFullScreen;
        dispose();
        
        if (isFullScreen) {
            setUndecorated(true);
            graphicsDevice.setFullScreenWindow(this);
        } else {
            setUndecorated(false);
            graphicsDevice.setFullScreenWindow(null);
            setSize(800, 600);
            setLocationRelativeTo(null);
        }
        
        setVisible(true);
        
        // Đảm bảo GamePanel hoặc LoginPanel lấy lại tiêu điểm bàn phím
        EventQueue.invokeLater(() -> {
            if (gamePanel != null && gamePanel.isShowing()) {
                gamePanel.requestFocusInWindow();
            } else if (loginPanel != null && loginPanel.isShowing()) {
                loginPanel.requestFocusInWindow();
            }
        });
    }
}
