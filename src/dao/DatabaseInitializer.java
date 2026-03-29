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

        // 6. BallProperties
        stmt.execute("CREATE TABLE IF NOT EXISTS BallProperties (" +
                "PlayerId INTEGER PRIMARY KEY, " +
                "BallSpeed REAL DEFAULT 5.0, " +
                "BallSize REAL DEFAULT 1.0, " +
                "BallCount INTEGER DEFAULT 1, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");

        // 7. PaddleProperties
        stmt.execute("CREATE TABLE IF NOT EXISTS PaddleProperties (" +
                "PlayerId INTEGER PRIMARY KEY, " +
                "PaddleWidth REAL DEFAULT 100.0, " +
                "PaddleSpeed REAL DEFAULT 10.0, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");

        // 8. SaveGame
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
        // Kiểm tra xem đã có dữ liệu chưa để tránh trùng lặp
        String checkSql = "SELECT COUNT(*) FROM ShopItems";
        try (ResultSet rs = conn.createStatement().executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }

        String sql = "INSERT INTO ShopItems (ItemName, Description, Price, CurrencyType, ItemType, EffectType, EffectValue, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // PowerUps
            ps.setString(1, "Extra Life"); ps.setString(2, "Tăng thêm 1 mạng"); ps.setInt(3, 500); ps.setString(4, "Coin"); ps.setString(5, "PowerUp"); ps.setString(6, "Life"); ps.setInt(7, 1); ps.addBatch();
            ps.setString(1, "Speed Boost"); ps.setString(2, "Tăng tốc độ bóng"); ps.setInt(3, 10); ps.setString(4, "Gem"); ps.setString(5, "Ball"); ps.setString(6, "Speed"); ps.setInt(7, 20); ps.addBatch();
            ps.setString(1, "Wide Paddle"); ps.setString(2, "Vợt rộng hơn"); ps.setInt(3, 1000); ps.setString(4, "Coin"); ps.setString(5, "Paddle"); ps.setString(6, "Width"); ps.setInt(7, 50); ps.addBatch();
            
            ps.executeBatch();
        }
        
        // Tạo một người chơi mặc định nếu chưa có
        String sqlPlayer = "INSERT OR IGNORE INTO Players (PlayerId, UserName, Email, Coins, Gems) VALUES (1, 'Player1', 'player1@gmail.com', 2000, 100)";
        conn.createStatement().execute(sqlPlayer);
    }
}
