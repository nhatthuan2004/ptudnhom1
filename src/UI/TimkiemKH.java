package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.KhachHang;
import java.time.LocalDate;
import dao.KhachHang_Dao;

public class TimkiemKH {
    private final ObservableList<KhachHang> danhSachKhachHang;
    private final KhachHang_Dao dao;

    public TimkiemKH() {
        this.dao = new KhachHang_Dao();
        this.danhSachKhachHang = FXCollections.observableArrayList();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            danhSachKhachHang.clear();
            danhSachKhachHang.addAll(dao.getAllKhachHang());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu khách hàng: " + e.getMessage());
        }
    }

    public StackPane getUI() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #f0f0f0;");

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
        StackPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        // Tiêu đề + Thanh tìm kiếm
        Label title = new Label("Tìm kiếm khách hàng");
        title.setFont(new Font(20));

        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập mã hoặc tên khách hàng...");
        txtTimKiem.setPrefWidth(200);

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Bảng kết quả
        TableView<KhachHang> table = new TableView<>();
        table.setPrefWidth(1120);
        table.setPrefHeight(740);

        TableColumn<KhachHang, String> maKhachHangCol = new TableColumn<>("Mã KH");
        maKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        maKhachHangCol.setPrefWidth(100);

        TableColumn<KhachHang, String> tenKhachHangCol = new TableColumn<>("Tên Khách Hàng");
        tenKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        tenKhachHangCol.setPrefWidth(150);

        TableColumn<KhachHang, String> cccdCol = new TableColumn<>("CCCD");
        cccdCol.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        cccdCol.setPrefWidth(120);

        TableColumn<KhachHang, String> soDienThoaiCol = new TableColumn<>("Số Điện Thoại");
        soDienThoaiCol.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        soDienThoaiCol.setPrefWidth(120);

        TableColumn<KhachHang, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(150);

        TableColumn<KhachHang, String> diaChiCol = new TableColumn<>("Địa Chỉ");
        diaChiCol.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        diaChiCol.setPrefWidth(150);

        TableColumn<KhachHang, String> quocTichCol = new TableColumn<>("Quốc Tịch");
        quocTichCol.setCellValueFactory(new PropertyValueFactory<>("quocTich"));
        quocTichCol.setPrefWidth(100);

        TableColumn<KhachHang, String> gioiTinhCol = new TableColumn<>("Giới Tính");
        gioiTinhCol.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        gioiTinhCol.setPrefWidth(80);

        TableColumn<KhachHang, LocalDate> ngaySinhCol = new TableColumn<>("Ngày Sinh");
        ngaySinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        ngaySinhCol.setPrefWidth(100);

        table.getColumns().setAll(maKhachHangCol, tenKhachHangCol, cccdCol, soDienThoaiCol, emailCol, diaChiCol, quocTichCol, gioiTinhCol, ngaySinhCol);

        // Hiển thị toàn bộ danh sách ban đầu
        table.setItems(danhSachKhachHang);

        // Kết hợp layout nội dung
        VBox content = new VBox(10, title, searchBox, table);
        content.setPadding(new Insets(20));

        // Giao diện tổng
        BorderPane layout = new BorderPane();
        layout.setTop(new HBox(searchBox, userInfoBox));
        HBox.setHgrow(searchBox, Priority.ALWAYS);
        HBox.setHgrow(userInfoBox, Priority.NEVER);
        layout.setCenter(table);
        layout.setPadding(new Insets(10));

        // Hành động tìm kiếm
        btnTimKiem.setOnAction(e -> {
            String keyword = txtTimKiem.getText().trim();
            try {
                if (keyword.isEmpty()) {
                    loadDataFromDatabase(); // Hiển thị lại toàn bộ danh sách nếu không nhập từ khóa
                } else {
                    danhSachKhachHang.clear();
                    danhSachKhachHang.addAll(dao.timKiemKhachHang(keyword));
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tìm kiếm: " + ex.getMessage());
            }
        });

        txtTimKiem.setOnAction(e -> btnTimKiem.fire()); // Hỗ trợ tìm kiếm bằng Enter

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