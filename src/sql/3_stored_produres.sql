-- =====================================================
-- 12. THỐNG KÊ NGƯỜI CHƠI (MySQL)
-- =====================================================
DELIMITER //

CREATE PROCEDURE sp_GetPlayerStats(IN p_PlayerId INT)
BEGIN
    SELECT 
        p.UserName,
        p.Coins,
        p.CreatedAt,
        gs.GamesPlayed,
        gs.TotalScore,
        gs.BricksBroken,
        gs.HighestLevel,
        (SELECT COUNT(*) FROM Scores WHERE PlayerId = p_PlayerId AND Score > 10000) AS HighScoreGames,
        (SELECT AVG(Score) FROM Scores WHERE PlayerId = p_PlayerId) AS AverageScore,
        (SELECT MAX(Score) FROM Scores WHERE PlayerId = p_PlayerId) AS PersonalBest
    FROM Players p
    LEFT JOIN GameStats gs ON p.PlayerId = gs.PlayerId
    WHERE p.PlayerId = p_PlayerId;
END //

-- =====================================================
-- 13. BẢNG XẾP HẠNG (MySQL - Cập nhật theo yêu cầu tổng điểm cao nhất mỗi màn)
-- =====================================================
CREATE PROCEDURE sp_GetLeaderboard(
    IN p_TopCount INT,
    IN p_SortBy VARCHAR(20)
)
BEGIN
    IF p_SortBy = 'Score' THEN
        -- Bảng xếp hạng theo TỔNG ĐIỂM CAO NHẤT CỦA MỖI LEVEL
        SELECT 
            p.UserName, 
            SUM(ms.MaxScore) AS TotalPoint,
            COUNT(ms.LevelNumber) AS LevelsCompleted
        FROM (
            SELECT PlayerId, LevelNumber, MAX(Score) AS MaxScore
            FROM Scores
            GROUP BY PlayerId, LevelNumber
        ) ms
        JOIN Players p ON ms.PlayerId = p.PlayerId
        GROUP BY p.PlayerId, p.UserName
        ORDER BY TotalPoint DESC
        LIMIT p_TopCount;
    
    ELSEIF p_SortBy = 'Games' THEN
        SELECT 
            p.UserName,
            gs.GamesPlayed,
            gs.TotalScore,
            gs.HighestLevel
        FROM Players p
        JOIN GameStats gs ON p.PlayerId = gs.PlayerId
        ORDER BY gs.GamesPlayed DESC
        LIMIT p_TopCount;
    
    ELSEIF p_SortBy = 'Level' THEN
        SELECT 
            p.UserName,
            gs.HighestLevel,
            gs.TotalScore,
            gs.GamesPlayed
        FROM Players p
        JOIN GameStats gs ON p.PlayerId = gs.PlayerId
        ORDER BY gs.HighestLevel DESC
        LIMIT p_TopCount;
    END IF;
END //

