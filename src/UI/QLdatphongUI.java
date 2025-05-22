package UI;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.*;
import javafx.beans.property.SimpleStringProperty;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import ConnectDB.ConnectDB;

public class QLdatphongUI {
    private static final double VAT_RATE = 0.10;
    private final ObservableList<Phong> phongList;
    private final ObservableList<HoaDon> hoaDonList;
    private final ObservableList<KhachHang> khachHangList;
    private final ObservableList<DichVu> dichVuList;
    private final ObservableList<KhuyenMai> khuyenMaiList;
    private final ObservableList<PhieuDatPhong> phieuDatPhongList;
    private FlowPane bookingFlowPane;
    private StackPane contentPane;
    private StackPane mainPane;
    private final PhieuDatPhong_Dao phieuDatPhongDao;
    private final Phong_Dao phongDao;
    private final HoaDon_Dao hoaDonDao;
    private final ChitietHoaDon_Dao chitietHoaDonDao;
    private final ChitietPhieuDatPhong_Dao chitietPhieuDatPhongDao;
    private final PhieuDichVu_Dao phieuDichVuDao;
    private final ChitietPhieuDichVu_Dao chitietPhieuDichVuDao;
    private final NhanVien_Dao nhanVienDao;
    private final DataManager dataManager;

    public QLdatphongUI() {
        try {
            phieuDatPhongDao = new PhieuDatPhong_Dao();
            phongDao = new Phong_Dao();
            hoaDonDao = new HoaDon_Dao();
            chitietHoaDonDao = new ChitietHoaDon_Dao();
            chitietPhieuDatPhongDao = new ChitietPhieuDatPhong_Dao();
            phieuDichVuDao = new PhieuDichVu_Dao();
            chitietPhieuDichVuDao = new ChitietPhieuDichVu_Dao();
            nhanVienDao = new NhanVien_Dao();
        } catch (Exception e) {
            throw new RuntimeException("Không thể kết nối tới cơ sở dữ liệu!", e);
        }

        dataManager = DataManager.getInstance();
        this.phongList = dataManager.getPhongList();
        this.hoaDonList = dataManager.getHoaDonList();
        this.khachHangList = dataManager.getKhachHangList();
        this.dichVuList = dataManager.getDichVuList();
        this.khuyenMaiList = dataManager.getKhuyenMaiList();
        this.phieuDatPhongList = dataManager.getPhieuDatPhongList();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
        this.contentPane.getChildren().add(mainPane);
        dataManager.addPhieuDatPhongListChangeListener(this::updateBookingDisplayDirectly);
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: white;");

        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("Thông tin người dùng"));
            userInfoBox.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        }
        userInfoBox.setPrefSize(200, 50);
        userInfoBox.setMaxSize(200, 50);
        userInfoBox.setAlignment(Pos.CENTER);

        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.TOP_CENTER);
        centerLayout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; "
                + "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

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
        layout.setTop(userInfoBox);
        BorderPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        BorderPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));
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
            try {
                List<PhieuDatPhong> searchResults = phieuDatPhongDao.timKiemPhieuDatPhong(searchText);
                filteredList.addAll(searchResults);
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể tìm kiếm phiếu đặt phòng: " + e.getMessage());
            }
        }
        updateBookingDisplay(filteredList);
    }

    private void updateBookingDisplay(ObservableList<PhieuDatPhong> displayList) {
        bookingFlowPane.getChildren().clear();
        for (PhieuDatPhong phieu : displayList) {
            try {
                List<ChitietPhieuDatPhong> chiTietList = chitietPhieuDatPhongDao
                        .timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());
                System.out.println("updateBookingDisplay - Mã phiếu đặt: " + phieu.getMaDatPhong());
                for (ChitietPhieuDatPhong chiTiet : chiTietList) {
                    System.out.println("  Mã phòng: " + chiTiet.getMaPhong());
                }

                VBox bookingBox = new VBox(8);
                bookingBox.setPrefSize(250, 220);
                bookingBox.setPadding(new Insets(10));
                bookingBox.setAlignment(Pos.CENTER_LEFT);
                String bgColor = "Chưa xác nhận".equals(phieu.getTrangThai()) ? "#FFD700" : "#90EE90";
                bookingBox.setStyle("-fx-background-color: " + bgColor
                        + "; -fx-background-radius: 10; -fx-border-radius: 10; "
                        + "-fx-border-color: #666; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                bookingBox.setOnMouseClicked(e -> showBookingDetailsDialog(phieu));

                Label maPhieuLabel = new Label("Phiếu: " + phieu.getMaDatPhong());
                maPhieuLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                Label maKhachHangLabel = new Label("Khách hàng: " + phieu.getMaKhachHang());
                Label ngayDenLabel = new Label(
                        "Ngày đến: " + (phieu.getNgayDen() != null ? phieu.getNgayDen() : "N/A"));
                Label ngayDiLabel = new Label("Ngày đi: " + (phieu.getNgayDi() != null ? phieu.getNgayDi() : "N/A"));
                Label soLuongNguoiLabel = new Label("Số người: " + phieu.getSoLuongNguoi());
                Label trangThaiLabel = new Label("Trạng thái: " + phieu.getTrangThai());

                StringBuilder phongInfo = new StringBuilder("Phòng: ");
                double totalThanhTien = 0;
                for (ChitietPhieuDatPhong chiTiet : chiTietList) {
                    phongInfo.append(chiTiet.getMaPhong()).append(", ");
                    totalThanhTien += chiTiet.getThanhTien();
                }
                Label phongLabel = new Label(
                        phongInfo.length() > 2 ? phongInfo.substring(0, phongInfo.length() - 2) : "Chưa chọn phòng");
                Label thanhTienLabel = new Label("Tổng tiền: " + String.format("%,.0f VNĐ", totalThanhTien));

                maPhieuLabel.setMaxWidth(230);
                maKhachHangLabel.setMaxWidth(230);
                ngayDenLabel.setMaxWidth(230);
                ngayDiLabel.setMaxWidth(230);
                soLuongNguoiLabel.setMaxWidth(230);
                trangThaiLabel.setMaxWidth(230);
                phongLabel.setMaxWidth(230);
                phongLabel.setWrapText(true);
                thanhTienLabel.setMaxWidth(230);

                Button btnHuyDatPhong = new Button("Hủy đặt phòng");
                btnHuyDatPhong.setStyle(
                        "-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 5;");
                btnHuyDatPhong.setPrefWidth(100);
                btnHuyDatPhong.setOnAction(e -> {
                    if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
                        showAlert("Lỗi", "Không thể hủy phiếu đã xác nhận hoặc đã hủy!");
                        return;
                    }

                    boolean isPaid = chiTietList.stream().anyMatch(ChitietPhieuDatPhong::isPaid);
                    if (isPaid) {
                        showAlert("Lỗi", "Không thể hủy phiếu đã thanh toán!");
                        return;
                    }

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Xác nhận hủy");
                    confirm.setHeaderText("Bạn có chắc muốn hủy phiếu đặt phòng này?");
                    confirm.setContentText("Phiếu đặt phòng: " + phieu.getMaDatPhong() + "\n" +
                            "Khách hàng: " + phieu.getMaKhachHang() + "\n" +
                            "Phòng: " + phongInfo.toString().substring(7));
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                for (ChitietPhieuDatPhong chiTiet : chiTietList) {
                                    Phong phong = phongList.stream()
                                            .filter(p -> p.getMaPhong().equals(chiTiet.getMaPhong()))
                                            .findFirst().orElse(null);
                                    if (phong != null) {
                                        boolean isBooked = dataManager.kiemTraPhongDat(
                                                phong.getMaPhong(), phieu.getNgayDen(), phieu.getNgayDi()
                                        );
                                        String newStatus = isBooked ? "Đã đặt" : "Trống";
                                        Phong updatedPhong = new Phong(
                                                phong.getMaPhong(), phong.getLoaiPhong(), phong.getGiaPhong(),
                                                newStatus, phong.getDonDep(), phong.getViTri(), phong.getSoNguoiToiDa(), phong.getMoTa()
                                        );
                                        dataManager.updatePhong(updatedPhong);
                                        System.out.println("Cập nhật trạng thái phòng: " + phong.getMaPhong() + " -> " + newStatus);
                                    }
                                }

                                dataManager.deletePhieuDatPhongFull(phieu.getMaDatPhong());
                                System.out.println("Đã xóa phiếu đặt phòng và dữ liệu liên quan: " + phieu.getMaDatPhong());

                                updateBookingDisplayDirectly();
                                showAlert("Thành công", "Hủy phiếu đặt phòng " + phieu.getMaDatPhong() + " thành công!");
                            } catch (SQLException ex) {
                                showAlert("Lỗi", "Không thể hủy phiếu đặt phòng: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    });
                });
                btnHuyDatPhong.setDisable("Xác nhận".equals(phieu.getTrangThai()) ||
                        "Đã hủy".equals(phieu.getTrangThai()) ||
                        chiTietList.stream().anyMatch(ChitietPhieuDatPhong::isPaid));

                bookingBox.getChildren().addAll(
                        maPhieuLabel, maKhachHangLabel, ngayDenLabel, ngayDiLabel,
                        soLuongNguoiLabel, trangThaiLabel, phongLabel, thanhTienLabel, btnHuyDatPhong
                );
                bookingFlowPane.getChildren().add(bookingBox);
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể tải chi tiết phiếu đặt phòng: " + e.getMessage());
            }
        }
    }

    private void updateBookingDisplayDirectly() {
        updateBookingDisplay(phieuDatPhongList);
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; "
                + "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
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
        try {
            List<ChitietPhieuDatPhong> chiTietList = chitietPhieuDatPhongDao
                    .timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());
            System.out.println("showBookingDetailsDialog - Mã phiếu đặt: " + phieu.getMaDatPhong());
            for (ChitietPhieuDatPhong chiTiet : chiTietList) {
                System.out.println("  Mã phòng: " + chiTiet.getMaPhong());
            }

            for (ChitietPhieuDatPhong chiTiet : chiTietList) {
                Phong phong = phongDao.getAllPhong().stream().filter(p -> p.getMaPhong().equals(chiTiet.getMaPhong()))
                        .findFirst().orElse(null);
                double giaPhong = phong != null ? phong.getGiaPhong() : 0;
                Label chiTietLabel = new Label(String.format(
                        "Phòng: %s, Giá phòng: %,.0f VNĐ, Số lượng: %d, Thành tiền: %,.0f VNĐ", chiTiet.getMaPhong(),
                        chiTiet.getTienPhong(), chiTiet.getSoLuong(), chiTiet.getThanhTien()));
                phongDetails.getChildren().add(chiTietLabel);
                totalThanhTien += chiTiet.getThanhTien();
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải chi tiết phòng: " + e.getMessage());
        }
        Label totalLabel = new Label("Tổng thành tiền: " + String.format("%,.0f VNĐ", totalThanhTien));

        content.getChildren().addAll(maPhieuLabel, maKhachHangLabel, ngayDenLabel, ngayDiLabel, ngayDatLabel,
                soLuongNguoiLabel, trangThaiLabel, phongDetails, totalLabel);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button btnDoiPhong = new Button("Đổi phòng");
        btnDoiPhong.setStyle(
                "-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnDoiPhong.setPrefWidth(120);
        btnDoiPhong.setOnAction(e -> showChangeRoomDialog(phieu));
        btnDoiPhong.setDisable("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai()));

        Button btnThanhToan = new Button("Thanh toán");
        btnThanhToan.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnThanhToan.setPrefWidth(120);
        btnThanhToan.setOnAction(e -> thanhToanPhieuDatPhong(phieu));
        btnThanhToan.setDisable("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai()));

        Button btnThemDichVu = new Button("Thêm dịch vụ");
        btnThemDichVu.setStyle(
                "-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnThemDichVu.setPrefWidth(120);
        btnThemDichVu.setOnAction(e -> themDichVuPhieuDatPhong(phieu));
        btnThemDichVu.setDisable("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai()));

        Button btnDong = new Button("Đóng");
        btnDong.setStyle(
                "-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnDong.setPrefWidth(120);
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        buttons.getChildren().addAll(btnDoiPhong, btnThanhToan, btnThemDichVu, btnDong);

        form.getChildren().addAll(content, buttons);
        contentPane.getChildren().setAll(form);
    }

    private void showChangeRoomDialog(PhieuDatPhong phieu) {
        if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
            showAlert("Lỗi", "Không thể đổi phòng cho phiếu đã xác nhận hoặc đã hủy!");
            return;
        }

        VBox form = createCenteredForm("Đổi phòng cho phiếu đặt phòng " + phieu.getMaDatPhong());

        List<ChitietPhieuDatPhong> chiTietList;
        try {
            chiTietList = chitietPhieuDatPhongDao.timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());
            System.out.println("showChangeRoomDialog - Mã phiếu đặt: " + phieu.getMaDatPhong() + ", Số phòng: "
                    + chiTietList.size());
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải chi tiết phòng: " + e.getMessage());
            return;
        }

        ComboBox<ChitietPhieuDatPhong> oldRoomCombo = new ComboBox<>(FXCollections.observableArrayList(chiTietList));
        oldRoomCombo.setPromptText("Chọn phòng cũ");
        oldRoomCombo.setPrefWidth(250);
        oldRoomCombo.setStyle(
                "-fx-font-size: 16px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");
        oldRoomCombo.setConverter(new javafx.util.StringConverter<ChitietPhieuDatPhong>() {
            @Override
            public String toString(ChitietPhieuDatPhong ct) {
                return ct != null
                        ? "Phòng: " + ct.getMaPhong() + " - Giá: " + String.format("%,.0f VNĐ", ct.getTienPhong())
                        : "";
            }

            @Override
            public ChitietPhieuDatPhong fromString(String string) {
                return null;
            }
        });

        ObservableList<Phong> availableRooms = FXCollections.observableArrayList();
        ComboBox<Phong> newRoomCombo = new ComboBox<>(availableRooms);
        newRoomCombo.setPromptText("Chọn phòng mới");
        newRoomCombo.setPrefWidth(250);
        newRoomCombo.setStyle(
                "-fx-font-size: 16px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");
        newRoomCombo.setConverter(new javafx.util.StringConverter<Phong>() {
            @Override
            public String toString(Phong phong) {
                return phong != null
                        ? phong.getMaPhong() + " - " + phong.getLoaiPhong() + " - "
                                + String.format("%,.0f VNĐ", phong.getGiaPhong()) + " (Sức chứa: "
                                + phong.getSoNguoiToiDa() + ")"
                        : "";
            }

            @Override
            public Phong fromString(String string) {
                return null;
            }
        });

        oldRoomCombo.setOnAction(e -> {
            availableRooms.clear();
            ChitietPhieuDatPhong selectedOldRoom = oldRoomCombo.getValue();
            if (selectedOldRoom != null && phieu.getNgayDen() != null && phieu.getNgayDi() != null) {
                try {
                    phongList.clear();
                    phongList.addAll(phongDao.getAllPhong());
                    System.out.println("Tổng số phòng trong danh sách: " + phongList.size());
                    for (Phong p : phongList) {
                        System.out.println("Phòng: " + p.getMaPhong() + " - " + p.getLoaiPhong() + " - Sức chứa: "
                                + p.getSoNguoiToiDa() + " - Trạng thái: " + p.getTrangThai());
                    }

                    int soLuongPhong = chiTietList.size();
                    int soNguoiToiDaMoiPhong = (int) Math.ceil((double) phieu.getSoLuongNguoi() / soLuongPhong);
                    System.out.println("Số lượng người trong phiếu: " + phieu.getSoLuongNguoi());
                    System.out.println("Số lượng phòng trong phiếu: " + soLuongPhong);
                    System.out.println("Số người tối đa mỗi phòng cần chứa: " + soNguoiToiDaMoiPhong);

                    for (Phong phong : phongList) {
                        if (phong.getSoNguoiToiDa() >= soNguoiToiDaMoiPhong
                                && !"Bảo trì".equals(phong.getTrangThai())) {
                            boolean isAvailable = true;

                            if (phong.getMaPhong().equals(selectedOldRoom.getMaPhong())) {
                                System.out.println("Phòng " + phong.getMaPhong() + " bị loại do là phòng cũ");
                                continue;
                            }

                            for (ChitietPhieuDatPhong ct : chiTietList) {
                                if (phong.getMaPhong().equals(ct.getMaPhong())) {
                                    isAvailable = false;
                                    System.out.println("Phòng " + phong.getMaPhong()
                                            + " bị loại do đã được sử dụng trong phiếu hiện tại");
                                    break;
                                }
                            }

                            if (isAvailable) {
                                boolean isBooked = dataManager.kiemTraPhongDat(phong.getMaPhong(), phieu.getNgayDen(),
                                        phieu.getNgayDi());
                                if (isBooked) {
                                    isAvailable = false;
                                    System.out.println("Phòng " + phong.getMaPhong() + " bị loại do đã được đặt");
                                }
                            }

                            if (isAvailable) {
                                availableRooms.add(phong);
                            }
                        } else {
                            System.out.println("Phòng " + phong.getMaPhong()
                                    + " bị loại do sức chứa hoặc trạng thái không phù hợp");
                        }
                    }
                    System.out.println("Số phòng trống khả dụng: " + availableRooms.size());
                    for (Phong p : availableRooms) {
                        System.out.println("Phòng khả dụng: " + p.getMaPhong() + " - " + p.getLoaiPhong());
                    }
                } catch (SQLException ex) {
                    showAlert("Lỗi", "Không thể tải danh sách phòng trống: " + ex.getMessage());
                }
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Phòng cũ:"), oldRoomCombo);
        grid.addRow(1, new Label("Phòng mới:"), newRoomCombo);

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnLuu.setPrefWidth(120);
        btnLuu.setOnAction(e -> {
            ChitietPhieuDatPhong oldRoom = oldRoomCombo.getValue();
            Phong newRoom = newRoomCombo.getValue();

            if (oldRoom == null || newRoom == null) {
                showAlert("Lỗi", "Vui lòng chọn cả phòng cũ và phòng mới!");
                return;
            }

            if (oldRoom.getMaPhong().equals(newRoom.getMaPhong())) {
                showAlert("Lỗi", "Phòng mới phải khác phòng cũ!");
                return;
            }

            Connection conn = null;
            boolean success = false;
            try {
                conn = ConnectDB.getConnection();
                conn.setAutoCommit(false);
                System.out.println("Bắt đầu giao dịch đổi phòng.");

                boolean isNewRoomBooked = dataManager.kiemTraPhongDat(newRoom.getMaPhong(), phieu.getNgayDen(),
                        phieu.getNgayDi());
                if (isNewRoomBooked) {
                    showAlert("Lỗi", "Phòng " + newRoom.getMaPhong() + " đã được đặt trong khoảng thời gian này!");
                    conn.rollback();
                    return;
                }

                chitietPhieuDatPhongDao.xoaChitietPhieuDatPhong(oldRoom.getMaDatPhong(), oldRoom.getMaPhong());
                dataManager.deleteChitietPhieuDatPhong(oldRoom.getMaDatPhong(), oldRoom.getMaPhong());
                System.out.println("Đã xóa chi tiết phòng cũ: " + oldRoom.getMaPhong() + " cho mã phiếu đặt: "
                        + phieu.getMaDatPhong());

                dataManager.refreshChitietPhieuDatPhongList();

                List<ChitietPhieuDatPhong> existingRecords = chitietPhieuDatPhongDao
                        .timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());
                ChitietPhieuDatPhong existingChitiet = existingRecords.stream()
                        .filter(ct -> ct.getMaPhong().equals(newRoom.getMaPhong())).findFirst().orElse(null);

                if (existingChitiet != null) {
                    long soNgay = Math.max(1, ChronoUnit.DAYS.between(phieu.getNgayDen(), phieu.getNgayDi()));
                    double thanhTien = newRoom.getGiaPhong() * soNgay;
                    existingChitiet.setTienPhong(newRoom.getGiaPhong());
                    existingChitiet.setThanhTien(thanhTien);
                    existingChitiet.setTrangThai("Đã đặt");
                    existingChitiet.setMoTa("Đổi phòng qua giao diện");
                    chitietPhieuDatPhongDao.suaChitietPhieuDatPhong(existingChitiet);
                    dataManager.updateChitietPhieuDatPhong(existingChitiet);
                    System.out.println("Đã cập nhật chi tiết phòng: " + newRoom.getMaPhong());
                } else {
                    long soNgay = Math.max(1, ChronoUnit.DAYS.between(phieu.getNgayDen(), phieu.getNgayDi()));
                    double thanhTien = newRoom.getGiaPhong() * soNgay;
                    ChitietPhieuDatPhong newChitiet = new ChitietPhieuDatPhong(phieu.getMaDatPhong(),
                            newRoom.getMaPhong(), "Đã đặt", "Đổi phòng qua giao diện", newRoom.getGiaPhong(), 0.0,
                            thanhTien, 1, false);
                    dataManager.addChitietPhieuDatPhong(newChitiet);
                    System.out.println("Đã thêm chi tiết phòng mới: " + newRoom.getMaPhong());
                }

                Phong oldPhong = phongList.stream().filter(p -> p.getMaPhong().equals(oldRoom.getMaPhong())).findFirst()
                        .orElse(null);
                if (oldPhong != null) {
                    boolean isOldRoomBooked = dataManager.kiemTraPhongDat(oldPhong.getMaPhong(), phieu.getNgayDen(),
                            phieu.getNgayDi());
                    String newStatus = isOldRoomBooked ? "Đã đặt" : "Trống";
                    Phong updatedOldPhong = new Phong(oldPhong.getMaPhong(), oldPhong.getLoaiPhong(),
                            oldPhong.getGiaPhong(), newStatus, oldPhong.getDonDep(), oldPhong.getViTri(),
                            oldPhong.getSoNguoiToiDa(), oldPhong.getMoTa());
                    phongDao.updatePhong(updatedOldPhong);
                    dataManager.updatePhong(updatedOldPhong);
                    System.out.println("Cập nhật trạng thái phòng cũ: " + oldPhong.getMaPhong() + " -> " + newStatus);
                }

                Phong updatedNewPhong = new Phong(newRoom.getMaPhong(), newRoom.getLoaiPhong(), newRoom.getGiaPhong(),
                        "Đã đặt", newRoom.getDonDep(), newRoom.getViTri(), newRoom.getSoNguoiToiDa(),
                        newRoom.getMoTa());
                phongDao.updatePhong(updatedNewPhong);
                dataManager.updatePhong(updatedNewPhong);
                System.out.println("Cập nhật trạng thái phòng mới: " + newRoom.getMaPhong() + " -> Đã đặt");

                conn.commit();
                System.out.println("Đã commit giao dịch đổi phòng.");
                success = true;

                updateBookingDisplayDirectly();
                showAlert("Thành công",
                        "Đã đổi phòng từ " + oldRoom.getMaPhong() + " sang " + newRoom.getMaPhong() + "!");
                contentPane.getChildren().setAll(mainPane);
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể đổi phòng: " + ex.getMessage());
                ex.printStackTrace();
                try {
                    if (conn != null) {
                        conn.rollback();
                        System.out.println("Đã rollback giao dịch do lỗi.");
                    }
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                        System.out.println("Đã đóng kết nối giao dịch.");
                    }
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnHuy.setPrefWidth(120);
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(15, btnLuu, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, footer);
        contentPane.getChildren().setAll(form);
    }

    public void themDichVuPhieuDatPhong(PhieuDatPhong phieu) {
    System.out.println(">> Bắt đầu thêm dịch vụ cho phiếu: " + phieu.getMaDatPhong());
    System.out.println(">> Trạng thái phiếu: " + phieu.getTrangThai());

    // Kiểm tra trạng thái phiếu
    if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
        showAlert("Lỗi", "Không thể thêm dịch vụ cho phiếu đã xác nhận hoặc đã hủy!");
        return;
    }

    // Tạo form giao diện
    VBox form = createCenteredForm("Thêm dịch vụ cho phiếu đặt phòng " + phieu.getMaDatPhong());

    // Tải danh sách dịch vụ
    ObservableList<DichVu> dichVuList = FXCollections.observableArrayList();
    try {
        List<DichVu> updatedDichVuList = new DichVu_Dao().getAllDichVu();
        dichVuList.addAll(updatedDichVuList);
        System.out.println(">> Số lượng dịch vụ tải được: " + updatedDichVuList.size());
    } catch (SQLException e) {
        showAlert("Lỗi", "Không thể tải danh sách dịch vụ: " + e.getMessage());
        e.printStackTrace();
        return;
    }

    if (dichVuList.isEmpty()) {
        showAlert("Lỗi", "Không có dịch vụ nào trong hệ thống!");
        return;
    }

    // Lọc dịch vụ ở trạng thái Hoạt động
    ObservableList<DichVu> filteredDichVuList = dichVuList.filtered(dv -> "Hoạt động".equals(dv.getTrangThai()));
    if (filteredDichVuList.isEmpty()) {
        showAlert("Lỗi", "Không có dịch vụ nào ở trạng thái 'Hoạt động' để sử dụng!");
        System.out.println(">> Không có dịch vụ hoạt động");
        return;
    }

    // Tải danh sách phòng trong phiếu
    ObservableList<ChitietPhieuDatPhong> chiTietList;
    try {
        chiTietList = FXCollections.observableArrayList(chitietPhieuDatPhongDao.timKiemChitietPhieuDatPhong(phieu.getMaDatPhong()));
        System.out.println(">> Số phòng trong phiếu: " + chiTietList.size());
    } catch (SQLException e) {
        showAlert("Lỗi", "Không thể tải danh sách phòng: " + e.getMessage());
        e.printStackTrace();
        return;
    }

    if (chiTietList.isEmpty()) {
        showAlert("Lỗi", "Phiếu đặt phòng không có phòng nào!");
        return;
    }

    // ComboBox cho dịch vụ
    ComboBox<DichVu> dichVuCombo = new ComboBox<>(filteredDichVuList);
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

    // ComboBox cho phòng
    ComboBox<ChitietPhieuDatPhong> phongCombo = new ComboBox<>(chiTietList);
    phongCombo.setPromptText("Chọn phòng");
    phongCombo.setPrefWidth(250);
    phongCombo.setStyle("-fx-font-size: 16px;");
    phongCombo.setConverter(new javafx.util.StringConverter<ChitietPhieuDatPhong>() {
        @Override
        public String toString(ChitietPhieuDatPhong ct) {
            return ct != null ? "Phòng: " + ct.getMaPhong() : "";
        }
        @Override
        public ChitietPhieuDatPhong fromString(String string) {
            return null;
        }
    });

    // TextField cho số lượng
    TextField soLuongField = new TextField("1");
    soLuongField.setPrefWidth(250);
    soLuongField.setStyle("-fx-font-size: 16px;");
    soLuongField.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.matches("\\d*")) {
            soLuongField.setText(newVal.replaceAll("[^\\d]", ""));
        }
    });

    // Label hiển thị tổng tiền
    Label totalLabel = new Label("Tổng tiền dịch vụ: 0 VNĐ");
    dichVuCombo.setOnAction(e -> updateServiceTotal(dichVuCombo, soLuongField, totalLabel));
    soLuongField.textProperty().addListener((obs, oldVal, newVal) -> updateServiceTotal(dichVuCombo, soLuongField, totalLabel));

    // Tạo lưới giao diện
    GridPane grid = new GridPane();
    grid.setHgap(15);
    grid.setVgap(15);
    grid.setAlignment(Pos.CENTER);
    grid.addRow(0, new Label("Dịch vụ:"), dichVuCombo);
    grid.addRow(1, new Label("Phòng sử dụng:"), phongCombo);
    grid.addRow(2, new Label("Số lượng:"), soLuongField);
    grid.addRow(3, new Label(""), totalLabel);

    // Kiểm tra mã hóa đơn
    String maHoaDon = phieu.getMaHoaDon();
    if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
        showAlert("Lỗi", "Phiếu đặt phòng không có mã hóa đơn hợp lệ!");
        return;
    }

    // Nút Thêm
    Button btnThem = new Button("Thêm");
    btnThem.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
    btnThem.setPrefWidth(120);
    btnThem.setOnAction(e -> {
        System.out.println(">> Xử lý nút Thêm dịch vụ");

        // Kiểm tra input
        DichVu selectedDichVu = dichVuCombo.getValue();
        ChitietPhieuDatPhong selectedPhong = phongCombo.getValue();
        String soLuongText = soLuongField.getText();

        if (selectedDichVu == null || selectedPhong == null || soLuongText.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn dịch vụ, phòng và nhập số lượng!");
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

        Connection conn = null;
        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false);

            double totalDichVu = selectedDichVu.getGia() * soLuong;
            System.out.println(">> Tổng tiền dịch vụ: " + totalDichVu);

            // Lấy mã nhân viên
            NhanVien currentUser = dataManager.getCurrentNhanVien();
            String maNhanVien = currentUser != null ? currentUser.getMaNhanVien() : nhanVienDao.getAllNhanVien().get(0).getMaNhanVien();
            System.out.println(">> Mã nhân viên: " + maNhanVien);

            // Tìm hóa đơn
            HoaDon hoaDon = hoaDonList.stream()
                    .filter(hd -> hd.getMaHoaDon().equals(maHoaDon) && !hd.getTrangThai())
                    .findFirst()
                    .orElse(null);
            if (hoaDon == null) {
                showAlert("Lỗi", "Không tìm thấy hóa đơn hợp lệ!");
                conn.rollback();
                return;
            }

            // Tạo phiếu dịch vụ trước
            String maPhieuDichVu = phieuDichVuDao.getNextMaPhieuDichVu();
            PhieuDichVu phieuDichVu = new PhieuDichVu(maPhieuDichVu, phieu.getMaDatPhong(), maHoaDon, LocalDate.now());
            dataManager.addPhieuDichVu(phieuDichVu); // Thêm PhieuDichVu vào cơ sở dữ liệu
            System.out.println(">> Tạo phiếu dịch vụ: " + phieuDichVu);

            // Tạo chi tiết phiếu dịch vụ
            ChitietPhieuDichVu chiTietDV = new ChitietPhieuDichVu(
                    maPhieuDichVu,
                    selectedDichVu.getMaDichVu(),
                    soLuong,
                    selectedDichVu.getGia(),
                    selectedPhong.getMaPhong()
            );
            dataManager.addChitietPhieuDichVu(chiTietDV);
            System.out.println(">> Tạo chi tiết phiếu dịch vụ: mã dịch vụ=" + selectedDichVu.getMaDichVu() + ", phòng=" + selectedPhong.getMaPhong());

            // Kiểm tra chi tiết hóa đơn
            List<ChitietHoaDon> existingCt = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(maHoaDon);
            ChitietHoaDon cthd;
            if (!existingCt.isEmpty()) {
                cthd = existingCt.get(0);
                cthd.setTienDichVu(cthd.getTienDichVu() + totalDichVu);
                cthd.setMaPhieuDichVu(maPhieuDichVu); // Sử dụng maPhieuDichVu đã thêm
                chitietHoaDonDao.suaChitietHoaDon(cthd);
                System.out.println(">> Cập nhật chi tiết hóa đơn: " + maHoaDon + ", Tổng tiền dịch vụ: " + cthd.getTienDichVu());
            } else {
                cthd = new ChitietHoaDon(
                        maHoaDon,
                        null,
                        10.0,
                        1,
                        0.0,
                        0.0,
                        totalDichVu,
                        null,
                        phieu.getMaDatPhong(),
                        maPhieuDichVu // Sử dụng maPhieuDichVu đã thêm
                );
                chitietHoaDonDao.themChitietHoaDon(cthd);
                System.out.println(">> Tạo chi tiết hóa đơn: " + maHoaDon + ", Tổng tiền dịch vụ: " + totalDichVu);
            }

            // Cập nhật tổng tiền hóa đơn
            hoaDon.setTongTien(hoaDon.getTongTien() + totalDichVu);
            dataManager.updateHoaDon(hoaDon);
            System.out.println(">> Cập nhật hóa đơn: " + hoaDon.getMaHoaDon() + ", Tổng tiền: " + hoaDon.getTongTien());

            conn.commit();
            System.out.println(">> Đã commit giao dịch thêm dịch vụ.");

            showAlert("Thành công", "Đã thêm dịch vụ " + selectedDichVu.getTenDichVu() + " cho phòng " + selectedPhong.getMaPhong() + "!");
            updateBookingDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
        } catch (SQLException ex) {
            showAlert("Lỗi", "Không thể thêm dịch vụ: " + ex.getMessage());
            ex.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println(">> Đã rollback giao dịch do lỗi.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println(">> Đã đóng kết nối giao dịch.");
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    });

    // Nút Hủy
    Button btnHuy = new Button("Hủy");
    btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
    btnHuy.setPrefWidth(120);
    btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

    // Tạo footer
    HBox footer = new HBox(15, btnThem, btnHuy);
    footer.setAlignment(Pos.CENTER);

    // Thêm các thành phần vào form
    form.getChildren().addAll(grid, footer);
    contentPane.getChildren().setAll(form);
}

    private void thanhToanPhieuDatPhong(PhieuDatPhong phieu) {
        if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
            showAlert("Lỗi", "Phiếu đặt phòng đã được xác nhận hoặc đã hủy!");
            return;
        }

        double totalThanhTien;
        double[] totalDichVu = {0};
        List<ChitietPhieuDatPhong> chiTietList;
        List<PhieuDichVu> dichVuList;

        try {
            chiTietList = chitietPhieuDatPhongDao.timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());
            totalThanhTien = chiTietList.stream().mapToDouble(ChitietPhieuDatPhong::getThanhTien).sum();

            dichVuList = dataManager.getPhieuDichVuList().stream()
                    .filter(pdv -> phieu.getMaHoaDon() != null && phieu.getMaHoaDon().equals(pdv.getMaHoaDon()))
                    .collect(Collectors.toList());
            for (PhieuDichVu pdv : dichVuList) {
                List<ChitietPhieuDichVu> chiTietDVList = chitietPhieuDichVuDao.timKiemChitietPhieuDichVu(pdv.getMaPhieuDichVu());
                totalDichVu[0] += chiTietDVList.stream().mapToDouble(ct -> ct.getDonGia() * ct.getSoLuong()).sum();
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tính tổng tiền: " + e.getMessage());
            return;
        }

        VBox form = createCenteredForm("Thanh toán phiếu đặt phòng " + phieu.getMaDatPhong());

        Label roomTitle = new Label("Chi tiết phòng");
        roomTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 0 5 0;");

        TableView<ChitietPhieuDatPhong> roomTable = new TableView<>();
        roomTable.setMaxHeight(150);
        roomTable.setStyle("-fx-font-size: 12;");

        TableColumn<ChitietPhieuDatPhong, String> roomCol = new TableColumn<>("Phòng");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("maPhong"));
        roomCol.setMinWidth(80);

        TableColumn<ChitietPhieuDatPhong, String> roomTypeCol = new TableColumn<>("Loại phòng");
        roomTypeCol.setCellValueFactory(cellData -> {
            try {
                Phong phong = phongDao.getAllPhong().stream()
                        .filter(p -> p.getMaPhong().equals(cellData.getValue().getMaPhong()))
                        .findFirst().orElse(null);
                return new SimpleStringProperty(phong != null ? phong.getLoaiPhong() : "Không xác định");
            } catch (SQLException e) {
                return new SimpleStringProperty("Lỗi truy vấn");
            }
        });
        roomTypeCol.setMinWidth(100);

        TableColumn<ChitietPhieuDatPhong, Double> roomPriceCol = new TableColumn<>("Đơn giá");
        roomPriceCol.setCellValueFactory(new PropertyValueFactory<>("tienPhong"));
        roomPriceCol.setCellFactory(col -> new TableCell<ChitietPhieuDatPhong, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        roomPriceCol.setMinWidth(100);

        TableColumn<ChitietPhieuDatPhong, Integer> daysCol = new TableColumn<>("Số ngày");
        daysCol.setCellValueFactory(cellData -> {
            long soNgay = ChronoUnit.DAYS.between(phieu.getNgayDen(), phieu.getNgayDi());
            return new javafx.beans.property.SimpleObjectProperty<>((int) Math.max(1, soNgay));
        });
        daysCol.setMinWidth(80);

        TableColumn<ChitietPhieuDatPhong, Double> roomTotalCol = new TableColumn<>("Thành tiền");
        roomTotalCol.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
        roomTotalCol.setCellFactory(col -> new TableCell<ChitietPhieuDatPhong, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        roomTotalCol.setMinWidth(120);

        roomTable.getColumns().addAll(roomCol, roomTypeCol, roomPriceCol, daysCol, roomTotalCol);
        roomTable.setItems(FXCollections.observableArrayList(chiTietList));

        Label serviceTitle = new Label("Chi tiết dịch vụ");
        serviceTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 10 0 5 0;");

        TableView<ChitietPhieuDichVu> serviceTable = new TableView<>();
        serviceTable.setMaxHeight(150);
        serviceTable.setStyle("-fx-font-size: 12;");

        TableColumn<ChitietPhieuDichVu, String> serviceCol = new TableColumn<>("Dịch vụ");
        serviceCol.setCellValueFactory(cellData -> {
            try {
                DichVu dichVu = dataManager.getDichVuList().stream()
                        .filter(dv -> dv.getMaDichVu().equals(cellData.getValue().getMaDichVu()))
                        .findFirst().orElse(null);
                return new SimpleStringProperty(dichVu != null ? dichVu.getTenDichVu() : "Không xác định");
            } catch (Exception e) {
                return new SimpleStringProperty("Lỗi truy vấn");
            }
        });
        serviceCol.setMinWidth(100);

        TableColumn<ChitietPhieuDichVu, String> serviceRoomCol = new TableColumn<>("Phòng sử dụng");
        serviceRoomCol.setCellValueFactory(new PropertyValueFactory<>("maPhong"));
        serviceRoomCol.setMinWidth(80);

        TableColumn<ChitietPhieuDichVu, Double> servicePriceCol = new TableColumn<>("Đơn giá");
        servicePriceCol.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        servicePriceCol.setCellFactory(col -> new TableCell<ChitietPhieuDichVu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        servicePriceCol.setMinWidth(100);

        TableColumn<ChitietPhieuDichVu, Integer> quantityCol = new TableColumn<>("Số lượng");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        quantityCol.setMinWidth(80);

        TableColumn<ChitietPhieuDichVu, Double> serviceTotalCol = new TableColumn<>("Thành tiền");
        serviceTotalCol.setCellValueFactory(cellData -> {
            double total = cellData.getValue().getDonGia() * cellData.getValue().getSoLuong();
            return new javafx.beans.property.SimpleObjectProperty<>(total);
        });
        serviceTotalCol.setCellFactory(col -> new TableCell<ChitietPhieuDichVu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f VNĐ", item));
            }
        });
        serviceTotalCol.setMinWidth(120);

        serviceTable.getColumns().addAll(serviceCol, serviceRoomCol, servicePriceCol, quantityCol, serviceTotalCol);
        ObservableList<ChitietPhieuDichVu> serviceItems = FXCollections.observableArrayList();
        for (PhieuDichVu pdv : dichVuList) {
            try {
                List<ChitietPhieuDichVu> chiTietDVList = chitietPhieuDichVuDao.timKiemChitietPhieuDichVu(pdv.getMaPhieuDichVu());
                serviceItems.addAll(chiTietDVList);
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể tải chi tiết dịch vụ: " + e.getMessage());
            }
        }
        serviceTable.setItems(serviceItems);

        ComboBox<KhuyenMai> promotionCombo = new ComboBox<>(khuyenMaiList.filtered(KhuyenMai::isTrangThai));
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
        Label vatLabel = new Label("Thuế VAT (10%): 0 VNĐ");
        Label finalTotalLabel = new Label(
                "Tổng tiền cuối: " + String.format("%,.0f VNĐ", totalThanhTien + totalDichVu[0]));

        double[] subtotal = { totalThanhTien + totalDichVu[0] };
        double[] discount = { 0 };
        double[] vatAmount = { 0 };
        double[] finalTotal = { totalThanhTien + totalDichVu[0] };
        String[] maKhuyenMai = { null };
        double[] chietKhau = { 0 };

        promotionCombo.setOnAction(e -> {
            KhuyenMai selected = promotionCombo.getValue();
            discount[0] = 0;
            chietKhau[0] = 0;
            maKhuyenMai[0] = null;

            if (selected != null) {
                chietKhau[0] = selected.getChietKhau();
                maKhuyenMai[0] = selected.getMaChuongTrinhKhuyenMai();
                discount[0] = totalThanhTien * (chietKhau[0] / 100);
            }

            subtotal[0] = totalThanhTien + totalDichVu[0] - discount[0];
            vatAmount[0] = subtotal[0] * VAT_RATE;
            finalTotal[0] = subtotal[0] + vatAmount[0];

            discountLabel.setText("Giảm giá: " + String.format("%,.0f VNĐ", discount[0]));
            vatLabel.setText("Thuế VAT (10%): " + String.format("%,.0f VNĐ", vatAmount[0]));
            finalTotalLabel.setText("Tổng tiền cuối: " + String.format("%,.0f VNĐ", finalTotal[0]));
        });

        VBox paymentDetails = new VBox(10);
        paymentDetails.getChildren().addAll(
                roomTitle, roomTable,
                serviceTitle, serviceTable,
                promotionCombo,
                discountLabel,
                vatLabel,
                finalTotalLabel,
                new Label("Chọn phương thức thanh toán:"));

        Button btnTienMat = new Button("Tiền mặt");
        btnTienMat.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnTienMat.setPrefWidth(120);
        btnTienMat.setOnAction(e -> {
            completePayment(phieu, finalTotal[0], "Tiền mặt", chiTietList, maKhuyenMai[0], chietKhau[0], vatAmount[0]);
            contentPane.getChildren().setAll(mainPane);
        });

        Button btnOnline = new Button("Trực tuyến");
        btnOnline.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnOnline.setPrefWidth(120);
        btnOnline.setOnAction(e -> {
            VBox onlineForm = createCenteredForm("Thanh toán trực tuyến");
            Label qrLabel = new Label("Quét mã QR để thanh toán " + String.format("%,.0f VNĐ", finalTotal[0]));

            Button btnXacNhan = new Button("Xác nhận thanh toán");
            btnXacNhan.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
            btnXacNhan.setPrefWidth(120);
            btnXacNhan.setOnAction(ev -> {
                completePayment(phieu, finalTotal[0], "Trực tuyến", chiTietList, maKhuyenMai[0], chietKhau[0], vatAmount[0]);
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

    private void completePayment(PhieuDatPhong phieu, double finalTotal, String hinhThucThanhToan,
                            List<ChitietPhieuDatPhong> chiTietList, String maKhuyenMai, double chietKhau, double vatAmount) {
    try {
        if (phieu == null || chiTietList == null) {
            showAlert("Lỗi", "Dữ liệu phiếu đặt phòng hoặc chi tiết không hợp lệ!");
            return;
        }

        NhanVien currentUser = dataManager.getCurrentNhanVien();
        if (currentUser == null) {
            List<NhanVien> nhanVienList = nhanVienDao.getAllNhanVien();
            if (nhanVienList.isEmpty()) {
                showAlert("Lỗi", "Không có nhân viên nào trong hệ thống!");
                return;
            }
            currentUser = nhanVienList.get(0);
        }
        String maNhanVien = currentUser.getMaNhanVien();

        // Cập nhật trạng thái phiếu đặt phòng
        PhieuDatPhong updatedPhieu = new PhieuDatPhong(
                phieu.getMaDatPhong(), phieu.getNgayDen(), phieu.getNgayDi(),
                phieu.getNgayDat(), phieu.getSoLuongNguoi(), "Xác nhận",
                phieu.getMaKhachHang(), phieu.getMaHoaDon()
        );
        dataManager.updatePhieuDatPhong(updatedPhieu);
        System.out.println("Đã cập nhật trạng thái phiếu đặt phòng: " + phieu.getMaDatPhong() + " -> Xác nhận");

        String maHoaDon = phieu.getMaHoaDon();
        HoaDon hoaDon = hoaDonList.stream()
                .filter(hd -> hd.getMaHoaDon().equals(maHoaDon))
                .findFirst()
                .orElse(null);
        if (hoaDon == null) {
            showAlert("Lỗi", "Không tìm thấy hóa đơn hợp lệ!");
            return;
        }
        hoaDon.setTongTien(finalTotal);
        hoaDon.setHinhThucThanhToan(hinhThucThanhToan);
        hoaDon.setTrangThai(true);
        dataManager.updateHoaDon(hoaDon);
        System.out.println("Đã cập nhật hóa đơn: " + maHoaDon + ", Tổng tiền: " + finalTotal);

        // Tổng hợp chi tiết phòng và dịch vụ
        double totalTienPhong = 0;
        double totalTienDichVu = 0;
        long soNgay = Math.max(1, ChronoUnit.DAYS.between(phieu.getNgayDen(), phieu.getNgayDi()));

        // Tính tổng tiền phòng
        for (ChitietPhieuDatPhong chiTiet : chiTietList) {
            double tienPhong = chiTiet.getTienPhong();
            double tienPhongSauGiam = tienPhong - (tienPhong * (chietKhau / 100));
            totalTienPhong += tienPhongSauGiam * soNgay;
        }

        // Tính tổng tiền dịch vụ
        List<PhieuDichVu> dichVuList = dataManager.getPhieuDichVuList().stream()
                .filter(pdv -> phieu.getMaHoaDon().equals(pdv.getMaHoaDon()))
                .collect(Collectors.toList());
        for (PhieuDichVu pdv : dichVuList) {
            List<ChitietPhieuDichVu> chiTietDVList = chitietPhieuDichVuDao.timKiemChitietPhieuDichVu(pdv.getMaPhieuDichVu());
            totalTienDichVu += chiTietDVList.stream().mapToDouble(ct -> ct.getDonGia() * ct.getSoLuong()).sum();
        }

        // Kiểm tra hoặc tạo bản ghi ChitietHoaDon
        List<ChitietHoaDon> existingCt = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(maHoaDon);
        ChitietHoaDon cthd;
        if (!existingCt.isEmpty()) {
            cthd = existingCt.get(0);
            cthd.setTienPhong(totalTienPhong);
            cthd.setTienDichVu(totalTienDichVu);
            cthd.setThueVat(VAT_RATE * 100);
            cthd.setSoNgay((int) soNgay);
            cthd.setKhuyenMai(chietKhau);
            cthd.setMaChuongTrinhKhuyenMai(maKhuyenMai);
            cthd.setMaDatPhong(phieu.getMaDatPhong());
            cthd.setMaPhieuDichVu(dichVuList.isEmpty() ? null : dichVuList.get(0).getMaPhieuDichVu());
            chitietHoaDonDao.suaChitietHoaDon(cthd);
            System.out.println("Đã cập nhật chi tiết hóa đơn: " + maHoaDon);
        } else {
            cthd = new ChitietHoaDon(
                    maHoaDon,
                    null, // Không cần maPhong vì tổng hợp tất cả phòng
                    VAT_RATE * 100,
                    (int) soNgay,
                    chietKhau,
                    totalTienPhong,
                    totalTienDichVu,
                    maKhuyenMai,
                    phieu.getMaDatPhong(),
                    dichVuList.isEmpty() ? null : dichVuList.get(0).getMaPhieuDichVu()
            );
            chitietHoaDonDao.themChitietHoaDon(cthd);
            System.out.println("Đã tạo chi tiết hóa đơn: " + maHoaDon);
        }

        // Không cập nhật trạng thái phòng thành "Trống" tại đây
        // Trạng thái phòng sẽ được xử lý sau khi thời gian đặt phòng kết thúc

        updateBookingDisplayDirectly();
        showAlert("Thành công", "Thanh toán phiếu đặt phòng " + phieu.getMaDatPhong() + " thành công!");
    } catch (SQLException ex) {
        showAlert("Lỗi", "Không thể hoàn tất thanh toán: " + ex.getMessage());
        ex.printStackTrace();
    }
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