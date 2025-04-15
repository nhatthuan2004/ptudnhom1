-- Database Creation and Initial Setup
CREATE DATABASE Khachsan;
GO
USE Khachsan;
GO

-- Bảng KhachHang
CREATE TABLE KhachHang (
    maKhachHang NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS PRIMARY KEY,
    tenKhachHang NVARCHAR(255) NOT NULL,
    cccd NVARCHAR(50),
    soDienThoai NVARCHAR(20),
    email NVARCHAR(50),
    diaChi NVARCHAR(255),
    quoctich NVARCHAR(50),
    gioitinh NVARCHAR(10),
    ngaySinh DATE
);
GO

-- Bảng NhanVien
CREATE TABLE NhanVien (
    maNhanVien NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS PRIMARY KEY,
    tenNhanVien NVARCHAR(255) NOT NULL,
    soDienThoai NVARCHAR(20),
    gioiTinh BIT,
    diaChi NVARCHAR(255),
    chucVu NVARCHAR(100),
    luong FLOAT
);
GO

-- Bảng TaiKhoan
CREATE TABLE TaiKhoan (
    tenDangNhap NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS PRIMARY KEY,
    matKhau NVARCHAR(255) NOT NULL,
    maNhanVien NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS UNIQUE,
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);
GO

-- Bảng Phong
CREATE TABLE Phong (
    maPhong NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS PRIMARY KEY,
    loaiPhong NVARCHAR(50) NOT NULL,
    giaPhong FLOAT NOT NULL,
    trangThai NVARCHAR(50) NOT NULL,
    donDep NVARCHAR(50),
    viTri NVARCHAR(50),
    soNguoiToiDa INT NOT NULL,
    moTa NVARCHAR(255)
);
GO

-- Bảng HoaDon
CREATE TABLE HoaDon (
    maHoaDon NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS PRIMARY KEY,
    ngayLap DATETIME NOT NULL,
    hinhThucThanhToan NVARCHAR(50) NOT NULL,
    tongTien FLOAT NOT NULL DEFAULT 0,
    trangThai BIT NOT NULL,
    maKhachHang NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
    maNhanVien NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);
GO

-- Bảng PhieuDatPhong
CREATE TABLE PhieuDatPhong (
    maDatPhong NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS PRIMARY KEY,
    ngayDen DATE,
    ngayDi DATE,
    ngayDat DATE,
    soLuongNguoi INT,
    trangThai NVARCHAR(50),
    maKhachHang NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    maHoaDon NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    CONSTRAINT FK_PhieuDatPhong_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    CONSTRAINT FK_PhieuDatPhong_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon)
);
GO

-- Bảng ChitietPhieuDatPhong
CREATE TABLE ChitietPhieuDatPhong (
    maDatPhong NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    maPhong NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    moTa NVARCHAR(250),
    trangThai NVARCHAR(50),
    tienPhong FLOAT,
    tienDichVu FLOAT DEFAULT 0,
    thanhTien FLOAT,
    soLuong INT NOT NULL,
    daThanhToan BIT DEFAULT 0,
    PRIMARY KEY (maPhong, maDatPhong),
    CONSTRAINT FK_ChitietPhieuDatPhong_Phong FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    CONSTRAINT FK_ChitietPhieuDatPhong_PhieuDatPhong FOREIGN KEY (maDatPhong) REFERENCES PhieuDatPhong(maDatPhong)
);
GO

-- Bảng ChuongTrinhKhuyenMai
CREATE TABLE ChuongTrinhKhuyenMai (
    maChuongTrinhKhuyenMai NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS PRIMARY KEY,
    tenChuongTrinhKhuyenMai NVARCHAR(255) NOT NULL,
    chietKhau FLOAT,
    moTaChuongTrinhKhuyenMai NVARCHAR(255),
    trangThai BIT
);
GO

-- Bảng DichVu
CREATE TABLE DichVu (
    maDichVu NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS PRIMARY KEY,
    tenDichVu NVARCHAR(255) NOT NULL,
    moTa NVARCHAR(255),
    gia FLOAT,
    trangThai NVARCHAR(50)
);
GO

-- Bảng PhieuDichVu
CREATE TABLE PhieuDichVu (
    maPhieuDichVu NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS PRIMARY KEY,
    maHoaDon NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    ngaySuDung DATE,
    CONSTRAINT FK_PhieuDichVu_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon)
);
GO

