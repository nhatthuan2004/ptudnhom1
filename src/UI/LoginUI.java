package UI;

import dao.NhanVien_Dao;
import dao.TaiKhoan_Dao;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.NhanVien;
import model.TaiKhoan;

import java.sql.SQLException;

public class LoginUI {
    private static DataManager dataManager = DataManager.getInstance();
    private static TaiKhoan_Dao taiKhoanDao = new TaiKhoan_Dao();
    private static NhanVien_Dao nhanVienDao = new NhanVien_Dao();

    private static Image loadImage(String filePath) {
        try {
            java.net.URL resourceUrl = LoginUI.class.getResource(filePath);
            if (resourceUrl == null) {
                System.err.println("Warning: Image not found at " + filePath);
                return new Image("https://via.placeholder.com/120");
            }
            return new Image(resourceUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading image " + filePath + ": " + e.getMessage());
            return new Image("https://via.placeholder.com/120");
        }
    }

    public static void showLogin(Stage primaryStage) {
        Image backgroundImage = loadImage("/img/anhhome4.png");
        BackgroundImage bgImage = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, true));

        BorderPane root = new BorderPane();
        root.setBackground(new Background(bgImage));

        VBox loginContainer = new VBox(15);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);"
                + "-fx-padding: 30; -fx-border-radius: 15; -fx-background-radius: 15;");
        loginContainer.setMaxWidth(400);
        loginContainer.setMaxHeight(350);

        ImageView profileImage = new ImageView(loadImage("/img/iconuser.png"));
        profileImage.setFitWidth(100);
        profileImage.setFitHeight(100);
        Circle clip = new Circle(50, 50, 50);
        profileImage.setClip(clip);

        VBox imageContainer = new VBox(profileImage);
        imageContainer.setAlignment(Pos.CENTER);

        VBox inputContainer = new VBox(10);
        inputContainer.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Tài khoản...");
        usernameField.setStyle("-fx-padding: 12; -fx-background-radius: 25; -fx-font-size: 14px;");
        usernameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu...");
        passwordField.setStyle("-fx-padding: 12; -fx-background-radius: 25; -fx-font-size: 14px;");
        passwordField.setMaxWidth(250);

        TextField visiblePassword = new TextField();
        visiblePassword.setPromptText("Mật khẩu...");
        visiblePassword.setStyle("-fx-padding: 12; -fx-background-radius: 25; -fx-font-size: 14px;");
        visiblePassword.setMaxWidth(250);
        visiblePassword.managedProperty().set(false);
        visiblePassword.setVisible(false);

        visiblePassword.textProperty().bindBidirectional(passwordField.textProperty());

        CheckBox showPass = new CheckBox("Hiện mật khẩu");
        showPass.textFillProperty().set(Color.WHITE);
        showPass.setOnAction(e -> {
            boolean show = showPass.isSelected();
            passwordField.setVisible(!show);
            passwordField.managedProperty().set(!show);
            visiblePassword.setVisible(show);
            visiblePassword.managedProperty().set(show);
        });

        Button loginButton = new Button("LOG IN");
        loginButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        loginButton.setOnAction(e -> handleLogin(usernameField, passwordField, primaryStage));

        inputContainer.getChildren().addAll(usernameField, passwordField, visiblePassword, showPass, loginButton);

        loginContainer.getChildren().addAll(imageContainer, inputContainer);

        StackPane container = new StackPane(loginContainer);
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(800, 500);

        root.setCenter(container);

        VBox footer = new VBox(10);
        footer.setAlignment(Pos.TOP_LEFT);
        footer.setPadding(new Insets(10, 0, 10, 10));

        HBox socialBox = new HBox(10);
        socialBox.setAlignment(Pos.CENTER_LEFT);

        ImageView fbIcon = new ImageView(loadImage("/img/iconfb.png"));
        fbIcon.setFitWidth(20);
        fbIcon.setFitHeight(20);
        ImageView instaIcon = new ImageView(loadImage("/img/iconinta.png"));
        instaIcon.setFitWidth(20);
        instaIcon.setFitHeight(20);
        ImageView ytIcon = new ImageView(loadImage("/img/iconyt.png"));
        ytIcon.setFitWidth(20);
        ytIcon.setFitHeight(20);

        socialBox.getChildren().addAll(fbIcon, instaIcon, ytIcon);
        footer.getChildren().add(socialBox);

        Text hotline = new Text("Hotline: 19008118");
        hotline.fontProperty().set(Font.font("Arial", FontWeight.BOLD, 12));
        hotline.fillProperty().set(Color.WHITE);
        footer.getChildren().add(hotline);

        root.setBottom(footer);

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Hotel Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void handleLogin(TextField usernameField, PasswordField passwordField, Stage primaryStage) {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();
    System.out.println("handleLogin: username=" + username + ", password=" + password);

    if (username.isEmpty() || password.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
        return;
    }

    try {
        NhanVien user = authenticate(username, password);
        if (user != null) {
            UserInfoBox.setCurrentUser(user);
            dataManager.setCurrentNhanVien(user);
            System.out.println("Đăng nhập thành công với vai trò: " + user.getChucVu());
            HomeUI homeUI = new HomeUI();
            homeUI.start(primaryStage);
        } else {
            showAlert(Alert.AlertType.ERROR, "Sai tài khoản hoặc mật khẩu!");
        }
    } catch (SQLException e) {
        System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        showAlert(Alert.AlertType.ERROR, "Không thể kết nối đến cơ sở dữ liệu. Vui lòng thử lại sau!");
    }
}

    private static NhanVien authenticate(String username, String password) throws SQLException {
        System.out.println("Đang xác thực: username=" + username + ", password=" + password);
        TaiKhoan taiKhoan = taiKhoanDao.authenticate(username, password);
        if (taiKhoan != null) {
            System.out.println("Tài khoản khớp: tenDangNhap=" + taiKhoan.getTenDangNhap() +
                              ", matKhau=" + taiKhoan.getMatKhau() +
                              ", maNhanVien=" + taiKhoan.getMaNhanVien());
            // Không yêu cầu maNhanVien bắt buộc
            if (taiKhoan.getMaNhanVien() != null && !taiKhoan.getMaNhanVien().isEmpty()) {
                NhanVien nhanVien = nhanVienDao.getNhanVienByMa(taiKhoan.getMaNhanVien());
                if (nhanVien != null) {
                    System.out.println("Tìm thấy nhân viên: " + nhanVien.getTenNhanVien());
                    return nhanVien;
                } else {
                    System.err.println("Không tìm thấy nhân viên cho maNhanVien=" + taiKhoan.getMaNhanVien());
                }
            }
            // Tạo NhanVien mặc định cho admin nếu maNhanVien là null
            System.out.println("Tài khoản không liên kết nhân viên, trả về NhanVien mặc định cho admin");
            return new NhanVien(
                "ADMIN", // maNhanVien
                "Quản trị viên", // tenNhanVien
                "", // soDienThoai
                true, // gioiTinh
                "", // diaChi
                "Quản lý", // chucVu
                0.0 // luong
            );
        } else {
            System.out.println("Không tìm thấy tài khoản hoặc mật khẩu sai cho username=" + username);
        }
        return null;
    }

    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Lỗi" : "Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}