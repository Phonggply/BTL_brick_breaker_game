USE [BrickBreakerGameDB]
GO

-- Index cho Scores
CREATE NONCLUSTERED INDEX [IX_Scores_PlayerId_Score] 
ON [dbo].[Scores] ([PlayerId], [Score] DESC)
INCLUDE ([PlayedDate]);
GO

CREATE NONCLUSTERED INDEX [IX_Scores_PlayedDate] 
ON [dbo].[Scores] ([PlayedDate] DESC);
GO

-- Index cho PurchaseHistory
CREATE NONCLUSTERED INDEX [IX_PurchaseHistory_PlayerId] 
ON [dbo].[PurchaseHistory] ([PlayerId], [PurchaseDate] DESC);
GO

CREATE NONCLUSTERED INDEX [IX_PurchaseHistory_ItemId] 
ON [dbo].[PurchaseHistory] ([ItemId]);
GO

-- Index cho UserInventory
CREATE NONCLUSTERED INDEX [IX_UserInventory_PlayerId_IsEquipped] 
ON [dbo].[UserInventory] ([PlayerId], [IsEquipped])
INCLUDE ([ItemId], [Quantity]);
GO

-- Index cho SaveGame
CREATE NONCLUSTERED INDEX [IX_SaveGame_PlayerId_SaveDate] 
ON [dbo].[SaveGame] ([PlayerId], [SaveDate] DESC);
GO

-- Index cho GameStats
CREATE NONCLUSTERED INDEX [IX_GameStats_TotalScore] 
ON [dbo].[GameStats] ([TotalScore] DESC)
INCLUDE ([PlayerId], [GamesPlayed], [HighestLevel]);
GO

PRINT 'Tạo indexes bổ sung thành công!';
GO