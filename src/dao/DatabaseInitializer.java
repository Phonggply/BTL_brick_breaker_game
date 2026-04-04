package dao;

import java.sql.*;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                createTables(conn);
                insertSampleData(conn);
                System.out.println("Khởi tạo Database SQLite thành công!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // 1. Players
        stmt.execute("CREATE TABLE IF NOT EXISTS Players (" +
                "PlayerId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "UserName TEXT NOT NULL UNIQUE, " +
                "Email TEXT, " +
                "Coins INTEGER DEFAULT 1000, " +
                "Gems INTEGER DEFAULT 50, " +
                "CreatedDate TEXT, " +
                "CreatedAt TEXT)");

        // 2. Scores
        stmt.execute("CREATE TABLE IF NOT EXISTS Scores (" +
                "ScoreId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PlayerId INTEGER, " +
                "Score INTEGER NOT NULL, " +
                "PlayedDate TEXT, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");

        // 3. GameStats
        stmt.execute("CREATE TABLE IF NOT EXISTS GameStats (" +
                "StatsId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PlayerId INTEGER UNIQUE, " +
                "GamesPlayed INTEGER DEFAULT 0, " +
                "TotalScore INTEGER DEFAULT 0, " +
                "BricksBroken INTEGER DEFAULT 0, " +
                "HighestLevel INTEGER DEFAULT 1, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");

        // 4. ShopItems
        stmt.execute("CREATE TABLE IF NOT EXISTS ShopItems (" +
                "ItemId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ItemName TEXT NOT NULL, " +
                "Description TEXT, " +
                "Price INTEGER NOT NULL, " +
                "CurrencyType TEXT, " +
                "ItemType TEXT, " +
                "EffectType TEXT, " +
                "EffectValue INTEGER, " +
                "ImagePath TEXT, " +
                "IsActive INTEGER DEFAULT 1)");

        // 5. UserInventory
        stmt.execute("CREATE TABLE IF NOT EXISTS UserInventory (" +
                "InventoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PlayerId INTEGER, " +
                "ItemId INTEGER, " +
                "Quantity INTEGER DEFAULT 1, " +
                "IsEquipped INTEGER DEFAULT 0, " +
                "PurchasedAt TEXT, " +
                "UNIQUE(PlayerId, ItemId), " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId), " +
                "FOREIGN KEY(ItemId) REFERENCES ShopItems(ItemId))");

        // 6. PurchaseHistory
        stmt.execute("CREATE TABLE IF NOT EXISTS PurchaseHistory (" +
                "HistoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PlayerId INTEGER, " +
                "ItemId INTEGER, " +
                "PricePaid INTEGER, " +
                "PurchaseDate TEXT, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId), " +
                "FOREIGN KEY(ItemId) REFERENCES ShopItems(ItemId))");

        // 7. BallProperties
        stmt.execute("CREATE TABLE IF NOT EXISTS BallProperties (" +
                "PlayerId INTEGER PRIMARY KEY, " +
                "BallSpeed REAL DEFAULT 5.0, " +
                "BallSize REAL DEFAULT 1.0, " +
                "BallCount INTEGER DEFAULT 1, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");

        // 8. PaddleProperties
        stmt.execute("CREATE TABLE IF NOT EXISTS PaddleProperties (" +
                "PlayerId INTEGER PRIMARY KEY, " +
                "PaddleWidth REAL DEFAULT 100.0, " +
                "PaddleSpeed REAL DEFAULT 10.0, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");

        // 9. SaveGame
        stmt.execute("CREATE TABLE IF NOT EXISTS SaveGame (" +
                "SaveId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PlayerId INTEGER, " +
                "Level INTEGER DEFAULT 1, " +
                "CurrentScore INTEGER DEFAULT 0, " +
                "Lives INTEGER DEFAULT 3, " +
                "GameState TEXT, " +
                "SaveDate TEXT, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        System.out.println("Đang cập nhật dữ liệu Shop...");
        // Sử dụng INSERT OR REPLACE với ID cố định để cập nhật giá mà không làm mất dữ liệu người chơi
        String sql = "INSERT OR REPLACE INTO ShopItems (ItemId, ItemName, Description, Price, CurrencyType, ItemType, EffectType, EffectValue, ImagePath, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // PowerUps (ID 1, 2, 3) - Giá 300 Coin
            ps.setInt(1, 1); ps.setString(2, "Multi Ball"); ps.setString(3, "Thêm 2 quả bóng mới vào màn chơi"); ps.setInt(4, 300); ps.setString(5, "Coin"); ps.setString(6, "PowerUp"); ps.setString(7, "MULTIBALL"); ps.setInt(8, 1); ps.setString(9, "assets/multiball_powerup.png"); ps.addBatch();
            ps.setInt(1, 2); ps.setString(2, "Expand Paddle"); ps.setString(3, "Tăng chiều rộng của thanh đỡ"); ps.setInt(4, 300); ps.setString(5, "Coin"); ps.setString(6, "PowerUp"); ps.setString(7, "EXPAND"); ps.setInt(8, 20); ps.setString(9, "assets/powerup_expand.png"); ps.addBatch();
            ps.setInt(1, 3); ps.setString(2, "Shield"); ps.setString(3, "Kích hoạt lá chắn bảo vệ bóng"); ps.setInt(4, 300); ps.setString(5, "Coin"); ps.setString(6, "PowerUp"); ps.setString(7, "SHIELD"); ps.setInt(8, 20); ps.setString(9, "assets/powerup_shield.png"); ps.addBatch();
            
            // Các vật phẩm khác (ID 4, 5, 6...)
            ps.setInt(1, 4); ps.setString(2, "Extra Life"); ps.setString(3, "Thêm 1 mạng chơi"); ps.setInt(4, 1000); ps.setString(5, "Gem"); ps.setString(6, "PowerUp"); ps.setString(7, "Life"); ps.setInt(8, 1); ps.setString(9, "assets/brick_strong_3.png"); ps.addBatch();
            ps.setInt(1, 5); ps.setString(2, "Long Paddle"); ps.setString(3, "Tăng chiều dài vợt vĩnh viễn"); ps.setInt(4, 400); ps.setString(5, "Coin"); ps.setString(6, "Width"); ps.setString(7, "Width"); ps.setInt(8, 20); ps.setString(9, "assets/paddle.png"); ps.addBatch();
            
            ps.executeBatch();
            System.out.println("Đã chèn/cập nhật xong các vật phẩm Shop.");
        }
        
        // Tạo một người chơi mặc định nếu CHƯA CÓ
        String sqlPlayer = "INSERT OR IGNORE INTO Players (PlayerId, UserName, Email, Coins, Gems) VALUES (1, 'Player1', 'player1@gmail.com', 5000, 100)";
        conn.createStatement().execute(sqlPlayer);
        System.out.println("Dữ liệu người chơi đã sẵn sàng.");
    }
}
