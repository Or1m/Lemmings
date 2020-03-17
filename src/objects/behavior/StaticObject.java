package objects.behavior;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

// base class of all stationary objects
public abstract class StaticObject implements GameObject, Collidable {
    protected double x;
    protected double y;
    private double width;
    private double height;
    private int direction = 1;

    protected Image image;
    private ImageView imageView;
    private BorderPane layer;

    // for static objects with fixed image size
    public StaticObject(double x, double y, BorderPane borderPane, Image image) {
        this.x = x;
        this.y = y;
        this.layer = borderPane;

        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();

        initUI();
        addToLayer();
    }

    // for static objects with dynamic size
    public StaticObject(double x, double y, double width, double height, BorderPane borderPane, Image image) {
        this.x = x;
        this.y = y;
        this.layer = borderPane;

        this.image = image;
        this.width = width;
        this.height = height;

        initUI();
        addToLayer();
    }

    // methods overridden from GameObject interface
    @Override
    public void initUI() {
        this.imageView = new ImageView(this.image);
        this.imageView.setFitWidth(this.width);
        this.imageView.setFitHeight(this.height);
        updateUI();
    }

    @Override
    public void updateUI() {
        this.imageView.relocate(this.x, this.y);
    }

    @Override
    public void addToLayer() {
        this.layer.getChildren().add(this.imageView);
    }

    @Override
    public void rotate() {
        this.imageView.setScaleX(this.direction = -this.direction);
    }

    @Override
    public void removeFromLayer() {
        this.layer.getChildren().remove(this.imageView);
    }

    // getters
    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public int getDirection() {
        return this.direction;
    }

    // setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
