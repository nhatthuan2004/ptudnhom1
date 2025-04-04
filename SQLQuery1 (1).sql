CREATE DATABASE HotelManagement;
GO
USE HotelManagement;
GO

CREATE TABLE KhachHang (
    maKhachHang VARCHAR(50) PRIMARY KEY,
    tenKhachHang VARCHAR(255) NOT NULL,
    soDienThoai VARCHAR(20),
    email VARCHAR(255),
    loaiKhachHang BIT,
    diaChi TEXT,
    cccd VARCHAR(50),
    ngaySinh DATE
);

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
    giaPhong FLOAT,
    trangThai VARCHAR(50),
    viTri TEXT,
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

