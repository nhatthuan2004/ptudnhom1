package UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Khởi tạo DataManager một lần duy nhất
        DataManager dataManager = DataManager.getInstance();

        // Khởi tạo QLDichVu và QLphongUI mà không truyền tham số
        QLDichVu qlDichVu = new QLDichVu();
        QLphongUI qlPhong = new QLphongUI();

        // Tạo giao diện chính với hai nút để chuyển đổi
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        Button btnQLDichVu = new Button("Quản Lý Dịch Vụ");
        Button btnQLPhong = new Button("Quản Lý Phòng");

        btnQLDichVu.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(qlDichVu.getUI(), 1120, 800));
            stage.setTitle("Quản Lý Dịch Vụ");
            stage.show();
        });

        btnQLPhong.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(qlPhong.getUI(), 1120, 800));
            stage.setTitle("Quản Lý Phòng");
            stage.show();
        });

        root.getChildren().addAll(btnQLDichVu, btnQLPhong);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ứng Dụng Quản Lý");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}