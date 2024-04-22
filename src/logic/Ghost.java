package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import util.Config;
import util.InputUtility;

import java.util.ArrayList;

public abstract class Ghost extends Entity implements Collidable  {
    private Image sprite;
    private Vector2D velocity;

    public Ghost(double x, double y, double width, double height, String imgPath) {
        super(x, y, width, height);
        // Load the image
        sprite = new Image(ClassLoader.getSystemResource(imgPath).toString());
        // Initialize the velocity to 0 in both x and y direction
        velocity = new Vector2D(0, 0);

    }

    private Direction getCurrentDirection() {
        if (velocity.getX() < 0) {
            return Direction.LEFT;
        } else if (velocity.getX() > 0) {
            return Direction.RIGHT;
        } else if (velocity.getY() < 0) {
            return Direction.UP;
        } else if (velocity.getY() > 0) {
            return Direction.DOWN;
        }
        return null;
    }

    @Override
    public int getZIndex() {
        return Config.GHOST_Z_INDEX;
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Draw the sprite at the current position scaled to the unit width
        gc.drawImage(sprite,
                position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                width,
                height);
    }

    public void update(double delta) {
    }

    private void move(double delta) {
    }

    @Override
    public Rectangle getCollisionBox() {
        GamePanel gamePanel = GameController.getInstance().getGamePanel();
        return new Rectangle(position.getX() * gamePanel.getUnitWidth() + gamePanel.getXPadding(), position.getY() * gamePanel.getUnitWidth() + gamePanel.getYPadding(), width, height);
    }
}




