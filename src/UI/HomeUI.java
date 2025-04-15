package UI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.NhanVien;

public class HomeUI extends Application {
    private VBox menuItems;
    private StackPane slidePane;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private DataManager dataManager;
    private Stage primaryStage;
    private Button selectedButton;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        dataManager = DataManager.getInstance();

        mediaView = loadVideo("c/img/nen.mp4");
        mediaView.setPreserveRatio(false);

        BorderPane menu = buildSidebarMenu();
        menu.setPrefWidth(280);
        menu.setMinWidth(280);

        slidePane = new StackPane();
        slidePane.setStyle("-fx-background-color: #f0f0f0;");
        slidePane.setPrefSize(1120, 800);

        // Thêm UserInfoBox vào slidePane
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
        slidePane.getChildren().add(userInfoBox);

        showDefaultHomeView();

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(menu);
        mainLayout.setCenter(slidePane);

        mediaView.fitWidthProperty().bind(mainLayout.widthProperty().subtract(menu.widthProperty()));
        mediaView.fitHeightProperty().bind(mainLayout.heightProperty());

        Scene scene = new Scene(mainLayout, 1400, 800);

        scene.setOnMouseClicked(e -> {
            if (!menu.getBoundsInParent().contains(e.getX(), e.getY())) {
                menuItems.getChildren().forEach(node -> {
                    if (node instanceof VBox) {
                        ((VBox) node).setVisible(false);
                        ((VBox) node).setManaged(false);
                    }
                });
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hotel Management - Home");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private MediaView loadVideo(String videoPath) {
        try {
            java.net.URL videoUrl = getClass().getResource(videoPath);
            if (videoUrl == null) {
                System.err.println("Không tìm thấy video tại: " + videoPath);
                return new MediaView();
            }

            Media media = new Media(videoUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaView = new MediaView(mediaPlayer);

            mediaPlayer.setOnError(() -> {
                String errorMessage = mediaPlayer.getError() != null ? mediaPlayer.getError().getMessage() : "Lỗi không xác định";
                System.err.println("Lỗi MediaPlayer: " + errorMessage);
            });

            mediaPlayer.setOnReady(() -> {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
            });

            return mediaView;
        } catch (Exception e) {
            System.err.println("Lỗi khi tải video: " + e.getMessage());
            return new MediaView();
        }
    }

    private BorderPane buildSidebarMenu() {
        BorderPane menu = new BorderPane();
        menu.setStyle("-fx-background-color: #111; -fx-padding: 40 30 20 30;");

        ImageView homeIcon = new ImageView(loadImage("/img/iconhome.png"));
        homeIcon.setFitWidth(30);
        homeIcon.setFitHeight(30);
        Text title = new Text("Hotel Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setFill(Color.WHITE);
        HBox titleBox = new HBox(10, homeIcon, title);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 20, 0));

        menuItems = new VBox(10);
        menuItems.setAlignment(Pos.TOP_LEFT);

        NhanVien currentUser = dataManager.getCurrentNhanVien();
        boolean isQuanLy = currentUser != null && currentUser.getChucVu() != null && 
                          currentUser.getChucVu().equals("Quan ly");
        boolean isAdmin = currentUser != null && currentUser.getMaNhanVien().equals("ADMIN");

        // Menu cho tất cả người dùng
        addExpandableMenu(menuItems, "Khách hàng", 
            isQuanLy ? new String[]{"Quản lý khách hàng", "Tìm kiếm khách hàng"} 
                     : new String[]{"Tìm kiếm khách hàng"});
        addExpandableMenu(menuItems, "Phòng", 
            isQuanLy ? new String[]{"Đặt phòng", "Quản lý phòng", "Quản lý đặt phòng", "Đổi phòng", "Tìm kiếm phòng"} 
                     : new String[]{"Đặt phòng", "Đổi phòng", "Tìm kiếm phòng"});
        addExpandableMenu(menuItems, "Dịch vụ", 
            isQuanLy ? new String[]{"Quản lý dịch vụ", "Tìm kiếm dịch vụ"} 
                     : new String[]{"Tìm kiếm dịch vụ"});
        addExpandableMenu(menuItems, "Hóa đơn", 
            isQuanLy ? new String[]{"Quản lý hóa đơn", "Quản lý doanh thu", "Tìm kiếm hóa đơn"} 
                     : new String[]{"Tìm kiếm hóa đơn"});
        addExpandableMenu(menuItems, "CT Khuyến mãi", 
            isQuanLy ? new String[]{"Quản lý khuyến mãi", "Tìm kiếm khuyến mãi"} 
                     : new String[]{"Tìm kiếm khuyến mãi"});
        
        // Menu chỉ cho Quản lý (không cho admin mặc định)
        if (isQuanLy && !isAdmin) {
            addExpandableMenu(menuItems, "Nhân viên", 
                new String[]{"Quản lý nhân viên", "Tìm kiếm nhân viên"});
            addExpandableMenu(menuItems, "Tài khoản", 
                new String[]{"Quản lý tài khoản"});
        } else {
            addExpandableMenu(menuItems, "Nhân viên", 
                new String[]{"Tìm kiếm nhân viên"});
        }

        // Menu đăng xuất và thoát
        addExpandableMenu(menuItems, "Đăng xuất");
        addExpandableMenu(menuItems, "Thoát");

        ImageView fbIcon = new ImageView(loadImage("/img/iconfb.png"));
        ImageView instaIcon = new ImageView(loadImage("/img/iconinta.png"));
        ImageView ytIcon = new ImageView(loadImage("/img/iconyt.png"));
        fbIcon.setFitWidth(20);
        fbIcon.setFitHeight(20);
        instaIcon.setFitWidth(20);
        instaIcon.setFitHeight(20);
        ytIcon.setFitWidth(20);
        ytIcon.setFitHeight(20);

        HBox socialBox = new HBox(10, fbIcon, instaIcon, ytIcon);
        socialBox.setAlignment(Pos.CENTER_LEFT);

        Text hotline = new Text("Hotline: 19008118");
        hotline.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        hotline.setFill(Color.WHITE);

        VBox footer = new VBox(10, socialBox, hotline);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        menu.setTop(titleBox);
        menu.setCenter(menuItems);
        menu.setBottom(footer);

        titleBox.setOnMouseClicked(e -> showDefaultHomeView());

        return menu;
    }

    private void addExpandableMenu(VBox menu, String title, String... items) {
        Button mainBtn = createStyledButton(title);
        VBox subBox = new VBox();
        subBox.setPadding(new Insets(5, 0, 0, 20));
        subBox.setSpacing(5);
        subBox.setVisible(false);
        subBox.setManaged(false);

        for (String item : items) {
            Button subBtn = createStyledButton(item);
            subBtn.setStyle(createBaseStyle() + "-fx-font-size: 14px; -fx-padding: 5 15;");

            subBtn.setOnAction(e -> {
                try {
                    if (selectedButton != null) {
                        selectedButton.setStyle(createBaseStyle());
                    }
                    subBtn.setStyle(createSelectedStyle());
                    selectedButton = subBtn;

                    NhanVien currentUser = dataManager.getCurrentNhanVien();
                    if (currentUser == null) {
                        showPermissionDeniedAlert("Vui lòng đăng nhập để sử dụng chức năng này.");
                        performLogout();
                        return;
                    }

                    String role = currentUser.getChucVu() != null && currentUser.getChucVu().equals("Quan ly") 
                                ? "Quản lý" : "Nhân viên";

                    System.out.println("Mở giao diện: " + item);
                    switch (item) {
                        case "Quản lý khách hàng":
                            QLKH qlkhUI = new QLKH();
                            slidePane.getChildren().setAll(qlkhUI.getUI());
                            break;
                        case "Tìm kiếm khách hàng":
                            TimkiemKH tkKhUI = new TimkiemKH();
                            slidePane.getChildren().setAll(tkKhUI.getUI());
                            break;
                        case "Đặt phòng":
                            DatphongUI datPhongUI = new DatphongUI();
                            slidePane.getChildren().setAll(datPhongUI.getUI());
                            break;
                        case "Quản lý phòng":
                            QLphongUI qlPhongUI = new QLphongUI();
                            slidePane.getChildren().setAll(qlPhongUI.getUI(role));
                            break;
                        case "Quản lý đặt phòng":
                            QLdatphongUI qlDatPhongUI = new QLdatphongUI();
                            slidePane.getChildren().setAll(qlDatPhongUI.getUI());
                            break;
                        case "Đổi phòng":
                            DoiPhongUI doiPhongUI = new DoiPhongUI();
                            slidePane.getChildren().setAll(doiPhongUI.getUI());
                            break;
                        case "Tìm kiếm phòng":
                            TimkiemphongUI tkPhongUI = new TimkiemphongUI();
                            slidePane.getChildren().setAll(tkPhongUI.getUI());
                            break;
                        case "Quản lý dịch vụ":
                            QLDichVu qlDvUI = new QLDichVu();
                            slidePane.getChildren().setAll(qlDvUI.getUI());
                            break;
                        case "Tìm kiếm dịch vụ":
                            TimkiemDV tkDvUI = new TimkiemDV();
                            slidePane.getChildren().setAll(tkDvUI.getUI());
                            break;
                        case "Quản lý nhân viên":
                            QLNV qlNvUI = new QLNV();
                            slidePane.getChildren().setAll(qlNvUI.getUI());
                            break;
                        case "Tìm kiếm nhân viên":
                            TimKiemNV tkNvUI = new TimKiemNV();
                            slidePane.getChildren().setAll(tkNvUI.getUI());
                            break;
                        case "Quản lý doanh thu":
                            QLDoanhThu qlDoanhThuUI = new QLDoanhThu();
                            slidePane.getChildren().setAll(qlDoanhThuUI.getUI());
                            break;
                        case "Quản lý hóa đơn":
                            QLHD qlHdUI = new QLHD();
                            slidePane.getChildren().setAll(qlHdUI.getUI());
                            break;
                        case "Tìm kiếm hóa đơn":
                            TimkiemHD tkHdUI = new TimkiemHD();
                            slidePane.getChildren().setAll(tkHdUI.getUI());
                            break;
                        case "Quản lý khuyến mãi":
                            QLKM qlKmUI = new QLKM();
                            slidePane.getChildren().setAll(qlKmUI.getUI());
                            break;
                        case "Tìm kiếm khuyến mãi":
                            TimkiemKM tkKmUI = new TimkiemKM();
                            slidePane.getChildren().setAll(tkKmUI.getUI());
                            break;
                        case "Quản lý tài khoản":
                            QLTaiKhoanUI qlTaiKhoanUI = new QLTaiKhoanUI();
                            slidePane.getChildren().setAll(qlTaiKhoanUI.getUI());
                            break;
                        case "Đăng xuất":
                            performLogout();
                            break;
                        case "Thoát":
                            Platform.exit();
                            break;
                        default:
                            showPlaceholder(item);
                            break;
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi khi mở giao diện " + item + ": " + ex.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Không thể mở giao diện");
                    alert.setContentText("Đã xảy ra lỗi: " + ex.getMessage());
                    alert.showAndWait();
                }
            });

            subBox.getChildren().add(subBtn);
        }

        if (items.length == 0 || subBox.getChildren().isEmpty()) {
            mainBtn.setOnAction(e -> {
                if (title.equals("Đăng xuất")) {
                    performLogout();
                } else if (title.equals("Thoát")) {
                    Platform.exit();
                }
            });
        } else {
            mainBtn.setOnMouseEntered(e -> mainBtn.setStyle(createHoverStyle()));
            mainBtn.setOnMouseExited(e -> {
                if (mainBtn != selectedButton) {
                    mainBtn.setStyle(createBaseStyle());
                }
            });

            mainBtn.setOnAction(e -> {
                boolean currentlyVisible = subBox.isVisible();
                menu.getChildren().forEach(node -> {
                    if (node instanceof VBox && node != subBox) {
                        ((VBox) node).setVisible(false);
                        ((VBox) node).setManaged(false);
                    }
                });
                subBox.setVisible(!currentlyVisible);
                subBox.setManaged(!currentlyVisible);
                if (subBox.isVisible()) {
                    mainBtn.setStyle(createSelectedStyle());
                    selectedButton = mainBtn;
                } else if (mainBtn != selectedButton) {
                    mainBtn.setStyle(createBaseStyle());
                }
            });
        }

        menu.getChildren().addAll(mainBtn);
        if (!subBox.getChildren().isEmpty()) {
            menu.getChildren().add(subBox);
        }
    }

    private void showPlaceholder(String item) {
        StackPane pane = new StackPane();
        Label label = new Label("Giao diện " + item + " đang được phát triển");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        pane.getChildren().add(label);
        slidePane.getChildren().setAll(pane);
    }

    private void showPermissionDeniedAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Không có quyền truy cập");
        alert.setHeaderText("Bạn không có quyền vào chức năng này");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(createBaseStyle());
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.BASELINE_LEFT);
        return button;
    }

