package logic;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        double length = getLength();
        return new Vector2D(x / length, y / length);
    }

    public Direction getCurrentDirection() {
        if (getX() < 0) {
            return Direction.LEFT;
        } else if (getX() > 0) {
            return Direction.RIGHT;
        } else if (getY() < 0) {
            return Direction.UP;
        } else if (getY() > 0) {
            return Direction.DOWN;
        }
        return null;
    }

    public boolean isSameAxis(Vector2D vec) {
        return (getX() == 0 && vec.getX() == 0) || (getY() == 0 && vec.getY() == 0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2D vec) {
            return x == vec.x && y == vec.y;
        }
        return false;
    }
}