-- Bảng ChitietPhieuDichVu
CREATE TABLE ChitietPhieuDichVu (
    maPhieuDichVu NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    maDichVu NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    soLuong INT,
    donGia FLOAT,
    PRIMARY KEY (maPhieuDichVu, maDichVu),
    CONSTRAINT FK_ChitietPhieuDichVu_PhieuDichVu FOREIGN KEY (maPhieuDichVu) REFERENCES PhieuDichVu(maPhieuDichVu),
    CONSTRAINT FK_ChitietPhieuDichVu_DichVu FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);
GO

-- Bảng ChitietHoaDon
CREATE TABLE ChitietHoaDon (
    id INT IDENTITY(1,1) PRIMARY KEY,  -- ✅ Khóa chính duy nhất
    maHoaDon NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
    maPhong NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
    thueVat FLOAT,
    soNgay INT,
    khuyenMai FLOAT,
    tienPhong FLOAT,
    tienDichVu FLOAT,
    maChuongTrinhKhuyenMai NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    maDatPhong NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    maPhieuDichVu NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
    
    -- KHÓA NGOẠI GIỮ NGUYÊN
    CONSTRAINT FK_ChitietHoaDon_Phong FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    CONSTRAINT FK_ChitietHoaDon_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    CONSTRAINT FK_ChitietHoaDon_ChuongTrinhKhuyenMai FOREIGN KEY (maChuongTrinhKhuyenMai) REFERENCES ChuongTrinhKhuyenMai(maChuongTrinhKhuyenMai),
    CONSTRAINT FK_ChitietHoaDon_PhieuDatPhong FOREIGN KEY (maDatPhong) REFERENCES PhieuDatPhong(maDatPhong),
    CONSTRAINT FK_ChitietHoaDon_PhieuDichVu FOREIGN KEY (maPhieuDichVu) REFERENCES PhieuDichVu(maPhieuDichVu)
);

GO
ALTER TABLE PhieuDichVu ADD ngayTao DATE NULL;

-- Bảng LichSuChuyenPhong
CREATE TABLE LichSuChuyenPhong (
    maLichSu INT IDENTITY(1,1) PRIMARY KEY,
    maDatPhong NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
    maPhongCu NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
    maPhongMoi NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
    ngayThayDoi DATETIME NOT NULL DEFAULT GETDATE(),
    liDo NVARCHAR(255),
    maNhanVien NVARCHAR(50) COLLATE SQL_Latin1_General_CP1_CI_AS,
    CONSTRAINT FK_LichSuChuyenPhong_PhieuDatPhong FOREIGN KEY (maDatPhong) REFERENCES PhieuDatPhong(maDatPhong),
    CONSTRAINT FK_LichSuChuyenPhong_PhongCu FOREIGN KEY (maPhongCu) REFERENCES Phong(maPhong),
    CONSTRAINT FK_LichSuChuyenPhong_PhongMoi FOREIGN KEY (maPhongMoi) REFERENCES Phong(maPhong),
    CONSTRAINT FK_LichSuChuyenPhong_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);
GO

-- Stored Procedure to Change Room
CREATE PROCEDURE sp_ChuyenPhong
    @maDatPhong NVARCHAR(50),
    @maPhongCu NVARCHAR(50),
    @maPhongMoi NVARCHAR(50),
    @liDo NVARCHAR(255),
    @maNhanVien NVARCHAR(50)
