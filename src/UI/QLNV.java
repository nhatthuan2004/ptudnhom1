package UI;

import dao.NhanVien_Dao;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.NhanVien;

import java.sql.SQLException;

public class QLNV {
    private final ObservableList<NhanVien> danhSachNhanVien;
    private TableView<NhanVien> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final DataManager dataManager;
    private final NhanVien_Dao nhanVienDao;

    public QLNV() {
        dataManager = DataManager.getInstance();
        this.danhSachNhanVien = dataManager.getNhanVienList();
        this.nhanVienDao = new NhanVien_Dao();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            danhSachNhanVien.clear();
            danhSachNhanVien.addAll(nhanVienDao.getAllNhanVien());
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải dữ liệu nhân viên: " + e.getMessage());
        }
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: #f0f0f0;");

        // User info display
        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("Chưa đăng nhập"));
            userInfoBox.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        }
        userInfoBox.setPrefSize(200, 50);
        userInfoBox.setMaxSize(200, 50);

        // Header
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f0f0f0;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(spacer, userInfoBox);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm theo mã, tên nhân viên...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        // Add button
        Button addButton = new Button("+ Thêm nhân viên");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        addButton.setOnAction(e -> showNhanVienForm(null));

        HBox searchBox = new HBox(10, new Label("Tìm kiếm:"), searchField, searchButton, addButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            try {
                if (keyword.isEmpty()) {
                    loadDataFromDatabase();
                } else {
                    danhSachNhanVien.clear();
                    danhSachNhanVien.addAll(nhanVienDao.timKiemNhanVien(keyword));
                }
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể tìm kiếm: " + ex.getMessage());
            }
        });

        searchField.setOnAction(e -> searchButton.fire());

        // Employee table
        table = new TableView<>(danhSachNhanVien);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<NhanVien, String> maNhanVienCol = new TableColumn<>("Mã NV");
        maNhanVienCol.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        maNhanVienCol.setMinWidth(100);

        TableColumn<NhanVien, String> tenNhanVienCol = new TableColumn<>("Họ Tên");
        tenNhanVienCol.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        tenNhanVienCol.setMinWidth(150);

        TableColumn<NhanVien, String> soDienThoaiCol = new TableColumn<>("SĐT");
        soDienThoaiCol.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        soDienThoaiCol.setMinWidth(120);

        TableColumn<NhanVien, String> diaChiCol = new TableColumn<>("Địa Chỉ");
        diaChiCol.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        diaChiCol.setMinWidth(200);

        TableColumn<NhanVien, String> chucVuCol = new TableColumn<>("Chức Vụ");
        chucVuCol.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        chucVuCol.setMinWidth(120);

        TableColumn<NhanVien, Double> luongCol = new TableColumn<>("Lương");
        luongCol.setCellValueFactory(new PropertyValueFactory<>("luong"));
        luongCol.setMinWidth(120);

        TableColumn<NhanVien, Void> editCol = new TableColumn<>("Sửa");
        editCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            {
                btnEdit.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
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
        editCol.setMinWidth(100);

        TableColumn<NhanVien, Void> deleteCol = new TableColumn<>("Xóa");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Xóa");
            {
                btnDelete.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
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
                                NhanVien nv = getTableRow().getItem();
                                try {
                                    if (nhanVienDao.xoaNhanVien(nv.getMaNhanVien())) {
                                        danhSachNhanVien.remove(nv);
                                        showAlert("Thành công", "Đã xóa nhân viên thành công!");
                                    } else {
                                        showAlert("Lỗi", "Không thể xóa nhân viên!");
                                    }
                                } catch (SQLException ex) {
                                    showAlert("Lỗi", "Lỗi khi xóa: " + ex.getMessage());
                                }
                            }
                        });
                    });
                }
            }
        });
        deleteCol.setMinWidth(100);

        table.getColumns().setAll(maNhanVienCol, tenNhanVienCol, soDienThoaiCol, diaChiCol, chucVuCol, luongCol, editCol, deleteCol);

        // Layout
        VBox centerLayout = new VBox(15, searchBox, table);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.TOP_CENTER);
        centerLayout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(centerLayout);
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
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(600);
        form.setMaxHeight(600);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showNhanVienForm(NhanVien nhanVien) {
        boolean isEditMode = nhanVien != null;
        VBox form = createCenteredForm(isEditMode ? "Sửa nhân viên " + nhanVien.getMaNhanVien() : "Thêm nhân viên mới");

        // Tự động sinh mã nhân viên từ DAO
        String maNhanVienValue;
        try {
            maNhanVienValue = isEditMode ? nhanVien.getMaNhanVien() : nhanVienDao.getNextMaNhanVien();
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể sinh mã nhân viên: " + e.getMessage());
            return;
        }
        Label lblMaNhanVien = new Label(maNhanVienValue);
        lblMaNhanVien.setStyle("-fx-font-size: 14px;");

        TextField tfTenNhanVien = new TextField(isEditMode ? nhanVien.getTenNhanVien() : "");
        tfTenNhanVien.setPromptText("Họ tên...");
        tfTenNhanVien.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfSoDienThoai = new TextField(isEditMode ? nhanVien.getSoDienThoai() : "");
        tfSoDienThoai.setPromptText("Số điện thoại...");
        tfSoDienThoai.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfDiaChi = new TextField(isEditMode ? nhanVien.getDiaChi() : "");
        tfDiaChi.setPromptText("Địa chỉ...");
        tfDiaChi.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        ChoiceBox<String> cbChucVu = new ChoiceBox<>();
        cbChucVu.getItems().addAll("Quản lý", "Lễ tân", "Phục vụ", "Bảo vệ");
        cbChucVu.setValue(isEditMode ? nhanVien.getChucVu() : "Lễ tân");
        cbChucVu.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        ChoiceBox<String> cbGioiTinh = new ChoiceBox<>();
        cbGioiTinh.getItems().addAll("Nam", "Nữ");
        cbGioiTinh.setValue(isEditMode ? (nhanVien.isGioiTinh() ? "Nam" : "Nữ") : "Nam");
        cbGioiTinh.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfLuong = new TextField(isEditMode ? String.valueOf(nhanVien.getLuong()) : "");
        tfLuong.setPromptText("Lương (VD: 5000000)...");
        tfLuong.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Mã NV:"), 0, 0); grid.add(lblMaNhanVien, 1, 0);
        grid.add(new Label("Họ Tên:"), 0, 1); grid.add(tfTenNhanVien, 1, 1);
        grid.add(new Label("SĐT:"), 0, 2); grid.add(tfSoDienThoai, 1, 2);
        grid.add(new Label("Địa Chỉ:"), 0, 3); grid.add(tfDiaChi, 1, 3);
        grid.add(new Label("Chức Vụ:"), 0, 4); grid.add(cbChucVu, 1, 4);
        grid.add(new Label("Giới Tính:"), 0, 5); grid.add(cbGioiTinh, 1, 5);
        grid.add(new Label("Lương:"), 0, 6); grid.add(tfLuong, 1, 6);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnLuu.setOnAction(e -> {
            String maNhanVien = maNhanVienValue;
            String tenNhanVien = tfTenNhanVien.getText();
            String soDienThoai = tfSoDienThoai.getText();
            String diaChi = tfDiaChi.getText();
            String chucVu = cbChucVu.getValue();
            boolean gioiTinh = cbGioiTinh.getValue().equals("Nam");
            String luongText = tfLuong.getText();

            // Validation
            if (tenNhanVien.isEmpty() || soDienThoai.isEmpty() || chucVu == null || luongText.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin (trừ địa chỉ)!");
                return;
            }

            double luong;
            try {
                luong = Double.parseDouble(luongText);
                if (luong < 0) {
                    showAlert("Lỗi", "Lương phải là số không âm!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Lương phải là số hợp lệ!");
                return;
            }

            NhanVien newNhanVien = new NhanVien(maNhanVien, tenNhanVien, soDienThoai, gioiTinh, diaChi, chucVu, luong);

            try {
                if (!isEditMode) {
                    if (nhanVienDao.themNhanVien(newNhanVien)) {
                        danhSachNhanVien.add(newNhanVien);
                        showAlert("Thành công", "Đã thêm nhân viên mới!");
                    } else {
                        showAlert("Lỗi", "Không thể thêm nhân viên!");
                        return;
                    }
                } else {
                    if (nhanVienDao.suaNhanVien(newNhanVien)) {
                        int index = danhSachNhanVien.indexOf(nhanVien);
                        danhSachNhanVien.set(index, newNhanVien);
                        table.refresh();
                        showAlert("Thành công", "Đã cập nhật thông tin nhân viên!");
                    } else {
                        showAlert("Lỗi", "Không thể cập nhật nhân viên!");
                        return;
                    }
                }
                contentPane.getChildren().setAll(mainPane);
            } catch (SQLException ex) {
                showAlert("Lỗi", "Đã xảy ra lỗi khi lưu dữ liệu: " + ex.getMessage());
            }
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(10, btnLuu, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public ObservableList<NhanVien> getDanhSachNhanVien() {
        return danhSachNhanVien;
    }
}