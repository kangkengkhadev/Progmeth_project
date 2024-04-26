import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.Scene;
import logic.GameController;
import logic.GamePanel;
import util.Config;

public class Main extends Application {
    long startTime = System.nanoTime();
    double accumulateFps = 0;

    @Override
    public void start(Stage stage) {
        // Setup the window
//
        VBox root = new VBox();
        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Pacbubu");
        stage.setScene(scene);

        root.setId("pane");
        scene.getStylesheets().add(getClass().getResource("/fontstyle.css").toExternalForm());


        Label label = new Label("Pacbubu");
        label.setStyle("-fx-font-size: 200px ");
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        label.setTextAlignment(TextAlignment.CENTER);


        Button btn1 = new Button("Play");
        Button btn2 = new Button("Exit");
        btn1.setAlignment(Pos.CENTER);
        btn2.setAlignment(Pos.CENTER);
        btn1.setPadding(new Insets(10, 40, 2, 40));
        btn1.setStyle("-fx-font-size: 80px");
        btn2.setPadding(new Insets(2, 40, 10, 40));
        btn2.setStyle("-fx-font-size: 80px");


        root.getChildren().add(label);
        root.getChildren().add(btn1);
        root.getChildren().add(btn2);


        // Setup the game panel (Canvas)
        GamePanel gamePanel = new GamePanel(1280, 720);
        GraphicsContext gc = gamePanel.getGraphicsContext2D();

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

        btn1.setOnAction(e -> {
            root.getChildren().removeAll(btn1, btn2, label);
            root.getChildren().add(gamePanel);
            gamePanel.requestFocus();
            gameLoop.start();
        });

        btn2.setOnAction(e -> {
            ((Stage) root.getScene().getWindow()).close();
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}