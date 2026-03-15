package BTL_brick_breaker_game.src.model;

public class Level {

    private Brick[][] bricks;
    private int rows;
    private int cols;
    private int remainingBricks;

    public Level() {

        rows = 5;
        cols = 8;

        bricks = new Brick[rows][cols];

        initLevel();
    }

    private void initLevel() {

        remainingBricks = 0;

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){

                bricks[r][c] = new Brick(c * 80, r * 30);

                remainingBricks++;
            }
        }
    }

    public Brick[][] getBricks(){
        return bricks;
    }

    public void brickDestroyed(){
        remainingBricks--;
    }

    public boolean isLevelComplete(){
        return remainingBricks <= 0;
    }
}
