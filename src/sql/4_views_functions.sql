USE [BrickBreakerGameDB]
GO

-- =====================================================
-- VIEWS
-- =====================================================

-- View: Thông tin chi tiết người chơi
CREATE OR ALTER VIEW [dbo].[vw_PlayerDetails]
AS
SELECT 
    p.PlayerId,
    p.UserName,
    p.Email,
    p.Coins,
    p.Gems,
    p.CreatedAt,
    gs.GamesPlayed,
    gs.TotalScore,
    gs.BricksBroken,
    gs.HighestLevel,
    bp.BallSpeed,
    bp.BallSize,
    bp.BallCount,
    pp.PaddleWidth,
    pp.PaddleSpeed
FROM Players p
LEFT JOIN GameStats gs ON p.PlayerId = gs.PlayerId
LEFT JOIN BallProperties bp ON p.PlayerId = bp.PlayerId
LEFT JOIN PaddleProperties pp ON p.PlayerId = pp.PlayerId;
GO

-- View: Top người chơi
CREATE OR ALTER VIEW [dbo].[vw_TopPlayers]
AS
SELECT TOP 100
    ROW_NUMBER() OVER (ORDER BY gs.TotalScore DESC) AS Rank,
    p.UserName,
    gs.TotalScore,
    gs.HighestLevel,
    gs.GamesPlayed,
    (SELECT COUNT(*) FROM Scores s WHERE s.PlayerId = p.PlayerId AND s.Score > 10000) AS HighScoreCount
FROM Players p
JOIN GameStats gs ON p.PlayerId = gs.PlayerId
WHERE gs.TotalScore > 0
ORDER BY gs.TotalScore DESC;
GO

-- View: Shop items active
CREATE OR ALTER VIEW [dbo].[vw_ShopItems]
AS
SELECT 
    ItemId,
    ItemName,
    Description,
    Price,
    CurrencyType,
    ItemType,
    EffectType,
    EffectValue,
    ImagePath,
    CASE 
        WHEN ItemType = 'PowerUp' THEN 1
        WHEN ItemType = 'Paddle' THEN 2
        WHEN ItemType = 'Ball' THEN 3
        ELSE 4
    END AS SortOrder
FROM ShopItems
WHERE IsActive = 1;
GO

-- =====================================================
-- FUNCTIONS
-- =====================================================

-- Function: Tính phần trăm hoàn thành game
CREATE OR ALTER FUNCTION [dbo].[fn_GetCompletionRate](@PlayerId INT)
RETURNS FLOAT
AS
BEGIN
    DECLARE @Rate FLOAT;
    
    SELECT @Rate = 
        CASE 
            WHEN GamesPlayed > 0 
            THEN (CAST(TotalScore AS FLOAT) / (GamesPlayed * 10000)) * 100
            ELSE 0 
        END
    FROM GameStats
    WHERE PlayerId = @PlayerId;
    
    RETURN ISNULL(@Rate, 0);
END
GO

-- Function: Lấy rank của người chơi
CREATE OR ALTER FUNCTION [dbo].[fn_GetPlayerRank](@PlayerId INT)
RETURNS INT
AS
BEGIN
    DECLARE @Rank INT;
    
    WITH RankedPlayers AS (
        SELECT 
            PlayerId,
            ROW_NUMBER() OVER (ORDER BY TotalScore DESC) AS Rank
        FROM GameStats
        WHERE TotalScore > 0
    )
    SELECT @Rank = Rank
    FROM RankedPlayers
    WHERE PlayerId = @PlayerId;
    
    RETURN ISNULL(@Rank, 999999);
END
GO

-- Function: Kiểm tra có đủ tiền mua không
CREATE OR ALTER FUNCTION [dbo].[fn_CanAfford](
    @PlayerId INT,
    @ItemId INT,
    @Quantity INT
)
RETURNS BIT
AS
BEGIN
    DECLARE @CanAfford BIT = 0;
    DECLARE @Price INT, @CurrencyType NVARCHAR(10);
    DECLARE @PlayerCoins INT, @PlayerGems INT;
    DECLARE @TotalPrice INT;
    
    SELECT @Price = Price, @CurrencyType = CurrencyType
    FROM ShopItems WHERE ItemId = @ItemId;
    
    SELECT @PlayerCoins = Coins, @PlayerGems = Gems
    FROM Players WHERE PlayerId = @PlayerId;
    
    SET @TotalPrice = @Price * @Quantity;
    
    IF @CurrencyType = 'Coin' AND @PlayerCoins >= @TotalPrice
        SET @CanAfford = 1;
    ELSE IF @CurrencyType = 'Gem' AND @PlayerGems >= @TotalPrice
        SET @CanAfford = 1;
    
    RETURN @CanAfford;
END
GO