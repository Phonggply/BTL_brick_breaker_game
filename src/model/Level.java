package model;

public class Level {

    private Brick[][] bricks;
    private int remainingBricks;
    private int[][] originalData;

    public Level(int[][] data) {
        this.originalData = data;
        initBricks(800); // Mặc định 800px, sẽ update sau
    }

    public void initBricks(int screenWidth) {
        int rows = originalData.length;
        int cols = originalData[0].length;
        bricks = new Brick[rows][cols];
        remainingBricks = 0;

        repositionBricks(screenWidth, true); // True là khởi tạo mới
    }

    public void repositionBricks(int screenWidth, boolean isInitial) {
        int rows = originalData.length;
        int cols = originalData[0].length;

        // Giảm kích thước gạch đi một nửa
        int brickWidth = (screenWidth / (cols + 2)) / 2; 
        if (brickWidth > 50) brickWidth = 50; // Giới hạn cũ là 100, nay là 50
        int brickHeight = 15; // Giảm từ 30 xuống 15
        
        int gridWidth = cols * brickWidth;
        int offsetX = (screenWidth - gridWidth) / 2;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int typeId = originalData[r][c];
                if (typeId > 0) {
                    int x = c * brickWidth + offsetX;
                    int y = r * brickHeight + 50;
                    
                    if (isInitial) {
                        Brick.BrickType type = getBrickTypeById(typeId);
                        // Khoảng cách giữa các viên gạch cũng nhỏ lại (giảm từ 2 xuống 1)
                        bricks[r][c] = new Brick(x, y, brickWidth - 1, brickHeight - 1, type);
                        if (type != Brick.BrickType.UNBREAKABLE) remainingBricks++;
                    } else if (bricks[r][c] != null) {
                        bricks[r][c].setPosition(x, y);
                        bricks[r][c].setSize(brickWidth - 1, brickHeight - 1);
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
