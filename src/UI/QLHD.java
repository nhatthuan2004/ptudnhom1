package UI;

import dao.ChitietHoaDon_Dao;
import dao.ChitietPhieuDatPhong_Dao;
import dao.ChitietPhieuDichVu_Dao;
import dao.DichVu_Dao;
import dao.KhachHang_Dao;
import dao.PhieuDatPhong_Dao;
import dao.Phong_Dao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.ChitietHoaDon;
import model.ChitietPhieuDatPhong;
import model.ChitietPhieuDichVu;
import model.DichVu;
import model.HoaDon;
import model.KhachHang;
import model.PhieuDatPhong;
import model.Phong;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QLHD {
    private final ObservableList<HoaDon> danhSachHoaDon;
    private final ObservableList<ChitietHoaDon> danhSachChitietHoaDon;
    private final ObservableList<KhachHang> danhSachKhachHang;
    private TableView<HoaDon> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final DataManager dataManager;
    private final ChitietHoaDon_Dao chitietHoaDonDao;
    private final KhachHang_Dao khachHangDao;
    private final PhieuDatPhong_Dao phieuDatPhongDao;

    public QLHD() {
        dataManager = DataManager.getInstance();
        this.danhSachHoaDon = dataManager.getHoaDonList().filtered(hd -> hd.getTrangThai());
        this.danhSachChitietHoaDon = dataManager.getChitietHoaDonList();
        this.danhSachKhachHang = dataManager.getKhachHangList();
        this.chitietHoaDonDao = new ChitietHoaDon_Dao();
        this.khachHangDao = new KhachHang_Dao();
        this.phieuDatPhongDao = new PhieuDatPhong_Dao();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: #f0f0f0;");

        // User info display
        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("Chưa đăng nhập"));
            userInfoBox.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        }
        userInfoBox.setPrefSize(200, 50);
        userInfoBox.setMaxSize(200, 50);

        // Header
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f0f0f0;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(spacer, userInfoBox);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm theo mã hóa đơn, mã KH, tên KH...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, new Label("Tìm kiếm:"), searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                table.setItems(danhSachHoaDon);
            } else {
                ObservableList<HoaDon> filteredList = danhSachHoaDon.filtered(hd -> {
                    try {
                        KhachHang kh = khachHangDao.getKhachHangById(hd.getMaKhachHang());
                        String tenKhachHang = kh != null ? kh.getTenKhachHang().toLowerCase() : "";
                        return hd.getMaHoaDon().toLowerCase().contains(keyword)
                                || hd.getMaKhachHang().toLowerCase().contains(keyword)
                                || tenKhachHang.contains(keyword);
                    } catch (SQLException ex) {
                        return false;
                    }
                });
                table.setItems(filteredList);
            }
        });

        searchField.setOnAction(e -> searchButton.fire());

        // Invoice table
        table = new TableView<>(danhSachHoaDon);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<HoaDon, String> maHoaDonCol = new TableColumn<>("Mã Hóa Đơn");
        maHoaDonCol.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        maHoaDonCol.setMinWidth(100);

        TableColumn<HoaDon, String> tenKhachHangCol = new TableColumn<>("Khách Hàng");
        tenKhachHangCol.setCellValueFactory(cellData -> {
            try {
                KhachHang kh = khachHangDao.getKhachHangById(cellData.getValue().getMaKhachHang());
                return new SimpleStringProperty(kh != null ? kh.getTenKhachHang() : "Không xác định");
            } catch (SQLException e) {
                return new SimpleStringProperty("Lỗi truy vấn");
            }
        });
        tenKhachHangCol.setMinWidth(150);

        TableColumn<HoaDon, String> moTaCol = new TableColumn<>("Chi Tiết");
        moTaCol.setCellValueFactory(cellData -> {
            String maHoaDon = cellData.getValue().getMaHoaDon();
            StringBuilder moTa = new StringBuilder();
            try {
                List<ChitietHoaDon> cthdList = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(maHoaDon);
                for (ChitietHoaDon ct : cthdList) {
                    if (ct.getMaPhong() != null) {
                        moTa.append("Phòng ").append(ct.getMaPhong()).append(", ");
                    }
                    if (ct.getMaPhieuDichVu() != null) {
                        moTa.append("Dịch vụ, ");
                    }
                }
            } catch (SQLException e) {
                return new SimpleStringProperty("Lỗi truy vấn");
            }
            return new SimpleStringProperty(
                    moTa.length() > 0 ? moTa.substring(0, moTa.length() - 2) : "Không có chi tiết");
        });
        moTaCol.setMinWidth(200);

        TableColumn<HoaDon, Double> tienPhongCol = new TableColumn<>("Tiền Phòng");
        tienPhongCol.setCellValueFactory(cellData -> {
            String maHoaDon = cellData.getValue().getMaHoaDon();
            try {
                List<ChitietHoaDon> cthdList = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(maHoaDon);
                double totalTienPhong = 0;
                for (ChitietHoaDon cthd : cthdList) {
                    if (cthd.getMaDatPhong() != null) {
                        List<ChitietPhieuDatPhong> chiTietList = new ChitietPhieuDatPhong_Dao()
                                .timKiemChitietPhieuDatPhong(cthd.getMaDatPhong());
                        for (ChitietPhieuDatPhong ctp : chiTietList) {
                            PhieuDatPhong phieu = phieuDatPhongDao.getPhieuDatPhongByMa(ctp.getMaDatPhong());
                            long soNgay = phieu != null && phieu.getNgayDen() != null && phieu.getNgayDi() != null
                                    ? Math.max(1,
                                            java.time.temporal.ChronoUnit.DAYS.between(phieu.getNgayDen(),
                                                    phieu.getNgayDi()))
                                    : 1;
                            totalTienPhong += ctp.getTienPhong() * soNgay;
                        }
                    }
                }
                return new javafx.beans.property.SimpleObjectProperty<>(totalTienPhong);
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
            }
        });
        tienPhongCol.setCellFactory(col -> new TableCell<HoaDon, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        tienPhongCol.setMinWidth(120);

        TableColumn<HoaDon, Double> tienDichVuCol = new TableColumn<>("Tiền Dịch Vụ");
        tienDichVuCol.setCellValueFactory(cellData -> {
            String maHoaDon = cellData.getValue().getMaHoaDon();
            try {
                List<ChitietHoaDon> cthdList = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(maHoaDon);
                double totalTienDichVu = 0;
                for (ChitietHoaDon cthd : cthdList) {
                    if (cthd.getMaPhieuDichVu() != null) {
                        List<ChitietPhieuDichVu> chiTietDVList = new ChitietPhieuDichVu_Dao()
                                .timKiemChitietPhieuDichVu(cthd.getMaPhieuDichVu());
                        for (ChitietPhieuDichVu ctdv : chiTietDVList) {
                            totalTienDichVu += ctdv.getDonGia() * ctdv.getSoLuong();
                        }
                    }
                }
                return new javafx.beans.property.SimpleObjectProperty<>(totalTienDichVu);
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
            }
        });
        tienDichVuCol.setCellFactory(col -> new TableCell<HoaDon, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        tienDichVuCol.setMinWidth(120);

        TableColumn<HoaDon, String> hinhThucThanhToanCol = new TableColumn<>("Hình Thức TT");
        hinhThucThanhToanCol.setCellValueFactory(new PropertyValueFactory<>("hinhThucThanhToan"));
        hinhThucThanhToanCol.setMinWidth(120);

        TableColumn<HoaDon, Boolean> trangThaiCol = new TableColumn<>("Trạng Thái");
        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        trangThaiCol.setCellFactory(col -> new TableCell<HoaDon, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Đã thanh toán");
            }
        });
        trangThaiCol.setMinWidth(120);

        TableColumn<HoaDon, String> maKhachHangCol = new TableColumn<>("Mã KH");
        maKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        maKhachHangCol.setMinWidth(100);

        TableColumn<HoaDon, String> maNhanVienCol = new TableColumn<>("Mã NV");
        maNhanVienCol.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        maNhanVienCol.setMinWidth(100);

        TableColumn<HoaDon, String> ngayLapCol = new TableColumn<>("Ngày Lập");
        ngayLapCol.setCellValueFactory(cellData -> {
            LocalDateTime ngayLap = cellData.getValue().getNgayLap();
            return new SimpleStringProperty(
                    ngayLap != null ? ngayLap.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "");
        });
        ngayLapCol.setMinWidth(150);

        table.getColumns().addAll(maHoaDonCol, tenKhachHangCol, moTaCol, tienPhongCol, tienDichVuCol,
                hinhThucThanhToanCol, trangThaiCol, maKhachHangCol, maNhanVienCol, ngayLapCol);

        table.setRowFactory(tv -> {
            TableRow<HoaDon> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showChiTietHoaDon(row.getItem());
                }
            });
            return row;
        });

        // Layout
        VBox centerLayout = new VBox(15, searchBox, table);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.TOP_CENTER);
        centerLayout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; "
                + "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(centerLayout);
        layout.setPadding(new Insets(10));

        mainPane.getChildren().add(layout);
        return mainPane;
    }

    public StackPane getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    private void showChiTietHoaDon(HoaDon hoaDon) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; "
                + "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(700);

        // Tiêu đề
        Label title = new Label("HÓA ĐƠN THANH TOÁN");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: #d32f2f;");
        Label maHoaDonLabel = new Label("Mã Hóa Đơn: " + hoaDon.getMaHoaDon());
        maHoaDonLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        // Thông tin khách hàng
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Label ngayLapLabel = new Label("Ngày lập: " + (hoaDon.getNgayLap() != null ? hoaDon.getNgayLap().format(formatter) : ""));
        Label maKhachHangLabel = new Label("Mã khách hàng: " + hoaDon.getMaKhachHang());
        Label tenKhachHangLabel;
        try {
            KhachHang kh = khachHangDao.getKhachHangById(hoaDon.getMaKhachHang());
            tenKhachHangLabel = new Label("Tên khách hàng: " + (kh != null ? kh.getTenKhachHang() : "Không xác định"));
        } catch (SQLException e) {
            tenKhachHangLabel = new Label("Tên khách hàng: Lỗi truy vấn");
            showAlert("Lỗi", "Không thể tải thông tin khách hàng: " + e.getMessage());
        }

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(5);
        infoGrid.addRow(0, new Label(""), maKhachHangLabel);
        infoGrid.addRow(1, new Label(""), tenKhachHangLabel);
        infoGrid.addRow(2, new Label(""), ngayLapLabel);

        // Chi tiết phòng
        Label roomTitle = new Label("CHI TIẾT PHÒNG");
        roomTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 0 5 0;");

        TableView<ChitietPhieuDatPhong> roomTable = new TableView<>();
        roomTable.setSelectionModel(null); // Disable selection
        roomTable.setMouseTransparent(true); // Disable mouse interaction
        roomTable.setStyle("-fx-font-size: 12; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

        TableColumn<ChitietPhieuDatPhong, String> roomCol = new TableColumn<>("Phòng");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("maPhong"));
        roomCol.setMinWidth(80);

        TableColumn<ChitietPhieuDatPhong, String> roomTypeCol = new TableColumn<>("Loại phòng");
        roomTypeCol.setCellValueFactory(cellData -> {
            String maPhong = cellData.getValue().getMaPhong();
            try {
                Phong phong = new Phong_Dao().getAllPhong().stream()
                        .filter(p -> p.getMaPhong().equals(maPhong))
                        .findFirst().orElse(null);
                return new SimpleStringProperty(phong != null ? phong.getLoaiPhong() : "Không xác định");
            } catch (SQLException e) {
                return new SimpleStringProperty("Lỗi truy vấn");
            }
        });
        roomTypeCol.setMinWidth(100);

        TableColumn<ChitietPhieuDatPhong, Double> roomPriceCol = new TableColumn<>("Đơn giá");
        roomPriceCol.setCellValueFactory(new PropertyValueFactory<>("tienPhong"));
        roomPriceCol.setCellFactory(col -> new TableCell<ChitietPhieuDatPhong, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        roomPriceCol.setMinWidth(100);

        TableColumn<ChitietPhieuDatPhong, Integer> daysCol = new TableColumn<>("Số ngày");
        daysCol.setCellValueFactory(cellData -> {
            try {
                String maDatPhong = cellData.getValue().getMaDatPhong();
                PhieuDatPhong phieu = phieuDatPhongDao.getPhieuDatPhongByMa(maDatPhong);
                long soNgay = phieu != null && phieu.getNgayDen() != null && phieu.getNgayDi() != null
                        ? Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(phieu.getNgayDen(), phieu.getNgayDi()))
                        : 1;
                return new javafx.beans.property.SimpleObjectProperty<>((int) soNgay);
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleObjectProperty<>(1);
            }
        });
        daysCol.setMinWidth(80);

        TableColumn<ChitietPhieuDatPhong, Double> roomTotalCol = new TableColumn<>("Thành tiền");
        roomTotalCol.setCellValueFactory(cellData -> {
            double tienPhong = cellData.getValue().getTienPhong();
            try {
                String maDatPhong = cellData.getValue().getMaDatPhong();
                PhieuDatPhong phieu = phieuDatPhongDao.getPhieuDatPhongByMa(maDatPhong);
                long soNgay = phieu != null && phieu.getNgayDen() != null && phieu.getNgayDi() != null
                        ? Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(phieu.getNgayDen(), phieu.getNgayDi()))
                        : 1;
                return new javafx.beans.property.SimpleObjectProperty<>(tienPhong * soNgay);
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleObjectProperty<>(tienPhong);
            }
        });
        roomTotalCol.setCellFactory(col -> new TableCell<ChitietPhieuDatPhong, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        roomTotalCol.setMinWidth(120);

        roomTable.getColumns().addAll(roomCol, roomTypeCol, roomPriceCol, daysCol, roomTotalCol);

        ObservableList<ChitietPhieuDatPhong> roomItems = FXCollections.observableArrayList();
        ObservableList<ChitietHoaDon> cthdList = FXCollections.observableArrayList();
        try {
            cthdList.setAll(chitietHoaDonDao.getChiTietDichVuByMaHoaDon(hoaDon.getMaHoaDon()));
            for (ChitietHoaDon cthd : cthdList) {
                if (cthd.getMaDatPhong() != null) {
                    List<ChitietPhieuDatPhong> chiTietList = new ChitietPhieuDatPhong_Dao().timKiemChitietPhieuDatPhong(cthd.getMaDatPhong());
                    roomItems.addAll(chiTietList);
                }
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải chi tiết phòng: " + e.getMessage());
        }
        roomTable.setItems(roomItems);
        roomTable.setPrefHeight(30 + roomItems.size() * 30); // Adjust height to fit all rows

        // Chi tiết dịch vụ
        Label serviceTitle = new Label("CHI TIẾT DỊCH VỤ");
        serviceTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 0 5 0;");

        TableView<ChitietPhieuDichVu> serviceTable = new TableView<>();
        serviceTable.setSelectionModel(null); // Disable selection
        serviceTable.setMouseTransparent(true); // Disable mouse interaction
        serviceTable.setStyle("-fx-font-size: 12; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

        TableColumn<ChitietPhieuDichVu, String> serviceCol = new TableColumn<>("Dịch vụ");
        serviceCol.setCellValueFactory(cellData -> {
            try {
                DichVu dichVu = new DichVu_Dao().getAllDichVu().stream()
                        .filter(dv -> dv.getMaDichVu().equals(cellData.getValue().getMaDichVu()))
                        .findFirst().orElse(null);
                return new SimpleStringProperty(dichVu != null ? dichVu.getTenDichVu() : "Không xác định");
            } catch (SQLException e) {
                return new SimpleStringProperty("Lỗi truy vấn");
            }
        });
        serviceCol.setMinWidth(100);

        TableColumn<ChitietPhieuDichVu, String> serviceRoomCol = new TableColumn<>("Phòng sử dụng");
        serviceRoomCol.setCellValueFactory(new PropertyValueFactory<>("maPhong"));
        serviceRoomCol.setMinWidth(80);

        TableColumn<ChitietPhieuDichVu, Double> servicePriceCol = new TableColumn<>("Đơn giá");
        servicePriceCol.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        servicePriceCol.setCellFactory(col -> new TableCell<ChitietPhieuDichVu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        servicePriceCol.setMinWidth(100);

        TableColumn<ChitietPhieuDichVu, Integer> quantityCol = new TableColumn<>("Số lượng");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        quantityCol.setMinWidth(80);

        TableColumn<ChitietPhieuDichVu, Double> serviceTotalCol = new TableColumn<>("Thành tiền");
        serviceTotalCol.setCellValueFactory(cellData -> {
            double total = cellData.getValue().getDonGia() * cellData.getValue().getSoLuong();
            return new javafx.beans.property.SimpleObjectProperty<>(total);
        });
        serviceTotalCol.setCellFactory(col -> new TableCell<ChitietPhieuDichVu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        serviceTotalCol.setMinWidth(120);

        serviceTable.getColumns().addAll(serviceCol, serviceRoomCol, servicePriceCol, quantityCol, serviceTotalCol);

        ObservableList<ChitietPhieuDichVu> serviceItems = FXCollections.observableArrayList();
        try {
            for (ChitietHoaDon cthd : cthdList) {
                if (cthd.getMaPhieuDichVu() != null) {
                    List<ChitietPhieuDichVu> chiTietDVList = new ChitietPhieuDichVu_Dao().timKiemChitietPhieuDichVu(cthd.getMaPhieuDichVu());
                    serviceItems.addAll(chiTietDVList);
                }
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải chi tiết dịch vụ: " + e.getMessage());
        }
        serviceTable.setItems(serviceItems);
        serviceTable.setPrefHeight(30 + serviceItems.size() * 30); // Adjust height to fit all rows

        // Khuyến mãi
        double tienPhong = 0;
        double tienDichVu = 0;
        double thueVat = 0;
        double khuyenMai = 0;
        String maChuongTrinhKhuyenMai = null;

        if (!cthdList.isEmpty()) {
            ChitietHoaDon ct = cthdList.get(0);
            thueVat = ct.getThueVat();
            khuyenMai = ct.getKhuyenMai();
            maChuongTrinhKhuyenMai = ct.getMaChuongTrinhKhuyenMai();

            for (ChitietPhieuDatPhong ctp : roomItems) {
                try {
                    PhieuDatPhong phieu = phieuDatPhongDao.getPhieuDatPhongByMa(ctp.getMaDatPhong());
                    long soNgay = phieu != null && phieu.getNgayDen() != null && phieu.getNgayDi() != null
                            ? Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(phieu.getNgayDen(), phieu.getNgayDi()))
                            : 1;
                    tienPhong += ctp.getTienPhong() * soNgay;
                } catch (SQLException e) {
                    showAlert("Lỗi", "Không thể tính tổng tiền phòng: " + e.getMessage());
                }
            }

            for (ChitietPhieuDichVu ctdv : serviceItems) {
                tienDichVu += ctdv.getDonGia() * ctdv.getSoLuong();
            }
        }

        Label promotionTitle = new Label("KHUYẾN MÃI ÁP DỤNG");
        promotionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 0 5 0;");
        Label promotionLabel = new Label(maChuongTrinhKhuyenMai != null ? "Mã KM: " + maChuongTrinhKhuyenMai : "Không có khuyến mãi");
        Label discountLabel = new Label("Giảm giá: " + String.format("%.1f%%", khuyenMai));

        // Tổng tiền
        double subTotal = tienPhong + tienDichVu;
        double discountAmount = subTotal * (khuyenMai / 100);
        double vatAmount = (subTotal - discountAmount) * (thueVat / 100);
        double finalTotal = subTotal - discountAmount + vatAmount;

        Label totalTitle = new Label("TỔNG CỘNG");
        totalTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 0 5 0;");
        Label roomTotalLabel = new Label("Tổng tiền phòng: " + String.format("%,.0f VNĐ", tienPhong));
        Label serviceTotalLabel = new Label("Tổng tiền dịch vụ: " + String.format("%,.0f VNĐ", tienDichVu));
        Label discountTotalLabel = new Label("Tổng giảm giá: " + String.format("%,.0f VNĐ", discountAmount));
        Label vatLabel = new Label("Thuế VAT (" + String.format("%.1f%%", thueVat) + "): " + String.format("%,.0f VNĐ", vatAmount));
        Label finalTotalLabel = new Label("Tổng tiền cuối: " + String.format("%,.0f VNĐ", finalTotal));
        finalTotalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #d32f2f;");

        // Chân trang
        Label footerNote = new Label("Kính cảm ơn quý khách đã sử dụng dịch vụ!");
        footerNote.setStyle("-fx-font-style: italic; -fx-padding: 10 0 0 0;");

        // Nút In và Đóng
        Button btnIn = new Button("In");
        btnIn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnIn.setOnAction(e -> {
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null && printerJob.showPrintDialog(form.getScene().getWindow())) {
                boolean success = printerJob.printPage(form);
                if (success) {
                    printerJob.endJob();
                    showAlert("Thành công", "Hóa đơn đã được in thành công!");
                } else {
                    showAlert("Lỗi", "Không thể in hóa đơn!");
                }
            }
        });

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footerButtons = new HBox(15, btnIn, btnDong);
        footerButtons.setAlignment(Pos.CENTER);
        footerButtons.setPadding(new Insets(20, 0, 0, 0));

        // Thêm các thành phần
        form.getChildren().addAll(
                title, maHoaDonLabel, infoGrid,
                roomTitle, roomTable,
                serviceTitle, serviceTable,
                promotionTitle, promotionLabel, discountLabel,
                totalTitle, roomTotalLabel, serviceTotalLabel, discountTotalLabel, vatLabel, finalTotalLabel,
                footerNote, footerButtons
        );

        contentPane.getChildren().setAll(form);
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; "
                + "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(700);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}