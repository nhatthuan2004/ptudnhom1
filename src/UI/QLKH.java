package UI;

import dao.KhachHang_Dao;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.KhachHang;

import java.sql.SQLException;
import java.time.LocalDate;

public class QLKH {
    private final ObservableList<KhachHang> danhSachKhachHang;
    private TableView<KhachHang> table;
    private StackPane contentPane;
    private StackPane mainPane;
    private final KhachHang_Dao khachHangDao;

    public QLKH() {
        this.khachHangDao = new KhachHang_Dao();
        this.danhSachKhachHang = DataManager.getInstance().getKhachHangList();
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

        // Add button with new style
        Button addButton = new Button("+ Thêm khách hàng");
        addButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 6;");
        addButton.setOnAction(e -> {
            try {
                showKhachHangForm(null);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        // Place the add button in an HBox for alignment
        HBox topHeader = new HBox(addButton);
        topHeader.setAlignment(Pos.CENTER_LEFT);
        topHeader.setPadding(new Insets(10, 0, 10, 20));

        // Customer table
        table = new TableView<>(danhSachKhachHang);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<KhachHang, String> maKhachHangCol = new TableColumn<>("Mã KH");
        maKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        maKhachHangCol.setMinWidth(100);

        TableColumn<KhachHang, String> tenKhachHangCol = new TableColumn<>("Họ và Tên");
        tenKhachHangCol.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        tenKhachHangCol.setMinWidth(150);

        TableColumn<KhachHang, String> cccdCol = new TableColumn<>("CCCD");
        cccdCol.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        cccdCol.setMinWidth(120);

        TableColumn<KhachHang, String> soDienThoaiCol = new TableColumn<>("Số Điện Thoại");
        soDienThoaiCol.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        soDienThoaiCol.setMinWidth(120);

        TableColumn<KhachHang, String> diaChiCol = new TableColumn<>("Địa Chỉ");
        diaChiCol.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        diaChiCol.setMinWidth(200);

        TableColumn<KhachHang, String> quocTichCol = new TableColumn<>("Quốc Tịch");
        quocTichCol.setCellValueFactory(new PropertyValueFactory<>("quocTich"));
        quocTichCol.setMinWidth(100);

        TableColumn<KhachHang, String> gioiTinhCol = new TableColumn<>("Giới Tính");
        gioiTinhCol.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        gioiTinhCol.setMinWidth(100);

        TableColumn<KhachHang, LocalDate> ngaySinhCol = new TableColumn<>("Ngày Sinh");
        ngaySinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        ngaySinhCol.setMinWidth(120);

        TableColumn<KhachHang, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setMinWidth(150);

        TableColumn<KhachHang, Void> editCol = new TableColumn<>("Sửa");
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
                            showKhachHangForm(getTableRow().getItem());
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    });
                }
            }
        });
        editCol.setMinWidth(100);

        TableColumn<KhachHang, Void> deleteCol = new TableColumn<>("Xóa");
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
                        confirm.setHeaderText("Bạn có chắc muốn xóa khách hàng này?");
                        confirm.setContentText("Khách hàng: " + getTableRow().getItem().getTenKhachHang());
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                KhachHang kh = getTableRow().getItem();
                                try {
                                    khachHangDao.xoaKhachHang(kh.getMaKhachHang());
                                    danhSachKhachHang.remove(kh);
                                    showAlert("Thành công", "Xóa khách hàng thành công!");
                                } catch (Exception ex) {
                                    showAlert("Lỗi", "Không thể xóa khách hàng: " + ex.getMessage());
                                }
                            }
                        });
                    });
                }
            }
        });
        deleteCol.setMinWidth(100);

        table.getColumns().setAll(maKhachHangCol, tenKhachHangCol, cccdCol, soDienThoaiCol, diaChiCol, quocTichCol,
                gioiTinhCol, ngaySinhCol, emailCol, editCol, deleteCol);

        // Layout
        VBox centerLayout = new VBox(15, topHeader, table);
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

    private void showKhachHangForm(KhachHang khachHang) throws SQLException {
        boolean isEditMode = khachHang != null;
        VBox form = createCenteredForm(
                isEditMode ? "Sửa thông tin khách hàng " + khachHang.getMaKhachHang() : "Thêm khách hàng mới");

        String maKhachHangValue = isEditMode ? khachHang.getMaKhachHang() : khachHangDao.getNextMaKhachHang();
        Label lblMaKhachHang = new Label(maKhachHangValue);
        lblMaKhachHang.setStyle("-fx-font-size: 14px;");

        TextField tfTenKhachHang = new TextField(isEditMode ? khachHang.getTenKhachHang() : "");
        tfTenKhachHang.setPromptText("Họ và tên...");
        tfTenKhachHang.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfCccd = new TextField(isEditMode ? khachHang.getCccd() : "");
        tfCccd.setPromptText("CCCD (12 số)...");
        tfCccd.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfSoDienThoai = new TextField(isEditMode ? khachHang.getSoDienThoai() : "");
        tfSoDienThoai.setPromptText("Số điện thoại (10 số)...");
        tfSoDienThoai.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfDiaChi = new TextField(isEditMode ? khachHang.getDiaChi() : "");
        tfDiaChi.setPromptText("Địa chỉ...");
        tfDiaChi.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfQuocTich = new TextField(isEditMode ? khachHang.getQuocTich() : "");
        tfQuocTich.setPromptText("Quốc tịch...");
        tfQuocTich.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        DatePicker dpNgaySinh = new DatePicker(isEditMode ? khachHang.getNgaySinh() : null);
        dpNgaySinh.setPromptText("Ngày sinh (dd/MM/yyyy)");
        dpNgaySinh.setStyle("-fx-border-radius: 5; -fx-background-radius: 5;");

        ComboBox<String> cbGioiTinh = new ComboBox<>();
        cbGioiTinh.getItems().addAll("Nam", "Nữ", "Khác");
        cbGioiTinh.setPromptText("Chọn giới tính...");
        if (isEditMode) cbGioiTinh.setValue(khachHang.getGioiTinh());
        cbGioiTinh.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfEmail = new TextField(isEditMode ? khachHang.getEmail() : "");
        tfEmail.setPromptText("Email...");
        tfEmail.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Mã KH:"), 0, 0); grid.add(lblMaKhachHang, 1, 0);
        grid.add(new Label("Họ và Tên:"), 0, 1); grid.add(tfTenKhachHang, 1, 1);
        grid.add(new Label("CCCD:"), 0, 2); grid.add(tfCccd, 1, 2);
        grid.add(new Label("Số Điện Thoại:"), 0, 3); grid.add(tfSoDienThoai, 1, 3);
        grid.add(new Label("Địa Chỉ:"), 0, 4); grid.add(tfDiaChi, 1, 4);
        grid.add(new Label("Quốc Tịch:"), 0, 5); grid.add(tfQuocTich, 1, 5);
        grid.add(new Label("Ngày Sinh:"), 0, 6); grid.add(dpNgaySinh, 1, 6);
        grid.add(new Label("Giới Tính:"), 0, 7); grid.add(cbGioiTinh, 1, 7);
        grid.add(new Label("Email:"), 0, 8); grid.add(tfEmail, 1, 8);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnLuu.setOnAction(e -> {
            String maKhachHang = maKhachHangValue;
            String tenKhachHang = tfTenKhachHang.getText().trim();
            String cccd = tfCccd.getText().trim();
            String soDienThoai = tfSoDienThoai.getText().trim();
            String diaChi = tfDiaChi.getText().trim();
            String quocTich = tfQuocTich.getText().trim();
            LocalDate ngaySinh = dpNgaySinh.getValue();
            String gioiTinh = cbGioiTinh.getValue();
            String email = tfEmail.getText().trim();

            // In giá trị các trường để debug
            System.out.println("Mã KH: " + maKhachHang);
            System.out.println("Tên KH: '" + tenKhachHang + "'");
            System.out.println("CCCD: '" + cccd + "'");
            System.out.println("SĐT: '" + soDienThoai + "'");
            System.out.println("Địa chỉ: '" + diaChi + "'");
            System.out.println("Quốc tịch: '" + quocTich + "'");
            System.out.println("Ngày sinh: " + (ngaySinh != null ? ngaySinh.toString() : "null"));
            System.out.println("Giới tính: '" + (gioiTinh != null ? gioiTinh : "null") + "'");
            System.out.println("Email: '" + email + "'");

            // Kiểm tra từng trường và liệt kê trường trống
            StringBuilder errorMsg = new StringBuilder();
            if (tenKhachHang.isEmpty()) errorMsg.append("Tên khách hàng, ");
            if (cccd.isEmpty()) errorMsg.append("CCCD, ");
            if (soDienThoai.isEmpty()) errorMsg.append("Số điện thoại, ");
            if (diaChi.isEmpty()) errorMsg.append("Địa chỉ, ");
            if (quocTich.isEmpty()) errorMsg.append("Quốc tịch, ");
            if (ngaySinh == null) errorMsg.append("Ngày sinh, ");
            if (gioiTinh == null || gioiTinh.isEmpty()) errorMsg.append("Giới tính, ");
            if (email.isEmpty()) errorMsg.append("Email, ");

            if (errorMsg.length() > 0) {
                errorMsg.setLength(errorMsg.length() - 2); // Xóa dấu phẩy và khoảng trắng cuối
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin! Các trường sau bị trống: " + errorMsg.toString());
                return;
            }

            // Kiểm tra định dạng CCCD
            if (!cccd.matches("\\d{12}")) {
                showAlert("Lỗi", "CCCD phải là 12 chữ số!");
                return;
            }

            // Kiểm tra định dạng số điện thoại
            if (!soDienThoai.matches("0\\d{9}")) {
                showAlert("Lỗi", "Số điện thoại phải bắt đầu bằng 0 và gồm 10 chữ số!");
                return;
            }

            // Kiểm tra định dạng email
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showAlert("Lỗi", "Email không hợp lệ!");
                return;
            }

            // Kiểm tra ngày sinh
            if (ngaySinh.isAfter(LocalDate.now())) {
                showAlert("Lỗi", "Ngày sinh phải trước ngày hiện tại!");
                return;
            }

            try {
                KhachHang newKhachHang = new KhachHang(maKhachHang, tenKhachHang, soDienThoai, email, diaChi, cccd, ngaySinh, quocTich, gioiTinh);
                if (!isEditMode) {
                    khachHangDao.themKhachHang(newKhachHang);
                    danhSachKhachHang.add(newKhachHang);
                    showAlert("Thành công", "Thêm khách hàng thành công!");
                } else {
                    khachHangDao.suaKhachHang(newKhachHang);
                    int index = danhSachKhachHang.indexOf(khachHang);
                    danhSachKhachHang.set(index, newKhachHang);
                    table.refresh();
                    showAlert("Thành công", "Cập nhật khách hàng thành công!");
                }
                contentPane.getChildren().setAll(mainPane);
            } catch (Exception ex) {
                showAlert("Lỗi", (isEditMode ? "Cập nhật" : "Thêm") + " khách hàng thất bại: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(15, btnLuu, btnHuy);
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