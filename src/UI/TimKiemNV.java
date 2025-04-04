package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.NhanVien;

public class TimKiemNV {
    private final ObservableList<NhanVien> danhSachNhanVien;
    private StackPane contentPane;
    private final DataManager dataManager;

    public TimKiemNV() {
        dataManager = DataManager.getInstance();
        this.danhSachNhanVien = dataManager.getNhanVienList();
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
        Label title = new Label("Tìm kiếm nhân viên theo mã hoặc họ tên");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold;");

        // Search bar
        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập mã hoặc họ tên nhân viên...");
        txtTimKiem.setPrefWidth(300);
        txtTimKiem.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        // Table
        TableView<NhanVien> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-border-color: #d3d3d3; -fx-background-radius: 5; -fx-border-radius: 5;");

        TableColumn<NhanVien, String> colMaNhanVien = new TableColumn<>("Mã Nhân Viên");
        colMaNhanVien.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colMaNhanVien.setPrefWidth(100);

        TableColumn<NhanVien, String> colTenNhanVien = new TableColumn<>("Họ Tên");
        colTenNhanVien.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        colTenNhanVien.setPrefWidth(150);

        TableColumn<NhanVien, String> colSoDienThoai = new TableColumn<>("Số Điện Thoại");
        colSoDienThoai.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colSoDienThoai.setPrefWidth(120);

        TableColumn<NhanVien, String> colDiaChi = new TableColumn<>("Địa Chỉ");
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        colDiaChi.setPrefWidth(200);

        TableColumn<NhanVien, String> colChucVu = new TableColumn<>("Chức Vụ");
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        colChucVu.setPrefWidth(100);

        TableColumn<NhanVien, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setPrefWidth(100);

        table.getColumns().addAll(colMaNhanVien, colTenNhanVien, colSoDienThoai, colDiaChi, colChucVu, colTrangThai);
        table.setItems(danhSachNhanVien); // Hiển thị toàn bộ danh sách ban đầu

        // Content layout
        VBox content = new VBox(15, title, searchBox, table);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        VBox.setVgrow(table, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setCenter(content);
        layout.setPadding(new Insets(10));

        // Search action
        Runnable searchAction = () -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            ObservableList<NhanVien> ketQua = FXCollections.observableArrayList();
            if (keyword.isEmpty()) {
                ketQua.addAll(danhSachNhanVien); // Hiển thị tất cả nếu không có từ khóa
            } else {
                for (NhanVien nv : danhSachNhanVien) {
                    if (nv.getMaNhanVien().toLowerCase().contains(keyword)
                            || nv.getTenNhanVien().toLowerCase().contains(keyword)) {
                        ketQua.add(nv);
                    }
                }
                if (ketQua.isEmpty()) {
                    showAlert("Thông báo", "Không tìm thấy nhân viên nào phù hợp với từ khóa: " + keyword);
                }
            }
            table.setItems(ketQua);
        };

        btnTimKiem.setOnAction(e -> searchAction.run());
        txtTimKiem.setOnAction(e -> searchAction.run());

        root.getChildren().addAll(layout, userInfoBox);
        return root;
    }

    public StackPane getUI() {
        return contentPane;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}