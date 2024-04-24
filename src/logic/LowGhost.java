package logic;

import javafx.scene.image.Image;

public abstract class LowGhost extends Ghost{
    public LowGhost(double x, double y, double width, double height, String imgPath) {
        super(x, y, width, height, imgPath);
    }

    protected void changeVelocity() {
        Vector2D currentDiscretePosition = new Vector2D((int)position.getX(), (int)position.getY());
        Map map = GameController.getInstance().getMap();
        if (position.equals(currentDiscretePosition)) {
            Direction currentDirection = velocity.getCurrentDirection();
        }
    }

    protected void move() {
    }
}
