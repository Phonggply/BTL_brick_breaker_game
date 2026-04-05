-- 1. Thêm cột LevelNumber vào bảng Scores (MySQL syntax)
ALTER TABLE Scores ADD COLUMN LevelNumber INT DEFAULT 1;

-- 2. Cập nhật các bản ghi cũ (nếu có) mặc định là Level 1
UPDATE Scores SET LevelNumber = 1 WHERE LevelNumber IS NULL;

-- 3. Tạo một View để dễ dàng xem bảng xếp hạng mới (MySQL syntax)
CREATE OR ALTER VIEW vw_LeaderboardByMaxScoreSum AS
SELECT 
    p.UserName, 
    SUM(ms.MaxScore) AS TotalPoint,
    COUNT(ms.LevelNumber) AS LevelsCompleted
FROM (
    -- Lấy điểm cao nhất của mỗi người chơi ở mỗi màn
    SELECT PlayerId, LevelNumber, MAX(Score) AS MaxScore
    FROM Scores
    GROUP BY PlayerId, LevelNumber
) ms
JOIN Players p ON ms.PlayerId = p.PlayerId
GROUP BY p.PlayerId, p.UserName
ORDER BY TotalPoint DESC;
