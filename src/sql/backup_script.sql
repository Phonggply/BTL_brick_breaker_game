-- Script backup database
USE master;
GO

DECLARE @BackupPath NVARCHAR(500) = 'D:\Backup\BrickBreakerGameDB_' + 
    FORMAT(GETDATE(), 'yyyyMMdd_HHmmss') + '.bak';

BACKUP DATABASE [BrickBreakerGameDB]
TO DISK = @BackupPath
WITH FORMAT, 
     MEDIANAME = 'BrickBreakerGameDB_Backup',
     NAME = 'Full Backup of BrickBreakerGameDB';
     
PRINT 'Backup completed: ' + @BackupPath;
GO