-- =====================================================
-- 14. SỬ DỤNG POWER-UP
-- =====================================================
CREATE PROCEDURE sp_UsePowerUp(
    IN p_PlayerId INT,
    IN p_InventoryId INT
)
BEGIN
    DECLARE v_ItemId INT;
    DECLARE v_Quantity INT;
    DECLARE v_EffectType VARCHAR(50);
    DECLARE v_EffectValue INT;
    
    START TRANSACTION;
    
    -- Lấy thông tin item
    SELECT ItemId, Quantity INTO v_ItemId, v_Quantity 
    FROM Inventory WHERE InventoryId = p_InventoryId AND PlayerId = p_PlayerId;
    
    IF v_Quantity IS NULL OR v_Quantity <= 0 THEN
        ROLLBACK;
        SELECT 'Không đủ số lượng' AS Message;
    ELSE
        SELECT EffectType, EffectValue INTO v_EffectType, v_EffectValue 
        FROM ShopItems WHERE ItemId = v_ItemId;
        
        -- Giảm số lượng
        UPDATE Inventory SET Quantity = Quantity - 1 
        WHERE InventoryId = p_InventoryId;
        
        -- Xóa nếu hết
        DELETE FROM Inventory WHERE InventoryId = p_InventoryId AND Quantity <= 0;
        
        -- Áp dụng hiệu ứng
        IF v_EffectType = 'BallCount' THEN
            UPDATE BallProperties SET BallCount = BallCount + v_EffectValue 
            WHERE PlayerId = p_PlayerId;
        ELSEIF v_EffectType = 'BallSpeed' THEN
            UPDATE BallProperties SET BallSpeed = BallSpeed * (1 + v_EffectValue/100.0) 
            WHERE PlayerId = p_PlayerId;
        ELSEIF v_EffectType = 'PaddleWidth' THEN
            UPDATE PaddleProperties SET PaddleWidth = PaddleWidth + v_EffectValue 
            WHERE PlayerId = p_PlayerId;
        ELSEIF v_EffectType = 'PaddleSpeed' THEN
            UPDATE PaddleProperties SET PaddleSpeed = PaddleSpeed + v_EffectValue 
            WHERE PlayerId = p_PlayerId;
        END IF;
        
        COMMIT;
        SELECT 'Sử dụng power-up thành công' AS Message;
    END IF;
END //

-- =====================================================
-- 15. TẠO NGƯỜI CHƠI MỚI
-- =====================================================
CREATE PROCEDURE sp_CreatePlayer(
    IN p_UserName VARCHAR(50),
    IN p_Password VARCHAR(255)
)
BEGIN
    DECLARE v_NewPlayerId INT;
    
    START TRANSACTION;
    
    -- Kiểm tra username đã tồn tại chưa
    IF EXISTS (SELECT 1 FROM Players WHERE UserName = p_UserName) THEN
        ROLLBACK;
        SELECT 'Username đã tồn tại' AS Message;
    ELSE
        -- Tạo player mới
        INSERT INTO Players (UserName, Password, Coins, CreatedAt)
        VALUES (p_UserName, p_Password, 1000, NOW());
        
        SET v_NewPlayerId = LAST_INSERT_ID();
        
        -- Tạo thuộc tính mặc định
        INSERT INTO BallProperties (PlayerId, BallSpeed, BallSize, BallCount)
        VALUES (v_NewPlayerId, 5.0, 1.0, 1);
        
        INSERT INTO PaddleProperties (PlayerId, PaddleWidth, PaddleSpeed)
        VALUES (v_NewPlayerId, 100.0, 10.0);
        
        -- Tạo thống kê
        INSERT INTO GameStats (PlayerId, GamesPlayed, TotalScore, BricksBroken, HighestLevel)
        VALUES (v_NewPlayerId, 0, 0, 0, 1);
        
        COMMIT;
        SELECT v_NewPlayerId AS PlayerId, 'Tạo tài khoản thành công' AS Message;
    END IF;
END //

-- =====================================================
-- 16. XÓA NGƯỜI CHƠI
-- =====================================================
CREATE PROCEDURE sp_DeletePlayer(IN p_PlayerId INT)
BEGIN
    START TRANSACTION;
    
    -- Xóa dữ liệu liên quan (Cascade đã thiết lập ở bảng, nhưng xóa thủ công cho chắc)
    DELETE FROM BallProperties WHERE PlayerId = p_PlayerId;
    DELETE FROM PaddleProperties WHERE PlayerId = p_PlayerId;
    DELETE FROM GameStats WHERE PlayerId = p_PlayerId;
    DELETE FROM Scores WHERE PlayerId = p_PlayerId;
    DELETE FROM Inventory WHERE PlayerId = p_PlayerId;
    DELETE FROM Purchases WHERE PlayerId = p_PlayerId;
    DELETE FROM SaveGame WHERE PlayerId = p_PlayerId;
    
    -- Xóa player
    DELETE FROM Players WHERE PlayerId = p_PlayerId;
    
    COMMIT;
    SELECT 'Xóa người chơi thành công' AS Message;
END //

DELIMITER ;
