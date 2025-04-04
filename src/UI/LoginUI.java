package UI;

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

public class LoginUI {
    private static DataManager dataManager = DataManager.getInstance();

    private static Image loadImage(String filePath) {
        try {
            return new Image(LoginUI.class.getResource(filePath).toExternalForm());
        } catch (Exception e) {
            System.out.println("Warning: " + filePath + " not found.");
            return new Image("https://via.placeholder.com/120");
        }
    }

    public static void showLogin(Stage primaryStage) {
        // Xóa dữ liệu cũ và thêm nhân viên mặc định
        dataManager.getNhanVienList().clear();
        System.out.println("Đã xóa danh sách nhân viên cũ");
        dataManager.getNhanVienList().add(new NhanVien(
            "NV001", "Admin", "0123456789", true, "Hà Nội", "Quản lý", 10000000, "admin", "12345", "Đang làm"
        ));
        System.out.println("Đã thêm nhân viên mặc định: admin/12345");
        System.out.println("Danh sách nhân viên hiện tại: " + dataManager.getNhanVienList());

        Image backgroundImage = loadImage("/img/anhhome4.png");
        BackgroundImage bgImage = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, true));

        BorderPane root = new BorderPane();
        root.setBackground(new Background(bgImage));

        VBox loginContainer = new VBox(15);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setStyle("-fx-background-color: rgb(255, 255, 255, 0.3);"
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
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Xác thực từ danh sách nhân viên trong DataManager
        NhanVien user = authenticate(username, password);
        if (user != null) {
            UserInfoBox.setCurrentUser(user); // Lưu người dùng hiện tại
            HomeUI homeUI = new HomeUI();
            homeUI.start(primaryStage);
        } else {
            showAlert(Alert.AlertType.ERROR, "Sai tài khoản hoặc mật khẩu!");
        }
    }

    private static NhanVien authenticate(String username, String password) {
        // Kiểm tra tài khoản và mật khẩu từ danh sách nhân viên
        for (NhanVien nv : dataManager.getNhanVienList()) {
            if (nv.getTaiKhoan().equals(username) && nv.getMatKhau().equals(password)) {
                System.out.println("Đăng nhập thành công: " + username);
                return nv;
            }
        }
        System.out.println("Đăng nhập thất bại: " + username + "/" + password);
        return null;
    }

    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}