package UI;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.KhuyenMai;

public class QLKM {
    private final ObservableList<KhuyenMai> danhSachKhuyenMai;
    private TableView<KhuyenMai> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final DataManager dataManager;

    public QLKM() {
        dataManager = DataManager.getInstance();
        this.danhSachKhuyenMai = dataManager.getKhuyenMaiList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
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

        // Nút thêm khuyến mãi
        Button addButton = new Button("+ Thêm khuyến mãi");
        addButton.setStyle(
            "-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 6 12; -fx-background-radius: 6;"
        );
        addButton.setOnAction(e -> showKhuyenMaiForm(null));

        HBox addBox = new HBox(addButton);
        addBox.setAlignment(Pos.CENTER_LEFT);
        addBox.setPadding(new Insets(0, 20, 10, 20));

        VBox topHeader = new VBox(addBox);
        topHeader.setSpacing(10);
        StackPane.setAlignment(topHeader, Pos.TOP_LEFT);

        // Bảng khuyến mãi
        table = new TableView<>(danhSachKhuyenMai);
        table.setPrefWidth(1120);
        table.setPrefHeight(740);

        TableColumn<KhuyenMai, String> maKhuyenMaiCol = new TableColumn<>("Mã KM");
        maKhuyenMaiCol.setCellValueFactory(new PropertyValueFactory<>("maChuongTrinhKhuyenMai"));
        maKhuyenMaiCol.setPrefWidth(100);

        TableColumn<KhuyenMai, String> tenKhuyenMaiCol = new TableColumn<>("Tên KM");
        tenKhuyenMaiCol.setCellValueFactory(new PropertyValueFactory<>("tenChuongTrinhKhuyenMai"));
        tenKhuyenMaiCol.setPrefWidth(200);

        TableColumn<KhuyenMai, String> moTaCol = new TableColumn<>("Mô Tả");
        moTaCol.setCellValueFactory(new PropertyValueFactory<>("moTaChuongTrinhKhuyenMai"));
        moTaCol.setPrefWidth(300);

        TableColumn<KhuyenMai, Double> chietKhauCol = new TableColumn<>("Giảm Giá (%)");
        chietKhauCol.setCellValueFactory(new PropertyValueFactory<>("chietKhau"));
        chietKhauCol.setPrefWidth(100);

        TableColumn<KhuyenMai, Boolean> trangThaiCol = new TableColumn<>("Trạng Thái");
        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        trangThaiCol.setCellFactory(col -> new TableCell<KhuyenMai, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Hoạt động" : "Ngừng hoạt động");
                }
            }
        });
        trangThaiCol.setPrefWidth(120);

        TableColumn<KhuyenMai, Void> editCol = new TableColumn<>("Sửa");
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
                    btnEdit.setOnAction(e -> showKhuyenMaiForm(getTableRow().getItem()));
                }
            }
        });
        editCol.setPrefWidth(100);

        TableColumn<KhuyenMai, Void> deleteCol = new TableColumn<>("Xóa");
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
                        confirm.setHeaderText("Bạn có chắc muốn xóa khuyến mãi này?");
                        confirm.setContentText("Khuyến mãi: " + getTableRow().getItem().getTenChuongTrinhKhuyenMai());
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                danhSachKhuyenMai.remove(getTableRow().getItem());
                            }
                        });
                    });
                }
            }
        });
        deleteCol.setPrefWidth(100);

        table.getColumns().setAll(maKhuyenMaiCol, tenKhuyenMaiCol, moTaCol, chietKhauCol, trangThaiCol, editCol, deleteCol);

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

    private void showKhuyenMaiForm(KhuyenMai khuyenMai) {
        boolean isEditMode = khuyenMai != null;
        VBox form = createCenteredForm(isEditMode ? "Sửa thông tin khuyến mãi " + khuyenMai.getMaChuongTrinhKhuyenMai() : "Thêm khuyến mãi");

        TextField tfMaKhuyenMai = new TextField(isEditMode ? khuyenMai.getMaChuongTrinhKhuyenMai() : "");
        tfMaKhuyenMai.setPromptText("Mã khuyến mãi...");
        tfMaKhuyenMai.setDisable(isEditMode);

        TextField tfTenKhuyenMai = new TextField(isEditMode ? khuyenMai.getTenChuongTrinhKhuyenMai() : "");
        tfTenKhuyenMai.setPromptText("Tên khuyến mãi...");

        TextField tfMoTa = new TextField(isEditMode ? khuyenMai.getMoTaChuongTrinhKhuyenMai() : "");
        tfMoTa.setPromptText("Mô tả...");

        TextField tfChietKhau = new TextField(isEditMode ? String.valueOf(khuyenMai.getChietKhau()) : "");
        tfChietKhau.setPromptText("Giảm giá (%)...");

        CheckBox cbTrangThai = new CheckBox("Hoạt động");
        if (isEditMode) cbTrangThai.setSelected(khuyenMai.isTrangThai());
        else cbTrangThai.setSelected(true);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Mã KM:"), 0, 0); grid.add(tfMaKhuyenMai, 1, 0);
        grid.add(new Label("Tên KM:"), 0, 1); grid.add(tfTenKhuyenMai, 1, 1);
        grid.add(new Label("Mô Tả:"), 0, 2); grid.add(tfMoTa, 1, 2);
        grid.add(new Label("Giảm Giá (%):"), 0, 3); grid.add(tfChietKhau, 1, 3);
        grid.add(new Label("Trạng Thái:"), 0, 4); grid.add(cbTrangThai, 1, 4);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> {
            String maKhuyenMai = tfMaKhuyenMai.getText();
            String tenKhuyenMai = tfTenKhuyenMai.getText();
            String moTa = tfMoTa.getText();
            String chietKhauText = tfChietKhau.getText();
            boolean trangThai = cbTrangThai.isSelected();

            if (maKhuyenMai.isEmpty() || tenKhuyenMai.isEmpty() || moTa.isEmpty() || chietKhauText.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
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

            KhuyenMai newKhuyenMai = isEditMode ? khuyenMai : new KhuyenMai(chietKhauText, chietKhauText, chietKhau, trangThai);
            newKhuyenMai.setMaChuongTrinhKhuyenMai(maKhuyenMai);
            newKhuyenMai.setTenChuongTrinhKhuyenMai(tenKhuyenMai);
            newKhuyenMai.setMoTaChuongTrinhKhuyenMai(moTa);
            newKhuyenMai.setChietKhau(chietKhau);
            newKhuyenMai.setTrangThai(trangThai);

            if (!isEditMode) {
                if (danhSachKhuyenMai.stream().anyMatch(km -> km.getMaChuongTrinhKhuyenMai().equals(maKhuyenMai))) {
                    showAlert("Lỗi", "Mã khuyến mãi " + maKhuyenMai + " đã tồn tại!");
                    return;
                }
                danhSachKhuyenMai.add(newKhuyenMai);
            } else {
                int index = danhSachKhuyenMai.indexOf(khuyenMai);
                danhSachKhuyenMai.set(index, newKhuyenMai);
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

    public ObservableList<KhuyenMai> getActivePromotions() {
        return danhSachKhuyenMai.filtered(km -> km.isTrangThai());
    }

    public ObservableList<KhuyenMai> getDanhSachKhuyenMai() {
        return danhSachKhuyenMai;
    }
}