package gameLogic;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

class Routines {

    void startMenu(Stage primaryStage, int width, int height) throws IOException {
        Stage menuStage = new Stage();
        MenuController menu = new MenuController();
        Scene scene = menu.createScene(width, height);

        menuStage.setTitle("Lemmings");
        menuStage.getIcons().add(new Image("file:resources/images/favicon.png"));
        menuStage.setResizable(false);

        menuStage.setScene(scene);
        menuStage.show();
        primaryStage.close();

        menu.initButtons(menuStage);
        menu.menuLoop();
    }

    void startGame(Stage primaryStage, int width, int height) {
        Stage newGameStage = new Stage();
        Controller controller = new Controller();
        Scene scene = controller.createScene(width, height);

        newGameStage.setTitle("Lemmings");
        newGameStage.getIcons().add(new Image("file:resources/images/favicon.png"));
        newGameStage.setResizable(false);

        newGameStage.setScene(scene);
        newGameStage.show();
        primaryStage.close();

        controller.addListeners(newGameStage);
        controller.gameLoop();
    }
}
