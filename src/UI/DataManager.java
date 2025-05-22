package UI;

import dao.*;
import model.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ConnectDB.ConnectDB;

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
    private final List<Runnable> lichSuChuyenPhongListChangeListeners = new ArrayList<>();
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
    private final TaiKhoan_Dao taiKhoanDao = new TaiKhoan_Dao();
    private NhanVien currentNhanVien;

    private DataManager() {
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
            List<TaiKhoan> taiKhoanList = taiKhoanDao.getAllTaiKhoan();
            boolean adminExists = taiKhoanList.stream()
                    .anyMatch(tk -> tk.getTenDangNhap().equals("admin"));

            if (!adminExists) {
                String maNhanVien = nhanVienDao.getNextMaNhanVien();
                NhanVien adminNhanVien = new NhanVien(
                        maNhanVien, "Admin", "0123456789", true,
                        "123 Admin Street, District 1", "Manager", 20000000.0
                );
                nhanVienDao.themNhanVien(adminNhanVien);
                nhanVienList.add(adminNhanVien);

                TaiKhoan adminTaiKhoan = new TaiKhoan("admin", "Admin123", maNhanVien);
                taiKhoanDao.themTaiKhoan(adminTaiKhoan);

                System.out.println("Created default admin account: admin / Admin123");
            } else {
                System.out.println("Admin account already exists.");
            }

            nhanVienList.setAll(nhanVienDao.getAllNhanVien());
            for (NhanVien nv : nhanVienList) {
                if (nv.getHoaDonList() == null) {
                    nv.setHoaDonList(new ArrayList<>());
                }
            }
            System.out.println("Loaded " + nhanVienList.size() + " employees.");

            phongList.setAll(phongDao.getAllPhong());
            System.out.println("Loaded " + phongList.size() + " rooms.");

            khachHangList.setAll(khachHangDao.getAllKhachHang());
            System.out.println("Loaded " + khachHangList.size() + " customers.");

            dichVuList.setAll(dichVuDao.getAllDichVu());
            System.out.println("Loaded " + dichVuList.size() + " services.");

            khuyenMaiList.setAll(khuyenMaiDao.getAllKhuyenMai());
            System.out.println("Loaded " + khuyenMaiList.size() + " promotions.");

            phieuDatPhongList.setAll(phieuDatPhongDao.getAllPhieuDatPhong());
            System.out.println("Loaded " + phieuDatPhongList.size() + " booking tickets.");

            chitietPhieuDatPhongList.setAll(chitietPhieuDatPhongDao.getAllChitietPhieuDatPhong());
            Map<String, PhieuDatPhong> phieuDatPhongMap = new HashMap<>();
            phieuDatPhongList.forEach(p -> phieuDatPhongMap.put(p.getMaDatPhong(), p));
            for (ChitietPhieuDatPhong ct : chitietPhieuDatPhongList) {
                PhieuDatPhong pdp = phieuDatPhongMap.get(ct.getMaDatPhong());
                if (pdp != null) {
                    pdp.addChitietPhieuDatPhong(ct);
                }
            }
            System.out.println("Loaded " + chitietPhieuDatPhongList.size() + " booking ticket details.");

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
            System.out.println("Loaded " + hoaDonList.size() + " invoices.");

            try {
                chitietHoaDonList.setAll(chitietHoaDonDao.getAllChitietHoaDon());
                Map<String, HoaDon> hoaDonMap = new HashMap<>();
                hoaDonList.forEach(hd -> hoaDonMap.put(hd.getMaHoaDon(), hd));
                for (ChitietHoaDon cthd : chitietHoaDonList) {
                    HoaDon hd = hoaDonMap.get(cthd.getMaHoaDon());
                    if (hd != null) {
                        hd.addChitietHoaDon(cthd);
                    }
                }
                System.out.println("Loaded " + chitietHoaDonList.size() + " invoice details.");
            } catch (SQLException e) {
                System.err.println("Error loading invoice details: " + e.getMessage());
                chitietHoaDonList.clear();
            }

            phieuDichVuList.setAll(phieuDichVuDao.getAllPhieuDichVu());
            System.out.println("Loaded " + phieuDichVuList.size() + " service tickets.");

            chitietPhieuDichVuList.setAll(chitietPhieuDichVuDao.getAllChitietPhieuDichVu());
            Map<String, PhieuDichVu> phieuDichVuMap = new HashMap<>();
            phieuDichVuList.forEach(pdv -> phieuDichVuMap.put(pdv.getMaPhieuDichVu(), pdv));
            for (ChitietPhieuDichVu ctpdv : chitietPhieuDichVuList) {
                PhieuDichVu pdv = phieuDichVuMap.get(ctpdv.getMaPhieuDichVu());
                if (pdv != null) {
                    pdv.addChitietPhieuDichVu(ctpdv);
                }
            }
            System.out.println("Loaded " + chitietPhieuDichVuList.size() + " service ticket details.");

        } catch (SQLException e) {
            System.err.println("Error loading data from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshBookingData() throws SQLException {
        phieuDatPhongList.clear();
        chitietPhieuDatPhongList.clear();
        phongList.clear();

        phieuDatPhongList.setAll(phieuDatPhongDao.getAllPhieuDatPhong());
        chitietPhieuDatPhongList.setAll(chitietPhieuDatPhongDao.getAllChitietPhieuDatPhong());
        phongList.setAll(phongDao.getAllPhong());

        Map<String, PhieuDatPhong> phieuDatPhongMap = new HashMap<>();
        phieuDatPhongList.forEach(p -> phieuDatPhongMap.put(p.getMaDatPhong(), p));
        for (ChitietPhieuDatPhong ct : chitietPhieuDatPhongList) {
            PhieuDatPhong pdp = phieuDatPhongMap.get(ct.getMaDatPhong());
            if (pdp != null) {
                pdp.addChitietPhieuDatPhong(ct);
            }
        }

        System.out.println("Refreshed booking and room data, maintaining room status from database.");
        notifyListeners(phongListChangeListeners);
        notifyListeners(phieuDatPhongListChangeListeners);
        notifyListeners(chitietPhieuDatPhongListChangeListeners);
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return taiKhoanDao.getAllTaiKhoan().stream()
                .anyMatch(tk -> tk.getTenDangNhap().equalsIgnoreCase(username.trim()));
    }

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

    public NhanVien getCurrentNhanVien() { return currentNhanVien; }

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

    public void setCurrentNhanVien(NhanVien nhanVien) { this.currentNhanVien = nhanVien; }

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
    public void addLichSuChuyenPhongListChangeListener(Runnable listener) { lichSuChuyenPhongListChangeListeners.add(listener); }

    public void addPhong(Phong phong) throws SQLException {
        if (phong == null || phongList.stream().anyMatch(p -> p.getMaPhong().equals(phong.getMaPhong()))) {
            throw new IllegalArgumentException("Invalid room or room already exists!");
        }
        phongDao.themPhong(phong);
        phongList.add(phong);
    }

    public void addHoaDon(HoaDon hoaDon) throws SQLException {
        if (hoaDon == null || hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(hoaDon.getMaHoaDon()))) {
            throw new IllegalArgumentException("Invalid invoice or invoice already exists!");
        }
        if (!khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(hoaDon.getMaKhachHang()))) {
            throw new SQLException("Customer ID does not exist!");
        }
        if (!nhanVienList.stream().anyMatch(nv -> nv.getMaNhanVien().equals(hoaDon.getMaNhanVien()))) {
            throw new SQLException("Employee ID does not exist!");
        }
        if (hoaDon.getMaKhuyenMai() != null && !((List<Phong>) hoaDon.getMaKhuyenMai()).isEmpty() &&
                !khuyenMaiList.stream().anyMatch(km -> km.getMaChuongTrinhKhuyenMai().equals(hoaDon.getMaKhuyenMai()))) {
            throw new SQLException("Promotion ID does not exist!");
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
        System.out.println("Added invoice: " + hoaDon.getMaHoaDon());
    }

    public void updateHoaDon(HoaDon hoaDon) throws SQLException {
        if (hoaDon == null) {
            throw new IllegalArgumentException("Invalid invoice!");
        }
        if (!khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(hoaDon.getMaKhachHang()))) {
            throw new SQLException("Customer ID does not exist!");
        }
        if (!nhanVienList.stream().anyMatch(nv -> nv.getMaNhanVien().equals(hoaDon.getMaNhanVien()))) {
            throw new SQLException("Employee ID does not exist!");
        }
        if (hoaDon.getMaKhuyenMai() != null && !((List<Phong>) hoaDon.getMaKhuyenMai()).isEmpty() &&
                !khuyenMaiList.stream().anyMatch(km -> km.getMaChuongTrinhKhuyenMai().equals(hoaDon.getMaKhuyenMai()))) {
            throw new SQLException("Promotion ID does not exist!");
        }
        HoaDon existingHoaDon = hoaDonList.stream()
                .filter(hd -> hd.getMaHoaDon().equals(hoaDon.getMaHoaDon()))
                .findFirst().orElseThrow(() -> new SQLException("Invoice does not exist in the list!"));
        hoaDonDao.suaHoaDon(hoaDon);
        int index = hoaDonList.indexOf(existingHoaDon);
        if (index >= 0) {
            hoaDonList.set(index, hoaDon);
        }
        KhachHang kh = khachHangList.stream()
                .filter(k -> k.getMaKhachHang().equals(hoaDon.getMaKhachHang()))
                .findFirst().orElse(null);
        if (kh != null) {
            kh.getHoaDonList().remove(existingHoaDon);
            kh.addHoaDon(hoaDon, hoaDonList);
        }
        NhanVien nv = nhanVienList.stream()
                .filter(n -> n.getMaNhanVien().equals(hoaDon.getMaNhanVien()))
                .findFirst().orElse(null);
        if (nv != null) {
            nv.getHoaDonList().remove(existingHoaDon);
            nv.addHoaDon(hoaDon, hoaDonList);
        }
        notifyListeners(hoaDonListChangeListeners);
        System.out.println("Updated invoice: " + hoaDon.getMaHoaDon() + ", Total amount: " + hoaDon.getTongTien());
    }

    public void addKhachHang(KhachHang khachHang) throws SQLException {
        if (khachHang == null || khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(khachHang.getMaKhachHang()))) {
            throw new IllegalArgumentException("Invalid customer or customer already exists!");
        }
        khachHangDao.themKhachHang(khachHang);
        khachHangList.add(khachHang);
    }

    public void addDichVu(DichVu dichVu) throws SQLException {
        if (dichVu == null || dichVuList.stream().anyMatch(dv -> dv.getMaDichVu().equals(dichVu.getMaDichVu()))) {
            throw new IllegalArgumentException("Invalid service or service already exists!");
        }
        dichVuDao.themDichVu(dichVu);
        dichVuList.add(dichVu);
    }

    public void addKhuyenMai(KhuyenMai khuyenMai) throws SQLException {
        if (khuyenMai == null || khuyenMaiList.stream().anyMatch(km -> km.getMaChuongTrinhKhuyenMai().equals(khuyenMai.getMaChuongTrinhKhuyenMai()))) {
            throw new IllegalArgumentException("Invalid promotion or promotion already exists!");
        }
        khuyenMaiDao.themKhuyenMai(khuyenMai);
        khuyenMaiList.add(khuyenMai);
    }

    public void addNhanVien(NhanVien nhanVien) throws SQLException {
        if (nhanVien == null || nhanVienList.stream().anyMatch(nv -> nv.getMaNhanVien().equals(nhanVien.getMaNhanVien()))) {
            throw new IllegalArgumentException("Invalid employee or employee already exists!");
        }
        nhanVienDao.themNhanVien(nhanVien);
        nhanVienList.add(nhanVien);
    }

    public void addPhieuDatPhong(PhieuDatPhong phieu) throws SQLException {
        if (phieu == null || phieuDatPhongList.stream().anyMatch(p -> p.getMaDatPhong().equals(phieu.getMaDatPhong()))) {
            throw new IllegalArgumentException("Invalid booking ticket or booking ticket already exists!");
        }
        if (!khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(phieu.getMaKhachHang()))) {
            throw new SQLException("Customer ID does not exist!");
        }
        if (phieu.getMaHoaDon() != null && !phieu.getMaHoaDon().isEmpty() &&
                !hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(phieu.getMaHoaDon()))) {
            throw new SQLException("Invoice ID does not exist!");
        }
        phieuDatPhongDao.themPhieuDatPhong(phieu);
        phieuDatPhongList.add(phieu);
    }

    public void addChitietPhieuDatPhong(ChitietPhieuDatPhong chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Invalid booking ticket detail!");
        }
        if (!phieuDatPhongList.stream().anyMatch(p -> p.getMaDatPhong().equals(chitiet.getMaDatPhong()))) {
            throw new SQLException("Booking ticket ID does not exist!");
        }
        if (!phongList.stream().anyMatch(p -> p.getMaPhong().equals(chitiet.getMaPhong()))) {
            throw new SQLException("Room ID does not exist!");
        }
        if (!chitiet.getMaPhong().matches("P\\d{3}")) {
            throw new IllegalArgumentException("Room ID format is invalid (Pxxx): " + chitiet.getMaPhong());
        }
        System.out.println("addChitietPhieuDatPhong - BookingTicketID: " + chitiet.getMaDatPhong() + ", RoomID: " + chitiet.getMaPhong());
        List<ChitietPhieuDatPhong> existingRecords = chitietPhieuDatPhongDao.timKiemChitietPhieuDatPhong(chitiet.getMaDatPhong());
        ChitietPhieuDatPhong existingChitiet = existingRecords.stream()
                .filter(ct -> ct.getMaPhong().equals(chitiet.getMaPhong()))
                .findFirst()
                .orElse(null);
        if (existingChitiet != null) {
            existingChitiet.setMoTa(chitiet.getMoTa());
            existingChitiet.setTrangThai(chitiet.getTrangThai());
            existingChitiet.setTienPhong(chitiet.getTienPhong());
            existingChitiet.setTienDichVu(chitiet.getTienDichVu());
            existingChitiet.setThanhTien(chitiet.getThanhTien());
            existingChitiet.setSoLuong(chitiet.getSoLuong());
            existingChitiet.setDaThanhToan(chitiet.isDaThanhToan());
            chitietPhieuDatPhongDao.suaChitietPhieuDatPhong(existingChitiet);
            int index = chitietPhieuDatPhongList.indexOf(existingChitiet);
            if (index >= 0) {
                chitietPhieuDatPhongList.set(index, existingChitiet);
            } else {
                chitietPhieuDatPhongList.add(existingChitiet);
            }
            System.out.println("Updated existing ChitietPhieuDatPhong: " + chitiet.getMaDatPhong() + ", " + chitiet.getMaPhong());
        } else {
            chitietPhieuDatPhongDao.themChitietPhieuDatPhong(chitiet);
            chitietPhieuDatPhongList.add(chitiet);
            System.out.println("Inserted new ChitietPhieuDatPhong: " + chitiet.getMaDatPhong() + ", " + chitiet.getMaPhong());
        }
        phieuDatPhongList.stream()
                .filter(p -> p.getMaDatPhong().equals(chitiet.getMaDatPhong()))
                .findFirst()
                .ifPresent(p -> p.addChitietPhieuDatPhong(chitiet));
        notifyListeners(chitietPhieuDatPhongListChangeListeners);
    }

    public void addChitietHoaDon(ChitietHoaDon chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Invalid invoice detail!");
        }
        if (!hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(chitiet.getMaHoaDon()))) {
            throw new SQLException("Invoice ID does not exist!");
        }
        if (chitiet.getMaPhong() != null && !phongList.stream().anyMatch(p -> p.getMaPhong().equals(chitiet.getMaPhong()))) {
            throw new SQLException("Room ID does not exist!");
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
            throw new IllegalArgumentException("Invalid service ticket or service ticket already exists!");
        }
        if (phieu.getMaHoaDon() != null && !phieu.getMaHoaDon().isEmpty() &&
                !hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(phieu.getMaHoaDon()))) {
            throw new SQLException("Invoice ID does not exist!");
        }
        phieuDichVuDao.themPhieuDichVu(phieu);
        phieuDichVuList.add(phieu);
    }

    public void addChitietPhieuDichVu(ChitietPhieuDichVu chitiet) throws SQLException {
        if (chitiet == null || chitietPhieuDichVuList.stream()
                .anyMatch(ct -> ct.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()) && ct.getMaDichVu().equals(chitiet.getMaDichVu()))) {
            throw new IllegalArgumentException("Invalid service ticket detail or detail already exists!");
        }
        if (!phieuDichVuList.stream().anyMatch(p -> p.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()))) {
            throw new SQLException("Service ticket ID does not exist!");
        }
        if (!dichVuList.stream().anyMatch(dv -> dv.getMaDichVu().equals(chitiet.getMaDichVu()))) {
            throw new SQLException("Service ID does not exist!");
        }
        chitietPhieuDichVuDao.themChitietPhieuDichVu(chitiet);
        chitietPhieuDichVuList.add(chitiet);
        phieuDichVuList.stream()
                .filter(p -> p.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()))
                .findFirst()
                .ifPresent(p -> p.addChitietPhieuDichVu(chitiet));
    }

    public void refreshChitietPhieuDatPhongList() {
        try {
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
            System.out.println("Refreshed chitietPhieuDatPhongList: " + chitietPhieuDatPhongList.size() + " records.");
        } catch (SQLException e) {
            System.err.println("Error refreshing chitietPhieuDatPhongList: " + e.getMessage());
        }
    }

    public void updatePhong(Phong phong) throws SQLException {
    if (phong == null) {
        throw new IllegalArgumentException("Phòng không hợp lệ!");
    }
    LocalDate currentDate = LocalDate.now();
    boolean isBooked = chitietPhieuDatPhongList.stream()
            .filter(ct -> ct.getMaPhong().equals(phong.getMaPhong()))
            .anyMatch(booking -> {
                PhieuDatPhong phieu = phieuDatPhongList.stream()
                        .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                        .findFirst()
                        .orElse(null);
                if (phieu != null && !"Đã hủy".equalsIgnoreCase(phieu.getTrangThai())) {
                    LocalDate phieuNgayDen = phieu.getNgayDen();
                    LocalDate phieuNgayDi = phieu.getNgayDi();
                    return phieuNgayDen != null && phieuNgayDi != null &&
                            (currentDate.isEqual(phieuNgayDen) || currentDate.isEqual(phieuNgayDi) ||
                                    (currentDate.isAfter(phieuNgayDen) && currentDate.isBefore(phieuNgayDi)));
                }
                return false;
            });
    if (isBooked && !phong.getTrangThai().equals("Đã đặt") && !phong.getTrangThai().equals("Bảo trì")) {
        throw new SQLException("Phòng đang được đặt vào ngày " + currentDate + ", trạng thái chỉ có thể là 'Đã đặt' hoặc 'Bảo trì'!");
    }
    if (!isBooked && phong.getTrangThai().equals("Đã đặt")) {
        throw new SQLException("Phòng không có đặt phòng vào ngày " + currentDate + ", không thể đặt trạng thái thành 'Đã đặt'!");
    }
    if (phong.getTrangThai().equals("Bảo trì") && isBooked) {
        throw new SQLException("Phòng đang được đặt vào ngày " + currentDate + ", không thể đặt thành 'Bảo trì'!");
    }
    System.out.println("Cập nhật phòng trong cơ sở dữ liệu: " + phong.getMaPhong() + ", trạng thái: " + phong.getTrangThai());
    phongDao.updatePhong(phong);
    Phong existingPhong = phongList.stream()
            .filter(p -> p.getMaPhong().equals(phong.getMaPhong()))
            .findFirst()
            .orElse(null);
    if (existingPhong != null) {
        int index = phongList.indexOf(existingPhong);
        phongList.set(index, phong);
    } else {
        phongList.add(phong);
    }
    notifyListeners(phongListChangeListeners);
    System.out.println("Đã cập nhật phòng trong danh sách: " + phong.getMaPhong() + ", trạng thái: " + phong.getTrangThai());
}

    public void updateKhachHang(KhachHang khachHang) throws SQLException {
        if (khachHang == null) {
            throw new IllegalArgumentException("Invalid customer!");
        }
        KhachHang existingKhachHang = khachHangList.stream()
                .filter(kh -> kh.getMaKhachHang().equals(khachHang.getMaKhachHang()))
                .findFirst().orElseThrow(() -> new SQLException("Customer does not exist in the list!"));
        khachHangDao.suaKhachHang(khachHang);
        int index = khachHangList.indexOf(existingKhachHang);
        if (index >= 0) {
            khachHangList.set(index, khachHang);
        }
    }

    public void updateDichVu(DichVu dichVu) throws SQLException {
        if (dichVu == null) {
            throw new IllegalArgumentException("Invalid service!");
        }
        DichVu existingDichVu = dichVuList.stream()
                .filter(dv -> dv.getMaDichVu().equals(dichVu.getMaDichVu()))
                .findFirst().orElseThrow(() -> new SQLException("Service does not exist in the list!"));
        dichVuDao.suaDichVu(dichVu);
        int index = dichVuList.indexOf(existingDichVu);
        if (index >= 0) {
            dichVuList.set(index, dichVu);
        }
    }

    public void updateKhuyenMai(KhuyenMai khuyenMai) throws SQLException {
        if (khuyenMai == null) {
            throw new IllegalArgumentException("Invalid promotion!");
        }
        KhuyenMai existingKhuyenMai = khuyenMaiList.stream()
                .filter(km -> km.getMaChuongTrinhKhuyenMai().equals(khuyenMai.getMaChuongTrinhKhuyenMai()))
                .findFirst()
                .orElseThrow(() -> new SQLException("Promotion does not exist in the list!"));
        khuyenMaiDao.suaKhuyenMai(khuyenMai);
        int index = khuyenMaiList.indexOf(existingKhuyenMai);
        if (index >= 0) {
            khuyenMaiList.set(index, khuyenMai);
        }
    }

    public void updateNhanVien(NhanVien nhanVien) throws SQLException {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Invalid employee!");
        }
        NhanVien existingNhanVien = nhanVienList.stream()
                .filter(nv -> nv.getMaNhanVien().equals(nhanVien.getMaNhanVien()))
                .findFirst().orElseThrow(() -> new SQLException("Employee does not exist in the list!"));
        nhanVienDao.suaNhanVien(nhanVien);
        int index = nhanVienList.indexOf(existingNhanVien);
        if (index >= 0) {
            nhanVienList.set(index, nhanVien);
        }
    }

    public void updatePhieuDatPhong(PhieuDatPhong phieu) throws SQLException {
        if (phieu == null) {
            throw new IllegalArgumentException("Invalid booking ticket!");
        }
        if (!khachHangList.stream().anyMatch(kh -> kh.getMaKhachHang().equals(phieu.getMaKhachHang()))) {
            throw new SQLException("Customer ID does not exist!");
        }
        if (phieu.getMaHoaDon() != null && !phieu.getMaHoaDon().isEmpty() &&
                !hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(phieu.getMaHoaDon()))) {
            throw new SQLException("Invoice ID does not exist!");
        }
        PhieuDatPhong existingPhieu = phieuDatPhongList.stream()
                .filter(p -> p.getMaDatPhong().equals(phieu.getMaDatPhong()))
                .findFirst().orElseThrow(() -> new SQLException("Booking ticket does not exist in the list!"));
        phieuDatPhongDao.suaPhieuDatPhong(phieu);
        int index = phieuDatPhongList.indexOf(existingPhieu);
        if (index >= 0) {
            phieuDatPhongList.set(index, phieu);
        }
    }

    public void updateChitietPhieuDatPhong(ChitietPhieuDatPhong chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Invalid booking ticket detail!");
        }
        if (!phieuDatPhongList.stream().anyMatch(p -> p.getMaDatPhong().equals(chitiet.getMaDatPhong()))) {
            throw new SQLException("Booking ticket ID does not exist!");
        }
        if (!phongList.stream().anyMatch(p -> p.getMaPhong().equals(chitiet.getMaPhong()))) {
            throw new SQLException("Room ID does not exist!");
        }
        ChitietPhieuDatPhong existingChitiet = chitietPhieuDatPhongList.stream()
                .filter(ct -> ct.getMaDatPhong().equals(chitiet.getMaDatPhong()) && ct.getMaPhong().equals(chitiet.getMaPhong()))
                .findFirst().orElseThrow(() -> new SQLException("Booking ticket detail does not exist in the list!"));
        chitietPhieuDatPhongDao.suaChitietPhieuDatPhong(chitiet);
        int index = chitietPhieuDatPhongList.indexOf(existingChitiet);
        if (index >= 0) {
            chitietPhieuDatPhongList.set(index, chitiet);
        }
    }

    public void updateChitietHoaDon(ChitietHoaDon chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Invalid invoice detail!");
        }
        if (!hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(chitiet.getMaHoaDon()))) {
            throw new SQLException("Invoice ID does not exist!");
        }
        if (chitiet.getMaPhong() != null && !phongList.stream().anyMatch(p -> p.getMaPhong().equals(chitiet.getMaPhong()))) {
            throw new SQLException("Room ID does not exist!");
        }
        ChitietHoaDon existingChitiet = chitietHoaDonList.stream()
                .filter(ct -> ct.getMaHoaDon().equals(chitiet.getMaHoaDon()) &&
                        (chitiet.getMaPhong() == null ? ct.getMaPhong() == null : chitiet.getMaPhong().equals(ct.getMaPhong())) &&
                        (chitiet.getMaPhieuDichVu() == null ? ct.getMaPhieuDichVu() == null : chitiet.getMaPhieuDichVu().equals(ct.getMaPhieuDichVu())))
                .findFirst().orElseThrow(() -> new SQLException("Invoice detail does not exist in the list!"));
        chitietHoaDonDao.suaChitietHoaDon(chitiet);
        int index = chitietHoaDonList.indexOf(existingChitiet);
        if (index >= 0) {
            chitietHoaDonList.set(index, chitiet);
        }
    }

    public void updatePhieuDichVu(PhieuDichVu phieu) throws SQLException {
        if (phieu == null) {
            throw new IllegalArgumentException("Invalid service ticket!");
        }
        if (phieu.getMaHoaDon() != null && !phieu.getMaHoaDon().isEmpty() &&
                !hoaDonList.stream().anyMatch(hd -> hd.getMaHoaDon().equals(phieu.getMaHoaDon()))) {
            throw new SQLException("Invoice ID does not exist!");
        }
        PhieuDichVu existingPhieu = phieuDichVuList.stream()
                .filter(p -> p.getMaPhieuDichVu().equals(phieu.getMaPhieuDichVu()))
                .findFirst().orElseThrow(() -> new SQLException("Service ticket does not exist in the list!"));
        phieuDichVuDao.suaPhieuDichVu(phieu);
        int index = phieuDichVuList.indexOf(existingPhieu);
        if (index >= 0) {
            phieuDichVuList.set(index, phieu);
        }
    }

    public void updateChitietPhieuDichVu(ChitietPhieuDichVu chitiet) throws SQLException {
        if (chitiet == null) {
            throw new IllegalArgumentException("Invalid service ticket detail!");
        }
        if (!phieuDichVuList.stream().anyMatch(p -> p.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()))) {
            throw new SQLException("Service ticket ID does not exist!");
        }
        if (!dichVuList.stream().anyMatch(dv -> dv.getMaDichVu().equals(chitiet.getMaDichVu()))) {
            throw new SQLException("Service ID does not exist!");
        }
        ChitietPhieuDichVu existingChitiet = chitietPhieuDichVuList.stream()
                .filter(ct -> ct.getMaPhieuDichVu().equals(chitiet.getMaPhieuDichVu()) && ct.getMaDichVu().equals(chitiet.getMaDichVu()))
                .findFirst().orElseThrow(() -> new SQLException("Service ticket detail does not exist in the list!"));
        chitietPhieuDichVuDao.suaChitietPhieuDichVu(chitiet);
        int index = chitietPhieuDichVuList.indexOf(existingChitiet);
        if (index >= 0) {
            chitietPhieuDichVuList.set(index, chitiet);
        }
    }

    public void deletePhong(String maPhong) throws SQLException {
        Phong phong = phongList.stream()
                .filter(p -> p.getMaPhong().equals(maPhong))
                .findFirst().orElseThrow(() -> new SQLException("Room does not exist!"));
        phongDao.xoaPhong(maPhong);
        phongList.remove(phong);
    }

    public void deleteHoaDon(String maHoaDon) throws SQLException {
        HoaDon hoaDon = hoaDonList.stream()
                .filter(hd -> hd.getMaHoaDon().equals(maHoaDon))
                .findFirst().orElseThrow(() -> new SQLException("Invoice does not exist!"));
        if (chitietHoaDonList.stream().anyMatch(ct -> ct.getMaHoaDon().equals(maHoaDon)) ||
                phieuDatPhongList.stream().anyMatch(p -> maHoaDon.equals(p.getMaHoaDon())) ||
                phieuDichVuList.stream().anyMatch(p -> maHoaDon.equals(p.getMaHoaDon()))) {
            throw new SQLException("Cannot delete invoice because it is used in invoice details, booking tickets, or service tickets!");
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
                .findFirst().orElseThrow(() -> new SQLException("Customer does not exist!"));
        if (hoaDonList.stream().anyMatch(hd -> hd.getMaKhachHang().equals(maKhachHang)) ||
                phieuDatPhongList.stream().anyMatch(p -> p.getMaKhachHang().equals(maKhachHang))) {
            throw new SQLException("Cannot delete customer because they have related invoices or booking tickets!");
        }
        khachHangDao.xoaKhachHang(maKhachHang);
        khachHangList.remove(khachHang);
    }

    public void deleteDichVu(String maDichVu) throws SQLException {
        DichVu dichVu = dichVuList.stream()
                .filter(dv -> dv.getMaDichVu().equals(maDichVu))
                .findFirst().orElseThrow(() -> new SQLException("Service does not exist!"));
        if (chitietPhieuDichVuList.stream().anyMatch(ct -> ct.getMaDichVu().equals(maDichVu))) {
            throw new SQLException("Cannot delete service because it is used in service ticket details!");
        }
        dichVuDao.xoaDichVu(maDichVu);
        dichVuList.remove(dichVu);
    }

    public void deleteKhuyenMai(String maKhuyenMai) throws SQLException {
        KhuyenMai khuyenMai = khuyenMaiList.stream()
                .filter(km -> km.getMaChuongTrinhKhuyenMai().equals(maKhuyenMai))
                .findFirst().orElseThrow(() -> new SQLException("Promotion does not exist!"));
        if (hoaDonList.stream().anyMatch(hd -> maKhuyenMai.equals(hd.getMaKhuyenMai()))) {
            throw new SQLException("Cannot delete promotion because it is used in invoices!");
        }
        khuyenMaiDao.xoaKhuyenMai(maKhuyenMai);
        khuyenMaiList.remove(khuyenMai);
    }

    public void deleteNhanVien(String maNhanVien) throws SQLException {
        NhanVien nhanVien = nhanVienList.stream()
                .filter(nv -> nv.getMaNhanVien().equals(maNhanVien))
                .findFirst().orElseThrow(() -> new SQLException("Employee does not exist!"));
        nhanVienDao.xoaNhanVien(maNhanVien);
        nhanVienList.remove(nhanVien);
    }

    public void deleteChitietPhieuDatPhong(String maDatPhong, String maPhong) throws SQLException {
        ChitietPhieuDatPhong chitiet = chitietPhieuDatPhongList.stream()
                .filter(ct -> ct.getMaDatPhong().equals(maDatPhong) && ct.getMaPhong().equals(maPhong))
                .findFirst().orElseThrow(() -> new SQLException("Booking ticket detail does not exist!"));
        chitietPhieuDatPhongDao.xoaChitietPhieuDatPhong(maDatPhong, maPhong);
        chitietPhieuDatPhongList.remove(chitiet);
        phieuDatPhongList.stream()
                .filter(p -> p.getMaDatPhong().equals(maDatPhong))
                .findFirst()
                .ifPresent(p -> p.getChitietPhieuDatPhongs().remove(chitiet));
    }

    public void deleteChitietHoaDon(String maHoaDon, String maPhong) throws SQLException {
        ChitietHoaDon chitiet = chitietHoaDonList.stream()
                .filter(ct -> ct.getMaHoaDon().equals(maHoaDon) && 
                        (maPhong == null ? ct.getMaPhong() == null : maPhong.equals(ct.getMaPhong())))
                .findFirst().orElseThrow(() -> new SQLException("Invoice detail does not exist!"));
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
                .findFirst().orElseThrow(() -> new SQLException("Service ticket does not exist!"));
        if (chitietPhieuDichVuList.stream().anyMatch(ct -> ct.getMaPhieuDichVu().equals(maPhieuDichVu))) {
            throw new SQLException("Cannot delete service ticket because it has related service ticket details!");
        }
        phieuDichVuDao.xoaPhieuDichVu(maPhieuDichVu);
        phieuDichVuList.remove(phieu);
    }

    public void deleteChitietPhieuDichVu(String maPhieuDichVu, String maDichVu) throws SQLException {
        ChitietPhieuDichVu chitiet = chitietPhieuDichVuList.stream()
                .filter(ct -> ct.getMaPhieuDichVu().equals(maPhieuDichVu) && ct.getMaDichVu().equals(maDichVu))
                .findFirst().orElseThrow(() -> new SQLException("Service ticket detail does not exist!"));
        chitietPhieuDichVuDao.xoaChitietPhieuDichVu(maPhieuDichVu, maDichVu);
        chitietPhieuDichVuList.remove(chitiet);
        phieuDichVuList.stream()
                .filter(p -> p.getMaPhieuDichVu().equals(maPhieuDichVu))
                .findFirst()
                .ifPresent(p -> p.getChitietPhieuDichVus().remove(chitiet));
    }

    public void deletePhieuDatPhong(String maDatPhong) throws SQLException {
        List<ChitietPhieuDatPhong> chitietList = chitietPhieuDatPhongList.stream()
                .filter(ct -> ct.getMaDatPhong().equals(maDatPhong))
                .collect(Collectors.toList());
        for (ChitietPhieuDatPhong ct : chitietList) {
            chitietPhieuDatPhongDao.xoaChitietPhieuDatPhong(ct.getMaDatPhong(), ct.getMaPhong());
            chitietPhieuDatPhongList.remove(ct);
        }
        PhieuDatPhong phieu = phieuDatPhongList.stream()
                .filter(p -> p.getMaDatPhong().equals(maDatPhong))
                .findFirst().orElse(null);
        if (phieu != null) {
            phieuDatPhongDao.xoaPhieuDatPhong(maDatPhong);
            phieuDatPhongList.remove(phieu);
        }
        notifyListeners(phieuDatPhongListChangeListeners);
        notifyListeners(chitietPhieuDatPhongListChangeListeners);
    }

    private void notifyListeners(List<Runnable> listeners) {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    public void updatePhongStatusByDate(LocalDate date) throws SQLException {
    for (Phong phong : phongList) {
        Phong dbPhong = phongDao.getPhongByMa(phong.getMaPhong());
        if (dbPhong == null) {
            System.out.println("Phòng không tìm thấy trong cơ sở dữ liệu: " + phong.getMaPhong());
            continue;
        }
        if ("Bảo trì".equalsIgnoreCase(dbPhong.getTrangThai())) {
            System.out.println("Bỏ qua cập nhật trạng thái cho phòng " + phong.getMaPhong() + " vì đang ở trạng thái Bảo trì");
            continue;
        }
        boolean isBooked = false;
        List<ChitietPhieuDatPhong> bookings = chitietPhieuDatPhongList.stream()
                .filter(ct -> ct.getMaPhong().equals(phong.getMaPhong()))
                .collect(Collectors.toList());
        for (ChitietPhieuDatPhong booking : bookings) {
            PhieuDatPhong phieu = phieuDatPhongList.stream()
                    .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                    .findFirst()
                    .orElse(null);
            if (phieu != null && !"Đã hủy".equalsIgnoreCase(phieu.getTrangThai())) { // Bao gồm cả "Xác nhận"
                LocalDate phieuNgayDen = phieu.getNgayDen();
                LocalDate phieuNgayDi = phieu.getNgayDi();
                if (phieuNgayDen != null && phieuNgayDi != null &&
                        (date.isEqual(phieuNgayDen) || date.isEqual(phieuNgayDi) ||
                                (date.isAfter(phieuNgayDen) && date.isBefore(phieuNgayDi)))) {
                    isBooked = true;
                    break;
                }
            }
        }
        String newStatus = isBooked ? "Đã đặt" : "Trống";
        if (!newStatus.equals(dbPhong.getTrangThai())) {
            phong.setTrangThai(newStatus);
            System.out.println("Chuẩn bị cập nhật trạng thái phòng: " + phong.getMaPhong() + " thành " + newStatus + " cho ngày " + date);
            phongDao.updatePhong(phong);
            System.out.println("Đã cập nhật trạng thái phòng: " + phong.getMaPhong() + " thành " + newStatus + " cho ngày " + date);
        } else {
            System.out.println("Không cần cập nhật trạng thái phòng: " + phong.getMaPhong() + ", trạng thái hiện tại: " + dbPhong.getTrangThai());
        }
    }
    notifyListeners(phongListChangeListeners);
}

    public boolean kiemTraPhongDat(String maPhong, LocalDate ngayDen, LocalDate ngayDi) throws SQLException {
        if (maPhong == null || ngayDen == null || ngayDi == null) {
            throw new IllegalArgumentException("Invalid room or date information!");
        }
        for (ChitietPhieuDatPhong booking : chitietPhieuDatPhongList) {
            if (booking.getMaPhong().equals(maPhong)) {
                PhieuDatPhong phieu = phieuDatPhongList.stream()
                        .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                        .findFirst()
                        .orElse(null);
                if (phieu != null && !"Canceled".equalsIgnoreCase(phieu.getTrangThai())) {
                    LocalDate phieuNgayDen = phieu.getNgayDen();
                    LocalDate phieuNgayDi = phieu.getNgayDi();
                    if (phieuNgayDen != null && phieuNgayDi != null) {
                        if (!(ngayDi.isBefore(phieuNgayDen) || ngayDen.isAfter(phieuNgayDi))) {
                            return true; // Room is booked during the specified period
                        }
                    }
                }
            }
        }
        return false; // Room is not booked
    }

    public void deletePhieuDatPhongFull(String maDatPhong) throws SQLException {
        if (maDatPhong == null) {
            throw new IllegalArgumentException("Invalid booking ticket ID!");
        }
        PhieuDatPhong phieu = phieuDatPhongList.stream()
                .filter(p -> p.getMaDatPhong().equals(maDatPhong))
                .findFirst()
                .orElseThrow(() -> new SQLException("Booking ticket does not exist!"));
        List<ChitietPhieuDatPhong> chitietPhieuDatPhongs = chitietPhieuDatPhongList.stream()
                .filter(ct -> ct.getMaDatPhong().equals(maDatPhong))
                .collect(Collectors.toList());
        for (ChitietPhieuDatPhong ct : chitietPhieuDatPhongs) {
            chitietPhieuDatPhongDao.xoaChitietPhieuDatPhong(ct.getMaDatPhong(), ct.getMaPhong());
            chitietPhieuDatPhongList.remove(ct);
            System.out.println("Deleted booking ticket detail: " + ct.getMaDatPhong() + ", " + ct.getMaPhong());
        }
        String maHoaDon = phieu.getMaHoaDon();
        phieuDatPhongDao.xoaPhieuDatPhong(maDatPhong);
        phieuDatPhongList.remove(phieu);
        System.out.println("Deleted booking ticket: " + maDatPhong);
        if (maHoaDon != null && !maHoaDon.isEmpty()) {
            List<ChitietHoaDon> chitietHoaDons = chitietHoaDonList.stream()
                    .filter(ct -> ct.getMaHoaDon().equals(maHoaDon))
                    .collect(Collectors.toList());
            for (ChitietHoaDon cthd : chitietHoaDons) {
                if (cthd.getMaPhieuDichVu() != null) {
                    List<ChitietPhieuDichVu> chitietPhieuDichVus = chitietPhieuDichVuList.stream()
                            .filter(ct -> ct.getMaPhieuDichVu().equals(cthd.getMaPhieuDichVu()))
                            .collect(Collectors.toList());
                    for (ChitietPhieuDichVu ctpdv : chitietPhieuDichVus) {
                        chitietPhieuDichVuDao.xoaChitietPhieuDichVu(ctpdv.getMaPhieuDichVu(), ctpdv.getMaDichVu());
                        chitietPhieuDichVuList.remove(ctpdv);
                        System.out.println("Deleted service ticket detail: " + ctpdv.getMaPhieuDichVu() + ", " + ctpdv.getMaDichVu());
                    }
                    PhieuDichVu pdv = phieuDichVuList.stream()
                            .filter(p -> p.getMaPhieuDichVu().equals(cthd.getMaPhieuDichVu()))
                            .findFirst()
                            .orElse(null);
                    if (pdv != null) {
                        phieuDichVuDao.xoaPhieuDichVu(pdv.getMaPhieuDichVu());
                        phieuDichVuList.remove(pdv);
                        System.out.println("Deleted service ticket: " + pdv.getMaPhieuDichVu());
                    }
                }
                chitietHoaDonDao.xoaChitietHoaDon(cthd.getMaHoaDon(), cthd.getMaPhong());
                chitietHoaDonList.remove(cthd);
                System.out.println("Deleted invoice detail: " + cthd.getMaHoaDon() + ", " + cthd.getMaPhong());
            }
            HoaDon hoaDon = hoaDonList.stream()
                    .filter(hd -> hd.getMaHoaDon().equals(maHoaDon))
                    .findFirst()
                    .orElse(null);
            if (hoaDon != null) {
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
                System.out.println("Deleted invoice: " + maHoaDon);
            }
        }
        notifyListeners(phieuDatPhongListChangeListeners);
        notifyListeners(chitietPhieuDatPhongListChangeListeners);
        notifyListeners(hoaDonListChangeListeners);
        notifyListeners(chitietHoaDonListChangeListeners);
        notifyListeners(phieuDichVuListChangeListeners);
        notifyListeners(chitietPhieuDichVuListChangeListeners);
    }

    public void deleteChitietPhieuDatPhongByMaDatPhong(String maDatPhong) throws SQLException {
        String sql = "DELETE FROM ChitietPhieuDatPhong WHERE maDatPhong = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDatPhong);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Number of records deleted: " + rowsAffected + " for BookingTicketID: " + maDatPhong);
            chitietPhieuDatPhongList.removeIf(ct -> ct.getMaDatPhong().equals(maDatPhong));
            notifyListeners(chitietPhieuDatPhongListChangeListeners);
        }
    }
}