package UI;

import dao.KhachHang_Dao;
import dao.Phong_Dao;
import dao.PhieuDatPhong_Dao;
import dao.ChitietPhieuDatPhong_Dao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.ChitietPhieuDatPhong;
import model.KhachHang;
import model.PhieuDatPhong;
import model.Phong;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import connectDB.ConnectDB;

public class DatphongUI {
    private TableView<Phong> tablePhongTrong;
    private TableView<Phong> tablePhongDaChon;
    private ObservableList<Phong> phongTrongList;
    private ObservableList<Phong> phongDaChonList;
    private ObservableList<Phong> allPhongList;
    private final Phong_Dao phongDao;
    private final KhachHang_Dao khachHangDao;
    private final PhieuDatPhong_Dao phieuDatPhongDao;
    private final ChitietPhieuDatPhong_Dao chitietPhieuDatPhongDao;

    private TextField tfHoTen, tfCCCD, tfDiaChi, tfSDT, tfQuocTich, tfEmail, tfSoLuongNguoi;
    private ComboBox<String> cbGioiTinh;
    private DatePicker dpNgaySinh;

    public DatphongUI() {
        try {
            phongDao = new Phong_Dao();
            khachHangDao = new KhachHang_Dao();
            phieuDatPhongDao = new PhieuDatPhong_Dao();
            chitietPhieuDatPhongDao = new ChitietPhieuDatPhong_Dao();
            allPhongList = FXCollections.observableArrayList(phongDao.getAllPhong());
        } catch (Exception e) {
            throw new RuntimeException("Không thể kết nối tới cơ sở dữ liệu!", e);
        }
        DataManager dataManager = DataManager.getInstance();
        dataManager.setPhongList(allPhongList);
        phongTrongList = FXCollections.observableArrayList();
        phongDaChonList = FXCollections.observableArrayList();

        updatePhongTrongList();

        dataManager.addPhongListChangeListener(this::updatePhongTrongList);
    }

