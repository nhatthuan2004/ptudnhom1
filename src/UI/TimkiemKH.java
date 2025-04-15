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

public class TimkiemKH {
    private final ObservableList<KhachHang> danhSachKhachHang;
    private final DataManager dataManager;

    public TimkiemKH() {
        dataManager = DataManager.getInstance();
        this.danhSachKhachHang = dataManager.getKhachHangList();
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
        Label title = new Label("Tìm kiếm khách hàng");
        title.setFont(new Font(20));
        title.setStyle("-fx-font-weight: bold;");

        // Search bar
        TextField txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập tên khách hàng...");
        txtTimKiem.setPrefWidth(300);
        txtTimKiem.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, txtTimKiem, btnTimKiem);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER);

        // Table
        TableView<KhachHang> table = new TableView<>();
        table.setStyle("-fx-border-color: #d3d3d3; -fx-background-radius: 5; -fx-border-radius: 5;");

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        TableColumn<KhachHang, String> maKhachHangCol = new TableColumn<>("Mã KH");
        maKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        maKhachHangCol.setPrefWidth(100);

        TableColumn<KhachHang, String> tenKhachHangCol = new TableColumn<>("Họ và Tên");
        tenKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        tenKhachHangCol.setPrefWidth(150);

        TableColumn<KhachHang, String> cccdCol = new TableColumn<>("CCCD");
        cccdCol.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        cccdCol.setPrefWidth(150);

        TableColumn<KhachHang, String> soDienThoaiCol = new TableColumn<>("Số Điện Thoại");
        soDienThoaiCol.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        soDienThoaiCol.setPrefWidth(120);

        TableColumn<KhachHang, String> diaChiCol = new TableColumn<>("Địa Chỉ");
        diaChiCol.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        diaChiCol.setPrefWidth(200);

        TableColumn<KhachHang, String> quocTichCol = new TableColumn<>("Quốc Tịch");
        quocTichCol.setCellValueFactory(new PropertyValueFactory<>("quocTich"));
        quocTichCol.setPrefWidth(100);

        TableColumn<KhachHang, String> gioiTinhCol = new TableColumn<>("Giới Tính");
        gioiTinhCol.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        gioiTinhCol.setPrefWidth(100);

        TableColumn<KhachHang, LocalDate> ngaySinhCol = new TableColumn<>("Ngày Sinh");
        ngaySinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        ngaySinhCol.setPrefWidth(120);

        table.getColumns().setAll(maKhachHangCol, tenKhachHangCol, cccdCol, soDienThoaiCol, diaChiCol, quocTichCol, gioiTinhCol, ngaySinhCol);
        table.setItems(danhSachKhachHang);

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
            ObservableList<KhachHang> ketQua = FXCollections.observableArrayList();
            if (keyword.isEmpty()) {
                ketQua.addAll(danhSachKhachHang);
            } else {
                for (KhachHang kh : danhSachKhachHang) {
                    if (kh.getTenKhachHang().toLowerCase().contains(keyword)) {
                        ketQua.add(kh);
                    }
                }
                if (ketQua.isEmpty()) {
                    showAlert("Thông báo", "Không tìm thấy khách hàng nào phù hợp với từ khóa: " + keyword);
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