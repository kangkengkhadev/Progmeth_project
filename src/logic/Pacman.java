package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import util.Config;
import util.InputUtility;

public class Pacman extends Entity implements Collidable {
    private Image sprite;
    private Vector2D velocity;
    private Vector2D nextVelocity;
    private int health;
    private PacmanState state;

    public Pacman(double x, double y, double width, double height, String imgPath) {
        super(x, y, width, height);
        // Load the image
        sprite = new Image(ClassLoader.getSystemResource(imgPath).toString());
        // Initialize the velocity to 0 in both x and y direction
        velocity = new Vector2D(0, 0);
        nextVelocity = new Vector2D(0, 0);
        health = Config.PACMAN_MAX_HEALTH;
        state = PacmanState.NORMAL;
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
                Vector2D nextDiscretePosition = new Vector2D(currentDiscretePosition.getX() + nextVelocity.getX() / Config.PACMAN_SPEED, currentDiscretePosition.getY() + nextVelocity.getY() / Config.PACMAN_SPEED);
                if (nextDiscretePosition.equals(new Vector2D(23, 10))) return;
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

    private void startInvincible(long duration) {
        Thread invincibleThread = new Thread(() -> {
            try {
                state = PacmanState.INVINCIBLE;
                Thread.sleep(duration * 1000);
                state = PacmanState.NORMAL;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        invincibleThread.start();
    }

    private void collisionCheck() {
        for (Ghost ghost : GameController.getInstance().getGhosts()) {
            if (getCollisionBox().isColliding(ghost.getCollisionBox())) {
                health--;
                startInvincible(Config.PACMAN_HURT_INVINCIBILITY_DURATION);
                break;
            }
        }
    }

    public void update(double delta) {
        getInput();
        changeVelocity();
        move(delta);
        collisionCheck();
        // print health
        System.out.println("Pacman Health: " + health);
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

    public Vector2D getVelocity() {
        return velocity;
    }
}
