package UI;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws SQLException {
        // Khởi tạo DataManager một lần duy nhất
        DataManager dataManager = DataManager.getInstance();

        // Tạo giao diện chính với hai nút để chuyển đổi
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        Button btnQLDichVu = new Button("Quản Lý Dịch Vụ");
        Button btnQLPhong = new Button("Quản Lý Phòng");

        btnQLDichVu.setOnAction(e -> {
            QLDichVu qlDichVu = new QLDichVu();
            Stage stage = new Stage();
            // Giả định QLDichVu vẫn có getUI() - nếu không, cần sửa thêm
            stage.setScene(new Scene(qlDichVu.getUI(), 1120, 800));
            stage.setTitle("Quản Lý Dịch Vụ");
            stage.show();
        });

        btnQLPhong.setOnAction(e -> {
            QLphongUI qlPhong = new QLphongUI();
            Stage stage = new Stage();
            // Gọi showUI với vai trò mặc định (có thể điều chỉnh theo logic đăng nhập)
            qlPhong.showUI(stage, "Quản lý"); // Hoặc lấy role từ DataManager nếu có
            stage.setTitle("Quản Lý Phòng");
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