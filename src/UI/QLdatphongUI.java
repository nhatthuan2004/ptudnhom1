package UI;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import ConnectDB.ConnectDB;

public class QLdatphongUI {
	private static final double VAT_RATE = 0;
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
	            System.out.println("updateBookingDisplay - MaDatPhong: " + phieu.getMaDatPhong());
	            for (ChitietPhieuDatPhong chiTiet : chiTietList) {
	                System.out.println("  MaPhong: " + chiTiet.getMaPhong());
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
	            Label maKhachHangLabel = new Label("Khách: " + phieu.getMaKhachHang());
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
	            Label thanhTienLabel = new Label("Thành tiền: " + String.format("%,.0f VNĐ", totalThanhTien));

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
	            btnHuyDatPhong.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 5;");
	            btnHuyDatPhong.setPrefWidth(100);
	            btnHuyDatPhong.setOnAction(e -> {
	                // Kiểm tra trạng thái phiếu
	                if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
	                    showAlert("Lỗi", "Không thể hủy phiếu đã xác nhận hoặc đã hủy!");
	                    return;
	                }

	                // Kiểm tra trạng thái thanh toán
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
	                            // Cập nhật trạng thái phòng
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

	                            // Xóa dữ liệu liên quan
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
	        // Log để debug
	        System.out.println("showBookingDetailsDialog - MaDatPhong: " + phieu.getMaDatPhong());
	        for (ChitietPhieuDatPhong chiTiet : chiTietList) {
	            System.out.println("  MaPhong: " + chiTiet.getMaPhong());
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
	    btnDoiPhong.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
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
	        System.out.println("showChangeRoomDialog - MaDatPhong: " + phieu.getMaDatPhong() + ", Số phòng: " + chiTietList.size());
	    } catch (SQLException e) {
	        showAlert("Lỗi", "Không thể tải chi tiết phòng: " + e.getMessage());
	        return;
	    }

	    ComboBox<ChitietPhieuDatPhong> oldRoomCombo = new ComboBox<>(FXCollections.observableArrayList(chiTietList));
	    oldRoomCombo.setPromptText("Chọn phòng cũ");
	    oldRoomCombo.setPrefWidth(250);
	    oldRoomCombo.setStyle("-fx-font-size: 16px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");
	    oldRoomCombo.setConverter(new javafx.util.StringConverter<ChitietPhieuDatPhong>() {
	        @Override
	        public String toString(ChitietPhieuDatPhong ct) {
	            return ct != null ? "Phòng: " + ct.getMaPhong() + " - Giá: " + String.format("%,.0f VNĐ", ct.getTienPhong()) : "";
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
	    newRoomCombo.setStyle("-fx-font-size: 16px; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");
	    newRoomCombo.setConverter(new javafx.util.StringConverter<Phong>() {
	        @Override
	        public String toString(Phong phong) {
	            return phong != null ? phong.getMaPhong() + " - " + phong.getLoaiPhong() + " - " +
	                   String.format("%,.0f VNĐ", phong.getGiaPhong()) + " (Sức chứa: " + phong.getSoNguoiToiDa() + ")" : "";
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
	                System.out.println("Tổng số phòng trong phongList: " + phongList.size());
	                for (Phong p : phongList) {
	                    System.out.println("Phòng: " + p.getMaPhong() + " - " + p.getLoaiPhong() + " - Sức chứa: " + p.getSoNguoiToiDa() + " - Trạng thái: " + p.getTrangThai());
	                }

	                int soLuongPhong = chiTietList.size();
	                int soNguoiToiDaMoiPhong = (int) Math.ceil((double) phieu.getSoLuongNguoi() / soLuongPhong);
	                System.out.println("Số lượng người trong phiếu: " + phieu.getSoLuongNguoi());
	                System.out.println("Số lượng phòng trong phiếu: " + soLuongPhong);
	                System.out.println("Số người tối đa mỗi phòng cần chứa: " + soNguoiToiDaMoiPhong);

	                for (Phong phong : phongList) {
	                    if (phong.getSoNguoiToiDa() >= soNguoiToiDaMoiPhong && !"Bảo Trì".equals(phong.getTrangThai())) {
	                        boolean isAvailable = true;

	                        if (phong.getMaPhong().equals(selectedOldRoom.getMaPhong())) {
	                            System.out.println("Phòng " + phong.getMaPhong() + " bị loại do là phòng cũ");
	                            continue;
	                        }

	                        for (ChitietPhieuDatPhong ct : chiTietList) {
	                            if (phong.getMaPhong().equals(ct.getMaPhong())) {
	                                isAvailable = false;
	                                System.out.println("Phòng " + phong.getMaPhong() + " bị loại do đã được sử dụng trong phiếu hiện tại");
	                                break;
	                            }
	                        }

	                        if (isAvailable) {
	                            boolean isBooked = dataManager.kiemTraPhongDat(phong.getMaPhong(), phieu.getNgayDen(), phieu.getNgayDi());
	                            if (isBooked) {
	                                isAvailable = false;
	                                System.out.println("Phòng " + phong.getMaPhong() + " bị loại do đã được đặt");
	                            }
	                        }

	                        if (isAvailable) {
	                            availableRooms.add(phong);
	                        }
	                    } else {
	                        System.out.println("Phòng " + phong.getMaPhong() + " bị loại do sức chứa hoặc trạng thái không phù hợp");
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
	            showAlert("Lỗi", "Vui lòng chọn phòng cũ và phòng mới!");
	            return;
	        }

	        if (oldRoom.getMaPhong().equals(newRoom.getMaPhong())) {
	            showAlert("Lỗi", "Phòng mới phải khác phòng cũ!");
	            return;
	        }

	        Connection conn = null;
	        boolean success = false; // Biến để kiểm soát trạng thái giao dịch
	        try {
	            // Bắt đầu giao dịch
	            conn = ConnectDB.getConnection();
	            conn.setAutoCommit(false);
	            System.out.println("Bắt đầu giao dịch để đổi phòng.");

	            // Kiểm tra trạng thái phòng mới
	            boolean isNewRoomBooked = dataManager.kiemTraPhongDat(newRoom.getMaPhong(), phieu.getNgayDen(), phieu.getNgayDi());
	            if (isNewRoomBooked) {
	                showAlert("Lỗi", "Phòng " + newRoom.getMaPhong() + " đã được đặt trong khoảng thời gian này!");
	                conn.rollback();
	                return;
	            }

	         // Xóa chi tiết phiếu đặt phòng của phòng cũ
	            chitietPhieuDatPhongDao.xoaChitietPhieuDatPhong(oldRoom.getMaDatPhong(), oldRoom.getMaPhong());
	            dataManager.deleteChitietPhieuDatPhong(oldRoom.getMaDatPhong(), oldRoom.getMaPhong());
	            System.out.println("Đã xóa chi tiết phòng cũ: " + oldRoom.getMaPhong() + " cho maDatPhong: " + phieu.getMaDatPhong());

	            // Làm mới danh sách chi tiết phiếu đặt phòng
	            dataManager.refreshChitietPhieuDatPhongList();

	         // Kiểm tra xem phòng mới đã tồn tại trong phiếu đặt phòng chưa
	            List<ChitietPhieuDatPhong> existingRecords = chitietPhieuDatPhongDao.timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());
	            ChitietPhieuDatPhong existingChitiet = existingRecords.stream()
	                    .filter(ct -> ct.getMaPhong().equals(newRoom.getMaPhong()))
	                    .findFirst()
	                    .orElse(null);

	            if (existingChitiet != null) {
	                // Nếu phòng mới đã tồn tại trong phiếu, cập nhật bản ghi hiện có
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
	                // Nếu không tồn tại, thêm mới
	                long soNgay = Math.max(1, ChronoUnit.DAYS.between(phieu.getNgayDen(), phieu.getNgayDi()));
	                double thanhTien = newRoom.getGiaPhong() * soNgay;
	                ChitietPhieuDatPhong newChitiet = new ChitietPhieuDatPhong(
	                        phieu.getMaDatPhong(),
	                        newRoom.getMaPhong(),
	                        "Đã đặt",
	                        "Đổi phòng qua giao diện",
	                        newRoom.getGiaPhong(),
	                        0.0,
	                        thanhTien,
	                        1,
	                        false
	                );
	                // Gọi addChitietPhieuDatPhong, phương thức này sẽ tự động kiểm tra và xử lý trùng lặp
	                dataManager.addChitietPhieuDatPhong(newChitiet);
	                System.out.println("Đã thêm chi tiết phòng mới: " + newRoom.getMaPhong());
	            }

	            // Cập nhật trạng thái phòng cũ
	            Phong oldPhong = phongList.stream()
	                    .filter(p -> p.getMaPhong().equals(oldRoom.getMaPhong()))
	                    .findFirst().orElse(null);
	            if (oldPhong != null) {
	                boolean isOldRoomBooked = dataManager.kiemTraPhongDat(oldPhong.getMaPhong(), phieu.getNgayDen(), phieu.getNgayDi());
	                String newStatus = isOldRoomBooked ? "Đã đặt" : "Trống";
	                Phong updatedOldPhong = new Phong(
	                        oldPhong.getMaPhong(), oldPhong.getLoaiPhong(), oldPhong.getGiaPhong(),
	                        newStatus, oldPhong.getDonDep(), oldPhong.getViTri(), oldPhong.getSoNguoiToiDa(), oldPhong.getMoTa()
	                );
	                phongDao.updatePhong(updatedOldPhong);
	                dataManager.updatePhong(updatedOldPhong);
	                System.out.println("Cập nhật trạng thái phòng cũ: " + oldPhong.getMaPhong() + " -> " + newStatus);
	            }

	            // Cập nhật trạng thái phòng mới
	            Phong updatedNewPhong = new Phong(
	                    newRoom.getMaPhong(), newRoom.getLoaiPhong(), newRoom.getGiaPhong(),
	                    "Đã đặt", newRoom.getDonDep(), newRoom.getViTri(), newRoom.getSoNguoiToiDa(), newRoom.getMoTa()
	            );
	            phongDao.updatePhong(updatedNewPhong);
	            dataManager.updatePhong(updatedNewPhong);
	            System.out.println("Cập nhật trạng thái phòng mới: " + newRoom.getMaPhong() + " -> Đã đặt");

	            // Commit giao dịch
	            conn.commit();
	            System.out.println("Đã commit giao dịch đổi phòng.");
	            success = true; // Đánh dấu giao dịch thành công

	            // Làm mới giao diện và hiển thị thông báo
	            updateBookingDisplayDirectly();
	            showAlert("Thành công", "Đã đổi phòng từ " + oldRoom.getMaPhong() + " sang " + newRoom.getMaPhong() + "!");
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
	                // Làm mới giao diện ngay cả khi có lỗi, vì dữ liệu đã được cập nhật đúng
	                if (!success) {
	                    updateBookingDisplayDirectly();
	                    showAlert("Thông báo", "Đổi phòng thành công nhưng có lỗi nhỏ. Dữ liệu đã được cập nhật!");
	                    contentPane.getChildren().setAll(mainPane);
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

	private void themDichVuPhieuDatPhong(PhieuDatPhong phieu) {
    System.out.println(">> Bắt đầu thêm dịch vụ cho phiếu: " + phieu.getMaDatPhong());
    System.out.println(">> Trạng thái phiếu: " + phieu.getTrangThai());

    if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
        showAlert("Lỗi", "Không thể thêm dịch vụ cho phiếu đã xác nhận hoặc đã hủy!");
        return;
    }

    VBox form = createCenteredForm("Thêm dịch vụ cho phiếu đặt phòng " + phieu.getMaDatPhong());

    try {
        dichVuList.clear();
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

    ObservableList<DichVu> filteredDichVuList = dichVuList.filtered(dv -> "Hoạt động".equals(dv.getTrangThai()));
    if (filteredDichVuList.isEmpty()) {
        showAlert("Lỗi", "Không có dịch vụ nào ở trạng thái 'Hoạt động' để sử dụng!");
        System.out.println(">> Không có dịch vụ hoạt động");
        return;
    }

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

    TextField soLuongField = new TextField("1");
    soLuongField.setPrefWidth(250);
    soLuongField.setStyle("-fx-font-size: 16px;");
    soLuongField.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.matches("\\d*")) {
            soLuongField.setText(newVal.replaceAll("[^\\d]", ""));
        }
    });

    Label totalLabel = new Label("Tổng tiền dịch vụ: 0 VNĐ");
    dichVuCombo.setOnAction(e -> updateServiceTotal(dichVuCombo, soLuongField, totalLabel));
    soLuongField.textProperty().addListener((obs, oldVal, newVal) -> updateServiceTotal(dichVuCombo, soLuongField, totalLabel));

    GridPane grid = new GridPane();
    grid.setHgap(15);
    grid.setVgap(15);
    grid.setAlignment(Pos.CENTER);
    grid.addRow(0, new Label("Dịch vụ:"), dichVuCombo);
    grid.addRow(1, new Label("Số lượng:"), soLuongField);
    grid.addRow(2, new Label(""), totalLabel);

    final String[] maHoaDon = { phieu.getMaHoaDon() };

    Button btnThem = new Button("Thêm");
    btnThem.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8 16;");
    btnThem.setPrefWidth(120);
    btnThem.setOnAction(e -> {
        System.out.println(">> Xử lý nút Thêm dịch vụ");

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

        try {
            double totalDichVu = selectedDichVu.getGia() * soLuong;
            System.out.println(">> Tổng tiền dịch vụ: " + totalDichVu);

            NhanVien currentUser = dataManager.getCurrentNhanVien();
            String maNhanVien = currentUser != null ? currentUser.getMaNhanVien()
                    : nhanVienDao.getAllNhanVien().get(0).getMaNhanVien();
            System.out.println(">> Mã nhân viên: " + maNhanVien);

            HoaDon hoaDon = null;
            if (maHoaDon[0] != null) {
                hoaDon = hoaDonList.stream()
                        .filter(hd -> hd.getMaHoaDon().equals(maHoaDon[0]) && !hd.getTrangThai())
                        .findFirst().orElse(null);
                System.out.println(">> Tìm hóa đơn: " + (hoaDon != null ? hoaDon.getMaHoaDon() : "null"));
            }

            if (hoaDon == null) {
                maHoaDon[0] = hoaDonDao.getNextMaHoaDon();
                hoaDon = new HoaDon(maHoaDon[0], LocalDateTime.now(), "Chưa xác định", totalDichVu, false,
                        phieu.getMaKhachHang(), maNhanVien);
                dataManager.addHoaDon(hoaDon);
                phieu.setMaHoaDon(maHoaDon[0]);
                dataManager.updatePhieuDatPhong(phieu);
                System.out.println(">> Tạo hóa đơn mới: " + maHoaDon[0]);
            } else {
                hoaDon.setTongTien(hoaDon.getTongTien() + totalDichVu);
                dataManager.updateHoaDon(hoaDon);
                System.out.println(">> Cập nhật hóa đơn: " + hoaDon.getMaHoaDon() + ", Tổng tiền: " + hoaDon.getTongTien());
            }

            String maPhieuDichVu = phieuDichVuDao.getNextMaPhieuDichVu();
            PhieuDichVu phieuDichVu = new PhieuDichVu(maPhieuDichVu, maHoaDon[0], LocalDate.now());
            dataManager.addPhieuDichVu(phieuDichVu);
            System.out.println(">> Tạo phiếu dịch vụ: " + maPhieuDichVu);

            ChitietPhieuDichVu chiTietDV = new ChitietPhieuDichVu(maPhieuDichVu,
                    selectedDichVu.getMaDichVu(), soLuong, selectedDichVu.getGia());
            dataManager.addChitietPhieuDichVu(chiTietDV);
            System.out.println(">> Tạo chi tiết phiếu dịch vụ: maDichVu=" + selectedDichVu.getMaDichVu());

            ChitietHoaDon cthd = new ChitietHoaDon(maHoaDon[0], null, 10.0, 1, 0.0, 0.0, totalDichVu,
                    null, phieu.getMaDatPhong(), maPhieuDichVu);
            dataManager.addChitietHoaDon(cthd);
            System.out.println(">> Tạo chi tiết hóa đơn: maHoaDon=" + maHoaDon[0] + ", maPhieuDichVu=" + maPhieuDichVu);

            showAlert("Thành công", "Đã thêm dịch vụ " + selectedDichVu.getTenDichVu() + " vào phiếu!");
            updateBookingDisplayDirectly();
            contentPane.getChildren().setAll(mainPane);
        } catch (SQLException ex) {
            showAlert("Lỗi", "Không thể thêm dịch vụ: " + ex.getMessage());
            ex.printStackTrace();
        }
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


	private void thanhToanPhieuDatPhong(PhieuDatPhong phieu) {
    if ("Xác nhận".equals(phieu.getTrangThai()) || "Đã hủy".equals(phieu.getTrangThai())) {
        showAlert("Lỗi", "Phiếu đặt phòng đã được xác nhận hoặc đã hủy!");
        return;
    }

    double totalThanhTien;
    final double[] totalDichVu = { 0 };
    List<ChitietPhieuDatPhong> chiTietList;
    final double VAT_RATE = 0.10; // Thuế VAT 10%

    try {
        // Lấy chi tiết phòng để tính tiền phòng
        chiTietList = chitietPhieuDatPhongDao.timKiemChitietPhieuDatPhong(phieu.getMaDatPhong());
        totalThanhTien = chiTietList.stream().mapToDouble(ChitietPhieuDatPhong::getThanhTien).sum();

        // Lấy tiền dịch vụ từ các dòng chi tiết hóa đơn có maPhieuDichVu
        if (phieu.getMaHoaDon() != null) {
            List<ChitietHoaDon> chiTietDichVuList = chitietHoaDonDao.getChiTietDichVuByMaHoaDon(phieu.getMaHoaDon());
            totalDichVu[0] = chiTietDichVuList.stream().mapToDouble(ChitietHoaDon::getTienDichVu).sum();
        }
    } catch (SQLException e) {
        showAlert("Lỗi", "Không thể tính tổng tiền: " + e.getMessage());
        return;
    }

    VBox form = createCenteredForm("Thanh toán phiếu đặt phòng " + phieu.getMaDatPhong());

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

    double[] subtotal = { totalThanhTien + totalDichVu[0] }; // Tổng tiền trước giảm giá và thuế
    double[] discount = { 0 };
    double[] vatAmount = { 0 };
    double[] finalTotal = { totalThanhTien + totalDichVu[0] }; // Tổng tiền sau giảm giá và thuế
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
            discount[0] = totalThanhTien * (chietKhau[0] / 100); // Giảm giá chỉ áp dụng cho tiền phòng
        }

        // Tính tổng tiền sau giảm giá
        subtotal[0] = totalThanhTien + totalDichVu[0] - discount[0];
        // Tính thuế VAT (10%) trên tổng sau giảm giá
        vatAmount[0] = subtotal[0] * VAT_RATE;
        // Tổng tiền cuối cùng bao gồm thuế
        finalTotal[0] = subtotal[0] + vatAmount[0];

        discountLabel.setText("Giảm giá: " + String.format("%,.0f VNĐ", discount[0]));
        vatLabel.setText("Thuế VAT (10%): " + String.format("%,.0f VNĐ", vatAmount[0]));
        finalTotalLabel.setText("Tổng tiền cuối: " + String.format("%,.0f VNĐ", finalTotal[0]));
    });

    VBox paymentDetails = new VBox(10);
    paymentDetails.getChildren().addAll(
            new Label("Tổng tiền phòng: " + String.format("%,.0f VNĐ", totalThanhTien)),
            new Label("Tổng tiền dịch vụ: " + String.format("%,.0f VNĐ", totalDichVu[0])),
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
            completePayment(phieu, finalTotal[0], "Online", chiTietList, maKhuyenMai[0], chietKhau[0], vatAmount[0]);
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
        // Lấy nhân viên hiện tại
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

        // Cập nhật trạng thái phòng về Trống
        for (ChitietPhieuDatPhong chiTiet : chiTietList) {
            Phong phong = phongList.stream()
                    .filter(p -> p.getMaPhong().equals(chiTiet.getMaPhong()))
                    .findFirst().orElse(null);
            if (phong != null) {
                Phong updatedPhong = new Phong(phong.getMaPhong(), phong.getLoaiPhong(), phong.getGiaPhong(),
                        "Trống", phong.getDonDep(), phong.getViTri(), phong.getSoNguoiToiDa(), phong.getMoTa());
                dataManager.updatePhong(updatedPhong);
            }
        }

        // Cập nhật trạng thái phiếu đặt phòng
        PhieuDatPhong updatedPhieu = new PhieuDatPhong(phieu.getMaDatPhong(), phieu.getNgayDen(), phieu.getNgayDi(),
                phieu.getNgayDat(), phieu.getSoLuongNguoi(), "Xác nhận", phieu.getMaKhachHang(),
                phieu.getMaHoaDon());
        dataManager.updatePhieuDatPhong(updatedPhieu);

        // Xác định hóa đơn
        String maHoaDon;
        HoaDon hoaDon = hoaDonList.stream()
                .filter(hd -> {
                    try {
                        return chitietHoaDonDao.getChitietHoaDonByMaDatPhong(phieu.getMaDatPhong())
                                .stream()
                                .anyMatch(ct -> ct.getMaHoaDon().equals(hd.getMaHoaDon())) && !hd.getTrangThai();
                    } catch (SQLException e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);

        if (hoaDon != null) {
            // Hóa đơn đã tồn tại → cập nhật
            maHoaDon = hoaDon.getMaHoaDon();
            hoaDon.setTongTien(finalTotal);
            hoaDon.setTrangThai(true);
            hoaDon.setHinhThucThanhToan(hinhThucThanhToan);
            dataManager.updateHoaDon(hoaDon);
        } else {
            // Chưa có → tạo mới
            maHoaDon = hoaDonDao.getNextMaHoaDon();
            hoaDon = new HoaDon(maHoaDon, LocalDateTime.now(), hinhThucThanhToan, finalTotal, true,
                    phieu.getMaKhachHang(), maNhanVien);
            dataManager.addHoaDon(hoaDon);
        }

        // Thêm chi tiết hóa đơn phòng
        for (ChitietPhieuDatPhong chiTiet : chiTietList) {
            long soNgay = ChronoUnit.DAYS.between(phieu.getNgayDen(), phieu.getNgayDi());
            soNgay = Math.max(1, soNgay);
            double tienPhong = chiTiet.getTienPhong();
            double tienPhongSauGiam = tienPhong - (tienPhong * (chietKhau / 100));
            double vatPhong = tienPhongSauGiam * VAT_RATE; // Thuế VAT cho từng phòng

            // Kiểm tra nếu đã tồn tại chi tiết phòng thì bỏ qua
            if (!chitietHoaDonDao.kiemTraTonTaiChiTietPhong(maHoaDon, chiTiet.getMaPhong())) {
                ChitietHoaDon cthd = new ChitietHoaDon(maHoaDon, chiTiet.getMaPhong(), VAT_RATE * 100, (int) soNgay,
                        chietKhau, tienPhongSauGiam, 0.0, maKhuyenMai, phieu.getMaDatPhong(), null);
                chitietHoaDonDao.themChitietHoaDon(cthd);
                dataManager.addChitietHoaDon(cthd);
            }
        }

        // Thêm chi tiết hóa đơn dịch vụ nếu chưa có
        List<ChitietHoaDon> existingDetails = chitietHoaDonDao.getChitietHoaDonByMaDatPhong(phieu.getMaDatPhong());
        for (ChitietHoaDon cthd : existingDetails) {
            if (cthd.getMaPhieuDichVu() != null) {
                boolean daTonTai = chitietHoaDonDao.kiemTraTonTaiPhieuDichVu(maHoaDon, cthd.getMaPhieuDichVu());
                if (!daTonTai) {
                    double vatDichVu = cthd.getTienDichVu() * VAT_RATE; // Thuế VAT cho dịch vụ
                    ChitietHoaDon newCthd = new ChitietHoaDon(maHoaDon, null, VAT_RATE * 100, 1, 0.0, 0.0,
                            cthd.getTienDichVu(), null, phieu.getMaDatPhong(), cthd.getMaPhieuDichVu());
                    chitietHoaDonDao.themChitietHoaDon(newCthd);
                    dataManager.addChitietHoaDon(newCthd);
                }
            }
        }

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