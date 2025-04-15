package UI;

import dao.ChitietHoaDon_Dao;
import dao.KhachHang_Dao;
import dao.PhieuDatPhong_Dao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.ChitietHoaDon;
import model.HoaDon;
import model.KhachHang;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TimkiemHD {
    private final ObservableList<HoaDon> danhSachHoaDon;
    private TableView<HoaDon> table;
    private StackPane contentPane;
    private final DataManager dataManager;
    private final ChitietHoaDon_Dao chitietHoaDonDao;
    private final KhachHang_Dao khachHangDao;
    private final PhieuDatPhong_Dao phieuDatPhongDao;

    public TimkiemHD() {
        dataManager = DataManager.getInstance();
        this.danhSachHoaDon = dataManager.getHoaDonList();
        this.chitietHoaDonDao = new ChitietHoaDon_Dao();
        this.khachHangDao = new KhachHang_Dao();
        this.phieuDatPhongDao = new PhieuDatPhong_Dao();
        this.contentPane = createMainPane();
    }

    private StackPane createMainPane() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: white;");

        // User info box
        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("User Info Placeholder"));
            userInfoBox.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        }
        userInfoBox.setPrefSize(200, 50);
        userInfoBox.setMaxSize(200, 50);
        StackPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        // Title
        Label title = new Label("Tìm kiếm hóa đơn theo tên khách hàng");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold;");

        // Search bar
        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập tên khách hàng...");
        txtTimKiem.setPrefWidth(300);
        txtTimKiem.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        // Table
        table = new TableView<>();
        table.setStyle("-fx-border-color: #d3d3d3; -fx-background-radius: 5; -fx-border-radius: 5;");

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        TableColumn<HoaDon, String> maHoaDonCol = new TableColumn<>("Mã Hóa Đơn");
        maHoaDonCol.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        maHoaDonCol.setPrefWidth(100);

        TableColumn<HoaDon, String> tenKhachHangCol = new TableColumn<>("Khách Hàng");
        tenKhachHangCol.setCellValueFactory(cellData -> {
            try {
                String maKhachHang = cellData.getValue().getMaKhachHang();
                KhachHang khachHang = khachHangDao.getKhachHangById(maKhachHang);
                return new SimpleStringProperty(khachHang != null ? khachHang.getTenKhachHang() : "Không xác định");
            } catch (SQLException e) {
                return new SimpleStringProperty("Lỗi truy vấn");
            }
        });
        tenKhachHangCol.setPrefWidth(150);

        TableColumn<HoaDon, String> moTaCol = new TableColumn<>("Chi Tiết");
        moTaCol.setCellValueFactory(cellData -> {
            try {
                List<ChitietHoaDon> chiTiets = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(cellData.getValue().getMaHoaDon());
                StringBuilder moTa = new StringBuilder();
                for (ChitietHoaDon ct : chiTiets) {
                    if (ct.getMaPhong() != null) {
                        moTa.append("Phòng ").append(ct.getMaPhong()).append(", ");
                    }
                    if (ct.getMaPhieuDichVu() != null) {
                        moTa.append(ct.getMaPhieuDichVu()).append(", ");
                    }
                }
                return new SimpleStringProperty(moTa.length() > 0 ? moTa.substring(0, moTa.length() - 2) : "Không có chi tiết");
            } catch (SQLException e) {
                return new SimpleStringProperty("Lỗi truy vấn");
            }
        });
        moTaCol.setPrefWidth(200);

        TableColumn<HoaDon, Double> tienPhongCol = new TableColumn<>("Tiền Phòng");
        tienPhongCol.setCellValueFactory(cellData -> {
            try {
                List<ChitietHoaDon> chiTiets = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(cellData.getValue().getMaHoaDon());
                double total = chiTiets.stream().mapToDouble(ChitietHoaDon::getTienPhong).sum();
                return new javafx.beans.property.SimpleObjectProperty<>(total);
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
            }
        });
        tienPhongCol.setPrefWidth(120);

        TableColumn<HoaDon, Double> tienDichVuCol = new TableColumn<>("Tiền Dịch Vụ");
        tienDichVuCol.setCellValueFactory(cellData -> {
            try {
                List<ChitietHoaDon> chiTiets = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(cellData.getValue().getMaHoaDon());
                double total = chiTiets.stream().mapToDouble(ChitietHoaDon::getTienDichVu).sum();
                return new javafx.beans.property.SimpleObjectProperty<>(total);
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleObjectProperty<>(0.0);
            }
        });
        tienDichVuCol.setPrefWidth(120);

        TableColumn<HoaDon, String> hinhThucThanhToanCol = new TableColumn<>("Hình Thức TT");
        hinhThucThanhToanCol.setCellValueFactory(new PropertyValueFactory<>("hinhThucThanhToan"));
        hinhThucThanhToanCol.setPrefWidth(120);

        TableColumn<HoaDon, Boolean> trangThaiCol = new TableColumn<>("Trạng Thái");
        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        trangThaiCol.setCellFactory(col -> new TableCell<HoaDon, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item ? "Đã thanh toán" : "Chưa thanh toán");
            }
        });
        trangThaiCol.setPrefWidth(120);

        TableColumn<HoaDon, String> maKhachHangCol = new TableColumn<>("Mã KH");
        maKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        maKhachHangCol.setPrefWidth(100);

        TableColumn<HoaDon, String> maNhanVienCol = new TableColumn<>("Mã NV");
        maNhanVienCol.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        maNhanVienCol.setPrefWidth(100);

        TableColumn<HoaDon, LocalDateTime> ngayLapCol = new TableColumn<>("Ngày Lập");
        ngayLapCol.setCellValueFactory(new PropertyValueFactory<>("ngayLap"));
        ngayLapCol.setPrefWidth(150);

        table.getColumns().addAll(maHoaDonCol, tenKhachHangCol, moTaCol, tienPhongCol, tienDichVuCol,
                hinhThucThanhToanCol, trangThaiCol, maKhachHangCol, maNhanVienCol, ngayLapCol);
        table.setItems(danhSachHoaDon);

        // Double-click to view details
        table.setRowFactory(tv -> {
            TableRow<HoaDon> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    HoaDon hoaDon = row.getItem();
                    showChiTietHoaDon(hoaDon);
                }
            });
            return row;
        });

        // Content layout
        VBox content = new VBox(15, title, searchBox, scrollPane);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setCenter(content);
        layout.setPadding(new Insets(10));

        // Search action
        Runnable searchAction = () -> timKiemHoaDon(txtTimKiem.getText().trim());
        btnTimKiem.setOnAction(e -> searchAction.run());
        txtTimKiem.setOnAction(e -> searchAction.run());

        root.getChildren().addAll(layout, userInfoBox);
        return root;
    }

    public StackPane getUI() {
        return contentPane;
    }

    private void timKiemHoaDon(String keyword) {
        ObservableList<HoaDon> ketQua = FXCollections.observableArrayList();
        if (keyword.isEmpty()) {
            ketQua.addAll(danhSachHoaDon);
        } else {
            String keywordLower = keyword.toLowerCase();
            try {
                for (HoaDon hd : danhSachHoaDon) {
                    KhachHang khachHang = khachHangDao.getKhachHangById(hd.getMaKhachHang());
                    if (khachHang != null && khachHang.getTenKhachHang().toLowerCase().contains(keywordLower)) {
                        ketQua.add(hd);
                    }
                }
                if (ketQua.isEmpty()) {
                    showAlert("Thông báo", "Không tìm thấy hóa đơn nào phù hợp với từ khóa: " + keyword);
                }
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể tìm kiếm hóa đơn: " + e.getMessage());
            }
        }
        table.setItems(ketQua);
        table.refresh();
    }

    private void showChiTietHoaDon(HoaDon hoaDon) {
        VBox form = createCenteredForm("Chi tiết hóa đơn " + hoaDon.getMaHoaDon());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        String tenKhachHang;
        try {
            KhachHang khachHang = khachHangDao.getKhachHangById(hoaDon.getMaKhachHang());
            tenKhachHang = khachHang != null ? khachHang.getTenKhachHang() : "Không xác định";
        } catch (SQLException e) {
            tenKhachHang = "Lỗi truy vấn";
        }

        List<ChitietHoaDon> chiTiets;
        double tienPhong = 0.0;
        double tienDichVu = 0.0;
        StringBuilder phong = new StringBuilder();
        StringBuilder dichVu = new StringBuilder();
        String maDatPhong = "Không có";
        try {
            chiTiets = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(hoaDon.getMaHoaDon());
            for (ChitietHoaDon ct : chiTiets) {
                if (ct.getMaPhong() != null) {
                    phong.append(ct.getMaPhong()).append(", ");
                }
                if (ct.getMaPhieuDichVu() != null) {
                    dichVu.append(ct.getMaPhieuDichVu()).append(", ");
                }
                tienPhong += ct.getTienPhong();
                tienDichVu += ct.getTienDichVu();
                if (ct.getMaDatPhong() != null && !ct.getMaDatPhong().isEmpty()) {
                    maDatPhong = ct.getMaDatPhong();
                }
            }
        } catch (SQLException e) {
            phong.append("Lỗi truy vấn");
            dichVu.append("Lỗi truy vấn");
        }

        int row = 0;
        grid.add(new Label("Mã Hóa Đơn:"), 0, row);
        grid.add(new Label(hoaDon.getMaHoaDon()), 1, row++);

        grid.add(new Label("Tên Khách Hàng:"), 0, row);
        grid.add(new Label(tenKhachHang), 1, row++);

        grid.add(new Label("Mã Phiếu Đặt Phòng:"), 0, row);
        grid.add(new Label(maDatPhong), 1, row++);

        grid.add(new Label("Phòng:"), 0, row);
        grid.add(new Label(phong.length() > 0 ? phong.substring(0, phong.length() - 2) : "Không có"), 1, row++);

        grid.add(new Label("Dịch Vụ:"), 0, row);
        grid.add(new Label(dichVu.length() > 0 ? dichVu.substring(0, dichVu.length() - 2) : "Không có"), 1, row++);

        grid.add(new Label("Tiền Phòng:"), 0, row);
        grid.add(new Label(String.format("%,.0f VNĐ", tienPhong)), 1, row++);

        grid.add(new Label("Tiền Dịch Vụ:"), 0, row);
        grid.add(new Label(String.format("%,.0f VNĐ", tienDichVu)), 1, row++);

        grid.add(new Label("Hình Thức TT:"), 0, row);
        grid.add(new Label(hoaDon.getHinhThucThanhToan()), 1, row++);

        grid.add(new Label("Trạng Thái:"), 0, row);
        grid.add(new Label(hoaDon.getTrangThai() ? "Đã thanh toán" : "Chưa thanh toán"), 1, row++);

        grid.add(new Label("Mã Khách Hàng:"), 0, row);
        grid.add(new Label(hoaDon.getMaKhachHang()), 1, row++);

        grid.add(new Label("Mã Nhân Viên:"), 0, row);
        grid.add(new Label(hoaDon.getMaNhanVien()), 1, row++);

        grid.add(new Label("Ngày Lập:"), 0, row);
        grid.add(new Label(hoaDon.getNgayLap().toString()), 1, row++);

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(createMainPane()));

        HBox footer = new HBox(btnDong);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 0, 0));

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(600);
        form.setMaxHeight(600);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }
}