package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDon {
    private final StringProperty maHoaDon = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> ngayLap = new SimpleObjectProperty<>();
    private final StringProperty hinhThucThanhToan = new SimpleStringProperty();
    private final DoubleProperty tongTien = new SimpleDoubleProperty();
    private final BooleanProperty trangThai = new SimpleBooleanProperty();
    private final StringProperty maKhachHang = new SimpleStringProperty();
    private final StringProperty maNhanVien = new SimpleStringProperty();
    private final ObservableList<ChitietHoaDon> chitietHoaDons = FXCollections.observableArrayList();

    // Constructor đầy đủ
    public HoaDon(String maHoaDon, LocalDateTime ngayLap, String hinhThucThanhToan, double tongTien, 
                  boolean trangThai, String maKhachHang, String maNhanVien) {
        setMaHoaDon(maHoaDon);
        setNgayLap(ngayLap);
        setHinhThucThanhToan(hinhThucThanhToan);
        setTongTien(tongTien);
        setTrangThai(trangThai);
        setMaKhachHang(maKhachHang);
        setMaNhanVien(maNhanVien);
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

    // Getter và Setter cho ngayLap
    public LocalDateTime getNgayLap() {
        return ngayLap.get();
    }

    public ObjectProperty<LocalDateTime> ngayLapProperty() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap.set(ngayLap != null ? ngayLap : LocalDateTime.now());
    }

    // Getter và Setter cho hinhThucThanhToan
    public String getHinhThucThanhToan() {
        return hinhThucThanhToan.get();
    }

    public StringProperty hinhThucThanhToanProperty() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan.set(hinhThucThanhToan != null ? hinhThucThanhToan.trim() : "Tiền mặt");
    }

    // Getter và Setter cho tongTien
    public double getTongTien() {
        return tongTien.get();
    }

    public DoubleProperty tongTienProperty() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien.set(tongTien >= 0 ? tongTien : 0);
    }

    // Getter và Setter cho trangThai
    public boolean getTrangThai() {
        return trangThai.get();
    }

    public BooleanProperty trangThaiProperty() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai.set(trangThai);
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

    // Getter và Setter cho maNhanVien
    public String getMaNhanVien() {
        return maNhanVien.get();
    }

    public StringProperty maNhanVienProperty() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien.set(maNhanVien != null ? maNhanVien.trim() : "");
    }

    // Getter và Setter cho chitietHoaDons
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

    // Phương thức toString để debug
    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon=" + maHoaDon.get() +
                ", ngayLap=" + ngayLap.get() +
                ", hinhThucThanhToan=" + hinhThucThanhToan.get() +
                ", tongTien=" + tongTien.get() +
                ", trangThai=" + trangThai.get() +
                ", maKhachHang=" + maKhachHang.get() +
                ", maNhanVien=" + maNhanVien.get() +
                '}';
    }

	public Object getMaKhuyenMai() {
		// TODO Auto-generated method stub
		return null;
	}
}