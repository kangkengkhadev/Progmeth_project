package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import util.Config;
import util.InputUtility;

public class Pacman extends Entity {
    private Image sprite;
    private double[] velocity;

    public Pacman(double x, double y, double width, double height, String imgPath) {
        super(x, y, width, height);
        sprite = new Image(ClassLoader.getSystemResource(imgPath).toString());
        velocity = new double[] {0, 0};
    }
    @Override
    public int getZIndex() {
        return -1;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(sprite, x * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(), y * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(), width, height);
    }

    public void update(double delta) {
        if (InputUtility.getKeyPressed(KeyCode.W)) {
            velocity[0] = 0;
            velocity[1] = -Config.PACMAN_SPEED;
        } else if (InputUtility.getKeyPressed(KeyCode.A)) {
            velocity[0] = -Config.PACMAN_SPEED;
            velocity[1] = 0;
        } else if (InputUtility.getKeyPressed(KeyCode.S)) {
            velocity[0] = 0;
            velocity[1] = Config.PACMAN_SPEED;
        } else if (InputUtility.getKeyPressed(KeyCode.D)) {
            velocity[0] = Config.PACMAN_SPEED;
            velocity[1] = 0;
        }

        move(delta);
    }

    public void move(double delta) {
        setX(getX() + velocity[0] * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
        setY(getY() + velocity[1] * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
    }
}
