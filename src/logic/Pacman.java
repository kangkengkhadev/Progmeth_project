package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import util.Config;
import util.InputUtility;

public class Pacman extends Entity {
    private Image sprite;
    private Vector2D velocity;

    public Pacman(double x, double y, double width, double height, String imgPath) {
        super(x, y, width, height);
        // Load the image
        sprite = new Image(ClassLoader.getSystemResource(imgPath).toString());
        // Initialize the velocity to 0 in both x and y direction
        velocity = new Vector2D(0, 0);
    }
    @Override
    public int getZIndex() {
        return Config.PACMAN_Z_INDEX;
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Draw the sprite at the current position scaled to the unit width
        gc.drawImage(sprite, x * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(), y * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(), width, height);
    }

    public void update(double delta) {
        // Receive the input from the player
        // Update the velocity based on the input
        if (InputUtility.getKeyPressed(KeyCode.W)) {
            velocity.setX(0);
            velocity.setY(-Config.PACMAN_SPEED);
        } else if (InputUtility.getKeyPressed(KeyCode.A)) {
            velocity.setX(-Config.PACMAN_SPEED);
            velocity.setY(0);
        } else if (InputUtility.getKeyPressed(KeyCode.S)) {
            velocity.setX(0);
            velocity.setY(Config.PACMAN_SPEED);
        } else if (InputUtility.getKeyPressed(KeyCode.D)) {
            velocity.setX(Config.PACMAN_SPEED);
            velocity.setY(0);
        }

        // Move the pacman based on the velocity
        move(delta);
    }

    public void move(double delta) {
        setX(getX() + velocity.getX() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
        setY(getY() + velocity.getY() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
    }
}
