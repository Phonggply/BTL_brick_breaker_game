-- =====================================================
-- VIEWS (MySQL)
-- =====================================================

-- View: Thông tin chi tiết người chơi
DROP VIEW IF EXISTS vw_PlayerDetails;
CREATE VIEW vw_PlayerDetails AS
SELECT 
    p.PlayerId,
    p.UserName,
    p.Coins,
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

-- View: Top người chơi (Theo tổng điểm cao nhất mỗi màn)
DROP VIEW IF EXISTS vw_TopPlayers;
CREATE VIEW vw_TopPlayers AS
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
ORDER BY TotalPoint DESC;

-- View: Shop items active
DROP VIEW IF EXISTS vw_ShopItems;
CREATE VIEW vw_ShopItems AS
SELECT 
    ItemId,
    ItemName,
    Description,
    Price,
    Category,
    EffectType,
    EffectValue,
    CASE 
        WHEN Category = 'PowerUp' THEN 1
        WHEN Category = 'Width' THEN 2
        WHEN Category = 'Paddle' THEN 3
        WHEN Category = 'Ball' THEN 4
        ELSE 5
    END AS SortOrder
FROM ShopItems;

-- =====================================================
-- FUNCTIONS (MySQL)
-- =====================================================
DELIMITER //

-- Function: Tính phần trăm hoàn thành game
DROP FUNCTION IF EXISTS fn_GetCompletionRate //
CREATE FUNCTION fn_GetCompletionRate(p_PlayerId INT) 
RETURNS DOUBLE
DETERMINISTIC
BEGIN
    DECLARE v_Rate DOUBLE DEFAULT 0;
    
    SELECT 
        CASE 
            WHEN GamesPlayed > 0 
            THEN (CAST(TotalScore AS DOUBLE) / (GamesPlayed * 10000)) * 100
            ELSE 0 
        END INTO v_Rate
    FROM GameStats
    WHERE PlayerId = p_PlayerId;
    
    RETURN COALESCE(v_Rate, 0);
END //

-- Function: Lấy rank của người chơi
DROP FUNCTION IF EXISTS fn_GetPlayerRank //
CREATE FUNCTION fn_GetPlayerRank(p_PlayerId INT) 
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_Rank INT DEFAULT 999999;
    
    -- MySQL 8.0+ hỗ trợ Window Functions
    SELECT `Rank` INTO v_Rank FROM (
        SELECT 
            PlayerId,
            ROW_NUMBER() OVER (ORDER BY TotalScore DESC) AS `Rank`
        FROM GameStats
        WHERE TotalScore > 0
    ) r
    WHERE PlayerId = p_PlayerId;
    
    RETURN COALESCE(v_Rank, 999999);
END //

-- Function: Kiểm tra có đủ tiền mua không
DROP FUNCTION IF EXISTS fn_CanAfford //
CREATE FUNCTION fn_CanAfford(
    p_PlayerId INT,
    p_ItemId INT,
    p_Quantity INT
) 
RETURNS TINYINT(1)
DETERMINISTIC
BEGIN
    DECLARE v_CanAfford TINYINT(1) DEFAULT 0;
    DECLARE v_Price INT;
    DECLARE v_PlayerCoins INT;
    DECLARE v_TotalPrice INT;
    
    SELECT Price INTO v_Price FROM ShopItems WHERE ItemId = p_ItemId;
    SELECT Coins INTO v_PlayerCoins FROM Players WHERE PlayerId = p_PlayerId;
    
    SET v_TotalPrice = v_Price * p_Quantity;
    
    IF v_PlayerCoins >= v_TotalPrice THEN
        SET v_CanAfford = 1;
    END IF;
    
    RETURN v_CanAfford;
END //

DELIMITER ;
