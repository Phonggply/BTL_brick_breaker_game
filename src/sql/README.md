# BRICK BREAKER GAME - DATABASE (MySQL Version)

Hệ thống cơ sở dữ liệu đã được tối ưu hóa cho **MySQL 8.0+** và tích hợp tính năng **Bảng xếp hạng theo tổng điểm kỷ lục của từng Level**.

## Yêu cầu hệ thống
- **MySQL Server** (hoặc MariaDB).
- Kết nối thông qua **MySQL Workbench**, **Navicat** hoặc **Clever Cloud CLI**.

## Thứ tự cài đặt (BẮT BUỘC)
Hãy chạy các file theo thứ tự đánh số từ 1 đến 6 để đảm bảo tính toàn vẹn dữ liệu và các ràng buộc khóa ngoại:

1.  **`1_script.sql`**: Khởi tạo cấu trúc các bảng (Players, Scores, GameStats, Inventory...).
2.  **`2_sample_data.sql`**: Thêm dữ liệu mẫu (Người chơi, vật phẩm Shop, lịch sử điểm số).
3.  **`3_stored_produres.sql`**: Tạo các thủ tục lưu sẵn (Tạo người chơi, tính bảng xếp hạng, sử dụng vật phẩm).
4.  **`4_views_functions.sql`**: Tạo các View thông tin và Hàm tính toán (Rank, phần trăm hoàn thành).
5.  **`5_trigger.sql`**: Tạo các Trigger tự động cập nhật thống kê khi có điểm mới hoặc mua hàng.
6.  **`6_indexes.sql`**: Tối ưu hóa hiệu năng truy vấn thông qua các chỉ mục.

## Tính năng Bảng xếp hạng mới
Bảng xếp hạng hiện tại được tính dựa trên **Tổng của các điểm số cao nhất (Personal Best) mà người chơi đạt được ở mỗi màn (Level)**.
- Khi người chơi thắng 1 màn, hệ thống lưu lại điểm của màn đó kèm theo số Level.
- Bảng xếp hạng sẽ lấy điểm kỷ lục của Level 1 + Level 2 + ... + Level n để ra tổng điểm xếp hạng.

## Lưu ý khi chạy Script trong MySQL
- **DELIMITER**: Các file số 3, 4, 5 sử dụng từ khóa `DELIMITER //`. Nếu bạn chạy từng đoạn code thủ công, hãy đảm bảo môi trường (như MySQL Workbench) hỗ trợ xử lý Delimiter.
- **Foreign Keys**: File số 1 đã thiết lập `ON DELETE CASCADE` cho các bảng liên quan, giúp việc xóa người chơi trở nên an toàn và sạch sẽ.
- **Backup**: Luôn sao lưu dữ liệu trước khi thực hiện các thay đổi lớn về cấu trúc bảng.

## Thông tin dự án
- **Hệ quản trị CSDL**: MySQL
- **Kết nối Java**: `jdbc:mysql://...` (Sử dụng Driver: `com.mysql.cj.jdbc.Driver`)
