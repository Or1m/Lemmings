package objects.visibleObjects;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import objects.behavior.StaticObject;

public class Obstacle extends StaticObject {
    private boolean ignored = false;

    public Obstacle(double x, double y, BorderPane borderPane) {
        super(x, y, borderPane, new Image("file:resources/images/obstacle.png"));
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public boolean isIgnored() {
        return ignored;
    }
}
