package logic;

import render.Renderable;

public abstract class Entity implements Renderable {

    protected Vector2D position;
    protected boolean destroyed;
    protected double width;
    protected double height;

    public Entity(double x, double y, double width, double height) {
        position = new Vector2D(x, y);
        setDestroyed(false);
        setWidth(width);
        setHeight(height);
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setX(double x) {
        position.setX(x);
    }

    public void setY(double y) {
        position.setY(y);
    }

    public Vector2D getCentroid() {
        return new Vector2D(position.getX() + 0.5, position.getY() + 0.5);
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

}
