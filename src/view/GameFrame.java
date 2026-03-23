package BTL_brick_breaker_game.src.view;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
    private Menu menuPanel;
    private GamePanel gamePanel;

    public GameFrame() {

        setTitle("Brick Breaker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // kích thước ban đầu
        setSize(1000, 700);

        // mở fullscreen dạng window (giống Chrome, VS Code)
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // cho phép resize
        setResizable(true);

        // căn giữa
        setLocationRelativeTo(null);
        menuPanel = new Menu(this);
        add(menuPanel);

        setVisible(true);
    }
    public void showGame() {

        remove(menuPanel);

        gamePanel = new GamePanel();
        add(gamePanel);

        revalidate();
        repaint();

        gamePanel.requestFocusInWindow();
    }
}