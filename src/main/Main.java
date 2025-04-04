package main;

import UI.LoginUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginUI.showLogin(primaryStage);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}