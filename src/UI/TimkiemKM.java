package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.ChuongTrinhKhuyenMai; // Sử dụng ChuongTrinhKhuyenMai từ DataManager

public class TimkiemKM {
    private final ObservableList<ChuongTrinhKhuyenMai> danhSachKhuyenMai;
    private final DataManager dataManager;

    public TimkiemKM() {
        dataManager = DataManager.getInstance();
        this.danhSachKhuyenMai = dataManager.getKhuyenMaiList();
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
        TableView<ChuongTrinhKhuyenMai> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ChuongTrinhKhuyenMai, String> colMaKhuyenMai = new TableColumn<>("Mã Khuyến Mãi");
        colMaKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("maChuongTrinhKhuyenMai"));

        TableColumn<ChuongTrinhKhuyenMai, String> colTenKhuyenMai = new TableColumn<>("Tên Khuyến Mãi");
        colTenKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("tenChuongTrinhKhuyenMai"));

        TableColumn<ChuongTrinhKhuyenMai, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTaChuongTrinhKhuyenMai"));

        TableColumn<ChuongTrinhKhuyenMai, Double> colGiamGia = new TableColumn<>("Giảm Giá");
        colGiamGia.setCellValueFactory(new PropertyValueFactory<>("chietKhau"));

        TableColumn<ChuongTrinhKhuyenMai, Boolean> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setCellFactory(col -> new TableCell<ChuongTrinhKhuyenMai, Boolean>() {
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
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            if (!keyword.isEmpty()) {
                ObservableList<ChuongTrinhKhuyenMai> ketQua = FXCollections.observableArrayList();
                for (ChuongTrinhKhuyenMai km : danhSachKhuyenMai) {
                    if (km.getTenChuongTrinhKhuyenMai().toLowerCase().contains(keyword)) {
                        ketQua.add(km);
                    }
                }
                table.setItems(ketQua);
            } else {
                table.setItems(danhSachKhuyenMai); // Hiển thị lại toàn bộ danh sách nếu không nhập từ khóa
            }
        });

        txtTimKiem.setOnAction(e -> btnTimKiem.fire()); // Hỗ trợ tìm kiếm bằng Enter

        root.getChildren().add(layout);
        return root;
    }
}