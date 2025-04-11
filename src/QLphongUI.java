package UI;

import dao.Phong_Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.*;

import java.sql.SQLException;
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
    private final Phong_Dao phongDao;

    private DatePicker dpNgay;
    private ComboBox<String> cbGio;
    private ComboBox<String> cbTrangThaiPhong;
    private ComboBox<String> cbLoaiPhong;
    private ComboBox<String> cbTrangThaiDonDep;
    private CheckBox showAllCheckBox;

    public QLphongUI() {
        try {	
            phongDao = new Phong_Dao();
            phongList = FXCollections.observableArrayList(phongDao.getAllPhong());
        } catch (Exception e) {
            throw new RuntimeException("Không thể kết nối tới cơ sở dữ liệu!", e);
        }
        DataManager dataManager = DataManager.getInstance();
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

        VBox infoPane = new VBox(15);
        infoPane.setPadding(new Insets(10, 20, 20, 20));
        infoPane.setAlignment(Pos.TOP_LEFT);
        infoPane.setPrefWidth(300);

        double labelWidth = 120;

        Label labelNgay = new Label("Ngày:");
        labelNgay.setMinWidth(labelWidth);
        dpNgay = new DatePicker(java.time.LocalDate.now());
        dpNgay.setPrefWidth(250);
        dpNgay.setStyle("-fx-font-size: 14px;");
        HBox hboxNgay = new HBox(10, labelNgay, dpNgay);
        hboxNgay.setAlignment(Pos.CENTER_LEFT);

        Label labelGio = new Label("Giờ:");
        labelGio.setMinWidth(labelWidth);
        cbGio = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            cbGio.getItems().add(String.format("%02d:00", i));
        }
        cbGio.setPromptText("Chọn giờ");
        cbGio.setPrefWidth(250);
        cbGio.setStyle("-fx-font-size: 14px;");
        HBox hboxGio = new HBox(10, labelGio, cbGio);
        hboxGio.setAlignment(Pos.CENTER_LEFT);

        Label labelTrangThaiPhong = new Label("Trạng thái phòng:");
        labelTrangThaiPhong.setMinWidth(labelWidth);
        cbTrangThaiPhong = new ComboBox<>();
        cbTrangThaiPhong.getItems().addAll("Trống", "Đã đặt", "Đang sửa");
        cbTrangThaiPhong.setPromptText("Chọn trạng thái");
        cbTrangThaiPhong.setPrefWidth(250);
        cbTrangThaiPhong.setStyle("-fx-font-size: 14px;");
        HBox hboxTrangThaiPhong = new HBox(10, labelTrangThaiPhong, cbTrangThaiPhong);
        hboxTrangThaiPhong.setAlignment(Pos.CENTER_LEFT);

        Label labelLoaiPhong = new Label("Loại phòng:");
        labelLoaiPhong.setMinWidth(labelWidth);
        cbLoaiPhong = new ComboBox<>();
        cbLoaiPhong.getItems().addAll("Đơn", "Đôi", "VIP");
        cbLoaiPhong.setPromptText("Chọn loại phòng");
        cbLoaiPhong.setPrefWidth(250);
        cbLoaiPhong.setStyle("-fx-font-size: 14px;");
        HBox hboxLoaiPhong = new HBox(10, labelLoaiPhong, cbLoaiPhong);
        hboxLoaiPhong.setAlignment(Pos.CENTER_LEFT);

        Label labelTrangThaiDonDep = new Label("Dọn dẹp:");
        labelTrangThaiDonDep.setMinWidth(labelWidth);
        cbTrangThaiDonDep = new ComboBox<>();
        cbTrangThaiDonDep.getItems().addAll("Đã dọn dẹp", "Chưa dọn dẹp");
        cbTrangThaiDonDep.setPromptText("Chọn trạng thái dọn dẹp");
        cbTrangThaiDonDep.setPrefWidth(250);
        cbTrangThaiDonDep.setStyle("-fx-font-size: 14px;");
        HBox hboxTrangThaiDonDep = new HBox(10, labelTrangThaiDonDep, cbTrangThaiDonDep);
        hboxTrangThaiDonDep.setAlignment(Pos.CENTER_LEFT);

        Button addRoomButton = new Button("Thêm Phòng");
        addRoomButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        addRoomButton.setPrefWidth(200);
        addRoomButton.setOnAction(e -> showAddRoomDialog());
        HBox hboxAddRoom = new HBox(10, new Label(""), addRoomButton);
        hboxAddRoom.setAlignment(Pos.CENTER_LEFT);

        showAllCheckBox = new CheckBox("Hiển thị tất cả phòng");
        showAllCheckBox.setStyle("-fx-font-size: 14px;");
        showAllCheckBox.setOnAction(e -> filterRooms());
        HBox hboxShowAll = new HBox(10, new Label(""), showAllCheckBox);
        hboxShowAll.setAlignment(Pos.CENTER_LEFT);

        infoPane.getChildren().addAll(hboxNgay, hboxGio, hboxTrangThaiPhong, hboxLoaiPhong, hboxTrangThaiDonDep, hboxAddRoom, hboxShowAll);

        VBox roomPane = new VBox(10);
        roomPane.setPadding(new Insets(10, 25, 25, 25));
        roomPane.setAlignment(Pos.TOP_CENTER);
        roomPane.setPrefWidth(550);
        roomPane.setMaxHeight(465);

        roomFlowPane = new FlowPane();
        roomFlowPane.setHgap(20);
        roomFlowPane.setVgap(20);
        roomFlowPane.setAlignment(Pos.CENTER);
        roomFlowPane.setPrefWidth(530);

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
        gridLayout.setHgap(25);
        gridLayout.add(infoPane, 0, 0);
        gridLayout.add(roomPane, 1, 0);
        GridPane.setValignment(infoPane, VPos.TOP);
        GridPane.setValignment(roomPane, VPos.TOP);
        GridPane.setHgrow(roomPane, Priority.ALWAYS);

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
            if (donDep != null && !phong.getDonDep().equals(donDep))
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
            roomBox.setPrefSize(180, 150);
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
            loaiPhongLabel.setStyle("-fx-font-size: 14px;");
            Label trangThaiLabel = new Label("Trạng thái: " + phong.getTrangThai());
            trangThaiLabel.setStyle("-fx-font-size: 14px;");
            Label donDepLabel = new Label("Dọn dẹp: " + phong.getDonDep());

            HoaDon hoaDon = hoaDonList.stream()
                    .filter(hd -> hd.getMaPhong().equals(phong.getMaPhong()) && "Chưa thanh toán".equals(hd.getTrangThai()))
                    .findFirst()
                    .orElse(null);
            Label khachHangLabel = new Label("Khách: " + (hoaDon != null ? hoaDon.getTenKhachHang() : "Không có"));
            khachHangLabel.setStyle("-fx-font-size: 14px;");

            maPhongLabel.setMaxWidth(160);
            loaiPhongLabel.setMaxWidth(160);
            trangThaiLabel.setMaxWidth(160);
            donDepLabel.setMaxWidth(160);
            khachHangLabel.setMaxWidth(160);

            roomBox.getChildren().addAll(maPhongLabel, loaiPhongLabel, trangThaiLabel, donDepLabel, khachHangLabel);
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
        form.setMaxHeight(600);

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
        maPhongField.setDisable(true); // Mã phòng sẽ được sinh tự động
        ComboBox<String> loaiPhongCombo = new ComboBox<>();
        loaiPhongCombo.getItems().addAll("Đơn", "Đôi", "VIP");
        loaiPhongCombo.setPromptText("Chọn loại phòng");
        TextField giaPhongField = new TextField();
        giaPhongField.setPromptText("Giá phòng (VD: 300000)");
        ComboBox<String> trangThaiCombo = new ComboBox<>();
        trangThaiCombo.getItems().addAll("Trống", "Đã đặt", "Đang sửa");
        trangThaiCombo.setPromptText("Chọn trạng thái");
        ComboBox<String> donDepCombo = new ComboBox<>();
        donDepCombo.getItems().addAll("Đã dọn dẹp", "Chưa dọn dẹp");
        donDepCombo.setPromptText("Chọn trạng thái dọn dẹp");
        TextField viTriField = new TextField();
        viTriField.setPromptText("Vị trí (e.g., Tầng 4)");
        TextField soNguoiToiDaField = new TextField();
        soNguoiToiDaField.setPromptText("Số người tối đa (e.g., 2)");
        TextField moTaField = new TextField();
        moTaField.setPromptText("Mô tả (e.g., Phòng sạch sẽ)");

        try {
            maPhongField.setText(phongDao.getNextMaPhong()); // Sinh mã tự động
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể sinh mã phòng: " + e.getMessage());
            return;
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Mã Phòng:"), maPhongField);
        grid.addRow(1, new Label("Loại Phòng:"), loaiPhongCombo);
        grid.addRow(2, new Label("Giá Phòng:"), giaPhongField);
        grid.addRow(3, new Label("Trạng Thái:"), trangThaiCombo);
        grid.addRow(4, new Label("Dọn Dẹp:"), donDepCombo);
        grid.addRow(5, new Label("Vị Trí:"), viTriField);
        grid.addRow(6, new Label("Số Người Tối Đa:"), soNguoiToiDaField);
        grid.addRow(7, new Label("Mô Tả:"), moTaField);

        Button btnThem = new Button("Thêm");
        btnThem.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnThem.setOnAction(e -> {
            String maPhong = maPhongField.getText();
            String loaiPhong = loaiPhongCombo.getValue();
            String giaPhongText = giaPhongField.getText();
            String trangThai = trangThaiCombo.getValue();
            String donDep = donDepCombo.getValue();
            String viTri = viTriField.getText();
            String soNguoiToiDaText = soNguoiToiDaField.getText();
            String moTa = moTaField.getText();

            if (maPhong.isEmpty() || loaiPhong == null || giaPhongText.isEmpty() || trangThai == null ||
                donDep == null || viTri.isEmpty() || soNguoiToiDaText.isEmpty() || moTa.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            double giaPhong;
            try {
                giaPhong = Double.parseDouble(giaPhongText);
                if (giaPhong <= 0) {
                    showAlert("Lỗi", "Giá phòng phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Giá phòng phải là số hợp lệ!");
                return;
            }

            int soNguoiToiDa;
            try {
                soNguoiToiDa = Integer.parseInt(soNguoiToiDaText);
                if (soNguoiToiDa <= 0) {
                    showAlert("Lỗi", "Số người tối đa phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Số người tối đa phải là số nguyên hợp lệ!");
                return;
            }

            Phong newPhong = new Phong(maPhong, loaiPhong, giaPhong, trangThai, donDep, viTri, soNguoiToiDa, moTa);
            try {
                if (phongDao.themPhong(newPhong)) {
                    phongList.add(newPhong);
                    updateRoomDisplayDirectly();
                    contentPane.getChildren().setAll(mainPane);
                } else {
                    showAlert("Lỗi", "Không thể thêm phòng!");
                }
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể thêm phòng: " + ex.getMessage());
            }
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
        Label maPhongLabel = new Label("Mã phòng: " + phong.getMaPhong());
        Label loaiPhongLabel = new Label("Loại phòng: " + phong.getLoaiPhong());
        Label giaPhongLabel = new Label("Giá phòng: " + phong.getGiaPhong());
        Label trangThaiLabel = new Label("Trạng thái: " + phong.getTrangThai());
        Label donDepLabel = new Label("Dọn dẹp: " + phong.getDonDep());
        Label viTriLabel = new Label("Vị trí: " + phong.getViTri());
        Label soNguoiToiDaLabel = new Label("Số người tối đa: " + phong.getSoNguoiToiDa());
        Label moTaLabel = new Label("Mô tả: " + phong.getMoTa());

        HoaDon hoaDon = hoaDonList.stream()
                .filter(hd -> hd.getMaPhong().equals(phong.getMaPhong()) && "Chưa thanh toán".equals(hd.getTrangThai()))
                .findFirst()
                .orElse(null);
        Label khachHangLabel = new Label("Khách hàng: " + (hoaDon != null ? hoaDon.getTenKhachHang() : "Không có"));

        content.getChildren().addAll(phongLabel, maPhongLabel, loaiPhongLabel, giaPhongLabel, trangThaiLabel,
                donDepLabel, viTriLabel, soNguoiToiDaLabel, moTaLabel, khachHangLabel);

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
        TextField giaPhongField = new TextField(String.valueOf(phong.getGiaPhong()));
        ComboBox<String> trangThaiCombo = new ComboBox<>(FXCollections.observableArrayList("Trống", "Đã đặt", "Đang sửa"));
        trangThaiCombo.setValue(phong.getTrangThai());
        ComboBox<String> donDepCombo = new ComboBox<>(FXCollections.observableArrayList("Đã dọn dẹp", "Chưa dọn dẹp"));
        donDepCombo.setValue(phong.getDonDep());
        TextField viTriField = new TextField(phong.getViTri());
        TextField soNguoiToiDaField = new TextField(String.valueOf(phong.getSoNguoiToiDa()));
        TextField moTaField = new TextField(phong.getMoTa());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Mã Phòng:"), maPhongField);
        grid.addRow(1, new Label("Loại Phòng:"), loaiPhongCombo);
        grid.addRow(2, new Label("Giá Phòng:"), giaPhongField);
        grid.addRow(3, new Label("Trạng Thái:"), trangThaiCombo);
        grid.addRow(4, new Label("Dọn Dẹp:"), donDepCombo);
        grid.addRow(5, new Label("Vị Trí:"), viTriField);
        grid.addRow(6, new Label("Số Người Tối Đa:"), soNguoiToiDaField);
        grid.addRow(7, new Label("Mô Tả:"), moTaField);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> {
            String loaiPhong = loaiPhongCombo.getValue();
            String giaPhongText = giaPhongField.getText();
            String trangThai = trangThaiCombo.getValue();
            String donDep = donDepCombo.getValue();
            String viTri = viTriField.getText();
            String soNguoiToiDaText = soNguoiToiDaField.getText();
            String moTa = moTaField.getText();

            if (loaiPhong == null || giaPhongText.isEmpty() || trangThai == null || donDep == null ||
                viTri.isEmpty() || soNguoiToiDaText.isEmpty() || moTa.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            double giaPhong;
            try {
                giaPhong = Double.parseDouble(giaPhongText);
                if (giaPhong <= 0) {
                    showAlert("Lỗi", "Giá phòng phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Giá phòng phải là số hợp lệ!");
                return;
            }

            int soNguoiToiDa;
            try {
                soNguoiToiDa = Integer.parseInt(soNguoiToiDaText);
                if (soNguoiToiDa <= 0) {
                    showAlert("Lỗi", "Số người tối đa phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Số người tối đa phải là số nguyên hợp lệ!");
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

            Phong updatedPhong = new Phong(phong.getMaPhong(), loaiPhong, giaPhong, trangThai, donDep, viTri, soNguoiToiDa, moTa);
            try {
                if (phongDao.suaPhong(updatedPhong)) {
                    int index = phongList.indexOf(phong);
                    phongList.set(index, updatedPhong);
                    updateRoomDisplayDirectly();
                    contentPane.getChildren().setAll(mainPane);
                } else {
                    showAlert("Lỗi", "Không thể sửa phòng!");
                }
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể sửa phòng: " + ex.getMessage());
            }
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
                    try {
                        if (phongDao.xoaPhong(phong.getMaPhong())) {
                            phongList.remove(phong);
                            updateRoomDisplayDirectly();
                            contentPane.getChildren().setAll(mainPane);
                        } else {
                            showAlert("Lỗi", "Không thể xóa phòng!");
                        }
                    } catch (SQLException ex) {
                        showAlert("Lỗi", "Không thể xóa phòng: " + ex.getMessage());
                    }
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