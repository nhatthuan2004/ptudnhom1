package UI;

import dao.KhuyenMai_Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.KhuyenMai; // Sử dụng KhuyenMai thay vì ChuongTrinhKhuyenMai để đồng bộ với DAO

import java.sql.SQLException;

public class TimkiemKM {
    private final ObservableList<KhuyenMai> danhSachKhuyenMai;
    private final KhuyenMai_Dao khuyenMaiDao;

    public TimkiemKM() {
        try {
            khuyenMaiDao = new KhuyenMai_Dao();
            danhSachKhuyenMai = FXCollections.observableArrayList(khuyenMaiDao.getAllKhuyenMai());
        } catch (Exception e) {
            throw new RuntimeException("Không thể kết nối tới cơ sở dữ liệu!", e);
        }
    }

    public StackPane getUI() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: white;");

        // ==== THÔNG TIN NGƯỜI DÙNG GÓC PHẢI ====
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

        // ==== TIÊU ĐỀ + THANH TÌM KIẾM ==== 
        Label title = new Label("Tìm kiếm khuyến mãi theo tên");
        title.setFont(new Font(20));

        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập tên khuyến mãi...");

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        // ==== BẢNG KẾT QUẢ ==== 
        TableView<KhuyenMai> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<KhuyenMai, String> colMaKhuyenMai = new TableColumn<>("Mã Khuyến Mãi");
        colMaKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("maChuongTrinhKhuyenMai"));

        TableColumn<KhuyenMai, String> colTenKhuyenMai = new TableColumn<>("Tên Khuyến Mãi");
        colTenKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("tenChuongTrinhKhuyenMai"));

        TableColumn<KhuyenMai, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTaChuongTrinhKhuyenMai"));

        TableColumn<KhuyenMai, Double> colGiamGia = new TableColumn<>("Giảm Giá");
        colGiamGia.setCellValueFactory(new PropertyValueFactory<>("chietKhau"));

        TableColumn<KhuyenMai, Boolean> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setCellFactory(col -> new TableCell<KhuyenMai, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : (item ? "Hoạt động" : "Không hoạt động"));
            }
        });

        table.getColumns().addAll(colMaKhuyenMai, colTenKhuyenMai, colMoTa, colGiamGia, colTrangThai);

        // Hiển thị toàn bộ danh sách ban đầu
        table.setItems(danhSachKhuyenMai);

        // ==== KẾT HỢP LAYOUT NỘI DUNG ==== 
        VBox content = new VBox(10, title, searchBox, table);
        content.setPadding(new Insets(20));

        // ==== GIAO DIỆN TỔNG ==== 
        BorderPane layout = new BorderPane();
        layout.setCenter(content);
        layout.setTop(userInfoBox);
        layout.setPadding(new Insets(10));

        // ==== HÀNH ĐỘNG TÌM KIẾM ==== 
        btnTimKiem.setOnAction(e -> {
            String keyword = txtTimKiem.getText().trim();
            try {
                if (!keyword.isEmpty()) {
                    ObservableList<KhuyenMai> ketQua = FXCollections.observableArrayList(khuyenMaiDao.timKiemKhuyenMai(keyword));
                    table.setItems(ketQua);
                } else {
                    table.setItems(FXCollections.observableArrayList(khuyenMaiDao.getAllKhuyenMai())); // Hiển thị lại toàn bộ danh sách
                }
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Không thể tìm kiếm khuyến mãi: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        txtTimKiem.setOnAction(e -> btnTimKiem.fire()); // Hỗ trợ tìm kiếm bằng Enter

        root.getChildren().add(layout);
        return root;
    }
}