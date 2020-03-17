package gameLogic;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private final int WIDTH = 1280;
    private final int HEIGHT = 720;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Routines routines = new Routines();
        routines.startMenu(primaryStage, WIDTH, HEIGHT);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
