package UI;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao.DichVu_Dao;
import dao.KhachHang_Dao;
import dao.KhuyenMai_Dao;
import dao.Phong_Dao;

public class DataManager {
    private static DataManager instance;
    private final ObservableList<Phong> phongList = FXCollections.observableArrayList();
    private final ObservableList<HoaDon> hoaDonList = FXCollections.observableArrayList();
    private final ObservableList<KhachHang> khachHangList = FXCollections.observableArrayList();
    private final ObservableList<DichVu> dichVuList = FXCollections.observableArrayList();
    private final ObservableList<KhuyenMai> khuyenMaiList = FXCollections.observableArrayList();
    private final ObservableList<NhanVien> nhanVienList = FXCollections.observableArrayList();
    private final List<Runnable> phongListChangeListeners = new ArrayList<>();
    private final List<Runnable> hoaDonListChangeListeners = new ArrayList<>();
    private final List<Runnable> khachHangListChangeListeners = new ArrayList<>();
    private final List<Runnable> dichVuListChangeListeners = new ArrayList<>();
    private final List<Runnable> khuyenMaiListChangeListeners = new ArrayList<>();
    private final List<Runnable> nhanVienListChangeListeners = new ArrayList<>();
    private final KhachHang_Dao khachHangDao = new KhachHang_Dao();
    private final DichVu_Dao dichVuDao = new DichVu_Dao();
    private final KhuyenMai_Dao khuyenMaiDao = new KhuyenMai_Dao();
    private final Phong_Dao phongDao = new Phong_Dao();

    private DataManager() {
        // Listener cho từng danh sách
        phongList.addListener((ListChangeListener<Phong>) change -> notifyListeners(phongListChangeListeners));
        hoaDonList.addListener((ListChangeListener<HoaDon>) change -> notifyListeners(hoaDonListChangeListeners));
        khachHangList.addListener((ListChangeListener<KhachHang>) change -> notifyListeners(khachHangListChangeListeners));
        dichVuList.addListener((ListChangeListener<DichVu>) change -> notifyListeners(dichVuListChangeListeners));
        khuyenMaiList.addListener((ListChangeListener<KhuyenMai>) change -> notifyListeners(khuyenMaiListChangeListeners));
        nhanVienList.addListener((ListChangeListener<NhanVien>) change -> notifyListeners(nhanVienListChangeListeners));
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            instance.initializeSampleData(); // Gọi sau khi instance được tạo
        }
        return instance;
    }

    private void initializeSampleData() {
        // Phong
        if (phongList.isEmpty()) {
            List<Phong> dsPhong = phongDao.getAllPhong();
            if (dsPhong != null) {
                phongList.addAll(dsPhong);
            }
        }

        // KhachHang
        if (khachHangList.isEmpty()) {
            List<KhachHang> dsKhachHang = khachHangDao.getAllKhachHang();
            if (dsKhachHang != null) {
                khachHangList.addAll(dsKhachHang);
            }
        }

        // NhanVien
        if (nhanVienList.isEmpty()) {
            nhanVienList.addAll(
                new NhanVien("NV001", "Nguyễn Văn A", "0901234567", true, "123 Đường ABC", "Quản lý", 15000000.0, "nva", "password123", "Đang làm"),
                new NhanVien("NV002", "Trần Thị B", "0912345678", false, "456 Đường XYZ", "Lễ tân", 8000000.0, "ttb", "pass456", "Nghỉ việc")
            );
        }

        // DichVu
        if (dichVuList.isEmpty()) {
            List<DichVu> dsDichVu = dichVuDao.getAllDichVu();
            if (dsDichVu != null) {
                dichVuList.addAll(dsDichVu);
            }
        }

        // ChuongTrinhKhuyenMai
        if (khuyenMaiList.isEmpty()) {
            List<KhuyenMai> dsKhuyenMai = khuyenMaiDao.getAllKhuyenMai();
            if (dsKhuyenMai != null) {
                khuyenMaiList.addAll(dsKhuyenMai);
            }
        }

        // HoaDon
        if (hoaDonList.isEmpty()) {
            HoaDon hd1 = new HoaDon(
                "HD001", "Nguyễn Văn A", "P101", 300000.0, 100000.0, 
                "Đã thanh toán", "P101", LocalDateTime.now().minusDays(2),
                "Hóa đơn phòng P101", "Tiền mặt", "KH001", "NV001"
            );
            HoaDon hd2 = new HoaDon(
                "HD002", "Trần Thị B", "P102", 500000.0, 200000.0, 
                "Chưa thanh toán", "P102", LocalDateTime.now().minusDays(1),
                "Hóa đơn phòng P102", "Thẻ tín dụng", "KH002", "NV002"
            );
            hoaDonList.addAll(hd1, hd2);
            khachHangList.get(0).addHoaDon(hd1, hoaDonList);
            khachHangList.get(1).addHoaDon(hd2, hoaDonList);
            nhanVienList.get(0).addHoaDon(hd1, hoaDonList);
            nhanVienList.get(1).addHoaDon(hd2, hoaDonList);
        }
    }

    // Getters
    public ObservableList<Phong> getPhongList() { return phongList; }
    public ObservableList<HoaDon> getHoaDonList() { return hoaDonList; }
    public ObservableList<KhachHang> getKhachHangList() { return khachHangList; }
    public ObservableList<DichVu> getDichVuList() { return dichVuList; }
    public ObservableList<KhuyenMai> getKhuyenMaiList() { return khuyenMaiList; }
    public ObservableList<NhanVien> getNhanVienList() { return nhanVienList; }

    // Setters
    public void setPhongList(ObservableList<Phong> newPhongList) {
        phongList.clear();
        phongList.addAll(newPhongList);
    }

    // Add Listeners
    public void addPhongListChangeListener(Runnable listener) { phongListChangeListeners.add(listener); }
    public void addHoaDonListChangeListener(Runnable listener) { hoaDonListChangeListeners.add(listener); }
    public void addKhachHangListChangeListener(Runnable listener) { khachHangListChangeListeners.add(listener); }
    public void addDichVuListChangeListener(Runnable listener) { dichVuListChangeListeners.add(listener); }
    public void addKhuyenMaiListChangeListener(Runnable listener) { khuyenMaiListChangeListeners.add(listener); }
    public void addNhanVienListChangeListener(Runnable listener) { nhanVienListChangeListeners.add(listener); }

    // Add methods
    public void addPhong(Phong phong) { if (phong != null && !phongList.contains(phong)) phongList.add(phong); }
    public void addHoaDon(HoaDon hoaDon) { if (hoaDon != null && !hoaDonList.contains(hoaDon)) hoaDonList.add(hoaDon); }
    public void addKhachHang(KhachHang khachHang) { if (khachHang != null && !khachHangList.contains(khachHang)) khachHangList.add(khachHang); }
    public void addDichVu(DichVu dichVu) { if (dichVu != null && !dichVuList.contains(dichVu)) dichVuList.add(dichVu); }
    public void addKhuyenMai(KhuyenMai khuyenMai) { if (khuyenMai != null && !khuyenMaiList.contains(khuyenMai)) khuyenMaiList.add(khuyenMai); }
    public void addNhanVien(NhanVien nhanVien) { if (nhanVien != null && !nhanVienList.contains(nhanVien)) nhanVienList.add(nhanVien); }

    private void notifyListeners(List<Runnable> listeners) {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}