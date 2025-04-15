package UI;

import dao.ChitietHoaDon_Dao;
import dao.HoaDon_Dao;
import dao.KhachHang_Dao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.ChitietHoaDon;
import model.HoaDon;
import model.KhachHang;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QLDoanhThu {
    private final ObservableList<HoaDon> hoaDonList;
    private final ObservableList<ChitietHoaDon> chitietHoaDonList;
    private PieChart pieChart;
    private Label lblRevenueRoom;
    private Label lblDoanhThuDichVu;
    private Label lblTongDoanhThu;
    private Label lblTitle;
    private StackPane contentPane;
    private StackPane mainPane;
    private DatePicker datePicker;
    private final DataManager dataManager;
    private final HoaDon_Dao hoaDonDao;
    private final ChitietHoaDon_Dao chitietHoaDonDao;

    public QLDoanhThu() {
        dataManager = DataManager.getInstance();
        this.hoaDonList = dataManager.getHoaDonList();
        this.chitietHoaDonList = dataManager.getChitietHoaDonList();
        this.hoaDonDao = new HoaDon_Dao();
        this.chitietHoaDonDao = new ChitietHoaDon_Dao();
        this.contentPane = new StackPane();
        this.mainPane = createMainPane();
        dataManager.addHoaDonListChangeListener(this::updateDataOnChange);
    }

    private StackPane createMainPane() {
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: white;");

        // User info display
        HBox userInfoBox = new HBox();
        userInfoBox.setPadding(new Insets(10));
        userInfoBox.setAlignment(Pos.CENTER);
        if (dataManager.getCurrentNhanVien() != null) {
            userInfoBox.getChildren().add(new Label("Nhân viên: " + dataManager.getCurrentNhanVien().getTenNhanVien()));
        } else {
            userInfoBox.getChildren().add(new Label("Chưa đăng nhập"));
        }
        userInfoBox.setPrefSize(200, 50);
        userInfoBox.setMaxSize(200, 50);
        StackPane.setAlignment(userInfoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(userInfoBox, new Insets(10, 10, 0, 0));

        // DatePicker
        Label lblThoiGian = new Label("Chọn ngày:");
        lblThoiGian.setStyle("-fx-font-weight: bold;");
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: #d3d3d3;");
        HBox dateBox = new HBox(10, lblThoiGian, datePicker);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPadding(new Insets(10));

        lblTitle = new Label("TỶ LỆ DOANH THU NGÀY " + datePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        VBox topHeader = new VBox(10, dateBox, lblTitle);
        topHeader.setAlignment(Pos.CENTER);

        // PieChart
        pieChart = new PieChart();
        pieChart.setPrefSize(600, 500);
        pieChart.setStyle("-fx-legend-side: bottom; -fx-border-radius: 5; -fx-background-radius: 5;");

        lblTongDoanhThu = new Label("0 VNĐ");
        lblTongDoanhThu.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        Label totalTitle = new Label("Tổng doanh thu trong ngày:");
        totalTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        VBox totalDisplay = new VBox(5, totalTitle, lblTongDoanhThu);
        totalDisplay.setAlignment(Pos.CENTER);

        VBox rightPane = new VBox(20, pieChart, totalDisplay);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(20));

        // Revenue Boxes
        lblRevenueRoom = new Label("0 VNĐ");
        VBox roomBox = createRevenueBox("Doanh thu phòng", "img/icongiuong.png", lblRevenueRoom);
        roomBox.setOnMouseClicked(e -> showRoomReport(datePicker.getValue()));

        lblDoanhThuDichVu = new Label("0 VNĐ");
        VBox serviceBox = createRevenueBox("Doanh thu dịch vụ", "img/iconmonan.png", lblDoanhThuDichVu);
        serviceBox.setOnMouseClicked(e -> showServiceReport(datePicker.getValue()));

        VBox totalBox = createRevenueBox("Báo cáo doanh thu", "img/icondola.png", new Label("Nhấn để xem"));
        totalBox.setOnMouseClicked(e -> showTotalReport(datePicker.getValue()));

        VBox statsBox = new VBox(20, roomBox, serviceBox, totalBox);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(20));
        statsBox.setPrefWidth(300);

        // Main Layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(statsBox);
        mainLayout.setRight(rightPane);
        mainLayout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        BorderPane layout = new BorderPane();
        layout.setTop(topHeader);
        layout.setCenter(mainLayout);
        layout.setPadding(new Insets(10));

        mainPane.getChildren().addAll(layout, userInfoBox);

        updateData(datePicker.getValue());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateData(newVal));

        return mainPane;
    }

    public StackPane getUI() {
        contentPane.getChildren().setAll(mainPane);
        return contentPane;
    }

    private VBox createRevenueBox(String title, String logoPath, Label revenueLabel) {
        Image image = loadImage(logoPath);
        ImageView logo = new ImageView(image);
        logo.setFitWidth(30);
        logo.setFitHeight(30);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox titleBox = new HBox(10, logo, titleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        revenueLabel.setStyle("-fx-font-size: 14px;");

        VBox box = new VBox(5, titleBox, revenueLabel);
        box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10;");
        box.setPrefWidth(250);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Image loadImage(String path) {
        try {
            Image image = new Image(getClass().getResourceAsStream("/" + path));
            if (image.isError()) throw new Exception("Image load failed");
            return image;
        } catch (Exception e) {
            System.err.println("Lỗi tải hình ảnh " + path + ": " + e.getMessage());
            return new Image("https://via.placeholder.com/30");
        }
    }

    private void updateData(LocalDate selectedDate) {
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        double doanhThuPhong = 0;
        double doanhThuDichVu = 0;
        double totalThueVat = 0;
        double totalChietKhau = 0;

        LocalDateTime start = selectedDate.atStartOfDay();
        LocalDateTime end = selectedDate.atTime(23, 59, 59);

        for (HoaDon hoaDon : hoaDonList) {
            if (hoaDon.getTrangThai() && !hoaDon.getNgayLap().isBefore(start) && !hoaDon.getNgayLap().isAfter(end)) {
                for (ChitietHoaDon ct : chitietHoaDonList) {
                    if (ct.getMaHoaDon().equals(hoaDon.getMaHoaDon())) {
                        double subTotal = ct.getTienPhong() + ct.getTienDichVu();
                        double vatAmount = subTotal * (ct.getThueVat() / 100);
                        double discountAmount = subTotal * (ct.getKhuyenMai() / 100);
                        doanhThuPhong += ct.getTienPhong();
                        doanhThuDichVu += ct.getTienDichVu();
                        totalThueVat += vatAmount;
                        totalChietKhau += discountAmount;
                    }
                }
            }
        }

        double tongDoanhThu = doanhThuPhong + doanhThuDichVu + totalThueVat - totalChietKhau;

        lblRevenueRoom.setText(String.format("%,.0f VNĐ", doanhThuPhong));
        lblDoanhThuDichVu.setText(String.format("%,.0f VNĐ", doanhThuDichVu));
        lblTongDoanhThu.setText(String.format("%,.0f VNĐ", tongDoanhThu));
        lblTitle.setText("TỶ LỆ DOANH THU NGÀY " + selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        if (doanhThuPhong > 0) pieChartData.add(new PieChart.Data("Doanh thu phòng", doanhThuPhong));
        if (doanhThuDichVu > 0) pieChartData.add(new PieChart.Data("Doanh thu dịch vụ", doanhThuDichVu));
        pieChart.setData(pieChartData);

        if (tongDoanhThu == 0) {
            pieChart.setData(FXCollections.observableArrayList(new PieChart.Data("Không có dữ liệu", 1)));
        }
    }

    private void updateDataOnChange() {
        updateData(datePicker.getValue());
    }

    private void showReport(String title, LocalDate initialDate, String reportType) {
        VBox form = createCenteredForm(title);

        DatePicker startDate = new DatePicker(initialDate);
        DatePicker endDate = new DatePicker(initialDate);
        Button generateButton = new Button("Tạo báo cáo");
        generateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");

        HBox dateBox = new HBox(10, new Label("Từ:"), startDate, new Label("Đến:"), endDate, generateButton);
        dateBox.setAlignment(Pos.CENTER);

        TableView<HoaDon> table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn<HoaDon, String> maHoaDonCol = new TableColumn<>("Mã Hóa Đơn");
        maHoaDonCol.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        maHoaDonCol.setMinWidth(100);

        TableColumn<HoaDon, String> tenKhachHangCol = new TableColumn<>("Khách Hàng");
        tenKhachHangCol.setCellValueFactory(cellData -> {
            try {
                KhachHang khachHang = dataManager.getKhachHangList().stream()
                        .filter(kh -> kh.getMaKhachHang().equals(cellData.getValue().getMaKhachHang()))
                        .findFirst().orElse(null);
                return new SimpleStringProperty(khachHang != null ? khachHang.getTenKhachHang() : "Không xác định");
            } catch (Exception e) {
                return new SimpleStringProperty("Lỗi truy vấn");
            }
        });
        tenKhachHangCol.setMinWidth(150);

        TableColumn<HoaDon, Double> tienPhongCol = new TableColumn<>("Tiền Phòng");
        tienPhongCol.setCellValueFactory(cellData -> {
            String maHoaDon = cellData.getValue().getMaHoaDon();
            double total = chitietHoaDonList.stream()
                    .filter(ct -> maHoaDon.equals(ct.getMaHoaDon()))
                    .mapToDouble(ChitietHoaDon::getTienPhong)
                    .sum();
            return new javafx.beans.property.SimpleObjectProperty<>(total);
        });
        tienPhongCol.setMinWidth(120);

        TableColumn<HoaDon, Double> tienDichVuCol = new TableColumn<>("Tiền Dịch Vụ");
        tienDichVuCol.setCellValueFactory(cellData -> {
            String maHoaDon = cellData.getValue().getMaHoaDon();
            double total = chitietHoaDonList.stream()
                    .filter(ct -> maHoaDon.equals(ct.getMaHoaDon()))
                    .mapToDouble(ChitietHoaDon::getTienDichVu)
                    .sum();
            return new javafx.beans.property.SimpleObjectProperty<>(total);
        });
        tienDichVuCol.setMinWidth(120);

        TableColumn<HoaDon, String> ngayLapCol = new TableColumn<>("Ngày Lập");
        ngayLapCol.setCellValueFactory(cellData -> {
            LocalDateTime ngayLap = cellData.getValue().getNgayLap();
            return new SimpleStringProperty(ngayLap != null ? ngayLap.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "");
        });
        ngayLapCol.setMinWidth(150);

        if ("room".equals(reportType)) {
            table.getColumns().addAll(maHoaDonCol, tenKhachHangCol, tienPhongCol, ngayLapCol);
        } else if ("service".equals(reportType)) {
            table.getColumns().addAll(maHoaDonCol, tenKhachHangCol, tienDichVuCol, ngayLapCol);
        } else {
            table.getColumns().addAll(maHoaDonCol, tenKhachHangCol, tienPhongCol, tienDichVuCol, ngayLapCol);
        }

        Label totalLabel = new Label("Tổng số tiền: 0 VNĐ");
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        HBox totalBox = new HBox(totalLabel);
        totalBox.setAlignment(Pos.CENTER_RIGHT);

        generateButton.setOnAction(e -> {
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();
            if (start == null || end == null || start.isAfter(end)) {
                showAlert("Lỗi", "Vui lòng chọn khoảng thời gian hợp lệ!");
                return;
            }

            ObservableList<HoaDon> reportData = FXCollections.observableArrayList();
            double totalAmount = 0;

            LocalDateTime startTime = start.atStartOfDay();
            LocalDateTime endTime = end.atTime(23, 59, 59);

            for (HoaDon hoaDon : hoaDonList) {
                if (hoaDon.getTrangThai() && !hoaDon.getNgayLap().isBefore(startTime) && !hoaDon.getNgayLap().isAfter(endTime)) {
                    double tienPhong = chitietHoaDonList.stream()
                            .filter(ct -> hoaDon.getMaHoaDon().equals(ct.getMaHoaDon()))
                            .mapToDouble(ChitietHoaDon::getTienPhong)
                            .sum();
                    double tienDichVu = chitietHoaDonList.stream()
                            .filter(ct -> hoaDon.getMaHoaDon().equals(ct.getMaHoaDon()))
                            .mapToDouble(ChitietHoaDon::getTienDichVu)
                            .sum();
                    double subTotal = tienPhong + tienDichVu;
                    double vatAmount = chitietHoaDonList.stream()
                            .filter(ct -> hoaDon.getMaHoaDon().equals(ct.getMaHoaDon()))
                            .mapToDouble(ct -> (ct.getTienPhong() + ct.getTienDichVu()) * ct.getThueVat() / 100)
                            .sum();
                    double discountAmount = chitietHoaDonList.stream()
                            .filter(ct -> hoaDon.getMaHoaDon().equals(ct.getMaHoaDon()))
                            .mapToDouble(ct -> (ct.getTienPhong() + ct.getTienDichVu()) * ct.getKhuyenMai() / 100)
                            .sum();

                    if ("room".equals(reportType) && tienPhong > 0) {
                        reportData.add(hoaDon);
                        totalAmount += tienPhong + (tienPhong * vatAmount / subTotal) - (tienPhong * discountAmount / subTotal);
                    } else if ("service".equals(reportType) && tienDichVu > 0) {
                        reportData.add(hoaDon);
                        totalAmount += tienDichVu + (tienDichVu * vatAmount / subTotal) - (tienDichVu * discountAmount / subTotal);
                    } else if ("total".equals(reportType)) {
                        reportData.add(hoaDon);
                        totalAmount += subTotal + vatAmount - discountAmount;
                    }
                }
            }

            if (reportData.isEmpty()) {
                showAlert("Thông báo", "Không có dữ liệu trong khoảng thời gian này!");
            }
            table.setItems(reportData);
            totalLabel.setText(String.format("Tổng số tiền: %,.0f VNĐ", totalAmount));
        });

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 5;");
        btnDong.setOnAction(e -> contentPane.getChildren().setAll(mainPane));

        HBox footer = new HBox(10, btnDong);
        footer.setAlignment(Pos.CENTER);

        form.getChildren().addAll(dateBox, table, totalBox, footer);
        contentPane.getChildren().setAll(form);
    }

    private void showRoomReport(LocalDate initialDate) {
        showReport("Báo cáo doanh thu phòng", initialDate, "room");
    }

    private void showServiceReport(LocalDate initialDate) {
        showReport("Báo cáo doanh thu dịch vụ", initialDate, "service");
    }

    private void showTotalReport(LocalDate initialDate) {
        showReport("Báo cáo tổng doanh thu", initialDate, "total");
    }

    private VBox createCenteredForm(String titleText) {
        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        form.setMaxWidth(700);
        form.setMaxHeight(500);

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