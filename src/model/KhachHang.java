package model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhachHang {
    private final StringProperty maKhachHang = new SimpleStringProperty();
    private final StringProperty tenKhachHang = new SimpleStringProperty();
    private final StringProperty soDienThoai = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty diaChi = new SimpleStringProperty();
    private final StringProperty cccd = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> ngaySinh = new SimpleObjectProperty<>();
    private final StringProperty quocTich = new SimpleStringProperty();
    private final StringProperty gioiTinh = new SimpleStringProperty();
    private List<HoaDon> hoaDonList;

 

    public KhachHang(String maKhachHang, String tenKhachHang, String soDienThoai, String email,
                     String diaChi, String cccd, LocalDate ngaySinh, String quocTich, String gioiTinh) {
        setMaKhachHang(maKhachHang);
        setTenKhachHang(tenKhachHang);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        setDiaChi(diaChi);
        setCccd(cccd);
        setNgaySinh(ngaySinh);
        setQuocTich(quocTich);
        setGioiTinh(gioiTinh);
    }

    public String getMaKhachHang() { return maKhachHang.get(); }
    public StringProperty maKhachHangProperty() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang.set(maKhachHang != null && !maKhachHang.trim().isEmpty() ? maKhachHang : null);
    }

    public String getTenKhachHang() { return tenKhachHang.get(); }
    public StringProperty tenKhachHangProperty() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) {
        if (tenKhachHang == null || tenKhachHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }
        this.tenKhachHang.set(tenKhachHang);
    }

    public String getSoDienThoai() { return soDienThoai.get(); }
    public StringProperty soDienThoaiProperty() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai.set(soDienThoai != null && soDienThoai.matches("\\d{10,11}") ? soDienThoai : null);
    }

    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }
    public void setEmail(String email) {
        this.email.set(email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$") ? email : null);
    }

    public String getDiaChi() { return diaChi.get(); }
    public StringProperty diaChiProperty() { return diaChi; }
    public void setDiaChi(String diaChi) {
        this.diaChi.set(diaChi != null ? diaChi : null);
    }

    public String getCccd() { return cccd.get(); }
    public StringProperty cccdProperty() { return cccd; }
    public void setCccd(String cccd) {
        this.cccd.set(cccd != null && cccd.matches("\\d{9,12}") ? cccd : null);
    }

    public LocalDate getNgaySinh() { return ngaySinh.get(); }
    public ObjectProperty<LocalDate> ngaySinhProperty() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh.set(ngaySinh != null && !ngaySinh.isAfter(LocalDate.now()) ? ngaySinh : null);
    }

    public String getQuocTich() { return quocTich.get(); }
    public StringProperty quocTichProperty() { return quocTich; }
    public void setQuocTich(String quocTich) {
        this.quocTich.set(quocTich != null ? quocTich : null);
    }

    public String getGioiTinh() { return gioiTinh.get(); }
    public StringProperty gioiTinhProperty() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh.set(gioiTinh != null && (gioiTinh.equals("Nam") || gioiTinh.equals("Nữ")) ? gioiTinh : null);
    }

    public List<HoaDon> getHoaDonList() {
        if (hoaDonList == null) {
            hoaDonList = new ArrayList<>();
        }
        return hoaDonList;
    }


	public void addHoaDon(HoaDon hoaDon, ObservableList<HoaDon> hoaDonList) {
		// TODO Auto-generated method stub
		
	}

	public List<HoaDon> getHoaDonList1() {
	    if (hoaDonList == null) {
	        hoaDonList = new ArrayList<>();
	    }
	    return hoaDonList;
	}

	public void setHoaDonList(List<HoaDon> hoaDonList) {
        this.hoaDonList = (hoaDonList != null) ? new ArrayList<>(hoaDonList) : new ArrayList<>();
    }


}