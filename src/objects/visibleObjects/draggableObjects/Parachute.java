package objects.visibleObjects.draggableObjects;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import objects.behavior.StaticObject;

// parachute sign created by button
public class Parachute extends StaticObject {
    private boolean freeze = false;

    public Parachute(double x, double y, BorderPane borderPane) {
        super(x, y, borderPane, new Image("file:resources/images/parachuteSign.png"));
    }

    @Override
    public void setX(double x) {
        if(!freeze) {
            this.x = x;
            this.updateUI();
        }
    }

    @Override
    public void setY(double y) {
        if(!freeze){
            this.y = y;
            this.updateUI();
        }
    }

    public void freeze() {
        freeze = true;
    }

    public boolean isFrozen() {
        return freeze;
    }
}
