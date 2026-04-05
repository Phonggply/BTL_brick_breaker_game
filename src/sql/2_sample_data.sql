-- =====================================================
-- THÊM DỮ LIỆU MẪU CHO GAME (MySQL)
-- =====================================================

-- Thêm người chơi mẫu
INSERT INTO Players (UserName, Password, Coins, CreatedAt)
VALUES 
    ('NguyenVanA', 'password123', 5000, NOW()),
    ('TranThiB', 'password123', 3000, NOW()),
    ('LeVanC', 'password123', 10000, NOW()),
    ('PhamThiD', 'password123', 1500, NOW()),
    ('HoangVanE', 'password123', 7500, NOW());

-- Thêm thuộc tính bóng cho người chơi
INSERT INTO BallProperties (PlayerId, BallSpeed, BallSize, BallCount)
SELECT PlayerId, 5.0, 1.0, 1 FROM Players;

-- Thêm thuộc tính vợt cho người chơi
INSERT INTO PaddleProperties (PlayerId, PaddleWidth, PaddleSpeed)
SELECT PlayerId, 100.0, 10.0 FROM Players;

-- Thêm vật phẩm shop
INSERT INTO ShopItems (ItemName, Description, Price, Category, EffectType, EffectValue)
VALUES 
    -- PowerUps
    ('Multi Ball', 'Thêm 2 quả bóng mới vào màn chơi', 300, 'PowerUp', 'MULTIBALL', 1),
    ('Expand Paddle', 'Tăng chiều rộng của thanh đỡ', 300, 'PowerUp', 'EXPAND', 20),
    ('Shield', 'Kích hoạt lá chắn bảo vệ bóng không bị rơi', 300, 'PowerUp', 'SHIELD', 20),
    ('Extra Life', 'Thêm 1 mạng chơi', 1000, 'PowerUp', 'Life', 1),
    
    -- Paddle Upgrades
    ('Long Paddle', 'Tăng chiều dài vợt vĩnh viễn', 400, 'Width', 'Width', 20),
    ('Fast Paddle', 'Tăng tốc độ vợt', 400, 'Paddle', 'PaddleSpeed', 5),
    
    -- Ball Upgrades
    ('Speed Ball', 'Tăng tốc độ bóng mặc định', 350, 'Ball', 'BallSpeed', 2);

-- Thêm lịch sử điểm số (Có thêm LevelNumber)
INSERT INTO Scores (PlayerId, Score, LevelNumber, PlayedDate)
VALUES 
    (1, 12500, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (1, 8700, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (2, 15000, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (3, 22000, 3, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (4, 5400, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (5, 18900, 2, DATE_SUB(NOW(), INTERVAL 4 DAY));

-- Thêm thống kê game
INSERT INTO GameStats (PlayerId, GamesPlayed, TotalScore, BricksBroken, HighestLevel)
VALUES 
    (1, 25, 45800, 1250, 8),
    (2, 18, 32200, 980, 7),
    (3, 42, 98500, 2560, 12),
    (4, 12, 18900, 450, 5),
    (5, 31, 67200, 1870, 10);

-- Thêm lịch sử mua hàng
INSERT INTO Purchases (PlayerId, ItemId, PricePaid, PurchaseDate)
VALUES 
    (1, 1, 300, DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (1, 5, 400, DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (2, 3, 300, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (3, 4, 1000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (3, 6, 400, DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (5, 2, 300, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- Thêm inventory người chơi
INSERT INTO Inventory (PlayerId, ItemId, Quantity, IsEquipped)
VALUES 
    (1, 1, 2, 0),
    (1, 5, 1, 1),
    (2, 3, 1, 1),
    (3, 4, 3, 0),
    (3, 6, 1, 1),
    (5, 2, 5, 0);

-- Thêm game đã lưu
INSERT INTO SaveGame (PlayerId, SaveName, Level, CurrentScore, Lives, BricksRemaining, BallPositionX, BallPositionY, PaddlePositionX, GameState, SaveDate)
VALUES 
    (1, 'Save 1', 5, 4500, 2, 12, 400.5, 300.2, 350.0, '{"level":5,"bricks":[]}', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
    (3, 'Level 7 Progress', 7, 8900, 1, 8, 420.0, 280.5, 400.0, '{"level":7,"bricks":[]}', DATE_SUB(NOW(), INTERVAL 5 HOUR));
