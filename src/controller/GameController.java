package BTL_brick_breaker_game.src.controller;

import BTL_brick_breaker_game.src.model.Ball;
import BTL_brick_breaker_game.src.model.Brick;
import BTL_brick_breaker_game.src.model.GameState;
import BTL_brick_breaker_game.src.model.Paddle;
import BTL_brick_breaker_game.src.model.Level;
import BTL_brick_breaker_game.src.model.LevelManager;

public class GameController {
    private Ball ball;
    private Paddle paddle;
    private Level level;
    private GameState gameState;
    private LevelManager levelManager;
    public GameController() {
        ball = new Ball(400, 300, 12, 3);
        paddle = new Paddle(350, 550, 100, 20);
        level = new Level();
        for(Brick[] row : Level.getBricks())
        levelManager = new LevelManager();
        gameState = GameState.START;
    }
    public void update() {
        ball.move();
        checkWallCollision();
        checkBrickCollision();
        checkPaddleCollision();
    }
    private void checkWallCollision() {

        if(ball.getX() <= 0 || ball.getX() >= 800){
            ball.reverseX();
        }

        if(ball.getY() <= 0){
            ball.reverseY();
        }
    }
    private void checkBrickCollision() {
        for(Brick[] row : Level.getBricks()){
            for(Brick b : row){
                if(!b.isDestroyed() && ball.getBounds().intersects(b.getBounds())){
                    b.destroy();
                    ball.reverseY();
                    level.brickDestroyed();
                    break;
                }
            }
        }
    }
    private void checkPaddleCollision(){
        if(ball.getBounds().intersects(paddle.getBounds())){
            ball.reverseY();
        }
    }
}
