package logic;

public class Rectangle {
    private Vector2D origin;
    private double width;
    private double height;

    public Rectangle(double x, double y, double width, double height) {
        origin = new Vector2D(x, y);
        this.width = width;
        this.height = height;
    }

    public boolean isColliding(Rectangle other) {
        if (origin.getX() + width < other.origin.getX() || other.origin.getX() + other.width < origin.getX()) {
            return false;
        }
        if (origin.getY() + height < other.origin.getY() || other.origin.getY() + other.height < origin.getY()) {
            return false;
        }
        return true;
    }

    public Vector2D getOrigin() {
        return origin;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
