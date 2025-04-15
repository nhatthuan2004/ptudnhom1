package UI;

import dao.ChitietPhieuDatPhong_Dao;
import dao.LichSuChuyenPhong_DAO;
import dao.PhieuDatPhong_Dao;
import dao.Phong_Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.ChitietPhieuDatPhong;
import model.LichSuChuyenPhong;
import model.PhieuDatPhong;
import model.Phong;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class DoiPhongUI {

    private final LichSuChuyenPhong_DAO lichSuDao;
    private final PhieuDatPhong_Dao phieuDatPhongDao;
    private final ChitietPhieuDatPhong_Dao chitietPhieuDatPhongDao;
    private final Phong_Dao phongDao;
    private final DataManager dataManager;
    private final ObservableList<PhieuDatPhong> phieuDatPhongList;
    private FlowPane bookingFlowPane;
    private StackPane contentPane;
    private StackPane mainPane;

    public DoiPhongUI() {
        try {
            lichSuDao = new LichSuChuyenPhong_DAO();
            phieuDatPhongDao = new PhieuDatPhong_Dao();
            chitietPhieuDatPhongDao = new ChitietPhieuDatPhong_Dao();
            phongDao = new Phong_Dao();
        } catch (Exception e) {
            throw new RuntimeException("Không thể kết nối tới cơ sở dữ liệu!", e);
        }

        dataManager = DataManager.getInstance();
        phieuDatPhongList = dataManager.getPhieuDatPhongList();
        contentPane = new StackPane();
        mainPane = createMainPane();
        contentPane.getChildren().add(mainPane);
        dataManager.addPhieuDatPhongListChangeListener(this::updateBookingDisplayDirectly);
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: white;");

        // UserInfoBox
        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("User Info Placeholder"));
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

        Label titleLabel = new Label("Đổi Phòng");
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
            filteredList.addAll(phieuDatPhongList.filtered(p -> !"Xác nhận".equals(p.getTrangThai()) && !"Đã hủy".equals(p.getTrangThai())));
        } else {
            try {
                List<PhieuDatPhong> searchResults = phieuDatPhongDao.timKiemPhieuDatPhong(searchText);
                filteredList.addAll(searchResults.stream()
                        .filter(p -> !"Xác nhận".equals(p.getTrangThai()) && !"Đã hủy".equals(p.getTrangThai()))
                        .toList());
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể tìm kiếm phiếu đặt phòng: " + e.getMessage());
            }
        }
        updateBookingDisplay(filteredList);
    }

    private void updateBookingDisplay(ObservableList<PhieuDatPhong> displayList) {
        bookingFlowPane.getChildren().clear();
        for (PhieuDatPhong phieu : displayList) {
            if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
                continue; // Skip confirmed or canceled bookings
            }
            try {
                List<ChitietPhieuDatPhong> chiTietList = chitietPhieuDatPhongDao
                        .timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());

                VBox bookingBox = new VBox(8);
                bookingBox.setPrefSize(250, 200);
                bookingBox.setPadding(new Insets(10));
                bookingBox.setAlignment(Pos.CENTER_LEFT);
                String bgColor = "Chưa xác nhận".equals(phieu.getTrangThai()) ? "#FFD700" : "#90EE90";
                bookingBox.setStyle("-fx-background-color: " + bgColor
                        + "; -fx-background-radius: 10; -fx-border-radius: 10; "
                        + "-fx-border-color: #666; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                bookingBox.setOnMouseClicked(e -> showChangeRoomDialog(phieu));

                Label maPhieuLabel = new Label("Phiếu: " + phieu.getMaDatPhong());
                maPhieuLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                Label maKhachHangLabel = new Label("Khách: " + phieu.getMaKhachHang());
                Label ngayDenLabel = new Label(
                        "Ngày đến: " + (phieu.getNgayDen() != null ? phieu.getNgayDen() : "N/A"));
                Label ngayDiLabel = new Label("Ngày đi: " + (phieu.getNgayDi() != null ? phieu.getNgayDi() : "N/A"));
                Label soLuongNguoiLabel = new Label("Số người: " + phieu.getSoLuongNguoi());
                Label trangThaiLabel = new Label("Trạng thái: " + phieu.getTrangThai());

                StringBuilder phongInfo = new StringBuilder("Phòng: ");
                for (ChitietPhieuDatPhong chiTiet : chiTietList) {
                    phongInfo.append(chiTiet.getMaPhong()).append(", ");
                }
                Label phongLabel = new Label(
                        phongInfo.length() > 2 ? phongInfo.substring(0, phongInfo.length() - 2) : "Chưa chọn phòng");

                maPhieuLabel.setMaxWidth(230);
                maKhachHangLabel.setMaxWidth(230);
                ngayDenLabel.setMaxWidth(230);
                ngayDiLabel.setMaxWidth(230);
                soLuongNguoiLabel.setMaxWidth(230);
                trangThaiLabel.setMaxWidth(230);
                phongLabel.setMaxWidth(230);
                phongLabel.setWrapText(true);

                bookingBox.getChildren().addAll(maPhieuLabel, maKhachHangLabel, ngayDenLabel, ngayDiLabel,
                        soLuongNguoiLabel, trangThaiLabel, phongLabel);
                bookingFlowPane.getChildren().add(bookingBox);
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể tải chi tiết phiếu đặt phòng: " + e.getMessage());
            }
        }
    }

    private void updateBookingDisplayDirectly() {
        updateBookingDisplay(phieuDatPhongList.filtered(p -> !"Xác nhận".equals(p.getTrangThai()) && !"Đã hủy".equals(p.getTrangThai())));
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

    private void showChangeRoomDialog(PhieuDatPhong phieu) {
        if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
            showAlert("Lỗi", "Không thể đổi phòng cho phiếu đã xác nhận hoặc đã hủy!");
            return;
        }

        VBox form = createCenteredForm("Đổi phòng cho phiếu " + phieu.getMaDatPhong());

        Label maPhieuLabel = new Label("Mã phiếu: " + phieu.getMaDatPhong());
        Label maKhachHangLabel = new Label("Khách hàng: " + phieu.getMaKhachHang());

        ComboBox<String> phongCuCombo = new ComboBox<>();
        phongCuCombo.setPromptText("Chọn phòng cũ");
        phongCuCombo.setPrefWidth(250);
        phongCuCombo.setStyle("-fx-font-size: 16px;");

        try {
            List<ChitietPhieuDatPhong> chiTietList = chitietPhieuDatPhongDao.timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());
            for (ChitietPhieuDatPhong chiTiet : chiTietList) {
                phongCuCombo.getItems().add(chiTiet.getMaPhong());
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải danh sách phòng: " + e.getMessage());
            return;
        }

        ComboBox<Phong> phongMoiCombo = new ComboBox<>();
        phongMoiCombo.setPromptText("Chọn phòng mới");
        phongMoiCombo.setPrefWidth(250);
        phongMoiCombo.setStyle("-fx-font-size: 16px;");
        try {
            ObservableList<Phong> availablePhongList = dataManager.getPhongList().filtered(p -> "Trống".equals(p.getTrangThai()));
            phongMoiCombo.setItems(availablePhongList);
            phongMoiCombo.setConverter(new javafx.util.StringConverter<Phong>() {
                @Override
                public String toString(Phong phong) {
                    return phong != null ? phong.getMaPhong() + " - " + phong.getLoaiPhong() : "";
                }

                @Override
                public Phong fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showAlert("Lỗi", "Không thể tải danh sách phòng trống: " + e.getMessage());
            return;
        }

        TextField lyDoField = new TextField();
        lyDoField.setPromptText("Lý do đổi phòng");
        lyDoField.setPrefWidth(250);
        lyDoField.setStyle("-fx-font-size: 16px;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Phòng cũ:"), phongCuCombo);
        grid.addRow(1, new Label("Phòng mới:"), phongMoiCombo);
        grid.addRow(2, new Label("Lý do:"), lyDoField);

        Button btnDoiPhong = new Button("Đổi phòng");
        btnDoiPhong.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnDoiPhong.setPrefWidth(120);
        btnDoiPhong.setOnAction(e -> {
            String maPhongCu = phongCuCombo.getValue();
            Phong phongMoi = phongMoiCombo.getValue();
            String lyDo = lyDoField.getText().trim();

            if (maPhongCu == null || phongMoi == null || lyDo.isEmpty()) {
                showAlert("Lỗi", "Vui lòng chọn phòng cũ, phòng mới và nhập lý do!");
                return;
            }

            if (maPhongCu.equals(phongMoi.getMaPhong())) {
                showAlert("Lỗi", "Phòng mới không được trùng với phòng cũ!");
                return;
            }

            try {
                // Update chi tiết phiếu đặt phòng
                List<ChitietPhieuDatPhong> chiTietList = chitietPhieuDatPhongDao.timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());
                ChitietPhieuDatPhong chiTietToUpdate = chiTietList.stream()
                        .filter(ct -> ct.getMaPhong().equals(maPhongCu))
                        .findFirst()
                        .orElse(null);

                if (chiTietToUpdate == null) {
                    showAlert("Lỗi", "Không tìm thấy chi tiết phòng cũ trong phiếu!");
                    return;
                }

                // Update phòng cũ về trạng thái Trống
                Phong phongCu = phongDao.getAllPhong().stream()
                        .filter(p -> p.getMaPhong().equals(maPhongCu))
                        .findFirst()
                        .orElse(null);
                if (phongCu != null) {
                    Phong updatedPhongCu = new Phong(phongCu.getMaPhong(), phongCu.getLoaiPhong(),
                            phongCu.getGiaPhong(), "Trống", phongCu.getDonDep(), phongCu.getViTri(),
                            phongCu.getSoNguoiToiDa(), phongCu.getMoTa());
                    dataManager.updatePhong(updatedPhongCu);
                }

                // Update phòng mới về trạng thái Đặt
                Phong updatedPhongMoi = new Phong(phongMoi.getMaPhong(), phongMoi.getLoaiPhong(),
                        phongMoi.getGiaPhong(), "Đặt", phongMoi.getDonDep(), phongMoi.getViTri(),
                        phongMoi.getSoNguoiToiDa(), phongMoi.getMoTa());
                dataManager.updatePhong(updatedPhongMoi);

                // Update chi tiết phiếu đặt phòng
                ChitietPhieuDatPhong updatedChiTiet = new ChitietPhieuDatPhong(
                        chiTietToUpdate.getMaDatPhong(),
                        phongMoi.getMaPhong(),
                        lyDo, lyDo, chiTietToUpdate.getTienPhong(),
                        chiTietToUpdate.getSoLuong(),
                        chiTietToUpdate.getThanhTien(), 0, false
                );
                dataManager.updateChitietPhieuDatPhong(updatedChiTiet);

                // Lưu lịch sử đổi phòng
                String maLichSu = lichSuDao.getNextMaLichSu();
                String maNhanVien = dataManager.getCurrentNhanVien() != null
                        ? dataManager.getCurrentNhanVien().getMaNhanVien()
                        : "NV001"; // Fallback if no current user
                LichSuChuyenPhong lichSu = new LichSuChuyenPhong(
                        maLichSu,
                        phieu.getMaDatPhong(),
                        maPhongCu,
                        phongMoi.getMaPhong(),
                        LocalDateTime.now(),
                        lyDo,
                        maNhanVien
                );
                lichSuDao.themLichSuChuyenPhong(lichSu);

                showAlert("Thành công", "Đổi phòng thành công từ " + maPhongCu + " sang " + phongMoi.getMaPhong() + "!");
                updateBookingDisplayDirectly();
                contentPane.getChildren().setAll(mainPane);
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể đổi phòng: " + ex.getMessage());
            }
        });

        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
        btnHuy.setPrefWidth(120);
        btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(15, btnDoiPhong, btnHuy);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(maPhieuLabel, maKhachHangLabel, grid, footer);
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