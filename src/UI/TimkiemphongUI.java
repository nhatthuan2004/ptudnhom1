package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Phong;
import dao.Phong_Dao;

import java.sql.SQLException;
import java.util.List;

public class TimkiemphongUI {
    private ObservableList<Phong> phongList;
    private FlowPane roomFlowPane;
    private StackPane contentPane;
    private StackPane mainPane;
    private final Phong_Dao phongDao;

    public TimkiemphongUI() {
        try {
            phongDao = new Phong_Dao();
            phongList = FXCollections.observableArrayList(phongDao.getAllPhong());
        } catch (Exception e) {
            throw new RuntimeException("Không thể kết nối tới cơ sở dữ liệu!", e);
        }
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: white;");

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
        userInfoBox.setAlignment(Pos.CENTER);

        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.TOP_CENTER);
        centerLayout.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label titleLabel = new Label("Tìm kiếm phòng");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField searchField = new TextField();
        searchField.setPromptText("Nhập mã phòng hoặc loại phòng (ví dụ: P101, Đơn)");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);

        roomFlowPane = new FlowPane();
        roomFlowPane.setHgap(20);
        roomFlowPane.setVgap(20);
        roomFlowPane.setAlignment(Pos.CENTER);
        roomFlowPane.setPadding(new Insets(10));

        updateRoomDisplay();

        ScrollPane scrollPane = new ScrollPane(roomFlowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        centerLayout.getChildren().addAll(titleLabel, searchBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setTop(userInfoBox);
        BorderPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        BorderPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));
        layout.setCenter(centerLayout);
        layout.setPadding(new Insets(10));

        Runnable searchAction = () -> {
            String tuKhoa = searchField.getText().trim();
            try {
                if (tuKhoa.isEmpty()) {
                    phongList.setAll(phongDao.getAllPhong());
                } else {
                    List<Phong> filteredList = phongDao.timKiemPhong(tuKhoa);
                    if (filteredList.isEmpty()) {
                        showAlert("Thông báo", "Không tìm thấy phòng nào với từ khóa: " + tuKhoa);
                    }
                    phongList.setAll(filteredList);
                }
                updateRoomDisplay();
            } catch (SQLException e) {
                showAlert("Lỗi", "Lỗi khi tìm kiếm phòng: " + e.getMessage());
            }
        };

        searchButton.setOnAction(e -> searchAction.run());
        searchField.setOnAction(e -> searchAction.run());

        mainPane.getChildren().add(layout);
        return mainPane;
    }

    public StackPane getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    private void updateRoomDisplay() {
        roomFlowPane.getChildren().clear();
        for (Phong phong : phongList) {
            VBox roomBox = new VBox(8);
            roomBox.setPrefSize(200, 200); // Tăng kích thước để chứa thêm thông tin
            roomBox.setPadding(new Insets(10));
            roomBox.setAlignment(Pos.CENTER_LEFT);
            String bgColor = switch (phong.getTrangThai()) {
                case "Trống" -> "#90EE90";
                case "Đã đặt" -> "#FFD700";
                case "Đang sửa" -> "#FF6347";
                default -> "#E0E0E0";
            };
            roomBox.setStyle("-fx-background-color: " + bgColor
                    + "; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #666; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            roomBox.setOnMouseClicked(e -> showRoomDetailsDialog(phong));

            Label maPhongLabel = new Label("Phòng: " + phong.getMaPhong());
            maPhongLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            Label loaiPhongLabel = new Label("Loại: " + phong.getLoaiPhong());
            Label trangThaiLabel = new Label("Trạng thái: " + phong.getTrangThai());
            // Sửa lỗi null cho donDep
            String donDepText = phong.getDonDep() != null && phong.getDonDep().equals("Đã dọn dẹp")
                    ? "Đã dọn dẹp" : "Chưa dọn dẹp";
            Label donDepLabel = new Label("Dọn dẹp: " + donDepText);
            Label viTriLabel = new Label("Vị trí: " + (phong.getViTri() != null ? phong.getViTri() : "Chưa xác định"));
            Label moTaLabel = new Label("Mô tả: " + (phong.getMoTa() != null ? phong.getMoTa() : "Chưa có mô tả"));
            Label giaPhongLabel = new Label("Giá: " + String.format("%,.0f VNĐ", phong.getGiaPhong()));

            maPhongLabel.setMaxWidth(180);
            loaiPhongLabel.setMaxWidth(180);
            trangThaiLabel.setMaxWidth(180);
            donDepLabel.setMaxWidth(180);
            viTriLabel.setMaxWidth(180);
            moTaLabel.setMaxWidth(180);
            giaPhongLabel.setMaxWidth(180);

            roomBox.getChildren().addAll(maPhongLabel, loaiPhongLabel, trangThaiLabel, donDepLabel,
                    viTriLabel, moTaLabel, giaPhongLabel);
            roomFlowPane.getChildren().add(roomBox);
        }
    }

    private void showRoomDetailsDialog(Phong phong) {
        VBox form = createCenteredForm("Chi tiết phòng " + phong.getMaPhong());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label phongLabel = new Label("Thông tin phòng:");
        phongLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        Label maPhongLabel = new Label("Mã phòng: " + phong.getMaPhong());
        Label loaiPhongLabel = new Label("Loại phòng: " + phong.getLoaiPhong());
        Label giaPhongLabel = new Label("Giá phòng: " + String.format("%,.0f VNĐ", phong.getGiaPhong()));
        Label trangThaiLabel = new Label("Trạng thái: " + phong.getTrangThai());
        // Sửa lỗi null cho donDep trong dialog
        Label donDepLabel = new Label("Dọn dẹp: " + (phong.getDonDep() != null ? phong.getDonDep() : "Chưa xác định"));
        Label viTriLabel = new Label("Vị trí: " + (phong.getViTri() != null ? phong.getViTri() : "Chưa xác định"));
        Label soNguoiToiDaLabel = new Label("Số người tối đa: " + phong.getSoNguoiToiDa());
        Label moTaLabel = new Label("Mô tả: " + (phong.getMoTa() != null ? phong.getMoTa() : "Chưa có mô tả"));

        content.getChildren().addAll(phongLabel, maPhongLabel, loaiPhongLabel, giaPhongLabel, trangThaiLabel,
                donDepLabel, viTriLabel, soNguoiToiDaLabel, moTaLabel);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        buttons.getChildren().add(btnDong);

        form.getChildren().addAll(content, buttons);
        contentPane.getChildren().setAll(form);
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(400);
        form.setMaxHeight(500);

        Label title = new Label(titleText);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}