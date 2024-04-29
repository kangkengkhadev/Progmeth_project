package logic.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import logic.*;
import logic.entity.ghost.BaseGhost;
import logic.entity.ghost.TankGhost;
import logic.entity.ghost.state.RespawnState;
import logic.entity.item.BaseItem;
import util.Config;
import util.InputUtility;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;


public class Pacman extends Entity {
    private static Image spriteNormal = new Image(ClassLoader.getSystemResource("Pacman.png").toString());
    private static Image spriteInvincible = new Image(ClassLoader.getSystemResource("Pacman_Invincible.png").toString());
    private static Image spriteHeart = new Image(ClassLoader.getSystemResource("Heart.png").toString());
    private Vector2D velocity;
    private Vector2D nextVelocity;
    private int health;
    private PacmanState state;

    private static final String GCOIN_FILE = "res/getcoin.mp3";
    private static final Media GCOIN_SOUND = new Media(new File(GCOIN_FILE).toURI().toString());

    private static final String EATER_FILE = "res/eater.mp3";
    private static final Media EATER_SOUND = new Media(new File(EATER_FILE).toURI().toString());

    private static final String GET_ITEM_FILE = "res/getItem.mp3";
    private static final Media GET_ITEM_SOUND = new Media(new File(GET_ITEM_FILE).toURI().toString());

