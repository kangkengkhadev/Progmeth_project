package logic;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import util.Config;
import util.InputUtility;

public class GamePanel extends Canvas {

    public GamePanel(double width, double height) {
        super(width, height);
        this.setVisible(true);
        addEventListener();
    }

    private void addEventListener() {
        this.setOnKeyPressed((KeyEvent event) -> {
            InputUtility.setKeyPressed(event.getCode(), true);
        });

        this.setOnKeyReleased((KeyEvent event) -> {
            InputUtility.setKeyPressed(event.getCode(), false);
        });
    }
}
