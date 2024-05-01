package logic;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import util.Config;


public class SceneController {
    private static SceneController instance;
    private Stage rootStage;
    private long startTime = System.nanoTime();
    private double accumulateFps = 0;

    public SceneController(Stage rootStage) {
        this.rootStage = rootStage;

        instance = this;
    }

    public static SceneController getInstance() {
        return instance;
    }

    public void transitionToMainMenu() {
        VBox root = new VBox();
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(ClassLoader.getSystemResource("style.css").toExternalForm());
        root.setAlignment(Pos.CENTER);

        rootStage.setScene(scene);

        Label gameTitle = new Label("Pacbubu");
        gameTitle.setStyle("-fx-font-size: 200px ");
        gameTitle.setTextAlignment(TextAlignment.CENTER);

        Button playButton = new Button("Play");
        Button exitButton = new Button("Exit");
        playButton.setAlignment(Pos.CENTER);
        exitButton.setAlignment(Pos.CENTER);
        playButton.setPadding(new Insets(10, 40, 2, 40));
        playButton.setStyle("-fx-font-size: 80px");
        exitButton.setPadding(new Insets(2, 40, 10, 40));
        exitButton.setStyle("-fx-font-size: 80px");

        root.getChildren().add(gameTitle);
        root.getChildren().add(playButton);
        root.getChildren().add(exitButton);

        playButton.setOnAction(e -> {
            transitionToGamePanel();
        });

        exitButton.setOnAction(e -> {
            rootStage.close();
        });

        rootStage.show();
    }

    public void transitionToGamePanel() {
        startTime = System.nanoTime();

        Pane root = new Pane();
        Scene scene = new Scene(root, 1280, 720);

        rootStage.setScene(scene);

        GamePanel gamePanel = new GamePanel(1280, 720);
        GraphicsContext gc = gamePanel.getGraphicsContext2D();
        root.getChildren().add(gamePanel);
        gamePanel.requestFocus();

        GameController.getInstance().start(gc);
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                if (GameController.getInstance().isGameOver()) {
                    stop();
                    transitionToGameOver();
                }

                // Calculate the time passed
                accumulateFps += (currentTime - startTime) / (1000000000.0 / Config.FPS_CAP);
                // Update the game if the time passed is more than 1 / FPS_CAP seconds
                if (accumulateFps >= 1) {
                    // Calculate delta time
                    double delta = (currentTime - startTime) / 1000000000.0;
                    startTime = currentTime;
                    // Update and render the game
                    GameController.getInstance().update(delta);
                    GameController.getInstance().render(gc);
                    // Reset the time passed
                    accumulateFps -= 1;
                }
            }
        };
        gameLoop.start();

        rootStage.show();
    }

    public void transitionToGameOver() {
        VBox root = new VBox();
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(ClassLoader.getSystemResource("style.css").toExternalForm());
        root.setAlignment(Pos.CENTER);

        rootStage.setScene(scene);

        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setStyle("-fx-font-size: 200px ");
        gameOverLabel.setTextAlignment(TextAlignment.CENTER);

        Button playButton = new Button("Play Again");
        Button exitButton = new Button("Exit");
        playButton.setAlignment(Pos.CENTER);
        exitButton.setAlignment(Pos.CENTER);
        playButton.setPadding(new Insets(10, 40, 2, 40));
        playButton.setStyle("-fx-font-size: 80px");
        exitButton.setPadding(new Insets(2, 40, 10, 40));
        exitButton.setStyle("-fx-font-size: 80px");

        root.getChildren().add(gameOverLabel);
        root.getChildren().add(playButton);
        root.getChildren().add(exitButton);

        playButton.setOnAction(e -> {
            transitionToGamePanel();
        });

        exitButton.setOnAction(e -> {
            rootStage.close();
        });

        rootStage.show();
    }
}
