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

public class QLphongUI {
	private final ObservableList<Phong> phongList;
	private final ObservableList<HoaDon> hoaDonList;
	private final ObservableList<DichVu> dichVuList;
	private final ObservableList<KhachHang> khachHangList;
	private final ObservableList<ChuongTrinhKhuyenMai> khuyenMaiList;
	private FlowPane roomFlowPane;
	private StackPane contentPane;
	private StackPane mainPane;
	private final List<HoaDon> hoaDonPhongList = new ArrayList<>(); // Danh sách final để quản lý hóa đơn phòng

	private DatePicker dpNgay;
	private ComboBox<String> cbGio;
	private ComboBox<String> cbTrangThaiPhong;
	private ComboBox<String> cbLoaiPhong;
	private ComboBox<String> cbTrangThaiDonDep;
	private CheckBox showAllCheckBox;

	public QLphongUI() {
		DataManager dataManager = DataManager.getInstance();
		this.phongList = dataManager.getPhongList();
		this.hoaDonList = dataManager.getHoaDonList();
		this.khachHangList = dataManager.getKhachHangList();
		this.dichVuList = dataManager.getDichVuList();
		this.khuyenMaiList = dataManager.getKhuyenMaiList();
		this.contentPane = new StackPane();
		this.mainPane = createMainPane();
		dataManager.addPhongListChangeListener(this::updateRoomDisplayDirectly);
	}

