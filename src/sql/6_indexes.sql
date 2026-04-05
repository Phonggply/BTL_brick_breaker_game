-- =====================================================
-- INDEXES (MySQL)
-- =====================================================

-- Index cho Scores
CREATE INDEX IX_Scores_PlayerId_Score ON Scores (PlayerId, Score DESC);
CREATE INDEX IX_Scores_PlayedDate ON Scores (PlayedDate DESC);

-- Index cho Purchases
CREATE INDEX IX_Purchases_PlayerId ON Purchases (PlayerId, PurchaseDate DESC);
CREATE INDEX IX_Purchases_ItemId ON Purchases (ItemId);

-- Index cho Inventory
CREATE INDEX IX_Inventory_PlayerId_IsEquipped ON Inventory (PlayerId, IsEquipped);

-- Index cho SaveGame
CREATE INDEX IX_SaveGame_PlayerId_SaveDate ON SaveGame (PlayerId, SaveDate DESC);

-- Index cho GameStats
CREATE INDEX IX_GameStats_TotalScore ON GameStats (TotalScore DESC);

-- Thêm Index cho cột mới LevelNumber
CREATE INDEX IX_Scores_LevelNumber ON Scores (LevelNumber);