AS
BEGIN
    BEGIN TRANSACTION;
    BEGIN TRY
        -- Check if new room is available
        IF NOT EXISTS (SELECT 1 FROM Phong WHERE maPhong = @maPhongMoi AND trangThai = N'Trống')
        BEGIN
            RAISERROR (N'Phòng mới không khả dụng.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Get existing booking details
        DECLARE @moTa NVARCHAR(250), @trangThai NVARCHAR(50), @tienPhong FLOAT, 
                @tienDichVu FLOAT, @thanhTien FLOAT, @soLuong INT, @daThanhToan BIT;
        SELECT @moTa = moTa, @trangThai = trangThai, @tienPhong = tienPhong, 
               @tienDichVu = tienDichVu, @thanhTien = thanhTien, @soLuong = soLuong, 
               @daThanhToan = daThanhToan
        FROM ChitietPhieuDatPhong
        WHERE maDatPhong = @maDatPhong AND maPhong = @maPhongCu;

        -- Get new room price
        DECLARE @giaPhongMoi FLOAT;
        SELECT @giaPhongMoi = giaPhong FROM Phong WHERE maPhong = @maPhongMoi;

        -- Calculate new total
        DECLARE @thanhTienMoi FLOAT = @giaPhongMoi * @soLuong + @tienDichVu;

        -- Update room status
        UPDATE Phong SET trangThai = N'Trống', donDep = N'Dirty' WHERE maPhong = @maPhongCu;
        UPDATE Phong SET trangThai = N'Đã đặt' WHERE maPhong = @maPhongMoi;

        -- Delete old record
        DELETE FROM ChitietPhieuDatPhong WHERE maDatPhong = @maDatPhong AND maPhong = @maPhongCu;

        -- Insert new record
        INSERT INTO ChitietPhieuDatPhong 
            (maDatPhong, maPhong, moTa, trangThai, tienPhong, tienDichVu, thanhTien, soLuong, daThanhToan)
        VALUES 
            (@maDatPhong, @maPhongMoi, 
             ISNULL(@moTa, '') + N' | Đổi từ phòng ' + @maPhongCu + N' sang ' + @maPhongMoi,
             @trangThai, @giaPhongMoi, @tienDichVu, @thanhTienMoi, @soLuong, @daThanhToan);

        -- Record change history
        INSERT INTO LichSuChuyenPhong 
            (maDatPhong, maPhongCu, maPhongMoi, ngayThayDoi, liDo, maNhanVien)
        VALUES 
            (@maDatPhong, @maPhongCu, @maPhongMoi, GETDATE(), @liDo, @maNhanVien);

        -- Update ChitietHoaDon if exists
        UPDATE ChitietHoaDon
        SET maPhong = @maPhongMoi,
            tienPhong = @giaPhongMoi
        WHERE maDatPhong = @maDatPhong AND maPhong = @maPhongCu;

        -- Update HoaDon total
        UPDATE HoaDon
        SET tongTien = (
            SELECT SUM(thanhTien)
            FROM ChitietPhieuDatPhong cpd
            JOIN PhieuDatPhong pdp ON cpd.maDatPhong = pdp.maDatPhong
            WHERE pdp.maHoaDon = HoaDon.maHoaDon
        )
        WHERE maHoaDon IN (
            SELECT maHoaDon
            FROM PhieuDatPhong
            WHERE maDatPhong = @maDatPhong
        );

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrorMessage, 16, 1);
    END CATCH
END;
GO

-- Stored Procedure to Process Payment
CREATE PROCEDURE sp_ThanhToanTuPhieuDat
    @maDatPhong NVARCHAR(50),
    @maPhong NVARCHAR(50),
    @maNhanVien NVARCHAR(50),
    @hinhThucThanhToan NVARCHAR(50),
    @maChuongTrinhKhuyenMai NVARCHAR(50) = NULL
