package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import util.Config;
import util.InputUtility;

import java.util.ArrayList;
import java.util.Vector;

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

    private void changeVelocity() {
        Vector2D currentDiscretePosition = new Vector2D((int)position.getX(), (int)position.getY());
        if (position.equals(currentDiscretePosition)) {
            if (nextVelocity.getCurrentDirection() == Direction.LEFT && GameController.getInstance().getMap().getMapInfo()[(int)position.getY()][(int)position.getX() - 1] == -1
            || nextVelocity.getCurrentDirection() == Direction.RIGHT && GameController.getInstance().getMap().getMapInfo()[(int)position.getY()][(int)position.getX() + 1] == -1
            || nextVelocity.getCurrentDirection() == Direction.UP && GameController.getInstance().getMap().getMapInfo()[(int)position.getY() - 1][(int)position.getX()] == -1
            || nextVelocity.getCurrentDirection() == Direction.DOWN && GameController.getInstance().getMap().getMapInfo()[(int)position.getY() + 1][(int)position.getX()] == -1) {
                velocity = nextVelocity;
            } else {
                Vector2D nextDiscretePosition;
                if (velocity.getCurrentDirection() == null) return;
                nextDiscretePosition = switch (velocity.getCurrentDirection()) {
                    case LEFT -> new Vector2D(currentDiscretePosition.getX() - 1, currentDiscretePosition.getY());
                    case RIGHT -> new Vector2D(currentDiscretePosition.getX() + 1, currentDiscretePosition.getY());
                    case UP -> new Vector2D(currentDiscretePosition.getX(), currentDiscretePosition.getY() - 1);
                    case DOWN -> new Vector2D(currentDiscretePosition.getX(), currentDiscretePosition.getY() + 1);
                };
                if (GameController.getInstance().getMap().isWall(nextDiscretePosition.getX(), nextDiscretePosition.getY())) {
                    velocity = new Vector2D(0, 0);
                }
            }
        } else if (velocity.isSameAxis(nextVelocity)) {
            velocity = nextVelocity;
        }
    }

    public void update(double delta) {
        getInput();
        createCollisionAroundPacman();
        changeVelocity();
        move(delta);
        // print position
        System.out.println(position.getX() + " " + position.getY());
    }

    private void move(double delta) {
        Direction currentDirection = velocity.getCurrentDirection();
        Vector2D currentDiscretePosition = new Vector2D((int)position.getX(), (int)position.getY());
        if (currentDirection != null) {
            if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
                setY(currentDiscretePosition.getY());
                if (currentDirection == Direction.LEFT) {
                    Vector2D vec = new Vector2D(currentDiscretePosition.getX() - position.getX(), 0);
                    if (vec.getLength() < Config.MOVEMENT_OFFSET_THRESHOLD * GameController.getInstance().getGamePanel().getUnitWidth() && vec.getLength() > 0) {
                        setX(currentDiscretePosition.getX());
                        return;
                    }
                } else {
                    Vector2D vec = new Vector2D(currentDiscretePosition.getX() + 1 - position.getX(), 0);
                    if (vec.getLength() < Config.MOVEMENT_OFFSET_THRESHOLD * GameController.getInstance().getGamePanel().getUnitWidth() && vec.getLength() > 0) {
                        setX(currentDiscretePosition.getX() + 1);
                        return;
                    }
                }
                setX(position.getX() + velocity.getX() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
            } else {
                setX(currentDiscretePosition.getX());
                if (currentDirection == Direction.UP) {
                    Vector2D vec = new Vector2D(0, currentDiscretePosition.getY() - position.getY());
                    if (vec.getLength() < Config.MOVEMENT_OFFSET_THRESHOLD * GameController.getInstance().getGamePanel().getUnitWidth() && vec.getLength() > 0) {
                        setY(currentDiscretePosition.getY());
                        return;
                    }
                } else {
                    Vector2D vec = new Vector2D(0, currentDiscretePosition.getY() + 1 - position.getY());
                    if (vec.getLength() < Config.MOVEMENT_OFFSET_THRESHOLD * GameController.getInstance().getGamePanel().getUnitWidth() && vec.getLength() > 0) {
                        setY(currentDiscretePosition.getY() + 1);
                        return;
                    }
                }
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
