package UI;

import dao.DichVu_Dao;
import dao.KhachHang_Dao;
import dao.KhuyenMai_Dao;
import dao.Phong_Dao;
import model.Phong;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private final ObservableList<Phong> phongList = FXCollections.observableArrayList();
    private final ObservableList<HoaDon> hoaDonList = FXCollections.observableArrayList();
    private final ObservableList<KhachHang> khachHangList = FXCollections.observableArrayList();
    private final ObservableList<DichVu> dichVuList = FXCollections.observableArrayList();
    private final ObservableList<KhuyenMai> khuyenMaiList = FXCollections.observableArrayList();
    private final ObservableList<NhanVien> nhanVienList = FXCollections.observableArrayList();
    private final ObservableList<PhieuDatPhong> phieuDatPhongList = FXCollections.observableArrayList();
    private final List<Runnable> phongListChangeListeners = new ArrayList<>();
    private final List<Runnable> hoaDonListChangeListeners = new ArrayList<>();
    private final List<Runnable> khachHangListChangeListeners = new ArrayList<>();
    private final List<Runnable> dichVuListChangeListeners = new ArrayList<>();
    private final List<Runnable> khuyenMaiListChangeListeners = new ArrayList<>();
    private final List<Runnable> nhanVienListChangeListeners = new ArrayList<>();
    private final List<Runnable> phieuDatPhongListChangeListeners = new ArrayList<>();
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
        phieuDatPhongList.addListener((ListChangeListener<PhieuDatPhong>) change -> notifyListeners(phieuDatPhongListChangeListeners));
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            instance.initializeSampleData();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Phong
//        if (phongList.isEmpty()) {
//            try {
//                List<Phong> dsPhong = phongDao.getAllPhong();
//                if (dsPhong != null && !dsPhong.isEmpty()) {
//                    phongList.addAll(dsPhong);
//                    System.out.println("Đã tải dữ liệu phòng từ cơ sở dữ liệu.");
//                } else {
//                    // Dữ liệu mẫu nếu DAO trả về null hoặc danh sách rỗng
//                    phongList.addAll(
//                        new Phong("P101", "Đơn", 300000.0, "Trống", true, "Tầng 1", 2, "Phòng đơn tiêu chuẩn"),
//                        new Phong("P102", "Đôi", 500000.0, "Trống", true, "Tầng 2", 4, "Phòng đôi tiện nghi")
//                    );
//                    System.out.println("Không có dữ liệu từ phongDao, sử dụng dữ liệu mẫu cho Phong.");
//                }
//            } catch (Exception e) {
//                // Xử lý ngoại lệ liên quan đến cơ sở dữ liệu
//                System.err.println("Lỗi khi tải dữ liệu Phong từ DAO: " + e.getMessage());
//                // Dùng dữ liệu mẫu nếu có lỗi
//                phongList.addAll(
//                    new Phong("P101", "Đơn", 300000.0, "Trống", true, "Tầng 1", 2, "Phòng đơn tiêu chuẩn"),
//                    new Phong("P102", "Đôi", 500000.0, "Trống", true, "Tầng 2", 4, "Phòng đôi tiện nghi")
//                );
//                System.out.println("Đã xảy ra ngoại lệ, sử dụng dữ liệu mẫu cho Phong.");
//            }
//        }

        // KhachHang
        if (khachHangList.isEmpty()) {
            try {
                List<KhachHang> dsKhachHang = khachHangDao.getAllKhachHang();
                if (dsKhachHang != null && !dsKhachHang.isEmpty()) {
                    khachHangList.addAll(dsKhachHang);
                    System.out.println("Đã tải dữ liệu khách hàng từ cơ sở dữ liệu.");
                } else {
                    khachHangList.addAll(
                        new KhachHang("KH001", "Nguyễn Văn A", "0901234567", "nva@example.com", "123 Đường ABC", "123456789012", LocalDate.of(1990, 1, 1), "Việt Nam", "Nam"),
                        new KhachHang("KH002", "Trần Thị B", "0912345678", "ttb@example.com", "456 Đường XYZ", "987654321098", LocalDate.of(1995, 5, 5), "Việt Nam", "Nữ")
                    );
                    System.out.println("Không có dữ liệu từ khachHangDao, sử dụng dữ liệu mẫu cho KhachHang.");
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tải dữ liệu KhachHang từ DAO: " + e.getMessage());
                khachHangList.addAll(
                    new KhachHang("KH001", "Nguyễn Văn A", "0901234567", "nva@example.com", "123 Đường ABC", "123456789012", LocalDate.of(1990, 1, 1), "Việt Nam", "Nam"),
                    new KhachHang("KH002", "Trần Thị B", "0912345678", "ttb@example.com", "456 Đường XYZ", "987654321098", LocalDate.of(1995, 5, 5), "Việt Nam", "Nữ")
                );
                System.out.println("Đã xảy ra ngoại lệ, sử dụng dữ liệu mẫu cho KhachHang.");
            }
        }

        // NhanVien
        if (nhanVienList.isEmpty()) {
            nhanVienList.addAll(
                new NhanVien("NV001", "Nguyễn Văn A", "0901234567", true, "123 Đường ABC", "Quản lý", 15000000.0, "nva", "password123", "Đang làm"),
                new NhanVien("NV002", "Trần Thị B", "0912345678", false, "456 Đường XYZ", "Lễ tân", 8000000.0, "ttb", "pass456", "Nghỉ việc")
            );
            System.out.println("Đã thêm dữ liệu mẫu cho NhanVien.");
        }

        // DichVu
        if (dichVuList.isEmpty()) {
            try {
                List<DichVu> dsDichVu = dichVuDao.getAllDichVu();
                if (dsDichVu != null && !dsDichVu.isEmpty()) {
                    dichVuList.addAll(dsDichVu);
                    System.out.println("Đã tải dữ liệu dịch vụ từ cơ sở dữ liệu.");
                } else {
                    dichVuList.addAll(
                        new DichVu("DV001", "Ăn sáng", 50000.0, true),
                        new DichVu("DV002", "Giặt là", 30000.0, true)
                    );
                    System.out.println("Không có dữ liệu từ dichVuDao, sử dụng dữ liệu mẫu cho DichVu.");
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tải dữ liệu DichVu từ DAO: " + e.getMessage());
                dichVuList.addAll(
                    new DichVu("DV001", "Ăn sáng", 50000.0, true),
                    new DichVu("DV002", "Giặt là", 30000.0, true)
                );
                System.out.println("Đã xảy ra ngoại lệ, sử dụng dữ liệu mẫu cho DichVu.");
            }
        }

        // ChuongTrinhKhuyenMai
        if (khuyenMaiList.isEmpty()) {
            try {
                List<KhuyenMai> dsKhuyenMai = khuyenMaiDao.getAllKhuyenMai();
                if (dsKhuyenMai != null && !dsKhuyenMai.isEmpty()) {
                    khuyenMaiList.addAll(dsKhuyenMai);
                    System.out.println("Đã tải dữ liệu khuyến mãi từ cơ sở dữ liệu.");
                } else {
                    khuyenMaiList.addAll(
                        new KhuyenMai("KM001", "Giảm giá mùa hè", 20.0, true),
                        new KhuyenMai("KM002", "Khuyến mãi cuối năm", 30.0, false)
                    );
                    System.out.println("Không có dữ liệu từ khuyenMaiDao, sử dụng dữ liệu mẫu cho KhuyenMai.");
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tải dữ liệu KhuyenMai từ DAO: " + e.getMessage());
                khuyenMaiList.addAll(
                    new KhuyenMai("KM001", "Giảm giá mùa hè", 20.0, true),
                    new KhuyenMai("KM002", "Khuyến mãi cuối năm", 30.0, false)
                );
                System.out.println("Đã xảy ra ngoại lệ, sử dụng dữ liệu mẫu cho KhuyenMai.");
            }
        }

        // PhieuDatPhong
        if (phieuDatPhongList.isEmpty()) {
            try {
                PhieuDatPhong phieu1 = new PhieuDatPhong(
                    "DP001",
                    LocalDate.now(),
                    LocalDate.now().plusDays(2),
                    LocalDate.now(),
                    2,
                    "Chưa xác nhận",
                    "KH001"
                );
                phieu1.addChitietPhieuDatPhong(new ChitietPhieuDatPhong("P101", "DP001", 300000.0, 600000.0, 2));
                PhieuDatPhong phieu2 = new PhieuDatPhong(
                    "DP002",
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    LocalDate.now(),
                    4,
                    "Xác nhận",
                    "KH002"
                );
                phieu2.addChitietPhieuDatPhong(new ChitietPhieuDatPhong("P102", "DP002", 500000.0, 1000000.0, 2));
                phieuDatPhongList.addAll(phieu1, phieu2);
                System.out.println("Đã thêm dữ liệu mẫu cho PhieuDatPhong.");
            } catch (Exception e) {
                System.err.println("Lỗi khi khởi tạo dữ liệu mẫu PhieuDatPhong: " + e.getMessage());
            }
        }

        // HoaDon
        if (hoaDonList.isEmpty()) {
            try {
                HoaDon hd1 = new HoaDon(
                    "HD001", "Nguyễn Văn A", "P101", 600000.0, 100000.0,
                    "Đã thanh toán", "P101", LocalDateTime.now().minusDays(2),
                    "Hóa đơn phòng P101", "Tiền mặt", "KH001", "NV001"
                );
                HoaDon hd2 = new HoaDon(
                    "HD002", "Trần Thị B", "P102", 1000000.0, 200000.0,
                    "Chưa thanh toán", "P102", LocalDateTime.now().minusDays(1),
                    "Hóa đơn phòng P102", "Thẻ tín dụng", "KH002", "NV002"
                );
                hoaDonList.addAll(hd1, hd2);
                khachHangList.get(0).addHoaDon(hd1, hoaDonList);
                khachHangList.get(1).addHoaDon(hd2, hoaDonList);
                nhanVienList.get(0).addHoaDon(hd1, hoaDonList);
                nhanVienList.get(1).addHoaDon(hd2, hoaDonList);
                System.out.println("Đã thêm dữ liệu mẫu cho HoaDon.");
            } catch (Exception e) {
                System.err.println("Lỗi khi khởi tạo dữ liệu mẫu HoaDon: " + e.getMessage());
            }
        }
    }

    // Getters
    public ObservableList<Phong> getPhongList() { return phongList; }
    public ObservableList<HoaDon> getHoaDonList() { return hoaDonList; }
    public ObservableList<KhachHang> getKhachHangList() { return khachHangList; }
    public ObservableList<DichVu> getDichVuList() { return dichVuList; }
    public ObservableList<KhuyenMai> getKhuyenMaiList() { return khuyenMaiList; }
    public ObservableList<NhanVien> getNhanVienList() { return nhanVienList; }
    public ObservableList<PhieuDatPhong> getPhieuDatPhongList() { return phieuDatPhongList; }

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
    public void addPhieuDatPhongListChangeListener(Runnable listener) { phieuDatPhongListChangeListeners.add(listener); }

    // Add methods
    public void addPhong(Phong phong) { 
        if (phong != null && !phongList.contains(phong)) phongList.add(phong); 
    }
    public void addHoaDon(HoaDon hoaDon) { 
        if (hoaDon != null && !hoaDonList.contains(hoaDon)) hoaDonList.add(hoaDon); 
    }
    public void addKhachHang(KhachHang khachHang) { 
        if (khachHang != null && !khachHangList.contains(khachHang)) khachHangList.add(khachHang); 
    }
    public void addDichVu(DichVu dichVu) { 
        if (dichVu != null && !dichVuList.contains(dichVu)) dichVuList.add(dichVu); 
    }
    public void addKhuyenMai(KhuyenMai khuyenMai) { 
        if (khuyenMai != null && !khuyenMaiList.contains(khuyenMai)) khuyenMaiList.add(khuyenMai); 
    }
    public void addNhanVien(NhanVien nhanVien) { 
        if (nhanVien != null && !nhanVienList.contains(nhanVien)) nhanVienList.add(nhanVien); 
    }
    public void addPhieuDatPhong(PhieuDatPhong phieu) { 
        if (phieu != null && !phieuDatPhongList.contains(phieu)) phieuDatPhongList.add(phieu); 
    }

    private void notifyListeners(List<Runnable> listeners) {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}