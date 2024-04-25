package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import util.Config;
import util.InputUtility;

import java.util.ArrayList;

public class Pacman extends Entity implements Collidable {
    private static Image spriteNormal = new Image(ClassLoader.getSystemResource("Pacman.png").toString());
    private static Image spriteInvincible = new Image(ClassLoader.getSystemResource("Pacman_Invincible.png").toString());
    private static Image spriteHeart = new Image(ClassLoader.getSystemResource("Heart.png").toString());
    private Vector2D velocity;
    private Vector2D nextVelocity;
    private int health;
    private PacmanState state;

    public Pacman(double x, double y, double width, double height) {
        super(x, y, width, height);
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
        if (state == PacmanState.NORMAL) {
            gc.drawImage(spriteNormal,
                    position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                    position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                    width,
                    height);
        } else if (state == PacmanState.INVINCIBLE) {
            gc.setGlobalAlpha(0.5);
            gc.drawImage(spriteInvincible,
                    position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                    position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                    width,
                    height);
            gc.setGlobalAlpha(1.0);
        }
        for (int i = 0; i < health; i++) {
            gc.drawImage(spriteHeart,
                    GameController.getInstance().getGamePanel().getXPadding() + i * GameController.getInstance().getGamePanel().getUnitWidth(),
                    GameController.getInstance().getGamePanel().getYPadding() - GameController.getInstance().getGamePanel().getUnitWidth(),
                    GameController.getInstance().getGamePanel().getUnitWidth(),
                    GameController.getInstance().getGamePanel().getUnitWidth());
        }
        gc.setFont(Font.font("Arial", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("State: " + state,
                position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding() - 20);
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

    public void startInvincible(long duration) {
        state = PacmanState.INVINCIBLE;
        Thread invincibleThread = new Thread(() -> {
            try {
                state = PacmanState.INVINCIBLE;
                Thread.sleep(duration * 1000);
                if (state == PacmanState.INVINCIBLE) {
                    state = PacmanState.NORMAL;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        invincibleThread.start();
    }

    private void collisionCheck() {
        if (state == PacmanState.INVINCIBLE) return;
        for (Ghost ghost : GameController.getInstance().getGhosts()) {
            Vector2D vec = new Vector2D(getCentroid().getX() - ghost.getCentroid().getX(), getCentroid().getY() - ghost.getCentroid().getY());
            if (vec.getLength() < Config.PACMAN_COLLISION_RADIUS) {
                health--;
                startInvincible(Config.PACMAN_HURT_INVINCIBILITY_DURATION);
                break;
            }
        }

        Map map = GameController.getInstance().getMap();
        Vector2D centeredMapPosition = new Vector2D((int)getCentroid().getX() + 0.5, (int)getCentroid().getY() + 0.5);
        Vector2D vec = new Vector2D(centeredMapPosition.getX() - getCentroid().getX(), centeredMapPosition.getY() - getCentroid().getY());
        int itemCode = map.getMapItemsInfo()[(int)centeredMapPosition.getY()][(int)centeredMapPosition.getX()];
        if (itemCode == 1 && vec.getLength() < Config.PACMAN_COLLISION_RADIUS) {
            map.setMapItemsInfo((int)centeredMapPosition.getX(), (int)centeredMapPosition.getY(), -1);
        }

        ArrayList<Item> deletedItems = new ArrayList<Item>();
        for(Item item : GameController.getInstance().getItems()){
            Vector2D itemPosition = new Vector2D((int)item.getCentroid().getX() + 0.5, (int)item.getCentroid().getY() + 0.5);
            Vector2D itemVec = new Vector2D(itemPosition.getX() - getCentroid().getX(), itemPosition.getY() - getCentroid().getY());
            if (itemVec.getLength() < Config.PACMAN_COLLISION_RADIUS) {
                item.useEffect();
                item.destroy();
                deletedItems.add(item);
            }
        }
        for(Item item : deletedItems){
            GameController.getInstance().getItems().remove(item);
        }
//        System.out.println(GameController.getInstance().getItems().toArray().length);

    }

    public void update(double delta) {
        getInput();
        changeVelocity();
        move(delta);
        collisionCheck();
    }

    private void move(double delta) {
        Direction currentDirection = velocity.getCurrentDirection();
        Vector2D currentDiscretePosition = new Vector2D((int)position.getX(), (int)position.getY());
        double speedMultiplier = (state == PacmanState.INVINCIBLE) ? Config.PACMAN_INVINCIBLE_SPEED_MULTIPLIER : 1;
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
                setX(position.getX() + speedMultiplier * velocity.getX() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
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
                setY(position.getY() + speedMultiplier * velocity.getY() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
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
