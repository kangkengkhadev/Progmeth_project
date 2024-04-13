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
        Pane root = new Pane();
        Scene scene = new Scene(root);
        stage.setTitle("Pacman");
        stage.setScene(scene);

        GamePanel gamePanel = new GamePanel(1280, 720);
        GraphicsContext gc = gamePanel.getGraphicsContext2D();
        root.getChildren().add(gamePanel);

        gamePanel.requestFocus();

        stage.show();

        GameController.getInstance().start(gc);
        // Start the game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                accumulateFps += (currentTime - startTime) / (1000000000.0 / Config.FPS_CAP);
                if (accumulateFps >= 1) {
                    double delta = (currentTime - startTime) / 1000000000.0;
                    startTime = currentTime;
                    GameController.getInstance().update(delta);
                    GameController.getInstance().render(gc);
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