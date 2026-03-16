USE [BrickBreakerGameDB]
GO

-- =====================================================
-- TRIGGER: Cập nhật thống kê khi có điểm mới
-- =====================================================
CREATE OR ALTER TRIGGER [dbo].[trg_AfterInsertScore]
ON [dbo].[Scores]
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE gs
    SET 
        TotalScore = gs.TotalScore + i.Score,
        HighestLevel = CASE 
            WHEN (SELECT COUNT(*) FROM SaveGame sg WHERE sg.PlayerId = i.PlayerId) > 0 
            THEN (SELECT MAX(Level) FROM SaveGame WHERE PlayerId = i.PlayerId)
            ELSE gs.HighestLevel
        END
    FROM GameStats gs
    INNER JOIN inserted i ON gs.PlayerId = i.PlayerId;
END
GO

-- =====================================================
-- TRIGGER: Kiểm tra số lượng tồn kho khi mua
-- =====================================================
CREATE OR ALTER TRIGGER [dbo].[trg_BeforeInsertPurchase]
ON [dbo].[PurchaseHistory]
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (
        SELECT 1 FROM inserted i
        JOIN ShopItems si ON i.ItemId = si.ItemId
        WHERE i.PricePaid < si.Price
    )
    BEGIN
        RAISERROR('Giá thanh toán không hợp lệ', 16, 1);
        RETURN;
    END
    
    INSERT INTO PurchaseHistory (PlayerId, ItemId, PricePaid, PurchaseDate)
    SELECT PlayerId, ItemId, PricePaid, PurchaseDate FROM inserted;
END
GO

-- =====================================================
-- TRIGGER: Audit log (ghi log thay đổi)
-- =====================================================
CREATE TABLE [dbo].[AuditLog] (
    LogId INT IDENTITY(1,1) PRIMARY KEY,
    TableName NVARCHAR(100),
    Action NVARCHAR(10),
    RecordId INT,
    OldValue NVARCHAR(MAX),
    NewValue NVARCHAR(MAX),
    ChangedBy INT,
    ChangedAt DATETIME DEFAULT GETDATE()
);
GO

CREATE OR ALTER TRIGGER [dbo].[trg_AuditPlayers]
ON [dbo].[Players]
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    INSERT INTO AuditLog (TableName, Action, RecordId, OldValue, NewValue, ChangedBy)
    SELECT 
        'Players',
        'UPDATE',
        d.PlayerId,
        (SELECT * FROM deleted d2 WHERE d2.PlayerId = d.PlayerId FOR JSON AUTO),
        (SELECT * FROM inserted i2 WHERE i2.PlayerId = i.PlayerId FOR JSON AUTO),
        i.PlayerId
    FROM inserted i
    INNER JOIN deleted d ON i.PlayerId = d.PlayerId;
END
GO