package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class ChuongTrinhKhuyenMai {
    private final StringProperty maChuongTrinhKhuyenMai = new SimpleStringProperty();
    private final StringProperty tenChuongTrinhKhuyenMai = new SimpleStringProperty();
    private final DoubleProperty chietKhau = new SimpleDoubleProperty();
    private final StringProperty moTaChuongTrinhKhuyenMai = new SimpleStringProperty();
    private final BooleanProperty trangThai = new SimpleBooleanProperty(); // true: Hoạt động, false: Không hoạt động
    private final ObservableList<ChitietHoaDon> chitietHoaDons = FXCollections.observableArrayList();

    public ChuongTrinhKhuyenMai() {}

    public ChuongTrinhKhuyenMai(String maChuongTrinhKhuyenMai, String tenChuongTrinhKhuyenMai, double chietKhau, 
                                String moTaChuongTrinhKhuyenMai, boolean trangThai) {
        setMaChuongTrinhKhuyenMai(maChuongTrinhKhuyenMai);
        setTenChuongTrinhKhuyenMai(tenChuongTrinhKhuyenMai);
        setChietKhau(chietKhau);
        setMoTaChuongTrinhKhuyenMai(moTaChuongTrinhKhuyenMai);
        setTrangThai(trangThai);
    }

    public String getMaChuongTrinhKhuyenMai() { return maChuongTrinhKhuyenMai.get(); }
    public StringProperty maChuongTrinhKhuyenMaiProperty() { return maChuongTrinhKhuyenMai; }
    public void setMaChuongTrinhKhuyenMai(String maChuongTrinhKhuyenMai) { 
        this.maChuongTrinhKhuyenMai.set(maChuongTrinhKhuyenMai != null ? maChuongTrinhKhuyenMai : ""); 
    }

    public String getTenChuongTrinhKhuyenMai() { return tenChuongTrinhKhuyenMai.get(); }
    public StringProperty tenChuongTrinhKhuyenMaiProperty() { return tenChuongTrinhKhuyenMai; }
    public void setTenChuongTrinhKhuyenMai(String tenChuongTrinhKhuyenMai) { 
        this.tenChuongTrinhKhuyenMai.set(tenChuongTrinhKhuyenMai != null ? tenChuongTrinhKhuyenMai : ""); 
    }

    public double getChietKhau() { return chietKhau.get(); }
    public DoubleProperty chietKhauProperty() { return chietKhau; }
    public void setChietKhau(double chietKhau) { 
        this.chietKhau.set(chietKhau >= 0 && chietKhau <= 100 ? chietKhau : 0); 
    }

    public String getMoTaChuongTrinhKhuyenMai() { return moTaChuongTrinhKhuyenMai.get(); }
    public StringProperty moTaChuongTrinhKhuyenMaiProperty() { return moTaChuongTrinhKhuyenMai; }
    public void setMoTaChuongTrinhKhuyenMai(String moTaChuongTrinhKhuyenMai) { 
        this.moTaChuongTrinhKhuyenMai.set(moTaChuongTrinhKhuyenMai != null ? moTaChuongTrinhKhuyenMai : ""); 
    }

    public boolean isTrangThai() { return trangThai.get(); }
    public BooleanProperty trangThaiProperty() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai.set(trangThai); }

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