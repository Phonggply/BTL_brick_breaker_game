USE [BrickBreakerGameDB]
GO

-- =====================================================
-- 12. THỐNG KÊ NGƯỜI CHƠI
-- =====================================================
CREATE OR ALTER PROCEDURE [dbo].[sp_GetPlayerStats]
    @PlayerId INT
AS
BEGIN
    SELECT 
        p.UserName,
        p.Coins,
        p.Gems,
        p.CreatedAt,
        gs.GamesPlayed,
        gs.TotalScore,
        gs.BricksBroken,
        gs.HighestLevel,
        (SELECT COUNT(*) FROM Scores WHERE PlayerId = @PlayerId AND Score > 10000) AS HighScoreGames,
        (SELECT AVG(Score) FROM Scores WHERE PlayerId = @PlayerId) AS AverageScore,
        (SELECT TOP 1 Score FROM Scores WHERE PlayerId = @PlayerId ORDER BY Score DESC) AS PersonalBest
    FROM Players p
    LEFT JOIN GameStats gs ON p.PlayerId = gs.PlayerId
    WHERE p.PlayerId = @PlayerId;
END
GO

-- =====================================================
-- 13. BẢNG XẾP HẠNG
-- =====================================================
CREATE OR ALTER PROCEDURE [dbo].[sp_GetLeaderboard]
    @TopCount INT = 10,
    @SortBy NVARCHAR(20) = 'Score' -- Score, Games, Level
AS
BEGIN
    IF @SortBy = 'Score'
        SELECT TOP (@TopCount)
            p.UserName,
            gs.TotalScore,
            gs.GamesPlayed,
            gs.HighestLevel,
            gs.BricksBroken,
            (SELECT MAX(Score) FROM Scores WHERE PlayerId = p.PlayerId) AS BestScore
        FROM Players p
        JOIN GameStats gs ON p.PlayerId = gs.PlayerId
        ORDER BY gs.TotalScore DESC;
    
    ELSE IF @SortBy = 'Games'
        SELECT TOP (@TopCount)
            p.UserName,
            gs.GamesPlayed,
            gs.TotalScore,
            gs.HighestLevel
        FROM Players p
        JOIN GameStats gs ON p.PlayerId = gs.PlayerId
        ORDER BY gs.GamesPlayed DESC;
    
    ELSE IF @SortBy = 'Level'
        SELECT TOP (@TopCount)
            p.UserName,
            gs.HighestLevel,
            gs.TotalScore,
            gs.GamesPlayed
        FROM Players p
        JOIN GameStats gs ON p.PlayerId = gs.PlayerId
        ORDER BY gs.HighestLevel DESC;
END
GO

-- =====================================================
-- 14. SỬ DỤNG POWER-UP
-- =====================================================
CREATE OR ALTER PROCEDURE [dbo].[sp_UsePowerUp]
    @PlayerId INT,
    @InventoryId INT
AS
BEGIN
    BEGIN TRANSACTION;
    
    DECLARE @ItemId INT, @Quantity INT, @EffectType NVARCHAR(50), @EffectValue INT;
    
    -- Lấy thông tin item
    SELECT @ItemId = ItemId, @Quantity = Quantity 
    FROM UserInventory WHERE InventoryId = @InventoryId AND PlayerId = @PlayerId;
    
    IF @Quantity <= 0
    BEGIN
        ROLLBACK;
        SELECT 'Không đủ số lượng' AS Message;
        RETURN;
    END
    
    SELECT @EffectType = EffectType, @EffectValue = EffectValue 
    FROM ShopItems WHERE ItemId = @ItemId;
    
    -- Giảm số lượng
    UPDATE UserInventory SET Quantity = Quantity - 1 
    WHERE InventoryId = @InventoryId;
    
    -- Xóa nếu hết
    IF (SELECT Quantity FROM UserInventory WHERE InventoryId = @InventoryId) = 0
        DELETE FROM UserInventory WHERE InventoryId = @InventoryId;
    
    -- Áp dụng hiệu ứng
    IF @EffectType = 'BallCount'
        UPDATE BallProperties SET BallCount = BallCount + @EffectValue 
        WHERE PlayerId = @PlayerId;
    
    IF @EffectType = 'BallSpeed'
        UPDATE BallProperties SET BallSpeed = BallSpeed * (1 + @EffectValue/100.0) 
        WHERE PlayerId = @PlayerId;
    
    IF @EffectType = 'PaddleWidth'
        UPDATE PaddleProperties SET PaddleWidth = PaddleWidth + @EffectValue 
        WHERE PlayerId = @PlayerId;
    
    IF @EffectType = 'PaddleSpeed'
        UPDATE PaddleProperties SET PaddleSpeed = PaddleSpeed + @EffectValue 
        WHERE PlayerId = @PlayerId;
    
    COMMIT;
    SELECT 'Sử dụng power-up thành công' AS Message;
END
GO

-- =====================================================
-- 15. TẠO NGƯỜI CHƠI MỚI
-- =====================================================
CREATE OR ALTER PROCEDURE [dbo].[sp_CreatePlayer]
    @UserName NVARCHAR(50),
    @Email NVARCHAR(100)
AS
BEGIN
    BEGIN TRANSACTION;
    
    -- Kiểm tra username đã tồn tại chưa
    IF EXISTS (SELECT 1 FROM Players WHERE UserName = @UserName)
    BEGIN
        ROLLBACK;
        SELECT 'Username đã tồn tại' AS Message;
        RETURN;
    END
    
    -- Tạo player mới
    INSERT INTO Players (UserName, Email, Coins, Gems, CreatedAt)
    VALUES (@UserName, @Email, 1000, 50, GETDATE());
    
    DECLARE @NewPlayerId INT = SCOPE_IDENTITY();
    
    -- Tạo thuộc tính mặc định
    INSERT INTO BallProperties (PlayerId, BallSpeed, BallSize, BallCount)
    VALUES (@NewPlayerId, 5.0, 1.0, 1);
    
    INSERT INTO PaddleProperties (PlayerId, PaddleWidth, PaddleSpeed)
    VALUES (@NewPlayerId, 100.0, 10.0);
    
    -- Tạo thống kê
    INSERT INTO GameStats (PlayerId, GamesPlayed, TotalScore, BricksBroken, HighestLevel)
    VALUES (@NewPlayerId, 0, 0, 0, 1);
    
    COMMIT;
    
    SELECT @NewPlayerId AS PlayerId, 'Tạo tài khoản thành công' AS Message;
END
GO

-- =====================================================
-- 16. XÓA NGƯỜI CHƠI
-- =====================================================
CREATE OR ALTER PROCEDURE [dbo].[sp_DeletePlayer]
    @PlayerId INT
AS
BEGIN
    BEGIN TRANSACTION;
    
    -- Xóa dữ liệu liên quan
    DELETE FROM BallProperties WHERE PlayerId = @PlayerId;
    DELETE FROM PaddleProperties WHERE PlayerId = @PlayerId;
    DELETE FROM GameStats WHERE PlayerId = @PlayerId;
    DELETE FROM Scores WHERE PlayerId = @PlayerId;
    DELETE FROM UserInventory WHERE PlayerId = @PlayerId;
    DELETE FROM PurchaseHistory WHERE PlayerId = @PlayerId;
    DELETE FROM SaveGame WHERE PlayerId = @PlayerId;
    
    -- Xóa player
    DELETE FROM Players WHERE PlayerId = @PlayerId;
    
    COMMIT;
    SELECT 'Xóa người chơi thành công' AS Message;
END
GO