package UI;

import dao.ChitietPhieuDatPhong_Dao;
import dao.KhachHang_Dao;
import dao.PhieuDatPhong_Dao;
import dao.Phong_Dao;
import dao.HoaDon_Dao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ChitietPhieuDatPhong;
import model.KhachHang;
import model.PhieuDatPhong;
import model.Phong;
import model.HoaDon;
import ConnectDB.ConnectDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DatphongUI {
    private final DataManager dataManager = DataManager.getInstance();
    private final ObservableList<Phong> phongList = dataManager.getPhongList();
    private final ObservableList<KhachHang> khachHangList = dataManager.getKhachHangList();
    private final ObservableList<PhieuDatPhong> phieuDatPhongList = dataManager.getPhieuDatPhongList();
    private final ObservableList<ChitietPhieuDatPhong> chitietPhieuDatPhongList = dataManager.getChitietPhieuDatPhongList();
    private final ObservableList<Phong> phongTrongList = FXCollections.observableArrayList();
    private final ObservableList<Phong> phongDaChonList = FXCollections.observableArrayList();
    private TextField tfHoTen;
    private TextField tfCCCD;
    private TextField tfSDT;
    private TextField tfEmail;
    private TextField tfDiaChi;
    private TextField tfQuocTich;
    private DatePicker dpNgaySinh;
    private ComboBox<String> cbGioiTinh;
    private DatePicker dpNgayDen;
    private DatePicker dpNgayDi;
    private TextField tfSoLuongNguoi;
    private TextField tfTimKiem;
    private TableView<Phong> tvPhongTrong;
    private TableView<Phong> tvPhongDaChon;
    private final KhachHang_Dao khachHangDao = new KhachHang_Dao();
    private final Phong_Dao phongDao = new Phong_Dao();
    private final PhieuDatPhong_Dao phieuDatPhongDao = new PhieuDatPhong_Dao();
    private final ChitietPhieuDatPhong_Dao chitietPhieuDatPhongDao = new ChitietPhieuDatPhong_Dao();
    private final HoaDon_Dao hoaDonDao = new HoaDon_Dao();
    private StackPane contentPane;
    private StackPane mainPane;

    public DatphongUI() {
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
        tfTimKiem = new TextField();
        tfTimKiem.setPromptText("Tìm kiếm phòng (mã, loại, vị trí)...");
        tfTimKiem.setPrefWidth(300);
        tfTimKiem.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, new Label("Tìm kiếm:"), tfTimKiem, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        searchButton.setOnAction(e -> updatePhongTrongList(dpNgayDen.getValue(), dpNgayDi.getValue()));
        tfTimKiem.setOnAction(e -> searchButton.fire());

        // Customer Info
        VBox customerInfo = new VBox(10);
        customerInfo.setAlignment(Pos.TOP_LEFT);

        tfHoTen = createTextField("Nhập Họ Tên");
        tfCCCD = createTextField("Nhập CCCD/passport");
        tfDiaChi = createTextField("Nhập Địa Chỉ");
        tfSDT = createTextField("Nhập SĐT");
        tfQuocTich = createTextField("Nhập Quốc Tịch");
        tfEmail = createTextField("Nhập Email");

        tfSoLuongNguoi = createTextField("Nhập Số Lượng Người");
        tfSoLuongNguoi.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfSoLuongNguoi.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        cbGioiTinh = new ComboBox<>(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        cbGioiTinh.setPromptText("Chọn Giới Tính");
        cbGioiTinh.setPrefWidth(250);
        cbGioiTinh.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        dpNgaySinh = new DatePicker();
        dpNgaySinh.setPromptText("Chọn Ngày Sinh");
        dpNgaySinh.setPrefWidth(250);
        dpNgaySinh.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        customerInfo.getChildren().addAll(tfHoTen, tfCCCD, tfDiaChi, tfSDT, tfQuocTich, tfEmail,
                tfSoLuongNguoi, cbGioiTinh, dpNgaySinh);

        // Room Info
        VBox roomInfo = new VBox(10);
        roomInfo.setAlignment(Pos.TOP_LEFT);

        dpNgayDen = new DatePicker(LocalDate.now());
        dpNgayDen.setPromptText("Chọn Ngày Đến");
        dpNgayDen.setPrefWidth(250);
        dpNgayDen.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");
        HBox hboxNgayDen = new HBox(10, new Label("Ngày đến:"), dpNgayDen);
        hboxNgayDen.setAlignment(Pos.CENTER_LEFT);

        dpNgayDi = new DatePicker(LocalDate.now().plusDays(1));
        dpNgayDi.setPromptText("Chọn Ngày Đi");
        dpNgayDi.setPrefWidth(250);
        dpNgayDi.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");
        HBox hboxNgayDi = new HBox(10, new Label("Ngày đi:"), dpNgayDi);
        hboxNgayDi.setAlignment(Pos.CENTER_LEFT);

        VBox dateLayout = new VBox(10, hboxNgayDen, hboxNgayDi);

        tvPhongTrong = new TableView<>();
        tvPhongTrong.setEditable(true);
        tvPhongTrong.setItems(phongTrongList);
        tvPhongTrong.setPrefWidth(450);
        tvPhongTrong.setPrefHeight(200);

        TableColumn<Phong, String> colMaPhong = new TableColumn<>("Mã Phòng");
        colMaPhong.setCellValueFactory(cellData -> cellData.getValue().maPhongProperty());
        colMaPhong.setPrefWidth(100);

        TableColumn<Phong, String> colLoaiPhong = new TableColumn<>("Loại Phòng");
        colLoaiPhong.setCellValueFactory(cellData -> cellData.getValue().loaiPhongProperty());
        colLoaiPhong.setPrefWidth(100);

        TableColumn<Phong, String> colGiaPhong = new TableColumn<>("Giá (VNĐ)");
        colGiaPhong.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.format("%,.0f", cellData.getValue().getGiaPhong())));
        colGiaPhong.setPrefWidth(100);

        TableColumn<Phong, String> colSoNguoiToiDa = new TableColumn<>("Số Người Tối Đa");
        colSoNguoiToiDa.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSoNguoiToiDa())));
        colSoNguoiToiDa.setPrefWidth(100);

        TableColumn<Phong, String> colThem = new TableColumn<>("Thêm");
        colThem.setCellFactory(column -> new TableCell<Phong, String>() {
            private final Button btnThem = new Button("Thêm");
            {
                btnThem.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    btnThem.setOnAction(e -> handleAddPhong(getTableRow().getItem()));
                    setGraphic(btnThem);
                }
            }
        });
        colThem.setPrefWidth(100);

        tvPhongTrong.getColumns().addAll(colMaPhong, colLoaiPhong, colGiaPhong, colSoNguoiToiDa, colThem);

        tvPhongDaChon = new TableView<>();
        tvPhongDaChon.setEditable(true);
        tvPhongDaChon.setItems(phongDaChonList);
        tvPhongDaChon.setPrefWidth(450);
        tvPhongDaChon.setPrefHeight(200);

        TableColumn<Phong, String> colPhongDaChonMa = new TableColumn<>("Mã Phòng");
        colPhongDaChonMa.setCellValueFactory(cellData -> cellData.getValue().maPhongProperty());
        colPhongDaChonMa.setPrefWidth(100);

        TableColumn<Phong, String> colPhongDaChonLoai = new TableColumn<>("Loại Phòng");
        colPhongDaChonLoai.setCellValueFactory(cellData -> cellData.getValue().loaiPhongProperty());
        colPhongDaChonLoai.setPrefWidth(100);

        TableColumn<Phong, String> colPhongDaChonSoNguoi = new TableColumn<>("Số Người Tối Đa");
        colPhongDaChonSoNguoi.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSoNguoiToiDa())));
        colPhongDaChonSoNguoi.setPrefWidth(120);

        TableColumn<Phong, String> colXoa = new TableColumn<>("Xóa");
        colXoa.setCellFactory(column -> new TableCell<Phong, String>() {
            private final Button btnXoa = new Button("Xóa");
            {
                btnXoa.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    btnXoa.setOnAction(e -> handleRemovePhong(getTableRow().getItem()));
                    setGraphic(btnXoa);
                }
            }
        });
        colXoa.setPrefWidth(100);

        tvPhongDaChon.getColumns().addAll(colPhongDaChonMa, colPhongDaChonLoai, colPhongDaChonSoNguoi, colXoa);

        HBox tableLayout = new HBox(20, tvPhongTrong, tvPhongDaChon);
        tableLayout.setAlignment(Pos.CENTER);

        roomInfo.getChildren().addAll(dateLayout, tableLayout);

        HBox contentLayout = new HBox(20, customerInfo, roomInfo);
        contentLayout.setAlignment(Pos.TOP_CENTER);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnLuu.setOnAction(e -> handleSave());

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnHuy.setOnAction(e -> clearInputFields());

        HBox buttonLayout = new HBox(20, btnLuu, btnHuy);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(10));

        // Center layout
        VBox centerLayout = new VBox(15, searchBox, contentLayout, buttonLayout);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.TOP_CENTER);
        centerLayout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(centerLayout);
        layout.setPadding(new Insets(10));

        mainPane.getChildren().add(layout);

        // Initialize listeners
        tfSDT.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                try {
                    KhachHang existingCustomer = khachHangDao.getKhachHangBySDT(newValue.trim());
                    if (existingCustomer != null) {
                        fillCustomerInfo(existingCustomer);
                    }
                } catch (SQLException e) {
                    showAlert("Lỗi", "Không thể kiểm tra khách hàng theo SĐT: " + e.getMessage());
                }
            }
        });

        tfCCCD.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                try {
                    KhachHang existingCustomer = khachHangDao.getKhachHangByCCCD(newValue.trim());
                    if (existingCustomer != null) {
                        fillCustomerInfo(existingCustomer);
                    }
                } catch (SQLException e) {
                    showAlert("Lỗi", "Không thể kiểm tra khách hàng theo CCCD: " + e.getMessage());
                }
            }
        });

        tfTimKiem.textProperty().addListener((obs, oldValue, newValue) ->
                updatePhongTrongList(dpNgayDen.getValue(), dpNgayDi.getValue()));

        updatePhongTrongList(dpNgayDen.getValue(), dpNgayDi.getValue());

        dataManager.addPhongListChangeListener(() -> updatePhongTrongList(dpNgayDen.getValue(), dpNgayDi.getValue()));
        dataManager.addPhieuDatPhongListChangeListener(() -> updatePhongTrongList(dpNgayDen.getValue(), dpNgayDi.getValue()));
        dataManager.addChitietPhieuDatPhongListChangeListener(() -> updatePhongTrongList(dpNgayDen.getValue(), dpNgayDi.getValue()));

        dpNgayDen.valueProperty().addListener((obs, oldValue, newValue) -> updatePhongTrongList(newValue, dpNgayDi.getValue()));
        dpNgayDi.valueProperty().addListener((obs, oldValue, newValue) -> updatePhongTrongList(dpNgayDen.getValue(), newValue));

        return mainPane;
    }

    public Node getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    public void showUI(Stage primaryStage) {
        if (primaryStage == null) {
            showAlert("Lỗi", "Không thể khởi tạo giao diện: primaryStage là null.");
            return;
        }

        Stage dialogStage = new Stage();
        try {
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setTitle("Đặt Phòng");

            Node ui = getUI();
            Scene scene = new Scene((Region) ui, 1000, 650);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (Exception e) {
            showAlert("Lỗi", "Không thể khởi tạo giao diện: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefWidth(250);
        textField.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");
        return textField;
    }

    private void fillCustomerInfo(KhachHang customer) {
        tfHoTen.setText(customer.getTenKhachHang() != null ? customer.getTenKhachHang() : "");
        tfCCCD.setText(customer.getCccd() != null ? customer.getCccd() : "");
        tfDiaChi.setText(customer.getDiaChi() != null ? customer.getDiaChi() : "");
        tfSDT.setText(customer.getSoDienThoai() != null ? customer.getSoDienThoai() : "");
        tfQuocTich.setText(customer.getQuocTich() != null ? customer.getQuocTich() : "");
        tfEmail.setText(customer.getEmail() != null ? customer.getEmail() : "");
        cbGioiTinh.setValue(customer.getGioiTinh() != null ? customer.getGioiTinh() : null);
        dpNgaySinh.setValue(customer.getNgaySinh());
    }

    private void updatePhongTrongList(LocalDate ngayDen, LocalDate ngayDi) {
        phongTrongList.clear();
        String searchText = tfTimKiem != null ? tfTimKiem.getText().trim().toLowerCase() : "";

        // Nếu không chọn ngày, hiển thị tất cả phòng (trừ phòng Bảo Trì)
        if (ngayDen == null || ngayDi == null) {
            for (Phong phong : phongList) {
                if (phong.getTrangThai() == null || !"Bảo Trì".equalsIgnoreCase(phong.getTrangThai())) {
                    if (searchText.isEmpty() ||
                            phong.getMaPhong().toLowerCase().contains(searchText) ||
                            phong.getLoaiPhong().toLowerCase().contains(searchText) ||
                            (phong.getViTri() != null && phong.getViTri().toLowerCase().contains(searchText))) {
                        phongTrongList.add(phong);
                    }
                }
            }
            return;
        }

        // Kiểm tra từng phòng xem có khả dụng trong khoảng ngày chọn không
        for (Phong phong : phongList) {
            // Bỏ qua phòng Bảo Trì
            if (phong.getTrangThai() != null && "Bảo Trì".equalsIgnoreCase(phong.getTrangThai())) {
                continue;
            }

            boolean isAvailable = true;

            // Bỏ qua các phòng đã được chọn để tránh hiển thị lại
            if (phongDaChonList.contains(phong)) {
                continue;
            }

            // Kiểm tra các phiếu đặt phòng trùng lặp
            for (ChitietPhieuDatPhong chitiet : chitietPhieuDatPhongList) {
                if (chitiet.getMaPhong().equals(phong.getMaPhong())) {
                    PhieuDatPhong phieu = phieuDatPhongList.stream()
                            .filter(p -> p.getMaDatPhong().equals(chitiet.getMaDatPhong()))
                            .findFirst()
                            .orElse(null);
                    if (phieu != null && !"Đã hủy".equalsIgnoreCase(phieu.getTrangThai())) {
                        LocalDate phieuNgayDen = phieu.getNgayDen();
                        LocalDate phieuNgayDi = phieu.getNgayDi();
                        if (phieuNgayDen != null && phieuNgayDi != null) {
                            // Kiểm tra trùng lặp: khoảng thời gian trùng nếu ngayDen <= phieuNgayDi và phieuNgayDen <= ngayDi
                            if (!(ngayDi.isBefore(phieuNgayDen) || ngayDen.isAfter(phieuNgayDi))) {
                                isAvailable = false;
                                break;
                            }
                        }
                    }
                }
            }

            // Thêm phòng vào danh sách nếu khả dụng và khớp với tìm kiếm
            if (isAvailable) {
                if (searchText.isEmpty() ||
                        phong.getMaPhong().toLowerCase().contains(searchText) ||
                        phong.getLoaiPhong().toLowerCase().contains(searchText) ||
                        (phong.getViTri() != null && phong.getViTri().toLowerCase().contains(searchText))) {
                    phongTrongList.add(phong);
                }
            }
        }
    }

    private void handleAddPhong(Phong selectedPhong) {
        if (selectedPhong == null) {
            showAlert("Lỗi", "Vui lòng chọn một phòng từ danh sách phòng trống.");
            return;
        }

        if (!phongDaChonList.contains(selectedPhong)) {
            phongDaChonList.add(selectedPhong);
            phongTrongList.remove(selectedPhong);
        } else {
            showAlert("Lỗi", "Phòng đã được chọn.");
        }
    }

    private void handleRemovePhong(Phong selectedPhong) {
        if (selectedPhong == null) {
            showAlert("Lỗi", "Vui lòng chọn một phòng từ danh sách phòng đã chọn.");
            return;
        }

        phongDaChonList.remove(selectedPhong);
        updatePhongTrongList(dpNgayDen.getValue(), dpNgayDi.getValue());
    }

    private void handleSave() {
        String hoTen = tfHoTen.getText().trim();
        String cccd = tfCCCD.getText().trim();
        String sdt = tfSDT.getText().trim();
        String email = tfEmail.getText().trim();
        String diaChi = tfDiaChi.getText().trim();
        String quocTich = tfQuocTich.getText().trim();
        LocalDate ngaySinh = dpNgaySinh.getValue();
        String gioiTinh = cbGioiTinh.getValue();
        LocalDate ngayDen = dpNgayDen.getValue();
        LocalDate ngayDi = dpNgayDi.getValue();
        String soLuongNguoiStr = tfSoLuongNguoi.getText().trim();

        // Validation
        if (hoTen.isEmpty()) {
            showAlert("Lỗi", "Họ tên không được để trống.");
            return;
        }

        if (sdt.isEmpty()) {
            showAlert("Lỗi", "Số điện thoại không được để trống.");
            return;
        }

        if (!sdt.matches("\\d{10,11}")) {
            showAlert("Lỗi", "Số điện thoại không hợp lệ. Vui lòng nhập 10 hoặc 11 số.");
            return;
        }

        if (!cccd.isEmpty() && !cccd.matches("\\d{9,12}")) {
            showAlert("Lỗi", "CCCD không hợp lệ. Vui lòng nhập 9-12 số hoặc để trống.");
            return;
        }

        if (!email.isEmpty() && !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Lỗi", "Email không hợp lệ. Vui lòng nhập đúng định dạng hoặc để trống.");
            return;
        }

        if (ngayDen == null || ngayDi == null) {
            showAlert("Lỗi", "Ngày đến và ngày đi không được để trống.");
            return;
        }

        if (ngayDi.isBefore(ngayDen) || ngayDi.isEqual(ngayDen)) {
            showAlert("Lỗi", "Ngày đi phải sau ngày đến.");
            return;
        }

        if (soLuongNguoiStr.isEmpty()) {
            showAlert("Lỗi", "Số lượng người không được để trống.");
            return;
        }

        int soLuongNguoi;
        try {
            soLuongNguoi = Integer.parseInt(soLuongNguoiStr);
            if (soLuongNguoi <= 0) {
                showAlert("Lỗi", "Số lượng người phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số lượng người không hợp lệ. Vui lòng nhập số nguyên.");
            return;
        }

        if (phongDaChonList.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn ít nhất một phòng.");
            return;
        }

        int totalSoNguoiToiDa = phongDaChonList.stream().mapToInt(Phong::getSoNguoiToiDa).sum();
        if (soLuongNguoi > totalSoNguoiToiDa) {
            showAlert("Lỗi", "Số lượng người vượt quá sức chứa tối đa của các phòng đã chọn (" + totalSoNguoiToiDa + " người).");
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectDB.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Create or update customer
            KhachHang khachHang;
            String maKhachHang;
            KhachHang existingCustomer = khachHangDao.getKhachHangBySDT(sdt);
            if (existingCustomer != null) {
                khachHang = existingCustomer;
                maKhachHang = khachHang.getMaKhachHang();
                khachHang.setTenKhachHang(hoTen);
                khachHang.setCccd(cccd.isEmpty() ? null : cccd);
                khachHang.setSoDienThoai(sdt);
                khachHang.setEmail(email.isEmpty() ? null : email);
                khachHang.setDiaChi(diaChi.isEmpty() ? null : diaChi);
                khachHang.setQuocTich(quocTich.isEmpty() ? null : quocTich);
                khachHang.setGioiTinh(gioiTinh != null ? gioiTinh : null);
                khachHang.setNgaySinh(ngaySinh);
                dataManager.updateKhachHang(khachHang);
            } else {
                maKhachHang = khachHangDao.getNextMaKhachHang();
                if (maKhachHang == null || maKhachHang.isEmpty()) {
                    throw new SQLException("Không thể tạo mã khách hàng.");
                }
                khachHang = new KhachHang(
                        maKhachHang,
                        hoTen,
                        sdt,
                        email.isEmpty() ? null : email,
                        diaChi.isEmpty() ? null : diaChi,
                        cccd.isEmpty() ? null : cccd,
                        ngaySinh,
                        quocTich.isEmpty() ? null : quocTich,
                        gioiTinh != null ? gioiTinh : null
                );
                dataManager.addKhachHang(khachHang);
            }

            long soNgayO = ChronoUnit.DAYS.between(ngayDen, ngayDi);
            double tongTien = 0;
            for (Phong phong : phongDaChonList) {
                double tienPhong = phong.getGiaPhong() * Math.max(1, soNgayO);
                tongTien += tienPhong;
            }

            // Check for existing unpaid invoice for the customer
            String maHoaDon = null;
            for (HoaDon hoaDon : dataManager.getHoaDonList()) {
                if (hoaDon.getMaKhachHang().equals(maKhachHang) && !hoaDon.getTrangThai()) {
                    maHoaDon = hoaDon.getMaHoaDon();
                    // Update total amount of existing invoice
                    hoaDon.setTongTien(hoaDon.getTongTien() + tongTien);
                    dataManager.updateHoaDon(hoaDon);
                    break;
                }
            }

            // If no existing unpaid invoice, create a new one
            if (maHoaDon == null) {
                maHoaDon = hoaDonDao.getNextMaHoaDon();
                String maNhanVien = dataManager.getCurrentNhanVien() != null
                        ? dataManager.getCurrentNhanVien().getMaNhanVien()
                        : "NV001";
                HoaDon hoaDon = new HoaDon(
                        maHoaDon,
                        LocalDateTime.now(),
                        "Chưa xác định",
                        tongTien,
                        false,
                        maKhachHang,
                        maNhanVien
                );
                dataManager.addHoaDon(hoaDon);
            }

            // Create booking with invoice ID
            String maDatPhong = generateMaDatPhong();
            PhieuDatPhong phieuDatPhong = new PhieuDatPhong(
                    maDatPhong,
                    ngayDen,
                    ngayDi,
                    LocalDate.now(),
                    soLuongNguoi,
                    "Đã đặt",
                    maKhachHang,
                    maHoaDon
            );
            dataManager.addPhieuDatPhong(phieuDatPhong);

            // Create booking details
            for (Phong phong : phongDaChonList) {
                double tienPhong = phong.getGiaPhong() * Math.max(1, soNgayO);
                ChitietPhieuDatPhong chiTiet = new ChitietPhieuDatPhong(
                        maDatPhong,
                        phong.getMaPhong(),
                        "Đã đặt",
                        "Phòng " + phong.getLoaiPhong() + " cho " + phong.getSoNguoiToiDa() + " người",
                        tienPhong,
                        0.0,
                        tienPhong,
                        phong.getSoNguoiToiDa(),
                        false
                );
                dataManager.addChitietPhieuDatPhong(chiTiet);
            }

            conn.commit();

            // Display booking info
            StringBuilder thongTinDatPhong = new StringBuilder();
            thongTinDatPhong.append("Thông tin đặt phòng:\n");
            thongTinDatPhong.append("Mã phiếu: ").append(maDatPhong).append("\n");
            thongTinDatPhong.append("Mã hóa đơn: ").append(maHoaDon).append("\n");
            thongTinDatPhong.append("Họ tên: ").append(hoTen).append("\n");
            thongTinDatPhong.append("CCCD: ").append(cccd.isEmpty() ? "Không cung cấp" : cccd).append("\n");
            thongTinDatPhong.append("Địa chỉ: ").append(diaChi.isEmpty() ? "Không cung cấp" : diaChi).append("\n");
            thongTinDatPhong.append("SĐT: ").append(sdt).append("\n");
            thongTinDatPhong.append("Email: ").append(email.isEmpty() ? "Không cung cấp" : email).append("\n");
            thongTinDatPhong.append("Quốc tịch: ").append(quocTich.isEmpty() ? "Không cung cấp" : quocTich).append("\n");
            thongTinDatPhong.append("Giới tính: ").append(gioiTinh != null ? gioiTinh : "Không cung cấp").append("\n");
            thongTinDatPhong.append("Ngày sinh: ").append(ngaySinh != null ? ngaySinh : "Không cung cấp").append("\n");
            thongTinDatPhong.append("Ngày đến: ").append(ngayDen).append("\n");
            thongTinDatPhong.append("Ngày đi: ").append(ngayDi).append("\n");
            thongTinDatPhong.append("Số lượng người: ").append(soLuongNguoi).append("\n");
            thongTinDatPhong.append("Tổng tiền: ").append(String.format("%,.0f", tongTien)).append(" VNĐ\n");
            thongTinDatPhong.append("Phòng đã đặt:\n");
            for (Phong phong : phongDaChonList) {
                thongTinDatPhong.append("- ").append(phong.getMaPhong()).append(" (").append(phong.getLoaiPhong())
                        .append(", Số người tối đa: ").append(phong.getSoNguoiToiDa()).append(", Giá: ")
                        .append(String.format("%,.0f", phong.getGiaPhong() * soNgayO)).append(" VNĐ)\n");
            }

            showAlert("Thành công", thongTinDatPhong.toString());

            // Clear inputs after successful save
            clearInputFields();
            phongDaChonList.clear();
            updatePhongTrongList(dpNgayDen.getValue(), dpNgayDi.getValue());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            showAlert("Lỗi", "Không thể lưu thông tin đặt phòng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    private void clearInputFields() {
        tfHoTen.clear();
        tfCCCD.clear();
        tfSDT.clear();
        tfEmail.clear();
        tfDiaChi.clear();
        tfQuocTich.clear();
        dpNgaySinh.setValue(null);
        cbGioiTinh.setValue(null);
        dpNgayDen.setValue(LocalDate.now());
        dpNgayDi.setValue(LocalDate.now().plusDays(1));
        tfSoLuongNguoi.clear();
        tfTimKiem.clear();
        phongDaChonList.clear();
        updatePhongTrongList(dpNgayDen.getValue(), dpNgayDi.getValue());
    }

    private String generateMaDatPhong() {
        String prefix = "DP";
        int maxNumber = 0;

        for (PhieuDatPhong phieu : phieuDatPhongList) {
            String maDatPhong = phieu.getMaDatPhong();
            if (maDatPhong != null && maDatPhong.startsWith(prefix)) {
                try {
                    int number = Integer.parseInt(maDatPhong.substring(prefix.length()));
                    maxNumber = Math.max(maxNumber, number);
                } catch (NumberFormatException e) {
                    // Ignore invalid codes
                }
            }
        }

        return prefix + String.format("%03d", maxNumber + 1);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}