    private String createBaseStyle() {
        return "-fx-background-color: transparent; -fx-border-color: transparent;" +
                " -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5 10;";
    }

    private String createHoverStyle() {
        return "-fx-background-color: transparent; -fx-border-color: transparent;" +
                " -fx-text-fill: #87CEEB; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5 10;";
    }

    private String createSelectedStyle() {
        return "-fx-background-color: transparent; -fx-border-color: transparent;" +
                " -fx-text-fill: #87CEEB; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5 10;";
    }

    private Image loadImage(String filePath) {
        try {
            java.net.URL resourceUrl = getClass().getResource(filePath);
            if (resourceUrl == null) {
                System.err.println("Không tìm thấy hình ảnh: " + filePath);
                return new Image("https://via.placeholder.com/20");
            }
            return new Image(resourceUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("Lỗi khi tải hình ảnh: " + filePath + ", lỗi: " + e.getMessage());
            return new Image("https://via.placeholder.com/20");
        }
    }

    private void showDefaultHomeView() {
        StackPane contentPane = new StackPane();
        contentPane.getChildren().add(mediaView);

        VBox welcomeBox = new VBox(10);
        welcomeBox.setAlignment(Pos.CENTER);
        Text welcomeText = new Text("Chào mừng đến với Hotel Management");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        welcomeText.setFill(Color.WHITE);
        Text subText = new Text("Quản lý khách sạn dễ dàng và hiệu quả");
        subText.setFont(Font.font("Arial", 18));
        subText.setFill(Color.WHITE);
        welcomeBox.getChildren().addAll(welcomeText, subText);
        contentPane.getChildren().add(welcomeBox);

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
        contentPane.getChildren().add(userInfoBox);

        slidePane.getChildren().setAll(contentPane);

        if (selectedButton != null) {
            selectedButton.setStyle(createBaseStyle());
            selectedButton = null;
        }
    }

    public void performLogout() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        UserInfoBox.setCurrentUser(null);
        dataManager.setCurrentNhanVien(null);
        LoginUI.showLogin(primaryStage);
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}