    public static void playScoreSound(Media sound) {
        Thread thread = new Thread(() -> {
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.dispose());
            mediaPlayer.play();
        });
        thread.start();
    }

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
            gc.drawImage(spriteNormal, position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(), position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(), width, height);
        } else if (state == PacmanState.INVINCIBLE) {
            gc.setGlobalAlpha(0.5);
            gc.drawImage(spriteInvincible, position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(), position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(), width, height);
            gc.setGlobalAlpha(1.0);
        }
        for (int i = 0; i < health; i++) {
            gc.drawImage(spriteHeart, GameController.getInstance().getGamePanel().getXPadding() + i * GameController.getInstance().getGamePanel().getUnitWidth(), GameController.getInstance().getGamePanel().getYPadding() - GameController.getInstance().getGamePanel().getUnitWidth(), GameController.getInstance().getGamePanel().getUnitWidth(), GameController.getInstance().getGamePanel().getUnitWidth());
        }
        gc.setFont(Font.font("Arial", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + GameController.getInstance().getScore(), position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(), position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding() - 20);
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
        Vector2D currentDiscretePosition = new Vector2D((int) position.getX(), (int) position.getY());
        Direction currentDirection = velocity.getCurrentDirection();
        Direction nextDirection = nextVelocity.getCurrentDirection();
        if (nextDirection == null) return;
        Vector2D nextDiscretePosition = switch (nextDirection) {
            case LEFT -> new Vector2D(currentDiscretePosition.getX() - 1, currentDiscretePosition.getY());
            case RIGHT -> new Vector2D(currentDiscretePosition.getX() + 1, currentDiscretePosition.getY());
            case UP -> new Vector2D(currentDiscretePosition.getX(), currentDiscretePosition.getY() - 1);
            case DOWN -> new Vector2D(currentDiscretePosition.getX(), currentDiscretePosition.getY() + 1);
        };

        if (position.equals(currentDiscretePosition)) {
            if (!GameController.getInstance().getMap().isWall(nextDiscretePosition.getX(), nextDiscretePosition.getY())) {
                if (nextDiscretePosition.equals(new Vector2D(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN))) return;
                velocity = nextVelocity;
            } else {
                if (currentDirection == null) return;

                Vector2D ongoingDiscretePosition = switch (currentDirection) {
                    case LEFT -> new Vector2D(currentDiscretePosition.getX() - 1, currentDiscretePosition.getY());
                    case RIGHT -> new Vector2D(currentDiscretePosition.getX() + 1, currentDiscretePosition.getY());
                    case UP -> new Vector2D(currentDiscretePosition.getX(), currentDiscretePosition.getY() - 1);
                    case DOWN -> new Vector2D(currentDiscretePosition.getX(), currentDiscretePosition.getY() + 1);
                };
                if (!GameController.getInstance().getMap().isWall(ongoingDiscretePosition.getX(), ongoingDiscretePosition.getY()))
                    return;

                velocity = new Vector2D(0, 0);
            }
        } else if (velocity.isSameAxis(nextVelocity)) velocity = nextVelocity;
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
        for (BaseGhost ghost : GameController.getInstance().getGhosts()) {
            Vector2D vec = new Vector2D(getCentroid().getX() - ghost.getCentroid().getX(), getCentroid().getY() - ghost.getCentroid().getY());

            if (vec.getLength() < Config.PACMAN_COLLISION_RADIUS) {
                String currentState = ghost.getFsm().getCurrentStateName();
                if (currentState.equals("RespawnState") || currentState.equals("SpawnState")) continue;
                else if (currentState.equals("FrightenState")) {
                    ghost.getFsm().changeState(new RespawnState(ghost));
                } else {
                    if (ghost instanceof TankGhost) {
                        health -= 2;
                    } else {
                        health--;
                    }
                    startInvincible(Config.PACMAN_HURT_INVINCIBILITY_DURATION);
                    break;
                }
            }
        }

        Map map = GameController.getInstance().getMap();
        Vector2D centeredMapPosition = new Vector2D((int) getCentroid().getX() + 0.5, (int) getCentroid().getY() + 0.5);
        Vector2D vec = new Vector2D(centeredMapPosition.getX() - getCentroid().getX(), centeredMapPosition.getY() - getCentroid().getY());
        int itemCode = map.getMapItemsInfo()[(int) centeredMapPosition.getY()][(int) centeredMapPosition.getX()];
        if (itemCode == 1 && vec.getLength() < Config.PACMAN_COLLISION_RADIUS) {
            playScoreSound(GCOIN_SOUND);
            map.setMapItemsInfo((int) centeredMapPosition.getX(), (int) centeredMapPosition.getY(), -1);
            GameController.getInstance().setScore(GameController.getInstance().getScore() + 1);
        } else if (itemCode == 3 && vec.getLength() < Config.PACMAN_COLLISION_RADIUS) {
            playScoreSound(EATER_SOUND);
            map.setMapItemsInfo((int) centeredMapPosition.getX(), (int) centeredMapPosition.getY(), -1);
            for (BaseGhost ghost : GameController.getInstance().getGhosts()) {
                ghost.startFrighten();
            }
        }
        ArrayList<BaseItem> deletedItems = new ArrayList<BaseItem>();
        for (BaseItem item : GameController.getInstance().getItems()) {
            Vector2D itemPosition = new Vector2D((int) item.getCentroid().getX() + 0.5, (int) item.getCentroid().getY() + 0.5);
            Vector2D itemVec = new Vector2D(itemPosition.getX() - getCentroid().getX(), itemPosition.getY() - getCentroid().getY());
            if (itemVec.getLength() < Config.PACMAN_COLLISION_RADIUS) {
                playScoreSound(GET_ITEM_SOUND);
                item.useEffect();
                item.destroy();
                deletedItems.add(item);
            }
        }
        for (BaseItem item : deletedItems) {
            GameController.getInstance().getItems().remove(item);
        }
    }

    public void update(double delta) {
        getInput();
        changeVelocity();
        move(delta);
        collisionCheck();
    }

    private void move(double delta) {
        Direction currentDirection = velocity.getCurrentDirection();
        Vector2D currentDiscretePosition = new Vector2D((int) position.getX(), (int) position.getY());
        double speedMultiplier = (state == PacmanState.INVINCIBLE) ? Config.PACMAN_INVINCIBLE_SPEED_MULTIPLIER : 1;
        if (currentDirection != null) {
            if (velocity.isSameAxis(new Vector2D(1, 0))) setY(currentDiscretePosition.getY());
            else if (velocity.isSameAxis(new Vector2D(0, 1))) setX(currentDiscretePosition.getX());

            Vector2D nextDiscretePosition = switch (currentDirection) {
                case LEFT, UP -> new Vector2D(currentDiscretePosition);
                case RIGHT -> new Vector2D(currentDiscretePosition.getX() + 1, currentDiscretePosition.getY());
                case DOWN -> new Vector2D(currentDiscretePosition.getX(), currentDiscretePosition.getY() + 1);
            };

            Vector2D vec = nextDiscretePosition.subtract(position);
            if (0 < vec.getLength() && vec.getLength() < Config.MOVEMENT_OFFSET_THRESHOLD * GameController.getInstance().getGamePanel().getUnitWidth()) {
                setX(nextDiscretePosition.getX());
                setY(nextDiscretePosition.getY());
            } else {
                setX(position.getX() + speedMultiplier * velocity.getX() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
                setY(position.getY() + speedMultiplier * velocity.getY() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
            }
        }
    }

    public Vector2D getVelocity() {
        return velocity;
    }
}
