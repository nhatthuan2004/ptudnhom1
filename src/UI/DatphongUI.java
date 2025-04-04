package UI;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import model.Phong;
import model.HoaDon;
import model.KhachHang;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DatphongUI {
    private TableView<Phong> tablePhongTrong;
    private TableView<Phong> tablePhongDaChon;
    private ObservableList<Phong> phongTrongList;
    private ObservableList<Phong> phongDaChonList;
    private ObservableList<Phong> allPhongList;

    private TextField tfHoTen, tfCCCD, tfDiaChi, tfSDT, tfQuocTich, tfGioiTinh;

    public DatphongUI() {
        DataManager dataManager = DataManager.getInstance();
        allPhongList = dataManager.getPhongList();
        phongTrongList = FXCollections.observableArrayList();
        phongDaChonList = FXCollections.observableArrayList();

        // Cập nhật danh sách phòng trống ban đầu
        updatePhongTrongList();

        // Thêm listener để đồng bộ khi danh sách phòng trong DataManager thay đổi
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

        VBox customerInfo = new VBox(35);
        customerInfo.setPadding(new Insets(20));
        customerInfo.setAlignment(Pos.TOP_LEFT);

        HBox hboxHoTen = createInputFieldWithIcon("Nhập Họ Tên", "/img/ten.png");
        tfHoTen = (TextField) hboxHoTen.getChildren().get(1);
        HBox hboxCCCD = createInputFieldWithIcon("Nhập CCCD/passport", "/img/cccd.png");
        tfCCCD = (TextField) hboxCCCD.getChildren().get(1);
        HBox hboxDiaChi = createInputFieldWithIcon("Nhập Địa Chỉ", "/img/diachi.png");
        tfDiaChi = (TextField) hboxDiaChi.getChildren().get(1);
        HBox hboxSDT = createInputFieldWithIcon("Nhập SĐT", "/img/sdt.png");
        tfSDT = (TextField) hboxSDT.getChildren().get(1);
        HBox hboxQuocTich = createInputFieldWithIcon("Nhập Quốc Tịch", "/img/quoctich.png");
        tfQuocTich = (TextField) hboxQuocTich.getChildren().get(1);
        HBox hboxGioiTinh = createInputFieldWithIcon("Nhập Giới Tính", "/img/gioitinh.png");
        tfGioiTinh = (TextField) hboxGioiTinh.getChildren().get(1);

        customerInfo.getChildren().addAll(hboxHoTen, hboxCCCD, hboxDiaChi, hboxSDT, hboxQuocTich, hboxGioiTinh);

        VBox roomInfo = new VBox(15);
        roomInfo.setPadding(new Insets(25));
        roomInfo.setAlignment(Pos.TOP_LEFT);

        GridPane dateTimeLayout = new GridPane();
        dateTimeLayout.setHgap(15);
        dateTimeLayout.setVgap(15);
        dateTimeLayout.setPadding(new Insets(0, 0, 15, 0));

        Label labelNgayDen = new Label("Ngày đến");
        DatePicker dpNgayDen = new DatePicker();

        Label labelNgayDi = new Label("Ngày đi");
        DatePicker dpNgayDi = new DatePicker();

        Label labelGioDen = new Label("Giờ đến");
        ComboBox<String> cbGioDen = new ComboBox<>();
        cbGioDen.getItems().addAll("08:00 AM", "12:00 PM", "02:00 PM", "06:00 PM");

        Label labelGioDi = new Label("Giờ đi");
        ComboBox<String> cbGioDi = new ComboBox<>();
        cbGioDi.getItems().addAll("08:00 AM", "12:00 PM", "02:00 PM", "06:00 PM");

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
        btnLuu.setOnAction(e -> handleSave(dpNgayDen, dpNgayDi, cbGioDen, cbGioDi));

        Button btnHuy = new Button("Hủy");
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

    private HBox createInputFieldWithIcon(String labelText, String iconPath) {
        TextField textField = new TextField();
        textField.setPromptText(labelText);

        ImageView icon;
        try {
            icon = new ImageView(new Image(getClass().getResource(iconPath).toExternalForm()));
        } catch (Exception e) {
            icon = new ImageView(new Image("https://via.placeholder.com/20"));
            System.out.println("Warning: Icon not found at " + iconPath);
        }
        icon.setFitHeight(20);
        icon.setFitWidth(20);

        HBox hbox = new HBox(25, icon, textField);
        hbox.setAlignment(Pos.CENTER_LEFT);

        textField.setPrefWidth(250);

        return hbox;
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
        tfGioiTinh.clear();

        phongDaChonList.clear();
        updatePhongTrongList();
        tablePhongTrong.refresh();
        tablePhongDaChon.refresh();
    }

    private void handleSave(DatePicker dpNgayDen, DatePicker dpNgayDi, ComboBox<String> cbGioDen, ComboBox<String> cbGioDi) {
        String sdt = tfSDT.getText();
        String cccd = tfCCCD.getText();
        LocalDate ngayDen = dpNgayDen.getValue();
        LocalDate ngayDi = dpNgayDi.getValue();

        if (!sdt.matches("0\\d{9}")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi nhập liệu");
            alert.setHeaderText("SĐT không hợp lệ");
            alert.setContentText("SĐT phải có đúng 10 số và bắt đầu bằng 0.");
            alert.showAndWait();
            return;
        }

        if (!cccd.matches("\\d{12}")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi nhập liệu");
            alert.setHeaderText("CCCD không hợp lệ");
            alert.setContentText("CCCD phải có đúng 12 số.");
            alert.showAndWait();
            return;
        }

        if (tfHoTen.getText().isEmpty() || tfDiaChi.getText().isEmpty() || tfQuocTich.getText().isEmpty() || 
            tfGioiTinh.getText().isEmpty() || ngayDen == null || ngayDi == null || 
            cbGioDen.getValue() == null || cbGioDi.getValue() == null || phongDaChonList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi nhập liệu");
            alert.setHeaderText("Thông tin chưa đầy đủ");
            alert.setContentText("Vui lòng nhập đầy đủ thông tin và chọn ít nhất một phòng.");
            alert.showAndWait();
            return;
        }

        if (!ngayDi.isAfter(ngayDen)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi nhập liệu");
            alert.setHeaderText("Ngày đi không hợp lệ");
            alert.setContentText("Ngày đi phải lớn hơn ngày đến.");
            alert.showAndWait();
            return;
        }

        DataManager dataManager = DataManager.getInstance();
        ObservableList<HoaDon> hoaDonList = dataManager.getHoaDonList();
        ObservableList<KhachHang> khachHangList = dataManager.getKhachHangList();

        long soNgayO = ChronoUnit.DAYS.between(ngayDen, ngayDi);

        for (Phong phong : phongDaChonList) {
            int index = allPhongList.indexOf(phong);
            if (index != -1) {
                phong.setTrangThai("Đã đặt");
                phong.setMoTa("Phòng đã được đặt bởi " + tfHoTen.getText());
                phong.setTenKhachHang(tfHoTen.getText());
                phong.setSoDienThoai(tfSDT.getText());

                double tienPhong = phong.getGiaPhong() * soNgayO;

                HoaDon hoaDon = new HoaDon();
                hoaDon.setMaHoaDon("HD" + String.format("%03d", hoaDonList.size() + 1));
                hoaDon.setTenKhachHang(tfHoTen.getText());
                hoaDon.setPhong(phong.getMaPhong());
                hoaDon.setTienPhong(tienPhong);
                hoaDon.setTienDichVu(0.0);
                hoaDon.setTrangThai("Chưa thanh toán");
                hoaDon.setMaPhong(phong.getMaPhong());
                hoaDon.setNgayLap(LocalDateTime.now());
                hoaDon.setHinhThucThanhToan("Tiền mặt");
                hoaDon.setMaKhachHang("KH" + String.format("%03d", khachHangList.size() + 1));
                hoaDon.setMaNhanVien("NV001");
                hoaDon.setMoTa("Phòng " + phong.getMaPhong() + " từ " + ngayDen + " đến " + ngayDi);

                hoaDonList.add(hoaDon);

                allPhongList.set(index, phong);
            }
        }

        KhachHang khachHang = new KhachHang(cccd, cccd, cccd, cccd, cccd, cccd, ngayDi, cccd, cccd);
        khachHang.setMaKhachHang("KH" + String.format("%03d", khachHangList.size() + 1));
        khachHang.setTenKhachHang(tfHoTen.getText());
        khachHang.setCccd(tfCCCD.getText());
        khachHang.setSoDienThoai(tfSDT.getText());
        khachHang.setDiaChi(tfDiaChi.getText());
        khachHang.setEmail("");
        khachHang.setNgaySinh(LocalDate.now());
        khachHangList.add(khachHang);

        StringBuilder thongTinDatPhong = new StringBuilder();
        thongTinDatPhong.append("Thông tin đặt phòng:\n");
        thongTinDatPhong.append("Họ tên: ").append(tfHoTen.getText()).append("\n");
        thongTinDatPhong.append("CCCD: ").append(tfCCCD.getText()).append("\n");
        thongTinDatPhong.append("Địa chỉ: ").append(tfDiaChi.getText()).append("\n");
        thongTinDatPhong.append("SĐT: ").append(tfSDT.getText()).append("\n");
        thongTinDatPhong.append("Quốc tịch: ").append(tfQuocTich.getText()).append("\n");
        thongTinDatPhong.append("Giới tính: ").append(tfGioiTinh.getText()).append("\n");
        thongTinDatPhong.append("Ngày đến: ").append(ngayDen).append(" ").append(cbGioDen.getValue()).append("\n");
        thongTinDatPhong.append("Ngày đi: ").append(ngayDi).append(" ").append(cbGioDi.getValue()).append("\n");
        thongTinDatPhong.append("Phòng đã đặt:\n");

        for (Phong phong : phongDaChonList) {
            thongTinDatPhong.append("- ").append(phong.getMaPhong())
                            .append(" (Số lượng người: ").append(phong.getSoNguoiToiDa()).append(")\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đặt phòng thành công");
        alert.setHeaderText("Thông tin đặt phòng đã được lưu!");
        alert.setContentText(thongTinDatPhong.toString());
        alert.showAndWait();

        updatePhongTrongList();
        tablePhongTrong.refresh();
        tablePhongDaChon.refresh();

        handleCancel();
    }

    public static void main(String[] args) {
        Stage stage = new Stage();
        DatphongUI datphongUI = new DatphongUI();
        stage.setScene(new Scene(datphongUI.getUI(), 1000, 600));
        stage.setTitle("Giao diện Đặt phòng");
        stage.show();
    }
}