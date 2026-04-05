package dao;

import java.sql.*;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                createTables(conn);
                insertSampleData(conn);
                System.out.println("Database Initialized Successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // 1. Players
        stmt.execute("CREATE TABLE IF NOT EXISTS Players (" +
                "PlayerId INT AUTO_INCREMENT PRIMARY KEY, " +
                "UserName VARCHAR(50) NOT NULL UNIQUE, " +
                "Password VARCHAR(100) NOT NULL, " +
                "Email VARCHAR(100), " +
                "Coins INT DEFAULT 1000, " +
                "Gems INT DEFAULT 50, " +
                "CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // 2. Scores
        stmt.execute("CREATE TABLE IF NOT EXISTS Scores (" +
                "ScoreId INT AUTO_INCREMENT PRIMARY KEY, " +
                "PlayerId INT, " +
                "Score INT NOT NULL, " +
                "LevelNumber INT DEFAULT 1, " +
                "PlayedDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");
        
        // Migration: Add LevelNumber column if not exists
        try {
            stmt.execute("ALTER TABLE Scores ADD COLUMN LevelNumber INT DEFAULT 1");
            System.out.println("Column LevelNumber added to Scores table.");
        } catch (SQLException e) {
            // Column might already exist
        }

        // 3. GameStats
        stmt.execute("CREATE TABLE IF NOT EXISTS GameStats (" +
                "StatsId INT AUTO_INCREMENT PRIMARY KEY, " +
                "PlayerId INT UNIQUE, " +
                "GamesPlayed INT DEFAULT 0, " +
                "TotalScore INT DEFAULT 0, " +
                "BricksBroken INT DEFAULT 0, " +
                "HighestLevel INT DEFAULT 1, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");

        // 4. ShopItems
        stmt.execute("CREATE TABLE IF NOT EXISTS ShopItems (" +
                "ItemId INT AUTO_INCREMENT PRIMARY KEY, " +
                "ItemName VARCHAR(100) NOT NULL, " +
                "Description TEXT, " +
                "Price INT NOT NULL, " +
                "CurrencyType VARCHAR(20), " +
                "ItemType VARCHAR(50), " +
                "EffectType VARCHAR(50), " +
                "EffectValue INT, " +
                "ImagePath VARCHAR(255), " +
                "IsActive BOOLEAN DEFAULT TRUE)");

        // 5. UserInventory
        stmt.execute("CREATE TABLE IF NOT EXISTS UserInventory (" +
                "InventoryId INT AUTO_INCREMENT PRIMARY KEY, " +
                "PlayerId INT, " +
                "ItemId INT, " +
                "Quantity INT DEFAULT 1, " +
                "IsEquipped BOOLEAN DEFAULT FALSE, " +
                "PurchasedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE(PlayerId, ItemId), " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId), " +
                "FOREIGN KEY(ItemId) REFERENCES ShopItems(ItemId))");

        // 6. PurchaseHistory
        stmt.execute("CREATE TABLE IF NOT EXISTS PurchaseHistory (" +
                "HistoryId INT AUTO_INCREMENT PRIMARY KEY, " +
                "PlayerId INT, " +
                "ItemId INT, " +
                "PricePaid INT, " +
                "PurchaseDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId), " +
                "FOREIGN KEY(ItemId) REFERENCES ShopItems(ItemId))");

        // 7. BallProperties
        stmt.execute("CREATE TABLE IF NOT EXISTS BallProperties (" +
                "PlayerId INT PRIMARY KEY, " +
                "BallSpeed DOUBLE DEFAULT 5.0, " +
                "BallSize DOUBLE DEFAULT 1.0, " +
                "BallCount INT DEFAULT 1, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");

        // 8. PaddleProperties
        stmt.execute("CREATE TABLE IF NOT EXISTS PaddleProperties (" +
                "PlayerId INT PRIMARY KEY, " +
                "PaddleWidth DOUBLE DEFAULT 100.0, " +
                "PaddleSpeed DOUBLE DEFAULT 10.0, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");

        // 9. SaveGame
        stmt.execute("CREATE TABLE IF NOT EXISTS SaveGame (" +
                "SaveId INT AUTO_INCREMENT PRIMARY KEY, " +
                "PlayerId INT, " +
                "Level INT DEFAULT 1, " +
                "CurrentScore INT DEFAULT 0, " +
                "Lives INT DEFAULT 3, " +
                "GameState TEXT, " +
                "SaveDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(PlayerId) REFERENCES Players(PlayerId))");
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        System.out.println("Updating sample data...");
        String sql = "INSERT INTO ShopItems (ItemId, ItemName, Description, Price, CurrencyType, ItemType, EffectType, EffectValue, ImagePath, IsActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1) " +
                     "ON DUPLICATE KEY UPDATE ItemName=VALUES(ItemName), Price=VALUES(Price), Description=VALUES(Description)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 1); ps.setString(2, "Multi Ball"); ps.setString(3, "Add 2 balls"); ps.setInt(4, 300); ps.setString(5, "Coin"); ps.setString(6, "PowerUp"); ps.setString(7, "MULTIBALL"); ps.setInt(8, 1); ps.setString(9, "assets/multiball_powerup.png"); ps.addBatch();
            ps.setInt(1, 2); ps.setString(2, "Expand Paddle"); ps.setString(3, "Increase paddle width"); ps.setInt(4, 300); ps.setString(5, "Coin"); ps.setString(6, "PowerUp"); ps.setString(7, "EXPAND"); ps.setInt(8, 20); ps.setString(9, "assets/powerup_expand.png"); ps.addBatch();
            ps.setInt(1, 3); ps.setString(2, "Shield"); ps.setString(3, "Protect ball from falling"); ps.setInt(4, 300); ps.setString(5, "Coin"); ps.setString(6, "PowerUp"); ps.setString(7, "SHIELD"); ps.setInt(8, 20); ps.setString(9, "assets/powerup_shield.png"); ps.addBatch();
            
            ps.executeBatch();
        }
        
        String sqlPlayer = "INSERT IGNORE INTO Players (PlayerId, UserName, Password, Email, Coins, Gems) VALUES (1, 'admin', 'admin123', 'admin@game.com', 5000, 100)";
        conn.createStatement().execute(sqlPlayer);
    }
}
