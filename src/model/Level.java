package model;

public class Level {

    private Brick[][] bricks;
    private int remainingBricks;
    private int[][] originalData;

    public Level(int[][] data) {
        this.originalData = data;
    }

    public void initBricks(int screenWidth) {
        if (screenWidth <= 0) return;
        int rows = originalData.length;
        int cols = originalData[0].length;
        bricks = new Brick[rows][cols];
        remainingBricks = 0;

        repositionBricks(screenWidth, true);
    }

    public void repositionBricks(int screenWidth, boolean isInitial) {
        int rows = originalData.length;
        int cols = originalData[0].length;

        // Giữ chiều dài tối đa để lấp đầy màn hình như lúc tăng 1.5 lần
        int brickWidth = (int) (screenWidth / (cols + 2)); 
        if (brickWidth > 100) brickWidth = 100; // Giới hạn tối đa 100px
        
        // Giữ chiều cao mỏng là 18px như bạn yêu cầu
        int brickHeight = 18; 
        
        int gridWidth = cols * brickWidth;
        int offsetX = (screenWidth - gridWidth) / 2;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int typeId = originalData[r][c];
                if (typeId > 0) {
                    int x = c * brickWidth + offsetX;
                    int y = r * brickHeight + 60;
                    
                    if (isInitial) {
                        Brick.BrickType type = getBrickTypeById(typeId);
                        // Giữ khoảng cách 2px để nhìn rõ từng viên gạch
                        bricks[r][c] = new Brick(x, y, brickWidth - 2, brickHeight - 2, type);
                        if (type != Brick.BrickType.UNBREAKABLE) {
                            remainingBricks++;
                        }
                    } else if (bricks[r][c] != null) {
                        bricks[r][c].setPosition(x, y);
                        bricks[r][c].setSize(brickWidth - 2, brickHeight - 2);
                    }
                }
            }
        }
    }

    private Brick.BrickType getBrickTypeById(int id) {
        switch (id) {
            case 1: return Brick.BrickType.NORMAL;
            case 2: return Brick.BrickType.STRONG_2;
            case 3: return Brick.BrickType.STRONG_3;
            case 9: return Brick.BrickType.UNBREAKABLE;
            default: return Brick.BrickType.NORMAL;
        }
    }

    public Brick[][] getBricks() { return bricks; }
    public void brickDestroyed() { remainingBricks--; }
    public boolean isLevelComplete() { return remainingBricks <= 0; }
}
