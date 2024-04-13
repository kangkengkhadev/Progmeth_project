package logic;

import render.Renderable;

public abstract class Entity implements Renderable {
    protected double x;
    protected double y;
    protected boolean destroyed;

    public Entity(double x, double y) {
        setX(x);
        setY(y);
        this.destroyed = false;
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
}
