package model;

import javafx.beans.property.*;

public class ChitietHoaDon {
    private final IntegerProperty thueVat = new SimpleIntegerProperty();
    private final StringProperty hinhThuc = new SimpleStringProperty();
    private final StringProperty dichVu = new SimpleStringProperty();
    private final IntegerProperty soNgay = new SimpleIntegerProperty();
    private final DoubleProperty khuyenMai = new SimpleDoubleProperty();
    private final DoubleProperty gia = new SimpleDoubleProperty();
    private final StringProperty maPhong = new SimpleStringProperty();
    private final StringProperty maHoaDon = new SimpleStringProperty();
    private final StringProperty maChuongTrinhKhuyenMai = new SimpleStringProperty();
    private final StringProperty maDatPhong = new SimpleStringProperty();
    private final StringProperty maPhieuDichVu = new SimpleStringProperty();
    private Phong phong;
    private HoaDon hoaDon;
    private ChuongTrinhKhuyenMai chuongTrinhKhuyenMai;
    private PhieuDatPhong phieuDatPhong;
    private PhieuDichVu phieuDichVu;

    public ChitietHoaDon() {}

    public ChitietHoaDon(int thueVat, String hinhThuc, String dichVu, int soNgay, double khuyenMai, double gia, 
                         String maPhong, String maHoaDon, String maChuongTrinhKhuyenMai, String maDatPhong, String maPhieuDichVu) {
        setThueVat(thueVat);
        setHinhThuc(hinhThuc);
        setDichVu(dichVu);
        setSoNgay(soNgay);
        setKhuyenMai(khuyenMai);
        setGia(gia);
        setMaPhong(maPhong);
        setMaHoaDon(maHoaDon);
        setMaChuongTrinhKhuyenMai(maChuongTrinhKhuyenMai);
        setMaDatPhong(maDatPhong);
        setMaPhieuDichVu(maPhieuDichVu);
    }

    public int getThueVat() { return thueVat.get(); }
    public IntegerProperty thueVatProperty() { return thueVat; }
    public void setThueVat(int thueVat) { 
        this.thueVat.set(thueVat >= 0 && thueVat <= 100 ? thueVat : 0); 
    }

    public String getHinhThuc() { return hinhThuc.get(); }
    public StringProperty hinhThucProperty() { return hinhThuc; }
    public void setHinhThuc(String hinhThuc) { 
        this.hinhThuc.set(hinhThuc != null ? hinhThuc : "Tiền mặt"); 
    }

    public String getDichVu() { return dichVu.get(); }
    public StringProperty dichVuProperty() { return dichVu; }
    public void setDichVu(String dichVu) { 
        this.dichVu.set(dichVu != null ? dichVu : ""); 
    }

    public int getSoNgay() { return soNgay.get(); }
    public IntegerProperty soNgayProperty() { return soNgay; }
    public void setSoNgay(int soNgay) { 
        this.soNgay.set(soNgay >= 0 ? soNgay : 0); 
    }

    public double getKhuyenMai() { return khuyenMai.get(); }
    public DoubleProperty khuyenMaiProperty() { return khuyenMai; }
    public void setKhuyenMai(double khuyenMai) { 
        this.khuyenMai.set(khuyenMai >= 0 && khuyenMai <= 100 ? khuyenMai : 0); 
    }

    public double getGia() { return gia.get(); }
    public DoubleProperty giaProperty() { return gia; }
    public void setGia(double gia) { 
        this.gia.set(gia >= 0 ? gia : 0); 
    }

    public String getMaPhong() { return maPhong.get(); }
    public StringProperty maPhongProperty() { return maPhong; }
    public void setMaPhong(String maPhong) { 
        this.maPhong.set(maPhong != null ? maPhong : ""); 
    }

    public String getMaHoaDon() { return maHoaDon.get(); }
    public StringProperty maHoaDonProperty() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { 
        this.maHoaDon.set(maHoaDon != null ? maHoaDon : ""); 
    }

    public String getMaChuongTrinhKhuyenMai() { return maChuongTrinhKhuyenMai.get(); }
    public StringProperty maChuongTrinhKhuyenMaiProperty() { return maChuongTrinhKhuyenMai; }
    public void setMaChuongTrinhKhuyenMai(String maChuongTrinhKhuyenMai) { 
        this.maChuongTrinhKhuyenMai.set(maChuongTrinhKhuyenMai != null ? maChuongTrinhKhuyenMai : ""); 
    }

    public String getMaDatPhong() { return maDatPhong.get(); }
    public StringProperty maDatPhongProperty() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { 
        this.maDatPhong.set(maDatPhong != null ? maDatPhong : ""); 
    }

    public String getMaPhieuDichVu() { return maPhieuDichVu.get(); }
    public StringProperty maPhieuDichVuProperty() { return maPhieuDichVu; }
    public void setMaPhieuDichVu(String maPhieuDichVu) { 
        this.maPhieuDichVu.set(maPhieuDichVu != null ? maPhieuDichVu : ""); 
    }

    public Phong getPhong() { return phong; }
    public void setPhong(Phong phong) { this.phong = phong; }

    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }

    public ChuongTrinhKhuyenMai getChuongTrinhKhuyenMai() { return chuongTrinhKhuyenMai; }
    public void setChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) { this.chuongTrinhKhuyenMai = chuongTrinhKhuyenMai; }

    public PhieuDatPhong getPhieuDatPhong() { return phieuDatPhong; }
    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) { this.phieuDatPhong = phieuDatPhong; }

    public PhieuDichVu getPhieuDichVu() { return phieuDichVu; }
    public void setPhieuDichVu(PhieuDichVu phieuDichVu) { this.phieuDichVu = phieuDichVu; }
}