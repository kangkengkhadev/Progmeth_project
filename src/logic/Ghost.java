package logic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import util.Config;
import util.InputUtility;

import java.util.ArrayList;

public abstract class Ghost extends Entity {
    protected Image sprite;
    protected Vector2D velocity;
    protected Vector2D target;
    private double speed;

    public Ghost(double x, double y, double width, double height, double speed, String imgPath) {
        super(x, y, width, height);
        // Load the image
        sprite = new Image(ClassLoader.getSystemResource(imgPath).toString());
        // Initialize the velocity to 0 in both x and y direction
        velocity = new Vector2D(speed, 0);
        target = new Vector2D(0, 0);
        this.speed = speed;
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
    }

    private void changeVelocity() {
        Vector2D currentDiscretePosition = new Vector2D((int)position.getX(), (int)position.getY());
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
                if (nextDiscretePosition.equals(new Vector2D(23, 10))) continue;
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
        Vector2D currentDiscretePosition = new Vector2D((int)position.getX(), (int)position.getY());
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

    protected abstract void updateTarget();

    public void update(double delta) {
        updateTarget();
        changeVelocity();
        move(delta);
    }
}




