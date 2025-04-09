package UI;

import dao.DichVu_Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.DichVu;

public class QLDichVu {
    private final ObservableList<DichVu> danhSachDichVu;
    private TableView<DichVu> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final DichVu_Dao dichVuDao;

    public QLDichVu() {
        this.dichVuDao = new DichVu_Dao();
        this.danhSachDichVu = FXCollections.observableArrayList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            danhSachDichVu.clear();
            danhSachDichVu.addAll(dichVuDao.getAllDichVu());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu dịch vụ: " + e.getMessage());
        }
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: #f0f0f0;");
        mainPane.setPrefSize(1120, 800);

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
        StackPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        // Thanh tìm kiếm
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm dịch vụ...");
        searchField.setPrefWidth(200);
        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            try {
                if (keyword.isEmpty()) {
                    loadDataFromDatabase();
                } else {
                    danhSachDichVu.clear();
                    danhSachDichVu.addAll(dichVuDao.timKiemDichVu(keyword));
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tìm kiếm: " + ex.getMessage());
            }
        });

        // Nút thêm dịch vụ
        Button addButton = new Button("+ Thêm dịch vụ");
        addButton.setStyle(
            "-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 6 12; -fx-background-radius: 6;"
        );
        addButton.setOnAction(e -> showDichVuForm(null));

        HBox topControls = new HBox(10, addButton, searchField, searchButton);
        topControls.setAlignment(Pos.CENTER_LEFT);
        topControls.setPadding(new Insets(0, 20, 10, 20));

        VBox topHeader = new VBox(topControls);
        topHeader.setSpacing(10);
        StackPane.setAlignment(topHeader, Pos.TOP_LEFT);

        // Bảng dịch vụ
        table = new TableView<>(danhSachDichVu);
        table.setPrefWidth(1120);
        table.setPrefHeight(740);

        TableColumn<DichVu, String> maDichVuCol = new TableColumn<>("Mã Dịch Vụ");
        maDichVuCol.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));
        maDichVuCol.setPrefWidth(150);

        TableColumn<DichVu, String> tenDichVuCol = new TableColumn<>("Tên Dịch Vụ");
        tenDichVuCol.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        tenDichVuCol.setPrefWidth(200);

        TableColumn<DichVu, String> moTaCol = new TableColumn<>("Mô Tả");
        moTaCol.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        moTaCol.setPrefWidth(300);

        TableColumn<DichVu, Double> giaCol = new TableColumn<>("Giá (VNĐ)");
        giaCol.setCellValueFactory(new PropertyValueFactory<>("gia"));
        giaCol.setPrefWidth(150);

        TableColumn<DichVu, String> trangThaiCol = new TableColumn<>("Trạng Thái");
        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        trangThaiCol.setPrefWidth(100);

        TableColumn<DichVu, Void> editCol = new TableColumn<>("Sửa");
        editCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            {
                btnEdit.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEdit);
                    btnEdit.setOnAction(e -> showDichVuForm(getTableRow().getItem()));
                }
            }
        });
        editCol.setPrefWidth(100);

        TableColumn<DichVu, Void> deleteCol = new TableColumn<>("Xóa");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("Xóa");
            {
                btnDelete.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");
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
                        confirm.setHeaderText("Bạn có chắc muốn xóa dịch vụ này?");
                        confirm.setContentText("Dịch vụ: " + getTableRow().getItem().getTenDichVu());
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                DichVu dv = getTableRow().getItem();
                                try {
                                    if (dichVuDao.xoaDichVu(dv.getMaDichVu())) {
                                        danhSachDichVu.remove(dv);
                                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa dịch vụ thành công!");
                                    } else {
                                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa dịch vụ!");
                                    }
                                } catch (Exception ex) {
                                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xóa: " + ex.getMessage());
                                }
                            }
                        });
                    });
                }
            }
        });
        deleteCol.setPrefWidth(100);

        table.getColumns().setAll(maDichVuCol, tenDichVuCol, moTaCol, giaCol, trangThaiCol, editCol, deleteCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
        form.setMaxHeight(400);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showDichVuForm(DichVu dichVu) {
        boolean isEditMode = dichVu != null;
        VBox form = createCenteredForm(isEditMode ? "Sửa dịch vụ " + dichVu.getMaDichVu() : "Thêm dịch vụ mới");

        // Tự động sinh mã dịch vụ từ DAO
        String maDichVuValue;
        try {
            maDichVuValue = isEditMode ? dichVu.getMaDichVu() : dichVuDao.getNextMaDichVu();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể sinh mã dịch vụ: " + e.getMessage());
            return;
        }
        Label lblMaDichVu = new Label(maDichVuValue);
        lblMaDichVu.setStyle("-fx-font-size: 14px;");

        TextField tfTenDichVu = new TextField(isEditMode ? dichVu.getTenDichVu() : "");
        tfTenDichVu.setPromptText("Tên dịch vụ...");

        TextField tfMoTa = new TextField(isEditMode ? dichVu.getMoTa() : "");
        tfMoTa.setPromptText("Mô tả...");

        TextField tfGia = new TextField(isEditMode ? String.valueOf(dichVu.getGia()) : "");
        tfGia.setPromptText("Giá (VD: 50000)...");

        ChoiceBox<String> cbTrangThai = new ChoiceBox<>();
        cbTrangThai.getItems().addAll("Hoạt động", "Ngừng hoạt động");
        cbTrangThai.setValue(isEditMode ? dichVu.getTrangThai() : "Hoạt động");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Mã Dịch Vụ:"), 0, 0); grid.add(lblMaDichVu, 1, 0);
        grid.add(new Label("Tên Dịch Vụ:"), 0, 1); grid.add(tfTenDichVu, 1, 1);
        grid.add(new Label("Mô Tả:"), 0, 2); grid.add(tfMoTa, 1, 2);
        grid.add(new Label("Giá:"), 0, 3); grid.add(tfGia, 1, 3);
        grid.add(new Label("Trạng Thái:"), 0, 4); grid.add(cbTrangThai, 1, 4);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> {
            String maDichVu = maDichVuValue;
            String tenDichVu = tfTenDichVu.getText();
            String moTa = tfMoTa.getText();
            String giaText = tfGia.getText();
            String trangThai = cbTrangThai.getValue();

            // Validation
            if (tenDichVu.isEmpty() || giaText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin (trừ mô tả)!");
                return;
            }

            double gia;
            try {
                gia = Double.parseDouble(giaText);
                if (gia < 0) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá phải là số không âm!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Giá phải là số hợp lệ!");
                return;
            }

            DichVu newDichVu = new DichVu(maDichVu, tenDichVu, moTa, gia, trangThai);

            try {
                if (!isEditMode) {
                    if (dichVuDao.themDichVu(newDichVu)) {
                        danhSachDichVu.add(newDichVu);
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm dịch vụ mới!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm dịch vụ!");
                        return;
                    }
                } else {
                    if (dichVuDao.suaDichVu(newDichVu)) {
                        int index = danhSachDichVu.indexOf(dichVu);
                        danhSachDichVu.set(index, newDichVu);
                        table.refresh();
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin dịch vụ!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật dịch vụ!");
                        return;
                    }
                }
                contentPane.getChildren().setAll(mainPane);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi khi lưu dữ liệu: " + ex.getMessage());
            }
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(10, btnLuu, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public ObservableList<DichVu> getDanhSachDichVu() {
        return danhSachDichVu;
    }
}