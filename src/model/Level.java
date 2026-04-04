package model;

public class Level {

    private Brick[][] bricks;
    private int remainingBricks;
    private int[][] originalData;

    public Level(int[][] data) {
        this.originalData = data;
    }

    public void initBricks(int screenWidth, int screenHeight) {
        if (screenWidth <= 0 || screenHeight <= 0) return;
        int rows = originalData.length;
        int cols = originalData[0].length;
        bricks = new Brick[rows][cols];
        remainingBricks = 0;

        repositionBricks(screenWidth, screenHeight, true);
    }

    public void repositionBricks(int screenWidth, int screenHeight, boolean isInitial) {
        int rows = originalData.length;
        int cols = originalData[0].length;

        // Tính toán chiều rộng gạch để lấp đầy khoảng 90% chiều rộng màn hình
        int availableWidth = (int) (screenWidth * 0.95);
        int brickWidth = availableWidth / cols;
        
        // Chiều cao gạch cố định theo tỉ lệ màn hình (khoảng 3%)
        int brickHeight = (int) (screenHeight * 0.035);
        if (brickHeight < 18) brickHeight = 18;
        
        // Vị trí bắt đầu (cách lề trên 8%)
        int topOffset = (int) (screenHeight * 0.08);
        
        // Căn giữa khối gạch
        int totalGridWidth = cols * brickWidth;
        int offsetX = (screenWidth - totalGridWidth) / 2;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int typeId = originalData[r][c];
                if (typeId > 0) {
                    // Không cộng thêm spacing để các viên gạch nằm sát nhau
                    int x = c * brickWidth + offsetX;
                    int y = r * brickHeight + topOffset;
                    
                    if (isInitial) {
                        Brick.BrickType type = getBrickTypeById(typeId);
                        // Size bằng đúng brickWidth/brickHeight để khít nhau
                        bricks[r][c] = new Brick(x, y, brickWidth, brickHeight, type);
                        if (type != Brick.BrickType.UNBREAKABLE) {
                            remainingBricks++;
                        }
                    } else if (bricks[r][c] != null) {
                        bricks[r][c].setPosition(x, y);
                        bricks[r][c].setSize(brickWidth, brickHeight);
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
