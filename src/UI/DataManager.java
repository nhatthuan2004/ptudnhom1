package UI;

import dao.*;
import model.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static DataManager instance;
    private final ObservableList<Phong> phongList = FXCollections.observableArrayList();
    private final ObservableList<HoaDon> hoaDonList = FXCollections.observableArrayList();
    private final ObservableList<KhachHang> khachHangList = FXCollections.observableArrayList();
    private final ObservableList<DichVu> dichVuList = FXCollections.observableArrayList();
    private final ObservableList<KhuyenMai> khuyenMaiList = FXCollections.observableArrayList();
    private final ObservableList<NhanVien> nhanVienList = FXCollections.observableArrayList();
    private final ObservableList<PhieuDatPhong> phieuDatPhongList = FXCollections.observableArrayList();
    private final ObservableList<ChitietPhieuDatPhong> chitietPhieuDatPhongList = FXCollections.observableArrayList();
    private final ObservableList<ChitietHoaDon> chitietHoaDonList = FXCollections.observableArrayList();
    private final ObservableList<PhieuDichVu> phieuDichVuList = FXCollections.observableArrayList();
    private final ObservableList<ChitietPhieuDichVu> chitietPhieuDichVuList = FXCollections.observableArrayList();
    private final ObservableList<LichSuChuyenPhong> lichSuChuyenPhongList = FXCollections.observableArrayList(); // Thêm danh sách LichSuChuyenPhong
    private final List<Runnable> phongListChangeListeners = new ArrayList<>();
    private final List<Runnable> hoaDonListChangeListeners = new ArrayList<>();
    private final List<Runnable> khachHangListChangeListeners = new ArrayList<>();
    private final List<Runnable> dichVuListChangeListeners = new ArrayList<>();
    private final List<Runnable> khuyenMaiListChangeListeners = new ArrayList<>();
    private final List<Runnable> nhanVienListChangeListeners = new ArrayList<>();
    private final List<Runnable> phieuDatPhongListChangeListeners = new ArrayList<>();
    private final List<Runnable> chitietPhieuDatPhongListChangeListeners = new ArrayList<>();
    private final List<Runnable> chitietHoaDonListChangeListeners = new ArrayList<>();
    private final List<Runnable> phieuDichVuListChangeListeners = new ArrayList<>();
    private final List<Runnable> chitietPhieuDichVuListChangeListeners = new ArrayList<>();
    private final List<Runnable> lichSuChuyenPhongListChangeListeners = new ArrayList<>(); // Thêm listener cho LichSuChuyenPhong
    private final KhachHang_Dao khachHangDao = new KhachHang_Dao();
    private final DichVu_Dao dichVuDao = new DichVu_Dao();
    private final KhuyenMai_Dao khuyenMaiDao = new KhuyenMai_Dao();
    private final Phong_Dao phongDao = new Phong_Dao();
    private final NhanVien_Dao nhanVienDao = new NhanVien_Dao();
    private final PhieuDatPhong_Dao phieuDatPhongDao = new PhieuDatPhong_Dao();
    private final ChitietPhieuDatPhong_Dao chitietPhieuDatPhongDao = new ChitietPhieuDatPhong_Dao();
    private final HoaDon_Dao hoaDonDao = new HoaDon_Dao();
    private final ChitietHoaDon_Dao chitietHoaDonDao = new ChitietHoaDon_Dao();
    private final PhieuDichVu_Dao phieuDichVuDao = new PhieuDichVu_Dao();
    private final ChitietPhieuDichVu_Dao chitietPhieuDichVuDao = new ChitietPhieuDichVu_Dao();
    private final LichSuChuyenPhong_DAO lichSuChuyenPhongDao = new LichSuChuyenPhong_DAO(); // Thêm DAO cho LichSuChuyenPhong
    private final TaiKhoan_Dao taiKhoanDao = new TaiKhoan_Dao();
    private NhanVien currentNhanVien;

    private DataManager() {

        // Thêm listener để thông báo khi danh sách thay đổi
    	
    	phongList.addListener((ListChangeListener<Phong>) change -> notifyListeners(phongListChangeListeners));
        hoaDonList.addListener((ListChangeListener<HoaDon>) change -> notifyListeners(hoaDonListChangeListeners));
        khachHangList.addListener((ListChangeListener<KhachHang>) change -> notifyListeners(khachHangListChangeListeners));
        dichVuList.addListener((ListChangeListener<DichVu>) change -> notifyListeners(dichVuListChangeListeners));
        khuyenMaiList.addListener((ListChangeListener<KhuyenMai>) change -> notifyListeners(khuyenMaiListChangeListeners));
        nhanVienList.addListener((ListChangeListener<NhanVien>) change -> notifyListeners(nhanVienListChangeListeners));
        phieuDatPhongList.addListener((ListChangeListener<PhieuDatPhong>) change -> notifyListeners(phieuDatPhongListChangeListeners));
        chitietPhieuDatPhongList.addListener((ListChangeListener<ChitietPhieuDatPhong>) change -> notifyListeners(chitietPhieuDatPhongListChangeListeners));
        chitietHoaDonList.addListener((ListChangeListener<ChitietHoaDon>) change -> notifyListeners(chitietHoaDonListChangeListeners));
        phieuDichVuList.addListener((ListChangeListener<PhieuDichVu>) change -> notifyListeners(phieuDichVuListChangeListeners));
        chitietPhieuDichVuList.addListener((ListChangeListener<ChitietPhieuDichVu>) change -> notifyListeners(chitietPhieuDichVuListChangeListeners));
        lichSuChuyenPhongList.addListener((ListChangeListener<LichSuChuyenPhong>) change -> notifyListeners(lichSuChuyenPhongListChangeListeners));
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            instance.initializeData();
        }
        return instance;
    }

    private void initializeData() {

        try {
            // Tải danh sách tài khoản và kiểm tra tài khoản admin
            List<TaiKhoan> taiKhoanList = taiKhoanDao.getAllTaiKhoan();
            boolean adminExists = taiKhoanList.stream()
                    .anyMatch(tk -> tk.getTenDangNhap().equals("admin"));

            if (!adminExists) {
                String maNhanVien = nhanVienDao.getNextMaNhanVien();
                NhanVien adminNhanVien = new NhanVien(
                        maNhanVien, "Admin", "0123456789", true,
                        "123 Đường Admin, Quận 1", "Quản lý", 20000000.0
                );
                nhanVienDao.themNhanVien(adminNhanVien);
                nhanVienList.add(adminNhanVien);

                // Tạo tài khoản admin với mật khẩu đáp ứng yêu cầu
                TaiKhoan adminTaiKhoan = new TaiKhoan("admin", "Admin123", maNhanVien);
                taiKhoanDao.themTaiKhoan(adminTaiKhoan);

                System.out.println("Đã tạo tài khoản admin mặc định: admin / Admin123");
            } else {
                System.out.println("Tài khoản admin đã tồn tại.");
            }

            // Tải danh sách nhân viên
         // Tải danh sách nhân viên
            nhanVienList.setAll(nhanVienDao.getAllNhanVien());
            for (NhanVien nv : nhanVienList) {
                if (nv.getHoaDonList() == null) {
                    nv.setHoaDonList(new ArrayList<>());
                }
            }
            System.out.println("Đã tải " + nhanVienList.size() + " nhân viên.");

            // Tải danh sách phòng
            phongList.setAll(phongDao.getAllPhong());
            System.out.println("Đã tải " + phongList.size() + " phòng.");

            // Tải danh sách khách hàng
            khachHangList.setAll(khachHangDao.getAllKhachHang());
            System.out.println("Đã tải " + khachHangList.size() + " khách hàng.");

            // Tải danh sách dịch vụ
            dichVuList.setAll(dichVuDao.getAllDichVu());
            System.out.println("Đã tải " + dichVuList.size() + " dịch vụ.");

            // Tải danh sách khuyến mãi
            khuyenMaiList.setAll(khuyenMaiDao.getAllKhuyenMai());
            System.out.println("Đã tải " + khuyenMaiList.size() + " khuyến mãi.");

            // Tải danh sách phiếu đặt phòng
            phieuDatPhongList.setAll(phieuDatPhongDao.getAllPhieuDatPhong());
            System.out.println("Đã tải " + phieuDatPhongList.size() + " phiếu đặt phòng.");

            // Tải danh sách chi tiết phiếu đặt phòng
            chitietPhieuDatPhongList.setAll(chitietPhieuDatPhongDao.getAllChitietPhieuDatPhong());
            Map<String, PhieuDatPhong> phieuDatPhongMap = new HashMap<>();
            phieuDatPhongList.forEach(p -> phieuDatPhongMap.put(p.getMaDatPhong(), p));
            for (ChitietPhieuDatPhong ct : chitietPhieuDatPhongList) {
                PhieuDatPhong pdp = phieuDatPhongMap.get(ct.getMaDatPhong());
                if (pdp != null) {
                    pdp.addChitietPhieuDatPhong(ct);
                }
            }
            System.out.println("Đã tải " + chitietPhieuDatPhongList.size() + " chi tiết phiếu đặt phòng.");

            // Tải danh sách hóa đơn
            hoaDonList.setAll(hoaDonDao.getAllHoaDon());
            Map<String, KhachHang> khachHangMap = new HashMap<>();
            khachHangList.forEach(kh -> khachHangMap.put(kh.getMaKhachHang(), kh));
            Map<String, NhanVien> nhanVienMap = new HashMap<>();
            nhanVienList.forEach(nv -> nhanVienMap.put(nv.getMaNhanVien(), nv));
            for (HoaDon hd : hoaDonList) {
                KhachHang kh = khachHangMap.get(hd.getMaKhachHang());
                if (kh != null) {
                    kh.addHoaDon(hd, hoaDonList);
                }
                NhanVien nv = nhanVienMap.get(hd.getMaNhanVien());
                if (nv != null) {
                    nv.addHoaDon(hd, hoaDonList);
                }
            }
            System.out.println("Đã tải " + hoaDonList.size() + " hóa đơn.");

            // Tải danh sách chi tiết hóa đơn
            chitietHoaDonList.setAll(chitietHoaDonDao.getAllChitietHoaDon());
            Map<String, HoaDon> hoaDonMap = new HashMap<>();
            hoaDonList.forEach(hd -> hoaDonMap.put(hd.getMaHoaDon(), hd));
            for (ChitietHoaDon cthd : chitietHoaDonList) {
                HoaDon hd = hoaDonMap.get(cthd.getMaHoaDon());
                if (hd != null) {
                    hd.addChitietHoaDon(cthd);
                }
            }
            System.out.println("Đã tải " + chitietHoaDonList.size() + " chi tiết hóa đơn.");

            // Tải danh sách phiếu dịch vụ
            phieuDichVuList.setAll(phieuDichVuDao.getAllPhieuDichVu());
            System.out.println("Đã tải " + phieuDichVuList.size() + " phiếu dịch vụ.");

            // Tải danh sách chi tiết phiếu dịch vụ
            chitietPhieuDichVuList.setAll(chitietPhieuDichVuDao.getAllChitietPhieuDichVu());
            Map<String, PhieuDichVu> phieuDichVuMap = new HashMap<>();
            phieuDichVuList.forEach(pdv -> phieuDichVuMap.put(pdv.getMaPhieuDichVu(), pdv));
            for (ChitietPhieuDichVu ctpdv : chitietPhieuDichVuList) {
                PhieuDichVu pdv = phieuDichVuMap.get(ctpdv.getMaPhieuDichVu());
                if (pdv != null) {
                    pdv.addChitietPhieuDichVu(ctpdv);
                }
            }
            System.out.println("Đã tải " + chitietPhieuDichVuList.size() + " chi tiết phiếu dịch vụ.");

            // Tải danh sách lịch sử chuyển phòng
            lichSuChuyenPhongList.setAll(lichSuChuyenPhongDao.getAllLichSuChuyenPhong());
            System.out.println("Đã tải " + lichSuChuyenPhongList.size() + " lịch sử chuyển phòng.");

        } catch (SQLException e) {
            System.err.println("Lỗi khi tải dữ liệu từ cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshData() {


        try {
            // Làm mới danh sách nhân viên
        	// Làm mới danh sách nhân viên
        	nhanVienList.setAll(nhanVienDao.getAllNhanVien());
        	for (NhanVien nv : nhanVienList) {
        	    if (nv.getHoaDonList() == null) {
        	        nv.setHoaDonList(new ArrayList<>());
        	    }
        	}

            // Làm mới danh sách phòng
            phongList.setAll(phongDao.getAllPhong());

            // Làm mới danh sách khách hàng
            khachHangList.clear();
            khachHangList.setAll(khachHangDao.getAllKhachHang());

            // Làm mới danh sách dịch vụ
            dichVuList.setAll(dichVuDao.getAllDichVu());

            // Làm mới danh sách khuyến mãi
            khuyenMaiList.setAll(khuyenMaiDao.getAllKhuyenMai());

            // Làm mới danh sách phiếu đặt phòng và chi tiết
            phieuDatPhongList.clear();
            chitietPhieuDatPhongList.clear();
            phieuDatPhongList.setAll(phieuDatPhongDao.getAllPhieuDatPhong());
            chitietPhieuDatPhongList.setAll(chitietPhieuDatPhongDao.getAllChitietPhieuDatPhong());
            Map<String, PhieuDatPhong> phieuDatPhongMap = new HashMap<>();
            phieuDatPhongList.forEach(p -> phieuDatPhongMap.put(p.getMaDatPhong(), p));
            for (ChitietPhieuDatPhong ct : chitietPhieuDatPhongList) {
                PhieuDatPhong pdp = phieuDatPhongMap.get(ct.getMaDatPhong());
                if (pdp != null) {
                    pdp.addChitietPhieuDatPhong(ct);
                }
            }

            // Làm mới danh sách hóa đơn và chi tiết
            hoaDonList.clear();
            chitietHoaDonList.clear();
            hoaDonList.setAll(hoaDonDao.getAllHoaDon());
            chitietHoaDonList.setAll(chitietHoaDonDao.getAllChitietHoaDon());
            Map<String, KhachHang> khachHangMap = new HashMap<>();
            khachHangList.forEach(kh -> khachHangMap.put(kh.getMaKhachHang(), kh));
            Map<String, NhanVien> nhanVienMap = new HashMap<>();
            nhanVienList.forEach(nv -> nhanVienMap.put(nv.getMaNhanVien(), nv));
            Map<String, HoaDon> hoaDonMap = new HashMap<>();
            hoaDonList.forEach(hd -> hoaDonMap.put(hd.getMaHoaDon(), hd));
            for (HoaDon hd : hoaDonList) {
                KhachHang kh = khachHangMap.get(hd.getMaKhachHang());
                if (kh != null) {
                    kh.addHoaDon(hd, hoaDonList);
                }
                NhanVien nv = nhanVienMap.get(hd.getMaNhanVien());
                if (nv != null) {
                    nv.addHoaDon(hd, hoaDonList);
                }
            }
            for (ChitietHoaDon cthd : chitietHoaDonList) {
                HoaDon hd = hoaDonMap.get(cthd.getMaHoaDon());
                if (hd != null) {
                    hd.addChitietHoaDon(cthd);
                }
            }

            // Làm mới danh sách phiếu dịch vụ và chi tiết
            phieuDichVuList.clear();
            chitietPhieuDichVuList.clear();
            phieuDichVuList.setAll(phieuDichVuDao.getAllPhieuDichVu());
            chitietPhieuDichVuList.setAll(chitietPhieuDichVuDao.getAllChitietPhieuDichVu());
            Map<String, PhieuDichVu> phieuDichVuMap = new HashMap<>();
            phieuDichVuList.forEach(pdv -> phieuDichVuMap.put(pdv.getMaPhieuDichVu(), pdv));
            for (ChitietPhieuDichVu ctpdv : chitietPhieuDichVuList) {
                PhieuDichVu pdv = phieuDichVuMap.get(ctpdv.getMaPhieuDichVu());
                if (pdv != null) {
                    pdv.addChitietPhieuDichVu(ctpdv);
                }
            }

            // Làm mới danh sách lịch sử chuyển phòng
            lichSuChuyenPhongList.clear();
            lichSuChuyenPhongList.setAll(lichSuChuyenPhongDao.getAllLichSuChuyenPhong());

            System.out.println("Đã làm mới tất cả dữ liệu.");

        } catch (SQLException e) {
            System.err.println("Lỗi khi làm mới dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return taiKhoanDao.getAllTaiKhoan().stream()
                .anyMatch(tk -> tk.getTenDangNhap().equalsIgnoreCase(username.trim()));
    }

    // Getters
    public ObservableList<Phong> getPhongList() { return phongList; }
    public ObservableList<HoaDon> getHoaDonList() { return hoaDonList; }
    public ObservableList<KhachHang> getKhachHangList() { return khachHangList; }
    public ObservableList<DichVu> getDichVuList() { return dichVuList; }
    public ObservableList<KhuyenMai> getKhuyenMaiList() { return khuyenMaiList; }
    public ObservableList<NhanVien> getNhanVienList() { return nhanVienList; }
    public ObservableList<PhieuDatPhong> getPhieuDatPhongList() { return phieuDatPhongList; }
    public ObservableList<ChitietPhieuDatPhong> getChitietPhieuDatPhongList() { return chitietPhieuDatPhongList; }
    public ObservableList<ChitietHoaDon> getChitietHoaDonList() { return chitietHoaDonList; }
    public ObservableList<PhieuDichVu> getPhieuDichVuList() { return phieuDichVuList; }
    public ObservableList<ChitietPhieuDichVu> getChitietPhieuDichVuList() { return chitietPhieuDichVuList; }
    public ObservableList<LichSuChuyenPhong> getLichSuChuyenPhongList() { return lichSuChuyenPhongList; } // Getter cho LichSuChuyenPhong
    public NhanVien getCurrentNhanVien() { return currentNhanVien; }

    // Setters
    public void setPhongList(ObservableList<Phong> newPhongList) { phongList.setAll(newPhongList); }
    public void setHoaDonList(ObservableList<HoaDon> newHoaDonList) { hoaDonList.setAll(newHoaDonList); }
    public void setKhachHangList(ObservableList<KhachHang> newKhachHangList) { khachHangList.setAll(newKhachHangList); }
    public void setDichVuList(ObservableList<DichVu> newDichVuList) { dichVuList.setAll(newDichVuList); }
    public void setKhuyenMaiList(ObservableList<KhuyenMai> newKhuyenMaiList) { khuyenMaiList.setAll(newKhuyenMaiList); }
    public void setNhanVienList(ObservableList<NhanVien> newNhanVienList) { nhanVienList.setAll(newNhanVienList); }
    public void setPhieuDatPhongList(ObservableList<PhieuDatPhong> newPhieuDatPhongList) { phieuDatPhongList.setAll(newPhieuDatPhongList); }
    public void setChitietPhieuDatPhongList(ObservableList<ChitietPhieuDatPhong> newChitietPhieuDatPhongList) { chitietPhieuDatPhongList.setAll(newChitietPhieuDatPhongList); }
    public void setChitietHoaDonList(ObservableList<ChitietHoaDon> newChitietHoaDonList) { chitietHoaDonList.setAll(newChitietHoaDonList); }
    public void setPhieuDichVuList(ObservableList<PhieuDichVu> newPhieuDichVuList) { phieuDichVuList.setAll(newPhieuDichVuList); }
    public void setChitietPhieuDichVuList(ObservableList<ChitietPhieuDichVu> newChitietPhieuDichVuList) { chitietPhieuDichVuList.setAll(newChitietPhieuDichVuList); }
    public void setLichSuChuyenPhongList(ObservableList<LichSuChuyenPhong> newLichSuChuyenPhongList) { lichSuChuyenPhongList.setAll(newLichSuChuyenPhongList); } // Setter cho LichSuChuyenPhong
    public void setCurrentNhanVien(NhanVien nhanVien) { this.currentNhanVien = nhanVien; }

    // Thêm Listeners
    public void addPhongListChangeListener(Runnable listener) { phongListChangeListeners.add(listener); }
    public void addHoaDonListChangeListener(Runnable listener) { hoaDonListChangeListeners.add(listener); }
    public void addKhachHangListChangeListener(Runnable listener) { khachHangListChangeListeners.add(listener); }
    public void addDichVuListChangeListener(Runnable listener) { dichVuListChangeListeners.add(listener); }
    public void addKhuyenMaiListChangeListener(Runnable listener) { khuyenMaiListChangeListeners.add(listener); }
    public void addNhanVienListChangeListener(Runnable listener) { nhanVienListChangeListeners.add(listener); }
    public void addPhieuDatPhongListChangeListener(Runnable listener) { phieuDatPhongListChangeListeners.add(listener); }
    public void addChitietPhieuDatPhongListChangeListener(Runnable listener) { chitietPhieuDatPhongListChangeListeners.add(listener); }
    public void addChitietHoaDonListChangeListener(Runnable listener) { chitietHoaDonListChangeListeners.add(listener); }
    public void addPhieuDichVuListChangeListener(Runnable listener) { phieuDichVuListChangeListeners.add(listener); }
    public void addChitietPhieuDichVuListChangeListener(Runnable listener) { chitietPhieuDichVuListChangeListeners.add(listener); }
    public void addLichSuChuyenPhongListChangeListener(Runnable listener) { lichSuChuyenPhongListChangeListeners.add(listener); } // Listener cho LichSuChuyenPhong

    // Thêm dữ liệu
    public void addPhong(Phong phong) throws SQLException {
        if (phong == null || phongList.stream().anyMatch(p -> p.getMaPhong().equals(phong.getMaPhong()))) {
            throw new IllegalArgumentException("Phòng không hợp lệ hoặc đã tồn tại!");
        }
        phongDao.themPhong(phong);
        phongList.add(phong);
    }

    public void addHoaDon(HoaDon hoaDon) throws SQLException {
        if (hoaDon == null || hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(hoaDon.getMaHoaDon()))) {
            throw new IllegalArgumentException("Hóa đơn không hợp lệ hoặc đã tồn tại!");
        }
        if (!khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(hoaDon.getMaKhachHang()))) {
            throw new SQLException("Mã khách hàng không tồn tại!");
        }
        if (!nhanVienList.stream().anyMatch(nv -> nv.getMaNhanVien().equals(hoaDon.getMaNhanVien()))) {
            throw new SQLException("Mã nhân viên không tồn tại!");
        }
        if (hoaDon.getMaKhuyenMai() != null && !extracted(hoaDon).isEmpty() &&
            !khuyenMaiList.stream().anyMatch(km -> km.getMaChuongTrinhKhuyenMai().equals(hoaDon.getMaKhuyenMai()))) {
            throw new SQLException("Mã khuyến mãi không tồn tại!");
        }
        hoaDonDao.themHoaDon(hoaDon);
        hoaDonList.add(hoaDon);
        KhachHang kh = khachHangList.stream()
                .filter(k -> k.getMaKhachHang().equals(hoaDon.getMaKhachHang()))
                .findFirst().orElse(null);
        if (kh != null) {
            kh.addHoaDon(hoaDon, hoaDonList);
        }
        NhanVien nv = nhanVienList.stream()
                .filter(n -> n.getMaNhanVien().equals(hoaDon.getMaNhanVien()))
                .findFirst().orElse(null);
        if (nv != null) {
            nv.addHoaDon(hoaDon, hoaDonList);
        }
    }

    public void addKhachHang(KhachHang khachHang) throws SQLException {
        if (khachHang == null || khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(khachHang.getMaKhachHang()))) {
            throw new IllegalArgumentException("Khách hàng không hợp lệ hoặc đã tồn tại!");
        }
        khachHangDao.themKhachHang(khachHang);
        khachHangList.add(khachHang);
    }

    public void addDichVu(DichVu dichVu) throws SQLException {
        if (dichVu == null || dichVuList.stream().anyMatch(dv -> dv.getMaDichVu().equals(dichVu.getMaDichVu()))) {
            throw new IllegalArgumentException("Dịch vụ không hợp lệ hoặc đã tồn tại!");
        }
        dichVuDao.themDichVu(dichVu);
        dichVuList.add(dichVu);
    }

    public void addKhuyenMai(KhuyenMai khuyenMai) throws SQLException {
        if (khuyenMai == null || khuyenMaiList.stream().anyMatch(km -> km.getMaChuongTrinhKhuyenMai().equals(khuyenMai.getMaChuongTrinhKhuyenMai()))) {
            throw new IllegalArgumentException("Khuyến mãi không hợp lệ hoặc đã tồn tại!");
        }
        khuyenMaiDao.themKhuyenMai(khuyenMai);
        khuyenMaiList.add(khuyenMai);
    }

    public void addNhanVien(NhanVien nhanVien) throws SQLException {
        if (nhanVien == null || nhanVienList.stream().anyMatch(nv -> nv.getMaNhanVien().equals(nhanVien.getMaNhanVien()))) {
            throw new IllegalArgumentException("Nhân viên không hợp lệ hoặc đã tồn tại!");
        }
        nhanVienDao.themNhanVien(nhanVien);
        nhanVienList.add(nhanVien);
    }

    public void addPhieuDatPhong(PhieuDatPhong phieu) throws SQLException {
        if (phieu == null || phieuDatPhongList.stream().anyMatch(p -> p.getMaDatPhong().equals(phieu.getMaDatPhong()))) {
            throw new IllegalArgumentException("Phiếu đặt phòng không hợp lệ hoặc đã tồn tại!");
        }
        if (!khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(phieu.getMaKhachHang()))) {
            throw new SQLException("Mã khách hàng không tồn tại!");
        }
        if (phieu.getMaHoaDon() != null && !phieu.getMaHoaDon().isEmpty() &&
            !hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(phieu.getMaHoaDon()))) {
            throw new SQLException("Mã hóa đơn không tồn tại!");
        }
        phieuDatPhongDao.themPhieuDatPhong(phieu);
        phieuDatPhongList.add(phieu);
    }
    public void addChitietPhieuDatPhong(ChitietPhieuDatPhong chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Chi tiết phiếu đặt phòng không hợp lệ!");
        }
        if (!phieuDatPhongList.stream().anyMatch(p -> p.getMaDatPhong().equals(chitiet.getMaDatPhong()))) {
            throw new SQLException("Mã phiếu đặt phòng không tồn tại!");
        }
        if (!phongList.stream().anyMatch(p -> p.getMaPhong().equals(chitiet.getMaPhong()))) {
            throw new SQLException("Mã phòng không tồn tại!");
        }
        if (chitietPhieuDatPhongList.stream().anyMatch(ct ->
                ct.getMaDatPhong().equals(chitiet.getMaDatPhong()) &&
                ct.getMaPhong().equals(chitiet.getMaPhong()))) {
            throw new SQLException("Chi tiết phiếu đặt phòng đã tồn tại!");
        }
        // Kiểm tra tính hợp lệ của maPhong
        if (!chitiet.getMaPhong().matches("P\\d{3}")) {
            throw new IllegalArgumentException("Mã phòng không đúng định dạng (Pxxx): " + chitiet.getMaPhong());
        }

        // Log để debug
        System.out.println("addChitietPhieuDatPhong - MaDatPhong: " + chitiet.getMaDatPhong() + ", MaPhong: " + chitiet.getMaPhong());

        chitietPhieuDatPhongDao.themChitietPhieuDatPhong(chitiet);
        chitietPhieuDatPhongList.add(chitiet);

        // Gắn vào phiếu đặt phòng tương ứng
        phieuDatPhongList.stream()
                .filter(p -> p.getMaDatPhong().equals(chitiet.getMaDatPhong()))
                .findFirst()
                .ifPresent(p -> p.addChitietPhieuDatPhong(chitiet));

        // Thông báo thay đổi
        notifyListeners(chitietPhieuDatPhongListChangeListeners);
    }


    public void addChitietHoaDon(ChitietHoaDon chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không hợp lệ!");
        }
        if (!hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(chitiet.getMaHoaDon()))) {
            throw new SQLException("Mã hóa đơn không tồn tại!");
        }
        if (chitiet.getMaPhong() != null && !phongList.stream().anyMatch(p -> p.getMaPhong().equals(chitiet.getMaPhong()))) {
            throw new SQLException("Mã phòng không tồn tại!");
        }
        chitietHoaDonDao.themChitietHoaDon(chitiet);
        chitietHoaDonList.add(chitiet);
        hoaDonList.stream()
            .filter(hd -> hd.getMaHoaDon().equals(chitiet.getMaHoaDon()))
            .findFirst()
            .ifPresent(hd -> hd.addChitietHoaDon(chitiet));
    }

    public void addPhieuDichVu(PhieuDichVu phieu) throws SQLException {
        if (phieu == null || phieuDichVuList.stream().anyMatch(p -> p.getMaPhieuDichVu().equals(phieu.getMaPhieuDichVu()))) {
            throw new IllegalArgumentException("Phiếu dịch vụ không hợp lệ hoặc đã tồn tại!");
        }
        if (phieu.getMaHoaDon() != null && !phieu.getMaHoaDon().isEmpty() &&
            !hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(phieu.getMaHoaDon()))) {
            throw new SQLException("Mã hóa đơn không tồn tại!");
        }
        phieuDichVuDao.themPhieuDichVu(phieu);
        phieuDichVuList.add(phieu);
    }

    public void addChitietPhieuDichVu(ChitietPhieuDichVu chitiet) throws SQLException {
        if (chitiet == null || chitietPhieuDichVuList.stream()
                .anyMatch(ct -> ct.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()) && ct.getMaDichVu().equals(chitiet.getMaDichVu()))) {
            throw new IllegalArgumentException("Chi tiết phiếu dịch vụ không hợp lệ hoặc đã tồn tại!");
        }
        if (!phieuDichVuList.stream().anyMatch(p -> p.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()))) {
            throw new SQLException("Mã phiếu dịch vụ không tồn tại!");
        }
        if (!dichVuList.stream().anyMatch(dv -> dv.getMaDichVu().equals(chitiet.getMaDichVu()))) {
            throw new SQLException("Mã dịch vụ không tồn tại!");
        }
        chitietPhieuDichVuDao.themChitietPhieuDichVu(chitiet);
        chitietPhieuDichVuList.add(chitiet);
        phieuDichVuList.stream()
                .filter(p -> p.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()))
                .findFirst()
                .ifPresent(p -> p.addChitietPhieuDichVu(chitiet));
    }

    public void addLichSuChuyenPhong(LichSuChuyenPhong lscp) throws SQLException {
        if (lscp == null || lichSuChuyenPhongList.stream().anyMatch(l -> l.getMaLichSu().equals(lscp.getMaLichSu()))) {
            throw new IllegalArgumentException("Lịch sử chuyển phòng không hợp lệ hoặc đã tồn tại!");
        }
        if (!phieuDatPhongList.stream().anyMatch(p -> p.getMaDatPhong().equals(lscp.getMaPhieuDat()))) {
            throw new SQLException("Mã phiếu đặt phòng không tồn tại!");
        }
        if (!phongList.stream().anyMatch(p -> p.getMaPhong().equals(lscp.getMaPhongCu()))) {
            throw new SQLException("Mã phòng cũ không tồn tại!");
        }
        if (!phongList.stream().anyMatch(p -> p.getMaPhong().equals(lscp.getMaPhongMoi()))) {
            throw new SQLException("Mã phòng mới không tồn tại!");
        }
        if (!nhanVienList.stream().anyMatch(nv -> nv.getMaNhanVien().equals(lscp.getMaNhanVien()))) {
            throw new SQLException("Mã nhân viên không tồn tại!");
        }
        lichSuChuyenPhongDao.themLichSuChuyenPhong(lscp);
        lichSuChuyenPhongList.add(lscp);
    }
    public void refreshChitietPhieuDatPhongList() throws SQLException {
        chitietPhieuDatPhongList.clear();
        chitietPhieuDatPhongList.setAll(chitietPhieuDatPhongDao.getAllChitietPhieuDatPhong());
        Map<String, PhieuDatPhong> phieuDatPhongMap = new HashMap<>();
        phieuDatPhongList.forEach(p -> phieuDatPhongMap.put(p.getMaDatPhong(), p));
        for (ChitietPhieuDatPhong ct : chitietPhieuDatPhongList) {
            PhieuDatPhong pdp = phieuDatPhongMap.get(ct.getMaDatPhong());
            if (pdp != null) {
                pdp.addChitietPhieuDatPhong(ct);
            }
        }
        notifyListeners(chitietPhieuDatPhongListChangeListeners);
        System.out.println("Đã làm mới chitietPhieuDatPhongList: " + chitietPhieuDatPhongList.size() + " bản ghi.");
    }
    // Cập nhật dữ liệu
    public void updatePhong(Phong phong) throws SQLException {
        if (phong == null) {
            throw new IllegalArgumentException("Phòng không hợp lệ!");
        }
        Phong existingPhong = phongList.stream()
                .filter(p -> p.getMaPhong().equals(phong.getMaPhong()))
                .findFirst().orElseThrow(() -> new SQLException("Phòng không tồn tại trong danh sách!"));
        phongDao.suaPhong(phong);
        int index = phongList.indexOf(existingPhong);
        if (index >= 0) {
            phongList.set(index, phong);
        }
    }

   public void updateHoaDon(HoaDon hoaDon) throws SQLException {
    if (hoaDon == null) {
        throw new IllegalArgumentException("Hóa đơn không hợp lệ!");
    }

    if (!khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(hoaDon.getMaKhachHang()))) {
        throw new SQLException("Mã khách hàng không tồn tại!");
    }

    if (!nhanVienList.stream().anyMatch(nv -> nv.getMaNhanVien().equals(hoaDon.getMaNhanVien()))) {
        throw new SQLException("Mã nhân viên không tồn tại!");
    }

    // Kiểm tra maKhuyenMai
    String maKhuyenMai = (String) hoaDon.getMaKhuyenMai();
    if (maKhuyenMai != null && !maKhuyenMai.trim().isEmpty()) {
        if (!khuyenMaiList.stream().anyMatch(km -> km.getMaChuongTrinhKhuyenMai().equals(maKhuyenMai))) {
            throw new SQLException("Mã khuyến mãi không tồn tại!");
        }
    }

    HoaDon existingHoaDon = hoaDonList.stream()
            .filter(hd -> hd.getMaHoaDon().equals(hoaDon.getMaHoaDon()))
            .findFirst()
            .orElseThrow(() -> new SQLException("Hóa đơn không tồn tại trong danh sách!"));

    hoaDonDao.suaHoaDon(hoaDon);

    int index = hoaDonList.indexOf(existingHoaDon);
    if (index >= 0) {
        hoaDonList.set(index, hoaDon);

        // Cập nhật liên kết với Khách Hàng
        KhachHang kh = khachHangList.stream()
                .filter(k -> k.getMaKhachHang().equals(hoaDon.getMaKhachHang()))
                .findFirst()
                .orElse(null);

        if (kh != null) {
            List<HoaDon> khHoaDonList = kh.getHoaDonList();
            if (khHoaDonList == null) {
                kh.setHoaDonList(new ArrayList<>());
                khHoaDonList = kh.getHoaDonList();
            }
            khHoaDonList.removeIf(hd -> hd.getMaHoaDon().equals(hoaDon.getMaHoaDon()));
            kh.addHoaDon(hoaDon, hoaDonList);
        }

        // Cập nhật liên kết với Nhân Viên
        NhanVien nv = nhanVienList.stream()
                .filter(n -> n.getMaNhanVien().equals(hoaDon.getMaNhanVien()))
                .findFirst()
                .orElse(null);

        if (nv != null) {
            List<HoaDon> nvHoaDonList = nv.getHoaDonList();
            if (nvHoaDonList == null) {
                nv.setHoaDonList(new ArrayList<>());
                nvHoaDonList = nv.getHoaDonList();
            }
            nvHoaDonList.removeIf(hd -> hd.getMaHoaDon().equals(hoaDon.getMaHoaDon()));
            nv.addHoaDon(hoaDon, hoaDonList);
        }
    }

    notifyListeners(hoaDonListChangeListeners);
}


	private List<Phong> extracted(HoaDon hoaDon) {
		return (List<Phong>) hoaDon.getMaKhuyenMai();
	}

    public void updateKhachHang(KhachHang khachHang) throws SQLException {
        if (khachHang == null) {
            throw new IllegalArgumentException("Khách hàng không hợp lệ!");
        }
        KhachHang existingKhachHang = khachHangList.stream()
                .filter(kh -> kh.getMaKhachHang().equals(khachHang.getMaKhachHang()))
                .findFirst().orElseThrow(() -> new SQLException("Khách hàng không tồn tại trong danh sách!"));
        khachHangDao.suaKhachHang(khachHang);
        int index = khachHangList.indexOf(existingKhachHang);
        if (index >= 0) {
            khachHangList.set(index, khachHang);
        }
    }

    public void updateDichVu(DichVu dichVu) throws SQLException {
        if (dichVu == null) {
            throw new IllegalArgumentException("Dịch vụ không hợp lệ!");
        }
        DichVu existingDichVu = dichVuList.stream()
                .filter(dv -> dv.getMaDichVu().equals(dichVu.getMaDichVu()))
                .findFirst().orElseThrow(() -> new SQLException("Dịch vụ không tồn tại trong danh sách!"));
        dichVuDao.suaDichVu(dichVu);
        int index = dichVuList.indexOf(existingDichVu);
        if (index >= 0) {
            dichVuList.set(index, dichVu);
        }
    }

    public void updateKhuyenMai(KhuyenMai khuyenMai) throws SQLException {
        if (khuyenMai == null) {
            throw new IllegalArgumentException("Khuyến mãi không hợp lệ!");
        }
        KhuyenMai existingKhuyenMai = khuyenMaiList.stream()
                .filter(km -> km.getMaChuongTrinhKhuyenMai().equals(khuyenMai.getMaChuongTrinhKhuyenMai()))
                .findFirst().orElseThrow(() -> new SQLException("Khuyến mãi không tồn tại trong danh sách!"));
        khuyenMaiDao.suaKhuyenMai(khuyenMai);
        int index = khuyenMaiList.indexOf(existingKhuyenMai);
        if (index >= 0) {
            khuyenMaiList.set(index, khuyenMai);
        }
    }

    public void updateNhanVien(NhanVien nhanVien) throws SQLException {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không hợp lệ!");
        }
        NhanVien existingNhanVien = nhanVienList.stream()
                .filter(nv -> nv.getMaNhanVien().equals(nhanVien.getMaNhanVien()))
                .findFirst().orElseThrow(() -> new SQLException("Nhân viên không tồn tại trong danh sách!"));
        nhanVienDao.suaNhanVien(nhanVien);
        int index = nhanVienList.indexOf(existingNhanVien);
        if (index >= 0) {
            nhanVienList.set(index, nhanVien);
        }
    }

    public void updatePhieuDatPhong(PhieuDatPhong phieu) throws SQLException {
        if (phieu == null) {
            throw new IllegalArgumentException("Phiếu đặt phòng không hợp lệ!");
        }
        if (!khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(phieu.getMaKhachHang()))) {
            throw new SQLException("Mã khách hàng không tồn tại!");
        }
        if (phieu.getMaHoaDon() != null && !phieu.getMaHoaDon().isEmpty() &&
            !hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(phieu.getMaHoaDon()))) {
            throw new SQLException("Mã hóa đơn không tồn tại!");
        }
        PhieuDatPhong existingPhieu = phieuDatPhongList.stream()
                .filter(p -> p.getMaDatPhong().equals(phieu.getMaDatPhong()))
                .findFirst().orElseThrow(() -> new SQLException("Phiếu đặt phòng không tồn tại trong danh sách!"));
        phieuDatPhongDao.suaPhieuDatPhong(phieu);
        int index = phieuDatPhongList.indexOf(existingPhieu);
        if (index >= 0) {
            phieuDatPhongList.set(index, phieu);
        }
    }

    public void updateChitietPhieuDatPhong(ChitietPhieuDatPhong chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Chi tiết phiếu đặt phòng không hợp lệ!");
        }
        if (!phieuDatPhongList.stream().anyMatch(p -> p.getMaDatPhong().equals(chitiet.getMaDatPhong()))) {
            throw new SQLException("Mã phiếu đặt phòng không tồn tại!");
        }
        if (!phongList.stream().anyMatch(p -> p.getMaPhong().equals(chitiet.getMaPhong()))) {
            throw new SQLException("Mã phòng không tồn tại!");
        }
        ChitietPhieuDatPhong existingChitiet = chitietPhieuDatPhongList.stream()
                .filter(ct -> ct.getMaDatPhong().equals(chitiet.getMaDatPhong()) && ct.getMaPhong().equals(chitiet.getMaPhong()))
                .findFirst().orElseThrow(() -> new SQLException("Chi tiết phiếu đặt phòng không tồn tại trong danh sách!"));
        chitietPhieuDatPhongDao.suaChitietPhieuDatPhong(chitiet);
        int index = chitietPhieuDatPhongList.indexOf(existingChitiet);
        if (index >= 0) {
            chitietPhieuDatPhongList.set(index, chitiet);
        }
    }

    public void updateChitietHoaDon(ChitietHoaDon chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không hợp lệ!");
        }
        if (!hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(chitiet.getMaHoaDon()))) {
            throw new SQLException("Mã hóa đơn không tồn tại!");
        }
        if (!phongList.stream().anyMatch(p -> p.getMaPhong().equals(chitiet.getMaPhong()))) {
            throw new SQLException("Mã phòng không tồn tại!");
        }
        ChitietHoaDon existingChitiet = chitietHoaDonList.stream()
                .filter(ct -> ct.getMaHoaDon().equals(chitiet.getMaHoaDon()) && ct.getMaPhong().equals(chitiet.getMaPhong()))
                .findFirst().orElseThrow(() -> new SQLException("Chi tiết hóa đơn không tồn tại trong danh sách!"));
        chitietHoaDonDao.suaChitietHoaDon(chitiet);
        int index = chitietHoaDonList.indexOf(existingChitiet);
        if (index >= 0) {
            chitietHoaDonList.set(index, chitiet);
        }
    }

    public void updatePhieuDichVu(PhieuDichVu phieu) throws SQLException {
        if (phieu == null) {
            throw new IllegalArgumentException("Phiếu dịch vụ không hợp lệ!");
        }
        if (phieu.getMaHoaDon() != null && !phieu.getMaHoaDon().isEmpty() &&
            !hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(phieu.getMaHoaDon()))) {
            throw new SQLException("Mã hóa đơn không tồn tại!");
        }
        PhieuDichVu existingPhieu = phieuDichVuList.stream()
                .filter(p -> p.getMaPhieuDichVu().equals(phieu.getMaPhieuDichVu()))
                .findFirst().orElseThrow(() -> new SQLException("Phiếu dịch vụ không tồn tại trong danh sách!"));
        phieuDichVuDao.suaPhieuDichVu(phieu);
        int index = phieuDichVuList.indexOf(existingPhieu);
        if (index >= 0) {
            phieuDichVuList.set(index, phieu);
        }
    }

    public void updateChitietPhieuDichVu(ChitietPhieuDichVu chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Chi tiết phiếu dịch vụ không hợp lệ!");
        }
        if (!phieuDichVuList.stream().anyMatch(p -> p.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()))) {
            throw new SQLException("Mã phiếu dịch vụ không tồn tại!");
        }
        if (!dichVuList.stream().anyMatch(dv -> dv.getMaDichVu().equals(chitiet.getMaDichVu()))) {
            throw new SQLException("Mã dịch vụ không tồn tại!");
        }
        ChitietPhieuDichVu existingChitiet = chitietPhieuDichVuList.stream()
                .filter(ct -> ct.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()) && ct.getMaDichVu().equals(chitiet.getMaDichVu()))
                .findFirst().orElseThrow(() -> new SQLException("Chi tiết phiếu dịch vụ không tồn tại trong danh sách!"));
        chitietPhieuDichVuDao.suaChitietPhieuDichVu(chitiet);
        int index = chitietPhieuDichVuList.indexOf(existingChitiet);
        if (index >= 0) {
            chitietPhieuDichVuList.set(index, chitiet);
        }
    }

    public void updateLichSuChuyenPhong(LichSuChuyenPhong lscp) throws SQLException {
        if (lscp == null) {
            throw new IllegalArgumentException("Lịch sử chuyển phòng không hợp lệ!");
        }
        if (!phieuDatPhongList.stream().anyMatch(p -> p.getMaDatPhong().equals(lscp.getMaPhieuDat()))) {
            throw new SQLException("Mã phiếu đặt phòng không tồn tại!");
        }
        if (!phongList.stream().anyMatch(p -> p.getMaPhong().equals(lscp.getMaPhongCu()))) {
            throw new SQLException("Mã phòng cũ không tồn tại!");
        }
        if (!phongList.stream().anyMatch(p -> p.getMaPhong().equals(lscp.getMaPhongMoi()))) {
            throw new SQLException("Mã phòng mới không tồn tại!");
        }
        if (!nhanVienList.stream().anyMatch(nv -> nv.getMaNhanVien().equals(lscp.getMaNhanVien()))) {
            throw new SQLException("Mã nhân viên không tồn tại!");
        }
        LichSuChuyenPhong existingLscp = lichSuChuyenPhongList.stream()
                .filter(l -> l.getMaLichSu().equals(lscp.getMaLichSu()))
                .findFirst().orElseThrow(() -> new SQLException("Lịch sử chuyển phòng không tồn tại trong danh sách!"));
        lichSuChuyenPhongDao.suaLichSuChuyenPhong(lscp);
        int index = lichSuChuyenPhongList.indexOf(existingLscp);
        if (index >= 0) {
            lichSuChuyenPhongList.set(index, lscp);
        }
    }

    // Xóa dữ liệu
    public void deletePhong(String maPhong) throws SQLException {
        Phong phong = phongList.stream()
                .filter(p -> p.getMaPhong().equals(maPhong))
                .findFirst().orElseThrow(() -> new SQLException("Phòng không tồn tại!"));
        if (chitietPhieuDatPhongList.stream().anyMatch(ct -> ct.getMaPhong().equals(maPhong)) ||
            chitietHoaDonList.stream().anyMatch(ct -> ct.getMaPhong().equals(maPhong)) ||
            lichSuChuyenPhongList.stream().anyMatch(l -> l.getMaPhongCu().equals(maPhong) || l.getMaPhongMoi().equals(maPhong))) {
            throw new SQLException("Không thể xóa phòng vì đã được sử dụng trong phiếu đặt phòng, hóa đơn hoặc lịch sử chuyển phòng!");
        }
        phongDao.xoaPhong(maPhong);
        phongList.remove(phong);
    }

    public void deleteHoaDon(String maHoaDon) throws SQLException {
        HoaDon hoaDon = hoaDonList.stream()
                .filter(hd -> hd.getMaHoaDon().equals(maHoaDon))
                .findFirst().orElseThrow(() -> new SQLException("Hóa đơn không tồn tại!"));
        if (chitietHoaDonList.stream().anyMatch(ct -> ct.getMaHoaDon().equals(maHoaDon)) ||
            phieuDatPhongList.stream().anyMatch(p -> maHoaDon.equals(p.getMaHoaDon())) ||
            phieuDichVuList.stream().anyMatch(p -> maHoaDon.equals(p.getMaHoaDon()))) {
            throw new SQLException("Không thể xóa hóa đơn vì đã được sử dụng trong chi tiết hóa đơn, phiếu đặt phòng hoặc phiếu dịch vụ!");
        }
        hoaDonDao.xoaHoaDon(maHoaDon);
        hoaDonList.remove(hoaDon);
        KhachHang kh = khachHangList.stream()
                .filter(k -> k.getMaKhachHang().equals(hoaDon.getMaKhachHang()))
                .findFirst().orElse(null);
        if (kh != null) {
            kh.getHoaDonList().remove(hoaDon);
        }
        NhanVien nv = nhanVienList.stream()
                .filter(n -> n.getMaNhanVien().equals(hoaDon.getMaNhanVien()))
                .findFirst().orElse(null);
        if (nv != null) {
            nv.getHoaDonList().remove(hoaDon);
        }
    }

    public void deleteKhachHang(String maKhachHang) throws SQLException {
        KhachHang khachHang = khachHangList.stream()
                .filter(kh -> kh.getMaKhachHang().equals(maKhachHang))
                .findFirst().orElseThrow(() -> new SQLException("Khách hàng không tồn tại!"));
        if (hoaDonList.stream().anyMatch(hd -> hd.getMaKhachHang().equals(maKhachHang)) ||
            phieuDatPhongList.stream().anyMatch(p -> p.getMaKhachHang().equals(maKhachHang))) {
            throw new SQLException("Không thể xóa khách hàng vì đã có hóa đơn hoặc phiếu đặt phòng liên quan!");
        }
        khachHangDao.xoaKhachHang(maKhachHang);
        khachHangList.remove(khachHang);
    }

    public void deleteDichVu(String maDichVu) throws SQLException {
        DichVu dichVu = dichVuList.stream()
                .filter(dv -> dv.getMaDichVu().equals(maDichVu))
                .findFirst().orElseThrow(() -> new SQLException("Dịch vụ không tồn tại!"));
        if (chitietPhieuDichVuList.stream().anyMatch(ct -> ct.getMaDichVu().equals(maDichVu))) {
            throw new SQLException("Không thể xóa dịch vụ vì đã được sử dụng trong chi tiết phiếu dịch vụ!");
        }
        dichVuDao.xoaDichVu(maDichVu);
        dichVuList.remove(dichVu);
    }

    public void deleteKhuyenMai(String maKhuyenMai) throws SQLException {
        KhuyenMai khuyenMai = khuyenMaiList.stream()
                .filter(km -> km.getMaChuongTrinhKhuyenMai().equals(maKhuyenMai))
                .findFirst().orElseThrow(() -> new SQLException("Khuyến mãi không tồn tại!"));
        if (hoaDonList.stream().anyMatch(hd -> maKhuyenMai.equals(hd.getMaKhuyenMai()))) {
            throw new SQLException("Không thể xóa khuyến mãi vì đã được sử dụng trong hóa đơn!");
        }
        khuyenMaiDao.xoaKhuyenMai(maKhuyenMai);
        khuyenMaiList.remove(khuyenMai);
    }

    public void deleteNhanVien(String maNhanVien) throws SQLException {
        NhanVien nhanVien = nhanVienList.stream()
                .filter(nv -> nv.getMaNhanVien().equals(maNhanVien))
                .findFirst().orElseThrow(() -> new SQLException("Nhân viên không tồn tại!"));
        if (hoaDonList.stream().anyMatch(hd -> hd.getMaNhanVien().equals(maNhanVien)) ||
            taiKhoanDao.getAllTaiKhoan().stream().anyMatch(tk -> tk.getMaNhanVien() != null && tk.getMaNhanVien().equals(maNhanVien)) ||
            lichSuChuyenPhongList.stream().anyMatch(l -> l.getMaNhanVien().equals(maNhanVien))) {
            throw new SQLException("Không thể xóa nhân viên vì đã có hóa đơn, tài khoản hoặc lịch sử chuyển phòng liên quan!");
        }
        nhanVienDao.xoaNhanVien(maNhanVien);
        nhanVienList.remove(nhanVien);
    }

    public void deletePhieuDatPhong(String maDatPhong) throws SQLException {
        PhieuDatPhong phieu = phieuDatPhongList.stream()
                .filter(p -> p.getMaDatPhong().equals(maDatPhong))
                .findFirst().orElseThrow(() -> new SQLException("Phiếu đặt phòng không tồn tại!"));
        if (chitietPhieuDatPhongList.stream().anyMatch(ct -> ct.getMaDatPhong().equals(maDatPhong)) ||
            lichSuChuyenPhongList.stream().anyMatch(l -> l.getMaPhieuDat().equals(maDatPhong))) {
            throw new SQLException("Không thể xóa phiếu đặt phòng vì đã có chi tiết phiếu đặt phòng hoặc lịch sử chuyển phòng liên quan!");
        }
        phieuDatPhongDao.xoaPhieuDatPhong(maDatPhong);
        phieuDatPhongList.remove(phieu);
    }

    public void deleteChitietPhieuDatPhong(String maDatPhong, String maPhong) throws SQLException {
        ChitietPhieuDatPhong chitiet = chitietPhieuDatPhongList.stream()
                .filter(ct -> ct.getMaDatPhong().equals(maDatPhong) && ct.getMaPhong().equals(maPhong))
                .findFirst().orElseThrow(() -> new SQLException("Chi tiết phiếu đặt phòng không tồn tại!"));
        chitietPhieuDatPhongDao.xoaChitietPhieuDatPhong(maDatPhong, maPhong);
        chitietPhieuDatPhongList.remove(chitiet);
        phieuDatPhongList.stream()
                .filter(p -> p.getMaDatPhong().equals(maDatPhong))
                .findFirst()
                .ifPresent(p -> p.getChitietPhieuDatPhongs().remove(chitiet));
    }

    public void deleteChitietHoaDon(String maHoaDon, String maPhong) throws SQLException {
        ChitietHoaDon chitiet = chitietHoaDonList.stream()
                .filter(ct -> ct.getMaHoaDon().equals(maHoaDon) && ct.getMaPhong().equals(maPhong))
                .findFirst().orElseThrow(() -> new SQLException("Chi tiết hóa đơn không tồn tại!"));
        chitietHoaDonDao.xoaChitietHoaDon(maHoaDon, maPhong);
        chitietHoaDonList.remove(chitiet);
        hoaDonList.stream()
                .filter(hd -> hd.getMaHoaDon().equals(maHoaDon))
                .findFirst()
                .ifPresent(hd -> hd.getChitietHoaDons().remove(chitiet));
    }

    public void deletePhieuDichVu(String maPhieuDichVu) throws SQLException {
        PhieuDichVu phieu = phieuDichVuList.stream()
                .filter(p -> p.getMaPhieuDichVu().equals(maPhieuDichVu))
                .findFirst().orElseThrow(() -> new SQLException("Phiếu dịch vụ không tồn tại!"));
        if (chitietPhieuDichVuList.stream().anyMatch(ct -> ct.getMaPhieuDichVu().equals(maPhieuDichVu))) {
            throw new SQLException("Không thể xóa phiếu dịch vụ vì đã có chi tiết phiếu dịch vụ liên quan!");
        }
        phieuDichVuDao.xoaPhieuDichVu(maPhieuDichVu);
        phieuDichVuList.remove(phieu);
    }

    public void deleteChitietPhieuDichVu(String maPhieuDichVu, String maDichVu) throws SQLException {
        ChitietPhieuDichVu chitiet = chitietPhieuDichVuList.stream()
                .filter(ct -> ct.getMaPhieuDichVu().equals(maPhieuDichVu) && ct.getMaDichVu().equals(maDichVu))
                .findFirst().orElseThrow(() -> new SQLException("Chi tiết phiếu dịch vụ không tồn tại!"));
        chitietPhieuDichVuDao.xoaChitietPhieuDichVu(maPhieuDichVu, maDichVu);
        chitietPhieuDichVuList.remove(chitiet);
        phieuDichVuList.stream()
                .filter(p -> p.getMaPhieuDichVu().equals(maPhieuDichVu))
                .findFirst()
                .ifPresent(p -> p.getChitietPhieuDichVus().remove(chitiet));
    }

    public void deleteLichSuChuyenPhong(String maLichSu) throws SQLException {
        LichSuChuyenPhong lscp = lichSuChuyenPhongList.stream()
                .filter(l -> l.getMaLichSu().equals(maLichSu))
                .findFirst().orElseThrow(() -> new SQLException("Lịch sử chuyển phòng không tồn tại!"));
        lichSuChuyenPhongDao.xoaLichSuChuyenPhong(maLichSu);
        lichSuChuyenPhongList.remove(lscp);
    }

    private void notifyListeners(List<Runnable> listeners) {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
    
}