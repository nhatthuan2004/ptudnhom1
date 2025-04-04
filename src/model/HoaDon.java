package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDon {
    private final StringProperty maHoaDon = new SimpleStringProperty();
    private final StringProperty tenKhachHang = new SimpleStringProperty();
    private final StringProperty phong = new SimpleStringProperty();
    private final DoubleProperty tienPhong = new SimpleDoubleProperty();
    private final DoubleProperty tienDichVu = new SimpleDoubleProperty();
    private final StringProperty trangThai = new SimpleStringProperty();
    private final StringProperty maPhong = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> ngayLap = new SimpleObjectProperty<>();
    private final StringProperty moTa = new SimpleStringProperty();
    private final StringProperty hinhThucThanhToan = new SimpleStringProperty();
    private final StringProperty maKhachHang = new SimpleStringProperty();
    private final StringProperty maNhanVien = new SimpleStringProperty();
    private final ObservableList<ChitietHoaDon> chitietHoaDons = FXCollections.observableArrayList();

    public HoaDon() {}

    public HoaDon(String maHoaDon, String tenKhachHang, String phong, double tienPhong, double tienDichVu, 
                  String trangThai, String maPhong, LocalDateTime ngayLap, String moTa, String hinhThucThanhToan, 
                  String maKhachHang, String maNhanVien) {
        setMaHoaDon(maHoaDon);
        setTenKhachHang(tenKhachHang);
        setPhong(phong);
        setTienPhong(tienPhong);
        setTienDichVu(tienDichVu);
        setTrangThai(trangThai);
        setMaPhong(maPhong);
        setNgayLap(ngayLap);
        setMoTa(moTa);
        setHinhThucThanhToan(hinhThucThanhToan);
        setMaKhachHang(maKhachHang);
        setMaNhanVien(maNhanVien);
    }
    

    // Getter và Setter cho các thuộc tính
    public String getMaHoaDon() { return maHoaDon.get(); }
    public StringProperty maHoaDonProperty() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon.set(maHoaDon != null ? maHoaDon : ""); }

    public String getTenKhachHang() { return tenKhachHang.get(); }
    public StringProperty tenKhachHangProperty() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang.set(tenKhachHang != null ? tenKhachHang : ""); }

    public String getPhong() { return phong.get(); }
    public StringProperty phongProperty() { return phong; }
    public void setPhong(String phong) { this.phong.set(phong != null ? phong : ""); }

    public double getTienPhong() { return tienPhong.get(); }
    public DoubleProperty tienPhongProperty() { return tienPhong; }
    public void setTienPhong(double tienPhong) { this.tienPhong.set(tienPhong >= 0 ? tienPhong : 0); }

    public double getTienDichVu() { return tienDichVu.get(); }
    public DoubleProperty tienDichVuProperty() { return tienDichVu; }
    public void setTienDichVu(double tienDichVu) { this.tienDichVu.set(tienDichVu >= 0 ? tienDichVu : 0); }

    public String getTrangThai() { return trangThai.get(); }
    public StringProperty trangThaiProperty() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai.set(trangThai != null ? trangThai : "Chưa thanh toán"); }

    public String getMaPhong() { return maPhong.get(); }
    public StringProperty maPhongProperty() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong.set(maPhong != null ? maPhong : ""); }

    public LocalDateTime getNgayLap() { return ngayLap.get(); }
    public ObjectProperty<LocalDateTime> ngayLapProperty() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap.set(ngayLap); }

    public String getMoTa() { return moTa.get(); }
    public StringProperty moTaProperty() { return moTa; }
    public void setMoTa(String moTa) { this.moTa.set(moTa != null ? moTa : ""); }

    public String getHinhThucThanhToan() { return hinhThucThanhToan.get(); }
    public StringProperty hinhThucThanhToanProperty() { return hinhThucThanhToan; }
    public void setHinhThucThanhToan(String hinhThucThanhToan) { 
        this.hinhThucThanhToan.set(hinhThucThanhToan != null ? hinhThucThanhToan : "Tiền mặt"); 
    }

    public String getMaKhachHang() { return maKhachHang.get(); }
    public StringProperty maKhachHangProperty() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang.set(maKhachHang != null ? maKhachHang : ""); }

    public String getMaNhanVien() { return maNhanVien.get(); }
    public StringProperty maNhanVienProperty() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien.set(maNhanVien != null ? maNhanVien : ""); }

    public ObservableList<ChitietHoaDon> getChitietHoaDons() { 
        return FXCollections.observableArrayList(chitietHoaDons); 
    }
    public void setChitietHoaDons(List<ChitietHoaDon> chitietHoaDons) { 
        this.chitietHoaDons.clear();
        if (chitietHoaDons != null) {
            this.chitietHoaDons.addAll(chitietHoaDons);
        }
    }
    public void addChitietHoaDon(ChitietHoaDon chitiet) {
        if (chitiet != null && !chitietHoaDons.contains(chitiet)) {
            chitietHoaDons.add(chitiet);
        }
    }
    
}