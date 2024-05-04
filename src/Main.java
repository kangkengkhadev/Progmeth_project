import javafx.application.Application;
import javafx.stage.Stage;
import render.SceneController;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Pacbubu");
        new SceneController(stage);
        SceneController.getInstance().showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}