    public StackPane getUI() {
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
        userInfoBox.setMaxSize(200, 50);
        layout.setTop(userInfoBox);
        BorderPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        BorderPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        VBox customerInfo = new VBox(20);
        customerInfo.setPadding(new Insets(20));
        customerInfo.setAlignment(Pos.TOP_LEFT);

        HBox hboxHoTen = createInputFieldWithoutIcon("Nhập Họ Tên");
        tfHoTen = (TextField) hboxHoTen.getChildren().get(0);

        HBox hboxCCCD = createInputFieldWithoutIcon("Nhập CCCD/passport");
        tfCCCD = (TextField) hboxCCCD.getChildren().get(0);

        HBox hboxDiaChi = createInputFieldWithoutIcon("Nhập Địa Chỉ");
        tfDiaChi = (TextField) hboxDiaChi.getChildren().get(0);

        HBox hboxSDT = createInputFieldWithoutIcon("Nhập SĐT");
        tfSDT = (TextField) hboxSDT.getChildren().get(0);

        HBox hboxQuocTich = createInputFieldWithoutIcon("Nhập Quốc Tịch");
        tfQuocTich = (TextField) hboxQuocTich.getChildren().get(0);

        HBox hboxEmail = createInputFieldWithoutIcon("Nhập Email");
        tfEmail = (TextField) hboxEmail.getChildren().get(0);

        HBox hboxSoLuongNguoi = createInputFieldWithoutIcon("Nhập Số Lượng Người");
        tfSoLuongNguoi = (TextField) hboxSoLuongNguoi.getChildren().get(0);
        tfSoLuongNguoi.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfSoLuongNguoi.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        HBox hboxGioiTinh = new HBox(10);
        hboxGioiTinh.setAlignment(Pos.CENTER_LEFT);
        cbGioiTinh = new ComboBox<>(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        cbGioiTinh.setPromptText("Chọn Giới Tính");
        cbGioiTinh.setPrefWidth(250);
        hboxGioiTinh.getChildren().add(cbGioiTinh);

        HBox hboxNgaySinh = new HBox(10);
        hboxNgaySinh.setAlignment(Pos.CENTER_LEFT);
        dpNgaySinh = new DatePicker();
        dpNgaySinh.setPromptText("Chọn Ngày Sinh");
        dpNgaySinh.setPrefWidth(250);
        hboxNgaySinh.getChildren().add(dpNgaySinh);

        customerInfo.getChildren().addAll(hboxHoTen, hboxCCCD, hboxDiaChi, hboxSDT, hboxQuocTich, hboxEmail, hboxSoLuongNguoi, hboxGioiTinh, hboxNgaySinh);

        tfSDT.textProperty().addListener((obs, oldValue, newValue) -> checkExistingCustomerBySDT(newValue));
        tfCCCD.textProperty().addListener((obs, oldValue, newValue) -> checkExistingCustomerByCCCD(newValue));

        VBox roomInfo = new VBox(15);
        roomInfo.setPadding(new Insets(25));
        roomInfo.setAlignment(Pos.TOP_LEFT);

        GridPane dateTimeLayout = new GridPane();
        dateTimeLayout.setHgap(15);
        dateTimeLayout.setVgap(15);
        dateTimeLayout.setPadding(new Insets(0, 0, 15, 0));

        Label labelNgayDen = new Label("Ngày đến");
        DatePicker dpNgayDen = new DatePicker(LocalDate.now());

        Label labelNgayDi = new Label("Ngày đi");
        DatePicker dpNgayDi = new DatePicker(LocalDate.now().plusDays(1));

        Label labelGioDen = new Label("Giờ đến");
        ComboBox<String> cbGioDen = new ComboBox<>();
        cbGioDen.getItems().addAll("08:00 AM", "12:00 PM", "02:00 PM", "06:00 PM");
        cbGioDen.setValue("12:00 PM");

        Label labelGioDi = new Label("Giờ đi");
        ComboBox<String> cbGioDi = new ComboBox<>();
        cbGioDi.getItems().addAll("08:00 AM", "12:00 PM", "02:00 PM", "06:00 PM");
        cbGioDi.setValue("12:00 PM");

        dateTimeLayout.add(labelNgayDen, 0, 0);
        dateTimeLayout.add(dpNgayDen, 1, 0);
        dateTimeLayout.add(labelGioDen, 2, 0);
        dateTimeLayout.add(cbGioDen, 3, 0);

        dateTimeLayout.add(labelNgayDi, 0, 1);
        dateTimeLayout.add(dpNgayDi, 1, 1);
        dateTimeLayout.add(labelGioDi, 2, 1);
        dateTimeLayout.add(cbGioDi, 3, 1);

        tablePhongTrong = new TableView<>();
        tablePhongTrong.setEditable(true);
        tablePhongTrong.setItems(phongTrongList);

        TableColumn<Phong, String> colMaPhong = new TableColumn<>("Mã phòng");
        colMaPhong.setCellValueFactory(cellData -> cellData.getValue().maPhongProperty());
        colMaPhong.setPrefWidth(100);

        TableColumn<Phong, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(cellData -> cellData.getValue().loaiPhongProperty());
        colLoaiPhong.setPrefWidth(100);

        TableColumn<Phong, String> colGiaPhong = new TableColumn<>("Giá (VNĐ)");
        colGiaPhong.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%,.0f", cellData.getValue().getGiaPhong())));
        colGiaPhong.setPrefWidth(100);

        TableColumn<Phong, String> colThem = new TableColumn<>("Thêm");
        colThem.setCellFactory(column -> new TableCell<Phong, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Phong phong = getTableRow().getItem();
                    Button btnThem = new Button("Thêm");
                    btnThem.setOnAction(e -> {
                        phongDaChonList.add(phong);
                        updatePhongTrongList();
                        tablePhongTrong.refresh();
                    });
                    HBox hbox = new HBox(btnThem);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });
        colThem.setPrefWidth(100);

        tablePhongTrong.getColumns().addAll(colMaPhong, colLoaiPhong, colGiaPhong, colThem);

        tablePhongDaChon = new TableView<>();
        tablePhongDaChon.setEditable(true);
        tablePhongDaChon.setItems(phongDaChonList);

        TableColumn<Phong, String> colPhongDaChonMa = new TableColumn<>("Mã phòng");
        colPhongDaChonMa.setCellValueFactory(cellData -> cellData.getValue().maPhongProperty());
        colPhongDaChonMa.setPrefWidth(100);

        TableColumn<Phong, String> colPhongDaChonLoai = new TableColumn<>("Loại phòng");
        colPhongDaChonLoai.setCellValueFactory(cellData -> cellData.getValue().loaiPhongProperty());
        colPhongDaChonLoai.setPrefWidth(100);

        TableColumn<Phong, String> colSoLuongNguoi = new TableColumn<>("Số lượng người");
        colSoLuongNguoi.setCellFactory(column -> new TableCell<Phong, String>() {
            private final TextField soLuongNguoiField = new TextField("1");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Phong phong = getTableRow().getItem();
                    soLuongNguoiField.setText(String.valueOf(phong.getSoNguoiToiDa()));
                    soLuongNguoiField.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.matches("\\d*")) {
                            soLuongNguoiField.setText(oldVal);
                        } else if (!newVal.isEmpty()) {
                            int value = Integer.parseInt(newVal);
                            if (value <= phong.getSoNguoiToiDa()) {
                                phong.setSoNguoiToiDa(value);
                            } else {
                                soLuongNguoiField.setText(String.valueOf(phong.getSoNguoiToiDa()));
                            }
                        }
                    });
                    soLuongNguoiField.setPrefWidth(80);
                    setGraphic(soLuongNguoiField);
                }
            }
        });
        colSoLuongNguoi.setPrefWidth(120);

        TableColumn<Phong, String> colXoa = new TableColumn<>("Xóa");
        colXoa.setCellFactory(column -> new TableCell<Phong, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Phong phong = getTableRow().getItem();
                    Button btnXoa = new Button("Xóa");
                    btnXoa.setOnAction(e -> {
                        phongDaChonList.remove(phong);
                        updatePhongTrongList();
                        tablePhongTrong.refresh();
                    });
                    HBox hbox = new HBox(btnXoa);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });
        colXoa.setPrefWidth(100);

        tablePhongDaChon.getColumns().addAll(colPhongDaChonMa, colPhongDaChonLoai, colSoLuongNguoi, colXoa);

        tablePhongTrong.setPrefWidth(400);
        tablePhongDaChon.setPrefWidth(400);
        tablePhongTrong.setPrefHeight(200);
        tablePhongDaChon.setPrefHeight(200);

        HBox tableLayout = new HBox(40, tablePhongTrong, tablePhongDaChon);
        tableLayout.setAlignment(Pos.CENTER);
        tableLayout.setPadding(new Insets(20));

        roomInfo.getChildren().addAll(dateTimeLayout, tableLayout);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> handleSave(dpNgayDen, dpNgayDi, cbGioDen, cbGioDi));

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnHuy.setOnAction(e -> handleCancel());

        HBox buttonLayout = new HBox(20, btnLuu, btnHuy);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(10));

        GridPane gridLayout = new GridPane();
        gridLayout.setPadding(new Insets(10));
        gridLayout.setVgap(10);
        gridLayout.setHgap(20);
        gridLayout.add(customerInfo, 0, 0);
        gridLayout.add(roomInfo, 1, 0);

        VBox layoutMain = new VBox(20, gridLayout, buttonLayout);
        layoutMain.setAlignment(Pos.TOP_CENTER);

        layout.setCenter(layoutMain);
        root.getChildren().add(layout);
        return root;
    }

    private HBox createInputFieldWithoutIcon(String labelText) {
        TextField textField = new TextField();
        textField.setPromptText(labelText);
        textField.setPrefWidth(250);
        HBox hbox = new HBox(10, textField);
        hbox.setAlignment(Pos.CENTER_LEFT);
        return hbox;
    }

    private void checkExistingCustomerBySDT(String sdt) {
        if (sdt.matches("0\\d{9}")) {
            try {
                KhachHang existingCustomer = khachHangDao.getKhachHangBySDT(sdt);
                if (existingCustomer != null) {
                    fillCustomerInfo(existingCustomer);
                }
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể kiểm tra khách hàng theo SĐT: " + e.getMessage());
            }
        }
    }

    private void checkExistingCustomerByCCCD(String cccd) {
        if (cccd.matches("\\d{12}")) {
            try {
                KhachHang existingCustomer = khachHangDao.getKhachHangByCCCD(cccd);
                if (existingCustomer != null) {
                    fillCustomerInfo(existingCustomer);
                }
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể kiểm tra khách hàng theo CCCD: " + e.getMessage());
            }
        }
    }

    private void fillCustomerInfo(KhachHang customer) {
        tfHoTen.setText(customer.getTenKhachHang());
        tfCCCD.setText(customer.getCccd());
        tfDiaChi.setText(customer.getDiaChi());
        tfSDT.setText(customer.getSoDienThoai());
        tfQuocTich.setText(customer.getQuocTich());
        tfEmail.setText(customer.getEmail());
        cbGioiTinh.setValue(customer.getGioiTinh());
        dpNgaySinh.setValue(customer.getNgaySinh());
    }

    private void updatePhongTrongList() {
        phongTrongList.clear();
        for (Phong phong : allPhongList) {
            if ("Trống".equals(phong.getTrangThai()) && !phongDaChonList.contains(phong)) {
                phongTrongList.add(phong);
            }
        }
        if (tablePhongTrong != null) {
            tablePhongTrong.refresh();
        }
    }

    private void handleCancel() {
        tfHoTen.clear();
        tfCCCD.clear();
        tfDiaChi.clear();
        tfSDT.clear();
        tfQuocTich.clear();
        tfEmail.clear();
        tfSoLuongNguoi.clear();
        cbGioiTinh.setValue(null);
        dpNgaySinh.setValue(null);

        phongDaChonList.clear();
        updatePhongTrongList();
        tablePhongTrong.refresh();
        tablePhongDaChon.refresh();
    }

    private void handleSave(DatePicker dpNgayDen, DatePicker dpNgayDi, ComboBox<String> cbGioDen, ComboBox<String> cbGioDi) {
        String sdt = tfSDT.getText();
        String cccd = tfCCCD.getText();
        String email = tfEmail.getText();
        String soLuongNguoiText = tfSoLuongNguoi.getText();
        LocalDate ngayDen = dpNgayDen.getValue();
        LocalDate ngayDi = dpNgayDi.getValue();
        LocalDate ngaySinh = dpNgaySinh.getValue();
        String gioiTinh = cbGioiTinh.getValue();

        // Kiểm tra các trường bắt buộc trước khi lưu
        if (soLuongNguoiText.isEmpty()) {
            showAlert("Lỗi", "Số lượng người không được để trống.");
            return;
        }

        if (tfHoTen.getText().isEmpty()) {
            showAlert("Lỗi", "Họ tên không được để trống.");
            return;
        }

        if (!sdt.matches("0\\d{9}")) {
            showAlert("Lỗi", "SĐT phải có đúng 10 số và bắt đầu bằng 0.");
            return;
        }

        if (!cccd.matches("\\d{12}")) {
            showAlert("Lỗi", "CCCD phải có đúng 12 số.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Lỗi", "Email không hợp lệ.");
            return;
        }

        if (tfDiaChi.getText().isEmpty() || tfQuocTich.getText().isEmpty()) {
            showAlert("Lỗi", "Địa chỉ và quốc tịch không được để trống.");
            return;
        }

        if (gioiTinh == null || ngaySinh == null || ngayDen == null || ngayDi == null ||
                cbGioDen.getValue() == null || cbGioDi.getValue() == null || phongDaChonList.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin và chọn ít nhất một phòng.");
            return;
        }

        if (!ngayDi.isAfter(ngayDen)) {
            showAlert("Lỗi", "Ngày đi phải lớn hơn ngày đến.");
            return;
        }

        if (ngaySinh.isAfter(LocalDate.now().minusYears(18))) {
            showAlert("Lỗi", "Khách hàng phải trên 18 tuổi.");
            return;
        }

        int soLuongNguoi;
        try {
            soLuongNguoi = Integer.parseInt(soLuongNguoiText);
            if (soLuongNguoi <= 0) {
                showAlert("Lỗi", "Số lượng người phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số lượng người phải là số hợp lệ.");
            return;
        }

        DataManager dataManager = DataManager.getInstance();
        ObservableList<KhachHang> khachHangList = dataManager.getKhachHangList();

        // Xử lý khách hàng
        KhachHang khachHang;
        try {
            KhachHang existingCustomer = khachHangDao.getKhachHangBySDT(sdt);
            if (existingCustomer != null) {
                khachHang = existingCustomer;
            } else {
                khachHang = new KhachHang(
                        "KH" + String.format("%03d", khachHangList.size() + 1),
                        tfHoTen.getText(),
                        tfSDT.getText(),
                        tfEmail.getText(),
                        tfDiaChi.getText(),
                        tfCCCD.getText(),
                        dpNgaySinh.getValue(),
                        tfQuocTich.getText(),
                        cbGioiTinh.getValue()
                );
                khachHangDao.themKhachHang(khachHang);
                dataManager.addKhachHang(khachHang);
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể lưu hoặc kiểm tra thông tin khách hàng: " + e.getMessage());
            return;
        }

        // Tạo mã đặt phòng tự động
        String maDatPhong;
        try {
            maDatPhong = phieuDatPhongDao.getNextMaDatPhong();
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tạo mã đặt phòng: " + e.getMessage());
            return;
        }

        // Tạo PhieuDatPhong
        PhieuDatPhong phieuDatPhong = new PhieuDatPhong(
                maDatPhong,
                ngayDen,
                ngayDi,
                LocalDate.now(),
                soLuongNguoi,
                "Chưa xác nhận",
                khachHang.getMaKhachHang()
        );

        long soNgayO = ChronoUnit.DAYS.between(ngayDen, ngayDi);

        // Bắt đầu giao dịch
        Connection conn = null;
        try {
            conn = ConnectDB.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Lưu PhieuDatPhong
            phieuDatPhongDao.addPhieuDatPhong(phieuDatPhong);

            // Xử lý các phòng đã chọn
            for (Phong phong : phongDaChonList) {
                int index = allPhongList.indexOf(phong);
                if (index != -1) {
                    Phong updatedPhong = new Phong(
                            phong.getMaPhong(),
                            phong.getLoaiPhong(),
                            phong.getGiaPhong(),
                            "Đã đặt",
                            phong.getDonDep(),
                            phong.getViTri(),
                            phong.getSoNguoiToiDa(),
                            phong.getMoTa()
                    );

                    // Cập nhật trạng thái phòng
                    phongDao.suaPhong(updatedPhong);	
                    allPhongList.set(index, updatedPhong);
                    dataManager.getPhongList().set(dataManager.getPhongList().indexOf(phong), updatedPhong);

                    // Tạo và lưu ChitietPhieuDatPhong
                    double tienPhong = phong.getGiaPhong() * Math.max(1, soNgayO);
                    ChitietPhieuDatPhong chiTiet = new ChitietPhieuDatPhong(
                            phong.getMaPhong(),
                            maDatPhong,
                            phong.getGiaPhong(),
                            tienPhong,
                            phong.getSoNguoiToiDa()
                    );
                    chitietPhieuDatPhongDao.addChitietPhieuDatPhong(chiTiet);
                    phieuDatPhong.addChitietPhieuDatPhong(chiTiet);
                }
            }

            // Cam kết giao dịch
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                showAlert("Lỗi", "Không thể hoàn tác giao dịch: " + rollbackEx.getMessage());
            }
            showAlert("Lỗi", "Không thể lưu phiếu đặt phòng hoặc chi tiết: " + e.getMessage());
            return;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể khôi phục chế độ auto-commit: " + e.getMessage());
            }
        }

        // Thêm PhieuDatPhong vào DataManager
        dataManager.addPhieuDatPhong(phieuDatPhong);

        // Hiển thị thông tin xác nhận đặt phòng
        StringBuilder thongTinDatPhong = new StringBuilder();
        thongTinDatPhong.append("Thông tin đặt phòng:\n");
        thongTinDatPhong.append("Mã phiếu: ").append(maDatPhong).append("\n");
        thongTinDatPhong.append("Họ tên: ").append(tfHoTen.getText()).append("\n");
        thongTinDatPhong.append("CCCD: ").append(tfCCCD.getText()).append("\n");
        thongTinDatPhong.append("Địa chỉ: ").append(tfDiaChi.getText()).append("\n");
        thongTinDatPhong.append("SĐT: ").append(tfSDT.getText()).append("\n");
        thongTinDatPhong.append("Email: ").append(tfEmail.getText()).append("\n");
        thongTinDatPhong.append("Quốc tịch: ").append(tfQuocTich.getText()).append("\n");
        thongTinDatPhong.append("Giới tính: ").append(gioiTinh).append("\n");
        thongTinDatPhong.append("Ngày sinh: ").append(ngaySinh).append("\n");
        thongTinDatPhong.append("Ngày đến: ").append(ngayDen).append(" ").append(cbGioDen.getValue()).append("\n");
        thongTinDatPhong.append("Ngày đi: ").append(ngayDi).append(" ").append(cbGioDi.getValue()).append("\n");
        thongTinDatPhong.append("Số lượng người: ").append(soLuongNguoi).append("\n");
        thongTinDatPhong.append("Phòng đã đặt:\n");
        for (Phong phong : phongDaChonList) {
            thongTinDatPhong.append("- ").append(phong.getMaPhong())
                    .append(" (Số lượng người: ").append(phong.getSoNguoiToiDa()).append(")\n");
        }

        showAlert("Thành công", thongTinDatPhong.toString());

        handleCancel();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}