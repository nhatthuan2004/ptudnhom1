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

        // Hộp thông tin người dùng
        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("Chưa có thông tin người dùng"));
            userInfoBox.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        }
        userInfoBox.setPrefSize(200, 50);
        userInfoBox.setMaxSize(200, 50);
        StackPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        // Tiêu đề
        Label title = new Label("Tìm kiếm dịch vụ theo tên");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold;");

        // Thanh tìm kiếm
        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập tên dịch vụ...");
        txtTimKiem.setPrefWidth(300);
        txtTimKiem.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        // Bảng
        TableView<DichVu> table = new TableView<>();
        table.setStyle("-fx-border-color: #d3d3d3; -fx-background-radius: 5; -fx-border-radius: 5;");

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        TableColumn<DichVu, String> colMaDichVu = new TableColumn<>("Mã Dịch Vụ");
        colMaDichVu.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));
        colMaDichVu.setPrefWidth(100);

        TableColumn<DichVu, String> colTenDichVu = new TableColumn<>("Tên Dịch Vụ");
        colTenDichVu.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        colTenDichVu.setPrefWidth(150);

        TableColumn<DichVu, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        colMoTa.setPrefWidth(200);

        TableColumn<DichVu, Double> colGia = new TableColumn<>("Giá");
        colGia.setCellValueFactory(new PropertyValueFactory<>("gia"));
        colGia.setPrefWidth(120);

        TableColumn<DichVu, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setPrefWidth(100);

        table.getColumns().addAll(colMaDichVu, colTenDichVu, colMoTa, colGia, colTrangThai);
        table.setItems(danhSachDichVu);

        // Bố cục nội dung
        VBox content = new VBox(15, title, searchBox, scrollPane);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setCenter(content);
        layout.setPadding(new Insets(10));

        // Hành động tìm kiếm
        Runnable searchAction = () -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            ObservableList<DichVu> ketQua = FXCollections.observableArrayList();
            if (keyword.isEmpty()) {
                ketQua.addAll(danhSachDichVu);
            } else {
                for (DichVu dv : danhSachDichVu) {
                    if (dv.getTenDichVu().toLowerCase().contains(keyword)) {
                        ketQua.add(dv);
                    }
                }
                if (ketQua.isEmpty()) {
                    showAlert("Thông báo", "Không tìm thấy dịch vụ nào phù hợp với từ khóa: " + keyword);
                }
            }
            table.setItems(ketQua);
        };

        btnTimKiem.setOnAction(e -> searchAction.run());
        txtTimKiem.setOnAction(e -> searchAction.run());

        root.getChildren().addAll(layout, userInfoBox);
        return root;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}