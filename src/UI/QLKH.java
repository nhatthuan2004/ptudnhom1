package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.KhachHang;
import java.time.LocalDate;

import dao.KhachHang_Dao;

public class QLKH {
    private final ObservableList<KhachHang> danhSachKhachHang;
    private TableView<KhachHang> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final KhachHang_Dao dao;

    public QLKH() {
        this.dao = new KhachHang_Dao();
        this.danhSachKhachHang = FXCollections.observableArrayList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            danhSachKhachHang.clear();
            danhSachKhachHang.addAll(dao.getAllKhachHang());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu khách hàng: " + e.getMessage());
        }
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

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm khách hàng...");
        searchField.setPrefWidth(200);
        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            try {
                if (keyword.isEmpty()) {
                    loadDataFromDatabase();
                } else {
                    danhSachKhachHang.clear();
                    danhSachKhachHang.addAll(dao.timKiemKhachHang(keyword));
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tìm kiếm: " + ex.getMessage());
            }
        });

        Button addButton = new Button("+ Thêm khách hàng");
        addButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 6 12; -fx-background-radius: 6;");
        addButton.setOnAction(e -> showKhachHangForm(null));

        HBox topControls = new HBox(10, addButton, searchField, searchButton);
        topControls.setAlignment(Pos.CENTER_LEFT);
        topControls.setPadding(new Insets(0, 20, 10, 20));

        VBox topHeader = new VBox(topControls);
        topHeader.setSpacing(10);
        StackPane.setAlignment(topHeader, Pos.TOP_LEFT);

        table = new TableView<>(danhSachKhachHang);
        table.setPrefWidth(1120);
        table.setPrefHeight(740);

        TableColumn<KhachHang, String> maKhachHangCol = new TableColumn<>("Mã KH");
        maKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        maKhachHangCol.setPrefWidth(100);

        TableColumn<KhachHang, String> tenKhachHangCol = new TableColumn<>("Tên Khách Hàng");
        tenKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        tenKhachHangCol.setPrefWidth(150);

        TableColumn<KhachHang, String> cccdCol = new TableColumn<>("CCCD");
        cccdCol.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        cccdCol.setPrefWidth(120);

        TableColumn<KhachHang, String> soDienThoaiCol = new TableColumn<>("Số Điện Thoại");
        soDienThoaiCol.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        soDienThoaiCol.setPrefWidth(120);

        TableColumn<KhachHang, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(150);

        TableColumn<KhachHang, String> diaChiCol = new TableColumn<>("Địa Chỉ");
        diaChiCol.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        diaChiCol.setPrefWidth(150);

        TableColumn<KhachHang, String> quocTichCol = new TableColumn<>("Quốc Tịch");
        quocTichCol.setCellValueFactory(new PropertyValueFactory<>("quocTich"));
        quocTichCol.setPrefWidth(100);

        TableColumn<KhachHang, String> gioiTinhCol = new TableColumn<>("Giới Tính");
        gioiTinhCol.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        gioiTinhCol.setPrefWidth(80);

        TableColumn<KhachHang, LocalDate> ngaySinhCol = new TableColumn<>("Ngày Sinh");
        ngaySinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        ngaySinhCol.setPrefWidth(100);

        TableColumn<KhachHang, Void> editCol = new TableColumn<>("Sửa");
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
                    btnEdit.setOnAction(e -> showKhachHangForm(getTableRow().getItem()));
                }
            }
        });
        editCol.setPrefWidth(80);

        TableColumn<KhachHang, Void> deleteCol = new TableColumn<>("Xóa");
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
                        confirm.setHeaderText("Bạn có chắc muốn xóa khách hàng này?");
                        confirm.setContentText("Khách hàng: " + getTableRow().getItem().getTenKhachHang());
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                KhachHang kh = getTableRow().getItem();
                                try {
                                    if (dao.xoaKhachHang(kh.getMaKhachHang())) {
                                        danhSachKhachHang.remove(kh);
                                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa khách hàng thành công!");
                                    } else {
                                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa khách hàng!");
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
        deleteCol.setPrefWidth(80);

        table.getColumns().setAll(maKhachHangCol, tenKhachHangCol, cccdCol, soDienThoaiCol, emailCol, diaChiCol, quocTichCol,
                                  gioiTinhCol, ngaySinhCol, editCol, deleteCol);

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
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(500);
        form.setMaxHeight(500);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showKhachHangForm(KhachHang khachHang) {
        boolean isEditMode = khachHang != null;
        VBox form = createCenteredForm(
                isEditMode ? "Sửa thông tin khách hàng " + khachHang.getMaKhachHang() : "Thêm khách hàng mới");

        String maKhachHangValue;
        try {
            maKhachHangValue = isEditMode ? khachHang.getMaKhachHang() : dao.getNextMaKhachHang();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể sinh mã khách hàng: " + e.getMessage());
            return;
        }
        Label lblMaKhachHang = new Label(maKhachHangValue);
        lblMaKhachHang.setStyle("-fx-font-size: 14px;");

        TextField tfTenKhachHang = new TextField(isEditMode ? khachHang.getTenKhachHang() : "");
        tfTenKhachHang.setPromptText("Họ và tên...");

        TextField tfCccd = new TextField(isEditMode ? khachHang.getCccd() : "");
        tfCccd.setPromptText("CCCD (12 số)...");

        TextField tfSoDienThoai = new TextField(isEditMode ? khachHang.getSoDienThoai() : "");
        tfSoDienThoai.setPromptText("Số điện thoại (10 số)...");

        TextField tfEmail = new TextField(isEditMode ? khachHang.getEmail() : "");
        tfEmail.setPromptText("Email...");

        TextField tfDiaChi = new TextField(isEditMode ? khachHang.getDiaChi() : "");
        tfDiaChi.setPromptText("Địa chỉ...");

        TextField tfQuocTich = new TextField(isEditMode ? khachHang.getQuocTich() : "");
        tfQuocTich.setPromptText("Quốc tịch...");

        DatePicker dpNgaySinh = new DatePicker(isEditMode ? khachHang.getNgaySinh() : null);
        dpNgaySinh.setPromptText("Ngày sinh (dd/MM/yyyy)");

        ComboBox<String> cbGioiTinh = new ComboBox<>();
        cbGioiTinh.getItems().addAll("Nam", "Nữ", "Khác");
        cbGioiTinh.setPromptText("Chọn giới tính...");
        if (isEditMode) cbGioiTinh.setValue(khachHang.getGioiTinh());

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Mã KH:"), 0, 0); grid.add(lblMaKhachHang, 1, 0);
        grid.add(new Label("Họ và Tên:"), 0, 1); grid.add(tfTenKhachHang, 1, 1);
        grid.add(new Label("CCCD:"), 0, 2); grid.add(tfCccd, 1, 2);
        grid.add(new Label("Số Điện Thoại:"), 0, 3); grid.add(tfSoDienThoai, 1, 3);
        grid.add(new Label("Email:"), 0, 4); grid.add(tfEmail, 1, 4);
        grid.add(new Label("Địa Chỉ:"), 0, 5); grid.add(tfDiaChi, 1, 5);
        grid.add(new Label("Quốc Tịch:"), 0, 6); grid.add(tfQuocTich, 1, 6);
        grid.add(new Label("Ngày Sinh:"), 0, 7); grid.add(dpNgaySinh, 1, 7);
        grid.add(new Label("Giới Tính:"), 0, 8); grid.add(cbGioiTinh, 1, 8);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> {
            String maKhachHang = maKhachHangValue;
            String tenKhachHang = tfTenKhachHang.getText();
            String cccd = tfCccd.getText();
            String soDienThoai = tfSoDienThoai.getText();
            String email = tfEmail.getText();
            String diaChi = tfDiaChi.getText();
            String quocTich = tfQuocTich.getText();
            LocalDate ngaySinh = dpNgaySinh.getValue();
            String gioiTinh = cbGioiTinh.getValue();

            // Validation
            if (tenKhachHang.isEmpty() || cccd.isEmpty() || soDienThoai.isEmpty() || 
                diaChi.isEmpty() || quocTich.isEmpty() || ngaySinh == null || gioiTinh == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin (trừ email)!");
                return;
            }

            if (!cccd.matches("\\d{12}")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "CCCD phải là 12 chữ số!");
                return;
            }

            if (!soDienThoai.matches("0\\d{9}")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số điện thoại phải bắt đầu bằng 0 và gồm 10 chữ số!");
                return;
            }

            if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Email không hợp lệ!");
                return;
            }

            if (ngaySinh.isAfter(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Ngày sinh phải trước ngày hiện tại!");
                return;
            }

            KhachHang newKhachHang = new KhachHang(maKhachHang, tenKhachHang, soDienThoai, email, diaChi, cccd, ngaySinh, quocTich, gioiTinh);

            try {
                if (!isEditMode) {
                    if (dao.themKhachHang(newKhachHang)) {
                        danhSachKhachHang.add(newKhachHang);
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khách hàng mới!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm khách hàng!");
                        return;
                    }
                } else {
                    if (dao.suaKhachHang(newKhachHang)) {
                        int index = danhSachKhachHang.indexOf(khachHang);
                        danhSachKhachHang.set(index, newKhachHang);
                        table.refresh();
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin khách hàng!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật khách hàng!");
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
}