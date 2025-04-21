package UI;

import dao.ChitietHoaDon_Dao;
import dao.KhachHang_Dao;
import dao.PhieuDatPhong_Dao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.ChitietHoaDon;
import model.HoaDon;
import model.KhachHang;
import model.KhuyenMai;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        this.danhSachHoaDon = dataManager.getHoaDonList().filtered(hd -> hd.getTrangThai()); // Chỉ lấy hóa đơn đã thanh toán
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
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, new Label("Tìm kiếm:"), searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                table.setItems(danhSachHoaDon); // Hiển thị tất cả hóa đơn đã thanh toán
            } else {
                ObservableList<HoaDon> filteredList = danhSachHoaDon.filtered(hd -> {
                    try {
                        KhachHang kh = khachHangDao.getKhachHangById(hd.getMaKhachHang());
                        String tenKhachHang = kh != null ? kh.getTenKhachHang().toLowerCase() : "";
                        return hd.getMaHoaDon().toLowerCase().contains(keyword) ||
                               hd.getMaKhachHang().toLowerCase().contains(keyword) ||
                               tenKhachHang.contains(keyword);
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
            for (ChitietHoaDon ct : danhSachChitietHoaDon) {
                if (maHoaDon.equals(ct.getMaHoaDon())) {
                    if (ct.getMaPhong() != null) {
                        moTa.append("Phòng ").append(ct.getMaPhong()).append(", ");
                    }
                    if (ct.getMaPhieuDichVu() != null) {
                        moTa.append("Dịch vụ, ");
                    }
                }
            }
            return new SimpleStringProperty(moTa.length() > 0 ? moTa.substring(0, moTa.length() - 2) : "Không có chi tiết");
        });
        moTaCol.setMinWidth(200);

        TableColumn<HoaDon, Double> tienPhongCol = new TableColumn<>("Tiền Phòng");
        tienPhongCol.setCellValueFactory(cellData -> {
            String maHoaDon = cellData.getValue().getMaHoaDon();
            double total = danhSachChitietHoaDon.stream()
                    .filter(ct -> maHoaDon.equals(ct.getMaHoaDon()))
                    .mapToDouble(ChitietHoaDon::getTienPhong)
                    .sum();
            return new javafx.beans.property.SimpleObjectProperty<>(total);
        });
        tienPhongCol.setMinWidth(120);

        TableColumn<HoaDon, Double> tienDichVuCol = new TableColumn<>("Tiền Dịch Vụ");
        tienDichVuCol.setCellValueFactory(cellData -> {
            String maHoaDon = cellData.getValue().getMaHoaDon();
            double total = danhSachChitietHoaDon.stream()
                    .filter(ct -> maHoaDon.equals(ct.getMaHoaDon()))
                    .mapToDouble(ChitietHoaDon::getTienDichVu)
                    .sum();
            return new javafx.beans.property.SimpleObjectProperty<>(total);
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
                setText(empty || item == null ? null : "Đã thanh toán"); // Chỉ hiển thị "Đã thanh toán"
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
            return new SimpleStringProperty(ngayLap != null ? ngayLap.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "");
        });
        ngayLapCol.setMinWidth(150);

        table.getColumns().addAll(maHoaDonCol, tenKhachHangCol, moTaCol, tienPhongCol, tienDichVuCol,
                hinhThucThanhToanCol, trangThaiCol, maKhachHangCol, maNhanVienCol, ngayLapCol); // Bỏ cột hành động

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
        centerLayout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

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
        VBox form = createCenteredForm("Chi tiết hóa đơn " + hoaDon.getMaHoaDon());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        Label maHoaDonLabel = new Label("Mã hóa đơn: " + hoaDon.getMaHoaDon());
        Label ngayLapLabel = new Label("Ngày lập: " + (hoaDon.getNgayLap() != null ? hoaDon.getNgayLap().format(formatter) : ""));
        Label hinhThucThanhToanLabel = new Label("Hình thức thanh toán: " + hoaDon.getHinhThucThanhToan());
        Label trangThaiLabel = new Label("Trạng thái: Đã thanh toán"); // Chỉ hiển thị "Đã thanh toán"
        Label maKhachHangLabel = new Label("Mã khách hàng: " + hoaDon.getMaKhachHang());
        Label maNhanVienLabel = new Label("Mã nhân viên: " + hoaDon.getMaNhanVien());

        try {
            KhachHang kh = khachHangDao.getKhachHangById(hoaDon.getMaKhachHang());
            Label tenKhachHangLabel = new Label("Tên khách hàng: " + (kh != null ? kh.getTenKhachHang() : "Không xác định"));
            content.getChildren().add(tenKhachHangLabel);
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải thông tin khách hàng: " + e.getMessage());
        }

        double tienPhong = 0;
        double tienDichVu = 0;
        double thueVat = 0;
        double khuyenMai = 0;
        int soNgay = 0;
        String maChuongTrinhKhuyenMai = null;

        for (ChitietHoaDon ct : danhSachChitietHoaDon) {
            if (hoaDon.getMaHoaDon().equals(ct.getMaHoaDon())) {
                tienPhong += ct.getTienPhong();
                tienDichVu += ct.getTienDichVu();
                if (ct.getMaPhong() != null) {
                    thueVat = ct.getThueVat();
                    khuyenMai = ct.getKhuyenMai();
                    soNgay = Math.max(soNgay, ct.getSoNgay());
                    if (ct.getMaChuongTrinhKhuyenMai() != null) {
                        maChuongTrinhKhuyenMai = ct.getMaChuongTrinhKhuyenMai();
                    }
                }
            }
        }

        Label tienPhongLabel = new Label("Tiền phòng: " + String.format("%,.0f VNĐ", tienPhong));
        Label tienDichVuLabel = new Label("Tiền dịch vụ: " + String.format("%,.0f VNĐ", tienDichVu));
        Label thueVatLabel = new Label("Thuế VAT: " + String.format("%.1f%%", thueVat));
        Label soNgayLabel = new Label("Số ngày lưu trú: " + soNgay);
        Label khuyenMaiLabel = new Label("Chiết khấu: " + String.format("%.1f%%", khuyenMai));

        if (maChuongTrinhKhuyenMai != null) {
            final String finalMaChuongTrinhKhuyenMai = maChuongTrinhKhuyenMai;
            try {
                KhuyenMai km = dataManager.getKhuyenMaiList().stream()
                        .filter(k -> k.getMaChuongTrinhKhuyenMai().equals(finalMaChuongTrinhKhuyenMai))
                        .findFirst().orElse(null);
                Label tenKhuyenMaiLabel = new Label("Chương trình KM: " + (km != null ? km.getTenChuongTrinhKhuyenMai() : finalMaChuongTrinhKhuyenMai));
                content.getChildren().add(tenKhuyenMaiLabel);
            } catch (Exception e) {
                showAlert("Lỗi", "Không thể tải thông tin khuyến mãi: " + e.getMessage());
            }
        }

        double discountAmount = tienPhong * (khuyenMai / 100);
        double subTotal = tienPhong + tienDichVu - discountAmount;
        double vatAmount = subTotal * (thueVat / 100);
        double finalTotal = subTotal + vatAmount;

        Label subTotalLabel = new Label("Tổng phụ: " + String.format("%,.0f VNĐ", tienPhong + tienDichVu));
        Label discountAmountLabel = new Label("Tiền chiết khấu: " + String.format("%,.0f VNĐ", discountAmount));
        Label vatAmountLabel = new Label("Tiền thuế VAT: " + String.format("%,.0f VNĐ", vatAmount));
        Label finalTotalLabel = new Label("Tổng tiền cuối: " + String.format("%,.0f VNĐ", finalTotal));

        content.getChildren().addAll(maHoaDonLabel, ngayLapLabel, hinhThucThanhToanLabel, trangThaiLabel,
                maKhachHangLabel, maNhanVienLabel, tienPhongLabel, tienDichVuLabel, thueVatLabel,
                soNgayLabel, khuyenMaiLabel, subTotalLabel, discountAmountLabel, vatAmountLabel, finalTotalLabel);

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(btnDong);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(content, footer);
        contentPane.getChildren().setAll(form);
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(600);
        form.setMaxHeight(600);

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