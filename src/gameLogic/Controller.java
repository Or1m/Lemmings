package gameLogic;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import objects.Player;
import objects.visibleObjects.draggableObjects.IgnoreObstacle;
import objects.visibleObjects.draggableObjects.Parachute;
import objects.visibleObjects.draggableObjects.Stop;
import objects.visibleObjects.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller {
    private int width;
    private int height;

    private final int numOfSheeps = 20;
    private final int minToWin = 15;
    private int finishedSheeps = 0;
    private int deadSheeps = 0;
    private int offsetX = 60;

    private BorderPane root;
    private Finish finish;
    private Island island;
    private Hole hole;
    private Parachute parachute;
    private IgnoreObstacle ignoreObstacle;
    private Stop stop;

    private Stage primaryStage;
    private Routines routines;
    private HBox hBox;

    private Button parachuteButton;
    private Button ignoreButton;
    private Button stopButton;
    private Button menuButton;
    private Button restartButton;

    private AnimationTimer timer;

    private List<Sheep> sheeps = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private boolean[] stoppedSheeps = new boolean[numOfSheeps];

    Scene createScene(int width, int height) {
        this.width = width;
        this.height = height;

        Image image = new Image("file:resources/images/background.png");
        BackgroundImage bcgImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

        root = new BorderPane();
        root.setBackground(new Background(bcgImage));

        routines = new Routines();

        // adding static objects
        Spawner spawner = new Spawner(125, 75, root);
        finish = new Finish(width - 260, 470, root);
        island = new Island(0, 400, 600, 70, root);
        hole = new Hole(20, 620, root);

        obstacles.add(new Obstacle(415, 325, root));
        obstacles.add(new Obstacle(890, 560, root));

        addObjectButtons();
        addManagementButtons();

        spawner.spawnSheeps(sheeps, numOfSheeps, root);

        return new Scene(root, width, height);
    }

    void gameLoop() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (numOfSheeps - deadSheeps != finishedSheeps) {
                    for (Sheep sheep : sheeps) {
                        if (!sheep.isFrozen()) {
                            checkForIsland(sheep);

                            if (checkForHole(sheep))
                                deadSheeps++;

                            if (fallDamage(sheep))
                                deadSheeps++;

                            checkForIgnoreSign(sheep);

                            checkForStopSign(sheep);

                            checkForObstacle(sheep);

                            checkForParachute(sheep);

                            checkForBorder(sheep);

                            checkForFinish(sheep);

                            if (sheep.hasStop())
                                stoppedSheeps[sheeps.indexOf(sheep)] = true;

                            s2sCollision(sheep);

                            sheep.move();
                            sheep.updateUI();
                        }
                    }
                } else {
                    timer.stop();
                    submitScore();
                }
            }
        };
        timer.start();
    }


    // checking functions
    // check for dynamic objects
    private void checkForParachute(Sheep sheep) {
        if (parachute != null)
            if (sheep.touch(parachute) && sheep.isFalling())
                sheep.spawnParachute();
    }

    private void checkForIgnoreSign(Sheep sheep) {
        if (ignoreObstacle != null) {
            if (sheep.touch(ignoreObstacle) && sheep.isWalking() && ignoreObstacle.isFrozen()) {
                sheep.spawnSign();

                for (Obstacle obstacle : obstacles) {
                    if (ignoreObstacle.getX() + 150 >= obstacle.getX())
                        obstacle.setIgnored(true);
                }

                ignoreObstacle.removeFromLayer();
                ignoreObstacle = null;
            }
        }
    }

    private void checkForStopSign(Sheep sheep) {
        if (stop != null) {
            if (sheep.touch(stop) && sheep.isWalking() && stop.isFrozen()) {
                sheep.spawnStop();

                stop.removeFromLayer();
                stop = null;
            }
        }
    }


    private void checkForBorder(Sheep sheep) {
        if (sheep.getX() >= width - sheep.getWidth()) {
            sheep.setX(width - sheep.getWidth());
            sheep.rotate();
        }

        if (sheep.getX() <= 0) {
            sheep.setX(0);
            sheep.rotate();
        }
    }

    private void checkForObstacle(Sheep sheep) {
        for (Obstacle obstacle : obstacles) {
            if (sheep.touch(obstacle) && !obstacle.isIgnored())
                sheep.rotate();
        }
    }

    private boolean checkForHole(Sheep sheep) {
        if (sheep.touch(hole)) {
            return sheep.toTheHole();
        }
        return false;
    }

    private void checkForIsland(Sheep sheep) {
        if (sheep.getX() < island.getCorner() && sheep.getY() < island.getSurface()) {
            sheep.addSpeedY();
        }

        if (sheep.getX() < island.getCorner() && (sheep.getY() >= island.getSurface() && sheep.getY() <= island.getBottomOfIsland())) {
            sheep.setSpeedY(0);
            sheep.setSpeedX(3);
            sheep.walk();
        }

        if (sheep.getX() > island.getCorner() && sheep.getY() > island.getSurface()) {
            sheep.fallDown();
            sheep.addSpeedY();
        }
    }

    private void checkForFinish(Sheep sheep) {
        int offsetY = 10;

        if (sheep.touch(finish)) {
            sheep.switchFreeze();
            sheep.removeParachute();
            sheep.makeSmall();

            sheep.setClickable(false);
            sheep.setDirection(-1);
            sheep.setX(width - offsetX);
            sheep.setY(offsetY);
            offsetX += 60;
            finishedSheeps++;
        }
    }


    private boolean fallDamage(Sheep sheep) {
        int bottom = 600;
        if (sheep.getY() >= bottom && !sheep.isParachuted()) {
            sheep.setY(bottom);
            sheep.die();
            return true;
        }

        if (sheep.getY() >= bottom && sheep.isParachuted() && !sheep.isInTheHole()) {
            sheep.setSpeedX(3);
            sheep.setSpeedY(0);
            sheep.walk();

            sheep.removeParachute();
        }
        return false;
    }

    private void s2sCollision(Sheep sheep) {
        for (int i = 0; i < stoppedSheeps.length; i++)
            if (stoppedSheeps[i] && sheep.touch(sheeps.get(i)))
                sheep.rotate();
    }


    // high score handling
    private void submitScore() {
        String winLose;

        Image img = new Image("file:resources/images/favicon.png");
        ImageView icon = new ImageView(img);
        TextInputDialog dialog = new TextInputDialog("");
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(img);

        if (finishedSheeps >= minToWin)
            winLose = "You won";
        else
            winLose = "You lose";

        dialog.getDialogPane().setGraphic(icon);
        dialog.setTitle(winLose);
        dialog.setHeaderText("Your score: " + finishedSheeps);
        dialog.setContentText("Please enter your name:");

        Platform.runLater(() -> {
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(this::saveScore);
            showManagementButtons();
            for(Sheep sheep : sheeps)
                sheep.signToFront();
        });
    }

    private void saveScore(String name) {
        List<Player> players = new ArrayList<>();
        File file = new File("resources/highScore.txt");
        String st;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((st = br.readLine()) != null) {
                String[] parts = st.split(": ");
                if(parts[0].length() != 0)
                    players.add(new Player(parts[0], Integer.parseInt(parts[1])));
            }
            br.close();

            if(name.length() != 0)
                players.add(new Player(name, finishedSheeps));

            players.sort(Collections.reverseOrder(new Player.scoreComparator()));

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for(Player p : players)
                writer.write( p.toString() + "\n");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // buttons
    private void addObjectButtons() {
        Image parachuteImg = new Image("file:resources/images/parachuteSign.png");
        ImageView parachuteImgView = new ImageView(parachuteImg);
        parachuteImgView.setFitWidth(parachuteImg.getWidth() / 2);
        parachuteImgView.setFitHeight(parachuteImg.getHeight() / 2);

        parachuteButton = new Button("", parachuteImgView);
        parachuteButton.setFocusTraversable(false);
        parachuteButton.setOnAction(actionEvent -> {
            if (parachute != null)
                parachute.removeFromLayer();
            parachute = new Parachute(width, 0, root);
        });

        Image ignoreImg = new Image("file:resources/images/ignoreSign.png");
        ImageView ignoreImgView = new ImageView(ignoreImg);
        ignoreImgView.setFitWidth(ignoreImg.getWidth() / 2);
        ignoreImgView.setFitHeight(ignoreImg.getHeight() / 2);

        ignoreButton = new Button("", ignoreImgView);
        ignoreButton.setFocusTraversable(false);
        ignoreButton.setOnAction(actionEvent -> {
            if (ignoreObstacle != null)
                ignoreObstacle.removeFromLayer();
            ignoreObstacle = new IgnoreObstacle(width, 0, root);
        });

        Image stopImg = new Image("file:resources/images/stopSign.png");
        ImageView stopImgView = new ImageView(stopImg);
        stopImgView.setFitWidth(stopImg.getWidth() / 2);
        stopImgView.setFitHeight(stopImg.getHeight() / 2);

        stopButton = new Button("", stopImgView);
        stopButton.setFocusTraversable(false);
        stopButton.setOnAction(actionEvent -> {
            if (stop != null)
                stop.removeFromLayer();
            stop = new Stop(width, 0, root);
        });


        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPrefWidth(50);
        vBox.setPrefHeight(50);

        parachuteButton.setMinWidth(vBox.getPrefWidth());
        ignoreButton.setMinWidth(vBox.getPrefWidth());
        stopButton.setMinWidth(vBox.getPrefWidth());

        parachuteButton.setMinHeight(vBox.getPrefHeight());
        ignoreButton.setMinHeight(vBox.getPrefHeight());
        stopButton.setMinHeight(vBox.getPrefHeight());

        parachuteButton.setStyle("-fx-background-color: #c85aed;-fx-border-color: #000000;-fx-border-width: 0.1");
        ignoreButton.setStyle("-fx-background-color: #c8ed5a;-fx-border-color: #000000;-fx-border-width: 0.1");
        stopButton.setStyle("-fx-background-color: #ed6e5a;-fx-border-color: #000000;-fx-border-width: 0.1");

        vBox.getChildren().addAll(parachuteButton, ignoreButton, stopButton);

        BorderPane.setMargin(vBox, new Insets(10, 10, 10, 10));
        root.setLeft(vBox);
    }

    private void addManagementButtons() {
        Image restartImg = new Image("file:resources/images/restart.png");
        ImageView restartImgView = new ImageView(restartImg);
        restartImgView.setFitWidth(restartImg.getWidth() / 4);
        restartImgView.setFitHeight(restartImg.getHeight() / 4);

        restartButton = new Button("", restartImgView);
        restartButton.setFocusTraversable(false);

        restartButton.setOnAction(actionEvent -> routines.startGame(primaryStage, this.width, this.height));


        Image menuImg = new Image("file:resources/images/menu.png");
        ImageView menuImgView = new ImageView(menuImg);
        menuImgView.setFitWidth(menuImg.getWidth() / 4);
        menuImgView.setFitHeight(menuImg.getHeight() / 4);

        menuButton = new Button("", menuImgView);
        menuButton.setFocusTraversable(false);

        menuButton.setOnAction(actionEvent2 -> {
            try {
                routines.startMenu(primaryStage, this.width, this.height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        menuButton.setStyle("-fx-background-color: #82f3ff;-fx-border-color: #000000;-fx-border-width: 0.1");
        restartButton.setStyle("-fx-background-color: #ffa582;-fx-border-color: #000000;-fx-border-width: 0.1");

        setupButtonEffects();

        hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPrefWidth(50);
        hBox.setPrefHeight(50);

        hBox.getChildren().addAll(restartButton, menuButton);
        BorderPane.setMargin(hBox, new Insets(272, 10, 10, 10));
    }

    private void showManagementButtons() {
        root.setCenter(hBox);
    }

    private void setupButtonEffects() {
        DropShadow shadow = new DropShadow();
        menuButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> menuButton.setEffect(shadow));
        restartButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> restartButton.setEffect(shadow));

        menuButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> menuButton.setEffect(null));
        restartButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> restartButton.setEffect(null));


        parachuteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> parachuteButton.setEffect(shadow));
        ignoreButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> ignoreButton.setEffect(shadow));
        stopButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> stopButton.setEffect(shadow));
    }


    // events
    void addListeners(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // keyboard events
        AtomicInteger i = new AtomicInteger();
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.SPACE && i.get() < sheeps.size()) {
                sheeps.get(i.get()).switchFreeze();
                sheeps.get(i.getAndIncrement()).setClickable(true);
            }

            if (key.getCode() == KeyCode.ESCAPE) {
                try {
                    routines.startMenu(primaryStage, this.width, this.height);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(key.getCode() == KeyCode.R) {
                routines.startGame(primaryStage, this.width, this.height);
            }
        });

        primaryStage.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (parachute != null) {
                parachute.setX(event.getSceneX() - parachute.getWidth() / 2);
                parachute.setY(event.getSceneY() - parachute.getHeight() / 2);
            }

            if (ignoreObstacle != null) {
                ignoreObstacle.setX(event.getSceneX() - ignoreObstacle.getWidth() / 2);
                ignoreObstacle.setY(event.getSceneY() - ignoreObstacle.getHeight() / 2);
            }

            if (stop != null) {
                stop.setX(event.getSceneX() - stop.getWidth() / 2);
                stop.setY(event.getSceneY() - stop.getHeight() / 2);
            }
        });

        // mouse events
        primaryStage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (parachute != null) {
                parachute.freeze();
                parachuteButton.setEffect(null);
            }


            if (ignoreObstacle != null) {
                ignoreObstacle.freeze();
                ignoreButton.setEffect(null);
            }

            if (stop != null) {
                stop.freeze();
                stopButton.setEffect(null);
            }

            for (Sheep sheep : sheeps) {
                if (event.getButton() == MouseButton.PRIMARY && sheep.isInBound(event.getX(), event.getY()) && sheep.isClickable() && ignoreObstacle == null && stop == null) {
                    stoppedSheeps[sheeps.indexOf(sheep)] = false;
                    if (!sheep.isFalling())
                        sheep.switchFreeze();

                    sheep.removeEffects();
                }
                if (event.getButton() == MouseButton.SECONDARY && sheep.isInBound(event.getX(), event.getY()) && sheep.isClickable() && ignoreObstacle == null && stop == null )
                    sheep.forceRotate();
            }
        });
    }
}