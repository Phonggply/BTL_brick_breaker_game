-- Script kiểm tra cài đặt
USE BrickBreakerGameDB;
GO

PRINT '=== KIỂM TRA CÀI ĐẶT DATABASE ===';

-- Kiểm tra số lượng bảng
SELECT COUNT(*) AS SoLuongBang 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_TYPE = 'BASE TABLE';

-- Kiểm tra stored procedures
SELECT COUNT(*) AS SoLuongSP 
FROM INFORMATION_SCHEMA.ROUTINES 
WHERE ROUTINE_TYPE = 'PROCEDURE';

-- Kiểm tra triggers
SELECT COUNT(*) AS SoLuongTrigger 
FROM sys.triggers;

-- Kiểm tra dữ liệu mẫu
SELECT COUNT(*) AS SoLuongPlayer FROM Players;
SELECT COUNT(*) AS SoLuongShopItem FROM ShopItems;

PRINT '=== KIỂM TRA HOÀN TẤT ===';
GO