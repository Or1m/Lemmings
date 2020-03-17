package objects.visibleObjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import objects.behavior.Clickable;
import objects.behavior.Collidable;
import objects.behavior.GameObject;

public class Sheep implements GameObject, Collidable, Clickable {
    private double x;
    private double y;
    private double width;
    private double height;

    private double speedX;
    private double speedY = 3;
    private int direction;

    // states
    private boolean hasParachute = false;
    private boolean hasSign = false;
    private boolean hasStop = false;

    private boolean isFalling = false;
    private boolean isWalking = false;
    private boolean inTheHole = false;

    private boolean freeze = true;
    private boolean parachuted = false;
    private boolean firstTimeParachute = true;
    private boolean clickable = false;
    private boolean lockedSign = false;

    private boolean lckHole = false;

    private BorderPane layer;

    // imageViews
    private ImageView imageView;
    private ImageView parachuteImageView;
    private ImageView signImageView;
    private ImageView bloodImageView;
    private ImageView stopImageView;

    public Sheep(double x, double y, int direction, double initSpeedX, BorderPane borderPane) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speedX = initSpeedX;
        this.layer = borderPane;


        initComponents();
        initUI();
        addToLayer();
    }

    private void initComponents() {
        int parachuteWidth = 100;
        int parachuteHeight = 60;

        int signWidth = 40;
        int signHeight = 40;

        int bloodWidth = 65;
        int bloodHeight = 20;

        int stopWidth = 32;
        int stopHeight = 32;

        Image parachuteImg = new Image("file:resources/images/parachute.png");

        parachuteImageView = new ImageView(parachuteImg);
        parachuteImageView.setFitWidth(parachuteWidth);
        parachuteImageView.setFitHeight(parachuteHeight);

        Image signImg = new Image("file:resources/images/ignore.png");

        signImageView = new ImageView(signImg);
        signImageView.setFitWidth(signWidth);
        signImageView.setFitHeight(signHeight);

        Image bloodImg = new Image("file:resources/images/blood.png");

        bloodImageView = new ImageView(bloodImg);
        bloodImageView.setFitWidth(bloodWidth);
        bloodImageView.setFitHeight(bloodHeight);

        Image stopImg = new Image("file:resources/images/stop.png");

        stopImageView = new ImageView(stopImg);
        stopImageView.setFitWidth(stopWidth);
        stopImageView.setFitHeight(stopHeight);
    }

    // methods overridden from GameObject interface
    @Override
    public void initUI() {
        Image image = new Image("file:resources/images/sheep.png");
        this.width = image.getWidth();
        this.height = image.getHeight();

        this.imageView = new ImageView(image);

        if(this.direction == -1)
            rotateSprite();

        updateUI();
    }

    @Override
    public void updateUI() {
        this.imageView.relocate(this.x, this.y);
        parachuteImageView.relocate(this.x - 20, this.y - 55);
    }

    @Override
    public void addToLayer() {
        this.layer.getChildren().add(this.imageView);
    }

    @Override
    public void rotate() {
        if(!this.isFrozen())
            this.imageView.setScaleX(this.direction = -this.direction);
    }

    @Override
    public void removeFromLayer() {
        this.layer.getChildren().remove(this.imageView);
    }

    // method overridden from Clickable interface
    @Override
    public boolean isInBound(double eventX, double eventY) {
        return (eventX >= this.x && eventX <= this.x + this.width && eventY >= this.y && eventY <= this.y + this.height);
    }


    // rotate while freezed, relocate signs
    public void forceRotate() {
        this.imageView.setScaleX(this.direction = -this.direction);

        if(this.hasStop) {
            if(this.direction == 1)
                stopImageView.relocate(this.x + 10, this.y + 5);
            else
                stopImageView.relocate(this.x + 35, this.y + 5);
        }

        if(hasSign) {
            if(this.direction == 1)
                signImageView.relocate(this.x + 8, this.y);
            else
                signImageView.relocate(this.x + 35, this.y);
        }
    }

    public void move() {
        this.x += this.speedX * this.direction;
        this.y += this.speedY;
    }

    public void die() {
        clickable = false;
        freeze = true;

        this.imageView.setScaleY(-1);
        this.y -= 10;

        this.layer.getChildren().add(bloodImageView);
        this.imageView.toBack();

        bloodImageView.relocate(this.x, this.y + height);
        bloodImageView.toBack();
    }

    public void addSpeedY() {
        double gravity;
        if(hasParachute)
            gravity = 0.000001;
        else
            gravity = 0.2;

        this.speedY += gravity;
    }

    public void switchFreeze() {
        this.freeze = !this.freeze;
    }

    public void makeSmall() {
        this.imageView.setFitWidth(this.width / 1.5);
        this.imageView.setFitHeight(this.height / 1.5);
    }

    private void rotateSprite() {
        this.imageView.setScaleX(this.direction);
    }

    public void signToFront() {
        this.signImageView.toFront();
    }


    // spawn functions
    public void spawnParachute() {
        if(this.firstTimeParachute) {
            this.layer.getChildren().add(parachuteImageView);
            this.firstTimeParachute = false;
        }

        this.hasParachute = true;
        this.parachuted = true;
    }

    public void spawnSign() {
        this.layer.getChildren().add(signImageView);

        if(this.direction == 1)
            signImageView.relocate(this.x + 8, this.y);
        else
            signImageView.relocate(this.x + 35, this.y);

        this.switchFreeze();
        this.hasSign = true;
    }

    public void spawnStop() {
        this.layer.getChildren().add(stopImageView);

        if(this.direction == 1)
            stopImageView.relocate(this.x + 10, this.y + 5);
        else
            stopImageView.relocate(this.x + 35, this.y + 5);

        this.switchFreeze();
        this.hasStop = true;
    }


    // remove functions
    public void removeParachute() {
        this.layer.getChildren().removeAll(parachuteImageView);
    }

    public void removeEffects() {
        hasParachute = false;
        hasSign = false;
        hasStop = false;

        this.layer.getChildren().removeAll(parachuteImageView, stopImageView);

        if(!lockedSign) {
            this.signImageView.relocate(this.x, this.y + 12);
            lockedSign = true;
        }
    }

    // change state functions
    public void fallDown() {
        if(!isFalling)
            speedY += 2;

        isFalling = true;
        isWalking = false;
    }

    public void walk() {
        isFalling = false;
        isWalking = true;
    }

    public boolean toTheHole() {
        if(!inTheHole) {
            this.speedY = 3;
            inTheHole = true;
        }

        if(this.y > 620 && !lckHole) {
            this.removeFromLayer();
            lckHole = !lckHole;
            return true;
        }

        return false;
    }


    // setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public void setClickable(boolean inactive) {
        this.clickable = inactive;
    }

    public void setDirection(int direction) {
        this.imageView.setScaleX(direction);
    }


    // getters
    public boolean isFrozen() {
        return this.freeze;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getDirection() {
        return direction;
    }

    public boolean isParachuted() {
        return parachuted;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public boolean hasStop() {
        return hasStop;
    }

    public boolean isClickable() {
        return clickable;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public boolean isInTheHole() {
        return inTheHole;
    }
}
