package logic;

import render.Renderable;

public abstract class Entity implements Renderable {
    protected double x;
    protected double y;
    protected boolean destroyed;
    protected double width;
    protected double height;

    public Entity(double x, double y, double width, double height) {
        setX(x);
        setY(y);
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
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
