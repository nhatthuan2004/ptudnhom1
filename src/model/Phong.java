package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class Phong {
    private final StringProperty maPhong = new SimpleStringProperty();
    private final StringProperty loaiPhong = new SimpleStringProperty();
    private final DoubleProperty giaPhong = new SimpleDoubleProperty();
    private final StringProperty trangThai = new SimpleStringProperty();
    private final StringProperty viTri = new SimpleStringProperty();
    private final IntegerProperty soNguoiToiDa = new SimpleIntegerProperty();
    private final StringProperty moTa = new SimpleStringProperty();
    private final StringProperty tenKhachHang = new SimpleStringProperty(); // Thêm để lưu tên khách hàng
    private final StringProperty soDienThoai = new SimpleStringProperty(); // Thêm để lưu SĐT
    private final ObservableList<DichVu> danhSachDichVu = FXCollections.observableArrayList(); // Thêm để lưu danh sách dịch vụ
    private final ObservableList<ChitietPhieuDatPhong> chitietPhieuDatPhongs = FXCollections.observableArrayList();
    private final ObservableList<ChitietHoaDon> chitietHoaDons = FXCollections.observableArrayList();

    public Phong() {}

    public Phong(String maPhong, String loaiPhong, double giaPhong, String trangThai, String viTri, int soNguoiToiDa, String moTa) {
        setMaPhong(maPhong);
        setLoaiPhong(loaiPhong);
        setGiaPhong(giaPhong);
        setTrangThai(trangThai);
        setViTri(viTri);
        setSoNguoiToiDa(soNguoiToiDa);
        setMoTa(moTa);
        setTenKhachHang("");
        setSoDienThoai("");
    }

    public String getMaPhong() { return maPhong.get(); }
    public StringProperty maPhongProperty() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong.set(maPhong != null ? maPhong : ""); }

    public String getLoaiPhong() { return loaiPhong.get(); }
    public StringProperty loaiPhongProperty() { return loaiPhong; }
    public void setLoaiPhong(String loaiPhong) { this.loaiPhong.set(loaiPhong != null ? loaiPhong : ""); }

    public double getGiaPhong() { return giaPhong.get(); }
    public DoubleProperty giaPhongProperty() { return giaPhong; }
    public void setGiaPhong(double giaPhong) { this.giaPhong.set(giaPhong > 0 ? giaPhong : 0); }

    public String getTrangThai() { return trangThai.get(); }
    public StringProperty trangThaiProperty() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai.set(trangThai != null ? trangThai : "Trống"); }

    public String getViTri() { return viTri.get(); }
    public StringProperty viTriProperty() { return viTri; }
    public void setViTri(String viTri) { this.viTri.set(viTri != null ? viTri : ""); }

    public int getSoNguoiToiDa() { return soNguoiToiDa.get(); }
    public IntegerProperty soNguoiToiDaProperty() { return soNguoiToiDa; }
    public void setSoNguoiToiDa(int soNguoiToiDa) { this.soNguoiToiDa.set(soNguoiToiDa > 0 ? soNguoiToiDa : 1); }

    public String getMoTa() { return moTa.get(); }
    public StringProperty moTaProperty() { return moTa; }
    public void setMoTa(String moTa) { this.moTa.set(moTa != null ? moTa : ""); }

    public String getTenKhachHang() { return tenKhachHang.get(); }
    public StringProperty tenKhachHangProperty() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang.set(tenKhachHang != null ? tenKhachHang : ""); }

    public String getSoDienThoai() { return soDienThoai.get(); }
    public StringProperty soDienThoaiProperty() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai.set(soDienThoai != null ? soDienThoai : ""); }

    public ObservableList<DichVu> getDanhSachDichVu() { 
        return FXCollections.observableArrayList(danhSachDichVu); 
    }
    public void setDanhSachDichVu(List<DichVu> dichVuList) { 
        this.danhSachDichVu.clear();
        if (dichVuList != null) {
            this.danhSachDichVu.addAll(dichVuList);
        }
    }
    public void addDichVu(DichVu dichVu) {
        if (dichVu != null && !danhSachDichVu.contains(dichVu)) {
            danhSachDichVu.add(dichVu);
        }
    }

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