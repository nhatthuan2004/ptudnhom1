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
    private final StringProperty maHoaDon = new SimpleStringProperty();
    private final ObservableList<ChitietPhieuDatPhong> chitietPhieuDatPhongs = FXCollections.observableArrayList();



    // Constructor đầy đủ
    public PhieuDatPhong(String maDatPhong, LocalDate ngayDen, LocalDate ngayDi, LocalDate ngayDat,
                         int soLuongNguoi, String trangThai, String maKhachHang, String maHoaDon) {
        setMaDatPhong(maDatPhong);
        setNgayDat(ngayDat);
        setNgayDen(ngayDen);
        setNgayDi(ngayDi);
        setSoLuongNguoi(soLuongNguoi);
        setTrangThai(trangThai);
        setMaKhachHang(maKhachHang);
        setMaHoaDon(maHoaDon);
    }

    // Getter và Setter cho maDatPhong
    public String getMaDatPhong() {
        return maDatPhong.get();
    }

    public StringProperty maDatPhongProperty() {
        return maDatPhong;
    }

    public void setMaDatPhong(String maDatPhong) {
        this.maDatPhong.set(maDatPhong != null ? maDatPhong.trim() : "");
    }

    // Getter và Setter cho ngayDat
    public LocalDate getNgayDat() {
        return ngayDat.get();
    }

    public ObjectProperty<LocalDate> ngayDatProperty() {
        return ngayDat;
    }

    public void setNgayDat(LocalDate ngayDat) {
        this.ngayDat.set(ngayDat != null && !ngayDat.isAfter(LocalDate.now()) ? ngayDat : LocalDate.now());
    }

    // Getter và Setter cho ngayDen
    public LocalDate getNgayDen() {
        return ngayDen.get();
    }

    public ObjectProperty<LocalDate> ngayDenProperty() {
        return ngayDen;
    }

    public void setNgayDen(LocalDate ngayDen) {
        LocalDate ngayDat = getNgayDat() != null ? getNgayDat() : LocalDate.now();
        this.ngayDen.set(ngayDen != null && !ngayDen.isBefore(ngayDat) ? ngayDen : ngayDat);
    }

    // Getter và Setter cho ngayDi
    public LocalDate getNgayDi() {
        return ngayDi.get();
    }

    public ObjectProperty<LocalDate> ngayDiProperty() {
        return ngayDi;
    }

    public void setNgayDi(LocalDate ngayDi) {
        LocalDate ngayDen = getNgayDen() != null ? getNgayDen() : LocalDate.now();
        this.ngayDi.set(ngayDi != null && ngayDi.isAfter(ngayDen) ? ngayDi : ngayDen.plusDays(1));
    }

    // Getter và Setter cho soLuongNguoi
    public int getSoLuongNguoi() {
        return soLuongNguoi.get();
    }

    public IntegerProperty soLuongNguoiProperty() {
        return soLuongNguoi;
    }

    public void setSoLuongNguoi(int soLuongNguoi) {
        this.soLuongNguoi.set(soLuongNguoi > 0 ? soLuongNguoi : 1);
    }

    // Getter và Setter cho trangThai
    public String getTrangThai() {
        return trangThai.get();
    }

    public StringProperty trangThaiProperty() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai.set(trangThai != null && (trangThai.equals("Chưa xác nhận") || trangThai.equals("Xác nhận") || 
                                                 trangThai.equals("Đã hủy") || trangThai.equals("Đã thanh toán")) 
                           ? trangThai : "Chưa xác nhận");
    }

    // Getter và Setter cho maKhachHang
    public String getMaKhachHang() {
        return maKhachHang.get();
    }

    public StringProperty maKhachHangProperty() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang.set(maKhachHang != null ? maKhachHang.trim() : "");
    }

    // Getter và Setter cho maHoaDon
    public String getMaHoaDon() {
        return maHoaDon.get();
    }

    public StringProperty maHoaDonProperty() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon.set(maHoaDon != null ? maHoaDon.trim() : "");
    }

    // Getter và Setter cho chitietPhieuDatPhongs
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

    // Phương thức toString để debug
    @Override
    public String toString() {
        return "PhieuDatPhong{" +
                "maDatPhong=" + maDatPhong.get() +
                ", ngayDen=" + ngayDen.get() +
                ", ngayDi=" + ngayDi.get() +
                ", ngayDat=" + ngayDat.get() +
                ", soLuongNguoi=" + soLuongNguoi.get() +
                ", trangThai=" + trangThai.get() +
                ", maKhachHang=" + maKhachHang.get() +
                ", maHoaDon=" + maHoaDon.get() +
                '}';
    }
}