AS
BEGIN
    BEGIN TRANSACTION;
    BEGIN TRY
        DECLARE @maKhachHang NVARCHAR(50);
        DECLARE @tienPhong FLOAT;
        DECLARE @tienDichVu FLOAT;
        DECLARE @soNgay INT;
        DECLARE @maHoaDon NVARCHAR(50);
        DECLARE @thueVat FLOAT = 0.1; -- 10% VAT
        DECLARE @khuyenMai FLOAT = 0;
        DECLARE @tongTien FLOAT;

        -- Get customer ID and stay duration
        SELECT @maKhachHang = maKhachHang,
               @soNgay = DATEDIFF(DAY, ngayDen, ngayDi)
        FROM PhieuDatPhong
        WHERE maDatPhong = @maDatPhong;

        IF @soNgay = 0 SET @soNgay = 1;

        -- Get room and service costs
        SELECT @tienPhong = tienPhong, @tienDichVu = tienDichVu
        FROM ChitietPhieuDatPhong
        WHERE maDatPhong = @maDatPhong AND maPhong = @maPhong;

        -- Get additional service fees
        DECLARE @tienDichVuThem FLOAT = 0;
        SELECT @tienDichVuThem = ISNULL(SUM(pdv.soLuong * pdv.donGia), 0)
        FROM PhieuDichVu pv
        JOIN ChitietPhieuDichVu pdv ON pv.maPhieuDichVu = pdv.maPhieuDichVu
        JOIN PhieuDatPhong pdp ON pdp.maDatPhong = @maDatPhong
        WHERE pv.maHoaDon IS NULL 
          AND pv.ngaySuDung BETWEEN pdp.ngayDen AND pdp.ngayDi;

        SET @tienDichVu = @tienDichVu + @tienDichVuThem;

        -- Get discount if applicable
        IF @maChuongTrinhKhuyenMai IS NOT NULL
        BEGIN
            SELECT @khuyenMai = chietKhau
            FROM ChuongTrinhKhuyenMai
            WHERE maChuongTrinhKhuyenMai = @maChuongTrinhKhuyenMai
              AND trangThai = 1;
        END

        -- Calculate total
        SET @tongTien = (@tienPhong * @soNgay * (1 + @thueVat)) - 
                        (@tienPhong * @soNgay * (@khuyenMai / 100)) + @tienDichVu;

        -- Generate invoice ID
        SET @maHoaDon = 'HD' + FORMAT(GETDATE(), 'yyyyMMddHHmmss');

        -- Create invoice
        INSERT INTO HoaDon 
            (maHoaDon, ngayLap, hinhThucThanhToan, tongTien, trangThai, maKhachHang, maNhanVien)
        VALUES 
            (@maHoaDon, GETDATE(), @hinhThucThanhToan, @tongTien, 1, @maKhachHang, @maNhanVien);

        -- Link services to invoice
        UPDATE PhieuDichVu
        SET maHoaDon = @maHoaDon
        FROM PhieuDichVu pv
        JOIN PhieuDatPhong pdp ON pdp.maDatPhong = @maDatPhong
        WHERE pv.maHoaDon IS NULL
          AND pv.ngaySuDung BETWEEN pdp.ngayDen AND pdp.ngayDi;

        -- Get service ticket ID
        DECLARE @maPhieuDichVu NVARCHAR(50);
        SELECT TOP 1 @maPhieuDichVu = pv.maPhieuDichVu
        FROM PhieuDichVu pv
        WHERE pv.maHoaDon = @maHoaDon;

        -- Create invoice details
        INSERT INTO ChitietHoaDon 
            (maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, tienDichVu, 
             maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu)
        VALUES 
            (@maHoaDon, @maPhong, @thueVat * 100, @soNgay, @khuyenMai, 
             @tienPhong * @soNgay, @tienDichVu, 
             @maChuongTrinhKhuyenMai, @maDatPhong, @maPhieuDichVu);

        -- Update payment status
        UPDATE ChitietPhieuDatPhong
        SET daThanhToan = 1,
            tienDichVu = @tienDichVu,
            thanhTien = (@tienPhong * @soNgay) + @tienDichVu,
            trangThai = N'Đã thanh toán'
        WHERE maDatPhong = @maDatPhong AND maPhong = @maPhong;

        -- Update booking
        UPDATE PhieuDatPhong
        SET maHoaDon = @maHoaDon, 
            trangThai = N'Đã thanh toán'
        WHERE maDatPhong = @maDatPhong;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR (@ErrorMessage, 16, 1);
    END CATCH
END;
GO

-- View for Booking Details
CREATE VIEW vw_ChiTietDatPhong AS
SELECT 
    ct.maDatPhong,
    ct.maPhong,
    p.loaiPhong,
    p.giaPhong,
    pdp.ngayDen,
    pdp.ngayDi,
    DATEDIFF(DAY, pdp.ngayDen, pdp.ngayDi) AS soNgayLuuTru,
    ct.soLuong,
    ct.tienPhong,
    ct.tienDichVu,
    ct.thanhTien,
    ct.trangThai,
    CASE WHEN ct.daThanhToan = 1 THEN N'Đã thanh toán' ELSE N'Chưa thanh toán' END AS trangThaiThanhToan,
    kh.maKhachHang,
    kh.tenKhachHang,
    kh.soDienThoai,
    pdp.maHoaDon
FROM ChitietPhieuDatPhong ct
JOIN PhieuDatPhong pdp ON ct.maDatPhong = pdp.maDatPhong
JOIN Phong p ON ct.maPhong = p.maPhong
JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang;
GO

