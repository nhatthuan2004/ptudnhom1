package UI;

import dao.KhuyenMai_Dao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.KhuyenMai;

import java.sql.SQLException;

public class QLKM {
    private final ObservableList<KhuyenMai> danhSachKhuyenMai;
    private TableView<KhuyenMai> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final DataManager dataManager;
    private final KhuyenMai_Dao khuyenMaiDao;

    public QLKM() {
        dataManager = DataManager.getInstance();
        this.khuyenMaiDao = new KhuyenMai_Dao();
        this.danhSachKhuyenMai = dataManager.getKhuyenMaiList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
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
        searchField.setPromptText("Tìm theo mã, tên khuyến mãi...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        // Add button
        Button addButton = new Button("+ Thêm khuyến mãi");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        addButton.setOnAction(e -> {
            try {
                showKhuyenMaiForm(null);
            } catch (SQLException e1) {
                showAlert("Lỗi", "Không thể mở form thêm khuyến mãi: " + e1.getMessage());
            }
        });

        HBox searchBox = new HBox(10, new Label("Tìm kiếm:"), searchField, searchButton, addButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                table.setItems(danhSachKhuyenMai);
            } else {
                ObservableList<KhuyenMai> filteredList = danhSachKhuyenMai.filtered(km ->
                        km.getMaChuongTrinhKhuyenMai().toLowerCase().contains(keyword) ||
                        km.getTenChuongTrinhKhuyenMai().toLowerCase().contains(keyword));
                table.setItems(filteredList);
            }
        });

        searchField.setOnAction(e -> searchButton.fire());

        // Promotion table
        table = new TableView<>(danhSachKhuyenMai);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<KhuyenMai, String> maKhuyenMaiCol = new TableColumn<>("Mã KM");
        maKhuyenMaiCol.setCellValueFactory(new PropertyValueFactory<>("maChuongTrinhKhuyenMai"));
        maKhuyenMaiCol.setMinWidth(100);

        TableColumn<KhuyenMai, String> tenKhuyenMaiCol = new TableColumn<>("Tên KM");
        tenKhuyenMaiCol.setCellValueFactory(new PropertyValueFactory<>("tenChuongTrinhKhuyenMai"));
        tenKhuyenMaiCol.setMinWidth(200);

        TableColumn<KhuyenMai, String> moTaCol = new TableColumn<>("Mô Tả");
        moTaCol.setCellValueFactory(new PropertyValueFactory<>("moTaChuongTrinhKhuyenMai"));
        moTaCol.setMinWidth(200);

        TableColumn<KhuyenMai, Double> chietKhauCol = new TableColumn<>("Giảm Giá (%)");
        chietKhauCol.setCellValueFactory(new PropertyValueFactory<>("chietKhau"));
        chietKhauCol.setMinWidth(120);

