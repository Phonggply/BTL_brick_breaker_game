package BTL_brick_breaker_game.src.view;
import javax.swing.JFrame;

public class GameFrame extends JFrame {
    public GameFrame(){

        setTitle("Brick Breaker");

        setSize(800, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        add(new GamePanel());

        setVisible(true);
    }
}

