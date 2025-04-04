package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.HoaDon;
import java.time.LocalDateTime;

public class TimkiemHD {
    private final ObservableList<HoaDon> danhSachHoaDon;
    private TableView<HoaDon> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final DataManager dataManager;

    public TimkiemHD() {
        dataManager = DataManager.getInstance();
        this.danhSachHoaDon = dataManager.getHoaDonList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: white;");

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

        Label title = new Label("Tìm kiếm hóa đơn theo tên khách hàng");
        title.setFont(new Font(20));

        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập tên khách hàng...");
        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        VBox topHeader = new VBox(10, title, searchBox);
        topHeader.setPadding(new Insets(20));
        StackPane.setAlignment(topHeader, Pos.TOP_LEFT);

        table = new TableView<>(danhSachHoaDon);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HoaDon, String> colMaHoaDon = new TableColumn<>("Mã Hóa Đơn");
        colMaHoaDon.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        TableColumn<HoaDon, String> colTenKhachHang = new TableColumn<>("Tên Khách Hàng");
        colTenKhachHang.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        TableColumn<HoaDon, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        TableColumn<HoaDon, Double> colTienPhong = new TableColumn<>("Tiền Phòng");
        colTienPhong.setCellValueFactory(new PropertyValueFactory<>("tienPhong"));
        TableColumn<HoaDon, Double> colTienDichVu = new TableColumn<>("Tiền Dịch Vụ");
        colTienDichVu.setCellValueFactory(new PropertyValueFactory<>("tienDichVu"));
        TableColumn<HoaDon, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        TableColumn<HoaDon, String> colPhong = new TableColumn<>("Phòng");
        colPhong.setCellValueFactory(new PropertyValueFactory<>("phong"));
        TableColumn<HoaDon, LocalDateTime> colNgayLap = new TableColumn<>("Ngày Lập");
        colNgayLap.setCellValueFactory(new PropertyValueFactory<>("ngayLap"));
        TableColumn<HoaDon, String> colHinhThucThanhToan = new TableColumn<>("Hình Thức TT");
        colHinhThucThanhToan.setCellValueFactory(new PropertyValueFactory<>("hinhThucThanhToan"));
        TableColumn<HoaDon, String> colMaKhachHang = new TableColumn<>("Mã KH");
        colMaKhachHang.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        TableColumn<HoaDon, String> colMaNhanVien = new TableColumn<>("Mã NV");
        colMaNhanVien.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));

        table.getColumns().addAll(colMaHoaDon, colTenKhachHang, colMoTa, colTienPhong, colTienDichVu,
                                  colTrangThai, colPhong, colNgayLap, colHinhThucThanhToan, colMaKhachHang, colMaNhanVien);

        table.setRowFactory(tv -> {
            TableRow<HoaDon> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    HoaDon hoaDon = row.getItem();
                    showChiTietHoaDon(hoaDon);
                }
            });
            return row;
        });

        BorderPane layout = new BorderPane();
        layout.setTop(new HBox(topHeader, userInfoBox));
        HBox.setHgrow(topHeader, Priority.ALWAYS);
        HBox.setHgrow(userInfoBox, Priority.NEVER);
        layout.setCenter(table);
        layout.setPadding(new Insets(10));

        btnTimKiem.setOnAction(e -> timKiemHoaDon(txtTimKiem.getText().trim()));
        txtTimKiem.setOnAction(e -> timKiemHoaDon(txtTimKiem.getText().trim()));

        mainPane.getChildren().add(layout);
        return mainPane;
    }

    public StackPane getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    private void timKiemHoaDon(String keyword) {
        ObservableList<HoaDon> ketQua = FXCollections.observableArrayList();
        if (keyword.isEmpty()) {
            ketQua.addAll(danhSachHoaDon);
        } else {
            String keywordLower = keyword.toLowerCase();
            for (HoaDon hd : danhSachHoaDon) {
                if (hd.getTenKhachHang().toLowerCase().contains(keywordLower)) {
                    ketQua.add(hd);
                }
            }
        }
        table.setItems(ketQua);
        table.refresh();
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(500);
        form.setMaxHeight(400);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showChiTietHoaDon(HoaDon hoaDon) {
        VBox form = createCenteredForm("Chi tiết hóa đơn " + hoaDon.getMaHoaDon());

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.addRow(0, new Label("Mã Hóa Đơn:"), new Label(hoaDon.getMaHoaDon()));
        grid.addRow(1, new Label("Tên Khách Hàng:"), new Label(hoaDon.getTenKhachHang()));
        grid.addRow(2, new Label("Mô Tả:"), new Label(hoaDon.getMoTa()));
        grid.addRow(3, new Label("Tiền Phòng:"), new Label(String.format("%,.0f VNĐ", hoaDon.getTienPhong())));
        grid.addRow(4, new Label("Tiền Dịch Vụ:"), new Label(String.format("%,.0f VNĐ", hoaDon.getTienDichVu())));
        grid.addRow(5, new Label("Trạng Thái:"), new Label(hoaDon.getTrangThai()));
        grid.addRow(6, new Label("Phòng:"), new Label(hoaDon.getPhong() != null ? hoaDon.getPhong() : "Không có"));
        grid.addRow(7, new Label("Ngày Lập:"), new Label(hoaDon.getNgayLap() != null ? hoaDon.getNgayLap().toString() : "Không xác định"));
        grid.addRow(8, new Label("Hình Thức Thanh Toán:"), new Label(hoaDon.getHinhThucThanhToan()));
        grid.addRow(9, new Label("Mã Khách Hàng:"), new Label(hoaDon.getMaKhachHang()));
        grid.addRow(10, new Label("Mã Nhân Viên:"), new Label(hoaDon.getMaNhanVien()));

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(btnDong);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }
}