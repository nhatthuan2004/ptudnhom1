package model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class LichSuChuyenPhong {
    private final StringProperty maLichSu = new SimpleStringProperty();
    private final StringProperty maPhieuDat = new SimpleStringProperty();
    private final StringProperty maPhongCu = new SimpleStringProperty();
    private final StringProperty maPhongMoi = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> thoiGianChuyen = new SimpleObjectProperty<>();
    private final StringProperty lyDo = new SimpleStringProperty();
    private final StringProperty maNhanVien = new SimpleStringProperty();

    public LichSuChuyenPhong() {}

    public LichSuChuyenPhong(String maLichSu, String maPhieuDat, String maPhongCu, String maPhongMoi,
                             LocalDateTime thoiGianChuyen, String lyDo, String maNhanVien) {
        setMaLichSu(maLichSu);
        setMaPhieuDat(maPhieuDat);
        setMaPhongCu(maPhongCu);
        setMaPhongMoi(maPhongMoi);
        setThoiGianChuyen(thoiGianChuyen);
        setLyDo(lyDo);
        setMaNhanVien(maNhanVien);
    }

    public String getMaLichSu() { return maLichSu.get(); }
    public StringProperty maLichSuProperty() { return maLichSu; }
    public void setMaLichSu(String maLichSu) {
        this.maLichSu.set(maLichSu != null && !maLichSu.trim().isEmpty() ? maLichSu : null);
    }

    public String getMaPhieuDat() { return maPhieuDat.get(); }
    public StringProperty maPhieuDatProperty() { return maPhieuDat; }
    public void setMaPhieuDat(String maPhieuDat) {
        this.maPhieuDat.set(maPhieuDat != null && !maPhieuDat.trim().isEmpty() ? maPhieuDat : null);
    }

    public String getMaPhongCu() { return maPhongCu.get(); }
    public StringProperty maPhongCuProperty() { return maPhongCu; }
    public void setMaPhongCu(String maPhongCu) {
        this.maPhongCu.set(maPhongCu != null && !maPhongCu.trim().isEmpty() ? maPhongCu : null);
    }

    public String getMaPhongMoi() { return maPhongMoi.get(); }
    public StringProperty maPhongMoiProperty() { return maPhongMoi; }
    public void setMaPhongMoi(String maPhongMoi) {
        this.maPhongMoi.set(maPhongMoi != null && !maPhongMoi.trim().isEmpty() ? maPhongMoi : null);
    }

    public LocalDateTime getThoiGianChuyen() { return thoiGianChuyen.get(); }
    public ObjectProperty<LocalDateTime> thoiGianChuyenProperty() { return thoiGianChuyen; }
    public void setThoiGianChuyen(LocalDateTime thoiGianChuyen) {
        this.thoiGianChuyen.set(thoiGianChuyen != null && !thoiGianChuyen.isAfter(LocalDateTime.now()) ? thoiGianChuyen : null);
    }

    public String getLyDo() { return lyDo.get(); }
    public StringProperty lyDoProperty() { return lyDo; }
    public void setLyDo(String lyDo) {
        this.lyDo.set(lyDo != null ? lyDo : null);
    }

    public String getMaNhanVien() { return maNhanVien.get(); }
    public StringProperty maNhanVienProperty() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien.set(maNhanVien != null && !maNhanVien.trim().isEmpty() ? maNhanVien : null);
    }
}