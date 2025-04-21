package UI;

import dao.ChitietPhieuDatPhong_Dao;
import dao.Phong_Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ChitietPhieuDatPhong;
import model.KhachHang;
import model.PhieuDatPhong;
import model.Phong;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class QLphongUI {
    private final DataManager dataManager = DataManager.getInstance();
    private final ObservableList<Phong> phongList = dataManager.getPhongList();
    private final ObservableList<KhachHang> khachHangList = dataManager.getKhachHangList();
    private final ObservableList<PhieuDatPhong> phieuDatPhongList = dataManager.getPhieuDatPhongList();
    private final ObservableList<ChitietPhieuDatPhong> chitietPhieuDatPhongList = dataManager
            .getChitietPhieuDatPhongList();
    private FlowPane roomFlowPane;
    private TextField tfTimKiem;
    private ComboBox<String> cbTrangThai;
    private ComboBox<String> cbLoaiPhong;
    private DatePicker dpNgay;
    private ComboBox<String> cbGio;
    private CheckBox showAllCheckBox;
    private String currentRole;
    private final Phong_Dao phongDao = new Phong_Dao();
    private final ChitietPhieuDatPhong_Dao chitietPhieuDatPhongDao = new ChitietPhieuDatPhong_Dao();
    private GridPane roomListPane;

    public QLphongUI() {
        DataManager.getInstance().addPhieuDatPhongListChangeListener(this::refreshRoomDisplay);
        DataManager.getInstance().addChitietPhieuDatPhongListChangeListener(this::refreshRoomDisplay);
    }

    private void initialize() {
        try {
            DataManager.getInstance().refreshBookingData();
            updateRoomDisplay(getFilteredPhongList());
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể làm mới dữ liệu: " + e.getMessage());
        }
    }

    public void showUI(Stage primaryStage, String role) {
        this.currentRole = role;
        BorderPane layout = getUI(role);
        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quản Lý Phòng");
        initialize();
        primaryStage.show();
    }

    private ObservableList<Phong> getFilteredPhongList() {
        ObservableList<Phong> filteredList = FXCollections.observableArrayList();
        String keyword = tfTimKiem.getText().trim().toLowerCase();
        String selectedStatus = cbTrangThai.getValue();
        String selectedType = cbLoaiPhong.getValue();
        LocalDate selectedDate = dpNgay.getValue();

        for (Phong phong : phongList) {
            boolean matches = true;

            if (!keyword.isEmpty()) {
                matches = phong.getMaPhong().toLowerCase().contains(keyword) ||
                        phong.getLoaiPhong().toLowerCase().contains(keyword);
            }

            if (selectedStatus != null && !selectedStatus.equals("Tất cả")) {
                String trangThaiHienTai = determineRoomStatus(phong, selectedDate);
                matches = matches && selectedStatus.equals(trangThaiHienTai);
            }

            if (selectedType != null && !selectedType.equals("Tất cả")) {
                matches = matches && phong.getLoaiPhong().equals(selectedType);
            }

            if (matches) {
                filteredList.add(phong);
            }
        }

        return filteredList;
    }

    // New method to determine room status with "Bảo Trì" priority
    private String determineRoomStatus(Phong phong, LocalDate selectedDate) {
        System.out.println("Kiểm tra trạng thái phòng: " + phong.getMaPhong() + ", ngày: " + selectedDate + ", trạng thái hiện tại: " + phong.getTrangThai());
        // Ưu tiên trạng thái "Bảo Trì"
        if (phong.getTrangThai() != null && phong.getTrangThai().equals("Bảo Trì")) {
            System.out.println("Trả về trạng thái Bảo Trì cho phòng: " + phong.getMaPhong());
            return "Bảo Trì";
        }

        // Kiểm tra đặt phòng nếu có ngày được chọn
        if (selectedDate != null) {
            boolean isBooked = chitietPhieuDatPhongList.stream()
                    .filter(ct -> ct.getMaPhong().equals(phong.getMaPhong()))
                    .anyMatch(booking -> {
                        PhieuDatPhong phieu = phieuDatPhongList.stream()
                                .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                                .findFirst()
                                .orElse(null);
                        if (phieu != null && !"Đã hủy".equalsIgnoreCase(phieu.getTrangThai())) {
                            LocalDate phieuNgayDen = phieu.getNgayDen();
                            LocalDate phieuNgayDi = phieu.getNgayDi();
                            return phieuNgayDen != null && phieuNgayDi != null &&
                                    (selectedDate.isEqual(phieuNgayDen) || selectedDate.isEqual(phieuNgayDi) ||
                                     (selectedDate.isAfter(phieuNgayDen) && selectedDate.isBefore(phieuNgayDi)));
                        }
                        return false;
                    });
            if (isBooked) {
                return "Đã đặt";
            }
        }


        // Mặc định là "Trống" nếu không có đặt phòng và không phải "Bảo Trì"
        System.out.println("Trả về trạng thái Trống cho phòng: " + phong.getMaPhong());
        return "Trống";
    }

    private void updateRoomDisplay(ObservableList<Phong> displayList) {
        if (roomFlowPane == null) return;
        roomFlowPane.getChildren().clear();

        LocalDate selectedDate = dpNgay.getValue();

        for (Phong phong : displayList) {
            VBox roomBox = new VBox(8);
            roomBox.setPrefSize(200, 200);
            roomBox.setPadding(new Insets(10));
            roomBox.setAlignment(Pos.CENTER_LEFT);

            String trangThaiHienTai = determineRoomStatus(phong, selectedDate);
            String tenKhachHang = "Không có";
            String maDatPhong = "Không có";

            if ("Đã đặt".equals(trangThaiHienTai) && selectedDate != null) {
                ChitietPhieuDatPhong booking = chitietPhieuDatPhongList.stream()
                        .filter(ct -> ct.getMaPhong().equals(phong.getMaPhong()))
                        .filter(ct -> {
                            PhieuDatPhong phieu = phieuDatPhongList.stream()
                                    .filter(p -> p.getMaDatPhong().equals(ct.getMaDatPhong()))
                                    .findFirst()
                                    .orElse(null);
                            if (phieu != null && !"Đã hủy".equalsIgnoreCase(phieu.getTrangThai())) {
                                LocalDate phieuNgayDen = phieu.getNgayDen();
                                LocalDate phieuNgayDi = phieu.getNgayDi();
                                return phieuNgayDen != null && phieuNgayDi != null &&
                                        (selectedDate.isEqual(phieuNgayDen) || selectedDate.isEqual(phieuNgayDi) ||
                                         (selectedDate.isAfter(phieuNgayDen) && selectedDate.isBefore(phieuNgayDi)));
                            }
                            return false;
                        })
                        .findFirst()
                        .orElse(null);
                if (booking != null) {
                    PhieuDatPhong phieu = phieuDatPhongList.stream()
                            .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                            .findFirst()
                            .orElse(null);
                    if (phieu != null) {
                        maDatPhong = phieu.getMaDatPhong();
                        tenKhachHang = khachHangList.stream()
                                .filter(kh -> kh.getMaKhachHang().equals(phieu.getMaKhachHang()))
                                .findFirst()
                                .map(KhachHang::getTenKhachHang)
                                .orElse("Không xác định");
                    }
                }
            }

            String bgColor = switch (trangThaiHienTai) {
                case "Trống" -> "#90EE90";
                case "Đã đặt" -> "#FFD700";
                case "Bảo Trì" -> "#FF6347";
                default -> "#E0E0E0";
            };
            roomBox.setStyle("-fx-background-color: " + bgColor
                    + "; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #666; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            roomBox.setOnMouseClicked(e -> {
                if (e.getClickCount() == 1 && "Quản lý".equals(currentRole)) {
                    StackPane contentPane = (StackPane) roomBox.getScene().getRoot().lookup("#contentPane");
                    if (contentPane != null) {
                        showRoomDetailsForm(contentPane, phong);
                    }
                }
            });

            Label maPhongLabel = new Label("Phòng: " + phong.getMaPhong());
            maPhongLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label loaiPhongLabel = new Label("Loại: " + phong.getLoaiPhong());
            Label trangThaiLabel = new Label("Trạng thái: " + trangThaiHienTai);
            Label khachHangLabel = new Label("Khách: " + tenKhachHang);
            Label viTriLabel = new Label(
                    "Vị trí: " + (phong.getViTri() != null ? phong.getViTri() : "Chưa xác định"));
            Label moTaLabel = new Label("Mô tả: " + (phong.getMoTa() != null ? phong.getMoTa() : "Chưa có mô tả"));
            Label giaPhongLabel = new Label("Giá: " + String.format("%,.0f VNĐ", phong.getGiaPhong()));

            maPhongLabel.setMaxWidth(180);
            loaiPhongLabel.setMaxWidth(180);
            trangThaiLabel.setMaxWidth(180);
            khachHangLabel.setMaxWidth(180);
            viTriLabel.setMaxWidth(180);
            moTaLabel.setMaxWidth(180);
            giaPhongLabel.setMaxWidth(180);

            roomBox.getChildren().addAll(maPhongLabel, loaiPhongLabel, trangThaiLabel, khachHangLabel, viTriLabel,
                    moTaLabel, giaPhongLabel);
            roomFlowPane.getChildren().add(roomBox);
        }
    }

    private void showRoomDetailsForm(StackPane contentPane, Phong phong) {
        if (!"Quản lý".equals(currentRole)) {
            return;
        }

        VBox form = createCenteredForm("Chi tiết phòng " + phong.getMaPhong());

        TextField tfMaPhong = new TextField(phong.getMaPhong());
        tfMaPhong.setDisable(true);
        tfMaPhong.setPrefWidth(250);
        tfMaPhong.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        ComboBox<String> loaiPhongCombo = new ComboBox<>(FXCollections.observableArrayList("Đơn", "Đôi", "VIP"));
        loaiPhongCombo.setValue(phong.getLoaiPhong());
        loaiPhongCombo.setPrefWidth(250);
        loaiPhongCombo.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfGiaPhong = new TextField(String.valueOf(phong.getGiaPhong()));
        tfGiaPhong.setPrefWidth(250);
        tfGiaPhong.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        ObservableList<String> trangThaiOptions = FXCollections.observableArrayList("Trống", "Bảo Trì", "Đã đặt");
        ComboBox<String> trangThaiCombo = new ComboBox<>(trangThaiOptions);
        trangThaiCombo.setPrefWidth(250);
        trangThaiCombo.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        // Theo dõi thay đổi trạng thái ComboBox
        trangThaiCombo.setOnAction(e -> {
            System.out.println("Trạng thái ComboBox thay đổi thành: " + trangThaiCombo.getValue());
        });

        ComboBox<String> donDepCombo = new ComboBox<>(FXCollections.observableArrayList("Sạch", "Chưa dọn"));
        donDepCombo.setValue(phong.getDonDep());
        donDepCombo.setPrefWidth(250);
        donDepCombo.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfViTri = new TextField(phong.getViTri());
        tfViTri.setPrefWidth(250);
        tfViTri.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfSoNguoiToiDa = new TextField(String.valueOf(phong.getSoNguoiToiDa()));
        tfSoNguoiToiDa.setPrefWidth(250);
        tfSoNguoiToiDa.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextArea taMoTa = new TextArea(phong.getMoTa());
        taMoTa.setPrefWidth(250);
        taMoTa.setPrefHeight(80);
        taMoTa.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        LocalDate selectedDate = dpNgay.getValue() != null ? dpNgay.getValue() : LocalDate.now();
        String trangThaiHienTai = determineRoomStatus(phong, selectedDate);
        System.out.println("determineRoomStatus trả về: " + trangThaiHienTai + " cho phòng: " + phong.getMaPhong() + ", ngày: " + selectedDate);
        trangThaiCombo.setValue(trangThaiHienTai);

        String tenKhachHang = "Không có";
        String maDatPhong = "Không có";
        String trangThaiChiTiet = "Không có";
        final boolean isBooked;

        if (selectedDate != null) {
            isBooked = chitietPhieuDatPhongList.stream()
                    .filter(ct -> ct.getMaPhong().equals(phong.getMaPhong()))
                    .anyMatch(booking -> {
                        PhieuDatPhong phieu = phieuDatPhongList.stream()
                                .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                                .findFirst()
                                .orElse(null);
                        if (phieu != null && !"Đã hủy".equalsIgnoreCase(phieu.getTrangThai())) {
                            LocalDate phieuNgayDen = phieu.getNgayDen();
                            LocalDate phieuNgayDi = phieu.getNgayDi();
                            return phieuNgayDen != null && phieuNgayDi != null &&
                                    (selectedDate.isEqual(phieuNgayDen) || selectedDate.isEqual(phieuNgayDi) ||
                                     (selectedDate.isAfter(phieuNgayDen) && selectedDate.isBefore(phieuNgayDi)));
                        }
                        return false;
                    });

            if (isBooked) {
                ChitietPhieuDatPhong booking = chitietPhieuDatPhongList.stream()
                        .filter(ct -> ct.getMaPhong().equals(phong.getMaPhong()))
                        .filter(ct -> {
                            PhieuDatPhong phieu = phieuDatPhongList.stream()
                                    .filter(p -> p.getMaDatPhong().equals(ct.getMaDatPhong()))
                                    .findFirst()
                                    .orElse(null);
                            if (phieu != null && !"Đã hủy".equalsIgnoreCase(phieu.getTrangThai())) {
                                LocalDate phieuNgayDen = phieu.getNgayDen();
                                LocalDate phieuNgayDi = phieu.getNgayDi();
                                return phieuNgayDen != null && phieuNgayDi != null &&
                                        (selectedDate.isEqual(phieuNgayDen) || selectedDate.isEqual(phieuNgayDi) ||
                                         (selectedDate.isAfter(phieuNgayDen) && selectedDate.isBefore(phieuNgayDi)));
                            }
                            return false;
                        })
                        .findFirst()
                        .orElse(null);
                if (booking != null) {
                    PhieuDatPhong phieu = phieuDatPhongList.stream()
                            .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                            .findFirst()
                            .orElse(null);
                    if (phieu != null) {
                        maDatPhong = phieu.getMaDatPhong();
                        trangThaiChiTiet = booking.getTrangThai();
                        tenKhachHang = khachHangList.stream()
                                .filter(kh -> kh.getMaKhachHang().equals(phieu.getMaKhachHang()))
                                .findFirst()
                                .map(KhachHang::getTenKhachHang)
                                .orElse("Không xác định");
                    }
                }
            }
        } else {
            isBooked = false;
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(120);
        col1.setHalignment(HPos.RIGHT);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(250);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.addRow(0, new Label("Mã Phòng:"), tfMaPhong);
        grid.addRow(1, new Label("Loại Phòng:"), loaiPhongCombo);
        grid.addRow(2, new Label("Giá Phòng:"), tfGiaPhong);
        grid.addRow(3, new Label("Trạng Thái:"), trangThaiCombo);
        grid.addRow(4, new Label("Dọn Dẹp:"), donDepCombo);
        grid.addRow(5, new Label("Vị Trí:"), tfViTri);
        grid.addRow(6, new Label("Số Người Tối Đa:"), tfSoNguoiToiDa);
        grid.addRow(7, new Label("Mô Tả:"), taMoTa);

        Label khachHangLabel = new Label("Khách hàng: " + tenKhachHang);
        khachHangLabel.setStyle("-fx-font-size: 14px;");
        Label datPhongLabel = new Label("Phiếu đặt: " + maDatPhong);
        datPhongLabel.setStyle("-fx-font-size: 14px;");
        Label trangThaiChiTietLabel = new Label("Trạng thái đặt: " + trangThaiChiTiet);
        trangThaiChiTietLabel.setStyle("-fx-font-size: 14px;");

        VBox content = new VBox(10, khachHangLabel, datPhongLabel, trangThaiChiTietLabel);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;");
        btnLuu.setPrefWidth(100);
        btnLuu.setOnAction(e -> {
            String loaiPhong = loaiPhongCombo.getValue();
            String giaPhongText = tfGiaPhong.getText().trim();
            String trangThai = trangThaiCombo.getValue();
            String donDep = donDepCombo.getValue();
            String viTri = tfViTri.getText().trim();
            String soNguoiToiDaText = tfSoNguoiToiDa.getText().trim();
            String moTa = taMoTa.getText().trim();

            System.out.println("Trạng thái được chọn từ giao diện trước khi lưu: " + trangThai);

            if (loaiPhong == null || giaPhongText.isEmpty() || trangThai == null || donDep == null
                    || viTri.isEmpty() || soNguoiToiDaText.isEmpty() || moTa.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            double giaPhong;
            try {
                giaPhong = Double.parseDouble(giaPhongText);
                if (giaPhong <= 0) {
                    showAlert("Lỗi", "Giá phòng phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Giá phòng phải là số hợp lệ!");
                return;
            }

            int soNguoiToiDa;
            try {
                soNguoiToiDa = Integer.parseInt(soNguoiToiDaText);
                if (soNguoiToiDa <= 0) {
                    showAlert("Lỗi", "Số người tối đa phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Số người tối đa phải là số nguyên hợp lệ!");
                return;
            }

            // Kiểm tra trạng thái phòng dựa trên ngày được chọn
            String trangThaiDung = determineRoomStatus(phong, selectedDate);
            System.out.println("Trạng thái đúng theo determineRoomStatus: " + trangThaiDung + " cho ngày: " + selectedDate);
            if (trangThaiDung.equals("Đã đặt") && !trangThai.equals("Đã đặt") && !trangThai.equals("Bảo Trì")) {
                showAlert("Lỗi", "Phòng đang có đặt phòng vào ngày " + selectedDate + ", trạng thái chỉ có thể là 'Đã đặt' hoặc 'Bảo Trì'!");
                return;
            }
            if (!trangThaiDung.equals("Đã đặt") && trangThai.equals("Đã đặt")) {
                showAlert("Lỗi", "Phòng không có đặt phòng vào ngày " + selectedDate + ", không thể đặt trạng thái 'Đã đặt'!");
                return;
            }
            if (trangThai.equals("Bảo Trì") && trangThaiDung.equals("Đã đặt")) {
                showAlert("Lỗi", "Phòng đang có đặt phòng vào ngày " + selectedDate + ", không thể chuyển sang 'Bảo Trì'!");
                return;
            }

            try {
                Phong updatedPhong = new Phong(
                        phong.getMaPhong(),
                        loaiPhong,
                        giaPhong,
                        trangThai,
                        donDep,
                        viTri,
                        soNguoiToiDa,
                        moTa
                );
                System.out.println("Trước khi lưu - Phòng: " + updatedPhong.getMaPhong() + ", Trạng thái: " + updatedPhong.getTrangThai());
                dataManager.updatePhong(updatedPhong);
                System.out.println("Sau khi lưu - Phòng: " + updatedPhong.getMaPhong() + ", Trạng thái: " + updatedPhong.getTrangThai());
                // Làm mới dữ liệu
                dataManager.refreshBookingData();
                updateRoomDisplay(getFilteredPhongList());
                contentPane.getChildren().clear();
                contentPane.getChildren().add(roomListPane);
                roomListPane.setVisible(true);
                showAlert("Thành công", "Cập nhật phòng " + phong.getMaPhong() + " thành công!");
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể cập nhật phòng: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        Button btnXoa = new Button("Xóa");
        btnXoa.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;");
        btnXoa.setPrefWidth(100);
        btnXoa.setOnAction(e -> {
            if (isBooked) {
                showAlert("Lỗi", "Không thể xóa phòng đang có đặt phòng!");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận xóa");
            confirm.setHeaderText("Bạn có chắc muốn xóa phòng này?");
            confirm.setContentText("Phòng: " + phong.getMaPhong() + "\nLoại: " + phong.getLoaiPhong());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        dataManager.deletePhong(phong.getMaPhong());
                        dataManager.refreshBookingData();
                        updateRoomDisplay(getFilteredPhongList());
                        contentPane.getChildren().clear();
                        contentPane.getChildren().add(roomListPane);
                        roomListPane.setVisible(true);
                        showAlert("Thành công", "Xóa phòng " + phong.getMaPhong() + " thành công!");
                    } catch (SQLException ex) {
                        showAlert("Lỗi", "Không thể xóa phòng: " + ex.getMessage());
                    }
                }
            });
        });

        Button btnQuayLai = new Button("Quay lại");
        btnQuayLai.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;");
        btnQuayLai.setPrefWidth(100);
        btnQuayLai.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(roomListPane);
            roomListPane.setVisible(true);
            try {
                dataManager.refreshBookingData();
                updateRoomDisplay(getFilteredPhongList());
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể làm mới dữ liệu: " + ex.getMessage());
            }
        });

        HBox footer = new HBox(10, btnLuu, btnXoa, btnQuayLai);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));

        form.getChildren().addAll(grid, content, footer);

        contentPane.getChildren().clear();
        contentPane.getChildren().add(form);
    }

    private void showAddRoomForm(StackPane contentPane) {
        if (!"Quản lý".equals(currentRole)) {
            showAlert("Lỗi", "Chỉ quản lý mới có quyền thêm phòng!");
            return;
        }

        VBox form = createCenteredForm("Thêm Phòng Mới");

        TextField tfMaPhong = new TextField();
        tfMaPhong.setPromptText("Mã phòng (e.g., P401)");
        tfMaPhong.setPrefWidth(250);
        tfMaPhong.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        ComboBox<String> loaiPhongCombo = new ComboBox<>();
        loaiPhongCombo.getItems().addAll("Đơn", "Đôi", "VIP");
        loaiPhongCombo.setPromptText("Chọn loại phòng");
        loaiPhongCombo.setPrefWidth(250);
        loaiPhongCombo.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfGiaPhong = new TextField();
        tfGiaPhong.setPromptText("Giá phòng (VD: 300000)");
        tfGiaPhong.setPrefWidth(250);
        tfGiaPhong.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        ComboBox<String> trangThaiCombo = new ComboBox<>();
        trangThaiCombo.getItems().addAll("Trống", "Bảo Trì");
        trangThaiCombo.setPromptText("Chọn trạng thái");
        trangThaiCombo.setPrefWidth(250);
        trangThaiCombo.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        ComboBox<String> donDepCombo = new ComboBox<>();
        donDepCombo.getItems().addAll("Sạch", "Chưa dọn");
        donDepCombo.setValue("Sạch");
        donDepCombo.setPrefWidth(250);
        donDepCombo.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfViTri = new TextField();
        tfViTri.setPromptText("Vị trí (e.g., Tầng 4)");
        tfViTri.setPrefWidth(250);
        tfViTri.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextField tfSoNguoiToiDa = new TextField();
        tfSoNguoiToiDa.setPromptText("Số người tối đa (e.g., 2)");
        tfSoNguoiToiDa.setPrefWidth(250);
        tfSoNguoiToiDa.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        TextArea taMoTa = new TextArea();
        taMoTa.setPromptText("Mô tả (e.g., Phòng sạch sẽ)");
        taMoTa.setPrefWidth(250);
        taMoTa.setPrefHeight(80);
        taMoTa.setStyle("-fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");

        try {
            String nextMaPhong = phongDao.getNextMaPhong();
            tfMaPhong.setText(nextMaPhong);
            tfMaPhong.setDisable(true);
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể sinh mã phòng: " + e.getMessage());
            return;
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(120);
        col1.setHalignment(HPos.RIGHT);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(250);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.addRow(0, new Label("Mã Phòng:"), tfMaPhong);
        grid.addRow(1, new Label("Loại Phòng:"), loaiPhongCombo);
        grid.addRow(2, new Label("Giá Phòng:"), tfGiaPhong);
        grid.addRow(3, new Label("Trạng Thái:"), trangThaiCombo);
        grid.addRow(4, new Label("Dọn Dẹp:"), donDepCombo);
        grid.addRow(5, new Label("Vị Trí:"), tfViTri);
        grid.addRow(6, new Label("Số Người Tối Đa:"), tfSoNguoiToiDa);
        grid.addRow(7, new Label("Mô Tả:"), taMoTa);

        Button btnThem = new Button("Thêm");
        btnThem.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;");
        btnThem.setPrefWidth(100);
        btnThem.setOnAction(e -> {
            String maPhong = tfMaPhong.getText().trim();
            String loaiPhong = loaiPhongCombo.getValue();
            String giaPhongText = tfGiaPhong.getText().trim();
            String trangThai = trangThaiCombo.getValue();
            String donDep = donDepCombo.getValue();
            String viTri = tfViTri.getText().trim();
            String soNguoiToiDaText = tfSoNguoiToiDa.getText().trim();
            String moTa = taMoTa.getText().trim();

            if (maPhong.isEmpty() || loaiPhong == null || giaPhongText.isEmpty() || trangThai == null
                    || donDep == null || viTri.isEmpty() || soNguoiToiDaText.isEmpty() || moTa.isEmpty()) {
                showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            double giaPhong;
            try {
                giaPhong = Double.parseDouble(giaPhongText);
                if (giaPhong <= 0) {
                    showAlert("Lỗi", "Giá phòng phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Giá phòng phải là số hợp lệ!");
                return;
            }

            int soNguoiToiDa;
            try {
                soNguoiToiDa = Integer.parseInt(soNguoiToiDaText);
                if (soNguoiToiDa <= 0) {
                    showAlert("Lỗi", "Số người tối đa phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Số người tối đa phải là số nguyên hợp lệ!");
                return;
            }

            try {
                if (phongList.stream().anyMatch(p -> p.getMaPhong().equals(maPhong))) {
                    showAlert("Lỗi", "Mã phòng " + maPhong + " đã tồn tại!");
                    return;
                }
                Phong newPhong = new Phong(maPhong, loaiPhong, giaPhong, trangThai, donDep, viTri, soNguoiToiDa, moTa);
                dataManager.addPhong(newPhong);
                dataManager.refreshBookingData();
                updateRoomDisplay(getFilteredPhongList());
                contentPane.getChildren().clear();
                contentPane.getChildren().add(roomListPane);
                roomListPane.setVisible(true);
                showAlert("Thành công", "Thêm phòng " + maPhong + " thành công!");
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể thêm phòng: " + ex.getMessage());
            }
        });

        Button btnQuayLai = new Button("Quay lại");
        btnQuayLai.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;");
        btnQuayLai.setPrefWidth(100);
        btnQuayLai.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(roomListPane);
            roomListPane.setVisible(true);
            try {
                dataManager.refreshBookingData();
                updateRoomDisplay(getFilteredPhongList());
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể làm mới dữ liệu: " + ex.getMessage());
            }
        });

        HBox footer = new HBox(10, btnThem, btnQuayLai);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));

        form.getChildren().addAll(grid, footer);

        contentPane.getChildren().clear();
        contentPane.getChildren().add(form);
    }

    private void deleteRoom() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Xóa phòng");
        dialog.setHeaderText("Nhập mã phòng cần xóa:");
        dialog.setContentText("Mã phòng:");

        dialog.showAndWait().ifPresent(maPhong -> {
            Phong phongToDelete = phongList.stream().filter(p -> p.getMaPhong().equals(maPhong)).findFirst()
                    .orElse(null);

            if (phongToDelete == null) {
                showAlert("Lỗi", "Không tìm thấy phòng với mã " + maPhong);
                return;
            }

            if (!"Trống".equals(phongToDelete.getTrangThai())) {
                showAlert("Lỗi", "Chỉ có thể xóa phòng đang trống.");
                return;
            }

            try {
                dataManager.deletePhong(maPhong);
                updateRoomDisplay(getFilteredPhongList());
                showAlert("Thành công", "Xóa phòng " + maPhong + " thành công!");
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể xóa phòng: " + e.getMessage());
            }
        });
    }

    public BorderPane getUI(String role) {
        this.currentRole = role;

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #f0f0f0;");

        HBox userInfoBox;
        try {
            userInfoBox = UserInfoBox.createUserInfoBox();
        } catch (Exception e) {
            userInfoBox = new HBox(new Label("User Info Placeholder"));
            userInfoBox.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        }
        userInfoBox.setPrefSize(200, 50);
        userInfoBox.setAlignment(Pos.CENTER);
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f0f0f0;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(spacer, userInfoBox);
        layout.setTop(header);

        StackPane contentPane = new StackPane();
        contentPane.setId("contentPane");
        layout.setCenter(contentPane);

        roomListPane = new GridPane();
        roomListPane.setPadding(new Insets(10));
        roomListPane.setHgap(25);

        VBox infoPane = new VBox(15);
        infoPane.setPadding(new Insets(10, 20, 20, 20));
        infoPane.setAlignment(Pos.TOP_LEFT);
        infoPane.setPrefWidth(300);

        double labelWidth = 120;

        Label labelNgay = new Label("Ngày:");
        labelNgay.setMinWidth(labelWidth);
        dpNgay = new DatePicker();
        dpNgay.setPrefWidth(250);
        dpNgay.setStyle("-fx-font-size: 14px;");
        HBox hboxNgay = new HBox(10, labelNgay, dpNgay);
        hboxNgay.setAlignment(Pos.CENTER_LEFT);

        Label labelTrangThaiPhong = new Label("Trạng thái phòng:");
        labelTrangThaiPhong.setMinWidth(labelWidth);
        cbTrangThai = new ComboBox<>();
        cbTrangThai.getItems().addAll("Tất cả", "Trống", "Đã đặt", "Bảo Trì");
        cbTrangThai.setValue("Tất cả");
        cbTrangThai.setPrefWidth(250);
        cbTrangThai.setStyle("-fx-font-size: 14px;");
        HBox hboxTrangThaiPhong = new HBox(10, labelTrangThaiPhong, cbTrangThai);
        hboxTrangThaiPhong.setAlignment(Pos.CENTER_LEFT);

        Label labelLoaiPhong = new Label("Loại phòng:");
        labelLoaiPhong.setMinWidth(labelWidth);
        cbLoaiPhong = new ComboBox<>();
        cbLoaiPhong.getItems().add("Tất cả");
        cbLoaiPhong.getItems().addAll(phongList.stream().map(Phong::getLoaiPhong).distinct().sorted().toList());
        cbLoaiPhong.setValue("Tất cả");
        cbLoaiPhong.setPrefWidth(250);
        cbLoaiPhong.setStyle("-fx-font-size: 14px;");
        HBox hboxLoaiPhong = new HBox(10, labelLoaiPhong, cbLoaiPhong);
        hboxLoaiPhong.setAlignment(Pos.CENTER_LEFT);

        showAllCheckBox = new CheckBox("Hiển thị tất cả phòng");
        showAllCheckBox.setStyle("-fx-font-size: 14px;");
        showAllCheckBox.setOnAction(e -> updateRoomDisplay(getFilteredPhongList()));
        HBox hboxShowAll = new HBox(10, new Label(""), showAllCheckBox);
        hboxShowAll.setAlignment(Pos.CENTER_LEFT);

        Button addRoomButton = new Button("+ Thêm Phòng");
        addRoomButton.setStyle(
                "-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 6;");
        addRoomButton.setPrefWidth(250);
        addRoomButton.setVisible("Quản lý".equals(role));
        Tooltip.install(addRoomButton, new Tooltip("Thêm một phòng mới vào hệ thống"));
        addRoomButton.setOnAction(e -> showAddRoomForm(contentPane));
        HBox hboxAddRoom = new HBox(10, new Label(""), addRoomButton);
        hboxAddRoom.setAlignment(Pos.CENTER_LEFT);

        tfTimKiem = new TextField();
        tfTimKiem.setPromptText("Nhập mã phòng, vị trí hoặc mô tả");
        infoPane.getChildren().addAll(tfTimKiem, hboxNgay, hboxTrangThaiPhong, hboxLoaiPhong, hboxShowAll, hboxAddRoom);
        VBox roomPane = new VBox(10);
        roomPane.setPadding(new Insets(10, 25, 25, 25));
        roomPane.setAlignment(Pos.TOP_CENTER);
        roomPane.setPrefWidth(550);
        roomPane.setMaxHeight(465);

        roomFlowPane = new FlowPane();
        roomFlowPane.setHgap(20);
        roomFlowPane.setVgap(20);
        roomFlowPane.setAlignment(Pos.CENTER);
        roomFlowPane.setPrefWidth(530);

        ScrollPane scrollPane = new ScrollPane(roomFlowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        roomPane.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        roomListPane.add(infoPane, 0, 0);
        roomListPane.add(roomPane, 1, 0);
        GridPane.setValignment(infoPane, VPos.TOP);
        GridPane.setValignment(roomPane, VPos.TOP);
        GridPane.setHgrow(roomPane, Priority.ALWAYS);

        VBox centerLayout = new VBox(roomListPane);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        StackPane.setAlignment(centerLayout, Pos.CENTER);

        contentPane.getChildren().add(centerLayout);

        updateRoomDisplay(getFilteredPhongList());

        tfTimKiem.textProperty().addListener((obs, oldValue, newValue) -> updateRoomDisplay(getFilteredPhongList()));
        cbTrangThai.setOnAction(e -> updateRoomDisplay(getFilteredPhongList()));
        cbLoaiPhong.setOnAction(e -> updateRoomDisplay(getFilteredPhongList()));
        dpNgay.setOnAction(e -> {
            try {
                dataManager.updatePhongStatusByDate(dpNgay.getValue());
                updateRoomDisplay(getFilteredPhongList());
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể cập nhật trạng thái phòng: " + ex.getMessage());
            }
        });

        dataManager.addPhongListChangeListener(() -> updateRoomDisplay(getFilteredPhongList()));

        return layout;
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
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
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

	private void refreshRoomDisplay() {
	}
}