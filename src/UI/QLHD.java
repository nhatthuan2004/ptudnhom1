package UI;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.HoaDon;
import java.time.LocalDateTime;

public class QLHD {
    private final ObservableList<HoaDon> danhSachHoaDon;
    private TableView<HoaDon> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final DataManager dataManager;

    public QLHD() {
        dataManager = DataManager.getInstance();
        this.danhSachHoaDon = dataManager.getHoaDonList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: #f0f0f0;");

        // UserInfoBox
        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("User Info Placeholder"));
            userInfoBox.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        }
        userInfoBox.setPrefSize(200, 50);
        userInfoBox.setMaxSize(200, 50);
        userInfoBox.setAlignment(Pos.CENTER); // Căn giữa nội dung trong UserInfoBox

        // Header với UserInfoBox ở góc phải
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f0f0f0;");
        Region spacer = new Region(); // Khoảng trống để đẩy UserInfoBox sang phải
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(spacer, userInfoBox);

        // Bảng hóa đơn
        table = new TableView<>(danhSachHoaDon);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<HoaDon, String> maHoaDonCol = new TableColumn<>("Mã Hóa Đơn");
        maHoaDonCol.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        maHoaDonCol.setPrefWidth(100);

        TableColumn<HoaDon, String> tenKhachHangCol = new TableColumn<>("Khách Hàng");
        tenKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        tenKhachHangCol.setPrefWidth(150);

        TableColumn<HoaDon, String> moTaCol = new TableColumn<>("Mô Tả");
        moTaCol.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        moTaCol.setPrefWidth(200);

        TableColumn<HoaDon, Double> tienPhongCol = new TableColumn<>("Tiền Phòng (VNĐ)");
        tienPhongCol.setCellValueFactory(new PropertyValueFactory<>("tienPhong"));
        tienPhongCol.setPrefWidth(120);

        TableColumn<HoaDon, Double> tienDichVuCol = new TableColumn<>("Tiền DV (VNĐ)");
        tienDichVuCol.setCellValueFactory(new PropertyValueFactory<>("tienDichVu"));
        tienDichVuCol.setPrefWidth(120);

        TableColumn<HoaDon, String> hinhThucThanhToanCol = new TableColumn<>("Hình Thức TT");
        hinhThucThanhToanCol.setCellValueFactory(new PropertyValueFactory<>("hinhThucThanhToan"));
        hinhThucThanhToanCol.setPrefWidth(120);

        TableColumn<HoaDon, String> trangThaiCol = new TableColumn<>("Trạng Thái");
        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        trangThaiCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.equals("Đã thanh toán") ? "Đã thanh toán" : "Chưa thanh toán");
                }
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

        // Sự kiện nhấp đúp để xem chi tiết
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

        // Bố cục chính
        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(table);
        layout.setPadding(new Insets(10));

        VBox.setVgrow(table, Priority.ALWAYS); // TableView mở rộng theo chiều dọc

        mainPane.getChildren().add(layout);
        return mainPane;
    }

    public StackPane getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle(
            "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
            "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        form.setMaxWidth(600);
        form.setMaxHeight(500);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showChiTietHoaDon(HoaDon hoaDon) {
        VBox form = createCenteredForm("Chi tiết hóa đơn " + hoaDon.getMaHoaDon());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Mã Hóa Đơn:"), 0, 0); grid.add(new Label(hoaDon.getMaHoaDon()), 1, 0);
        grid.add(new Label("Tên Khách Hàng:"), 0, 1); grid.add(new Label(hoaDon.getTenKhachHang()), 1, 1);
        grid.add(new Label("Mô Tả:"), 0, 2); grid.add(new Label(hoaDon.getMoTa()), 1, 2);
        grid.add(new Label("Tiền Phòng:"), 0, 3); grid.add(new Label(String.format("%,.0f VNĐ", hoaDon.getTienPhong())), 1, 3);
        grid.add(new Label("Tiền Dịch Vụ:"), 0, 4); grid.add(new Label(String.format("%,.0f VNĐ", hoaDon.getTienDichVu())), 1, 4);
        grid.add(new Label("Hình Thức TT:"), 0, 5); grid.add(new Label(hoaDon.getHinhThucThanhToan()), 1, 5);
        grid.add(new Label("Trạng Thái:"), 0, 6); grid.add(new Label(hoaDon.getTrangThai()), 1, 6);
        grid.add(new Label("Mã Khách Hàng:"), 0, 7); grid.add(new Label(hoaDon.getMaKhachHang()), 1, 7);
        grid.add(new Label("Mã Nhân Viên:"), 0, 8); grid.add(new Label(hoaDon.getMaNhanVien()), 1, 8);
        grid.add(new Label("Ngày Lập:"), 0, 9); grid.add(new Label(hoaDon.getNgayLap().toString()), 1, 9);

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(btnDong);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 0, 0));

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }
}