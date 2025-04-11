package UI;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.NhanVien;

public class QLNV {
    private final ObservableList<NhanVien> danhSachNhanVien;
    private TableView<NhanVien> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final DataManager dataManager;

    public QLNV() {
        dataManager = DataManager.getInstance();
        this.danhSachNhanVien = dataManager.getNhanVienList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: #f0f0f0;");
        mainPane.setPrefSize(1120, 800);

        HBox userInfoBox = UserInfoBox.createUserInfoBox();
        userInfoBox.setPrefSize(200, 50);
        userInfoBox.setMaxSize(200, 50);
        StackPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        NhanVien currentUser = UserInfoBox.getCurrentUser();
        boolean isQuanLy = currentUser != null && "Quản lý".equals(currentUser.getChucVu());

        Button addButton = new Button("+ Thêm nhân viên");
        addButton.setStyle(
            "-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 6 12; -fx-background-radius: 6;"
        );
        addButton.setVisible(isQuanLy); // Chỉ quản lý mới được thêm
        addButton.setOnAction(e -> showNhanVienForm(null));

        HBox addBox = new HBox(addButton);
        addBox.setAlignment(Pos.CENTER_LEFT);
        addBox.setPadding(new Insets(0, 20, 10, 20));

        VBox topHeader = new VBox(addBox);
        topHeader.setSpacing(10);
        StackPane.setAlignment(topHeader, Pos.TOP_LEFT);

        table = new TableView<>(danhSachNhanVien);
        table.setPrefWidth(1120);
        table.setPrefHeight(740);

        TableColumn<NhanVien, String> maNhanVienCol = new TableColumn<>("Mã NV");
        maNhanVienCol.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        maNhanVienCol.setPrefWidth(100);

        TableColumn<NhanVien, String> tenNhanVienCol = new TableColumn<>("Họ Tên");
        tenNhanVienCol.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        tenNhanVienCol.setPrefWidth(150);

        TableColumn<NhanVien, String> soDienThoaiCol = new TableColumn<>("SĐT");
        soDienThoaiCol.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        soDienThoaiCol.setPrefWidth(120);

        TableColumn<NhanVien, String> diaChiCol = new TableColumn<>("Địa Chỉ");
        diaChiCol.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        diaChiCol.setPrefWidth(200);

        TableColumn<NhanVien, String> chucVuCol = new TableColumn<>("Chức Vụ");
        chucVuCol.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        chucVuCol.setPrefWidth(120);

        TableColumn<NhanVien, String> trangThaiCol = new TableColumn<>("Trạng Thái");
        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        trangThaiCol.setPrefWidth(120);

        TableColumn<NhanVien, String> taiKhoanCol = new TableColumn<>("Tài Khoản");
        taiKhoanCol.setCellValueFactory(new PropertyValueFactory<>("taiKhoan"));
        taiKhoanCol.setPrefWidth(100);

        TableColumn<NhanVien, Void> editCol = new TableColumn<>("Sửa");
        editCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            {
                btnEdit.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
                btnEdit.setVisible(isQuanLy); // Chỉ quản lý mới được sửa
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEdit);
                    btnEdit.setOnAction(e -> showNhanVienForm(getTableRow().getItem()));
                }
            }
        });
        editCol.setPrefWidth(100);

        TableColumn<NhanVien, Void> deleteCol = new TableColumn<>("Xóa");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Xóa");
            {
                btnDelete.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");
                btnDelete.setVisible(isQuanLy); // Chỉ quản lý mới được xóa
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                    btnDelete.setOnAction(e -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Xác nhận xóa");
                        confirm.setHeaderText("Bạn có chắc muốn xóa nhân viên này?");
                        confirm.setContentText("Nhân viên: " + getTableRow().getItem().getTenNhanVien());
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                danhSachNhanVien.remove(getTableRow().getItem());
                            }
                        });
                    });
                }
            }
        });
        deleteCol.setPrefWidth(100);

        table.getColumns().setAll(maNhanVienCol, tenNhanVienCol, soDienThoaiCol, diaChiCol, chucVuCol, trangThaiCol, taiKhoanCol, editCol, deleteCol);

        BorderPane layout = new BorderPane();
        layout.setTop(new HBox(topHeader, userInfoBox));
        HBox.setHgrow(topHeader, Priority.ALWAYS);
        HBox.setHgrow(userInfoBox, Priority.NEVER);
        layout.setCenter(table);
        layout.setPadding(new Insets(10));

        mainPane.getChildren().add(layout);
        return mainPane;
    }

    public StackPane getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle(
            "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
            "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        form.setMaxWidth(500);
        form.setMaxHeight(500);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showNhanVienForm(NhanVien nhanVien) {
        boolean isEditMode = nhanVien != null;
        VBox form = createCenteredForm(isEditMode ? "Sửa nhân viên " + nhanVien.getMaNhanVien() : "Thêm nhân viên mới");

        TextField tfMaNhanVien = new TextField(isEditMode ? nhanVien.getMaNhanVien() : "");
        tfMaNhanVien.setPromptText("Mã nhân viên...");
        tfMaNhanVien.setDisable(isEditMode);

        TextField tfTenNhanVien = new TextField(isEditMode ? nhanVien.getTenNhanVien() : "");
        tfTenNhanVien.setPromptText("Họ tên...");

        TextField tfSoDienThoai = new TextField(isEditMode ? nhanVien.getSoDienThoai() : "");
        tfSoDienThoai.setPromptText("Số điện thoại (10 số)...");

        TextField tfDiaChi = new TextField(isEditMode ? nhanVien.getDiaChi() : "");
        tfDiaChi.setPromptText("Địa chỉ...");

        ComboBox<String> cbChucVu = new ComboBox<>();
        cbChucVu.getItems().addAll("Quản lý", "Lễ tân", "Phục vụ", "Bảo vệ");
        cbChucVu.setPromptText("Chọn chức vụ...");
        if (isEditMode) cbChucVu.setValue(nhanVien.getChucVu());

        ComboBox<String> cbTrangThai = new ComboBox<>();
        cbTrangThai.getItems().addAll("Đang làm", "Nghỉ việc");
        cbTrangThai.setPromptText("Chọn trạng thái...");
        if (isEditMode) cbTrangThai.setValue(nhanVien.getTrangThai());
        else cbTrangThai.setValue("Đang làm");

        TextField tfTaiKhoan = new TextField(isEditMode ? nhanVien.getTaiKhoan() : "");
        tfTaiKhoan.setPromptText("Tài khoản...");

        PasswordField pfMatKhau = new PasswordField();
        pfMatKhau.setPromptText("Mật khẩu...");
        if (isEditMode) pfMatKhau.setText(nhanVien.getMatKhau());

        CheckBox cbGioiTinh = new CheckBox("Nam");
        if (isEditMode) cbGioiTinh.setSelected(nhanVien.isGioiTinh());

        TextField tfLuong = new TextField(isEditMode ? String.valueOf(nhanVien.getLuong()) : "0");
        tfLuong.setPromptText("Lương...");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Mã NV:"), 0, 0); grid.add(tfMaNhanVien, 1, 0);
        grid.add(new Label("Họ Tên:"), 0, 1); grid.add(tfTenNhanVien, 1, 1);
        grid.add(new Label("SĐT:"), 0, 2); grid.add(tfSoDienThoai, 1, 2);
        grid.add(new Label("Địa Chỉ:"), 0, 3); grid.add(tfDiaChi, 1, 3);
        grid.add(new Label("Chức Vụ:"), 0, 4); grid.add(cbChucVu, 1, 4);
        grid.add(new Label("Trạng Thái:"), 0, 5); grid.add(cbTrangThai, 1, 5);
        grid.add(new Label("Tài Khoản:"), 0, 6); grid.add(tfTaiKhoan, 1, 6);
        grid.add(new Label("Mật Khẩu:"), 0, 7); grid.add(pfMatKhau, 1, 7);
        grid.add(new Label("Giới Tính:"), 0, 8); grid.add(cbGioiTinh, 1, 8);
        grid.add(new Label("Lương:"), 0, 9); grid.add(tfLuong, 1, 9);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> {
            String maNhanVien = tfMaNhanVien.getText();
            String tenNhanVien = tfTenNhanVien.getText();
            String soDienThoai = tfSoDienThoai.getText();
            String diaChi = tfDiaChi.getText();
            String chucVu = cbChucVu.getValue();
            String trangThai = cbTrangThai.getValue();
            String taiKhoan = tfTaiKhoan.getText();
            String matKhau = pfMatKhau.getText();
            boolean gioiTinh = cbGioiTinh.isSelected();
            String luongText = tfLuong.getText();

            if (maNhanVien.isEmpty() || tenNhanVien.isEmpty() || soDienThoai.isEmpty() || diaChi.isEmpty() || 
                chucVu == null || trangThai == null || taiKhoan.isEmpty() || matKhau.isEmpty() || luongText.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            if (!soDienThoai.matches("0\\d{9}")) {
                showAlert("Lỗi", "Số điện thoại phải bắt đầu bằng 0 và gồm 10 chữ số!");
                return;
            }

            double luong;
            try {
                luong = Double.parseDouble(luongText);
                if (luong < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Lương phải là số không âm!");
                return;
            }

            if (!isEditMode) {
                if (danhSachNhanVien.stream().anyMatch(nv -> nv.getMaNhanVien().equals(maNhanVien))) {
                    showAlert("Lỗi", "Mã nhân viên " + maNhanVien + " đã tồn tại!");
                    return;
                }
                if (danhSachNhanVien.stream().anyMatch(nv -> nv.getTaiKhoan().equals(taiKhoan))) {
                    showAlert("Lỗi", "Tài khoản " + taiKhoan + " đã tồn tại!");
                    return;
                }
                NhanVien newNhanVien = new NhanVien(maNhanVien, tenNhanVien, soDienThoai, gioiTinh, diaChi, chucVu, luong, taiKhoan, matKhau, trangThai);
                dataManager.addNhanVien(newNhanVien);
            } else {
                nhanVien.setTenNhanVien(tenNhanVien);
                nhanVien.setSoDienThoai(soDienThoai);
                nhanVien.setGioiTinh(gioiTinh);
                nhanVien.setDiaChi(diaChi);
                nhanVien.setChucVu(chucVu);
                nhanVien.setLuong(luong);
                nhanVien.setTaiKhoan(taiKhoan);
                nhanVien.setMatKhau(matKhau);
                nhanVien.setTrangThai(trangThai);
                table.refresh();
            }
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(10, btnLuu, btnHuy);
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