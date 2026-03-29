package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Menu menuPanel;
    private GamePanel gamePanel;
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

        menuPanel = new Menu(this);
        mainPanel.add(menuPanel, "MENU");

        add(mainPanel);

        // Thêm phím tắt F11 để toggle FullScreen
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
        if (gamePanel != null) {
            gamePanel.requestFocusInWindow();
        }
    }

    public void showGame() {
        if (gamePanel != null) {
            gamePanel.stopGame();
            mainPanel.remove(gamePanel);
        }
        gamePanel = new GamePanel();
        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
        
        gamePanel.requestFocusInWindow();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showLevelSelect() {
        LevelSelectPanel levelPanel = new LevelSelectPanel(this);
        mainPanel.add(levelPanel, "LEVEL_SELECT");
        cardLayout.show(mainPanel, "LEVEL_SELECT");
    }

    public void showShop() {
        ShopPanel shopPanel = new ShopPanel(this);
        mainPanel.add(shopPanel, "SHOP");
        cardLayout.show(mainPanel, "SHOP");
    }

    public void showGameAtLevel(int levelIndex) {
        if (gamePanel != null) {
            gamePanel.stopGame();
            mainPanel.remove(gamePanel);
        }
        gamePanel = new GamePanel(levelIndex);
        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
        
        gamePanel.requestFocusInWindow();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showMenu() {
        if (menuPanel != null) {
            menuPanel.refreshPlayerData();
        }
        cardLayout.show(mainPanel, "MENU");
        menuPanel.requestFocusInWindow();
    }
}
