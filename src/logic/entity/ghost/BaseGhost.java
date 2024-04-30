package logic.entity.ghost;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import logic.*;
import logic.entity.Entity;
import logic.entity.ghost.state.ChaseState;
import logic.entity.ghost.state.FreezeState;
import logic.entity.ghost.state.FrightenState;
import logic.entity.ghost.state.SpawnState;
import logic.fsm.FiniteStateMachine;
import util.Config;

public abstract class BaseGhost extends Entity {
    private Image sprite;
    private static Image frightenedSprite = new Image(ClassLoader.getSystemResource("FrightenedGhost.png").toString());
    private static Image ghostEyeSprite = new Image(ClassLoader.getSystemResource("GhostEye.png").toString());
    private static Image freezeOverlaySprite = new Image(ClassLoader.getSystemResource("FreezeOverlay.png").toString());
    protected Vector2D velocity;
    protected Vector2D target;
    private double speed;
    private FiniteStateMachine fsm;

    public BaseGhost(double x, double y, double width, double height, double speed, String imgPath) {
        super(x, y, width, height);
        // Load the image
        sprite = new Image(ClassLoader.getSystemResource(imgPath).toString());
        // Initialize the velocity to 0 in both x and y direction
        velocity = new Vector2D(speed, 0);
        target = new Vector2D(0, 0);
        this.speed = speed;
        fsm = new FiniteStateMachine(new SpawnState(this));
    }

    public void startFreeze() {
        fsm.changeState(new FreezeState(this));
    }

    public void startFrighten() {
        fsm.changeState(new FrightenState(this));
    }

    @Override
    public int getZIndex() {
        return Config.GHOST_Z_INDEX;
    }

    @Override
    public void draw(GraphicsContext gc) {
        Image sprite = switch (fsm.getCurrentStateName()) {
            case "FrightenState" -> frightenedSprite;
            case "RespawnState" -> ghostEyeSprite;
            default -> this.sprite;
        };
        gc.drawImage(sprite,
                position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                width,
                height);
        if (fsm.getCurrentStateName().equals("FreezeState")) {
            gc.setGlobalAlpha(0.8);
            gc.drawImage(freezeOverlaySprite,
                    position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                    position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                    width,
                    height);
            gc.setGlobalAlpha(1.0);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("State: " + fsm.getCurrentStateName(),
                position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding() - 10);
    }

    private void changeVelocity() {
        Vector2D currentDiscretePosition = new Vector2D((int) position.getX(), (int) position.getY());
        Direction currentDirection = velocity.getCurrentDirection();
        Map map = GameController.getInstance().getMap();

        if (position.equals(currentDiscretePosition)) {
            Direction[] directions = {Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN};
            Vector2D[] directionOffsets = {new Vector2D(-1, 0), new Vector2D(1, 0), new Vector2D(0, -1), new Vector2D(0, 1)};
            int bestDirectionIndex = -1;
            double bestDistance = Double.MAX_VALUE;
            for (int i = 0; i < 4; i++) {
                Vector2D nextDiscretePosition = currentDiscretePosition.add(directionOffsets[i]);
                if (map.isWall(nextDiscretePosition.getX(), nextDiscretePosition.getY())
                    || (nextDiscretePosition.equals(new Vector2D(Config.GHOST_X_ORIGIN, Config.GHOST_Y_ORIGIN)) && !fsm.getCurrentStateName().equals("RespawnState"))
                    || (directionOffsets[i].isSameAxis(velocity)) && directions[i] != currentDirection) {
                    continue;
                }
                Vector2D vec = target.subtract(nextDiscretePosition);
                if (vec.getLength() < bestDistance) {
                    bestDistance = vec.getLength();
                    bestDirectionIndex = i;
                }
            }

            if (bestDirectionIndex != -1) {
                velocity.setX(directionOffsets[bestDirectionIndex].getX() * speed);
                velocity.setY(directionOffsets[bestDirectionIndex].getY() * speed);
            } else {
                velocity.setX(-velocity.getX());
                velocity.setY(-velocity.getY());
            }
        }
    }

    private void move(double delta) {
        Direction currentDirection = velocity.getCurrentDirection();
        Vector2D currentDiscretePosition = new Vector2D((int) position.getX(), (int) position.getY());

        double speedMultiplier = switch (fsm.getCurrentStateName()) {
            case "FrightenState" -> Config.GHOST_FRIGHTENED_SPEED_MULTIPLIER;
            case "RespawnState" -> Config.GHOST_RESPAWN_SPEED_MULTIPLIER;
            case "FreezeState" -> Config.GHOST_FREEZE_SPEED_MULTIPLIER;
            default -> 1;
        };
        if (speedMultiplier == 1 && getClass().getSimpleName().equals("SwiftGhost")) {
            speedMultiplier = Config.SWIFT_GHOST_SPEED;
        }

        double offSetThresholdMultiplier = switch (fsm.getCurrentStateName()) {
            case "RespawnState" -> Config.GHOST_RESPAWN_MOVEMENT_OFFSET_MULTIPLIER;
            default -> 1;
        };

        if (currentDirection != null) {
            if (velocity.isSameAxis(new Vector2D(1, 0))) setY(currentDiscretePosition.getY());
            else if (velocity.isSameAxis(new Vector2D(0, 1))) setX(currentDiscretePosition.getX());

            Vector2D nextDiscretePosition = switch (currentDirection) {
                case LEFT, UP -> new Vector2D(currentDiscretePosition);
                case RIGHT -> new Vector2D(currentDiscretePosition.getX() + 1, currentDiscretePosition.getY());
                case DOWN -> new Vector2D(currentDiscretePosition.getX(), currentDiscretePosition.getY() + 1);
            };

            Vector2D vec = nextDiscretePosition.subtract(position);
            if (0 < vec.getLength() && vec.getLength() < offSetThresholdMultiplier * Config.MOVEMENT_OFFSET_THRESHOLD * GameController.getInstance().getGamePanel().getUnitWidth()) {
                setX(nextDiscretePosition.getX());
                setY(nextDiscretePosition.getY());
            } else {
                setX(position.getX() + speedMultiplier * velocity.getX() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
                setY(position.getY() + speedMultiplier * velocity.getY() * GameController.getInstance().getGamePanel().getUnitWidth() * delta);
            }
        }
    }

    public void update(double delta) {
        fsm.update(delta);
        changeVelocity();
        move(delta);
    }

    public void setTarget(Vector2D target) {
        this.target = target;
    }

    public FiniteStateMachine getFsm() {
        return fsm;
    }

    public Vector2D getVelocity() {
        return velocity;
    }
}




