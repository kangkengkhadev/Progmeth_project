package logic.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import logic.*;
import logic.entity.ghost.BaseGhost;
import logic.entity.ghost.TankGhost;
import logic.entity.ghost.state.RespawnState;
import logic.entity.item.BaseItem;
import logic.entity.item.FreezePotion;
import util.Config;
import util.InputUtility;

public class Pacman extends Entity {
    private static Image spriteNormal = new Image(ClassLoader.getSystemResource("Pacman.png").toString());
    private static Image spriteInvincible = new Image(ClassLoader.getSystemResource("Pacman_Invincible.png").toString());
    private static Image spriteHeart = new Image(ClassLoader.getSystemResource("Heart.png").toString());
    private static AudioPlayer frightenedGhostAudio = new AudioPlayer("FrightenedGhostAudio.mp3", true);
    private static AudioPlayer collectedCoinAudio = new AudioPlayer("CollectedCoinAudio.mp3", false);
    private static AudioPlayer collectedFreezePotionAudio = new AudioPlayer("CollectedFreezePotion.mp3", false);
    private static AudioPlayer collectedCloakAudio = new AudioPlayer("CollectedCloakAudio.mp3", false);

    private Vector2D velocity;
    private Vector2D nextVelocity;
    private int health;
    private PacmanState state;

    public Pacman(double x, double y, double width, double height) {
        super(x, y, width, height);
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
                    ghost.attack();
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
            new Thread(() -> collectedCoinAudio.playAudio()).start();
            map.setMapItemsInfo((int) centeredMapPosition.getX(), (int) centeredMapPosition.getY(), -1);
            GameController.getInstance().setScore(GameController.getInstance().getScore() + 1);
        } else if (itemCode == 3 && vec.getLength() < Config.PACMAN_COLLISION_RADIUS) {
            new Thread(() -> {
                try {
                    frightenedGhostAudio.playAudio();
                    Thread.sleep(Config.GHOST_FRIGHTENED_DURATION * 1000);
                    frightenedGhostAudio.stopAudio();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            map.setMapItemsInfo((int) centeredMapPosition.getX(), (int) centeredMapPosition.getY(), -1);
            for (BaseGhost ghost : GameController.getInstance().getGhosts()) {
                ghost.startFrighten();
            }
        }
        for (int i = GameController.getInstance().getItems().size() - 1; i >= 0; i--) {
            BaseItem item = GameController.getInstance().getItems().get(i);
            if (getCentroid().subtract(item.getCentroid()).getLength() < Config.PACMAN_COLLISION_RADIUS) {
                if (item instanceof FreezePotion) {
                    new Thread(() -> collectedFreezePotionAudio.playAudio()).start();
                    for (BaseGhost ghost : GameController.getInstance().getGhosts()) {
                        ghost.startFreeze();
                    }
                } else {
                    new Thread(() -> collectedCloakAudio.playAudio()).start();
                    startInvincible(Config.INVINCIBILITY_DURATION);
                }
                item.useEffect();
                item.destroy();
                GameController.getInstance().getItems().remove(i);
            }
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

    public void takeDamage(int damage) {
        health -= damage;
    }
}
