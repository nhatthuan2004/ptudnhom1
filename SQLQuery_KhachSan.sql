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
    loaiPhong VARCHAR(100),
	trangThai VARCHAR(50),
	dondep VARCHAR(50),
    giaPhong FLOAT,
    soNguoiToiDa INT,
    moTa TEXT
);

CREATE TABLE PhieuDatPhong (
    maDatPhong VARCHAR(50) PRIMARY KEY,
    ngayDen DATE,
    ngayDi DATE,
    ngayDat DATE,
    soLuongNguoi INT,
    trangThai VARCHAR(50),
    maKhachHang VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang)
);

CREATE TABLE ChitietPhieuDatPhong (
    maPhong VARCHAR(50) ,
    maDatPhong VARCHAR(50) ,
    thanhTien FLOAT,
    giaPhong FLOAT,
	soluong INT,
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
    tenChuongTrinhKhuyenMai VARCHAR(255) NOT NULL,
    chietKhau FLOAT,
    moTaChuongTrinhKhuyenMai TEXT,
    trangThai BIT
);

CREATE TABLE PhieuDichVu (
    maPhieuDichVu VARCHAR(50) PRIMARY KEY,
    maHoaDon VARCHAR(50),
    ngaySuDung DATE,
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon)
);

CREATE TABLE DichVu (
    maDichVu VARCHAR(50) PRIMARY KEY,
    tenDichVu VARCHAR(255) NOT NULL,
    gia FLOAT,
    moTa TEXT
);

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