        TableColumn<KhuyenMai, Boolean> trangThaiCol = new TableColumn<>("Trạng Thái");
        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        trangThaiCol.setCellFactory(col -> new TableCell<KhuyenMai, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item ? "Hoạt động" : "Ngừng hoạt động");
            }
        });
        trangThaiCol.setMinWidth(120);

        TableColumn<KhuyenMai, Void> editCol = new TableColumn<>("Sửa");
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
                    btnEdit.setOnAction(e -> {
                        try {
                            showKhuyenMaiForm(getTableRow().getItem());
                        } catch (SQLException e1) {
                            showAlert("Lỗi", "Không thể mở form sửa khuyến mãi: " + e1.getMessage());
                        }
                    });
                }
            }
        });
        editCol.setMinWidth(100);

        TableColumn<KhuyenMai, Void> deleteCol = new TableColumn<>("Xóa");
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
                        KhuyenMai km = getTableRow().getItem();
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Xác nhận xóa");
                        confirm.setHeaderText("Bạn có chắc muốn xóa khuyến mãi này?");
                        confirm.setContentText("Khuyến mãi: " + km.getTenChuongTrinhKhuyenMai());
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    khuyenMaiDao.xoaKhuyenMai(km.getMaChuongTrinhKhuyenMai());
                                    danhSachKhuyenMai.remove(km);
                                    showAlert("Thành công", "Xóa khuyến mãi thành công!");
                                } catch (SQLException ex) {
                                    showAlert("Lỗi", "Không thể xóa khuyến mãi: " + ex.getMessage());
                                }
                            }
                        });
                    });
                }
            }
        });
        deleteCol.setMinWidth(100);

        table.getColumns().setAll(maKhuyenMaiCol, tenKhuyenMaiCol, moTaCol, chietKhauCol, trangThaiCol, editCol, deleteCol);

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

    private void showKhuyenMaiForm(KhuyenMai khuyenMai) throws SQLException {
        boolean isEditMode = khuyenMai != null;
        String maKhuyenMai = isEditMode ? khuyenMai.getMaChuongTrinhKhuyenMai() : khuyenMaiDao.getNextMaKhuyenMai();
        VBox form = createCenteredForm(isEditMode ? "Sửa khuyến mãi " + maKhuyenMai : "Thêm khuyến mãi");

        Label lblMaKhuyenMai = new Label(maKhuyenMai);
        lblMaKhuyenMai.setStyle("-fx-font-size: 14px;");

        TextField tfTenKhuyenMai = new TextField(isEditMode ? khuyenMai.getTenChuongTrinhKhuyenMai() : "");
        tfTenKhuyenMai.setPromptText("Tên khuyến mãi...");
        tfTenKhuyenMai.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfMoTa = new TextField(isEditMode ? khuyenMai.getMoTaChuongTrinhKhuyenMai() : "");
        tfMoTa.setPromptText("Mô tả...");
        tfMoTa.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfChietKhau = new TextField(isEditMode ? String.valueOf(khuyenMai.getChietKhau()) : "");
        tfChietKhau.setPromptText("Giảm giá (%)...");
        tfChietKhau.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");
        tfChietKhau.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tfChietKhau.setText(oldValue);
            }
        });

        CheckBox cbTrangThai = new CheckBox("Hoạt động");
        cbTrangThai.setSelected(isEditMode ? khuyenMai.isTrangThai() : true);
        cbTrangThai.setStyle("-fx-font-size: 14px;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Mã KM:"), 0, 0); grid.add(lblMaKhuyenMai, 1, 0);
        grid.add(new Label("Tên KM:"), 0, 1); grid.add(tfTenKhuyenMai, 1, 1);
        grid.add(new Label("Mô Tả:"), 0, 2); grid.add(tfMoTa, 1, 2);
        grid.add(new Label("Giảm Giá (%):"), 0, 3); grid.add(tfChietKhau, 1, 3);
        grid.add(new Label("Trạng Thái:"), 0, 4); grid.add(cbTrangThai, 1, 4);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnLuu.setOnAction(e -> {
            String tenKhuyenMai = tfTenKhuyenMai.getText().trim();
            String moTa = tfMoTa.getText().trim();
            String chietKhauText = tfChietKhau.getText().trim();
            boolean trangThai = cbTrangThai.isSelected();

            if (tenKhuyenMai.isEmpty()) {
                showAlert("Lỗi", "Tên khuyến mãi không được để trống!");
                return;
            }

            double chietKhau;
            try {
                chietKhau = Double.parseDouble(chietKhauText);
                if (chietKhau < 0 || chietKhau > 100) {
                    showAlert("Lỗi", "Giảm giá phải từ 0 đến 100%!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Giảm giá phải là số hợp lệ!");
                return;
            }

            try {
                KhuyenMai newKhuyenMai = new KhuyenMai(maKhuyenMai, tenKhuyenMai, chietKhau, moTa, trangThai);
                if (isEditMode) {
                    khuyenMaiDao.suaKhuyenMai(newKhuyenMai);
                    int index = danhSachKhuyenMai.indexOf(khuyenMai);
                    danhSachKhuyenMai.set(index, newKhuyenMai);
                    showAlert("Thành công", "Cập nhật khuyến mãi thành công!");
                } else {
                    khuyenMaiDao.themKhuyenMai(newKhuyenMai);
                    danhSachKhuyenMai.add(newKhuyenMai);
                    showAlert("Thành công", "Thêm khuyến mãi thành công!");
                }
                table.refresh();
                contentPane.getChildren().setAll(mainPane);
            } catch (SQLException ex) {
                showAlert("Lỗi", (isEditMode ? "Cập nhật" : "Thêm") + " khuyến mãi thất bại: " + ex.getMessage());
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

    public ObservableList<KhuyenMai> getActivePromotions() {
        return danhSachKhuyenMai.filtered(KhuyenMai::isTrangThai);
    }

    public ObservableList<KhuyenMai> getDanhSachKhuyenMai() {
        return danhSachKhuyenMai;
    }
}