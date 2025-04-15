package UI;

import dao.ChitietPhieuDatPhong_Dao;
import dao.Phong_Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;

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
    }

    public void showUI(Stage primaryStage, String role) {
        this.currentRole = role;
        BorderPane layout = getUI(role);
        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quản Lý Phòng");
        primaryStage.show();
    }

    private ObservableList<Phong> getFilteredPhongList() {
        String searchText = tfTimKiem != null ? tfTimKiem.getText().trim().toLowerCase() : "";
        String trangThai = cbTrangThai != null ? cbTrangThai.getValue() : "Tất cả";
        String loaiPhong = cbLoaiPhong != null ? cbLoaiPhong.getValue() : "Tất cả";

        ObservableList<Phong> filteredList = FXCollections.observableArrayList();
        for (Phong phong : phongList) {
            boolean matchesSearch = searchText.isEmpty() || phong.getMaPhong().toLowerCase().contains(searchText)
                    || (phong.getViTri() != null && phong.getViTri().toLowerCase().contains(searchText))
                    || (phong.getMoTa() != null && phong.getMoTa().toLowerCase().contains(searchText));

            boolean matchesTrangThai = "Tất cả".equals(trangThai) || phong.getTrangThai().equals(trangThai);

            boolean matchesLoaiPhong = "Tất cả".equals(loaiPhong) || phong.getLoaiPhong().equals(loaiPhong);

            if ((matchesSearch && matchesTrangThai && matchesLoaiPhong) || (showAllCheckBox != null && showAllCheckBox.isSelected())) {
                filteredList.add(phong);
            }
        }
        return filteredList;
    }

    private void updateRoomDisplay(ObservableList<Phong> displayList) {
        if (roomFlowPane == null) return;
        roomFlowPane.getChildren().clear();

        for (Phong phong : displayList) {
            try {
                VBox roomBox = new VBox(8);
                roomBox.setPrefSize(200, 200);
                roomBox.setPadding(new Insets(10));
                roomBox.setAlignment(Pos.CENTER_LEFT);

                String trangThaiHienTai = phong.getTrangThai();
                String tenKhachHang = "Không có";
                String maDatPhong = "Không có";

                List<ChitietPhieuDatPhong> bookings = chitietPhieuDatPhongDao
                        .timKiemChitietPhieuDatPhong(phong.getMaPhong());
                for (ChitietPhieuDatPhong booking : bookings) {
                    PhieuDatPhong phieu = phieuDatPhongList.stream()
                            .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong())).findFirst().orElse(null);
                    if (phieu != null && !"Đã hủy".equals(phieu.getTrangThai())
                            && "Đã đặt".equals(booking.getTrangThai())) {
                        KhachHang khachHang = khachHangList.stream()
                                .filter(kh -> kh.getMaKhachHang().equals(phieu.getMaKhachHang())).findFirst()
                                .orElse(null);
                        tenKhachHang = khachHang != null ? khachHang.getTenKhachHang() : "Không xác định";
                        maDatPhong = phieu.getMaDatPhong();
                        break;
                    }
                }

                String bgColor = switch (trangThaiHienTai) {
                    case "Trống" -> "#90EE90";
                    case "Đã đặt" -> "#FFD700";
                    case "Đang sửa" -> "#FF6347";
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
            } catch (SQLException e) {
                showAlert("Lỗi", "Không thể kiểm tra trạng thái đặt phòng: " + e.getMessage());
            }
        }
    }

    private void showRoomDetailsForm(StackPane contentPane, Phong phong) {
        if (!"Quản lý".equals(currentRole)) {
            return;
        }

        VBox form = createCenteredForm("Chi tiết phòng " + phong.getMaPhong());

        TextField tfMaPhong = new TextField(phong.getMaPhong());
        tfMaPhong.setDisable(true);
        ComboBox<String> loaiPhongCombo = new ComboBox<>(FXCollections.observableArrayList("Đơn", "Đôi", "VIP"));
        loaiPhongCombo.setValue(phong.getLoaiPhong());
        TextField tfGiaPhong = new TextField(String.valueOf(phong.getGiaPhong()));
        ComboBox<String> trangThaiCombo = new ComboBox<>(
                FXCollections.observableArrayList("Trống", "Đã đặt", "Đang sửa"));
        trangThaiCombo.setValue(phong.getTrangThai());
        ComboBox<String> donDepCombo = new ComboBox<>(
                FXCollections.observableArrayList("Sạch", "Chưa dọn"));
        donDepCombo.setValue(phong.getDonDep());
        TextField tfViTri = new TextField(phong.getViTri());
        TextField tfSoNguoiToiDa = new TextField(String.valueOf(phong.getSoNguoiToiDa()));
        TextArea taMoTa = new TextArea(phong.getMoTa());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Mã Phòng:"), tfMaPhong);
        grid.addRow(1, new Label("Loại Phòng:"), loaiPhongCombo);
        grid.addRow(2, new Label("Giá Phòng:"), tfGiaPhong);
        grid.addRow(3, new Label("Trạng Thái:"), trangThaiCombo);
        grid.addRow(4, new Label("Dọn Dẹp:"), donDepCombo);
        grid.addRow(5, new Label("Vị Trí:"), tfViTri);
        grid.addRow(6, new Label("Số Người Tối Đa:"), tfSoNguoiToiDa);
        grid.addRow(7, new Label("Mô Tả:"), taMoTa);

        String tenKhachHang = "Không có";
        String maDatPhong = "Không có";
        String trangThaiChiTiet = "Không có";

        try {
            List<ChitietPhieuDatPhong> bookings = chitietPhieuDatPhongDao
                    .timKiemChitietPhieuDatPhong(phong.getMaPhong());
            for (ChitietPhieuDatPhong booking : bookings) {
                PhieuDatPhong phieu = phieuDatPhongList.stream()
                        .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                        .findFirst()
                        .orElse(null);
                if (phieu != null && !"Đã hủy".equals(phieu.getTrangThai())
                        && "Đã đặt".equals(booking.getTrangThai())) {
                    KhachHang khachHang = khachHangList.stream()
                            .filter(kh -> kh.getMaKhachHang().equals(phieu.getMaKhachHang()))
                            .findFirst()
                            .orElse(null);
                    tenKhachHang = khachHang != null ? khachHang.getTenKhachHang() : "Không xác định";
                    maDatPhong = phieu.getMaDatPhong();
                    trangThaiChiTiet = booking.getTrangThai();
                    break;
                }
            }
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể kiểm tra trạng thái đặt phòng: " + e.getMessage());
        }

        Label khachHangLabel = new Label("Khách hàng: " + tenKhachHang);
        Label datPhongLabel = new Label("Phiếu đặt: " + maDatPhong);
        Label trangThaiChiTietLabel = new Label("Trạng thái đặt: " + trangThaiChiTiet);

        VBox content = new VBox(10, khachHangLabel, datPhongLabel, trangThaiChiTietLabel);
        content.setPadding(new Insets(10));

        Button btnLuu = new Button("Lưu");
        btnLuu.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;");
        btnLuu.setOnAction(e -> {
            String loaiPhong = loaiPhongCombo.getValue();
            String giaPhongText = tfGiaPhong.getText().trim();
            String trangThai = trangThaiCombo.getValue();
            String donDep = donDepCombo.getValue();
            String viTri = tfViTri.getText().trim();
            String soNguoiToiDaText = tfSoNguoiToiDa.getText().trim();
            String moTa = taMoTa.getText().trim();

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

            boolean isBooked = false;
            try {
                List<ChitietPhieuDatPhong> bookings = chitietPhieuDatPhongDao
                        .timKiemChitietPhieuDatPhong(phong.getMaPhong());
                for (ChitietPhieuDatPhong booking : bookings) {
                    PhieuDatPhong phieu = phieuDatPhongList.stream()
                            .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                            .findFirst()
                            .orElse(null);
                    if (phieu != null && !"Đã hủy".equals(phieu.getTrangThai())
                            && "Đã đặt".equals(booking.getTrangThai())) {
                        isBooked = true;
                        break;
                    }
                }
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể kiểm tra trạng thái đặt phòng: " + ex.getMessage());
                return;
            }

            if (isBooked && !"Đã đặt".equals(trangThai)) {
                showAlert("Lỗi", "Phòng đang có đặt phòng, không thể thay đổi trạng thái khác 'Đã đặt'!");
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
                dataManager.updatePhong(updatedPhong);
                updateRoomDisplay(getFilteredPhongList());
                contentPane.getChildren().clear();
                contentPane.getChildren().add(roomListPane);
                roomListPane.setVisible(true);
                showAlert("Thành công", "Cập nhật phòng " + phong.getMaPhong() + " thành công!");
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể sửa phòng: " + ex.getMessage());
            }
        });

        Button btnXoa = new Button("Xóa");
        btnXoa.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 5;");
        btnXoa.setOnAction(e -> {
            boolean isBooked = false;
            try {
                List<ChitietPhieuDatPhong> bookings = chitietPhieuDatPhongDao
                        .timKiemChitietPhieuDatPhong(phong.getMaPhong());
                for (ChitietPhieuDatPhong booking : bookings) {
                    PhieuDatPhong phieu = phieuDatPhongList.stream()
                            .filter(p -> p.getMaDatPhong().equals(booking.getMaDatPhong()))
                            .findFirst()
                            .orElse(null);
                    if (phieu != null && !"Đã hủy".equals(phieu.getTrangThai())
                            && "Đã đặt".equals(booking.getTrangThai())) {
                        isBooked = true;
                        break;
                    }
                }
            } catch (SQLException ex) {
                showAlert("Lỗi", "Không thể kiểm tra trạng thái đặt phòng: " + ex.getMessage());
                return;
            }

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
        btnQuayLai.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(roomListPane);
            roomListPane.setVisible(true);
            updateRoomDisplay(getFilteredPhongList());
        });

        HBox footer = new HBox(10, btnLuu, btnXoa, btnQuayLai);
        footer.setAlignment(Pos.CENTER);

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
        ComboBox<String> loaiPhongCombo = new ComboBox<>();
        loaiPhongCombo.getItems().addAll("Đơn", "Đôi", "VIP");
        loaiPhongCombo.setPromptText("Chọn loại phòng");
        TextField tfGiaPhong = new TextField();
        tfGiaPhong.setPromptText("Giá phòng (VD: 300000)");
        ComboBox<String> trangThaiCombo = new ComboBox<>();
        trangThaiCombo.getItems().addAll("Trống", "Đang sửa");
        trangThaiCombo.setPromptText("Chọn trạng thái");
        ComboBox<String> donDepCombo = new ComboBox<>();
        donDepCombo.getItems().addAll("Sạch", "Chưa dọn");
        donDepCombo.setValue("Sạch");
        TextField tfViTri = new TextField();
        tfViTri.setPromptText("Vị trí (e.g., Tầng 4)");
        TextField tfSoNguoiToiDa = new TextField();
        tfSoNguoiToiDa.setPromptText("Số người tối đa (e.g., 2)");
        TextArea taMoTa = new TextArea();
        taMoTa.setPromptText("Mô tả (e.g., Phòng sạch sẽ)");

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
        btnQuayLai.setOnAction(e -> {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(roomListPane);
            roomListPane.setVisible(true);
            updateRoomDisplay(getFilteredPhongList());
        });

        HBox footer = new HBox(10, btnThem, btnQuayLai);
        footer.setAlignment(Pos.CENTER);

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

        Label labelGio = new Label("Giờ:");
        labelGio.setMinWidth(labelWidth);
        cbGio = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            cbGio.getItems().add(String.format("%02d:00", i));
        }
        cbGio.setPromptText("Chọn giờ");
        cbGio.setPrefWidth(250);
        cbGio.setStyle("-fx-font-size: 14px;");
        HBox hboxGio = new HBox(10, labelGio, cbGio);
        hboxGio.setAlignment(Pos.CENTER_LEFT);

        Label labelTrangThaiPhong = new Label("Trạng thái phòng:");
        labelTrangThaiPhong.setMinWidth(labelWidth);
        cbTrangThai = new ComboBox<>();
        cbTrangThai.getItems().addAll("Tất cả", "Trống", "Đã đặt", "Đang sửa");
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
        infoPane.getChildren().addAll(tfTimKiem, hboxNgay, hboxGio, hboxTrangThaiPhong, hboxLoaiPhong, hboxShowAll, hboxAddRoom);

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
        dpNgay.setOnAction(e -> updateRoomDisplay(getFilteredPhongList()));
        cbGio.setOnAction(e -> updateRoomDisplay(getFilteredPhongList()));

        dataManager.addPhongListChangeListener(() -> updateRoomDisplay(getFilteredPhongList()));
        dataManager.addPhieuDatPhongListChangeListener(() -> updateRoomDisplay(getFilteredPhongList()));
        dataManager.addChitietPhieuDatPhongListChangeListener(() -> updateRoomDisplay(getFilteredPhongList()));

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
}