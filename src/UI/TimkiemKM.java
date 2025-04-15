package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.KhuyenMai;

public class TimkiemKM {
    private final ObservableList<KhuyenMai> danhSachKhuyenMai;
    private final DataManager dataManager;

    public TimkiemKM() {
        dataManager = DataManager.getInstance();
        this.danhSachKhuyenMai = dataManager.getKhuyenMaiList();
    }

    public StackPane getUI() {
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
        Label title = new Label("Tìm kiếm khuyến mãi theo tên");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold;");

        // Search bar
        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập tên khuyến mãi...");
        txtTimKiem.setPrefWidth(300);
        txtTimKiem.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        // Table
        TableView<KhuyenMai> table = new TableView<>();
        table.setStyle("-fx-border-color: #d3d3d3; -fx-background-radius: 5; -fx-border-radius: 5;");

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        TableColumn<KhuyenMai, String> colMaKhuyenMai = new TableColumn<>("Mã Khuyến Mãi");
        colMaKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("maChuongTrinhKhuyenMai"));
        colMaKhuyenMai.setPrefWidth(120);

        TableColumn<KhuyenMai, String> colTenKhuyenMai = new TableColumn<>("Tên Khuyến Mãi");
        colTenKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("tenChuongTrinhKhuyenMai"));
        colTenKhuyenMai.setPrefWidth(150);

        TableColumn<KhuyenMai, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTaChuongTrinhKhuyenMai"));
        colMoTa.setPrefWidth(200);

        TableColumn<KhuyenMai, Double> colGiamGia = new TableColumn<>("Giảm Giá");
        colGiamGia.setCellValueFactory(new PropertyValueFactory<>("chietKhau"));
        colGiamGia.setPrefWidth(100);

        TableColumn<KhuyenMai, Boolean> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setCellFactory(col -> new TableCell<KhuyenMai, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : (item ? "Hoạt động" : "Không hoạt động"));
            }
        });
        colTrangThai.setPrefWidth(100);

        table.getColumns().addAll(colMaKhuyenMai, colTenKhuyenMai, colMoTa, colGiamGia, colTrangThai);
        table.setItems(danhSachKhuyenMai);

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
        Runnable searchAction = () -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            ObservableList<KhuyenMai> ketQua = FXCollections.observableArrayList();
            if (keyword.isEmpty()) {
                ketQua.addAll(danhSachKhuyenMai);
            } else {
                for (KhuyenMai km : danhSachKhuyenMai) {
                    if (km.getTenChuongTrinhKhuyenMai().toLowerCase().contains(keyword)) {
                        ketQua.add(km);
                    }
                }
                if (ketQua.isEmpty()) {
                    showAlert("Thông báo", "Không tìm thấy khuyến mãi nào phù hợp với từ khóa: " + keyword);
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