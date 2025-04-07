package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class QLphongUI {
    private final ObservableList<Phong> phongList;
    private final ObservableList<HoaDon> hoaDonList;
    private final ObservableList<DichVu> dichVuList;
    private final ObservableList<KhachHang> khachHangList;
    private FlowPane roomFlowPane;
    private StackPane contentPane;
    private StackPane mainPane;
    private final List<HoaDon> hoaDonPhongList = new ArrayList<>();

    private DatePicker dpNgay;
    private ComboBox<String> cbGio;
    private ComboBox<String> cbTrangThaiPhong;
    private ComboBox<String> cbLoaiPhong;
    private ComboBox<String> cbTrangThaiDonDep;
    private CheckBox showAllCheckBox;

    public QLphongUI() {
        DataManager dataManager = DataManager.getInstance();
        this.phongList = dataManager.getPhongList();
        this.hoaDonList = dataManager.getHoaDonList();
        this.khachHangList = dataManager.getKhachHangList();
        this.dichVuList = dataManager.getDichVuList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
        dataManager.addPhongListChangeListener(this::updateRoomDisplayDirectly);
        dataManager.addHoaDonListChangeListener(this::updateRoomDisplayDirectly);
    }

    private StackPane createMainPane() {
        StackPane root = new StackPane();
        BorderPane layout = new BorderPane();

        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("User Info Placeholder"));
            userInfoBox.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        }
        userInfoBox.setPrefSize(200, 50);
        layout.setTop(userInfoBox);
        BorderPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        BorderPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        // Bên trái: infoPane
        VBox infoPane = new VBox(15);
        infoPane.setPadding(new Insets(10, 20, 20, 20));
        infoPane.setAlignment(Pos.TOP_LEFT);
        infoPane.setPrefWidth(300); // Thêm để cân đối

        double labelWidth = 120;

        Label labelNgay = new Label("Ngày:");
        labelNgay.setMinWidth(labelWidth);
        dpNgay = new DatePicker(java.time.LocalDate.now());
        dpNgay.setPrefWidth(250); // Tăng từ 200 lên 250
        dpNgay.setStyle("-fx-font-size: 14px;"); // Thêm font-size
        HBox hboxNgay = new HBox(10, labelNgay, dpNgay);
        hboxNgay.setAlignment(Pos.CENTER_LEFT);

        Label labelGio = new Label("Giờ:");
        labelGio.setMinWidth(labelWidth);
        cbGio = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            cbGio.getItems().add(String.format("%02d:00", i));
        }
        cbGio.setPromptText("Chọn giờ");
        cbGio.setPrefWidth(250); // Tăng từ 200 lên 250
        cbGio.setStyle("-fx-font-size: 14px;"); // Thêm font-size
        HBox hboxGio = new HBox(10, labelGio, cbGio);
        hboxGio.setAlignment(Pos.CENTER_LEFT);

        Label labelTrangThaiPhong = new Label("Trạng thái phòng:");
        labelTrangThaiPhong.setMinWidth(labelWidth);
        cbTrangThaiPhong = new ComboBox<>();
        cbTrangThaiPhong.getItems().addAll("Trống", "Đã đặt", "Đang sửa");
        cbTrangThaiPhong.setPromptText("Chọn trạng thái");
        cbTrangThaiPhong.setPrefWidth(250); // Tăng từ 200 lên 250
        cbTrangThaiPhong.setStyle("-fx-font-size: 14px;"); // Thêm font-size
        HBox hboxTrangThaiPhong = new HBox(10, labelTrangThaiPhong, cbTrangThaiPhong);
        hboxTrangThaiPhong.setAlignment(Pos.CENTER_LEFT);

        Label labelLoaiPhong = new Label("Loại phòng:");
        labelLoaiPhong.setMinWidth(labelWidth);
        cbLoaiPhong = new ComboBox<>();
        cbLoaiPhong.getItems().addAll("Đơn", "Đôi", "VIP");
        cbLoaiPhong.setPromptText("Chọn loại phòng");
        cbLoaiPhong.setPrefWidth(250); // Tăng từ 200 lên 250
        cbLoaiPhong.setStyle("-fx-font-size: 14px;"); // Thêm font-size
        HBox hboxLoaiPhong = new HBox(10, labelLoaiPhong, cbLoaiPhong);
        hboxLoaiPhong.setAlignment(Pos.CENTER_LEFT);

        Label labelTrangThaiDonDep = new Label("Dọn dẹp:");
        labelTrangThaiDonDep.setMinWidth(labelWidth);
        cbTrangThaiDonDep = new ComboBox<>();
        cbTrangThaiDonDep.getItems().addAll("Đã dọn dẹp", "Chưa dọn dẹp");
        cbTrangThaiDonDep.setPromptText("Chọn trạng thái dọn dẹp");
        cbTrangThaiDonDep.setPrefWidth(250); // Tăng từ 200 lên 250
        cbTrangThaiDonDep.setStyle("-fx-font-size: 14px;"); // Thêm font-size
        HBox hboxTrangThaiDonDep = new HBox(10, labelTrangThaiDonDep, cbTrangThaiDonDep);
        hboxTrangThaiDonDep.setAlignment(Pos.CENTER_LEFT);

        Button addRoomButton = new Button("Thêm Phòng");
        addRoomButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        addRoomButton.setPrefWidth(200); // Thêm chiều rộng
        addRoomButton.setOnAction(e -> showAddRoomDialog());
        HBox hboxAddRoom = new HBox(10, new Label(""), addRoomButton);
        hboxAddRoom.setAlignment(Pos.CENTER_LEFT);

        showAllCheckBox = new CheckBox("Hiển thị tất cả phòng");
        showAllCheckBox.setStyle("-fx-font-size: 14px;"); // Thêm font-size
        showAllCheckBox.setOnAction(e -> filterRooms());
        HBox hboxShowAll = new HBox(10, new Label(""), showAllCheckBox);
        hboxShowAll.setAlignment(Pos.CENTER_LEFT);

        infoPane.getChildren().addAll(hboxNgay, hboxGio, hboxTrangThaiPhong, hboxLoaiPhong, hboxTrangThaiDonDep, hboxAddRoom, hboxShowAll);

        // Bên phải: roomPane
        VBox roomPane = new VBox(10);
        roomPane.setPadding(new Insets(10, 25, 25, 25));
        roomPane.setAlignment(Pos.TOP_CENTER);
        roomPane.setPrefWidth(550); // Thêm để cân đối
        roomPane.setMaxHeight(465);

        roomFlowPane = new FlowPane();
        roomFlowPane.setHgap(20);
        roomFlowPane.setVgap(20);
        roomFlowPane.setAlignment(Pos.CENTER);
        roomFlowPane.setPrefWidth(530); // Thêm để phù hợp với roomPane

        cbTrangThaiDonDep.setOnAction(e -> filterRooms());
        cbTrangThaiPhong.setOnAction(e -> filterRooms());
        cbLoaiPhong.setOnAction(e -> filterRooms());
        dpNgay.setOnAction(e -> filterRooms());
        cbGio.setOnAction(e -> filterRooms());

        updateRoomDisplayDirectly();

        ScrollPane scrollPane = new ScrollPane(roomFlowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        roomPane.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        GridPane gridLayout = new GridPane();
        gridLayout.setPadding(new Insets(10));
        gridLayout.setHgap(25); // Tăng từ 20 lên 25
        gridLayout.add(infoPane, 0, 0);
        gridLayout.add(roomPane, 1, 0);
        GridPane.setValignment(infoPane, VPos.TOP);
        GridPane.setValignment(roomPane, VPos.TOP);
        GridPane.setHgrow(roomPane, Priority.ALWAYS); // Thay Vgrow thành Hgrow để roomPane mở rộng ngang

        VBox layoutMain = new VBox(10, gridLayout);
        layoutMain.setAlignment(Pos.TOP_CENTER);

        layout.setCenter(layoutMain);
        root.getChildren().add(layout);
        return root;
    }

    public StackPane getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    private void filterRooms() {
        ObservableList<Phong> filteredList = FXCollections.observableArrayList();
        String trangThai = cbTrangThaiPhong.getValue();
        String loaiPhong = cbLoaiPhong.getValue();
        String donDep = cbTrangThaiDonDep.getValue();

        for (Phong phong : phongList) {
            boolean matches = true;
            if (trangThai != null && !phong.getTrangThai().equals(trangThai))
                matches = false;
            if (loaiPhong != null && !phong.getLoaiPhong().equals(loaiPhong))
                matches = false;
            if (donDep != null && !phong.getMoTa().contains(donDep))
                matches = false;
            if (matches || showAllCheckBox.isSelected())
                filteredList.add(phong);
        }
        updateRoomDisplay(filteredList);
    }

    private void updateRoomDisplay(ObservableList<Phong> displayList) {
        roomFlowPane.getChildren().clear();
        for (Phong phong : displayList) {
            VBox roomBox = new VBox(8);
            roomBox.setPrefSize(180, 150); // Tăng từ 160, 130 lên 180, 150
            roomBox.setPadding(new Insets(10));
            roomBox.setAlignment(Pos.CENTER_LEFT);
            String bgColor = switch (phong.getTrangThai()) {
                case "Trống" -> "#90EE90";
                case "Đã đặt" -> "#FFD700";
                case "Đang sửa" -> "#FF6347";
                default -> "#E0E0E0";
            };
            roomBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #666; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            roomBox.setOnMouseClicked(e -> showRoomDetailsDialog(phong));

            Label maPhongLabel = new Label("Phòng: " + phong.getMaPhong());
            maPhongLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label loaiPhongLabel = new Label("Loại: " + phong.getLoaiPhong());
            loaiPhongLabel.setStyle("-fx-font-size: 14px;"); // Thêm font-size
            Label trangThaiLabel = new Label("Trạng thái: " + phong.getTrangThai());
            trangThaiLabel.setStyle("-fx-font-size: 14px;"); // Thêm font-size

            HoaDon hoaDon = hoaDonList.stream()
                    .filter(hd -> hd.getMaPhong().equals(phong.getMaPhong()) && "Chưa thanh toán".equals(hd.getTrangThai()))
                    .findFirst()
                    .orElse(null);
            Label khachHangLabel = new Label("Khách: " + (hoaDon != null ? hoaDon.getTenKhachHang() : "Không có"));
            khachHangLabel.setStyle("-fx-font-size: 14px;"); // Thêm font-size

            maPhongLabel.setMaxWidth(160); // Tăng từ 140 lên 160
            loaiPhongLabel.setMaxWidth(160);
            trangThaiLabel.setMaxWidth(160);
            khachHangLabel.setMaxWidth(160);

            roomBox.getChildren().addAll(maPhongLabel, loaiPhongLabel, trangThaiLabel, khachHangLabel);
            roomFlowPane.getChildren().add(roomBox);
        }
    }

    private void updateRoomDisplayDirectly() {
        updateRoomDisplay(phongList);
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(500);
        form.setMaxHeight(400);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showAddRoomDialog() {
        VBox form = createCenteredForm("Thêm Phòng Mới");

        TextField maPhongField = new TextField();
        maPhongField.setPromptText("Mã phòng (e.g., P401)");
        ComboBox<String> loaiPhongCombo = new ComboBox<>();
        loaiPhongCombo.getItems().addAll("Đơn", "Đôi", "VIP");
        loaiPhongCombo.setPromptText("Chọn loại phòng");
        ComboBox<String> trangThaiCombo = new ComboBox<>();
        trangThaiCombo.getItems().addAll("Trống", "Đã đặt", "Đang sửa");
        trangThaiCombo.setPromptText("Chọn trạng thái");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(new Label("Mã Phòng:"), 0, 0);
        grid.add(maPhongField, 1, 0);
        grid.add(new Label("Loại Phòng:"), 0, 1);
        grid.add(loaiPhongCombo, 1, 1);
        grid.add(new Label("Trạng Thái:"), 0, 2);
        grid.add(trangThaiCombo, 1, 2);

        Button btnThem = new Button("Thêm");
        btnThem.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnThem.setOnAction(e -> {
            String maPhong = maPhongField.getText();
            String loaiPhong = loaiPhongCombo.getValue();
            String trangThai = trangThaiCombo.getValue();

            if (maPhong.isEmpty() || loaiPhong == null || trangThai == null) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            if (phongList.stream().anyMatch(p -> p.getMaPhong().equals(maPhong))) {
                showAlert("Lỗi", "Mã phòng " + maPhong + " đã tồn tại!");
                return;
            }

            double giaPhong = switch (loaiPhong) {
                case "Đơn" -> 300000;
                case "Đôi" -> 500000;
                case "VIP" -> 1000000;
                default -> 0;
            };
            Phong newPhong = new Phong(maPhong, loaiPhong, giaPhong, trangThai, "Tầng " + maPhong.charAt(1), loaiPhong.equals("Đơn") ? 2 : 4, "Phòng " + loaiPhong + " sạch sẽ");
            phongList.add(newPhong);

            updateRoomDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(10, btnThem, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }

    private void showRoomDetailsDialog(Phong phong) {
        VBox form = createCenteredForm("Chi tiết phòng " + phong.getMaPhong());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label phongLabel = new Label("Thông tin phòng:");
        phongLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        Label maPhong = new Label("Mã phòng: " + phong.getMaPhong());
        Label loaiPhong = new Label("Loại phòng: " + phong.getLoaiPhong());
        Label trangThai = new Label("Trạng thái: " + phong.getTrangThai());
        Label viTri = new Label("Vị trí: " + phong.getViTri());

        HoaDon hoaDon = hoaDonList.stream()
                .filter(hd -> hd.getMaPhong().equals(phong.getMaPhong()) && "Chưa thanh toán".equals(hd.getTrangThai()))
                .findFirst()
                .orElse(null);
        Label khachHangLabel = new Label("Khách hàng: " + (hoaDon != null ? hoaDon.getTenKhachHang() : "Không có"));

        content.getChildren().addAll(phongLabel, maPhong, loaiPhong, trangThai, viTri, khachHangLabel);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button btnSua = new Button("Sửa");
        btnSua.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnSua.setOnAction(e -> showEditRoomDialog(phong));

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        buttons.getChildren().addAll(btnSua, btnDong);

        form.getChildren().addAll(content, buttons);
        contentPane.getChildren().setAll(form);
    }

    private void showEditRoomDialog(Phong phong) {
        VBox form = createCenteredForm("Chỉnh sửa phòng " + phong.getMaPhong());

        TextField maPhongField = new TextField(phong.getMaPhong());
        maPhongField.setDisable(true);
        ComboBox<String> loaiPhongCombo = new ComboBox<>(FXCollections.observableArrayList("Đơn", "Đôi", "VIP"));
        loaiPhongCombo.setValue(phong.getLoaiPhong());
        ComboBox<String> trangThaiCombo = new ComboBox<>(FXCollections.observableArrayList("Trống", "Đã đặt", "Đang sửa"));
        trangThaiCombo.setValue(phong.getTrangThai());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Mã Phòng:"), maPhongField);
        grid.addRow(1, new Label("Loại Phòng:"), loaiPhongCombo);
        grid.addRow(2, new Label("Trạng Thái:"), trangThaiCombo);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> {
            String loaiPhong = loaiPhongCombo.getValue();
            String trangThai = trangThaiCombo.getValue();

            if (loaiPhong == null || trangThai == null) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin phòng!");
                return;
            }

            HoaDon hoaDon = hoaDonList.stream()
                    .filter(hd -> hd.getMaPhong().equals(phong.getMaPhong()) && "Chưa thanh toán".equals(hd.getTrangThai()))
                    .findFirst()
                    .orElse(null);
            if (hoaDon != null && !"Đã đặt".equals(trangThai)) {
                showAlert("Lỗi", "Phòng đang có đặt phòng, không thể thay đổi trạng thái khác 'Đã đặt'!");
                return;
            }

            int index = phongList.indexOf(phong);
            double giaPhong = switch (loaiPhong) {
                case "Đơn" -> 300000;
                case "Đôi" -> 500000;
                case "VIP" -> 1000000;
                default -> phong.getGiaPhong();
            };
            Phong updatedPhong = new Phong(phong.getMaPhong(), loaiPhong, giaPhong, trangThai, phong.getViTri(), phong.getSoNguoiToiDa(), phong.getMoTa());
            phongList.set(index, updatedPhong);

            updateRoomDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnXoa = new Button("Xóa");
        btnXoa.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnXoa.setOnAction(e -> {
            HoaDon hoaDon = hoaDonList.stream()
                    .filter(hd -> hd.getMaPhong().equals(phong.getMaPhong()) && "Chưa thanh toán".equals(hd.getTrangThai()))
                    .findFirst()
                    .orElse(null);
            if (hoaDon != null) {
                showAlert("Lỗi", "Không thể xóa phòng đang có đặt phòng!");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận xóa");
            confirm.setHeaderText("Bạn có chắc muốn xóa phòng này?");
            confirm.setContentText("Phòng: " + phong.getMaPhong());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    phongList.remove(phong);
                    updateRoomDisplayDirectly();
                    contentPane.getChildren().setAll(mainPane);
                }
            });
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(10, btnLuu, btnXoa, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}