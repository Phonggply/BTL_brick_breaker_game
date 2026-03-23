package BTL_brick_breaker_game.src.main;

import javax.swing.SwingUtilities;
import BTL_brick_breaker_game.src.view.GameFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}