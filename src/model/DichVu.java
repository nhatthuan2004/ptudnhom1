package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class DichVu {
    private final StringProperty maDichVu = new SimpleStringProperty();
    private final StringProperty tenDichVu = new SimpleStringProperty();
    private final DoubleProperty gia = new SimpleDoubleProperty();
    private final StringProperty moTa = new SimpleStringProperty();
    private final StringProperty trangThai = new SimpleStringProperty(); // Thêm thuộc tính trạng thái
    private final ObservableList<ChitietPhieuDichVu> chitietPhieuDichVus = FXCollections.observableArrayList();

    public DichVu() {}

    public DichVu(String maDichVu, String tenDichVu, double gia, String moTa, String trangThai) {
        setMaDichVu(maDichVu);
        setTenDichVu(tenDichVu);
        setGia(gia);
        setMoTa(moTa);
        setTrangThai(trangThai);
    }

    public String getMaDichVu() { return maDichVu.get(); }
    public StringProperty maDichVuProperty() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu.set(maDichVu != null ? maDichVu : ""); }

    public String getTenDichVu() { return tenDichVu.get(); }
    public StringProperty tenDichVuProperty() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu.set(tenDichVu != null ? tenDichVu : ""); }

    public double getGia() { return gia.get(); }
    public DoubleProperty giaProperty() { return gia; }
    public void setGia(double gia) { this.gia.set(gia > 0 ? gia : 0); }

    public String getMoTa() { return moTa.get(); }
    public StringProperty moTaProperty() { return moTa; }
    public void setMoTa(String moTa) { this.moTa.set(moTa != null ? moTa : ""); }

    public String getTrangThai() { return trangThai.get(); }
    public StringProperty trangThaiProperty() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai.set(trangThai != null ? trangThai : "Hoạt động"); }

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
}