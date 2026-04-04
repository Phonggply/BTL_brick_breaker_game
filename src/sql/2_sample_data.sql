USE [BrickBreakerGameDB]
GO

-- =====================================================
-- THÊM DỮ LIỆU MẪU CHO GAME
-- =====================================================

-- Thêm người chơi mẫu
INSERT INTO [dbo].[Players] ([UserName], [Email], [Coins], [Gems], [CreatedAt])
VALUES 
    ('NguyenVanA', 'nguyenvana@email.com', 5000, 100, GETDATE()),
    ('TranThiB', 'tranthib@email.com', 3000, 50, GETDATE()),
    ('LeVanC', 'levanc@email.com', 10000, 200, GETDATE()),
    ('PhamThiD', 'phamthid@email.com', 1500, 30, GETDATE()),
    ('HoangVanE', 'hoangvane@email.com', 7500, 150, GETDATE());
GO

-- Thêm thuộc tính bóng cho người chơi
INSERT INTO [dbo].[BallProperties] ([PlayerId], [BallSpeed], [BallSize], [BallCount])
SELECT PlayerId, 5.0, 1.0, 1 FROM [dbo].[Players];
GO

-- Thêm thuộc tính vợt cho người chơi
INSERT INTO [dbo].[PaddleProperties] ([PlayerId], [PaddleWidth], [PaddleSpeed])
SELECT PlayerId, 100.0, 10.0 FROM [dbo].[Players];
GO

-- Thêm vật phẩm shop
INSERT INTO [dbo].[ShopItems] ([ItemName], [Description], [Price], [CurrencyType], [ItemType], [EffectType], [EffectValue], [ImagePath], [IsActive])
VALUES 
    -- PowerUps (Đồng giá 300 Coin)
    ('Multi Ball', 'Thêm 2 quả bóng mới vào màn chơi', 300, 'Coin', 'PowerUp', 'MULTIBALL', 1, 'assets/multiball_powerup.png', 1),
    ('Expand Paddle', 'Tăng chiều rộng của thanh đỡ', 300, 'Coin', 'PowerUp', 'EXPAND', 20, 'assets/powerup_expand.png', 1),
    ('Shield', 'Kích hoạt lá chắn bảo vệ bóng không bị rơi', 300, 'Coin', 'PowerUp', 'SHIELD', 20, 'assets/powerup_shield.png', 1),
    ('Extra Life', 'Thêm 1 mạng chơi', 1000, 'Gem', 'PowerUp', 'Life', 1, 'assets/brick_strong_3.png', 1),
    
    -- Paddle Upgrades
    ('Long Paddle', 'Tăng chiều dài vợt vĩnh viễn', 400, 'Coin', 'Width', 'Width', 20, 'assets/paddle.png', 1),
    ('Fast Paddle', 'Tăng tốc độ vợt', 400, 'Coin', 'Paddle', 'PaddleSpeed', 5, 'assets/paddle.png', 1),
    
    -- Ball Upgrades
    ('Speed Ball', 'Tăng tốc độ bóng mặc định', 350, 'Coin', 'Ball', 'BallSpeed', 2, 'assets/brick_normal.png', 1);
GO

-- Thêm lịch sử điểm số
INSERT INTO [dbo].[Scores] ([PlayerId], [Score], [PlayedDate])
VALUES 
    (1, 12500, DATEADD(day, -1, GETDATE())),
    (1, 8700, DATEADD(day, -3, GETDATE())),
    (2, 15000, DATEADD(day, -2, GETDATE())),
    (3, 22000, DATEADD(day, -5, GETDATE())),
    (4, 5400, DATEADD(day, -1, GETDATE())),
    (5, 18900, DATEADD(day, -4, GETDATE()));
GO

-- Thêm thống kê game
INSERT INTO [dbo].[GameStats] ([PlayerId], [GamesPlayed], [TotalScore], [BricksBroken], [HighestLevel])
VALUES 
    (1, 25, 45800, 1250, 8),
    (2, 18, 32200, 980, 7),
    (3, 42, 98500, 2560, 12),
    (4, 12, 18900, 450, 5),
    (5, 31, 67200, 1870, 10);
GO

-- Thêm lịch sử mua hàng
INSERT INTO [dbo].[PurchaseHistory] ([PlayerId], [ItemId], [PricePaid], [PurchaseDate])
VALUES 
    (1, 1, 500, DATEADD(day, -10, GETDATE())),
    (1, 5, 400, DATEADD(day, -8, GETDATE())),
    (2, 3, 800, DATEADD(day, -5, GETDATE())),
    (3, 4, 1000, DATEADD(day, -3, GETDATE())),
    (3, 6, 600, DATEADD(day, -2, GETDATE())),
    (5, 2, 300, DATEADD(day, -7, GETDATE()));
GO

-- Thêm inventory người chơi
INSERT INTO [dbo].[UserInventory] ([PlayerId], [ItemId], [Quantity], [IsEquipped], [PurchasedAt])
VALUES 
    (1, 1, 2, 0, DATEADD(day, -10, GETDATE())),
    (1, 5, 1, 1, DATEADD(day, -8, GETDATE())),
    (2, 3, 1, 1, DATEADD(day, -5, GETDATE())),
    (3, 4, 3, 0, DATEADD(day, -3, GETDATE())),
    (3, 6, 1, 1, DATEADD(day, -2, GETDATE())),
    (5, 2, 5, 0, DATEADD(day, -7, GETDATE()));
GO

-- Thêm game đã lưu
INSERT INTO [dbo].[SaveGame] ([PlayerId], [SaveName], [Level], [CurrentScore], [Lives], [BricksRemaining], [BallPositionX], [BallPositionY], [PaddlePositionX], [GameState], [SaveDate])
VALUES 
    (1, 'Save 1', 5, 4500, 2, 12, 400.5, 300.2, 350.0, '{"level":5,"bricks":[...]}', DATEADD(hour, -2, GETDATE())),
    (3, 'Level 7 Progress', 7, 8900, 1, 8, 420.0, 280.5, 400.0, '{"level":7,"bricks":[...]}', DATEADD(hour, -5, GETDATE()));
GO

PRINT 'Thêm dữ liệu mẫu thành công!';
GO