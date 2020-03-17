package objects.visibleObjects;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import objects.behavior.StaticObject;

public class Island extends StaticObject {
    private double maxX;
    private double maxY;

    public Island(double x, double y, double width, double height, BorderPane borderPane) {
        super(x, y, width, height, borderPane, new Image("file:resources/images/island.png"));

        this.maxX = this.x + width;
        this.maxY = this.y + height;
    }

    public double getSurface() {
        return this.y - 40;
    }

    public double getCorner() {
        return this.maxX - 40;
    }

    public double getBottomOfIsland() {
        return this.maxY;
    }
}
