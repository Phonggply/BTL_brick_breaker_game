-- =====================================================
-- Audit Log Table (MySQL)
-- =====================================================
CREATE TABLE IF NOT EXISTS AuditLog (
    LogId INT AUTO_INCREMENT PRIMARY KEY,
    TableName VARCHAR(100),
    Action VARCHAR(10),
    RecordId INT,
    OldValue TEXT,
    NewValue TEXT,
    ChangedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- TRIGGERS (MySQL)
-- =====================================================
DELIMITER //

-- Trigger: Cập nhật thống kê khi có điểm mới
DROP TRIGGER IF EXISTS trg_AfterInsertScore //
CREATE TRIGGER trg_AfterInsertScore
AFTER INSERT ON Scores
FOR EACH ROW
BEGIN
    -- Cập nhật tổng điểm trong GameStats
    UPDATE GameStats 
    SET 
        TotalScore = TotalScore + NEW.Score,
        GamesPlayed = GamesPlayed + 1
    WHERE PlayerId = NEW.PlayerId;
    
    -- Cập nhật HighestLevel nếu Level mới cao hơn
    UPDATE GameStats
    SET HighestLevel = NEW.LevelNumber
    WHERE PlayerId = NEW.PlayerId AND HighestLevel < NEW.LevelNumber;
END //

-- Trigger: Kiểm tra giá trước khi mua (Thay thế INSTEAD OF)
DROP TRIGGER IF EXISTS trg_BeforeInsertPurchase //
CREATE TRIGGER trg_BeforeInsertPurchase
BEFORE INSERT ON Purchases
FOR EACH ROW
BEGIN
    DECLARE v_Price INT;
    
    SELECT Price INTO v_Price FROM ShopItems WHERE ItemId = NEW.ItemId;
    
    IF NEW.PricePaid < v_Price THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Giá thanh toán không hợp lệ';
    END IF;
END //

-- Trigger: Audit log khi cập nhật thông tin người chơi
DROP TRIGGER IF EXISTS trg_AuditPlayers //
CREATE TRIGGER trg_AuditPlayers
AFTER UPDATE ON Players
FOR EACH ROW
BEGIN
    INSERT INTO AuditLog (TableName, Action, RecordId, OldValue, NewValue)
    VALUES (
        'Players',
        'UPDATE',
        OLD.PlayerId,
        CONCAT('Coins: ', OLD.Coins, ', UserName: ', OLD.UserName),
        CONCAT('Coins: ', NEW.Coins, ', UserName: ', NEW.UserName)
    );
END //

DELIMITER ;
