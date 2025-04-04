package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.NhanVien;

public class UserInfoBox {
    private static NhanVien currentUser; // Lưu người dùng hiện tại

    // Thiết lập người dùng hiện tại
    public static void setCurrentUser(NhanVien user) {
        currentUser = user;
    }

    // Lấy người dùng hiện tại
    public static NhanVien getCurrentUser() {
        return currentUser;
    }

    public static HBox createUserInfoBox() {
        if (currentUser == null) {
            throw new IllegalStateException("Người dùng chưa được đăng nhập");
        }

        // Xử lý tải hình ảnh avatar với fallback
        Image avatarImage;
        try {
            avatarImage = new Image(UserInfoBox.class.getResourceAsStream("/img/iconuser.png"));
            if (avatarImage.isError()) throw new Exception("Image load failed");
        } catch (Exception e) {
            System.err.println("Không tìm thấy iconuser.png: " + e.getMessage());
            avatarImage = new Image("https://via.placeholder.com/40?text=User"); // Hình ảnh thay thế
        }
        ImageView avatar = new ImageView(avatarImage);
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);

        // Hiển thị tên và chức vụ từ currentUser
        Label name = new Label(currentUser.getTenNhanVien()); // Sử dụng getTenNhanVien thay vì getHoTen
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label role = new Label(currentUser.getChucVu());
        role.setTextFill(Color.GRAY);
        role.setFont(Font.font(12));

        VBox info = new VBox(name, role);
        info.setSpacing(2);
        info.setAlignment(Pos.CENTER_LEFT);

        HBox box = new HBox(10, avatar, info);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(10));
        box.setBackground(new Background(new BackgroundFill(Color.web("#f2f2f2"), new CornerRadii(12), Insets.EMPTY)));
        box.setMaxWidth(250);
        box.setMaxHeight(80);

        HBox wrapper = new HBox(box);
        wrapper.setAlignment(Pos.TOP_RIGHT);
        wrapper.setPadding(new Insets(10, 15, 0, 0));

        return wrapper;
    }
}