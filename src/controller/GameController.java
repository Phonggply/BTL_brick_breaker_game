package BTL_brick_breaker_game.src.controller;

import BTL_brick_breaker_game.src.model.Ball;
import BTL_brick_breaker_game.src.model.GameState;
import BTL_brick_breaker_game.src.model.Paddle;
import BTL_brick_breaker_game.src.model.Level;
import BTL_brick_breaker_game.src.model.Levelmanage;

public class GameController {
    private Ball ball;
    private Paddle paddle;
    private Level level;
    private GameState gameState;
    private Levelmanage levelManage;
    public GameController() {
        ball = new Ball();
        paddle = new Paddle();
        level = new Level();
        gameState = GameState.START;
    }
    public void update() {
        ball.move();
        checkWallCollision();
        checkBrickCollision();
    }
    private void checkWallCollision() {

        if(ball.getX() <= 0 || ball.getX() >= 800){
            ball.reverseX();
        }

        if(ball.getY() <= 0){
            ball.reverseY();
        }
    }
}
