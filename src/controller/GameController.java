package BTL_brick_breaker_game.src.controller;

import java.util.ArrayList;
import java.util.List;

import BTL_brick_breaker_game.src.model.Ball;
import BTL_brick_breaker_game.src.model.GameState;
import BTL_brick_breaker_game.src.model.Paddle;
import BTL_brick_breaker_game.src.model.Brick;
import BTL_brick_breaker_game.src.model.Level;
import BTL_brick_breaker_game.src.model.LevelManager;

public class GameController {
    private List<Ball> balls = new ArrayList<>();
    private Paddle paddle;
    private Level level;
    private GameState gameState;
    private LevelManager levelManager;
    private static final int Width = 800;
    private static final int Height = 600;
    public GameController() {
        Ball ball = new Ball(400, 300, 12, 3);
        balls.add(ball);
        paddle = new Paddle(350, 550, 100, 20, 6);
        level = new Level();
        levelManager = new LevelManager();
        level = levelManager.getCurrentLevel();
        gameState = GameState.START;
    }
    public void update() {
        for(Ball b : balls){
            b.move();
        }
        checkWallCollision();
        checkBrickCollision();
        checkPaddleCollision();
        checkBallOut();
        checkLevelComplete();
    }
    private void checkWallCollision() {

        for(Ball b : balls){

            if(b.getX() <= 0 || b.getX() >= Width){
                b.reverseX();
            }

            if(b.getY() <= 0){
                b.reverseY();
            }
        }
    }
    private void checkBrickCollision() {
        for(Ball b : balls){

            for(Brick[] row : level.getBricks()){
                for(Brick brick : row){

                    if(brick != null && !brick.isDestroyed() &&
                    b.getBounds().intersects(brick.getBounds())){

                        brick.destroy();
                        b.reverseY();
                        level.brickDestroyed();
                        break;
                    }
                }
            }

        }
    }
    private void checkPaddleCollision(){

        for(Ball b : balls){

            if(b.getBounds().intersects(paddle.getBounds())){

                b.setPosition(b.getX(), paddle.getY() - b.getSize());

            int paddleWidth = paddle.getWidth();
            int section = paddleWidth / 5;

            int ballCenter = b.getX() + b.getSize() / 2;
            int relativeX = ballCenter - paddle.getX();

            int region = relativeX / section;

            if (region < 0) region = 0;
            if (region > 4) region = 4;

            b.handlePaddleCollision(region);
            
            }
        }
    }
    private void checkBallOut(){

        for(Ball b : balls){
            if(b.getY() >= Height){

                if(paddle.hasShield()){
                    b.reverseY();
                    b.setPosition(b.getX(), Height - b.getSize());
                }
                else{
                    gameState = GameState.GAME_OVER;
                }
            }
        }
    }
    private void checkLevelComplete(){

        if(level.isLevelComplete()){

            levelManager.nextLevel();
            level = levelManager.getCurrentLevel();
            resetBall();
        }

    }
    public Ball getBall(){
        for(Ball b : balls){
            return b;
        }
        return null;
    }

    public Paddle getPaddle(){
        return paddle;
    }

    public Level getLevel(){
        return level;
    }

    public GameState getGameState(){
        return gameState;
    }
    private void resetBall(){
        for(Ball b : balls){
            b.setPosition(400, 300);
        }
    }
}