-- View for Invoice Details
CREATE VIEW vw_ChiTietHoaDon AS
SELECT 
    hd.maHoaDon,
    hd.ngayLap,
    hd.hinhThucThanhToan,
    hd.tongTien,
    CASE WHEN hd.trangThai = 1 THEN N'Đã thanh toán' ELSE N'Chưa thanh toán' END AS trangThai,
    ct.maPhong,
    p.loaiPhong,
    ct.soNgay,
    ct.tienPhong,
    ct.tienDichVu,
    ct.thueVat,
    ct.khuyenMai,
    km.tenChuongTrinhKhuyenMai,
    kh.maKhachHang,
    kh.tenKhachHang,
    nv.maNhanVien,
    nv.tenNhanVien
FROM HoaDon hd
JOIN ChitietHoaDon ct ON hd.maHoaDon = ct.maHoaDon
JOIN Phong p ON ct.maPhong = p.maPhong
JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang
JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien
LEFT JOIN ChuongTrinhKhuyenMai km ON ct.maChuongTrinhKhuyenMai = km.maChuongTrinhKhuyenMai;
GO

-- Insert Sample Data
INSERT INTO KhachHang (maKhachHang, tenKhachHang, cccd, soDienThoai, diaChi, quoctich, gioitinh, ngaySinh) VALUES
('KH001', N'Nguyễn Văn A', '123456789', '0901234567', N'Hà Nội', N'Việt Nam', N'Nam', CONVERT(DATE, '01/01/1990', 103)),
('KH002', N'Trần Thị B', '987654321', '0912345678', N'Hồ Chí Minh', N'Việt Nam', N'Nữ', CONVERT(DATE, '15/02/1992', 103)),
('KH003', N'Lê Văn C', '234567891', '0923456789', N'Đà Nẵng', N'Việt Nam', N'Nam', CONVERT(DATE, '20/03/1988', 103)),
('KH004', N'Phạm Thị D', '345678912', '0934567890', N'Cần Thơ', N'Việt Nam', N'Nữ', CONVERT(DATE, '10/04/1995', 103)),
('KH005', N'Hoàng Văn E', '456789123', '0945678901', N'Hải Phòng', N'Việt Nam', N'Nam', CONVERT(DATE, '25/05/1991', 103)),
('KH006', N'Võ Thị F', '567891234', '0956789012', N'Nha Trang', N'Việt Nam', N'Nữ', CONVERT(DATE, '30/06/1989', 103)),
('KH007', N'Đỗ Văn G', '678912345', '0967890123', N'Bình Dương', N'Việt Nam', N'Nam', CONVERT(DATE, '12/07/1993', 103)),
('KH008', N'Bùi Thị H', '789123456', '0978901234', N'Biên Hòa', N'Việt Nam', N'Nữ', CONVERT(DATE, '18/08/1990', 103)),
('KH009', N'Ngô Văn I', '891234567', '0989012345', N'Huế', N'Việt Nam', N'Nam', CONVERT(DATE, '22/09/1994', 103)),
('KH010', N'Đặng Thị K', '912345678', '0990123456', N'Vũng Tàu', N'Việt Nam', N'Nữ', CONVERT(DATE, '05/10/1996', 103));
GO

INSERT INTO NhanVien (maNhanVien, tenNhanVien, soDienThoai, gioiTinh, diaChi, chucVu, luong)
VALUES ('NV011', 'Nguyen Van Quan Ly', '0987654321', 1, 'Ha Noi', 'Quan ly', 20000000);
GO

INSERT INTO TaiKhoan (tenDangNhap, matKhau, maNhanVien)
VALUES ('quanly01', 'QuanLy@2025', 'NV011');
GO

