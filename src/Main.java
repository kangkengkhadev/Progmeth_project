import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import logic.GameController;
import logic.GamePanel;
import logic.SceneController;
import util.Config;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Pacbubu");
        new SceneController(stage);
        SceneController.getInstance().transitionToMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}