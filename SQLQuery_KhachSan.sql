CREATE DATABASE Khachsan;
GO
USE Khachsan;
GO

CREATE TABLE KhachHang (
    maKhachHang VARCHAR(50) PRIMARY KEY,
    tenKhachHang NVARCHAR(255) NOT NULL,     
    cccd VARCHAR(50),	
    soDienThoai VARCHAR(20),
	email VARCHAR(50),
    diaChi NVARCHAR(255),                    
    quoctich NVARCHAR(50),                 
    gioitinh NVARCHAR(10),
    ngaySinh DATE
);


INSERT INTO KhachHang (maKhachHang, tenKhachHang, cccd, soDienThoai, diaChi, quoctich, gioitinh, ngaySinh) VALUES
('KH001', N'Nguyễn Văn A', '123456789', '0901234567', N'Hà Nội', N'Việt Nam',  N'Nam', CONVERT(DATE, '01/01/1990', 103)),
('KH002', N'Trần Thị B', '987654321', '0912345678', N'Hồ Chí Minh', N'Việt Nam', N'Nữ', CONVERT(DATE, '15/02/1992', 103)),
('KH003', N'Lê Văn C', '234567891', '0923456789', N'Đà Nẵng', N'Việt Nam',  N'Nam', CONVERT(DATE, '20/03/1988', 103)),
('KH004', N'Phạm Thị D', '345678912', '0934567890', N'Cần Thơ', N'Việt Nam', N'Nữ', CONVERT(DATE, '10/04/1995', 103)),
('KH005', N'Hoàng Văn E', '456789123', '0945678901', N'Hải Phòng', N'Việt Nam',  N'Nam', CONVERT(DATE, '25/05/1991', 103)),
('KH006', N'Võ Thị F', '567891234', '0956789012', N'Nha Trang', N'Việt Nam', N'Nữ', CONVERT(DATE, '30/06/1989', 103)),
('KH007', N'Đỗ Văn G', '678912345', '0967890123', N'Bình Dương', N'Việt Nam',  N'Nam', CONVERT(DATE, '12/07/1993', 103)),
('KH008', N'Bùi Thị H', '789123456', '0978901234', N'Biên Hòa', N'Việt Nam', N'Nữ', CONVERT(DATE, '18/08/1990', 103)),
('KH009', N'Ngô Văn I', '891234567', '0989012345', N'Huế', N'Việt Nam',  N'Nam', CONVERT(DATE, '22/09/1994', 103)),
('KH010', N'Đặng Thị K', '912345678', '0990123456', N'Vũng Tàu', N'Việt Nam', N'Nữ', CONVERT(DATE, '05/10/1996', 103));



CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(255) NOT NULL,
);

CREATE TABLE NhanVien (
    maNhanVien VARCHAR(50) PRIMARY KEY,
    tenNhanVien VARCHAR(255) NOT NULL,
    soDienThoai VARCHAR(20),
    gioiTinh BIT,
    diaChi TEXT,
    chucVu VARCHAR(100),
    luong FLOAT,
    tenDangNhap VARCHAR(50) UNIQUE,
    FOREIGN KEY (tenDangNhap) REFERENCES TaiKhoan(tenDangNhap)
);

CREATE TABLE Phong (
    maPhong VARCHAR(50) PRIMARY KEY,
    loaiPhong NVARCHAR(50),
	giaPhong FLOAT,
	trangThai NVARCHAR(50),
	dondep NVARCHAR(50),
    vitri NVARCHAR(50),
    soNguoiToiDa INT,
    moTa NVARCHAR(225),
);
INSERT INTO Phong (maPhong, loaiPhong, giaPhong, trangThai, dondep, vitri, soNguoiToiDa, moTa) VALUES
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



CREATE TABLE PhieuDatPhong (
    maDatPhong VARCHAR(50) PRIMARY KEY,
    ngayDen DATE,
    ngayDi DATE,
	ngayDat DATE,
    soLuongNguoi INT,
    trangThai NVARCHAR(50),
    maKhachHang VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang)
);

CREATE TABLE ChitietPhieuDatPhong (
    maDatPhong VARCHAR(50) ,
	maPhong VARCHAR(50) ,
	mota NVARCHAR(250),
	TrangThai NVARCHAR(50),
	TienPhong FLOAT,
	ThanhTien FLOAT,
	soLuong INT NOT NULL,
	PRIMARY KEY (maPhong, maDatPhong),
    FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    FOREIGN KEY (maDatPhong) REFERENCES PhieuDatPhong(maDatPhong)
);

CREATE TABLE HoaDon (
    maHoaDon VARCHAR(50) PRIMARY KEY,
    ngayLap DATETIME,
    hinhThucThanhToan VARCHAR(100),
    trangThai BIT,
    maKhachHang VARCHAR(50),
    maNhanVien VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);

CREATE TABLE ChuongTrinhKhuyenMai (
    maChuongTrinhKhuyenMai VARCHAR(50) PRIMARY KEY,
    tenChuongTrinhKhuyenMai NVARCHAR(255) NOT NULL,
    chietKhau FLOAT,
    moTaChuongTrinhKhuyenMai NVARCHAR(255),
    trangThai BIT
);

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


CREATE TABLE PhieuDichVu (
    maPhieuDichVu VARCHAR(50) PRIMARY KEY,
    maHoaDon VARCHAR(50),
    ngaySuDung DATE,
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon)
);

CREATE TABLE DichVu (
    maDichVu VARCHAR(50) PRIMARY KEY,
    tenDichVu NVARCHAR(255) NOT NULL,
	moTa  NVARCHAR(255),
	gia FLOAT,
    trangthai NVARCHAR(50),
);

INSERT INTO DichVu (maDichVu, tenDichVu, moTa, gia, trangthai) VALUES
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



CREATE TABLE ChitietPhieuDichVu (
    maPhieuDichVu VARCHAR(50) ,
    maDichVu VARCHAR(50) ,
    soLuong INT,
    donGia FLOAT,
	PRIMARY KEY (maPhieuDichVu, maDichVu),
    FOREIGN KEY (maPhieuDichVu) REFERENCES PhieuDichVu(maPhieuDichVu),
    FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);

CREATE TABLE ChitietHoaDon (
    thueVat INT,
    hinhThuc VARCHAR(100),
    dichVu VARCHAR(100),	 
    soNgay INT,
    khuyenMai FLOAT,
    gia FLOAT,
    maPhong VARCHAR(50),
    maHoaDon VARCHAR(50),
    maChuongTrinhKhuyenMai VARCHAR(50),
    maDatPhong VARCHAR(50),
    maPhieuDichVu VARCHAR(50),
    PRIMARY KEY (maHoaDon, maPhong),  
    FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maChuongTrinhKhuyenMai) REFERENCES ChuongTrinhKhuyenMai(maChuongTrinhKhuyenMai),
    FOREIGN KEY (maDatPhong) REFERENCES PhieuDatPhong(maDatPhong),
    FOREIGN KEY (maPhieuDichVu) REFERENCES PhieuDichVu(maPhieuDichVu)
);