INSERT INTO Phong (maPhong, loaiPhong, giaPhong, trangThai, donDep, viTri, soNguoiToiDa, moTa) VALUES
('P001', N'Đôi', 1200000, N'Trống', 'Clean', N'Tầng 1 - Khu A', 2, N'Phòng rộng rãi, có ban công.'),
('P002', N'Đơn', 800000, N'Đã đặt', 'Dirty', N'Tầng 2 - Khu B', 2, N'Phòng đơn, có cửa sổ.'),
('P003', N'Vip', 2000000, N'Trống', 'Clean', N'Tầng 3 - Khu VIP', 4, N'Phòng cao cấp, có bồn tắm.'),
('P004', N'Đơn', 850000, N'Trống', 'Clean', N'Tầng 1 - Khu B', 2, N'Phòng đơn, view sân vườn.'),
('P005', N'Đôi', 1300000, N'Đã đặt', 'Dirty', N'Tầng 2 - Khu A', 3, N'Phòng gia đình nhỏ.'),
('P006', N'Vip', 2200000, N'Trống', 'Clean', N'Tầng 4 - Khu VIP', 4, N'Phòng VIP, có minibar.'),
('P007', N'Đơn', 780000, N'Bảo trì', 'Dirty', N'Tầng G - Sảnh chính', 2, N'Đang bảo trì, không sử dụng.'),
('P008', N'Đôi', 1250000, N'Trống', 'Clean', N'Tầng 3 - Khu A', 3, N'Phòng góc, ánh sáng tự nhiên.'),
('P009', N'Đơn', 790000, N'Trống', 'Clean', N'Tầng 1 - Khu C', 2, N'Phòng tiện nghi, giá rẻ.'),
('P010', N'Vip', 2100000, N'Đã đặt', 'Dirty', N'Tầng 5 - Khu VIP', 4, N'Phòng rộng, phù hợp cho gia đình.');
GO

INSERT INTO ChuongTrinhKhuyenMai (maChuongTrinhKhuyenMai, tenChuongTrinhKhuyenMai, chietKhau, moTaChuongTrinhKhuyenMai, trangThai) VALUES
('KM001', N'Giảm giá cuối tuần', 10.0, N'Áp dụng cho khách lưu trú vào Thứ 7, CN', 1),
('KM002', N'Khuyến mãi lễ 30/4', 15.0, N'Áp dụng từ 28/4 đến 2/5', 1),
('KM003', N'Ưu đãi thành viên', 5.0, N'Chỉ dành cho khách hàng thân thiết', 1),
('KM004', N'Giảm giá mùa hè', 20.0, N'Khuyến mãi lớn trong tháng 6', 0),
('KM005', N'Miễn phí nâng cấp phòng', 0.0, N'Khách đặt phòng đôi được nâng cấp lên VIP', 1),
('KM006', N'Giảm giá đặt sớm', 12.0, N'Đặt trước 7 ngày để nhận ưu đãi', 1),
('KM007', N'Tặng phiếu buffet sáng', 0.0, N'Dành cho phòng VIP', 1),
('KM008', N'Giảm giá combo phòng & dịch vụ', 18.0, N'Khi sử dụng kèm dịch vụ spa', 0),
('KM009', N'Khuyến mãi Tết Nguyên Đán', 25.0, N'Áp dụng trong dịp Tết', 1),
('KM010', N'Giảm giá Black Friday', 30.0, N'Áp dụng duy nhất ngày 29/11', 0);
GO

INSERT INTO DichVu (maDichVu, tenDichVu, moTa, gia, trangThai) VALUES
('DV001', N'Giặt ủi', N'Dịch vụ giặt và ủi quần áo cho khách', 50000, N'Hoạt động'),
('DV002', N'Dọn phòng', N'Dọn dẹp và vệ sinh phòng hàng ngày', 0, N'Hoạt động'),
('DV003', N'Ăn sáng', N'Phục vụ buffet sáng tại nhà hàng', 150000, N'Hoạt động'),
('DV004', N'Spa', N'Massage thư giãn và chăm sóc da', 300000, N'Tạm ngưng'),
('DV005', N'Đưa đón sân bay', N'Dịch vụ đưa đón khách tại sân bay', 250000, N'Hoạt động'),
('DV006', N'Thuê xe máy', N'Cho thuê xe máy theo ngày', 120000, N'Hoạt động'),
('DV007', N'Bơi lội', N'Sử dụng hồ bơi miễn phí cho khách lưu trú', 0, N'Hoạt động'),
('DV008', N'Gọi đồ ăn', N'Phục vụ món ăn tại phòng', 100000, N'Hoạt động'),
('DV009', N'Thức uống minibar', N'Nước ngọt, bia, rượu trong tủ lạnh mini', 80000, N'Hoạt động'),
('DV010', N'Giữ hành lý', N'Giữ hành lý miễn phí sau khi trả phòng', 0, N'Hoạt động');
GO
