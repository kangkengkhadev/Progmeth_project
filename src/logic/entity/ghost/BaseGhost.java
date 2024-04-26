package logic.entity.ghost;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import logic.*;
import logic.entity.Entity;
import logic.entity.ghost.state.ChaseState;
import logic.entity.ghost.state.FrightenState;
import logic.entity.ghost.state.SpawnState;
import logic.fsm.FiniteStateMachine;
import util.Config;

public abstract class BaseGhost extends Entity {
    private Image sprite;
    private static Image frightenedSprite = new Image(ClassLoader.getSystemResource("FrightenedGhost.png").toString());
    private static Image ghostEyeSprite = new Image(ClassLoader.getSystemResource("GhostEye.png").toString());
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

        gc.setFill(switch (this.getClass().getSimpleName()) {
            case "OrangeGhost" -> Color.ORANGE;
            case "YellowGhost" -> Color.YELLOW;
            case "GreenGhost" -> Color.GREEN;
            default -> Color.BLACK;
        });

        gc.fillRect((target.getX() + 0.5) * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                (target.getY() + 0.5) * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding(),
                5,
                5);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("State: " + fsm.getCurrentStateName(),
                position.getX() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getXPadding(),
                position.getY() * GameController.getInstance().getGamePanel().getUnitWidth() + GameController.getInstance().getGamePanel().getYPadding() - 10);
    }

    private void changeVelocity() {
        Vector2D currentDiscretePosition = new Vector2D((int) position.getX(), (int) position.getY());
        Map map = GameController.getInstance().getMap();
        if (position.equals(currentDiscretePosition)) {
            Direction currentDirection = velocity.getCurrentDirection();
            boolean isWallLeft = map.isWall(currentDiscretePosition.getX() - 1, currentDiscretePosition.getY());
            boolean isWallRight = map.isWall(currentDiscretePosition.getX() + 1, currentDiscretePosition.getY());
            boolean isWallUp = map.isWall(currentDiscretePosition.getX(), currentDiscretePosition.getY() - 1);
            boolean isWallDown = map.isWall(currentDiscretePosition.getX(), currentDiscretePosition.getY() + 1);
            Direction[] directions = {Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN};
            Vector2D[] directionOffsets = {new Vector2D(-1, 0), new Vector2D(1, 0), new Vector2D(0, -1), new Vector2D(0, 1)};
            int bestDirectionIndex = -1;
            double bestDistance = Double.MAX_VALUE;
            for (int i = 0; i < 4; i++) {
                Direction dir = directions[i];
                Vector2D offset = directionOffsets[i];
                if ((velocity.isSameAxis(offset) && dir != currentDirection)
                        || dir == Direction.LEFT && isWallLeft
                        || dir == Direction.RIGHT && isWallRight
                        || dir == Direction.UP && isWallUp
                        || dir == Direction.DOWN && isWallDown) {
                    continue;
                }

                Vector2D nextDiscretePosition = new Vector2D(currentDiscretePosition.getX() + offset.getX(), currentDiscretePosition.getY() + offset.getY());
                if (nextDiscretePosition.equals(new Vector2D(23, 10)) && !fsm.getCurrentStateName().equals("RespawnState"))
                    continue;
                Vector2D vec = new Vector2D(target.getX() - nextDiscretePosition.getX(), target.getY() - nextDiscretePosition.getY());

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
            default -> 1;
        };

        double offSetThresholdMultiplier = switch (fsm.getCurrentStateName()) {
            case "RespawnState" -> Config.GHOST_RESPAWN_MOVEMENT_OFFSET_MULTIPLIER;
            default -> 1;
        };

        if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
            setY(currentDiscretePosition.getY());
            if (currentDirection == Direction.LEFT) {
                Vector2D vec = new Vector2D(currentDiscretePosition.getX() - position.getX(), 0);
                if (vec.getLength() < offSetThresholdMultiplier * Config.MOVEMENT_OFFSET_THRESHOLD * GameController.getInstance().getGamePanel().getUnitWidth() && vec.getLength() > 0) {
                    setX(currentDiscretePosition.getX());
                    return;
                }
            } else {
                Vector2D vec = new Vector2D(currentDiscretePosition.getX() + 1 - position.getX(), 0);
                if (vec.getLength() < offSetThresholdMultiplier * Config.MOVEMENT_OFFSET_THRESHOLD * GameController.getInstance().getGamePanel().getUnitWidth() && vec.getLength() > 0) {
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