	private StackPane createMainPane() {
		// Giữ nguyên code từ phiên bản trước
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

		VBox infoPane = new VBox(15);
		infoPane.setPadding(new Insets(10, 20, 20, 20));
		infoPane.setAlignment(Pos.TOP_LEFT);

		double labelWidth = 120;

		Label labelNgay = new Label("Ngày:");
		labelNgay.setMinWidth(labelWidth);
		dpNgay = new DatePicker(LocalDate.now());
		dpNgay.setPrefWidth(200);
		HBox hboxNgay = new HBox(10, labelNgay, dpNgay);
		hboxNgay.setAlignment(Pos.CENTER_LEFT);

		Label labelGio = new Label("Giờ:");
		labelGio.setMinWidth(labelWidth);
		cbGio = new ComboBox<>();
		for (int i = 0; i < 24; i++) {
			cbGio.getItems().add(String.format("%02d:00", i));
		}
		cbGio.setPromptText("Chọn giờ");
		cbGio.setPrefWidth(200);
		HBox hboxGio = new HBox(10, labelGio, cbGio);
		hboxGio.setAlignment(Pos.CENTER_LEFT);

		Label labelTrangThaiPhong = new Label("Trạng thái phòng:");
		labelTrangThaiPhong.setMinWidth(labelWidth);
		cbTrangThaiPhong = new ComboBox<>();
		cbTrangThaiPhong.getItems().addAll("Trống", "Đã đặt", "Đang sửa");
		cbTrangThaiPhong.setPromptText("Chọn trạng thái");
		cbTrangThaiPhong.setPrefWidth(200);
		HBox hboxTrangThaiPhong = new HBox(10, labelTrangThaiPhong, cbTrangThaiPhong);
		hboxTrangThaiPhong.setAlignment(Pos.CENTER_LEFT);

		Label labelLoaiPhong = new Label("Loại phòng:");
		labelLoaiPhong.setMinWidth(labelWidth);
		cbLoaiPhong = new ComboBox<>();
		cbLoaiPhong.getItems().addAll("Đơn", "Đôi", "VIP");
		cbLoaiPhong.setPromptText("Chọn loại phòng");
		cbLoaiPhong.setPrefWidth(200);
		HBox hboxLoaiPhong = new HBox(10, labelLoaiPhong, cbLoaiPhong);
		hboxLoaiPhong.setAlignment(Pos.CENTER_LEFT);

		Label labelTrangThaiDonDep = new Label("Dọn dẹp:");
		labelTrangThaiDonDep.setMinWidth(labelWidth);
		cbTrangThaiDonDep = new ComboBox<>();
		cbTrangThaiDonDep.getItems().addAll("Đã dọn dẹp", "Chưa dọn dẹp");
		cbTrangThaiDonDep.setPromptText("Chọn trạng thái dọn dẹp");
		cbTrangThaiDonDep.setPrefWidth(200);
		HBox hboxTrangThaiDonDep = new HBox(10, labelTrangThaiDonDep, cbTrangThaiDonDep);
		hboxTrangThaiDonDep.setAlignment(Pos.CENTER_LEFT);

		Button addRoomButton = new Button("Thêm Phòng");
		addRoomButton
				.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
		addRoomButton.setOnAction(e -> showAddRoomDialog());
		HBox hboxAddRoom = new HBox(10, new Label(""), addRoomButton);
		hboxAddRoom.setAlignment(Pos.CENTER_LEFT);

		showAllCheckBox = new CheckBox("Hiển thị tất cả phòng");
		showAllCheckBox.setOnAction(e -> filterRooms());
		HBox hboxShowAll = new HBox(10, new Label(""), showAllCheckBox);
		hboxShowAll.setAlignment(Pos.CENTER_LEFT);

		infoPane.getChildren().addAll(hboxNgay, hboxGio, hboxTrangThaiPhong, hboxLoaiPhong, hboxTrangThaiDonDep,
				hboxAddRoom, hboxShowAll);

		VBox roomPane = new VBox(10);
		roomPane.setPadding(new Insets(10, 25, 25, 25));
		roomPane.setAlignment(Pos.TOP_CENTER);
		roomPane.setMaxHeight(465);

		roomFlowPane = new FlowPane();
		roomFlowPane.setHgap(20);
		roomFlowPane.setVgap(20);
		roomFlowPane.setAlignment(Pos.CENTER);

		cbTrangThaiDonDep.setOnAction(e -> filterRooms());
		cbTrangThaiPhong.setOnAction(e -> filterRooms());
		cbLoaiPhong.setOnAction(e -> filterRooms());
		dpNgay.setOnAction(e -> filterRooms());
		cbGio.setOnAction(e -> filterRooms());

		updateRoomDisplayDirectly();

		ScrollPane scrollPane = new ScrollPane(roomFlowPane);
		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

		roomPane.getChildren().add(scrollPane);
		VBox.setVgrow(scrollPane, Priority.ALWAYS);

		GridPane gridLayout = new GridPane();
		gridLayout.setPadding(new Insets(10));
		gridLayout.setHgap(20);
		gridLayout.add(infoPane, 0, 0);
		gridLayout.add(roomPane, 1, 0);
		GridPane.setValignment(infoPane, VPos.TOP);
		GridPane.setValignment(roomPane, VPos.TOP);
		GridPane.setVgrow(infoPane, Priority.ALWAYS);

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

	private void filterRooms() {
		ObservableList<Phong> filteredList = FXCollections.observableArrayList();
		String trangThai = cbTrangThaiPhong.getValue();
		String loaiPhong = cbLoaiPhong.getValue();
		String donDep = cbTrangThaiDonDep.getValue();

		for (Phong phong : phongList) {
			boolean matches = true;
			if (trangThai != null && !phong.getTrangThai().equals(trangThai))
				matches = false;
			if (loaiPhong != null && !phong.getLoaiPhong().equals(loaiPhong))
				matches = false;
			if (donDep != null && !phong.getMoTa().contains(donDep))
				matches = false;
			if (matches || showAllCheckBox.isSelected())
				filteredList.add(phong);
		}
		updateRoomDisplay(filteredList);
	}

	private void updateRoomDisplay(ObservableList<Phong> displayList) {
		roomFlowPane.getChildren().clear();
		for (Phong phong : displayList) {
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
			roomBox.setOnMouseClicked(e -> showRoomDetailsDialog(phong));

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

	private VBox createCenteredForm(String titleText) {
		VBox form = new VBox(10);
		form.setAlignment(Pos.CENTER);
		form.setPadding(new Insets(20));
		form.setStyle(
				"-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
		form.setMaxWidth(500);
		form.setMaxHeight(400);

		Label title = new Label(titleText);
		title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
		title.setPadding(new Insets(0, 0, 10, 0));

		form.getChildren().add(title);
		return form;
	}

	private void showAddRoomDialog() {
		VBox form = createCenteredForm("Thêm Phòng Mới");

		TextField maPhongField = new TextField();
		maPhongField.setPromptText("Mã phòng (e.g., P401)");
		ComboBox<String> loaiPhongCombo = new ComboBox<>();
		loaiPhongCombo.getItems().addAll("Đơn", "Đôi", "VIP");
		loaiPhongCombo.setPromptText("Chọn loại phòng");
		ComboBox<String> trangThaiCombo = new ComboBox<>();
		trangThaiCombo.getItems().addAll("Trống", "Đã đặt", "Đang sửa");
		trangThaiCombo.setPromptText("Chọn trạng thái");
		ComboBox<String> donDepCombo = new ComboBox<>();
		donDepCombo.getItems().addAll("Đã dọn dẹp", "Chưa dọn dẹp");
		donDepCombo.setPromptText("Chọn trạng thái dọn dẹp");

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setAlignment(Pos.CENTER);
		grid.add(new Label("Mã Phòng:"), 0, 0);
		grid.add(maPhongField, 1, 0);
		grid.add(new Label("Loại Phòng:"), 0, 1);
		grid.add(loaiPhongCombo, 1, 1);
		grid.add(new Label("Trạng Thái:"), 0, 2);
		grid.add(trangThaiCombo, 1, 2);
		grid.add(new Label("Dọn Dẹp:"), 0, 3);
		grid.add(donDepCombo, 1, 3);

		Button btnThem = new Button("Thêm");
		btnThem.setStyle(
				"-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
		btnThem.setOnAction(e -> {
			String maPhong = maPhongField.getText();
			String loaiPhong = loaiPhongCombo.getValue();
			String trangThai = trangThaiCombo.getValue();
			String donDep = donDepCombo.getValue();

			if (maPhong.isEmpty() || loaiPhong == null || trangThai == null || donDep == null) {
				showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
				return;
			}

			if (phongList.stream().anyMatch(p -> p.getMaPhong().equals(maPhong))) {
				showAlert("Lỗi", "Mã phòng " + maPhong + " đã tồn tại!");
				return;
			}

			double giaPhong = switch (loaiPhong) {
			case "Đơn" -> 200000;
			case "Đôi" -> 350000;
			case "VIP" -> 500000;
			default -> 0;
			};
			Phong newPhong = new Phong(maPhong, loaiPhong, giaPhong, trangThai, "", 1, donDep);
			phongList.add(newPhong);

			if ("Đã đặt".equals(trangThai)) {
				hoaDonPhongList.add(new HoaDon("HD" + (hoaDonList.size() + 1), "Khách hàng mặc định", maPhong, giaPhong,
						0.0, "Chưa thanh toán", maPhong, LocalDateTime.now(),
						"Phòng " + maPhong + " từ " + LocalDate.now() + " đến " + LocalDate.now().plusDays(1),
						"Tiền mặt", khachHangList.isEmpty() ? "KH001"
								: khachHangList.get(khachHangList.size() - 1).getMaKhachHang(),
						"NV001"));
				hoaDonList.add(hoaDonPhongList.get(hoaDonPhongList.size() - 1));
			}

			updateRoomDisplayDirectly();
			contentPane.getChildren().setAll(mainPane);
		});

		Button btnHuy = new Button("Hủy");
		btnHuy.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
		btnHuy.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

		HBox footer = new HBox(10, btnThem, btnHuy);
		footer.setAlignment(Pos.CENTER);

		form.getChildren().addAll(grid, footer);
		contentPane.getChildren().setAll(form);
	}

	private void showRoomDetailsDialog(Phong phong) {
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

		Button btnSua = new Button("Sửa");
		btnSua.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
		btnSua.setOnAction(e -> showEditRoomDialog(phong));

		Button btnDong = new Button("Đóng");
		btnDong.setStyle(
				"-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
		btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

		buttons.getChildren().addAll(btnSua, btnDong);

		if ("Đã đặt".equals(phong.getTrangThai())) {
			Button btnThemDichVu = new Button("Thêm dịch vụ");
			btnThemDichVu.setStyle(
					"-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
			btnThemDichVu.setOnAction(e -> showAddServiceDialog(phong));

			Button btnThanhToan = new Button("Thanh toán");
			btnThanhToan.setStyle(
					"-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12;");
			btnThanhToan.setOnAction(e -> thanhToanPhong(phong));

			buttons.getChildren().addAll(btnThemDichVu, btnThanhToan);
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

		final HoaDon hoaDonPhongInitial = getHoaDonPhong(phong); // Đổi tên và thêm final
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
		grid.addRow(2, new Label("Trạng Tháiibia:"), trangThaiCombo);
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

			HoaDon hoaDonPhong = getHoaDonPhong(updatedPhong); // Biến cục bộ mới trong lambda
			if ("Đã đặt".equals(trangThai)) {
				long soNgayO = ChronoUnit.DAYS.between(ngayDenPicker.getValue(), ngayDiPicker.getValue());
				double tienPhong = giaPhong * Math.max(1, soNgayO);
				if (hoaDonPhong == null) {
					hoaDonPhong = new HoaDon("HD" + (hoaDonList.size() + 1), tenKhachHang, updatedPhong.getMaPhong(),
							tienPhong, 0.0, "Chưa thanh toán", updatedPhong.getMaPhong(), LocalDateTime.now(),
							"Phòng " + updatedPhong.getMaPhong() + " từ " + ngayDenPicker.getValue() + " đến "
									+ ngayDiPicker.getValue(),
							"Tiền mặt", khachHangList.isEmpty() ? "KH001"
									: khachHangList.get(khachHangList.size() - 1).getMaKhachHang(),
							"NV001");
					hoaDonPhongList.add(hoaDonPhong);
					hoaDonList.add(hoaDonPhong);
				} else {
					hoaDonPhongList.set(hoaDonPhongList.indexOf(hoaDonPhong),
							new HoaDon(hoaDonPhong.getMaHoaDon(), tenKhachHang, updatedPhong.getMaPhong(), tienPhong,
									hoaDonPhong.getTienDichVu(), "Chưa thanh toán", updatedPhong.getMaPhong(),
									hoaDonPhong.getNgayLap(),
									"Phòng " + updatedPhong.getMaPhong() + " từ " + ngayDenPicker.getValue() + " đến "
											+ ngayDiPicker.getValue(),
									hoaDonPhong.getHinhThucThanhToan(), hoaDonPhong.getMaKhachHang(),
									hoaDonPhong.getMaNhanVien()));
					hoaDonList.set(hoaDonList.indexOf(hoaDonPhong),
							hoaDonPhongList.get(hoaDonPhongList.indexOf(hoaDonPhong)));
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

			DichVu dichVuMoi = new DichVu("DV" + (phong.getDanhSachDichVu().size() + 1), selected.getTenDichVu(),
					selected.getGia(), selected.getMoTa(), selected.getTrangThai());

			phong.addDichVu(dichVuMoi);

			HoaDon hoaDonPhong = getHoaDonPhong(phong);
			if (hoaDonPhong == null) {
				hoaDonPhong = new HoaDon("HD" + (hoaDonList.size() + 1),
						phong.getTenKhachHang().isEmpty() ? "Khách hàng mặc định" : phong.getTenKhachHang(),
						phong.getMaPhong(), phong.getGiaPhong(), dichVuMoi.getGia(), "Chưa thanh toán",
						phong.getMaPhong(), LocalDateTime.now(),
						"Phòng " + phong.getMaPhong() + " từ " + LocalDate.now() + " đến " + LocalDate.now().plusDays(1)
								+ ", " + dichVuMoi.getTenDichVu() + " ("
								+ String.format("%,.0f VNĐ", dichVuMoi.getGia()) + ")",
						"Tiền mặt", khachHangList.isEmpty() ? "KH001"
								: khachHangList.get(khachHangList.size() - 1).getMaKhachHang(),
						"NV001");
				hoaDonPhongList.add(hoaDonPhong);
				hoaDonList.add(hoaDonPhong);
			} else {
				double tienDichVuHienTai = hoaDonPhong.getTienDichVu();
				hoaDonPhongList.set(hoaDonPhongList.indexOf(hoaDonPhong),
						new HoaDon(hoaDonPhong.getMaHoaDon(), hoaDonPhong.getTenKhachHang(), hoaDonPhong.getMaPhong(),
								hoaDonPhong.getTienPhong(), tienDichVuHienTai + dichVuMoi.getGia(), "Chưa thanh toán",
								hoaDonPhong.getMaPhong(), hoaDonPhong.getNgayLap(),
								hoaDonPhong.getMoTa() + ", " + dichVuMoi.getTenDichVu() + " ("
										+ String.format("%,.0f VNĐ", dichVuMoi.getGia()) + ")",
								hoaDonPhong.getHinhThucThanhToan(), hoaDonPhong.getMaKhachHang(),
								hoaDonPhong.getMaNhanVien()));
				hoaDonList.set(hoaDonList.indexOf(hoaDonPhong),
						hoaDonPhongList.get(hoaDonPhongList.indexOf(hoaDonPhong)));
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

	private void thanhToanPhong(Phong phong) {
		final HoaDon hoaDonPhong = getHoaDonPhong(phong); // Thêm từ khóa final
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
			Image qrImage = new Image("/img/QRcode.jpg", true);
			ImageView qrView = new ImageView(qrImage);
			qrView.setFitWidth(150);
			qrView.setFitHeight(150);
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

	private HoaDon getHoaDonPhong(Phong phong) {
		return hoaDonPhongList.stream()
				.filter(hd -> hd.getMaPhong().equals(phong.getMaPhong()) && "Chưa thanh toán".equals(hd.getTrangThai()))
				.findFirst().orElse(null);
	}

	private void completePayment(Phong phong, HoaDon hoaDonPhong, double tongTien, String moTa,
			String hinhThucThanhToan) {
		int index = hoaDonPhongList.indexOf(hoaDonPhong);
		if (index != -1) {
			hoaDonPhongList.set(index,
					new HoaDon(hoaDonPhong.getMaHoaDon(), hoaDonPhong.getTenKhachHang(), hoaDonPhong.getMaPhong(),
							hoaDonPhong.getTienPhong(), hoaDonPhong.getTienDichVu(), "Đã thanh toán",
							hoaDonPhong.getMaPhong(), hoaDonPhong.getNgayLap(), moTa, hinhThucThanhToan,
							hoaDonPhong.getMaKhachHang(), hoaDonPhong.getMaNhanVien()));
			hoaDonList.set(hoaDonList.indexOf(hoaDonPhong), hoaDonPhongList.get(index));
		}

		int phongIndex = phongList.indexOf(phong);
		Phong newPhong = new Phong(phong.getMaPhong(), phong.getLoaiPhong(), phong.getGiaPhong(), "Trống",
				phong.getViTri(), phong.getSoNguoiToiDa(), "Chưa dọn dẹp");
		phongList.set(phongIndex, newPhong);
		updateRoomDisplayDirectly();
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}