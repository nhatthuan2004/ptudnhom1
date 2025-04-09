package UI;

import dao.DichVu_Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.DichVu;

public class TimkiemDV {
    private final ObservableList<DichVu> danhSachDichVu;
    private final DichVu_Dao dichVuDao;

    public TimkiemDV() {
        this.dichVuDao = new DichVu_Dao();
        this.danhSachDichVu = FXCollections.observableArrayList();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            danhSachDichVu.clear();
            danhSachDichVu.addAll(dichVuDao.getAllDichVu());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu dịch vụ: " + e.getMessage());
        }
    }

    public StackPane getUI() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: white;");

        // Thêm UserInfoBox ở góc trên bên phải
        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("User Info Placeholder"));
            userInfoBox.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        }
        userInfoBox.setPrefSize(200, 50);
        StackPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        // ==== TIÊU ĐỀ ==== 
        Label title = new Label("Tìm kiếm dịch vụ");
        title.setFont(new Font(20));

        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập mã hoặc tên dịch vụ...");
        
        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        // ==== BẢNG KẾT QUẢ ==== 
        TableView<DichVu> table = new TableView<>();
        table.setPrefWidth(1120);
        table.setPrefHeight(740);

        TableColumn<DichVu, String> colMaDichVu = new TableColumn<>("Mã Dịch Vụ");
        colMaDichVu.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));
        colMaDichVu.setPrefWidth(150);

        TableColumn<DichVu, String> colTenDichVu = new TableColumn<>("Tên Dịch Vụ");
        colTenDichVu.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        colTenDichVu.setPrefWidth(200);

        TableColumn<DichVu, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        colMoTa.setPrefWidth(300);

        TableColumn<DichVu, Double> colGia = new TableColumn<>("Giá (VNĐ)");
        colGia.setCellValueFactory(new PropertyValueFactory<>("gia"));
        colGia.setPrefWidth(150);

        TableColumn<DichVu, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setPrefWidth(100);

        table.getColumns().addAll(colMaDichVu, colTenDichVu, colMoTa, colGia, colTrangThai);

        VBox content = new VBox(10, title, searchBox, table);
        content.setPadding(new Insets(20));

        BorderPane layout = new BorderPane();
        layout.setCenter(content);
        layout.setTop(userInfoBox);  // Đặt UserInfoBox lên trên
        layout.setPadding(new Insets(10));

        // Hành động tìm kiếm
        btnTimKiem.setOnAction(e -> {
            String keyword = txtTimKiem.getText().trim();
            try {
                if (keyword.isEmpty()) {
                    loadDataFromDatabase(); // Hiển thị lại toàn bộ danh sách nếu không nhập từ khóa
                } else {
                    danhSachDichVu.clear();
                    danhSachDichVu.addAll(dichVuDao.timKiemDichVu(keyword));
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tìm kiếm: " + ex.getMessage());
            }
        });

        txtTimKiem.setOnAction(e -> btnTimKiem.fire()); // Hỗ trợ tìm kiếm bằng Enter

        // Ban đầu hiển thị toàn bộ danh sách dịch vụ
        table.setItems(danhSachDichVu);

        root.getChildren().add(layout);
        return root;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}