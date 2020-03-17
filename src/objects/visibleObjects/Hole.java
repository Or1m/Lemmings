package objects.visibleObjects;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import objects.behavior.StaticObject;

public class Hole extends StaticObject {

    public Hole(double x, double y, BorderPane borderPane) {
        super(x, y, borderPane, new Image("file:resources/images/hole.png"));
    }
}
