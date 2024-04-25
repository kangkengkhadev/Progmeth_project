package logic;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import util.Config;
import util.InputUtility;

public class GamePanel extends Canvas {

    private double unitWidth;
    private double xPadding;
    private double yPadding;

    public GamePanel(double width, double height) {
        super(width, height);
        this.setVisible(true);
        // Calculate the unit width and padding
        unitWidth = Math.min(width / Config.MAP_X_DIMENSION, height / Config.MAP_Y_DIMENSION);
        xPadding = (width - unitWidth * Config.MAP_X_DIMENSION) / 2.0;
        yPadding = (height - unitWidth * Config.MAP_Y_DIMENSION) / 2.0;
        // Add event listener
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

    public double getUnitWidth() {
        return this.unitWidth;
    }

    public double getXPadding() {
        return this.xPadding;
    }

    public double getYPadding() {
        return this.yPadding;
    }
}
