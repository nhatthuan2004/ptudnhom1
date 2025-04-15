package model;

import javafx.beans.property.*;

public class DichVu {
    private final StringProperty maDichVu = new SimpleStringProperty();
    private final StringProperty tenDichVu = new SimpleStringProperty();
    private final DoubleProperty gia = new SimpleDoubleProperty();
    private final StringProperty moTa = new SimpleStringProperty();
    private final StringProperty trangThai = new SimpleStringProperty();

    public DichVu() {}

    public DichVu(String maDichVu, String tenDichVu, String moTa, double gia, String trangThai) {
        setMaDichVu(maDichVu);
        setTenDichVu(tenDichVu);
        setMoTa(moTa);
        setGia(gia);
        setTrangThai(trangThai);
    }

    public String getMaDichVu() { return maDichVu.get(); }
    public StringProperty maDichVuProperty() { return maDichVu; }
    public void setMaDichVu(String maDichVu) {
        this.maDichVu.set(maDichVu != null && !maDichVu.trim().isEmpty() ? maDichVu : null);
    }

    public String getTenDichVu() { return tenDichVu.get(); }
    public StringProperty tenDichVuProperty() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) {
        if (tenDichVu == null || tenDichVu.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên dịch vụ không được để trống.");
        }
        this.tenDichVu.set(tenDichVu);
    }

    public double getGia() { return gia.get(); }
    public DoubleProperty giaProperty() { return gia; }
    public void setGia(double gia) {
        this.gia.set(gia >= 0 ? gia : 0);
    }

    public String getMoTa() { return moTa.get(); }
    public StringProperty moTaProperty() { return moTa; }
    public void setMoTa(String moTa) {
        this.moTa.set(moTa != null ? moTa : null);
    }

    public String getTrangThai() { return trangThai.get(); }
    public StringProperty trangThaiProperty() { return trangThai; }
    public void setTrangThai(String trangThai) {
        this.trangThai.set(trangThai != null && (trangThai.equals("Hoạt động") || trangThai.equals("Tạm ngưng")) ? trangThai : "Hoạt động");
    }
}