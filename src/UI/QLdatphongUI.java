package UI;

import dao.ChitietPhieuDatPhong_Dao;
import dao.PhieuDatPhong_Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class QLdatphongUI {
    private final ObservableList<Phong> phongList;
    private final ObservableList<HoaDon> hoaDonList;
    private final ObservableList<KhachHang> khachHangList;
    private final ObservableList<DichVu> dichVuList;
    private final ObservableList<KhuyenMai> khuyenMaiList;
    private final ObservableList<PhieuDatPhong> phieuDatPhongList;
    private FlowPane bookingFlowPane;
    private StackPane contentPane;
    private StackPane mainPane;

    public QLdatphongUI() {
        DataManager dataManager = DataManager.getInstance();
        this.phongList = dataManager.getPhongList();
        this.hoaDonList = dataManager.getHoaDonList();
        this.khachHangList = dataManager.getKhachHangList();
        this.dichVuList = dataManager.getDichVuList();
        this.khuyenMaiList = dataManager.getKhuyenMaiList();
        this.phieuDatPhongList = dataManager.getPhieuDatPhongList();
        this.contentPane = new StackPane();

        // Load dữ liệu từ database
        loadPhieuDatPhongFromDatabase();

        this.mainPane = createMainPane();
        this.contentPane.getChildren().add(mainPane);
        dataManager.addPhieuDatPhongListChangeListener(this::updateBookingDisplayDirectly);
    }

    private void loadPhieuDatPhongFromDatabase() {
        try {
            PhieuDatPhong_Dao phieuDao = new PhieuDatPhong_Dao();
            ChitietPhieuDatPhong_Dao chiTietDao = new ChitietPhieuDatPhong_Dao();

            // Lấy tất cả phiếu đặt phòng từ database
            phieuDatPhongList.clear(); // Xóa dữ liệu cũ trong danh sách
            phieuDatPhongList.addAll(phieuDao.getAllPhieuDatPhong());

            // Load chi tiết phòng cho từng phiếu
            for (PhieuDatPhong phieu : phieuDatPhongList) {
                phieu.setChitietPhieuDatPhongs(chiTietDao.getChitietByMaDatPhong(phieu.getMaDatPhong()));
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể load dữ liệu phiếu đặt phòng: " + e.getMessage());
        }
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: white;");

        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.TOP_CENTER);
        centerLayout.setStyle(
                "-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label titleLabel = new Label("Quản Lý Phiếu Đặt Phòng");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        TextField searchField = new TextField();
        searchField.setPromptText("Nhập mã phiếu đặt phòng (ví dụ: DP001)");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);

        bookingFlowPane = new FlowPane();
        bookingFlowPane.setHgap(20);
        bookingFlowPane.setVgap(20);
        bookingFlowPane.setAlignment(Pos.CENTER);
        bookingFlowPane.setPadding(new Insets(10));

        updateBookingDisplayDirectly();

        ScrollPane scrollPane = new ScrollPane(bookingFlowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        centerLayout.getChildren().addAll(titleLabel, searchBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setCenter(centerLayout);
        layout.setPadding(new Insets(10));

        Runnable searchAction = () -> {
            String tuKhoa = searchField.getText().trim();
            filterBookings(tuKhoa);
        };

        searchButton.setOnAction(e -> searchAction.run());
        searchField.setOnAction(e -> searchAction.run());

        mainPane.getChildren().add(layout);
        return mainPane;
    }

    public StackPane getUI() {
        return contentPane;
    }

    private void filterBookings(String searchText) {
        ObservableList<PhieuDatPhong> filteredList = FXCollections.observableArrayList();
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredList.addAll(phieuDatPhongList);
        } else {
            String searchLower = searchText.toLowerCase();
            for (PhieuDatPhong phieu : phieuDatPhongList) {
                if (phieu.getMaDatPhong().toLowerCase().contains(searchLower) ||
                    phieu.getMaKhachHang().toLowerCase().contains(searchLower)) {
                    filteredList.add(phieu);
                }
            }
            if (filteredList.isEmpty()) {
                showAlert("Thông báo", "Không tìm thấy phiếu đặt phòng nào với từ khóa: " + searchText);
            }
        }
        updateBookingDisplay(filteredList);
    }

    private void updateBookingDisplay(ObservableList<PhieuDatPhong> displayList) {
        bookingFlowPane.getChildren().clear();
        for (PhieuDatPhong phieu : displayList) {
            VBox bookingBox = new VBox(8);
            bookingBox.setPrefSize(250, 200);
            bookingBox.setPadding(new Insets(10));
            bookingBox.setAlignment(Pos.CENTER_LEFT);
            String bgColor = "Chưa xác nhận".equals(phieu.getTrangThai()) ? "#FFD700" : "#90EE90";
            bookingBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: #666; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            bookingBox.setOnMouseClicked(e -> showBookingDetailsDialog(phieu));

            // Hiển thị thông tin giống mẫu
            Label maPhieuLabel = new Label("Mã phiếu đặt phòng: " + phieu.getMaDatPhong());
            maPhieuLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label maKhachHangLabel = new Label("Mã khách hàng: " + phieu.getMaKhachHang());
            Label ngayDenLabel = new Label("Ngày đến: " + (phieu.getNgayDen() != null ? phieu.getNgayDen() : "N/A"));
            Label ngayDiLabel = new Label("Ngày đi: " + (phieu.getNgayDi() != null ? phieu.getNgayDi() : "N/A"));
            Label soLuongNguoiLabel = new Label("Số lượng người: " + phieu.getSoLuongNguoi());
            Label trangThaiLabel = new Label("Trạng thái: " + phieu.getTrangThai());

            // Chi tiết phòng
            VBox phongDetails = new VBox(5);
            phongDetails.getChildren().add(new Label("Chi tiết phòng:"));
            double totalThanhTien = 0;
            for (ChitietPhieuDatPhong chiTiet : phieu.getChitietPhieuDatPhongs()) {
                Label chiTietLabel = new Label(String.format("Phòng: %s, Số lượng: %d, Giá phòng: %,.0f VNĐ, Thành tiền: %,.0f VNĐ",
                        chiTiet.getMaPhong(), chiTiet.getSoLuong(), chiTiet.getGiaPhong(), chiTiet.getThanhTien()));
                phongDetails.getChildren().add(chiTietLabel);
                totalThanhTien += chiTiet.getThanhTien();
            }
            Label totalLabel = new Label("Tổng thành tiền: " + String.format("%,.0f VNĐ", totalThanhTien));

            // Giới hạn kích thước để tránh tràn
            maPhieuLabel.setMaxWidth(230);
            maKhachHangLabel.setMaxWidth(230);
            ngayDenLabel.setMaxWidth(230);
            ngayDiLabel.setMaxWidth(230);
            soLuongNguoiLabel.setMaxWidth(230);
            trangThaiLabel.setMaxWidth(230);
            phongDetails.setMaxWidth(230);
            totalLabel.setMaxWidth(230);

            bookingBox.getChildren().addAll(maPhieuLabel, maKhachHangLabel, ngayDenLabel, ngayDiLabel,
                    soLuongNguoiLabel, trangThaiLabel, phongDetails, totalLabel);
            bookingFlowPane.getChildren().add(bookingBox);
        }
    }

    private void updateBookingDisplayDirectly() {
        updateBookingDisplay(phieuDatPhongList);
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(600);
        form.setMaxHeight(500);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        title.setPadding(new Insets(0, 0, 10, 0));

        form.getChildren().add(title);
        return form;
    }

    private void showBookingDetailsDialog(PhieuDatPhong phieu) {
        VBox form = createCenteredForm("Chi tiết phiếu đặt phòng " + phieu.getMaDatPhong());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label maPhieuLabel = new Label("Mã phiếu đặt phòng: " + phieu.getMaDatPhong());
        Label maKhachHangLabel = new Label("Mã khách hàng: " + phieu.getMaKhachHang());
        Label ngayDenLabel = new Label("Ngày đến: " + (phieu.getNgayDen() != null ? phieu.getNgayDen() : "N/A"));
        Label ngayDiLabel = new Label("Ngày đi: " + (phieu.getNgayDi() != null ? phieu.getNgayDi() : "N/A"));
        Label ngayDatLabel = new Label("Ngày đặt: " + (phieu.getNgayDat() != null ? phieu.getNgayDat() : "N/A"));
        Label soLuongNguoiLabel = new Label("Số lượng người: " + phieu.getSoLuongNguoi());
        Label trangThaiLabel = new Label("Trạng thái: " + phieu.getTrangThai());

        VBox phongDetails = new VBox(5);
        phongDetails.getChildren().add(new Label("Chi tiết phòng:"));
        double totalThanhTien = 0;
        for (ChitietPhieuDatPhong chiTiet : phieu.getChitietPhieuDatPhongs()) {
            Label chiTietLabel = new Label(String.format("Phòng: %s, Số lượng: %d, Giá phòng: %,.0f VNĐ, Thành tiền: %,.0f VNĐ",
                    chiTiet.getMaPhong(), chiTiet.getSoLuong(), chiTiet.getGiaPhong(), chiTiet.getThanhTien()));
            phongDetails.getChildren().add(chiTietLabel);
            totalThanhTien += chiTiet.getThanhTien();
        }
        Label totalLabel = new Label("Tổng thành tiền: " + String.format("%,.0f VNĐ", totalThanhTien));

        content.getChildren().addAll(maPhieuLabel, maKhachHangLabel, ngayDenLabel, ngayDiLabel,
                ngayDatLabel, soLuongNguoiLabel, trangThaiLabel, phongDetails, totalLabel);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button btnSua = new Button("Sửa");
        btnSua.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnSua.setPrefWidth(120);
        btnSua.setOnAction(e -> showEditBookingDialog(phieu));

        Button btnThanhToan = new Button("Thanh toán");
        btnThanhToan.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnThanhToan.setPrefWidth(120);
        btnThanhToan.setOnAction(e -> thanhToanPhieuDatPhong(phieu));
        btnThanhToan.setDisable("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai()));

        Button btnThemDichVu = new Button("Thêm dịch vụ");
        btnThemDichVu.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnThemDichVu.setPrefWidth(120);
        btnThemDichVu.setOnAction(e -> themDichVuPhieuDatPhong(phieu));
        btnThemDichVu.setDisable("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai()));

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnDong.setPrefWidth(120);
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        buttons.getChildren().addAll(btnSua, btnThanhToan, btnThemDichVu, btnDong);

        form.getChildren().addAll(content, buttons);
        contentPane.getChildren().setAll(form);
    }

    private void showEditBookingDialog(PhieuDatPhong phieu) {
        VBox form = createCenteredForm("Chỉnh sửa phiếu đặt phòng " + phieu.getMaDatPhong());

        TextField maPhieuField = new TextField(phieu.getMaDatPhong());
        maPhieuField.setDisable(true);
        maPhieuField.setPrefWidth(250);
        maPhieuField.setStyle("-fx-font-size: 16px;");

        TextField maKhachHangField = new TextField(phieu.getMaKhachHang());
        maKhachHangField.setPrefWidth(250);
        maKhachHangField.setStyle("-fx-font-size: 16px;");

        DatePicker ngayDenPicker = new DatePicker(phieu.getNgayDen());
        ngayDenPicker.setPrefWidth(250);
        ngayDenPicker.setStyle("-fx-font-size: 16px;");

        DatePicker ngayDiPicker = new DatePicker(phieu.getNgayDi());
        ngayDiPicker.setPrefWidth(250);
        ngayDiPicker.setStyle("-fx-font-size: 16px;");

        TextField soLuongNguoiField = new TextField(String.valueOf(phieu.getSoLuongNguoi()));
        soLuongNguoiField.setPrefWidth(250);
        soLuongNguoiField.setStyle("-fx-font-size: 16px;");
        soLuongNguoiField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                soLuongNguoiField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        ComboBox<String> trangThaiCombo = new ComboBox<>();
        trangThaiCombo.getItems().addAll("Chưa xác nhận", "Xác nhận", "Đã hủy");
        trangThaiCombo.setValue(phieu.getTrangThai());
        trangThaiCombo.setPrefWidth(250);
        trangThaiCombo.setStyle("-fx-font-size: 16px;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Mã Phiếu Đặt Phòng:"), maPhieuField);
        grid.addRow(1, new Label("Mã Khách Hàng:"), maKhachHangField);
        grid.addRow(2, new Label("Ngày Đến:"), ngayDenPicker);
        grid.addRow(3, new Label("Ngày Đi:"), ngayDiPicker);
        grid.addRow(4, new Label("Số Lượng Người:"), soLuongNguoiField);
        grid.addRow(5, new Label("Trạng Thái:"), trangThaiCombo);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnLuu.setPrefWidth(120);
        btnLuu.setOnAction(e -> {
            String maKhachHang = maKhachHangField.getText();
            LocalDate ngayDen = ngayDenPicker.getValue();
            LocalDate ngayDi = ngayDiPicker.getValue();
            String soLuongNguoiText = soLuongNguoiField.getText();
            String trangThai = trangThaiCombo.getValue();

            if (maKhachHang.isEmpty() || ngayDen == null || ngayDi == null || soLuongNguoiText.isEmpty() || trangThai == null) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            if (ngayDen.isAfter(ngayDi)) {
                showAlert("Lỗi", "Ngày đến phải trước ngày đi!");
                return;
            }

            int soLuongNguoi;
            try {
                soLuongNguoi = Integer.parseInt(soLuongNguoiText);
                if (soLuongNguoi <= 0) {
                    showAlert("Lỗi", "Số lượng người phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Số lượng người phải là số hợp lệ!");
                return;
            }

            PhieuDatPhong updatedPhieu = new PhieuDatPhong(
                    phieu.getMaDatPhong(),
                    ngayDen,
                    ngayDi,
                    phieu.getNgayDat(),
                    soLuongNguoi,
                    trangThai,
                    maKhachHang
            );
            updatedPhieu.setChitietPhieuDatPhongs(phieu.getChitietPhieuDatPhongs());
            int index = phieuDatPhongList.indexOf(phieu);
            phieuDatPhongList.set(index, updatedPhieu);

            // Cập nhật database khi sửa
            try {
                PhieuDatPhong_Dao phieuDao = new PhieuDatPhong_Dao();
                phieuDao.addPhieuDatPhong(updatedPhieu); // Cần sửa thành update nếu có phương thức update
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể cập nhật phiếu đặt phòng: " + ex.getMessage());
            }

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
            confirm.setContentText("Phiếu đặt phòng: " + phieu.getMaDatPhong());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    for (ChitietPhieuDatPhong chiTiet : phieu.getChitietPhieuDatPhongs()) {
                        Phong phong = phongList.stream()
                                .filter(p -> p.getMaPhong().equals(chiTiet.getMaPhong()))
                                .findFirst().orElse(null);
                        if (phong != null) {
                            int phongIndex = phongList.indexOf(phong);
                            Phong updatedPhong = new Phong(
                                    phong.getMaPhong(),
                                    phong.getLoaiPhong(),
                                    phong.getGiaPhong(),
                                    "Trống",
                                    phong.getDonDep(),
                                    phong.getViTri(),
                                    phong.getSoNguoiToiDa(),
                                    phong.getMoTa()
                            );
                            phongList.set(phongIndex, updatedPhong);
                        }
                    }
                    phieuDatPhongList.remove(phieu);
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

    private void thanhToanPhieuDatPhong(PhieuDatPhong phieu) {
        if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
            showAlert("Lỗi", "Phiếu đặt phòng đã được xác nhận hoặc đã hủy!");
            return;
        }

        double totalThanhTien = phieu.getChitietPhieuDatPhongs().stream()
                .mapToDouble(ChitietPhieuDatPhong::getThanhTien)
                .sum();

        VBox form = createCenteredForm("Thanh toán phiếu đặt phòng " + phieu.getMaDatPhong());

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
        Label finalTotalLabel = new Label("Tổng tiền cuối: " + String.format("%,.0f VNĐ", totalThanhTien));

        double[] finalTotal = {totalThanhTien};
        StringBuilder moTa = new StringBuilder("Thanh toán cho " + phieu.getMaDatPhong());

        promotionCombo.setOnAction(e -> {
            KhuyenMai selected = promotionCombo.getValue();
            double discount = 0;
            moTa.setLength(0);
            moTa.append("Thanh toán cho ").append(phieu.getMaDatPhong());
            if (selected != null) {
                discount = totalThanhTien * (selected.getChietKhau() / 100);
                finalTotal[0] = totalThanhTien - discount;
                discountLabel.setText("Giảm giá: " + String.format("%,.0f VNĐ", discount));
                finalTotalLabel.setText("Tổng tiền cuối: " + String.format("%,.0f VNĐ", finalTotal[0]));
                moTa.append(" - Sử dụng ").append(selected.getMaChuongTrinhKhuyenMai())
                    .append(" (Giảm ").append(String.format("%,.0f VNĐ", discount)).append(")");
            } else {
                finalTotal[0] = totalThanhTien;
                discountLabel.setText("Giảm giá: 0 VNĐ");
                finalTotalLabel.setText("Tổng tiền cuối: " + String.format("%,.0f VNĐ", totalThanhTien));
            }
        });

        VBox paymentDetails = new VBox(10);
        paymentDetails.getChildren().addAll(
                new Label("Tổng tiền phòng: " + String.format("%,.0f VNĐ", totalThanhTien)),
                promotionCombo,
                discountLabel,
                finalTotalLabel,
                new Label("Chọn phương thức thanh toán:")
        );

        Button btnTienMat = new Button("Tiền mặt");
        btnTienMat.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnTienMat.setPrefWidth(120);
        btnTienMat.setOnAction(e -> {
            completePayment(phieu, finalTotal[0], moTa.toString(), "Tiền mặt");
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnOnline = new Button("Online");
        btnOnline.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnOnline.setPrefWidth(120);
        btnOnline.setOnAction(e -> {
            VBox onlineForm = createCenteredForm("Thanh toán Online");
            Label qrLabel = new Label("Quét mã QR để thanh toán " + String.format("%,.0f VNĐ", finalTotal[0]));

            Button btnXacNhan = new Button("Xác nhận đã thu");
            btnXacNhan.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
            btnXacNhan.setPrefWidth(120);
            btnXacNhan.setOnAction(ev -> {
                completePayment(phieu, finalTotal[0], moTa.toString(), "Online");
                contentPane.getChildren().setAll(mainPane);
            });

            Button btnHuyOnline = new Button("Hủy");
            btnHuyOnline.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
            btnHuyOnline.setPrefWidth(120);
            btnHuyOnline.setOnAction(ev -> contentPane.getChildren().setAll(mainPane));

            HBox onlineFooter = new HBox(15, btnXacNhan, btnHuyOnline);
            onlineFooter.setAlignment(Pos.CENTER);

            onlineForm.getChildren().addAll(qrLabel, onlineFooter);
            contentPane.getChildren().setAll(onlineForm);
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnHuy.setPrefWidth(120);
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox paymentButtons = new HBox(15, btnTienMat, btnOnline, btnHuy);
        paymentButtons.setAlignment(Pos.CENTER);

        form.getChildren().addAll(paymentDetails, paymentButtons);
        contentPane.getChildren().setAll(form);
    }

    private void completePayment(PhieuDatPhong phieu, double finalTotal, String moTa, String hinhThucThanhToan) {
        for (ChitietPhieuDatPhong chiTiet : phieu.getChitietPhieuDatPhongs()) {
            Phong phong = phongList.stream()
                    .filter(p -> p.getMaPhong().equals(chiTiet.getMaPhong()))
                    .findFirst().orElse(null);
            if (phong != null) {
                int phongIndex = phongList.indexOf(phong);
                Phong updatedPhong = new Phong(
                        phong.getMaPhong(),
                        phong.getLoaiPhong(),
                        phong.getGiaPhong(),
                        "Trống",
                        phong.getDonDep(),
                        phong.getViTri(),
                        phong.getSoNguoiToiDa(),
                        phong.getMoTa()
                );
                phongList.set(phongIndex, updatedPhong);
            }
        }

        PhieuDatPhong updatedPhieu = new PhieuDatPhong(
                phieu.getMaDatPhong(),
                phieu.getNgayDen(),
                phieu.getNgayDi(),
                phieu.getNgayDat(),
                phieu.getSoLuongNguoi(),
                "Xác nhận",
                phieu.getMaKhachHang()
        );
        updatedPhieu.setChitietPhieuDatPhongs(phieu.getChitietPhieuDatPhongs());
        int index = phieuDatPhongList.indexOf(phieu);
        phieuDatPhongList.set(index, updatedPhieu);

        String maHoaDon = "HD" + System.currentTimeMillis();
        HoaDon hoaDon = new HoaDon(
                maHoaDon,
                phieu.getMaKhachHang(),
                phieu.getChitietPhieuDatPhongs().isEmpty() ? "Unknown" : phieu.getChitietPhieuDatPhongs().get(0).getMaPhong(),
                finalTotal,
                0,
                "Đã thanh toán",
                phieu.getChitietPhieuDatPhongs().isEmpty() ? "Unknown" : phieu.getChitietPhieuDatPhongs().get(0).getMaPhong(),
                LocalDate.now(),
                moTa,
                hinhThucThanhToan,
                phieu.getMaKhachHang(),
                "NV001"
        );
        hoaDonList.add(hoaDon);

        updateBookingDisplayDirectly();
        showAlert("Thành công", "Thanh toán phiếu đặt phòng " + phieu.getMaDatPhong() + " thành công!");
    }

    private void themDichVuPhieuDatPhong(PhieuDatPhong phieu) {
        VBox form = createCenteredForm("Thêm dịch vụ cho phiếu đặt phòng " + phieu.getMaDatPhong());

        ComboBox<DichVu> dichVuCombo = new ComboBox<>(dichVuList);
        dichVuCombo.setPromptText("Chọn dịch vụ");
        dichVuCombo.setPrefWidth(250);
        dichVuCombo.setStyle("-fx-font-size: 16px;");
        dichVuCombo.setConverter(new javafx.util.StringConverter<DichVu>() {
            @Override
            public String toString(DichVu dv) {
                return dv != null ? dv.getTenDichVu() + " - " + String.format("%,.0f VNĐ", dv.getGia()) : "";
            }

            @Override
            public DichVu fromString(String string) {
                return null;
            }
        });

        TextField soLuongField = new TextField("1");
        soLuongField.setPrefWidth(250);
        soLuongField.setStyle("-fx-font-size: 16px;");
        soLuongField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                soLuongField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Label totalLabel = new Label("Tổng tiền dịch vụ: 0 VNĐ");
        dichVuCombo.setOnAction(e -> updateServiceTotal(dichVuCombo, soLuongField, totalLabel));
        soLuongField.textProperty().addListener((obs, oldValue, newValue) -> updateServiceTotal(dichVuCombo, soLuongField, totalLabel));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Dịch vụ:"), dichVuCombo);
        grid.addRow(1, new Label("Số lượng:"), soLuongField);
        grid.addRow(2, new Label(""), totalLabel);

        Button btnThem = new Button("Thêm");
        btnThem.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnThem.setPrefWidth(120);
        btnThem.setOnAction(e -> {
            DichVu selectedDichVu = dichVuCombo.getValue();
            String soLuongText = soLuongField.getText();

            if (selectedDichVu == null || soLuongText.isEmpty()) {
                showAlert("Lỗi", "Vui lòng chọn dịch vụ và nhập số lượng!");
                return;
            }

            int soLuong;
            try {
                soLuong = Integer.parseInt(soLuongText);
                if (soLuong <= 0) {
                    showAlert("Lỗi", "Số lượng phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Số lượng phải là số hợp lệ!");
                return;
            }

            double additionalCost = selectedDichVu.getGia() * soLuong;
            ChitietHoaDon chiTietHoaDon = new ChitietHoaDon(
                    soLuong, phieu.getMaDatPhong(),
                    selectedDichVu.getMaDichVu(),
                    soLuong,
                    selectedDichVu.getGia(),
                    additionalCost, soLuongText, soLuongText, soLuongText, soLuongText, soLuongText
            );
            phieu.addChitietHoaDon(chiTietHoaDon);

            updateBookingDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
            showAlert("Thành công", "Đã thêm dịch vụ " + selectedDichVu.getTenDichVu() + " vào phiếu đặt phòng!");
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

    private void updateServiceTotal(ComboBox<DichVu> dichVuCombo, TextField soLuongField, Label totalLabel) {
        DichVu selectedDichVu = dichVuCombo.getValue();
        String soLuongText = soLuongField.getText();
        if (selectedDichVu != null && !soLuongText.isEmpty()) {
            try {
                int soLuong = Integer.parseInt(soLuongText);
                double total = selectedDichVu.getGia() * soLuong;
                totalLabel.setText("Tổng tiền dịch vụ: " + String.format("%,.0f VNĐ", total));
            } catch (NumberFormatException e) {
                totalLabel.setText("Tổng tiền dịch vụ: 0 VNĐ");
            }
        } else {
            totalLabel.setText("Tổng tiền dịch vụ: 0 VNĐ");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Lỗi") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}