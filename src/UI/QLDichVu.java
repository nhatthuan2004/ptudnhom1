package UI;

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
    private final DataManager dataManager;

    public QLDichVu() {
        dataManager = DataManager.getInstance();
        this.danhSachDichVu = dataManager.getDichVuList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: #f0f0f0;");
        mainPane.setPrefSize(1120, 800);

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

        Button addButton = new Button("+ Thêm dịch vụ");
        addButton.setStyle(
            "-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 6 12; -fx-background-radius: 6;"
        );
        addButton.setOnAction(e -> showDichVuForm(null));

        HBox addBox = new HBox(addButton);
        addBox.setAlignment(Pos.CENTER_LEFT);
        addBox.setPadding(new Insets(0, 20, 10, 20));

        VBox topHeader = new VBox(addBox);
        topHeader.setSpacing(10);
        StackPane.setAlignment(topHeader, Pos.TOP_LEFT);

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
                                danhSachDichVu.remove(getTableRow().getItem());
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

        TextField tfMaDichVu = new TextField(isEditMode ? dichVu.getMaDichVu() : "");
        tfMaDichVu.setPromptText("Mã dịch vụ...");
        tfMaDichVu.setDisable(isEditMode);

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

        grid.add(new Label("Mã Dịch Vụ:"), 0, 0); grid.add(tfMaDichVu, 1, 0);
        grid.add(new Label("Tên Dịch Vụ:"), 0, 1); grid.add(tfTenDichVu, 1, 1);
        grid.add(new Label("Mô Tả:"), 0, 2); grid.add(tfMoTa, 1, 2);
        grid.add(new Label("Giá:"), 0, 3); grid.add(tfGia, 1, 3);
        grid.add(new Label("Trạng Thái:"), 0, 4); grid.add(cbTrangThai, 1, 4);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> {
            String maDichVu = tfMaDichVu.getText();
            String tenDichVu = tfTenDichVu.getText();
            String moTa = tfMoTa.getText();
            String giaText = tfGia.getText();
            String trangThai = cbTrangThai.getValue();

            if (maDichVu.isEmpty() || tenDichVu.isEmpty() || giaText.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            double gia;
            try {
                gia = Double.parseDouble(giaText);
                if (gia < 0) {
                    showAlert("Lỗi", "Giá phải là số không âm!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Giá phải là số hợp lệ!");
                return;
            }

            DichVu newDichVu = new DichVu(maDichVu, tenDichVu, gia, moTa, trangThai);

            if (!isEditMode) {
                if (danhSachDichVu.stream().anyMatch(dv -> dv.getMaDichVu().equals(maDichVu))) {
                    showAlert("Lỗi", "Mã dịch vụ " + maDichVu + " đã tồn tại!");
                    return;
                }
                dataManager.addDichVu(newDichVu);
            } else {
                int index = danhSachDichVu.indexOf(dichVu);
                danhSachDichVu.set(index, newDichVu);
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
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public ObservableList<DichVu> getDanhSachDichVu() {
        return danhSachDichVu;
    }
}