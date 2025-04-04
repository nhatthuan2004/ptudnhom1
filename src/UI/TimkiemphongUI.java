package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.DichVu;
import model.HoaDon;
import model.Phong;
import model.ChuongTrinhKhuyenMai;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TimkiemphongUI {
    private final ObservableList<Phong> phongList;
    private final ObservableList<HoaDon> hoaDonList;
    private final ObservableList<DichVu> dichVuList;
    private final ObservableList<ChuongTrinhKhuyenMai> khuyenMaiList;
    private FlowPane roomFlowPane;
    private StackPane contentPane;
    private StackPane mainPane;
    private final List<HoaDon> hoaDonPhongList = new ArrayList<>(); // Thêm danh sách để quản lý hóa đơn phòng

    public TimkiemphongUI() {
        DataManager dataManager = DataManager.getInstance();
        this.phongList = dataManager.getPhongList();
        this.hoaDonList = dataManager.getHoaDonList();
        this.dichVuList = dataManager.getDichVuList();
        this.khuyenMaiList = dataManager.getKhuyenMaiList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
        dataManager.addPhongListChangeListener(this::updateRoomDisplayDirectly);
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: white;");

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

        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.TOP_CENTER);
        centerLayout.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label titleLabel = new Label("Tìm kiếm phòng theo mã phòng");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField searchField = new TextField();
        searchField.setPromptText("Nhập mã phòng (ví dụ: P101)");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);

        roomFlowPane = new FlowPane();
        roomFlowPane.setHgap(20);
        roomFlowPane.setVgap(20);
        roomFlowPane.setAlignment(Pos.CENTER);
        roomFlowPane.setPadding(new Insets(10));

        updateRoomDisplayDirectly();

        ScrollPane scrollPane = new ScrollPane(roomFlowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        centerLayout.getChildren().addAll(titleLabel, searchBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setCenter(centerLayout);
        layout.setPadding(new Insets(10));

        Runnable searchAction = () -> {
            String maPhong = searchField.getText().trim().toUpperCase();
            ObservableList<Phong> filteredList = FXCollections.observableArrayList();
            if (maPhong.isEmpty()) {
                filteredList.addAll(phongList);
            } else {
                for (Phong phong : phongList) {
                    if (phong.getMaPhong().toUpperCase().contains(maPhong)) {
                        filteredList.add(phong);
                    }
                }
                if (filteredList.isEmpty()) {
                    showAlert("Thông báo", "Không tìm thấy phòng nào với mã: " + maPhong);
                }
            }
            updateRoomDisplay(filteredList);
        };

        searchButton.setOnAction(e -> searchAction.run());
        searchField.setOnAction(e -> searchAction.run());

        mainPane.getChildren().addAll(layout, userInfoBox);
        return mainPane;
    }

    public StackPane getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    private void updateRoomDisplay(ObservableList<Phong> filteredList) {
        roomFlowPane.getChildren().clear();
        for (Phong phong : filteredList) {
            VBox roomBox = new VBox(8);
            roomBox.setPrefSize(160, 130);
            roomBox.setPadding(new Insets(10));
            roomBox.setAlignment(Pos.CENTER_LEFT);
            String bgColor = switch (phong.getTrangThai()) {
                case "Trống" -> "#90EE90";
                case "Đã đặt" -> "#FFD700";
                case "Đang sửa" -> "#FF6347";
                default -> "#E0E0E0";
            };
            roomBox.setStyle("-fx-background-color: " + bgColor
                    + "; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #666; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            roomBox.setOnMouseClicked(e -> showRoomDetails(phong));

            Label maPhongLabel = new Label("Phòng: " + phong.getMaPhong());
            maPhongLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            Label loaiPhongLabel = new Label("Loại: " + phong.getLoaiPhong());
            Label trangThaiLabel = new Label("Trạng thái: " + phong.getTrangThai());
            Label donDepLabel = new Label(
                    "Dọn dẹp: " + (phong.getMoTa().contains("Đã dọn dẹp") ? "Đã dọn dẹp" : "Chưa dọn dẹp"));

            maPhongLabel.setMaxWidth(140);
            loaiPhongLabel.setMaxWidth(140);
            trangThaiLabel.setMaxWidth(140);
            donDepLabel.setMaxWidth(140);

            roomBox.getChildren().addAll(maPhongLabel, loaiPhongLabel, trangThaiLabel, donDepLabel);
            roomFlowPane.getChildren().add(roomBox);
        }
    }

    private void updateRoomDisplayDirectly() {
        updateRoomDisplay(phongList);
    }

    private void showRoomDetails(Phong phong) {
        VBox form = createCenteredForm("Chi tiết phòng " + phong.getMaPhong());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label khachHangLabel = new Label("Thông tin người đặt:");
        khachHangLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        Label tenKhachHang = new Label(
                "Tên: " + (phong.getTenKhachHang().isEmpty() ? "Chưa có" : phong.getTenKhachHang()));
        Label soDienThoai = new Label(
                "SĐT: " + (phong.getSoDienThoai().isEmpty() ? "Chưa có" : phong.getSoDienThoai()));

        Label phongLabel = new Label("Thông tin phòng:");
        phongLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        Label maPhong = new Label("Mã phòng: " + phong.getMaPhong());
        Label loaiPhong = new Label("Loại phòng: " + phong.getLoaiPhong());
        Label trangThai = new Label("Trạng thái: " + phong.getTrangThai());
        Label donDep = new Label(
                "Dọn dẹp: " + (phong.getMoTa().contains("Đã dọn dẹp") ? "Đã dọn dẹp" : "Chưa dọn dẹp"));

        Label dichVuLabel = new Label("Dịch vụ sử dụng:");
        dichVuLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        VBox dichVuBox = new VBox(5);
        if (phong.getDanhSachDichVu().isEmpty()) {
            dichVuBox.getChildren().add(new Label("Chưa có dịch vụ nào."));
        } else {
            for (DichVu dv : phong.getDanhSachDichVu()) {
                dichVuBox.getChildren()
                        .add(new Label(dv.getTenDichVu() + " - " + String.format("%,.0f VNĐ", dv.getGia())));
            }
        }

        content.getChildren().addAll(khachHangLabel, tenKhachHang, soDienThoai, phongLabel, maPhong, loaiPhong,
                trangThai, donDep, dichVuLabel, dichVuBox);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button btnEdit = new Button("Sửa");
        btnEdit.setStyle(
                "-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnEdit.setOnAction(e -> showEditRoomDialog(phong));

        Button btnDong = new Button("Đóng");
        btnDong.setStyle(
                "-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        buttons.getChildren().addAll(btnEdit, btnDong);

        if ("Đã đặt".equals(phong.getTrangThai())) {
            Button btnAddService = new Button("Thêm dịch vụ");
            btnAddService.setStyle(
                    "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
            btnAddService.setOnAction(e -> showAddServiceDialog(phong));

            Button btnThanhToan = new Button("Thanh toán");
            btnThanhToan.setStyle(
                    "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
            btnThanhToan.setOnAction(e -> showThanhToanForm(phong));

            buttons.getChildren().addAll(btnAddService, btnThanhToan);
        }

        form.getChildren().addAll(content, buttons);
        contentPane.getChildren().setAll(form);
    }

    private void showEditRoomDialog(Phong phong) {
        VBox form = createCenteredForm("Chỉnh sửa phòng " + phong.getMaPhong());

        TextField maPhongField = new TextField(phong.getMaPhong());
        maPhongField.setDisable(true);
        ComboBox<String> loaiPhongCombo = new ComboBox<>(FXCollections.observableArrayList("Đơn", "Đôi", "VIP"));
        loaiPhongCombo.setValue(phong.getLoaiPhong());
        ComboBox<String> trangThaiCombo = new ComboBox<>(
                FXCollections.observableArrayList("Trống", "Đã đặt", "Đang sửa"));
        trangThaiCombo.setValue(phong.getTrangThai());
        ComboBox<String> donDepCombo = new ComboBox<>(FXCollections.observableArrayList("Đã dọn dẹp", "Chưa dọn dẹp"));
        donDepCombo.setValue(phong.getMoTa().contains("Đã dọn dẹp") ? "Đã dọn dẹp" : "Chưa dọn dẹp");
        TextField tenKhachHangField = new TextField(phong.getTenKhachHang());
        TextField soDienThoaiField = new TextField(phong.getSoDienThoai());
        DatePicker ngayDenPicker = new DatePicker();
        DatePicker ngayDiPicker = new DatePicker();

        final HoaDon hoaDonPhongInitial = getHoaDonPhong(phong); // Thêm final
        if (hoaDonPhongInitial != null && "Đã đặt".equals(phong.getTrangThai())) {
            String moTa = hoaDonPhongInitial.getMoTa();
            String[] parts = moTa.split(" từ | đến ");
            if (parts.length >= 3) {
                ngayDenPicker.setValue(LocalDate.parse(parts[1].split(" ")[0]));
                ngayDiPicker.setValue(LocalDate.parse(parts[2].split(" ")[0]));
            } else {
                ngayDenPicker.setValue(LocalDate.now());
                ngayDiPicker.setValue(LocalDate.now().plusDays(1));
            }
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Mã Phòng:"), maPhongField);
        grid.addRow(1, new Label("Loại Phòng:"), loaiPhongCombo);
        grid.addRow(2, new Label("Trạng Thái:"), trangThaiCombo);
        grid.addRow(3, new Label("Dọn Dẹp:"), donDepCombo);
        grid.addRow(4, new Label("Tên Khách Hàng:"), tenKhachHangField);
        grid.addRow(5, new Label("SĐT:"), soDienThoaiField);

        if ("Đã đặt".equals(trangThaiCombo.getValue())) {
            grid.addRow(6, new Label("Ngày Đến:"), ngayDenPicker);
            grid.addRow(7, new Label("Ngày Đi:"), ngayDiPicker);
        }

        trangThaiCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= 6);
            if ("Đã đặt".equals(newVal)) {
                if (ngayDenPicker.getValue() == null)
                    ngayDenPicker.setValue(LocalDate.now());
                if (ngayDiPicker.getValue() == null)
                    ngayDiPicker.setValue(LocalDate.now().plusDays(1));
                grid.addRow(6, new Label("Ngày Đến:"), ngayDenPicker);
                grid.addRow(7, new Label("Ngày Đi:"), ngayDiPicker);
            }
        });

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnLuu.setOnAction(e -> {
            String tenKhachHang = tenKhachHangField.getText();
            String soDienThoai = soDienThoaiField.getText();
            String trangThai = trangThaiCombo.getValue();
            String donDep = donDepCombo.getValue();

            if (trangThai == null || donDep == null || loaiPhongCombo.getValue() == null) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin phòng!");
                return;
            }

            if ("Đã đặt".equals(trangThai)) {
                if (tenKhachHang.isEmpty() || soDienThoai.isEmpty()) {
                    showAlert("Lỗi", "Vui lòng nhập thông tin khách hàng khi phòng đã đặt!");
                    return;
                }
                if (!soDienThoai.matches("0\\d{9}")) {
                    showAlert("Lỗi", "Số điện thoại phải bắt đầu bằng 0 và gồm 10 chữ số!");
                    return;
                }
                if (ngayDenPicker.getValue() == null || ngayDiPicker.getValue() == null) {
                    showAlert("Lỗi", "Vui lòng chọn ngày đến và ngày đi!");
                    return;
                }
                if (ngayDenPicker.getValue().isAfter(ngayDiPicker.getValue())) {
                    showAlert("Lỗi", "Ngày đến phải trước ngày đi!");
                    return;
                }
            }

            int index = phongList.indexOf(phong);
            double giaPhong = switch (loaiPhongCombo.getValue()) {
                case "Đơn" -> 200000;
                case "Đôi" -> 350000;
                case "VIP" -> 500000;
                default -> phong.getGiaPhong();
            };
            Phong updatedPhong = new Phong(phong.getMaPhong(), loaiPhongCombo.getValue(), giaPhong, trangThai,
                    phong.getViTri(), phong.getSoNguoiToiDa(), donDep);
            updatedPhong.setTenKhachHang(tenKhachHang);
            updatedPhong.setSoDienThoai(soDienThoai);
            updatedPhong.setDanhSachDichVu(phong.getDanhSachDichVu());
            phongList.set(index, updatedPhong);

            HoaDon hoaDonPhong = getHoaDonPhong(updatedPhong); // Biến cục bộ trong lambda
            if ("Đã đặt".equals(trangThai)) {
                long soNgayO = ChronoUnit.DAYS.between(ngayDenPicker.getValue(), ngayDiPicker.getValue());
                double tienPhong = giaPhong * Math.max(1, soNgayO);
                if (hoaDonPhong == null) {
                    hoaDonPhong = new HoaDon(
                        "HD" + (hoaDonList.size() + 1),
                        tenKhachHang,
                        updatedPhong.getMaPhong(),
                        tienPhong,
                        0.0,
                        "Chưa thanh toán",
                        updatedPhong.getMaPhong(),
                        LocalDateTime.now(),
                        "Phòng " + updatedPhong.getMaPhong() + " từ " + ngayDenPicker.getValue() + " đến " + ngayDiPicker.getValue(),
                        "Tiền mặt",
                        "KH001",
                        "NV001"
                    );
                    hoaDonPhongList.add(hoaDonPhong);
                    hoaDonList.add(hoaDonPhong);
                } else {
                    hoaDonPhongList.set(hoaDonPhongList.indexOf(hoaDonPhong), new HoaDon(
                        hoaDonPhong.getMaHoaDon(),
                        tenKhachHang,
                        updatedPhong.getMaPhong(),
                        tienPhong,
                        hoaDonPhong.getTienDichVu(),
                        "Chưa thanh toán",
                        updatedPhong.getMaPhong(),
                        hoaDonPhong.getNgayLap(),
                        "Phòng " + updatedPhong.getMaPhong() + " từ " + ngayDenPicker.getValue() + " đến " + ngayDiPicker.getValue(),
                        hoaDonPhong.getHinhThucThanhToan(),
                        hoaDonPhong.getMaKhachHang(),
                        hoaDonPhong.getMaNhanVien()
                    ));
                    hoaDonList.set(hoaDonList.indexOf(hoaDonPhong), hoaDonPhongList.get(hoaDonPhongList.indexOf(hoaDonPhong)));
                }
            } else if (hoaDonPhong != null && "Chưa thanh toán".equals(hoaDonPhong.getTrangThai())) {
                hoaDonPhongList.remove(hoaDonPhong);
                hoaDonList.remove(hoaDonPhong);
            }
            updateRoomDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnXoa = new Button("Xóa");
        btnXoa.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnXoa.setOnAction(e -> {
            HoaDon hoaDonPhongToRemove = getHoaDonPhong(phong);
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận xóa");
            confirm.setHeaderText("Bạn có chắc muốn xóa phòng này?");
            confirm.setContentText("Phòng: " + phong.getMaPhong());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (hoaDonPhongToRemove != null && "Chưa thanh toán".equals(hoaDonPhongToRemove.getTrangThai())) {
                        hoaDonPhongList.remove(hoaDonPhongToRemove);
                        hoaDonList.remove(hoaDonPhongToRemove);
                    }
                    phongList.remove(phong);
                    updateRoomDisplayDirectly();
                    contentPane.getChildren().setAll(mainPane);
                }
            });
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(10, btnLuu, btnXoa, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }

    private void showAddServiceDialog(Phong phong) {
        VBox form = createCenteredForm("Thêm dịch vụ cho phòng " + phong.getMaPhong());

        ObservableList<DichVu> dichVuHoatDong = dichVuList.filtered(dv -> "Hoạt động".equals(dv.getTrangThai()));
        TableView<DichVu> table = new TableView<>(dichVuHoatDong);
        table.setPrefHeight(200);
        table.setPrefWidth(450);

        TableColumn<DichVu, String> tenDichVuCol = new TableColumn<>("Tên Dịch Vụ");
        tenDichVuCol.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        tenDichVuCol.setPrefWidth(150);
        TableColumn<DichVu, Double> giaCol = new TableColumn<>("Giá (VNĐ)");
        giaCol.setCellValueFactory(new PropertyValueFactory<>("gia"));
        giaCol.setPrefWidth(100);
        TableColumn<DichVu, String> moTaCol = new TableColumn<>("Mô Tả");
        moTaCol.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        moTaCol.setPrefWidth(200);

        table.getColumns().addAll(tenDichVuCol, giaCol, moTaCol);

        Label instructionLabel = new Label("Chọn một dịch vụ từ danh sách:");

        Button btnThem = new Button("Thêm");
        btnThem.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnThem.setOnAction(e -> {
            DichVu selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Lỗi", "Vui lòng chọn một dịch vụ!");
                return;
            }

            DichVu dichVuMoi = new DichVu(
                "DV" + (phong.getDanhSachDichVu().size() + 1),
                selected.getTenDichVu(),
                selected.getGia(),
                selected.getMoTa(),
                selected.getTrangThai()
            );
            phong.addDichVu(dichVuMoi);

            HoaDon hoaDonPhong = getHoaDonPhong(phong);
            if (hoaDonPhong == null) {
                hoaDonPhong = new HoaDon(
                    "HD" + (hoaDonList.size() + 1),
                    phong.getTenKhachHang().isEmpty() ? "Khách hàng mặc định" : phong.getTenKhachHang(),
                    phong.getMaPhong(),
                    phong.getGiaPhong(),
                    dichVuMoi.getGia(),
                    "Chưa thanh toán",
                    phong.getMaPhong(),
                    LocalDateTime.now(),
                    "Phòng " + phong.getMaPhong() + " từ " + LocalDate.now() + " đến " + LocalDate.now().plusDays(1) + ", " + dichVuMoi.getTenDichVu() + " (" + String.format("%,.0f VNĐ", dichVuMoi.getGia()) + ")",
                    "Tiền mặt",
                    "KH001",
                    "NV001"
                );
                hoaDonPhongList.add(hoaDonPhong);
                hoaDonList.add(hoaDonPhong);
            } else {
                double tienDichVuHienTai = hoaDonPhong.getTienDichVu();
                hoaDonPhongList.set(hoaDonPhongList.indexOf(hoaDonPhong), new HoaDon(
                    hoaDonPhong.getMaHoaDon(),
                    hoaDonPhong.getTenKhachHang(),
                    hoaDonPhong.getMaPhong(),
                    hoaDonPhong.getTienPhong(),
                    tienDichVuHienTai + dichVuMoi.getGia(),
                    "Chưa thanh toán",
                    hoaDonPhong.getMaPhong(),
                    hoaDonPhong.getNgayLap(),
                    hoaDonPhong.getMoTa() + ", " + dichVuMoi.getTenDichVu() + " (" + String.format("%,.0f VNĐ", dichVuMoi.getGia()) + ")",
                    hoaDonPhong.getHinhThucThanhToan(),
                    hoaDonPhong.getMaKhachHang(),
                    hoaDonPhong.getMaNhanVien()
                ));
                hoaDonList.set(hoaDonList.indexOf(hoaDonPhong), hoaDonPhongList.get(hoaDonPhongList.indexOf(hoaDonPhong)));
            }

            int index = phongList.indexOf(phong);
            phongList.set(index, phong);
            updateRoomDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(10, btnThem, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(instructionLabel, table, footer);
        contentPane.getChildren().setAll(form);
    }

    private void showThanhToanForm(Phong phong) {
        final HoaDon hoaDonPhong = getHoaDonPhong(phong); // Thêm final và dùng phương thức getHoaDonPhong
        if (hoaDonPhong == null) {
            showAlert("Lỗi", "Không tìm thấy hóa đơn cho phòng " + phong.getMaPhong());
            return;
        }

        double tienPhong = hoaDonPhong.getTienPhong();
        double tienDichVu = hoaDonPhong.getTienDichVu();
        double tongTien = tienPhong + tienDichVu;
        String moTaBanDau = hoaDonPhong.getMoTa();

        VBox form = createCenteredForm("Thanh toán phòng " + phong.getMaPhong());

        ComboBox<ChuongTrinhKhuyenMai> promotionCombo = new ComboBox<>(khuyenMaiList.filtered(km -> km.isTrangThai()));
        promotionCombo.setPromptText("Chọn mã khuyến mãi (nếu có)");
        promotionCombo.setConverter(new javafx.util.StringConverter<ChuongTrinhKhuyenMai>() {
            @Override
            public String toString(ChuongTrinhKhuyenMai km) {
                return km != null ? km.getMaChuongTrinhKhuyenMai() + " - " + km.getTenChuongTrinhKhuyenMai() : "";
            }

            @Override
            public ChuongTrinhKhuyenMai fromString(String string) {
                return null;
            }
        });

        Label discountLabel = new Label("Giảm giá: 0 VNĐ");
        Label finalTotalLabel = new Label("Tổng tiền cuối: " + String.format("%,.0f VNĐ", tongTien));

        double[] finalTotal = { tongTien };
        String[] finalMoTa = { moTaBanDau };

        promotionCombo.setOnAction(e -> {
            ChuongTrinhKhuyenMai selected = promotionCombo.getValue();
            double discount = 0;
            if (selected != null) {
                discount = tongTien * (selected.getChietKhau() / 100);
                finalTotal[0] = tongTien - discount;
                discountLabel.setText("Giảm giá: " + String.format("%,.0f VNĐ", discount));
                finalTotalLabel.setText("Tổng tiền cuối: " + String.format("%,.0f VNĐ", finalTotal[0]));
                finalMoTa[0] = moTaBanDau + " - Sử dụng " + selected.getMaChuongTrinhKhuyenMai() + " (Giảm "
                        + String.format("%,.0f VNĐ", discount) + ")";
            } else {
                finalTotal[0] = tongTien;
                discountLabel.setText("Giảm giá: 0 VNĐ");
                finalTotalLabel.setText("Tổng tiền cuối: " + String.format("%,.0f VNĐ", tongTien));
                finalMoTa[0] = moTaBanDau;
            }
        });

        VBox paymentDetails = new VBox(10);
        paymentDetails.getChildren().addAll(new Label("Tiền phòng: " + String.format("%,.0f VNĐ", tienPhong)),
                new Label("Tiền dịch vụ: " + String.format("%,.0f VNĐ", tienDichVu)),
                new Label("Tổng tiền gốc: " + String.format("%,.0f VNĐ", tongTien)), promotionCombo, discountLabel,
                finalTotalLabel, new Label("Chọn phương thức thanh toán:"));

        Button btnTienMat = new Button("Tiền mặt");
        btnTienMat.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnTienMat.setOnAction(e -> {
            completePayment(phong, hoaDonPhong, finalTotal[0], finalMoTa[0], "Tiền mặt");
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnOnline = new Button("Online");
        btnOnline.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnOnline.setOnAction(e -> {
            VBox onlineForm = createCenteredForm("Thanh toán Online");
            Image qrImage = new Image(getClass().getResourceAsStream("/img/QRcode.jpg"), 150, 150, true, true);
            ImageView qrView = new ImageView(qrImage);
            Label qrLabel = new Label("Quét mã QR để thanh toán " + String.format("%,.0f VNĐ", finalTotal[0]));

            Button btnXacNhan = new Button("Xác nhận đã thu");
            btnXacNhan.setStyle(
                    "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
            btnXacNhan.setOnAction(ev -> {
                completePayment(phong, hoaDonPhong, finalTotal[0], finalMoTa[0], "Online");
                contentPane.getChildren().setAll(mainPane);
            });

            Button btnHuyOnline = new Button("Hủy");
            btnHuyOnline.setStyle(
                    "-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
            btnHuyOnline.setOnAction(ev -> contentPane.getChildren().setAll(mainPane));

            HBox onlineFooter = new HBox(10, btnXacNhan, btnHuyOnline);
            onlineFooter.setAlignment(Pos.CENTER);

            onlineForm.getChildren().addAll(qrView, qrLabel, onlineFooter);
            contentPane.getChildren().setAll(onlineForm);
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(10, btnTienMat, btnOnline, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(paymentDetails, footer);
        contentPane.getChildren().setAll(form);
    }

    private void completePayment(Phong phong, HoaDon hoaDonPhong, double tongTien, String moTa, String hinhThucThanhToan) {
        int index = hoaDonPhongList.indexOf(hoaDonPhong);
        if (index != -1) {
            hoaDonPhongList.set(index, new HoaDon(
                hoaDonPhong.getMaHoaDon(),
                hoaDonPhong.getTenKhachHang(),
                hoaDonPhong.getMaPhong(),
                hoaDonPhong.getTienPhong(),
                hoaDonPhong.getTienDichVu(),
                "Đã thanh toán",
                hoaDonPhong.getMaPhong(),
                hoaDonPhong.getNgayLap(),
                moTa,
                hinhThucThanhToan,
                hoaDonPhong.getMaKhachHang(),
                hoaDonPhong.getMaNhanVien()
            ));
            hoaDonList.set(hoaDonList.indexOf(hoaDonPhong), hoaDonPhongList.get(index));
        }

        int phongIndex = phongList.indexOf(phong);
        Phong newPhong = new Phong(phong.getMaPhong(), phong.getLoaiPhong(), phong.getGiaPhong(), "Trống",
                phong.getViTri(), phong.getSoNguoiToiDa(), "Chưa dọn dẹp");
        phongList.set(phongIndex, newPhong);
        updateRoomDisplayDirectly();
        showAlert("Thành công", "Thanh toán thành công bằng " + hinhThucThanhToan + ".\nTổng tiền: " + String.format("%,.0f VNĐ", tongTien));
    }

    private HoaDon getHoaDonPhong(Phong phong) {
        return hoaDonPhongList.stream()
                .filter(hd -> hd.getMaPhong().equals(phong.getMaPhong()) && "Chưa thanh toán".equals(hd.getTrangThai()))
                .findFirst()
                .orElse(null);
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(500);
        form.setMaxHeight(600);

        Label title = new Label(titleText);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}