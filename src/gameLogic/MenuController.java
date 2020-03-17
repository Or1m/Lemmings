package gameLogic;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import objects.visibleObjects.Sheep;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// main menu of game
class MenuController {
    private int width;
    private int height;
    private int numOfSheeps = 13;

    private boolean drum = false;

    private Button playButton;
    private Button highScoreButton;
    private Button quitButton;
    private Button musicButton;
    private MediaPlayer mediaPlayer;

    private Stage highScoreStage;
    private Scene scene;
    private BorderPane root;
    private TextFlow text_flow;


    private BackgroundImage bcgImage;
    private BackgroundImage bcgImage2;

    private List<Sheep> sheeps = new ArrayList<>();
    private List<Text> texts = new ArrayList<>();

    Scene createScene(int width, int height) throws IOException {
        this.width = width;
        this.height = height;

        Image image = new Image("file:resources/images/background.png");
        Image image2 = new Image("file:resources/images/b2.png");

        bcgImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        bcgImage2 = new BackgroundImage(image2, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

        root = new BorderPane();
        root.setBackground(new Background(bcgImage));

        Image titleImage = new Image("file:resources/images/title.png");
        ImageView titleImageView = new ImageView(titleImage);
        titleImageView.setFitWidth(titleImage.getWidth() * 1.9);
        titleImageView.setFitHeight(titleImage.getHeight() * 1.9);
        titleImageView.setX(width / 2 - titleImage.getWidth() * 0.9);
        titleImageView.setY(15);

        root.getChildren().add(titleImageView);

        initSheeps();

        initHighScore();

        initMediaPlayer();

        addSignature();

        return new Scene(root, width, height);
    }

    void menuLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long l) {
                for (Sheep sheep : sheeps) {
                    checkForBorder(sheep);
                    sheep.setSpeedY(0);
                    sheep.move();
                    sheep.updateUI();
                }
            }
        }.start();
    }

    private void checkForBorder(Sheep sheep) {
        if (sheep.getX() >= width - sheep.getWidth()) {
            sheep.setX(width - sheep.getWidth());
            sheep.forceRotate();
        }

        if (sheep.getX() <= 0) {
            sheep.setX(0);
            sheep.forceRotate();
        }
    }

    private void drum() {
        Random random = new Random();

        if(!drum) {
            mediaPlayer.play();
            root.setBackground(new Background(bcgImage2));
            for (Sheep sheep : sheeps) {
                double speed = 20 + random.nextDouble() * 50;
                sheep.setSpeedX(speed);
            }
        }
        else {
            mediaPlayer.stop();
            root.setBackground(new Background(bcgImage));
            for (Sheep sheep : sheeps) {
                double speed = 1 + random.nextDouble() * 2;
                sheep.setSpeedX(speed);
            }
        }
        drum = !drum;
    }

    private void startGame(Stage primaryStage) {
        Routines routines = new Routines();
        routines.startGame(primaryStage, this.width, this.height);
        mediaPlayer.stop();
    }

    // init functions
    private void initSheeps() {
        for (int i = 0; i < numOfSheeps; i++) {
            int min = 1;
            int max = 2;
            Random rand = new Random();
            int initDirection;
            double initSpeedX = min + rand.nextDouble() * max;

            initDirection = rand.nextInt(100) >= 50 ? 1 : -1;

            sheeps.add(new Sheep(i * 100, 600, initDirection, initSpeedX, root));
        }
    }

    private void initMediaPlayer() {
        String musicFile = "resources/tripToTheIsland.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.02);
    }

    private void initHighScore() throws IOException {
        File highScore = new File("resources/highScore.txt");
        BufferedReader br = new BufferedReader(new FileReader(highScore));

        highScoreStage = new Stage();
        highScoreStage.setTitle("High score");
        highScoreStage.alwaysOnTopProperty();
        text_flow = new TextFlow();
        text_flow.setLineSpacing(2);
        text_flow.setTextAlignment(TextAlignment.CENTER);
        ScrollPane scrollPane = new ScrollPane(text_flow);
        scrollPane.setFitToWidth(true);

        scene = new Scene(scrollPane, 250, 330);

        String st;
        int in = 0;
        while ((st = br.readLine()) != null) {
            Text text = new Text(st + "\n");
            if (in == 0) {
                text.setFill(Color.RED);
                text.setStyle("-fx-font: 36 arial;");
            } else {
                text.setFill(Color.BLACK);
                text.setStyle("-fx-font: 24 arial;");
            }

            texts.add(text);
            in++;
        }

        for(Text text : texts) {
            text_flow.getChildren().add(text);
        }

        highScoreStage.setScene(scene);
    }


    // handling buttons
    void initButtons(Stage primaryStage) {
        initMusicButton();

        VBox vBox = new VBox();
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPrefWidth(200);
        vBox.setPrefHeight(80);

        BorderPane.setMargin(vBox, new Insets(120, 0, 0, 90));

        playButton = new Button("Play game");
        playButton.setOnAction(actionEvent -> startGame(primaryStage));
        playButton.setMinWidth(vBox.getPrefWidth());
        playButton.setMinHeight(vBox.getPrefHeight());

        highScoreButton = new Button("High score");
        highScoreButton.setOnAction(actionEvent -> highScoreStage.show());
        highScoreButton.setMinWidth(vBox.getPrefWidth());
        highScoreButton.setMinHeight(vBox.getPrefHeight());

        quitButton = new Button("Quit game");
        quitButton.setOnAction(actionEvent -> primaryStage.close());
        quitButton.setMinWidth(vBox.getPrefWidth());
        quitButton.setMinHeight(vBox.getPrefHeight());


        quitButton.setStyle("-fx-font-size: 30; -fx-background-color: #ff0000");
        playButton.setStyle("-fx-font-size: 30; -fx-background-color: #00ff00");
        highScoreButton.setStyle("-fx-font-size: 30; -fx-background-color: #ffff00");
        musicButton.setStyle("-fx-background-color: #ffffFF");

        vBox.getChildren().addAll(playButton, highScoreButton, quitButton);

        setupButtonEffects();

        root.setCenter(vBox);
        root.setRight(musicButton);
    }

    private void initMusicButton() {
        Image musicImgOn = new Image("file:resources/images/musicOn.png");
        Image musicImgOff = new Image("file:resources/images/musicOff.png");

        ImageView musicImgView = new ImageView(musicImgOff);
        musicImgView.setFitWidth(musicImgOn.getWidth() / 6);
        musicImgView.setFitHeight(musicImgOn.getHeight() / 6);

        musicButton = new Button("", musicImgView);
        musicButton.setOnAction(actionEvent -> {
            if(!drum)
                musicImgView.setImage(musicImgOn);
            else
                musicImgView.setImage(musicImgOff);

            drum();
        });
        BorderPane.setMargin(musicButton, new Insets(10, 10, 10, 10));
    }

    private void setupButtonEffects() {
        DropShadow shadow = new DropShadow();
        playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> playButton.setEffect(shadow));
        highScoreButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> highScoreButton.setEffect(shadow));
        quitButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> quitButton.setEffect(shadow));
        musicButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> musicButton.setEffect(shadow));

        playButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> playButton.setEffect(null));
        highScoreButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> highScoreButton.setEffect(null));
        quitButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> quitButton.setEffect(null));
        musicButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> musicButton.setEffect(null));
    }

    private void addSignature() {
        Text text = new Text();
        text.setText("Developed by Miroslav Kačeriak, 2019");
        text.setX(width - 220);
        text.setY(height - 20);
        text.setFill(Color.WHITE);
    }
}
