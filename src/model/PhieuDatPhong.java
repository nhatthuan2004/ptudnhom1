package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;

public class PhieuDatPhong {
    private final StringProperty maDatPhong = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> ngayDen = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> ngayDi = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> ngayDat = new SimpleObjectProperty<>();
    private final IntegerProperty soLuongNguoi = new SimpleIntegerProperty();
    private final StringProperty trangThai = new SimpleStringProperty();
    private final StringProperty maKhachHang = new SimpleStringProperty();
    private KhachHang khachHang;
    private final ObservableList<ChitietPhieuDatPhong> chitietPhieuDatPhongs = FXCollections.observableArrayList();
    private final ObservableList<ChitietHoaDon> chitietHoaDons = FXCollections.observableArrayList();

    public PhieuDatPhong() {}

    public PhieuDatPhong(String maDatPhong, LocalDate ngayDen, LocalDate ngayDi, LocalDate ngayDat, 
                         int soLuongNguoi, String trangThai, String maKhachHang) {
        setMaDatPhong(maDatPhong);
        setNgayDen(ngayDen);
        setNgayDi(ngayDi);
        setNgayDat(ngayDat);
        setSoLuongNguoi(soLuongNguoi);
        setTrangThai(trangThai);
        setMaKhachHang(maKhachHang);
    }

    public String getMaDatPhong() { return maDatPhong.get(); }
    public StringProperty maDatPhongProperty() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { 
        this.maDatPhong.set(maDatPhong != null ? maDatPhong : ""); 
    }

    public LocalDate getNgayDen() { return ngayDen.get(); }
    public ObjectProperty<LocalDate> ngayDenProperty() { return ngayDen; }
    public void setNgayDen(LocalDate ngayDen) { 
        this.ngayDen.set(ngayDen != null && !ngayDen.isBefore(LocalDate.now()) ? ngayDen : LocalDate.now()); 
    }

    public LocalDate getNgayDi() { return ngayDi.get(); }
    public ObjectProperty<LocalDate> ngayDiProperty() { return ngayDi; }
    public void setNgayDi(LocalDate ngayDi) { 
        if (ngayDi != null && ngayDi.isAfter(getNgayDen() != null ? getNgayDen() : LocalDate.now())) {
            this.ngayDi.set(ngayDi);
        } else {
            this.ngayDi.set(null);
        }
    }

    public LocalDate getNgayDat() { return ngayDat.get(); }
    public ObjectProperty<LocalDate> ngayDatProperty() { return ngayDat; }
    public void setNgayDat(LocalDate ngayDat) { 
        this.ngayDat.set(ngayDat != null && !ngayDat.isAfter(LocalDate.now()) ? ngayDat : LocalDate.now()); 
    }

    public int getSoLuongNguoi() { return soLuongNguoi.get(); }
    public IntegerProperty soLuongNguoiProperty() { return soLuongNguoi; }
    public void setSoLuongNguoi(int soLuongNguoi) { 
        this.soLuongNguoi.set(soLuongNguoi > 0 ? soLuongNguoi : 1); 
    }

    public String getTrangThai() { return trangThai.get(); }
    public StringProperty trangThaiProperty() { return trangThai; }
    public void setTrangThai(String trangThai) { 
        this.trangThai.set(trangThai != null ? trangThai : "Chưa xác nhận"); 
    }

    public String getMaKhachHang() { return maKhachHang.get(); }
    public StringProperty maKhachHangProperty() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { 
        this.maKhachHang.set(maKhachHang != null ? maKhachHang : ""); 
    }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public ObservableList<ChitietPhieuDatPhong> getChitietPhieuDatPhongs() { 
        return FXCollections.observableArrayList(chitietPhieuDatPhongs); 
    }
    public void setChitietPhieuDatPhongs(List<ChitietPhieuDatPhong> chitietPhieuDatPhongs) { 
        this.chitietPhieuDatPhongs.clear();
        if (chitietPhieuDatPhongs != null) {
            this.chitietPhieuDatPhongs.addAll(chitietPhieuDatPhongs);
        }
    }
    public void addChitietPhieuDatPhong(ChitietPhieuDatPhong chitiet) {
        if (chitiet != null && !chitietPhieuDatPhongs.contains(chitiet)) {
            chitietPhieuDatPhongs.add(chitiet);
        }
    }

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