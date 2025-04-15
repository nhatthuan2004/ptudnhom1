package UI;

import dao.NhanVien_Dao;
import dao.TaiKhoan_Dao;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.NhanVien;
import model.TaiKhoan;

import java.sql.SQLException;
import java.util.List;

public class QLTaiKhoanUI {
    private final ObservableList<NhanVien> danhSachNhanVien;
    private TableView<NhanVien> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final DataManager dataManager;
    private final NhanVien_Dao nhanVienDao;
    private final TaiKhoan_Dao taiKhoanDao;

    public QLTaiKhoanUI() {
        dataManager = DataManager.getInstance();
        nhanVienDao = new NhanVien_Dao();
        taiKhoanDao = new TaiKhoan_Dao();
        this.danhSachNhanVien = dataManager.getNhanVienList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
        this.contentPane.getChildren().add(mainPane);
        dataManager.addNhanVienListChangeListener(this::refreshTable);
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

        Button addButton = new Button("+ Thêm tài khoản");
        addButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 6;");
        addButton.setOnAction(e -> showTaiKhoanForm(null));

        HBox topHeader = new HBox(addButton);
        topHeader.setAlignment(Pos.CENTER_LEFT);
        topHeader.setPadding(new Insets(10, 0, 10, 20));
        StackPane.setAlignment(topHeader, Pos.TOP_LEFT);

        table = new TableView<>(danhSachNhanVien);
        table.setPrefWidth(1120);
        table.setPrefHeight(740);

        TableColumn<NhanVien, String> maNhanVienCol = new TableColumn<>("Mã NV");
        maNhanVienCol.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        maNhanVienCol.setPrefWidth(150);

        TableColumn<NhanVien, String> tenNhanVienCol = new TableColumn<>("Họ Tên");
        tenNhanVienCol.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        tenNhanVienCol.setPrefWidth(200);

        TableColumn<NhanVien, String> taiKhoanCol = new TableColumn<>("Tài Khoản");
        taiKhoanCol.setCellValueFactory(cellData -> {
            try {
                List<TaiKhoan> taiKhoans = taiKhoanDao.timKiemTaiKhoan(cellData.getValue().getMaNhanVien());
                return taiKhoans.isEmpty() ? new javafx.beans.property.SimpleStringProperty("") 
                                          : new javafx.beans.property.SimpleStringProperty(taiKhoans.get(0).getTenDangNhap());
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể lấy tài khoản: " + e.getMessage());
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        taiKhoanCol.setPrefWidth(200);

        TableColumn<NhanVien, String> matKhauCol = new TableColumn<>("Mật Khẩu");
        matKhauCol.setCellValueFactory(cellData -> {
            try {
                List<TaiKhoan> taiKhoans = taiKhoanDao.timKiemTaiKhoan(cellData.getValue().getMaNhanVien());
                return taiKhoans.isEmpty() ? new javafx.beans.property.SimpleStringProperty("") 
                                          : new javafx.beans.property.SimpleStringProperty(taiKhoans.get(0).getMatKhau());
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể lấy mật khẩu: " + e.getMessage());
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        matKhauCol.setPrefWidth(200);

        TableColumn<NhanVien, Void> editCol = new TableColumn<>("Đổi MK");
        editCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Đổi MK");
            {
                btnEdit.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    NhanVien nv = getTableRow().getItem();
                    try {
                        List<TaiKhoan> taiKhoans = taiKhoanDao.timKiemTaiKhoan(nv.getMaNhanVien());
                        btnEdit.setDisable(taiKhoans.isEmpty());
                        btnEdit.setOnAction(e -> showTaiKhoanForm(nv));
                        setGraphic(btnEdit);
                    } catch (SQLException e) {
                        showAlert("Lỗi", "Không thể kiểm tra tài khoản: " + e.getMessage());
                        setGraphic(null);
                    }
                }
            }
        });
        editCol.setPrefWidth(100);

        TableColumn<NhanVien, Void> deleteCol = new TableColumn<>("Xóa");
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
                    NhanVien nv = getTableRow().getItem();
                    try {
                        List<TaiKhoan> taiKhoans = taiKhoanDao.timKiemTaiKhoan(nv.getMaNhanVien());
                        btnDelete.setDisable(taiKhoans.isEmpty());
                        btnDelete.setOnAction(e -> {
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                            confirm.setTitle("Xác nhận xóa");
                            confirm.setHeaderText("Bạn có chắc muốn xóa tài khoản này?");
                            confirm.setContentText("Nhân viên: " + nv.getTenNhanVien());
                            confirm.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    try {
                                        if (taiKhoanDao.xoaTaiKhoan(taiKhoans.get(0).getTenDangNhap())) {
                                            showAlert("Thành công", "Xóa tài khoản thành công!");
                                            table.refresh();
                                        } else {
                                            showAlert("Lỗi", "Không thể xóa tài khoản!");
                                        }
                                    } catch (SQLException ex) {
                                        showAlert("Lỗi", "Lỗi khi xóa tài khoản: " + ex.getMessage());
                                    }
                                }
                            });
                        });
                        setGraphic(btnDelete);
                    } catch (SQLException e) {
                        showAlert("Lỗi", "Không thể kiểm tra tài khoản: " + e.getMessage());
                        setGraphic(null);
                    }
                }
            }
        });
        deleteCol.setPrefWidth(100);

        table.getColumns().setAll(maNhanVienCol, tenNhanVienCol, taiKhoanCol, matKhauCol, editCol, deleteCol);

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

    private void refreshTable() {
        table.refresh();
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle(
            "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
            "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        form.setMaxWidth(400);
        form.setMaxHeight(300);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showTaiKhoanForm(NhanVien nhanVien) {
        boolean isEditMode = nhanVien != null;
        String title = isEditMode ? "Đổi mật khẩu " + nhanVien.getMaNhanVien() : "Thêm tài khoản mới";
        VBox form = createCenteredForm(title);

        TextField tfMaNhanVien = new TextField(isEditMode ? nhanVien.getMaNhanVien() : "");
        tfMaNhanVien.setPromptText("Mã nhân viên...");
        tfMaNhanVien.setDisable(isEditMode);

        TextField tfTaiKhoan = new TextField();
        tfTaiKhoan.setPromptText("Tài khoản...");
        tfTaiKhoan.setDisable(isEditMode);
        if (isEditMode) {
            try {
                List<TaiKhoan> taiKhoans = taiKhoanDao.timKiemTaiKhoan(nhanVien.getMaNhanVien());
                if (!taiKhoans.isEmpty()) {
                    tfTaiKhoan.setText(taiKhoans.get(0).getTenDangNhap());
                }
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể lấy tài khoản: " + e.getMessage());
            }
        }

        PasswordField pfMatKhau = new PasswordField();
        pfMatKhau.setPromptText("Mật khẩu mới...");
        if (isEditMode) {
            try {
                List<TaiKhoan> taiKhoans = taiKhoanDao.timKiemTaiKhoan(nhanVien.getMaNhanVien());
                if (!taiKhoans.isEmpty()) {
                    pfMatKhau.setText(taiKhoans.get(0).getMatKhau());
                }
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể lấy mật khẩu: " + e.getMessage());
            }
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Mã NV:"), 0, 0); grid.add(tfMaNhanVien, 1, 0);
        grid.add(new Label("Tài Khoản:"), 0, 1); grid.add(tfTaiKhoan, 1, 1);
        grid.add(new Label("Mật Khẩu:"), 0, 2); grid.add(pfMatKhau, 1, 2);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> {
            String maNhanVien = tfMaNhanVien.getText();
            String taiKhoan = tfTaiKhoan.getText();
            String matKhau = pfMatKhau.getText();

            if (maNhanVien.isEmpty() || taiKhoan.isEmpty() || matKhau.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            try {
                if (!isEditMode) {
                    // Kiểm tra mã nhân viên tồn tại
                    NhanVien existingNhanVien = nhanVienDao.getNhanVienByMa(maNhanVien);
                    if (existingNhanVien == null) {
                        showAlert("Lỗi", "Mã nhân viên " + maNhanVien + " không tồn tại!");
                        return;
                    }
                    // Kiểm tra tài khoản đã tồn tại
                    List<TaiKhoan> existingTaiKhoans = taiKhoanDao.timKiemTaiKhoan(taiKhoan);
                    if (!existingTaiKhoans.isEmpty()) {
                        showAlert("Lỗi", "Tài khoản " + taiKhoan + " đã tồn tại!");
                        return;
                    }
                    // Thêm tài khoản
                    TaiKhoan newTaiKhoan = new TaiKhoan(taiKhoan, matKhau, maNhanVien);
                    if (taiKhoanDao.themTaiKhoan(newTaiKhoan)) {
                        showAlert("Thành công", "Thêm tài khoản thành công!");
                        contentPane.getChildren().setAll(mainPane);
                        table.refresh();
                    } else {
                        showAlert("Lỗi", "Không thể thêm tài khoản!");
                    }
                } else {
                    // Sửa tài khoản
                    TaiKhoan updatedTaiKhoan = new TaiKhoan(taiKhoan, matKhau, maNhanVien);
                    if (taiKhoanDao.suaTaiKhoan(updatedTaiKhoan)) {
                        showAlert("Thành công", "Cập nhật mật khẩu thành công!");
                        contentPane.getChildren().setAll(mainPane);
                        table.refresh();
                    } else {
                        showAlert("Lỗi", "Không thể cập nhật mật khẩu!");
                    }
                }
            } catch (SQLException ex) {
                showAlert("Lỗi", "Lỗi khi xử lý tài khoản: " + ex.getMessage());
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}