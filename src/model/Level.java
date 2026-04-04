package model;

public class Level {

    private Brick[][] bricks;
    private int remainingBricks;
    private int[][] originalData;

    private int fixedBrickWidth;
    private int fixedBrickHeight = 18;

    public Level(int[][] data) {
        this.originalData = data;
    }

    public void initBricks(int screenWidth, int screenHeight) {
        if (screenWidth <= 0 || screenHeight <= 0) return;
        int rows = originalData.length;
        int cols = originalData[0].length;
        bricks = new Brick[rows][cols];
        remainingBricks = 0;

        // Tính toán kích thước gạch cố định dựa trên màn hình lúc bắt đầu
        this.fixedBrickWidth = (int) (screenWidth / (cols + 2));
        if (this.fixedBrickWidth > 100) this.fixedBrickWidth = 100;

        repositionBricks(screenWidth, screenHeight, true);
    }

    public void repositionBricks(int screenWidth, int screenHeight, boolean isInitial) {
        int rows = originalData.length;
        int cols = originalData[0].length;

        int brickWidth = this.fixedBrickWidth;
        int brickHeight = this.fixedBrickHeight;
        
        // Tính toán khoảng cách giữa các hàng gạch theo tỉ lệ màn hình (khoảng 3% chiều cao)
        int rowSpacing = (int) (screenHeight * 0.03);
        if (rowSpacing < 4) rowSpacing = 4; // Tối thiểu 4px
        if (rowSpacing > 10) rowSpacing = 10; // Tối đa 10px để gạch không rời rạc quá
        
        // Vị trí bắt đầu của khối gạch (10% chiều cao màn hình)
        int topOffset = (int) (screenHeight * 0.1);
        
        int gridWidth = cols * brickWidth;
        int offsetX = (screenWidth - gridWidth) / 2;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int typeId = originalData[r][c];
                if (typeId > 0) {
                    int x = c * brickWidth + offsetX;
                    // Vị trí Y giờ đây phụ thuộc vào screenHeight
                    int y = r * (brickHeight + rowSpacing) + topOffset;
                    
                    if (isInitial) {
                        Brick.BrickType type = getBrickTypeById(typeId);
                        bricks[r][c] = new Brick(x, y, brickWidth - 2, brickHeight - 2, type);
                        if (type != Brick.BrickType.UNBREAKABLE) {
                            remainingBricks++;
                        }
                    } else if (bricks[r][c] != null) {
                        bricks[r][c].setPosition(x, y);
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
