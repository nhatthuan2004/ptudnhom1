package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;

public class PhieuDichVu {
    private final StringProperty maPhieuDichVu = new SimpleStringProperty();
    private final StringProperty maHoaDon = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> ngayTao = new SimpleObjectProperty<>();
    private final ObservableList<ChitietPhieuDichVu> chitietPhieuDichVus = FXCollections.observableArrayList();
    private final StringProperty maDatPhong = new SimpleStringProperty();

    // Constructor đầy đủ
    public PhieuDichVu(String maPhieuDichVu, String maDatPhong, String maHoaDon, LocalDate ngayTao) {
        setMaPhieuDichVu(maPhieuDichVu);
        setMaDatPhong(maDatPhong);
        setMaHoaDon(maHoaDon);
        setNgayTao(ngayTao);
    }

    // Getter và Setter cho maPhieuDichVu
    public String getMaPhieuDichVu() {
        return maPhieuDichVu.get();
    }

    public StringProperty maPhieuDichVuProperty() {
        return maPhieuDichVu;
    }

    public void setMaPhieuDichVu(String maPhieuDichVu) {
        this.maPhieuDichVu.set(maPhieuDichVu != null ? maPhieuDichVu.trim() : "");
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

    // Getter và Setter cho ngayTao
    public LocalDate getNgayTao() {
        return ngayTao.get();
    }

    public ObjectProperty<LocalDate> ngayTaoProperty() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao.set(ngayTao != null && !ngayTao.isAfter(LocalDate.now()) ? ngayTao : LocalDate.now());
    }

    // Getter và Setter cho chitietPhieuDichVus
    public ObservableList<ChitietPhieuDichVu> getChitietPhieuDichVus() {
        return FXCollections.observableArrayList(chitietPhieuDichVus);
    }

    public void setChitietPhieuDichVus(List<ChitietPhieuDichVu> chitietPhieuDichVus) {
        this.chitietPhieuDichVus.clear();
        if (chitietPhieuDichVus != null) {
            this.chitietPhieuDichVus.addAll(chitietPhieuDichVus);
        }
    }

    public void addChitietPhieuDichVu(ChitietPhieuDichVu chitiet) {
        if (chitiet != null && !chitietPhieuDichVus.contains(chitiet)) {
            chitietPhieuDichVus.add(chitiet);
        }
    }

    // Getter và Setter cho maDatPhong
    public String getMaDatPhong() {
        if (maDatPhong == null) {
            System.err.println("maDatPhong StringProperty is null!");
            return null;
        }
        return maDatPhong.get();
    }

    public StringProperty maDatPhongProperty() {
        return maDatPhong;
    }

    public void setMaDatPhong(String maDatPhong) {
        this.maDatPhong.set(maDatPhong != null ? maDatPhong.trim() : "");
    }

    // Phương thức toString để debug
    @Override
    public String toString() {
        return "PhieuDichVu{" +
                "maPhieuDichVu=" + maPhieuDichVu.get() +
                ", maDatPhong=" + maDatPhong.get() +
                ", maHoaDon=" + maHoaDon.get() +
                ", ngayTao=" + ngayTao.get() +
                '}';
    }
}