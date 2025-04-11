package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class QLdatphongUI {
    private final ObservableList<Phong> phongList;
    private final ObservableList<HoaDon> hoaDonList;
    private final ObservableList<KhachHang> khachHangList;
    private final ObservableList<DichVu> dichVuList;
    private final ObservableList<KhuyenMai> khuyenMaiList;
    private FlowPane bookingFlowPane;
    private StackPane contentPane;
    private StackPane mainPane;

    private DatePicker dpNgay;
    private ComboBox<String> cbGio;
    private ComboBox<String> cbTrangThaiPhong;
    private CheckBox showAllCheckBox;

    public QLdatphongUI() {
        DataManager dataManager = DataManager.getInstance();
        this.phongList = dataManager.getPhongList();
        this.hoaDonList = dataManager.getHoaDonList();
        this.khachHangList = dataManager.getKhachHangList();
        this.dichVuList = dataManager.getDichVuList();
        this.khuyenMaiList = dataManager.getKhuyenMaiList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
        dataManager.addPhongListChangeListener(this::updateBookingDisplayDirectly);
        dataManager.addHoaDonListChangeListener(this::updateBookingDisplayDirectly);
    }

    private StackPane createMainPane() {
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
        layout.setTop(userInfoBox);
        BorderPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        BorderPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        // Bên trái: infoPane
        VBox infoPane = new VBox(15); // Giảm spacing từ 20 xuống 15
        infoPane.setPadding(new Insets(15, 25, 25, 25));
        infoPane.setAlignment(Pos.TOP_LEFT);
        infoPane.setPrefWidth(300); // Giảm từ 350 xuống 300

        double labelWidth = 140;

        Label labelNgay = new Label("Ngày:");
        labelNgay.setMinWidth(labelWidth);
        dpNgay = new DatePicker(LocalDate.now());
        dpNgay.setPrefWidth(250); // Giảm từ 300 xuống 250
        dpNgay.setStyle("-fx-font-size: 14px;");
        HBox hboxNgay = new HBox(15, labelNgay, dpNgay);
        hboxNgay.setAlignment(Pos.CENTER_LEFT);

        Label labelGio = new Label("Giờ:");
        labelGio.setMinWidth(labelWidth);
        cbGio = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            cbGio.getItems().add(String.format("%02d:00", i));
        }
        cbGio.setPromptText("Chọn giờ");
        cbGio.setPrefWidth(250); // Giảm từ 300 xuống 250
        cbGio.setStyle("-fx-font-size: 14px;");
        HBox hboxGio = new HBox(15, labelGio, cbGio);
        hboxGio.setAlignment(Pos.CENTER_LEFT);

        Label labelTrangThaiPhong = new Label("Trạng thái hóa đơn:");
        labelTrangThaiPhong.setMinWidth(labelWidth);
        cbTrangThaiPhong = new ComboBox<>();
        cbTrangThaiPhong.getItems().addAll("Chưa thanh toán", "Đã thanh toán");
        cbTrangThaiPhong.setPromptText("Chọn trạng thái");
        cbTrangThaiPhong.setPrefWidth(250); // Giảm từ 300 xuống 250
        cbTrangThaiPhong.setStyle("-fx-font-size: 14px;");
        HBox hboxTrangThaiPhong = new HBox(15, labelTrangThaiPhong, cbTrangThaiPhong);
        hboxTrangThaiPhong.setAlignment(Pos.CENTER_LEFT);

        Button addBookingButton = new Button("Thêm Phiếu Đặt");
        addBookingButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
        addBookingButton.setPrefWidth(200); // Tăng từ 150 lên 200
        addBookingButton.setOnAction(e -> showAddBookingDialog());
        HBox hboxAddBooking = new HBox(15, new Label(""), addBookingButton);
        hboxAddBooking.setAlignment(Pos.CENTER_LEFT);

        showAllCheckBox = new CheckBox("Hiển thị tất cả phiếu đặt");
        showAllCheckBox.setStyle("-fx-font-size: 14px;");
        showAllCheckBox.setOnAction(e -> filterBookings());
        HBox hboxShowAll = new HBox(15, new Label(""), showAllCheckBox);
        hboxShowAll.setAlignment(Pos.CENTER_LEFT);

        infoPane.getChildren().addAll(hboxNgay, hboxGio, hboxTrangThaiPhong, hboxAddBooking, hboxShowAll);

        // Bên phải: bookingPane
        VBox bookingPane = new VBox(10);
        bookingPane.setPadding(new Insets(10, 20, 20, 20));
        bookingPane.setAlignment(Pos.TOP_CENTER);
        bookingPane.setPrefWidth(550); // Giảm từ 600 xuống 550

        bookingFlowPane = new FlowPane();
        bookingFlowPane.setHgap(15);
        bookingFlowPane.setVgap(15);
        bookingFlowPane.setAlignment(Pos.CENTER);
        bookingFlowPane.setPrefWidth(530); // Giảm từ 580 xuống 530

        cbTrangThaiPhong.setOnAction(e -> filterBookings());
        dpNgay.setOnAction(e -> filterBookings());
        cbGio.setOnAction(e -> filterBookings());

        updateBookingDisplayDirectly();

        ScrollPane scrollPane = new ScrollPane(bookingFlowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefViewportHeight(500);

        bookingPane.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        GridPane gridLayout = new GridPane();
        gridLayout.setPadding(new Insets(10));
        gridLayout.setHgap(25); // Tăng từ 20 lên 25
        gridLayout.add(infoPane, 0, 0);
        gridLayout.add(bookingPane, 1, 0);
        GridPane.setValignment(infoPane, VPos.TOP);
        GridPane.setValignment(bookingPane, VPos.TOP);
        GridPane.setHgrow(bookingPane, Priority.ALWAYS);

        VBox layoutMain = new VBox(10, gridLayout);
        layoutMain.setAlignment(Pos.TOP_CENTER);

        layout.setCenter(layoutMain);
        root.getChildren().add(layout);
        return root;
    }

    public StackPane getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    private void filterBookings() {
        ObservableList<HoaDon> filteredList = FXCollections.observableArrayList();
        String trangThai = cbTrangThaiPhong.getValue();

        for (HoaDon hoaDon : hoaDonList) {
            boolean matches = true;
            if (trangThai != null && !hoaDon.getTrangThai().equals(trangThai))
                matches = false;
            if (matches || showAllCheckBox.isSelected())
                filteredList.add(hoaDon);
        }
        updateBookingDisplay(filteredList);
    }

    private void updateBookingDisplay(ObservableList<HoaDon> displayList) {
        bookingFlowPane.getChildren().clear();
        for (HoaDon hoaDon : displayList) {
            VBox bookingBox = new VBox(8);
            bookingBox.setPrefSize(180, 150); // Tăng từ 160, 130 lên 180, 150
            bookingBox.setPadding(new Insets(8));
            bookingBox.setAlignment(Pos.CENTER_LEFT);
            String bgColor = "Chưa thanh toán".equals(hoaDon.getTrangThai()) ? "#FFD700" : "#90EE90";
            bookingBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #666; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            bookingBox.setOnMouseClicked(e -> showBookingDetailsDialog(hoaDon));

            Label maHoaDonLabel = new Label("Hóa đơn: " + hoaDon.getMaHoaDon());
            maHoaDonLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"); // Tăng từ 13px lên 14px
            Label maPhongLabel = new Label("Phòng: " + hoaDon.getMaPhong());
            maPhongLabel.setStyle("-fx-font-size: 14px;"); // Tăng từ mặc định lên 14px
            Label tenKhachHangLabel = new Label("Khách: " + hoaDon.getTenKhachHang());
            tenKhachHangLabel.setStyle("-fx-font-size: 14px;"); // Tăng từ mặc định lên 14px
            Label trangThaiLabel = new Label("Trạng thái: " + hoaDon.getTrangThai());
            trangThaiLabel.setStyle("-fx-font-size: 14px;"); // Tăng từ mặc định lên 14px

            maHoaDonLabel.setMaxWidth(160); // Tăng từ 140 lên 160
            maPhongLabel.setMaxWidth(160);
            tenKhachHangLabel.setMaxWidth(160);
            trangThaiLabel.setMaxWidth(160);

            bookingBox.getChildren().addAll(maHoaDonLabel, maPhongLabel, tenKhachHangLabel, trangThaiLabel);
            bookingFlowPane.getChildren().add(bookingBox);
        }
    }

    private void updateBookingDisplayDirectly() {
        updateBookingDisplay(hoaDonList);
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(600);
        form.setMaxHeight(500);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showAddBookingDialog() {
        VBox form = createCenteredForm("Thêm Phiếu Đặt Mới");

        ComboBox<String> maPhongCombo = new ComboBox<>();
        phongList.stream().filter(p -> "Trống".equals(p.getTrangThai())).forEach(p -> maPhongCombo.getItems().add(p.getMaPhong()));
        maPhongCombo.setPromptText("Chọn mã phòng");
        maPhongCombo.setPrefWidth(250);
        maPhongCombo.setStyle("-fx-font-size: 16px;");
        TextField tenKhachHangField = new TextField();
        tenKhachHangField.setPromptText("Tên khách hàng");
        tenKhachHangField.setPrefWidth(250);
        tenKhachHangField.setStyle("-fx-font-size: 16px;");
        DatePicker ngayDenPicker = new DatePicker(LocalDate.now());
        ngayDenPicker.setPrefWidth(250);
        ngayDenPicker.setStyle("-fx-font-size: 16px;");
        DatePicker ngayDiPicker = new DatePicker(LocalDate.now().plusDays(1));
        ngayDiPicker.setPrefWidth(250);
        ngayDiPicker.setStyle("-fx-font-size: 16px;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Mã Phòng:"), maPhongCombo);
        grid.addRow(1, new Label("Tên Khách Hàng:"), tenKhachHangField);
        grid.addRow(2, new Label("Ngày Đến:"), ngayDenPicker);
        grid.addRow(3, new Label("Ngày Đi:"), ngayDiPicker);

        Button btnThem = new Button("Thêm");
        btnThem.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnThem.setPrefWidth(120);
        btnThem.setOnAction(e -> {
            String maPhong = maPhongCombo.getValue();
            String tenKhachHang = tenKhachHangField.getText();
            LocalDate ngayDen = ngayDenPicker.getValue();
            LocalDate ngayDi = ngayDiPicker.getValue();

            if (maPhong == null || tenKhachHang.isEmpty() || ngayDen == null || ngayDi == null) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            if (ngayDen.isAfter(ngayDi)) {
                showAlert("Lỗi", "Ngày đến phải trước ngày đi!");
                return;
            }

            Phong phong = phongList.stream().filter(p -> p.getMaPhong().equals(maPhong)).findFirst().orElse(null);
            if (phong == null) {
                showAlert("Lỗi", "Phòng không tồn tại!");
                return;
            }

            long soNgayO = ChronoUnit.DAYS.between(ngayDen, ngayDi);
            double tienPhong = phong.getGiaPhong() * Math.max(1, soNgayO);
            HoaDon newHoaDon = new HoaDon(
                    "HD" + (hoaDonList.size() + 1), tenKhachHang, maPhong, tienPhong, 0.0, "Chưa thanh toán",
                    maPhong, LocalDateTime.now(), "Phòng " + maPhong + " từ " + ngayDen + " đến " + ngayDi,
                    "Tiền mặt", khachHangList.isEmpty() ? "KH001" : khachHangList.get(khachHangList.size() - 1).getMaKhachHang(), "NV001"
            );
            hoaDonList.add(newHoaDon);

            int index = phongList.indexOf(phong);
            Phong updatedPhong = new Phong(phong.getMaPhong(), phong.getLoaiPhong(), phong.getGiaPhong(), "Đã đặt", phong.getViTri(), tenKhachHang, phong.getSoNguoiToiDa(), phong.getMoTa());
            phongList.set(index, updatedPhong);

            updateBookingDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnHuy.setPrefWidth(120);
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(15, btnThem, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }

    private void showBookingDetailsDialog(HoaDon hoaDon) {
        VBox form = createCenteredForm("Chi tiết phiếu đặt phòng " + hoaDon.getMaHoaDon());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label maHoaDonLabel = new Label("Mã hóa đơn: " + hoaDon.getMaHoaDon());
        Label maPhongLabel = new Label("Mã phòng: " + hoaDon.getMaPhong());
        Label tenKhachHangLabel = new Label("Tên khách hàng: " + hoaDon.getTenKhachHang());
        Label trangThaiLabel = new Label("Trạng thái: " + hoaDon.getTrangThai());
        Label moTaLabel = new Label("Mô tả: " + hoaDon.getMoTa());
        Label tienPhongLabel = new Label("Tiền phòng: " + hoaDon.getTienPhong());
        Label tienDichVuLabel = new Label("Tiền dịch vụ: " + hoaDon.getTienDichVu());

        content.getChildren().addAll(maHoaDonLabel, maPhongLabel, tenKhachHangLabel, trangThaiLabel, moTaLabel, tienPhongLabel, tienDichVuLabel);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button btnSua = new Button("Sửa");
        btnSua.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnSua.setPrefWidth(120);
        btnSua.setOnAction(e -> showEditBookingDialog(hoaDon));

        Button btnThanhToan = new Button("Thanh toán");
        btnThanhToan.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnThanhToan.setPrefWidth(120);
        btnThanhToan.setOnAction(e -> thanhToanHoaDon(hoaDon));
        btnThanhToan.setDisable("Đã thanh toán".equals(hoaDon.getTrangThai()));

        Button btnThemDichVu = new Button("Thêm dịch vụ");
        btnThemDichVu.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnThemDichVu.setPrefWidth(120);
        btnThemDichVu.setOnAction(e -> themDichVuHoaDon(hoaDon));
        btnThemDichVu.setDisable("Đã thanh toán".equals(hoaDon.getTrangThai()));

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnDong.setPrefWidth(120);
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        buttons.getChildren().addAll(btnSua, btnThanhToan, btnThemDichVu, btnDong);

        form.getChildren().addAll(content, buttons);
        contentPane.getChildren().setAll(form);
    }

    private void showEditBookingDialog(HoaDon hoaDon) {
        VBox form = createCenteredForm("Chỉnh sửa phiếu đặt phòng " + hoaDon.getMaHoaDon());

        TextField maHoaDonField = new TextField(hoaDon.getMaHoaDon());
        maHoaDonField.setDisable(true);
        maHoaDonField.setPrefWidth(250);
        maHoaDonField.setStyle("-fx-font-size: 16px;");
        TextField tenKhachHangField = new TextField(hoaDon.getTenKhachHang());
        tenKhachHangField.setPrefWidth(250);
        tenKhachHangField.setStyle("-fx-font-size: 16px;");
        DatePicker ngayDenPicker = new DatePicker();
        ngayDenPicker.setPrefWidth(250);
        ngayDenPicker.setStyle("-fx-font-size: 16px;");
        DatePicker ngayDiPicker = new DatePicker();
        ngayDiPicker.setPrefWidth(250);
        ngayDiPicker.setStyle("-fx-font-size: 16px;");

        String moTa = hoaDon.getMoTa();
        String[] parts = moTa.split(" từ | đến ");
        if (parts.length >= 3) {
            ngayDenPicker.setValue(LocalDate.parse(parts[1]));
            ngayDiPicker.setValue(LocalDate.parse(parts[2]));
        }

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Mã Hóa Đơn:"), maHoaDonField);
        grid.addRow(1, new Label("Tên Khách Hàng:"), tenKhachHangField);
        grid.addRow(2, new Label("Ngày Đến:"), ngayDenPicker);
        grid.addRow(3, new Label("Ngày Đi:"), ngayDiPicker);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnLuu.setPrefWidth(120);
        btnLuu.setOnAction(e -> {
            String tenKhachHang = tenKhachHangField.getText();
            LocalDate ngayDen = ngayDenPicker.getValue();
            LocalDate ngayDi = ngayDiPicker.getValue();

            if (tenKhachHang.isEmpty() || ngayDen == null || ngayDi == null) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            if (ngayDen.isAfter(ngayDi)) {
                showAlert("Lỗi", "Ngày đến phải trước ngày đi!");
                return;
            }

            Phong phong = phongList.stream().filter(p -> p.getMaPhong().equals(hoaDon.getMaPhong())).findFirst().orElse(null);
            if (phong == null) {
                showAlert("Lỗi", "Phòng không tồn tại!");
                return;
            }

            long soNgayO = ChronoUnit.DAYS.between(ngayDen, ngayDi);
            double tienPhong = phong.getGiaPhong() * Math.max(1, soNgayO);
            HoaDon updatedHoaDon = new HoaDon(hoaDon.getMaHoaDon(), tenKhachHang, hoaDon.getMaPhong(), tienPhong, hoaDon.getTienDichVu(), hoaDon.getTrangThai(),
                    hoaDon.getMaPhong(), hoaDon.getNgayLap(), "Phòng " + hoaDon.getMaPhong() + " từ " + ngayDen + " đến " + ngayDi,
                    hoaDon.getHinhThucThanhToan(), hoaDon.getMaKhachHang(), hoaDon.getMaNhanVien());
            int index = hoaDonList.indexOf(hoaDon);
            hoaDonList.set(index, updatedHoaDon);

            updateBookingDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnXoa = new Button("Hủy đặt phòng");
        btnXoa.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnXoa.setPrefWidth(120);
        btnXoa.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận hủy");
            confirm.setHeaderText("Bạn có chắc muốn hủy phiếu đặt phòng này?");
            confirm.setContentText("Hóa đơn: " + hoaDon.getMaHoaDon());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Phong phong = phongList.stream().filter(p -> p.getMaPhong().equals(hoaDon.getMaPhong())).findFirst().orElse(null);
                    if (phong != null) {
                        int phongIndex = phongList.indexOf(phong);
                        Phong updatedPhong = new Phong(phong.getMaPhong(), phong.getLoaiPhong(), phong.getGiaPhong(), "Trống", phong.getViTri(), moTa, phong.getSoNguoiToiDa(), phong.getMoTa());
                        phongList.set(phongIndex, updatedPhong);
                    }
                    hoaDonList.remove(hoaDon);
                    updateBookingDisplayDirectly();
                    contentPane.getChildren().setAll(mainPane);
                }
            });
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnHuy.setPrefWidth(120);
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(15, btnLuu, btnXoa, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }

    private void thanhToanHoaDon(HoaDon hoaDon) {
        if ("Đã thanh toán".equals(hoaDon.getTrangThai())) {
            showAlert("Lỗi", "Hóa đơn đã được thanh toán!");
            return;
        }

        double tienPhong = hoaDon.getTienPhong();
        double tienDichVu = hoaDon.getTienDichVu();
        double tongTien = tienPhong + tienDichVu;
        String moTaBanDau = hoaDon.getMoTa();

        VBox form = createCenteredForm("Thanh toán hóa đơn " + hoaDon.getMaHoaDon());

        ComboBox<KhuyenMai> promotionCombo = new ComboBox<>(khuyenMaiList.filtered(km -> km.isTrangThai()));
        promotionCombo.setPromptText("Chọn mã khuyến mãi (nếu có)");
        promotionCombo.setPrefWidth(250);
        promotionCombo.setStyle("-fx-font-size: 16px;");
        promotionCombo.setConverter(new javafx.util.StringConverter<KhuyenMai>() {
            @Override
            public String toString(KhuyenMai km) {
                return km != null ? km.getMaChuongTrinhKhuyenMai() + " - " + km.getTenChuongTrinhKhuyenMai() : "";
            }

            @Override
            public KhuyenMai fromString(String string) {
                return null;
            }
        });

        Label discountLabel = new Label("Giảm giá: 0 VNĐ");
        Label finalTotalLabel = new Label("Tổng tiền cuối: " + String.format("%,.0f VNĐ", tongTien));

        double[] finalTotal = { tongTien };
        String[] finalMoTa = { moTaBanDau };

        promotionCombo.setOnAction(e -> {
        	KhuyenMai selected = promotionCombo.getValue();
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
        btnTienMat.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnTienMat.setPrefWidth(120);
        btnTienMat.setOnAction(e -> {
            completePayment(hoaDon, finalTotal[0], finalMoTa[0], "Tiền mặt");
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnOnline = new Button("Online");
        btnOnline.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnOnline.setPrefWidth(120);
        btnOnline.setOnAction(e -> {
            VBox onlineForm = createCenteredForm("Thanh toán Online");
            Image qrImage = new Image("/img/QRcode.jpg", true);
            ImageView qrView = new ImageView(qrImage);
            qrView.setFitWidth(150);
            qrView.setFitHeight(150);
            Label qrLabel = new Label("Quét mã QR để thanh toán " + String.format("%,.0f VNĐ", finalTotal[0]));

            Button btnXacNhan = new Button("Xác nhận đã thu");
            btnXacNhan.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
            btnXacNhan.setPrefWidth(120);
            btnXacNhan.setOnAction(ev -> {
                completePayment(hoaDon, finalTotal[0], finalMoTa[0], "Online");
                contentPane.getChildren().setAll(mainPane);
            });

            Button btnHuyOnline = new Button("Hủy");
            btnHuyOnline.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
            btnHuyOnline.setPrefWidth(120);
            btnHuyOnline.setOnAction(ev -> contentPane.getChildren().setAll(mainPane));

            HBox onlineFooter = new HBox(15, btnXacNhan, btnHuyOnline);
            onlineFooter.setAlignment(Pos.CENTER);

            onlineForm.getChildren().addAll(qrView, qrLabel, onlineFooter);
            contentPane.getChildren().setAll(onlineForm);
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnHuy.setPrefWidth(120);
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(15, btnTienMat, btnOnline, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(paymentDetails, footer);
        contentPane.getChildren().setAll(form);
    }

    private void completePayment(HoaDon hoaDon, double tongTien, String moTa, String hinhThucThanhToan) {
        int index = hoaDonList.indexOf(hoaDon);
        if (index != -1) {
            HoaDon updatedHoaDon = new HoaDon(hoaDon.getMaHoaDon(), hoaDon.getTenKhachHang(), hoaDon.getMaPhong(),
                    hoaDon.getTienPhong(), hoaDon.getTienDichVu(), "Đã thanh toán", hoaDon.getMaPhong(),
                    hoaDon.getNgayLap(), moTa, hinhThucThanhToan, hoaDon.getMaKhachHang(), hoaDon.getMaNhanVien());
            hoaDonList.set(index, updatedHoaDon);
        }

        Phong phong = phongList.stream().filter(p -> p.getMaPhong().equals(hoaDon.getMaPhong())).findFirst().orElse(null);
        if (phong != null) {
            int phongIndex = phongList.indexOf(phong);
            Phong updatedPhong = new Phong(phong.getMaPhong(), phong.getLoaiPhong(), phong.getGiaPhong(), "Trống",
                    phong.getViTri(), hinhThucThanhToan, phong.getSoNguoiToiDa(), "Chưa dọn dẹp");
            phongList.set(phongIndex, updatedPhong);
        }

        updateBookingDisplayDirectly();
        showAlert("Thông báo", "Thanh toán hóa đơn " + hoaDon.getMaHoaDon() + " thành công!");
    }

    private void themDichVuHoaDon(HoaDon hoaDon) {
        if ("Đã thanh toán".equals(hoaDon.getTrangThai())) {
            showAlert("Lỗi", "Không thể thêm dịch vụ cho hóa đơn đã thanh toán!");
            return;
        }

        VBox form = createCenteredForm("Thêm dịch vụ cho hóa đơn " + hoaDon.getMaHoaDon());

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
        btnThem.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnThem.setPrefWidth(120);
        btnThem.setOnAction(e -> {
            DichVu selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Lỗi", "Vui lòng chọn một dịch vụ!");
                return;
            }

            double tienDichVuMoi = hoaDon.getTienDichVu() + selected.getGia();
            String moTaMoi = hoaDon.getMoTa() + ", " + selected.getTenDichVu() + " (" 
                            + String.format("%,.0f VNĐ", selected.getGia()) + ")";

            int index = hoaDonList.indexOf(hoaDon);
            HoaDon updatedHoaDon = new HoaDon(hoaDon.getMaHoaDon(), hoaDon.getTenKhachHang(), hoaDon.getMaPhong(),
                    hoaDon.getTienPhong(), tienDichVuMoi, hoaDon.getTrangThai(), hoaDon.getMaPhong(),
                    hoaDon.getNgayLap(), moTaMoi, hoaDon.getHinhThucThanhToan(), hoaDon.getMaKhachHang(),
                    hoaDon.getMaNhanVien());
            hoaDonList.set(index, updatedHoaDon);

            updateBookingDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
            showAlert("Thông báo", "Thêm dịch vụ " + selected.getTenDichVu() + " thành công!");
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnHuy.setPrefWidth(120);
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(15, btnThem, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(instructionLabel, table, footer);
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