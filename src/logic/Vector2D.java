package logic;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public double getLength() {
        // return the length of the vector
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D add(Vector2D vec) {
        // return a new vector that is the sum of this vector and the given vector (vec)
        return new Vector2D(x + vec.x, y + vec.y);
    }

    public Vector2D subtract(Vector2D vec) {
        // return a new vector that is the difference of this vector and the given vector (vec)
        return new Vector2D(x - vec.x, y - vec.y);
    }

    public Direction getCurrentDirection() {
        // return the direction of the vector (UP, DOWN, LEFT, RIGHT)
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
        // return true if this vector and the given vector (vec) are on the same axis
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
