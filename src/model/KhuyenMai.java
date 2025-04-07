package model;

public class KhuyenMai {
    private String maChuongTrinhKhuyenMai;
    private String tenChuongTrinhKhuyenMai;
    private double chietKhau;
    private String moTaChuongTrinhKhuyenMai;
    private boolean trangThai;
    
    
    
	public KhuyenMai(String maChuongTrinhKhuyenMai, String tenChuongTrinhKhuyenMai, double chietKhau,
			String moTaChuongTrinhKhuyenMai, boolean trangThai) {
		super();
		this.maChuongTrinhKhuyenMai = maChuongTrinhKhuyenMai;
		this.tenChuongTrinhKhuyenMai = tenChuongTrinhKhuyenMai;
		this.chietKhau = chietKhau;
		this.moTaChuongTrinhKhuyenMai = moTaChuongTrinhKhuyenMai;
		this.trangThai = trangThai;
	}
	
	public String getMaChuongTrinhKhuyenMai() {
		return maChuongTrinhKhuyenMai;
	}
	public void setMaChuongTrinhKhuyenMai(String maChuongTrinhKhuyenMai) {
		this.maChuongTrinhKhuyenMai = maChuongTrinhKhuyenMai;
	}
	public String getTenChuongTrinhKhuyenMai() {
		return tenChuongTrinhKhuyenMai;
	}
	public void setTenChuongTrinhKhuyenMai(String tenChuongTrinhKhuyenMai) {
		this.tenChuongTrinhKhuyenMai = tenChuongTrinhKhuyenMai;
	}
	public double getChietKhau() {
		return chietKhau;
	}
	public void setChietKhau(double chietKhau) {
		this.chietKhau = chietKhau;
	}
	public String getMoTaChuongTrinhKhuyenMai() {
		return moTaChuongTrinhKhuyenMai;
	}
	public void setMoTaChuongTrinhKhuyenMai(String moTaChuongTrinhKhuyenMai) {
		this.moTaChuongTrinhKhuyenMai = moTaChuongTrinhKhuyenMai;
	}
	public boolean isTrangThai() {
		return trangThai;
	}
	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}
    
    
    
    
	
    
    
    
    
}