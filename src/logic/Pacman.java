package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import util.Config;
import util.InputUtility;

import java.util.ArrayList;

public class Pacman extends Entity implements Collidable {
    private Image sprite;
    private Vector2D velocity;
    private Vector2D nextVelocity;
    private ArrayList<Rectangle> tileCollisions = new ArrayList<Rectangle>();

    public Pacman(double x, double y, double width, double height, String imgPath) {
        super(x, y, width, height);
        // Load the image
        sprite = new Image(ClassLoader.getSystemResource(imgPath).toString());
        // Initialize the velocity to 0 in both x and y direction
        velocity = new Vector2D(0, 0);
        nextVelocity = new Vector2D(0, 0);
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
        return Config.PACMAN_Z_INDEX;
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Draw the sprite at the current position scaled to the unit width
        gc.drawImage(sprite,
                position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                width,
                height);
        gc.setFill(Color.RED);
        // Draw tile collision around pacman
        for (Rectangle tileCollision : tileCollisions) {
            gc.fillRect(tileCollision.getOrigin().getX(),
                    tileCollision.getOrigin().getY(),
                    tileCollision.getWidth(),
                    tileCollision.getHeight());
        }
        Rectangle collisionBox = getCollisionBox();
        gc.setGlobalAlpha(0.5);
        gc.setFill(Color.BLUE);
        gc.fillRect(collisionBox.getOrigin().getX(),
                collisionBox.getOrigin().getY(),
                collisionBox.getWidth(),
                collisionBox.getHeight());
        gc.setGlobalAlpha(1);
        gc.setFill(Color.GREEN);
        gc.fillRect(position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                5,
                5);
    }

    private void getInput() {
        if (InputUtility.getKeyPressed(KeyCode.W)) {
            nextVelocity = new Vector2D(0, -Config.PACMAN_SPEED);
        } else if (InputUtility.getKeyPressed(KeyCode.A)) {
            nextVelocity = new Vector2D(-Config.PACMAN_SPEED, 0);
        } else if (InputUtility.getKeyPressed(KeyCode.S)) {
            nextVelocity = new Vector2D(0, Config.PACMAN_SPEED);
        } else if (InputUtility.getKeyPressed(KeyCode.D)) {
            nextVelocity = new Vector2D(Config.PACMAN_SPEED, 0);
        }
    }

    public void update(double delta) {
        getInput();
        createCollisionAroundPacman();
        // Move the pacman based on the velocity
        velocity = nextVelocity;
        // print current direction
        move(delta);
    }

    private void move(double delta) {
        Direction currentDirection = getCurrentDirection();
        Vector2D currentDiscretePosition = new Vector2D((int)position.getX(), (int)position.getY());
        if (currentDirection != null) {
            if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
                setY(currentDiscretePosition.getY());
                setX(position.getX() + velocity.getX() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
            } else {
                setX(currentDiscretePosition.getX());
                setY(position.getY() + velocity.getY() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
            }
        }
    }

    @Override
    public Rectangle getCollisionBox() {
        GamePanel gamePanel = GameController.getInstance().getGamePanel();
        return new Rectangle(position.getX() * gamePanel.getUnitWidth() + gamePanel.getXPadding(), position.getY() * gamePanel.getUnitWidth() + gamePanel.getYPadding(), width, height);
    }

    private void createCollisionAroundPacman() {
        GamePanel gamePanel = GameController.getInstance().getGamePanel();

        // Get the current discrete position of the pacman
        Vector2D currentDiscretePosition = new Vector2D((int)position.getX(), (int)position.getY());

        // Create the collision boxes for the tiles around the pacman
        tileCollisions.clear();
        // If left cell is not a wall, create a collision box
        if (GameController.getInstance().getMap().getMapInfo()[(int)currentDiscretePosition.getY()][(int)currentDiscretePosition.getX() - 1] == -1) {
            tileCollisions.add(new Rectangle((currentDiscretePosition.getX() - 1) * gamePanel.getUnitWidth() + gamePanel.getXPadding(),
                    currentDiscretePosition.getY() * gamePanel.getUnitWidth() + gamePanel.getYPadding(),
                    gamePanel.getUnitWidth(),
                    gamePanel.getUnitWidth()));
        }
        // If right cell is not a wall, create a collision box
        if (GameController.getInstance().getMap().getMapInfo()[(int)currentDiscretePosition.getY()][(int)currentDiscretePosition.getX() + 1] == -1) {
            tileCollisions.add(new Rectangle((currentDiscretePosition.getX() + 1) * gamePanel.getUnitWidth() + gamePanel.getXPadding(),
                    currentDiscretePosition.getY() * gamePanel.getUnitWidth() + gamePanel.getYPadding(),
                    gamePanel.getUnitWidth(),
                    gamePanel.getUnitWidth()));
        }
        // If top cell is not a wall, create a collision box
        if (GameController.getInstance().getMap().getMapInfo()[(int)currentDiscretePosition.getY() - 1][(int)currentDiscretePosition.getX()] == -1) {
            tileCollisions.add(new Rectangle(currentDiscretePosition.getX() * gamePanel.getUnitWidth() + gamePanel.getXPadding(),
                    (currentDiscretePosition.getY() - 1) * gamePanel.getUnitWidth() + gamePanel.getYPadding(),
                    gamePanel.getUnitWidth(),
                    gamePanel.getUnitWidth()));
        }
        // If bottom cell is not a wall, create a collision box
        if (GameController.getInstance().getMap().getMapInfo()[(int)currentDiscretePosition.getY() + 1][(int)currentDiscretePosition.getX()] == -1) {
            tileCollisions.add(new Rectangle(currentDiscretePosition.getX() * gamePanel.getUnitWidth() + gamePanel.getXPadding(),
                    (currentDiscretePosition.getY() + 1) * gamePanel.getUnitWidth() + gamePanel.getYPadding(),
                    gamePanel.getUnitWidth(),
                    gamePanel.getUnitWidth()));
        }
    }
}
