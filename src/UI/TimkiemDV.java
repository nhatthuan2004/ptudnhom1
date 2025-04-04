package UI;

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
    private final DataManager dataManager;

    public TimkiemDV() {
        dataManager = DataManager.getInstance();
        this.danhSachDichVu = dataManager.getDichVuList();
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
        Label title = new Label("Tìm kiếm dịch vụ theo tên");
        title.setFont(new Font(20));

        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập tên dịch vụ...");
        
        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        // ==== BẢNG KẾT QUẢ ==== 
        TableView<DichVu> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DichVu, String> colMaDichVu = new TableColumn<>("Mã Dịch Vụ");
        colMaDichVu.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));

        TableColumn<DichVu, String> colTenDichVu = new TableColumn<>("Tên Dịch Vụ");
        colTenDichVu.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));

        TableColumn<DichVu, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));

        TableColumn<DichVu, Double> colGia = new TableColumn<>("Giá");
        colGia.setCellValueFactory(new PropertyValueFactory<>("gia"));

        TableColumn<DichVu, String> colLoai = new TableColumn<>("Loại");
        colLoai.setCellValueFactory(new PropertyValueFactory<>("loai"));

        TableColumn<DichVu, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        table.getColumns().addAll(colMaDichVu, colTenDichVu, colMoTa, colGia, colLoai, colTrangThai);

        VBox content = new VBox(10, title, searchBox, table);
        content.setPadding(new Insets(20));

        BorderPane layout = new BorderPane();
        layout.setCenter(content);
        layout.setTop(userInfoBox);  // Đặt UserInfoBox lên trên
        layout.setPadding(new Insets(10));

        btnTimKiem.setOnAction(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            ObservableList<DichVu> ketQua = FXCollections.observableArrayList();
            if (!keyword.isEmpty()) {
                for (DichVu dv : danhSachDichVu) {
                    if (dv.getTenDichVu().toLowerCase().contains(keyword)) {
                        ketQua.add(dv);
                    }
                }
            } else {
                ketQua.setAll(danhSachDichVu); // Hiển thị toàn bộ danh sách nếu không có từ khóa
            }
            table.setItems(ketQua);
        });

        txtTimKiem.setOnAction(btnTimKiem.getOnAction());

        // Ban đầu hiển thị toàn bộ danh sách dịch vụ
        table.setItems(danhSachDichVu);

        root.getChildren().add(layout);
        return root;
    }
}