package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;

public class PhieuDichVu {
    private final StringProperty maPhieuDichVu = new SimpleStringProperty();
    private final StringProperty maHoaDon = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> ngaySuDung = new SimpleObjectProperty<>();
    private HoaDon hoaDon;
    private final ObservableList<ChitietPhieuDichVu> chitietPhieuDichVus = FXCollections.observableArrayList();
    private final ObservableList<ChitietHoaDon> chitietHoaDons = FXCollections.observableArrayList();

    public PhieuDichVu() {}

    public PhieuDichVu(String maPhieuDichVu, String maHoaDon, LocalDate ngaySuDung) {
        setMaPhieuDichVu(maPhieuDichVu);
        setMaHoaDon(maHoaDon);
        setNgaySuDung(ngaySuDung);
    }

    public String getMaPhieuDichVu() { return maPhieuDichVu.get(); }
    public StringProperty maPhieuDichVuProperty() { return maPhieuDichVu; }
    public void setMaPhieuDichVu(String maPhieuDichVu) { 
        this.maPhieuDichVu.set(maPhieuDichVu != null ? maPhieuDichVu : ""); 
    }

    public String getMaHoaDon() { return maHoaDon.get(); }
    public StringProperty maHoaDonProperty() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { 
        this.maHoaDon.set(maHoaDon != null ? maHoaDon : ""); 
    }

    public LocalDate getNgaySuDung() { return ngaySuDung.get(); }
    public ObjectProperty<LocalDate> ngaySuDungProperty() { return ngaySuDung; }
    public void setNgaySuDung(LocalDate ngaySuDung) { 
        this.ngaySuDung.set(ngaySuDung != null && !ngaySuDung.isAfter(LocalDate.now()) ? ngaySuDung : LocalDate.now()); 
    }

    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }

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