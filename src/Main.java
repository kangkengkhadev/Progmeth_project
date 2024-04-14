import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import logic.GameController;
import logic.GamePanel;
import util.Config;
import util.InputUtility;

public class Main extends Application {
    long startTime = System.nanoTime();
    double accumulateFps = 0;

    @Override
    public void start(Stage stage) {
        // Setup the window
        Pane root = new Pane();
        Scene scene = new Scene(root);
        stage.setTitle("Pacman");
        stage.setScene(scene);

        // Setup the game panel (Canvas)
        GamePanel gamePanel = new GamePanel(1280, 720);
        GraphicsContext gc = gamePanel.getGraphicsContext2D();
        root.getChildren().add(gamePanel);

        gamePanel.requestFocus();

        stage.show();

        // Start the game
        GameController.getInstance().start(gc);
        // Start the game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}