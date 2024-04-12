import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class Main extends Application {
    @Override
    public void start(Stage stage){
        Pane root = new Pane();
        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Pacman");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}